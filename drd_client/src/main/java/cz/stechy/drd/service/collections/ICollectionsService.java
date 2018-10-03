package cz.stechy.drd.service.collections;

import cz.stechy.drd.model.item.OnlineCollection;
import cz.stechy.drd.model.item.OnlineCollection.CollectionType;
import java.util.concurrent.CompletableFuture;
import javafx.collections.ObservableList;

public interface ICollectionsService {

    ObservableList<OnlineCollection> getCollections();

    CompletableFuture<Void> uploadCollection(OnlineCollection onlineCollection);

    CompletableFuture<Void> updateCollection(OnlineCollection onlineCollection);

    CompletableFuture<Void> deleteCollection(OnlineCollection onlineCollection);

    CompletableFuture<Void> addItemToCollection(OnlineCollection collection, CollectionType collectionType, String id);

    CompletableFuture<Void> removeItemFromCollection(OnlineCollection collection, CollectionType collectionType, String id);
}
