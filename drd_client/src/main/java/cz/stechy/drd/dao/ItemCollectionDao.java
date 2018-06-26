package cz.stechy.drd.dao;

import static cz.stechy.drd.R.Database.Collectionsitems.COLUMN_AUTHOR;
import static cz.stechy.drd.R.Database.Collectionsitems.COLUMN_ID;
import static cz.stechy.drd.R.Database.Collectionsitems.COLUMN_NAME;
import static cz.stechy.drd.R.Database.Collectionsitems.COLUMN_RECORDS;
import static cz.stechy.drd.R.Database.Collectionsitems.FIREBASE_CHILD;
import static cz.stechy.drd.R.Database.Collectionsitems.TABLE_NAME;

import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.db.base.OnlineDatabase;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.model.item.ItemCollection;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
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
    private final Map<String, ItemCollectionContentDao> contentMap = new HashMap<>();
    private final Semaphore semaphore = new Semaphore(0);
    private final ClientCommunicator communicator;
    private String workingId;
    private boolean success;

    // endregion

    // region Constructors

    public ItemCollectionDao(ClientCommunicator clientCommunicator) {
        this.communicator = clientCommunicator;
        this.communicator.connectionStateProperty()
            .addListener((observable, oldValue, newValue) -> {
                if (newValue != ConnectionState.CONNECTED) {
                    return;
                }

                this.communicator
                    .registerMessageObserver(MessageType.DATABASE, this.databaseListener);
                LOGGER.info("Posílám registrační požadavek pro tabulku: " + TABLE_NAME);
                this.communicator.sendMessage(getRegistrationMessage());
            });
//        wrapper.firebaseProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue != null) {
//                collections.clear();
//                firebaseReference = newValue.getReference(FIREBASE_CHILD_NAME);
//                firebaseReference.addChildEventListener(childEventListener);
//            }
//        });
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

    @Deprecated
    public ItemCollectionContentDao getContent(ItemCollection collection) {
        final String id = collection.getId();
        ItemCollectionContentDao collectionContent = null;
        if (!contentMap.containsKey(id)) {
//            collectionContent = new ItemCollectionContentDao(firebaseReference.child(collection.getId()).child(COLUMN_RECORDS));
//            contentMap.put(id, collectionContent);
        } else {
            collectionContent = contentMap.get(id);
        }

        return collectionContent;
    }

    public CompletableFuture<Void> addItemToCollection(ItemCollection collection, String id) {
        return CompletableFuture.supplyAsync(() -> {
            workingId = collection.getId();

            collection.getRecords().add(id);
            communicator.sendMessage(new DatabaseMessage(MessageSource.CLIENT,
                new DatabaseMessageCRUD(toStringItemMap(collection),
                    getFirebaseChildName(),
                    DatabaseAction.UPDATE, collection.getId())));

            try {
                semaphore.acquire();
            } catch (InterruptedException ignored) {}

            if (!success) {
                collection.getRecords().remove(id);
                throw new RuntimeException("Item se nepodařilo přidat do kolekce.");
            }

            return null;
        }, ThreadPool.COMMON_EXECUTOR)
            .thenApplyAsync(ignored -> null, ThreadPool.JAVAFX_EXECUTOR);
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
            .records((Collection<String>) map.get(COLUMN_RECORDS))
            .build();
    }

    @Override
    public Map<String, Object> toStringItemMap(ItemCollection item) {
        final Map<String, Object> map = new HashMap<>();
        map.put(COLUMN_ID, item.getId());
        map.put(COLUMN_NAME, item.getName());
        map.put(COLUMN_AUTHOR, item.getAuthor());
        map.put(COLUMN_RECORDS, item.getRecords().stream().collect(Collectors.toMap(o -> o, o -> o)));
        return map;
    }

    @Override
    public CompletableFuture<Void> uploadAsync(ItemCollection item) {
        return CompletableFuture.supplyAsync(() -> {
            workingId = item.getId();

            communicator.sendMessage(new DatabaseMessage(
                MessageSource.CLIENT, new DatabaseMessageCRUD(
                toStringItemMap(item), getFirebaseChildName(),
                DatabaseAction.CREATE,
                item.getId()
            )));

            try {
                semaphore.acquire();
            } catch (InterruptedException ignored) {}

            if (!success) {
                throw new RuntimeException("Nahrání se nezdařilo.");
            }

            LOGGER.info("Nahrání proběhlo v pořádku.");
            return item;

        }, ThreadPool.COMMON_EXECUTOR)
            .thenApplyAsync(ignored -> null, ThreadPool.JAVAFX_EXECUTOR);
    }

    @Override
    public CompletableFuture<Void> deleteRemoteAsync(ItemCollection item) {
        return CompletableFuture.supplyAsync(() -> {
            workingId = item.getId();

            communicator.sendMessage(new DatabaseMessage(
                MessageSource.CLIENT, new DatabaseMessageCRUD(
                    toStringItemMap(item), getFirebaseChildName(),
                DatabaseAction.DELETE,
                item.getId())));

            try {
                semaphore.acquire();
            } catch (InterruptedException ignored) {}

            if (!success) {
                throw new RuntimeException("Smazání záznamu se nezdařilo.");
            }

            LOGGER.info("Smazání proběhlo v pořádku.");
            return item;

        }, ThreadPool.COMMON_EXECUTOR)
            .thenApplyAsync(ignored -> null, ThreadPool.JAVAFX_EXECUTOR);
    }

    // endregion

    // region Getters & Setters

    public ObservableList<ItemCollection> getCollections() {
        return collections;
    }

    // endregion

    private final OnDataReceivedListener databaseListener = message -> {
        this.success = message.isSuccess();
        if (!success) {
            semaphore.release();
            return;
        }

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
                collections.add(itemCollection);
                break;
            case UPDATE:
                break;
            case DELETE:
                LOGGER.trace("Kolekce předmětů {} byla smazána z online databáze",
                    itemCollection.toString());
                collections.stream()
                    .filter(itemCollection::equals)
                    .findFirst()
                    .ifPresent(collections::remove);
                break;
            default:
                throw new IllegalArgumentException("Neplatny argument");
        }

        if (itemCollection.getId().equals(workingId)) {
            workingId = null;
            semaphore.release();
        }
    };

//    private final ChildEventListener childEventListener = new ChildEventListener() {
//        @Override
//        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//            final ItemCollection user = parseDataSnapshot(dataSnapshot);
//            LOGGER.trace("Přidávám kolekci předmětů {} z online databáze", user.toString());
//            collections.add(user);
//        }
//
//        @Override
//        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//            LOGGER.trace("Data v kolekci předmětů byla změněna v online databázi");
//        }
//
//        @Override
//        public void onChildRemoved(DataSnapshot dataSnapshot) {
//            final ItemCollection u = parseDataSnapshot(dataSnapshot);
//            LOGGER.trace("Kolekce předmětů byla smazána z online databáze", u.toString());
//            collections.stream()
//                .filter(u::equals)
//                .findFirst()
//                .ifPresent(collections::remove);
//        }
//
//        @Override
//        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//        }
//
//        @Override
//        public void onCancelled(DatabaseError databaseError) {
//
//        }
//    };
}
