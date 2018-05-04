package cz.stechy.drd.db;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseReference.CompletionListener;
import com.google.firebase.database.FirebaseDatabase;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.db.base.Firebase;
import cz.stechy.drd.db.base.OnlineItem;
import cz.stechy.drd.di.Inject;
import cz.stechy.drd.util.Base64Util;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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

    protected final ObservableList<T> onlineDatabase = FXCollections.observableArrayList();
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
     * @return Vrátí název potomka ve firebase
     */
    protected abstract String getFirebaseChildName();

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

    // Listener reagující na změnu ve zdrojovém listu a propagujíce změnu do výstupného listu
    private final ListChangeListener<? super T> listChangeListener = (ListChangeListener<T>) c -> {
        while (c.next()) {
            this.usedItems.addAll(c.getAddedSubList());
            this.usedItems.removeAll(c.getRemoved());
        }
    };

    // endregion

    // region Public methods

    @Override
    public CompletableFuture<ObservableList<T>> selectAllAsync() {
        return super.selectAllAsync().thenApply(resultItems -> usedItems);
    }

    @Override
    public CompletableFuture<T> insertAsync(T item) {
        if (showOnline) {
            item.setUploaded(true);
        }
        item.setDownloaded(true);
        return super.insertAsync(item);
    }

    public Map<String, Object> toFirebaseMap(T item) {
        String[] columns = getColumnsKeys().split(",");
        Object[] values = itemToParams(item).toArray();
        assert columns.length == values.length;
        final Map<String, Object> map = new HashMap<>(columns.length);

        for (int i = 0; i < columns.length; i++) {
            map.put(columns[i], values[i]);
        }

        return map;
    }

    @Override
    public void deleteRemoteAsync(T item, boolean remote, CompletionListener listener) {
        if (remote) {
            LOGGER.trace("Odebírám online item {} z online databáze", item.toString());
            final DatabaseReference child = firebaseReference.child(item.getId());
            child.removeValue((error, ref) -> {
                T itemCopy = item.duplicate();
                itemCopy.setUploaded(false);
                updateAsync(itemCopy)
                    .exceptionally(throwable -> {
                        listener
                            .onComplete(DatabaseError.fromCode(DatabaseError.USER_CODE_EXCEPTION),
                                ref);
                        throw new RuntimeException(throwable);
                    })
                    .thenAccept(t -> listener.onComplete(null, ref));
            });
        } else {
            deleteAsync(item)
                .exceptionally(throwable -> {
                    listener.onComplete(DatabaseError.fromCode(DatabaseError.USER_CODE_EXCEPTION),
                        null);
                    throw new RuntimeException(throwable);
                })
                .thenAccept(t -> listener.onComplete(null, null));
        }
    }

    @Override
    public void uploadAsync(T item, CompletionListener listener) {
        if (firebaseReference == null) {
            listener.onComplete(DatabaseError.fromCode(DatabaseError.USER_CODE_EXCEPTION), null);
            return;
        }

        LOGGER.trace("Nahrávám item {} do online databáze", item.toString());
        final DatabaseReference child = firebaseReference.child(item.getId());
        child.setValue(toFirebaseMap(item), (error, ref) -> {
            T itemCopy = item.duplicate();
            itemCopy.setUploaded(true);
            updateAsync(itemCopy)
                .exceptionally(throwable -> {
                    listener
                        .onComplete(DatabaseError.fromCode(DatabaseError.USER_CODE_EXCEPTION), ref);
                    throw new RuntimeException(throwable);
                })
                .thenAccept(t -> listener.onComplete(null, ref));
        });
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
     */
    public CompletableFuture<Integer> synchronize(final String author) {
        final Object lock = new Object();
        final int[] updated = new int[]{0};
        return CompletableFuture.supplyAsync(() -> {
            onlineDatabase
                .stream()
                .filter(onlineItem -> Objects.equals(onlineItem.getAuthor(), author))
                .forEach(onlineItem -> {
                    LOGGER.info(
                        "Pokus o synchronizaci předmětu: " + onlineItem.toString() + " ve vlákně: "
                            + Thread.currentThread());
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
                            updateAsync(offlineDuplicate)
                                .thenAccept(t -> {
                                    synchronized (lock) {
                                        updated[0]++;
                                    }
                                })
                                .join();
                        }
                        // Nemám-li offline záznam o souboru, tak ho vytvořím
                    } else {
                        final T offlineItem = onlineItem;
                        final T offlineDuplicate = offlineItem.duplicate();
                        offlineDuplicate.setDownloaded(true);
                        offlineDuplicate.setUploaded(true);
                        insertAsync(offlineDuplicate)
                            .thenAccept(t -> {
                                synchronized (lock) {
                                    updated[0]++;
                                }
                            })
                            .join();
                    }
                });
            return updated[0];
        });
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
        wrapper.firebaseProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                firebaseReference = newValue.getReference(getFirebaseChildName());
                firebaseReference.addChildEventListener(childEventListener);
            }
        });
    }

    // endregion

    private final ChildEventListener childEventListener = new ChildEventListener() {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final T item = parseDataSnapshot(dataSnapshot);
            LOGGER.trace("Přidávám online item {} do svého povědomí.", item.toString());

            Platform.runLater(() -> {
                items.stream()
                    .filter(t -> item.getId().equals(t.getId()))
                    .findFirst()
                    .ifPresent(itemBase -> {
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

}
