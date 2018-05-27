package cz.stechy.drd.db;

import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.db.base.OnlineItem;
import cz.stechy.drd.util.Base64Util;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vylepšený databázový manažer o možnost spojení s firebase
 */
public abstract class AdvancedDatabaseService<T extends OnlineItem> extends
    BaseDatabaseService<T> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvancedDatabaseService.class);

    // endregion

    // region Variables

    protected final ObservableList<T> onlineDatabase = FXCollections.observableArrayList();
    private final ObservableList<T> usedItems = FXCollections.observableArrayList();

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

    public Map<String, Object> toStringItemMap(T item) {
        String[] columns = getColumnsKeys().split(",");
        Object[] values = itemToParams(item).toArray();
        assert columns.length == values.length;
        final Map<String, Object> map = new HashMap<>(columns.length);

        for (int i = 0; i < columns.length; i++) {
            map.put(columns[i], values[i]);
        }

        return map;
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

    // endregion


}
