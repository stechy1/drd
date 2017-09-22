package cz.stechy.drd.model.persistent;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import cz.stechy.drd.model.db.FirebaseWrapper;
import cz.stechy.drd.model.db.base.Firebase;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.ItemCollection;
import cz.stechy.drd.model.item.ItemCollection.Builder;
import cz.stechy.drd.model.service.OnlineItemRegistry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemCollectionService implements Firebase<ItemCollection> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemCollectionService.class);

    private static final String FIREBASE_CHILD_NAME = "collections/items";

    // region Názvy sloupečků v databázi

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_RECORDS = "records";

    private static final String COLUMN_ENTRY_ID = "id";
    private static final String COLUMN_ENTRY_NAME = "name";
    private static final String COLUMN_ENTRY_IMAGE = "image";

    // endregion

    // endregion

    // region Variables

    private final ObservableList<ItemCollection> collections = FXCollections.observableArrayList();
    private DatabaseReference firebaseReference;

    // endregion

    // region Constructors

    public ItemCollectionService(FirebaseWrapper wrapper) {
        wrapper.firebaseProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                collections.clear();
                firebaseReference = newValue.getReference(FIREBASE_CHILD_NAME);
                firebaseReference.addChildEventListener(childEventListener);
            }
        });
    }

    // endregion

    // region Private methods

    private ItemBase parseEntryDataSnapshot(DataSnapshot snapshot) {
        final String id = snapshot.child(COLUMN_ENTRY_ID).getValue(String.class);
        final Optional<ItemBase> optional = OnlineItemRegistry.getINSTANCE().getItemById(id);
        if (optional.isPresent()) {
            return optional.get();
        }

        return null;
    }

    private Map<String, Object> entryToMap(ItemBase item) {
        final Map<String, Object> map = new HashMap<>();
        map.put(COLUMN_ENTRY_ID, item.getId());
        return map;
    }

    // endregion

    // region Public methods



    // endregion

    @Override
    public ItemCollection parseDataSnapshot(DataSnapshot snapshot) {
        final Builder builder = new Builder()
            .id(snapshot.child(COLUMN_ID).getValue(String.class))
            .name(snapshot.child(COLUMN_NAME).getValue(String.class))
            .author(snapshot.child(COLUMN_AUTHOR).getValue(String.class));
        final DataSnapshot records = snapshot.child(COLUMN_RECORDS);
        records.getChildren().forEach(snapshot1 -> builder.entry(parseEntryDataSnapshot(snapshot1)));
        return builder.build();
    }

    @Override
    public Map<String, Object> toFirebaseMap(ItemCollection item) {
        final Map<String, Object> map = new HashMap<>();
        map.put(COLUMN_NAME, item.getName());
        map.put(COLUMN_AUTHOR, item.getAuthor());
        final List<Map<String, Object>> values = item.getItems().stream()
            .map(this::entryToMap)
            .collect(Collectors.toList());
        map.put(COLUMN_RECORDS, values);
        return map;
    }

    @Override
    public void upload(ItemCollection item) {
        final DatabaseReference child = firebaseReference.child(item.getId());
        child.child(COLUMN_NAME).setValue(item.getName());
        child.child(COLUMN_AUTHOR).setValue(item.getAuthor());
        final DatabaseReference recordsReference = child.child(COLUMN_RECORDS);
        item.getItems().forEach(itemBase -> recordsReference.push().setValue(itemBase.getId()));
    }

    @Override
    public void deleteRemote(ItemCollection item, boolean remote) {
        firebaseReference.child(item.getId()).removeValue();
    }

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
