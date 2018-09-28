package cz.stechy.drd.dao;

import static cz.stechy.drd.R.Database.Collections.COLUMN_AUTHOR;
import static cz.stechy.drd.R.Database.Collections.COLUMN_BESTIARY;
import static cz.stechy.drd.R.Database.Collections.COLUMN_ID;
import static cz.stechy.drd.R.Database.Collections.COLUMN_ITEMS;
import static cz.stechy.drd.R.Database.Collections.COLUMN_NAME;
import static cz.stechy.drd.R.Database.Collections.COLUMN_SPELLS;
import static cz.stechy.drd.R.Database.Collections.FIREBASE_CHILD;
import static cz.stechy.drd.R.Database.Collections.TABLE_NAME;

import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.db.base.OnlineDatabase;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.model.item.ItemCollection;
import cz.stechy.drd.model.item.ItemCollection.CollectionType;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ItemCollectionDao implements OnlineDatabase<ItemCollection> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemCollectionDao.class);

    // endregion

    // region Variables

    private final ObservableList<ItemCollection> collections = FXCollections.observableArrayList();
    private final ClientCommunicator communicator;

    // endregion

    // region Constructors

    public ItemCollectionDao(ClientCommunicator clientCommunicator) {
        this.communicator = clientCommunicator;
        this.communicator.connectionStateProperty()
            .addListener((observable, oldValue, newValue) -> {
                if (newValue != ConnectionState.CONNECTED) {
                    collections.clear();
                    return;
                }

                this.communicator
                    .registerMessageObserver(DatabaseMessage.MESSAGE_TYPE, this.databaseListener);
                LOGGER.info("Posílám registrační požadavek pro tabulku: " + TABLE_NAME);
                this.communicator.sendMessage(getRegistrationMessage());
            });
    }

    // endregion

    // region Private methods

    private IMessage getRegistrationMessage() {
        return new DatabaseMessage(
            MessageSource.CLIENT, new DatabaseMessageAdministration(
            FIREBASE_CHILD,
            DatabaseMessageAdministration.DatabaseAction.REGISTER));
    }

    // endregion

    // region Public methods

    public CompletableFuture<Void> addItemToCollection(ItemCollection collection, CollectionType type, String id) {
        return communicator.sendMessageFuture(
            new DatabaseMessage(MessageSource.CLIENT,
                new DatabaseMessageCRUD(toStringItemMap(collection), getFirebaseChildName(), DatabaseAction.UPDATE, collection.getId())))
            .thenAcceptAsync(responce -> {
                if (!responce.isSuccess()) {
                    throw new RuntimeException("Item se nepodařilo přidat do kolekce.");
                }
                collection.getCollection(type).add(id);
                LOGGER.info("Item se podařilo vložit do kolekce.");
            }, ThreadPool.JAVAFX_EXECUTOR);
    }

    public CompletableFuture<Void> removeItemFromCollection(ItemCollection collection, CollectionType type, String id) {
        return communicator.sendMessageFuture(
            new DatabaseMessage(MessageSource.CLIENT,
                new DatabaseMessageCRUD(toStringItemMap(collection), getFirebaseChildName(), DatabaseAction.UPDATE, collection.getId())))
            .thenAcceptAsync(responce -> {
                if (!responce.isSuccess()) {
                    throw new RuntimeException("Item se nepodařilo smazat z kolekce.");
                }

                collection.getCollection(type).remove(id);
            }, ThreadPool.JAVAFX_EXECUTOR);
    }

    // endregion

    // region Private methods

    @Override
    public String getFirebaseChildName() {
        return FIREBASE_CHILD;
    }

    @Override
    public ItemCollection fromStringItemMap(Map<String, Object> map) {
        return new ItemCollection.Builder()
            .id((String) map.get(COLUMN_ID))
            .name((String) map.get(COLUMN_NAME))
            .author((String) map.get(COLUMN_AUTHOR))
            .items((Collection<String>) map.get(COLUMN_ITEMS))
            .bestiary((Collection<String>) map.get(COLUMN_BESTIARY))
            .spells((Collection<String>) map.get(COLUMN_SPELLS))
            .build();
    }

    @Override
    public Map<String, Object> toStringItemMap(ItemCollection item) {
        final Map<String, Object> map = new HashMap<>();
        map.put(COLUMN_ID, item.getId());
        map.put(COLUMN_NAME, item.getName());
        map.put(COLUMN_AUTHOR, item.getAuthor());
        map.put(COLUMN_ITEMS, item.getCollection(CollectionType.ITEMS).stream().collect(Collectors.toMap(o -> o, o -> o)));
        map.put(COLUMN_BESTIARY, item.getCollection(CollectionType.BESTIARY).stream().collect(Collectors.toMap(o -> o, o -> o)));
        map.put(COLUMN_SPELLS, item.getCollection(CollectionType.SPELLS).stream().collect(Collectors.toMap(o -> o, o -> o)));
        return map;
    }

    @Override
    public CompletableFuture<Void> uploadAsync(ItemCollection item) {
        return communicator.sendMessageFuture(
            new DatabaseMessage(MessageSource.CLIENT,
                new DatabaseMessageCRUD(toStringItemMap(item), getFirebaseChildName(), DatabaseAction.CREATE, item.getId())))
            .thenAcceptAsync(responce -> {
            if (!responce.isSuccess()) {
                throw new RuntimeException("Nahrání se nezdařilo.");
            }
            }, ThreadPool.JAVAFX_EXECUTOR);
    }

    @Override
    public CompletableFuture<Void> updateOnlineAsync(ItemCollection item) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> deleteRemoteAsync(ItemCollection item) {
            return communicator.sendMessageFuture(
                new DatabaseMessage(MessageSource.CLIENT,
                    new DatabaseMessageCRUD(toStringItemMap(item), getFirebaseChildName(), DatabaseAction.DELETE, item.getId())))
                .thenAcceptAsync(responce -> {
                    if (!responce.isSuccess()) {
                        throw new RuntimeException("Smazání záznamu se nezdařilo.");
                    }
                }, ThreadPool.JAVAFX_EXECUTOR);
    }

    // endregion

    // region Getters & Setters

    public ObservableList<ItemCollection> getCollections() {
        return collections;
    }

    // endregion

    private final OnDataReceivedListener databaseListener = message -> {
        final DatabaseMessage databaseMessage = (DatabaseMessage) message;
        IDatabaseMessageData databaseMessageData = (IDatabaseMessageData) databaseMessage.getData();

        if (databaseMessageData.getDataType() != DatabaseMessageDataType.DATA_MANIPULATION) {
            return;
        }

        final DatabaseMessageCRUD crudMessage = (DatabaseMessageCRUD) databaseMessageData;
        if (!crudMessage.getTableName().equals(FIREBASE_CHILD)) {
            return;
        }

        final DatabaseMessageCRUD.DatabaseAction crudAction = crudMessage.getAction();
        final Map<String, Object> data = (Map<String, Object>) crudMessage.getData();
        final ItemCollection itemCollection = fromStringItemMap(data);

        switch (crudAction) {
            case CREATE:
                LOGGER.info("Přidávám kolekci {} do svého povědomí.", itemCollection.toString());
                Platform.runLater(() -> collections.add(itemCollection));
                break;
            case UPDATE:
                LOGGER.info("Musím aktualizovat záznam.");
                break;
            case DELETE:
                LOGGER.trace("Kolekce předmětů {} byla smazána z online databáze",
                    itemCollection.toString());
                Platform.runLater(() -> collections.remove(itemCollection));
                break;
            default:
                throw new IllegalArgumentException("Neplatny argument");
        }
    };

}
