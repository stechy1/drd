package cz.stechy.drd.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import cz.stechy.drd.ServerDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Třída reprezentující repozitář s předměty
 */
public final class FirebaseRepository implements ServerDatabase {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseRepository.class);
    // endregion

    // region Variables

    private final Map<String, List<ItemEventListener>> listeners = new HashMap<>();
    private final Map<String, List<Map<String, Object>>> items = new HashMap<>();
    private DatabaseReference itemsReference;

    // endregion

    // region Constructors

    public FirebaseRepository() {

    }

    // endregion

    // region Public methods

    /**
     * Inicializuje firebase referenci na databázi.
     * Musí se zavolat až po inicializaci samotné firebase.
     */
    public void init() {
        this.itemsReference = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Vloží záznam do firebase
     *
     * @param tableName Název tabulky
     * @param item Záznam, který se má vložit
     * @param id Id záznamu
     */
    @Override
    public void performInsert(final String tableName, Map<String, Object> item, String id) {
        try {
            this.itemsReference
                .child(tableName)
                .child(id)
                .setValueAsync(item).get();
        } catch (Exception ignored) {}
    }

    /**
     * Aktualizuje záznam ve firebase
     *
     * @param tableName Název tabulky
     * @param item Záznam, který se má aktualizovat
     * @param id Id záznamu
     */
    @Override
    public void performUpdate(final String tableName, Map<String, Object> item, String id) {
        this.itemsReference
            .child(tableName)
            .child(id)
            .updateChildrenAsync(item);
    }

    /**
     * Odstraní záznam z firebase
     *
     * @param tableName Název tabulky
     * @param id Id záznamu
     */
    @Override
    public void performDelete(final String tableName, String id) {
        this.itemsReference
            .child(tableName)
            .child(id)
            .removeValueAsync();
        final List<Map<String, Object>> maps = items.get(tableName);

    }

    /**
     * Zaregistruje posluchače událostí pro danou tabulku
     *
     * @param tableName Název tabulky, pro kterou se registruje posluchač
     * @param listener {@link ItemEventListener} posluchač událostí, který se hlásí k odběru
     */
    @Override
    public synchronized void registerListener(final String tableName, ItemEventListener listener) {
        List<ItemEventListener> observables = listeners.get(tableName);
        // Pokud jsem ještě neprovedl žádnou registraci pro daný typ předmětu
        if (observables == null) {
            observables = new ArrayList<>();
            itemsReference
                .child(tableName)
                .addChildEventListener(
                    new FirebaseItemListener(FirebaseConvertors.forKey(tableName), observables, tableName)
                );
            listeners.put(tableName, observables);
        }

        observables.add(listener);

        // Tímto zajistím, že se ke každému klientovi dostanou všechny lokálně uložené itemy
        // Při prvním průchodu bude toto prázdné...
        final List<Map<String, Object>> itemList = items.get(tableName);
        itemList.stream()
            .map(item -> FirebaseItemEvents.forChildAdded(item, tableName))
            .forEach(listener::onEvent);
    }

    /**
     * Zruší odběr událostí pro danou tabulku
     *
     * @param tableName Název tabulky, pro kterou se má odhlásit odběr událostí
     * @param listener {@link ItemEventListener} posluchač událostí, který se má odstranit
     */
    @Override
    public synchronized void unregisterListener(final String tableName, ItemEventListener listener) {
        final List<ItemEventListener> observables = listeners.get(tableName);
        if (observables == null) {
            return;
        }

        observables.remove(listener);
    }

    /**
     * Odhlásí odběr pro daného posluchače ze všech tabulek
     *
     * @param listener {@link ItemEventListener} posluchač, který se má odstranit
     */
    @Override
    public synchronized void unregisterFromAllListeners(ItemEventListener listener) {
        for (List<ItemEventListener> itemEventListeners : listeners.values()) {
            itemEventListeners.remove(listener);
        }
    }

    // endregion

    /**
     * Pomocná třída komunikující přímo s firebase databazí
     */
    private final class FirebaseItemListener implements ChildEventListener {

        private final FirebaseConvertor convertor;
        private final List<ItemEventListener> listeners;
        private final String tableName;

        private FirebaseItemListener(FirebaseConvertor convertor, List<ItemEventListener> listeners, String tableName) {
            this.convertor = convertor;
            this.listeners = listeners;
            this.tableName = tableName;
            items.put(tableName, new ArrayList<>());
        }

        private void notifyListeners(ItemEvent event) {
            listeners.forEach(itemEventListener -> itemEventListener.onEvent(event));
        }

        @Override
        public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
            final Map<String, Object> item = convertor.convert(snapshot);
            // Uložení itemu do lokální kolekce
            final List<Map<String, Object>> list = items.get(tableName);
            list.add(item);

            final ItemEvent event = FirebaseItemEvents.forChildAdded(item, tableName);
            notifyListeners(event);
        }

        @Override
        public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
            final Map<String, Object> item = convertor.convert(snapshot);
            final List<Map<String, Object>> list = items.get(tableName);
            final String idKey = tableName + "_id";
            final Object id = item.get(idKey);
            for(Iterator<Map<String, Object>> it = list.iterator(); it.hasNext();) {
                final Map<String, Object> map = it.next();
                if (map.containsValue(id)) {
                    map.clear();
                    map.putAll(item);
                    break;
                }
            }


            final ItemEvent event = FirebaseItemEvents.forChildChanged(item, tableName);
            notifyListeners(event);
        }

        @Override
        public void onChildRemoved(DataSnapshot snapshot) {
            final Map<String, Object> item = convertor.convert(snapshot);
            final List<Map<String, Object>> list = items.get(tableName);
            final String idKey = tableName + "_id";
//            item.keySet().parallelStream()
//                .filter(s -> s.contains("id"))
//                .findFirst()
//                .ifPresent(s -> {
            final Object id = item.get(idKey);
            for(Iterator<Map<String, Object>> it = list.iterator(); it.hasNext();) {
                final Map<String, Object> map = it.next();
                if (map.containsValue(id)) {
                    it.remove();
                    break;
                }
            }
//                });

            final ItemEvent event = FirebaseItemEvents.forChildRemoved(item, tableName);
            notifyListeners(event);
        }

        @Override
        public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

        }

        @Override
        public void onCancelled(DatabaseError error) {

        }
    }
}
