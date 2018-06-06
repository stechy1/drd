package cz.stechy.drd.db;

import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.db.base.OnlineDatabase;
import cz.stechy.drd.db.base.OnlineItem;
import cz.stechy.drd.di.Inject;
import cz.stechy.drd.net.ClientCommunicator;
import cz.stechy.drd.net.ConnectionState;
import cz.stechy.drd.net.OnDataReceivedListener;
import cz.stechy.drd.net.message.DatabaseMessage;
import cz.stechy.drd.net.message.DatabaseMessage.DatabaseMessageAdministration;
import cz.stechy.drd.net.message.DatabaseMessage.DatabaseMessageCRUD;
import cz.stechy.drd.net.message.DatabaseMessage.DatabaseMessageCRUD.DatabaseAction;
import cz.stechy.drd.net.message.DatabaseMessage.DatabaseMessageDataType;
import cz.stechy.drd.net.message.DatabaseMessage.IDatabaseMessageData;
import cz.stechy.drd.net.message.IMessage;
import cz.stechy.drd.net.message.MessageSource;
import cz.stechy.drd.net.message.MessageType;
import cz.stechy.drd.util.Base64Util;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Semaphore;
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
    BaseDatabaseService<T> implements OnlineDatabase<T> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvancedDatabaseService.class);

    private static final ForkJoinPool ONLINE_POOL = new ForkJoinPool(1);

    // endregion

    // region Variables

    protected final ObservableList<T> onlineDatabase = FXCollections.observableArrayList();
    private final ObservableList<T> usedItems = FXCollections.observableArrayList();
    private final Semaphore onlineSemaphore = new Semaphore(0);

    private boolean showOnline = false;
    private String workingItemId;
    private ClientCommunicator communicator;

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

    private IMessage getRegistrationMessage() {
        return new DatabaseMessage(
            new DatabaseMessageAdministration(
                getFirebaseChildName(),
                DatabaseMessageAdministration.DatabaseAction.REGISTER),
            MessageSource.CLIENT);
    }

    // Listener reagující na změnu ve zdrojovém listu a propagujíce změnu do výstupného listu
    private final ListChangeListener<? super T> listChangeListener = (ListChangeListener<T>) c -> {
        while (c.next()) {
            this.usedItems.addAll(c.getAddedSubList());
            this.usedItems.removeAll(c.getRemoved());
        }
    };

    @SuppressWarnings("unchecked")
    private final OnDataReceivedListener databaseListener = message -> {
        final DatabaseMessage databaseMessage = (DatabaseMessage) message;
        final IDatabaseMessageData databaseMessageData = (IDatabaseMessageData) databaseMessage
            .getData();
        if (databaseMessageData.getDataType() != DatabaseMessageDataType.DATA_MANIPULATION) {
            return;
        }

        final DatabaseMessageCRUD crudMessage = (DatabaseMessageCRUD) databaseMessageData;
        if (!crudMessage.getTableName().equals(getFirebaseChildName())) {
            return;
        }

        final DatabaseAction crudAction = crudMessage.getAction();
        final Map<String, Object> data = (Map<String, Object>) crudMessage.getData();
        final T item = fromStringItemMap(data);
        switch (crudAction) {
            case CREATE:
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
                break;
            case UPDATE:
                LOGGER.trace("Položka v online databázi byla změněna");
                // TODO vymslet, jak aktualizovat údaj
                break;
            case DELETE:
                LOGGER.trace("Položka v online databázi: {} byla odebrána", item.toString());
                onlineDatabase.remove(item);
                break;
            default:
                throw new IllegalArgumentException("Neplatný argument");
        }

        if (item.getId().equals(workingItemId)) {
            workingItemId = null;
            onlineSemaphore.release();
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

    @Override
    public CompletableFuture<Void> uploadAsync(T item) {
        return CompletableFuture.supplyAsync(() -> {
            communicator.sendMessage(new DatabaseMessage(
                new DatabaseMessageCRUD(
                    DatabaseAction.CREATE,
                    getFirebaseChildName(),
                    toStringItemMap(item)),
                MessageSource.CLIENT));

            workingItemId = item.getId();
            try {
                onlineSemaphore.acquire();
            } catch (InterruptedException ignored) {}

            LOGGER.info("Nahrání proběhlo v pořádku.");
            return null;
        }, ONLINE_POOL)
            .thenApplyAsync(ignored -> {
                return null;
            }, ThreadPool.JAVAFX_EXECUTOR);
    }

    @Override
    public CompletableFuture<Void> deleteRemoteAsync(T item) {
        return null;
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

    @Inject
    @SuppressWarnings("unused")
    public void setCommunicator(ClientCommunicator communicator) {
        this.communicator = communicator;
        this.communicator.connectionStateProperty()
            .addListener((observable, oldValue, newValue) -> {
                if (newValue != ConnectionState.CONNECTED) {
                    return;
                }

                this.communicator
                    .registerMessageObserver(MessageType.DATABASE, this.databaseListener);
                LOGGER.info("Posílám registrační požadavek pro tabulku: " + getFirebaseChildName());
                this.communicator.sendMessage(getRegistrationMessage());
            });
    }

    // endregion


}
