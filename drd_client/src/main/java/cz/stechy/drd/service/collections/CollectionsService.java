package cz.stechy.drd.service.collections;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Service;
import cz.stechy.drd.db.BaseOnlineTable;
import cz.stechy.drd.db.base.ITableFactory;
import cz.stechy.drd.model.item.OnlineCollection;
import cz.stechy.drd.model.item.OnlineCollection.CollectionType;
import java.util.concurrent.CompletableFuture;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CollectionsService implements ICollectionsService {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionsService.class);

    // endregion

    // region Variables

    private final BaseOnlineTable<OnlineCollection> collectionsTable;

    // endregion

    // region Constructors

    @Inject
    public CollectionsService(ITableFactory tableFactory) {
        collectionsTable = tableFactory.getOnlineTable(OnlineCollection.class);
    }

    // endregion

    // region Public methods

    @Override
    public ObservableList<OnlineCollection> getCollections() {
        return collectionsTable.selectAllOnline();
    }

    @Override
    public CompletableFuture<Void> uploadCollection(OnlineCollection onlineCollection) {
        return collectionsTable.uploadAsync(onlineCollection);
    }

    @Override
    public CompletableFuture<Void> updateCollection(OnlineCollection onlineCollection) {
        return collectionsTable.updateOnlineAsync(onlineCollection);
    }

    @Override
    public CompletableFuture<Void> deleteCollection(OnlineCollection onlineCollection) {
        return collectionsTable.deleteRemoteAsync(onlineCollection);
    }

    @Override
    public CompletableFuture<Void> addItemToCollection(OnlineCollection collection, CollectionType collectionType, String id) {
        collection.getCollection(collectionType).add(id);
        return updateCollection(collection)
            .exceptionally(throwable -> {
                collection.getCollection(collectionType).remove(id);
                throw new RuntimeException(throwable);
            });
//        return communicator.sendMessageFuture(
//            new DatabaseMessage(MessageSource.CLIENT,
//                new DatabaseMessageCRUD(toStringItemMap(collection), getFirebaseChildName(), DatabaseAction.UPDATE, collection.getId())))
//            .thenAcceptAsync(responce -> {
//                if (!responce.isSuccess()) {
//                    throw new RuntimeException("Item se nepodařilo přidat do kolekce.");
//                }
//                collection.getCollection(type).add(id);
//                LOGGER.info("Item se podařilo vložit do kolekce.");
//            }, ThreadPool.JAVAFX_EXECUTOR);
    }

    @Override
    public CompletableFuture<Void> removeItemFromCollection(OnlineCollection collection, CollectionType collectionType, String id) {
        collection.getCollection(collectionType).remove(id);
        return updateCollection(collection)
            .exceptionally(throwable -> {
                collection.getCollection(collectionType).add(id);
                throw new RuntimeException(throwable);
            });
    }

    // endregion
}
