package cz.stechy.drd.model.db;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import cz.stechy.drd.model.db.base.Database;
import cz.stechy.drd.model.db.base.Firebase;
import cz.stechy.drd.model.db.base.OnlineItem;
import cz.stechy.drd.util.Base64Util;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vylepšený databázový manažer o možnost spojení s firebase
 */
public abstract class AdvancedDatabaseManager<T extends OnlineItem> extends
    BaseDatabaseManager<T> implements Firebase<T> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(AdvancedDatabaseManager.class);

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
    public AdvancedDatabaseManager(Database db) {
        super(db);
    }

    // endregion

    // region Public static methods

    /**
     * Převede formát Base64 na pole bytu
     *
     * @param source Base64
     * @return Pole bytu
     */
    public static byte[] base64ToBlob(String source) {
        return Base64Util.decode(source);
    }

    /**
     * Převede poly bytu na Base64
     *
     * @param source Pole bytu
     * @return Base64
     */
    public static String blobToBase64(byte[] source) {
        return Base64Util.encode(source);
    }

    // endregion

    // region Private methods

    /**
     * Konvertuje {@link DataSnapshot]} na instanci třídy {@link T}
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
        super.insert(item);
        if (!showOnline) {
            usedItems.add(item);
        }
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

        if (!showOnline) {
            usedItems.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .ifPresent(items::remove);
        }
    }

    @Override
    public void deleteRemote(T item, boolean remote) {
        if (remote) {
            logger.trace("Odebírám online item {} z online databáze", item.toString());
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

        logger.trace("Nahrávám item {} do online databáze", item.toString());
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
        usedItems.setAll(showOnline ? onlineDatabase : items);
        if (showOnline) {
            usedItems.setAll(onlineDatabase);
        } else {
            usedItems.setAll(items);
        }
    }

    // endregion

    // region Getters & Setters

    /**
     * Nastaví firebase databázi
     *
     * @param firebaseDatabase {@link FirebaseDatabase}
     */
    public void setFirebaseDatabase(FirebaseDatabase firebaseDatabase) {
        firebaseReference = firebaseDatabase.getReference(getFirebaseChildName());
        firebaseReference.addChildEventListener(childEventListener);
    }

    // endregion

    private final ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            T item = parseDataSnapshot(dataSnapshot);
            logger.trace("Přidávám online item {} do svého povědomí.", item.toString());
            onlineDatabase.add(item);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            logger.trace("Položka v online databázi byla změněna");
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            T item = parseDataSnapshot(dataSnapshot);
            logger.trace("Položka v online databázi: {} byla odebrána", item.toString());
            onlineDatabase.remove(item);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

}
