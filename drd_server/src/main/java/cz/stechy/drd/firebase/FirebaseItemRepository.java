package cz.stechy.drd.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import cz.stechy.drd.model.item.ItemType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Třída reprezentující repozitář s předměty
 */
public final class FirebaseItemRepository {

    // region Constants

    private static final String ITEMS_PATH = "items";

    // endregion

    // region Variables

    private final Map<ItemType, List<ItemEventListener>> listeners = new HashMap<>();
    private final DatabaseReference itemsReference;

    // endregion

    // region Constructors

    public FirebaseItemRepository() {
        itemsReference = FirebaseDatabase.getInstance().getReference(ITEMS_PATH);
    }

    // endregion

    // region Public methods

    public void registerListener(final ItemType itemType, ItemEventListener listener) {
        final String itemTypeName = itemType.path;
        List<ItemEventListener> observables = listeners.get(itemType);
        // Pokud jsem ještě neprovedl žádnou registraci pro daný typ předmětu
        if (observables == null) {
            observables = new ArrayList<>();
            itemsReference
                .child(itemTypeName)
                .addChildEventListener(
                    new FirebaseItemListener(FirebaseItemConvertors.forItem(itemType), observables)
                );
        }

        observables.add(listener);
    }

    public void unregisterListener(final ItemType itemType, ItemEventListener listener) {
        final List<ItemEventListener> observables = listeners.get(itemType);
        if (observables == null) {
            return;
        }

        observables.remove(listener);
    }

    // endregion

    private final class FirebaseItemListener implements ChildEventListener {

        private final ItemConvertor convertor;
        private final List<ItemEventListener> listeners;

        private FirebaseItemListener(ItemConvertor convertor, List<ItemEventListener> listeners) {
            this.convertor = convertor;
            this.listeners = listeners;
        }

        private void notifyListeners(ItemEvent event) {
            listeners.forEach(itemEventListener -> itemEventListener.onEvent(event));
        }

        @Override
        public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
            final Map<String, Object> item = convertor.convert(snapshot);
            final ItemEvent event = FirebaseItemEvents.forChildAdded(item);
            notifyListeners(event);
        }

        @Override
        public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
            final Map<String, Object> item = convertor.convert(snapshot);
            final ItemEvent event = FirebaseItemEvents.forChildChanged(item);
            notifyListeners(event);
        }

        @Override
        public void onChildRemoved(DataSnapshot snapshot) {
            final Map<String, Object> item = convertor.convert(snapshot);
            final ItemEvent event = FirebaseItemEvents.forChildRemoved(item);
            notifyListeners(event);
        }

        @Override
        public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

        }

        @Override
        public void onCancelled(DatabaseError error) {

        }
    }
}
