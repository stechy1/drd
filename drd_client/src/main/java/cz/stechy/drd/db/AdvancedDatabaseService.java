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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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

    // endregion

    // region Variables

    protected final ObservableList<T> onlineDatabase = FXCollections.observableArrayList();
    private final ObservableList<T> usedItems = FXCollections.observableArrayList();
    private final Semaphore semaphore = new Semaphore(0);

    private boolean success = false;
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
            MessageSource.CLIENT, new DatabaseMessageAdministration(
                getFirebaseChildName(),
                DatabaseMessageAdministration.DatabaseAction.REGISTER)
        );
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
        this.success = message.isSuccess();
        if (!success) {
            semaphore.release();
            return;
        }

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
            semaphore.release();
        }
    };

    // endregion

    // region Public methods

    public Optional<T> selectOnline(Predicate<T> filter) {
        return onlineDatabase.stream().filter(filter).findFirst();
    }

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

    @Override
    public CompletableFuture<T> deleteAsync(T item) {
        onlineDatabase.stream()
            .filter(t -> t.equals(item))
            .findFirst()
            .ifPresent(t -> t.setDownloaded(false));
        return super.deleteAsync(item);
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
                MessageSource.CLIENT, new DatabaseMessageCRUD(
                    toStringItemMap(item), getFirebaseChildName(), DatabaseAction.CREATE,
                    item.getId())
            ));

            workingItemId = item.getId();

            try {
                semaphore.acquire();
            } catch (InterruptedException ignored) {}

            if (!success) {
                throw new RuntimeException("Nahrání se nezdařilo.");
            }

            LOGGER.info("Nahrání proběhlo v pořádku.");
            return item;
        }, ThreadPool.COMMON_EXECUTOR)
            .thenCompose(t -> {
                final T itemCopy = t.duplicate();
                itemCopy.setUploaded(true);
                return updateAsync(itemCopy);
            })
            .thenApplyAsync(ignored -> null, ThreadPool.JAVAFX_EXECUTOR);
    }

    @Override
    public CompletableFuture<Void> deleteRemoteAsync(T item) {
        return CompletableFuture.supplyAsync(() -> {
            communicator.sendMessage(new DatabaseMessage(
                MessageSource.CLIENT, new DatabaseMessageCRUD(
                    toStringItemMap(item), getFirebaseChildName(), DatabaseAction.DELETE,
                    item.getId())
            ));

            workingItemId = item.getId();

            try {
                semaphore.acquire();
            } catch (InterruptedException ignored) {}

            if (!success) {
                throw new RuntimeException("Odstranění z databáze se nezdařilo.");
            }

            LOGGER.info("Smazání proběhlo v pořádku.");
            return item;
        }, ThreadPool.COMMON_EXECUTOR)
            .thenCompose(t -> {
                final T itemCopy = t.duplicate();
                itemCopy.setUploaded(false);
                return updateAsync(itemCopy);
            })
            .thenApplyAsync(ignored -> null, ThreadPool.JAVAFX_EXECUTOR);
    }

    /**
     * Stáhne všechny online předměty do offline databáze
     *
     * @param author Autor předmětů, které chci stáhnout
     */
    public CompletableFuture<Integer> synchronize(final String author) {
        final List<T> filteredList = onlineDatabase.parallelStream()
            .filter(item -> item.getAuthor().equals(author))
            .collect(Collectors.toList());
        return saveAll(filteredList);
    }

    /**
     * Uloží všechny záznamy z předané kolekce, které ještě uloženy nejsou
     *
     * @param items Kolekce předmětů, která se má uložit
     * @return {@link CompletableFuture} Počet uložených záznamů
     */
    public CompletableFuture<Integer> saveAll(Collection items) {
        final List<T> workingList = new ArrayList<>(items);
        workingList.removeAll(super.items);

        if (workingList.isEmpty()) {
            return CompletableFuture.completedFuture(0);
        }

        return CompletableFuture.supplyAsync(() -> {
            for (T entry : workingList) {
                entry.setDownloaded(true);
                T duplicated = entry.duplicate();
                insertAsync(duplicated).join();
            }

            return workingList.size();
        }, ThreadPool.COMMON_EXECUTOR);
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
                //this.communicator.sendMessage(getRegistrationMessage());
            });
    }

    // endregion


}
