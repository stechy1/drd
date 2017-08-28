package cz.stechy.drd.model.db;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.di.Inject;
import cz.stechy.drd.model.db.base.Database;
import cz.stechy.drd.model.db.base.Firebase;
import cz.stechy.drd.model.db.base.OnlineItem;
import cz.stechy.drd.model.item.ItemRegistry;
import cz.stechy.drd.util.Base64Util;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vylepšený databázový manažer o možnost spojení s firebase
 */
public abstract class AdvancedDatabaseService<T extends OnlineItem> extends
    BaseDatabaseService<T> implements Firebase<T> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvancedDatabaseService.class);

    // endregion

    // region Variables

    private final ObservableList<T> onlineDatabase = FXCollections.observableArrayList();
    private final ObservableList<T> usedItems = FXCollections.observableArrayList();

    private DatabaseReference firebaseReference;
    private boolean showOnline = false;

    // endregion

    // region Constructors

    /**
     * Inicializuje rozšířenou verzi databázového manažeru
     *
     * @param db {@link Database}
     */
    protected AdvancedDatabaseService(Database db) {
        super(db);

        attachOfflineListener();
    }

    // endregion

    // region Public static methods

    /**
     * Převede formát Base64 na pole bytu
     *
     * @param source Base64
     * @return Pole bytu
     */
    protected static byte[] base64ToBlob(String source) {
        return Base64Util.decode(source);
    }

    /**
     * Převede poly bytu na Base64
     *
     * @param source Pole bytu
     * @return Base64
     */
    protected static String blobToBase64(byte[] source) {
        return Base64Util.encode(source);
    }

    // endregion

    // region Private methods

    /**
     * Konvertuje {@link DataSnapshot} na instanci třídy {@link T}
     *
     * @param snapshot Snapshot itemu
     * @return Instanci třídy {@link T}
     */
    protected abstract T parseDataSnapshot(DataSnapshot snapshot);

    /**
     * @return Vrátí název potomka ve firebase
     */
    protected abstract String getFirebaseChildName();

    /**
     * Namapuje vybraný item do mapy
     *
     * @param item Item, který se má převést do mapy
     * @return Mapu, kde klíč je název sloupce a hodnota je hodnota sloupce
     */
    protected Map<String, Object> toFirebaseMap(T item) {
        String[] columns = getColumnsKeys().split(",");
        Object[] values = itemToParams(item).toArray();
        assert columns.length == values.length;
        final Map<String, Object> map = new HashMap<>(columns.length);

        for (int i = 0; i < columns.length; i++) {
            map.put(columns[i], values[i]);
        }

        return map;
    }

    private ListChangeListener<T> makeChangeListener() {
        return c -> {
            while(c.next()) {
                this.usedItems.addAll(c.getAddedSubList());
                this.usedItems.removeAll(c.getRemoved());
            }
        };
    }

    private void attachOfflineListener() {
        this.onlineDatabase.removeListener(listChangeListener);
        super.items.addListener(listChangeListener);
        this.usedItems.setAll(super.items);
    }

    private void attachOnlineListener() {
        super.items.removeListener(listChangeListener);
        this.onlineDatabase.addListener(listChangeListener);
        this.usedItems.setAll(this.onlineDatabase);
    }

    // endregion

    // region Public methods

    @Override
    public ObservableList<T> selectAll() {
        ObservableList<T> tmp = super.selectAll();
        if (!showOnline) {
            usedItems.setAll(tmp);
        }

        return usedItems;
    }

    @Override
    public void insert(T item) throws DatabaseException {
        if (showOnline) {
            item.setUploaded(true);
        }
        item.setDownloaded(true);
        super.insert(item);
    }

    @Override
    public void update(T item) throws DatabaseException {
        super.update(item);

        if (!showOnline) {
            usedItems.stream()
                .filter(t -> Objects.equals(t.getId(), item.getId()))
                .findFirst()
                .ifPresent(t -> t.update(item));
        }
    }

    @Override
    public void delete(String id) throws DatabaseException {
        super.delete(id);

        onlineDatabase.stream()
            .filter(item -> item.getId().equals(id))
            .findFirst()
            .ifPresent(t -> t.setDownloaded(false));
    }

    @Override
    public void deleteRemote(T item, boolean remote) {
        if (remote) {
            LOGGER.trace("Odebírám online item {} z online databáze", item.toString());
            firebaseReference.child(item.getId()).removeValue();
            T itemCopy = item.duplicate();
            itemCopy.setUploaded(false);
            try {
                update(itemCopy);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                delete(item.getId());
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void upload(T item) {
        if (firebaseReference == null) {
            return;
        }

        LOGGER.trace("Nahrávám item {} do online databáze", item.toString());
        DatabaseReference newReference = firebaseReference.child(item.getId());
        newReference.setValue(toFirebaseMap(item));
        T itemCopy = item.duplicate();
        itemCopy.setUploaded(true);
        try {
            update(itemCopy);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Přepne databázi podle parametru
     *
     * @param showOnline True, pokud se má zobrazit online databáze, jinak offline databáze
     */
    public void toggleDatabase(boolean showOnline) {
        this.showOnline = showOnline;
        this.usedItems.clear();

        if (showOnline) {
            attachOnlineListener();
        } else {
            attachOfflineListener();
        }
    }

    /**
     * Stáhne všechny online předměty do offline databáze
     *
     * @param author Autor předmětů, které chci stáhnout
     * @param completeListener
     */
    public void synchronize(final String author,
        final OnSynchronizationCompleteListener completeListener) {
        final Task<Integer> task = new SynchronizationTask(author);
        task.setOnSucceeded(event -> {
            if (completeListener != null) {
                completeListener.onSynchronizationComplete(task.getValue());
            }
        });

        ThreadPool.getInstance().submit(task);
    }

    // endregion

    // region Getters & Setters

    /**
     * Nastaví firebase databázi
     *
     * @param wrapper {@link FirebaseDatabase}
     */
    @Inject
    public void setFirebaseDatabase(FirebaseWrapper wrapper) {
        System.out.println("Settings firebase wrapper");
        wrapper.firebaseProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                firebaseReference = newValue.getReference(getFirebaseChildName());
                firebaseReference.addChildEventListener(childEventListener);
            }
        });
    }

    // endregion

    private final ChildEventListener childEventListener = new ChildEventListener() {

        final ItemRegistry itemRegistry = ItemRegistry.getINSTANCE();

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final T item = parseDataSnapshot(dataSnapshot);
            LOGGER.trace("Přidávám online item {} do svého povědomí.", item.toString());

            Platform.runLater(() -> {
                itemRegistry.getItemById(item.getId()).ifPresent(itemBase -> {
                    item.setDownloaded(true);
                    itemBase.setUploaded(true);
                });

                item.setUploaded(true);
                onlineDatabase.add(item);
            });
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            LOGGER.trace("Položka v online databázi byla změněna");
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            T item = parseDataSnapshot(dataSnapshot);
            LOGGER.trace("Položka v online databázi: {} byla odebrána", item.toString());
            onlineDatabase.remove(item);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private final ListChangeListener<T> listChangeListener = makeChangeListener();

    /**
     * Třída představující synchronizační úlohu prováděnou asynchroně
     */
    private final class SynchronizationTask extends Task<Integer> {

        // region Variables

        private final Object lock = new Object();
        private final String author;

        // endregion

        // region Constructors

        /**
         * Vytvoří novou synchronizační úlohu
         *
         * @param author Autor předmětů, které se budou synchronizovat
         */
        private SynchronizationTask(String author) {
            this.author = author;
        }

        // endregion

        @Override
        protected Integer call() throws Exception {
            final int[] updated = new int[] {0};
            onlineDatabase
                .stream()
                .filter(onlineItem -> Objects.equals(onlineItem.getAuthor(), author))
                .forEach(onlineItem -> {
                    final Optional<T> optional = items.stream()
                        .filter(item -> Objects.equals(item.getId(), onlineItem.getId()))
                        .findFirst();
                    onlineItem.setDownloaded(true);
                    // Mám-li offline záznam o souboru
                    if (optional.isPresent()) {
                        final T offlineItem = optional.get();
                        // Pokud nemá offline item záznam že je nahraný,
                        // tak vložím záznam do databáze
                        if (!offlineItem.isUploaded()) {
                            final T offlineDuplicate = offlineItem.duplicate();
                            offlineDuplicate.setUploaded(true);
                            try {
                                update(offlineDuplicate);
                                // Protože aktualizuji sdílenou proměnnou, pridám zámek
                                synchronized (lock) {
                                    updated[0]++;
                                }
                            } catch (DatabaseException e) {
                                e.printStackTrace();
                            }
                        }
                        // Nemám-li offline záznam o souboru, tak ho vytvořím
                    } else {
                        final T offlineItem = onlineItem;
                        final T offlineDuplicate = offlineItem.duplicate();
                        offlineDuplicate.setDownloaded(true);
                        offlineDuplicate.setUploaded(true);
                        try {
                            insert(offlineDuplicate);
                            // Protože aktualizuji sdílenou proměnnou, pridám zámek
                            synchronized (lock) {
                                updated[0]++;
                            }
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            return updated[0];
        }
    }

    /**
     * Rozhraní s metodou pro callback po úspěšné synchronizaci
     */
    @FunctionalInterface
    public interface OnSynchronizationCompleteListener {

        /**
         * Metoda se zavolá po úspěšné synchronizaci
         *
         * @param total Celkový počet synchronizovaných položek
         */
        void onSynchronizationComplete(int total);
    }
}
