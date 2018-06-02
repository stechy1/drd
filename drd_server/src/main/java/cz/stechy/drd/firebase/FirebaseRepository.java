package cz.stechy.drd.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Třída reprezentující repozitář s předměty
 */
public final class FirebaseRepository {

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

    public void init() {
        this.itemsReference = FirebaseDatabase.getInstance().getReference();
    }

    public synchronized void registerListener(final String key, ItemEventListener listener) {
        List<ItemEventListener> observables = listeners.get(key);
        // Pokud jsem ještě neprovedl žádnou registraci pro daný typ předmětu
        if (observables == null) {
            observables = new ArrayList<>();
            itemsReference
                .child(key)
                .addChildEventListener(
                    new FirebaseItemListener(FirebaseConvertors.forKey(key), observables, key)
                );
            listeners.put(key, observables);
        }

        observables.add(listener);

        // Tímto zajistím, že se ke každému klientovi dostanou všechny lokálně uložené itemy
        // Při prvním průchodu bude toto prázdné...
        final List<Map<String, Object>> itemList = items.get(key);
        itemList.stream().map(FirebaseItemEvents::forChildAdded).forEach(listener::onEvent);
    }

    public synchronized void unregisterListener(final String key, ItemEventListener listener) {
        final List<ItemEventListener> observables = listeners.get(key);
        if (observables == null) {
            return;
        }

        observables.remove(listener);
    }

    // endregion

    private final class FirebaseItemListener implements ChildEventListener {

        private final FirebaseConvertor convertor;
        private final List<ItemEventListener> listeners;
        private final String key;

        private FirebaseItemListener(FirebaseConvertor convertor, List<ItemEventListener> listeners, String key) {
            this.convertor = convertor;
            this.listeners = listeners;
            this.key = key;
            items.put(key, new ArrayList<>());
        }

        private void notifyListeners(ItemEvent event) {
            listeners.forEach(itemEventListener -> itemEventListener.onEvent(event));
        }

        @Override
        public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
            final Map<String, Object> item = convertor.convert(snapshot);
            // Uložení itemu do lokální kolekce
            final List<Map<String, Object>> list = items.get(key);
            list.add(item);

            final ItemEvent event = FirebaseItemEvents.forChildAdded(item);
            notifyListeners(event);
        }

        @Override
        public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
            final Map<String, Object> item = convertor.convert(snapshot);
            final ItemEvent event = FirebaseItemEvents.forChildChanged(item);
            notifyListeners(event);
        }

        @Override
        public void onChildRemoved(DataSnapshot snapshot) {
            final Map<String, Object> item = convertor.convert(snapshot);
            final ItemEvent event = FirebaseItemEvents.forChildRemoved(item);
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
