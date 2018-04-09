package cz.stechy.drd.dao;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseReference.CompletionListener;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.db.FirebaseWrapper;
import cz.stechy.drd.db.base.Firebase;
import cz.stechy.drd.model.item.ItemCollection;
import cz.stechy.drd.model.item.ItemCollection.Builder;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ItemCollectionDao implements Firebase<ItemCollection> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemCollectionDao.class);

    private static final String FIREBASE_CHILD_NAME = "collections/items";

    // region Názvy sloupečků v databázi

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_RECORDS = "records";

    // endregion

    // endregion

    // region Variables

    private final ObservableList<ItemCollection> collections = FXCollections.observableArrayList();
    private final Map<String, ItemCollectionContentDao> contentMap = new HashMap<>();
    private DatabaseReference firebaseReference;

    // endregion

    // region Constructors

    public ItemCollectionDao(FirebaseWrapper wrapper) {
        wrapper.firebaseProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                collections.clear();
                firebaseReference = newValue.getReference(FIREBASE_CHILD_NAME);
                firebaseReference.addChildEventListener(childEventListener);
            }
        });
    }

    // endregion

    // region Public methods

    public ItemCollectionContentDao getContent(ItemCollection collection) {
        final String id = collection.getId();
        ItemCollectionContentDao collectionContent;
        if (!contentMap.containsKey(id)) {
            collectionContent = new ItemCollectionContentDao(firebaseReference.child(collection.getId()).child(COLUMN_RECORDS));
            contentMap.put(id, collectionContent);
        } else {
            collectionContent = contentMap.get(id);
        }

        return collectionContent;
    }

    // endregion

    // region Private methods

    @Override
    public ItemCollection parseDataSnapshot(DataSnapshot snapshot) {
        final Builder builder = new Builder()
            .id(snapshot.child(COLUMN_ID).getValue(String.class))
            .name(snapshot.child(COLUMN_NAME).getValue(String.class))
            .author(snapshot.child(COLUMN_AUTHOR).getValue(String.class));
        return builder.build();
    }

    @Override
    public Map<String, Object> toFirebaseMap(ItemCollection item) {
        final Map<String, Object> map = new HashMap<>();
        map.put(COLUMN_ID, item.getId());
        map.put(COLUMN_NAME, item.getName());
        map.put(COLUMN_AUTHOR, item.getAuthor());
        return map;
    }

    @Override
    public void uploadAsync(ItemCollection item, DatabaseReference.CompletionListener listener) {
        final DatabaseReference child = firebaseReference.child(item.getId());
        child.setValue(toFirebaseMap(item), listener);
    }

    @Override
    public void deleteRemoteAsync(ItemCollection item, boolean remote, CompletionListener listener) {
        firebaseReference.child(item.getId()).removeValue(listener);
    }

    // endregion

    // region Getters & Setters

    public ObservableList<ItemCollection> getCollections() {
        return collections;
    }

    // endregion

    private final ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final ItemCollection user = parseDataSnapshot(dataSnapshot);
            LOGGER.trace("Přidávám kolekci předmětů {} z online databáze", user.toString());
            collections.add(user);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            LOGGER.trace("Data v kolekci předmětů byla změněna v online databázi");
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            final ItemCollection u = parseDataSnapshot(dataSnapshot);
            LOGGER.trace("Kolekce předmětů byla smazána z online databáze", u.toString());
            collections.stream()
                .filter(u::equals)
                .findFirst()
                .ifPresent(collections::remove);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
