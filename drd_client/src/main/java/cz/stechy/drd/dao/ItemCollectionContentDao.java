package cz.stechy.drd.dao;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseReference.CompletionListener;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.service.OnlineItemRegistry;
import java.util.Map;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ItemCollectionContentDao implements Firebase<ItemBase> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemCollectionContentDao.class);

    // endregion

    // region Variables

    private final ObservableList<ItemBase> items = FXCollections.observableArrayList();
    private final DatabaseReference reference;

    // endregion

    // region Constructors

    public ItemCollectionContentDao(DatabaseReference reference) {
        this.reference = reference;
        reference.addChildEventListener(childEventListener);
    }

    // endregion

    // region Private methods

    @Override
    public ItemBase parseDataSnapshot(DataSnapshot snapshot) {
        final String id = snapshot.getValue(String.class);
        final Optional<ItemBase> optional = OnlineItemRegistry.getINSTANCE().getItemById(id);
        return optional.orElse(null);
    }

    @Override
    public Map<String, Object> toFirebaseMap(ItemBase item) {
        throw new NotImplementedException();
    }

    @Override
    public void uploadAsync(ItemBase item, CompletionListener listener) {
        if (items.contains(item)) {
            return;
        }

        reference.child(item.getId()).setValue(item.getId(), null);
    }

    @Override
    public void deleteRemoteAsync(ItemBase item, boolean remote, CompletionListener listener) {
        reference.child(item.getId()).removeValue(listener);
    }

    // endregion

    // region Getters & Setters

    public ObservableList<ItemBase> getItems() {
        return items;
    }

    // endregion

    private final ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final ItemBase item = parseDataSnapshot(dataSnapshot);
            LOGGER.trace("Přidávám předmět do kolekce předmětů {} z online databáze",
                item.toString());
            items.add(item);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            LOGGER.trace("Předmet v kolekci předmětů byl změněn v online databázi");
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            final ItemBase item = parseDataSnapshot(dataSnapshot);
            LOGGER
                .trace("Předmet v kolekci předmětů byl smazán z online databáze", item.toString());
            items.stream()
                .filter(item::equals)
                .findFirst()
                .ifPresent(items::remove);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
