package cz.stechy.drd.db;

import com.google.inject.Inject;
import cz.stechy.drd.db.base.BaseTableDefinitions;
import cz.stechy.drd.db.base.OnlineRecord;
import cz.stechy.drd.db.base.OnlineTable;
import cz.stechy.drd.net.ConnectionState;
import cz.stechy.drd.net.OnDataReceivedListener;
import cz.stechy.drd.net.message.DatabaseMessage;
import cz.stechy.drd.net.message.DatabaseMessage.DatabaseMessageAdministration;
import cz.stechy.drd.net.message.DatabaseMessage.DatabaseMessageCRUD;
import cz.stechy.drd.net.message.DatabaseMessage.DatabaseMessageCRUD.DatabaseAction;
import cz.stechy.drd.net.message.DatabaseMessage.DatabaseMessageDataType;
import cz.stechy.drd.net.message.DatabaseMessage.IDatabaseMessageData;
import cz.stechy.drd.net.message.MessageSource;
import cz.stechy.drd.service.communicator.ClientCommunicator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("all")
public abstract class BaseOnlineTable<T extends OnlineRecord> implements OnlineTable<T> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseOnlineTable.class);

    // endregion

    // region Variables

    protected final BaseTableDefinitions<T> tableDefinitions;
    final ObservableList<T> records = FXCollections.observableArrayList();

    private ClientCommunicator communicator;

    // endregion

    // region Constructors

    @Inject
    public BaseOnlineTable(BaseTableDefinitions<T> tableDefinitions) {
        this.tableDefinitions = tableDefinitions;
    }

    // endregion

    // region Private methods

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
        final T item = fromStringMap(data);
        switch (crudAction) {
            case CREATE:
                LOGGER.trace("Přidávám online item {} do svého povědomí.", item.toString());
                Platform.runLater(() -> {
//                    records.stream()
//                        .filter(t -> item.getId().equals(t.getId()))
//                        .findFirst()
//                        .ifPresent(itemBase -> {
//                            item.setDownloaded(true);
//                            itemBase.setUploaded(true);
//                        });

                    item.setUploaded(true);
                    records.add(item);
                });
                break;
            case UPDATE:
                Platform.runLater(() -> {
//                    records.stream()
//                        .filter(ID_FILTER(item.getId()))
//                        .findFirst()
//                        .ifPresent(onlineItem -> {
//                            onlineItem.update(item);
//                            LOGGER.trace("Položka v online databázi {} byla změněna.", item.toString());
//                        });
                });
//                updateAsync(item).thenAccept(t -> {
//                    LOGGER.trace("Položka v online databázi byla změněna");
//                });
                break;
            case DELETE:
                LOGGER.trace("Položka v online databázi: {} byla odebrána", item.toString());
                records.remove(item);
                break;
            default:
                throw new IllegalArgumentException("Neplatný argument");
        }
    };

    // endregion

    // region Public methods

    @Override
    public Optional<T> selectOnline(Predicate<? super T> filter) {
        LOGGER.trace("Provádím select dotaz v tabulce: {}.", getFirebaseChildName());
        return records.stream().filter(filter).findFirst();
    }

    @Override
    public ObservableList<T> selectAllOnline() {
        return FXCollections.unmodifiableObservableList(records);
    }

    @Override
    public CompletableFuture<Void> uploadAsync(T item) {
        LOGGER.trace("Nahrávám záznam {} do online databáze do tabulky: {}", item.toString(), getFirebaseChildName());
        return communicator.sendMessageFuture(
            new DatabaseMessage(MessageSource.CLIENT,
                new DatabaseMessageCRUD(toStringItemMap(item), getFirebaseChildName(), DatabaseAction.CREATE, item.getId())))
            .thenAccept(responce -> {
                if (!responce.isSuccess()) {
                    LOGGER.error("Nahrání záznamu {} se nezdařilo.", item.toString());
                    throw new RuntimeException("Nahrání záznamu se nezdařilo.");
                }
//                final T itemCopy = item.duplicate();
//                itemCopy.setUploaded(true);
//                return itemCopy;
            });
    }

    @Override
    public CompletableFuture<Void> updateOnlineAsync(T item) {
        LOGGER.trace("Aktualizuji online záznam {} v tabulce: {}", item.toString(), getFirebaseChildName());
        return communicator.sendMessageFuture(
            new DatabaseMessage(MessageSource.CLIENT,
                new DatabaseMessageCRUD(toStringItemMap(item), getFirebaseChildName(), DatabaseAction.UPDATE, item.getId())))
            .thenAccept(responce-> {
                if(!responce.isSuccess()) {
                    LOGGER.error("Aktualizace záznamu {} se nezdařila.", item.toString());
                    throw new RuntimeException("Aktualizace záznamu se nezdařila.");
                }
            });
    }

    @Override
    public CompletableFuture<Void> deleteRemoteAsync(T item) {
        LOGGER.trace("Mažu záznam {} z online databáze z tabulky: {}", item.toString(), getFirebaseChildName());
        return communicator.sendMessageFuture(
            new DatabaseMessage(MessageSource.CLIENT,
                new DatabaseMessageCRUD(toStringItemMap(item), getFirebaseChildName(), DatabaseAction.DELETE, item.getId())))
            .thenAccept(responce -> {
                    if (!responce.isSuccess()) {
                        LOGGER.error("Odstranění záznamu {} se nezdařilo.", item.toString());
                        throw new RuntimeException("Odstranění online záznamu z databáze se nezdařilo.");
                    }
                });
//            }, ThreadPool.JAVAFX_EXECUTOR)
//            .thenComposeAsync(ignored -> {
//                final T itemCopy = item.duplicate();
//                itemCopy.setUploaded(false);
//                return updateAsync(itemCopy);
//            }, ThreadPool.COMMON_EXECUTOR)
//            .thenAcceptAsync(ignored -> {}, ThreadPool.JAVAFX_EXECUTOR);
    }

    @Override
    public Map<String, Object> toStringItemMap(T item) {
        return tableDefinitions.toStringItemMap(item);
    }

    // endregion

    @Inject
    public final void setCommunicator(ClientCommunicator communicator) {
        this.communicator = communicator;
        this.communicator.connectionStateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != ConnectionState.CONNECTED) {
                records.clear();
                return;
            }

            this.communicator.registerMessageObserver(DatabaseMessage.MESSAGE_TYPE, this.databaseListener);
            LOGGER.info("Posílám registrační požadavek pro tabulku: {}", getFirebaseChildName());
            this.communicator.sendMessage(
                new DatabaseMessage(MessageSource.CLIENT,
                    new DatabaseMessageAdministration(getFirebaseChildName(), DatabaseMessageAdministration.DatabaseAction.REGISTER)));
        });
    }
}
