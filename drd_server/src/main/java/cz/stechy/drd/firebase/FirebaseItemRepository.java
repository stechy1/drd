package cz.stechy.drd.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.ItemType;
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

    private final Map<ItemType, List<?>> listeners = new HashMap<>();
    private final DatabaseReference itemsReference;

    // endregion

    // region Constructors

    public FirebaseItemRepository() {
        itemsReference = FirebaseDatabase.getInstance().getReference(ITEMS_PATH);
    }

    // endregion

    // region Public methods

    public void registerListener(final ItemType itemType) {
        final String itemTypeName = itemType.path;
        List<?> observables = listeners.get(itemType);
        // Pokud jsem ještě neprovedl žádnou registraci pro daný typ předmětu
        if (observables == null) {
            itemsReference.child(itemTypeName).addChildEventListener(new MyListener(FirebaseItemConvertors.forItem(itemType)));
        }
    }

    // endregion

    public interface ItemEventListener {

        ItemEventAction getAction();

        void getItem();

        enum ItemEventAction {
            ADD, CHANGE, REMOVE;
        }
    }

    @FunctionalInterface
    public interface ItemConvertor {
        ItemBase convert(DataSnapshot snapshot);
    }

    private final class MyListener implements ChildEventListener {

        private final ItemConvertor convertor;

        private MyListener(ItemConvertor convertor) {
            this.convertor = convertor;
        }

        @Override
        public void onChildAdded(DataSnapshot snapshot, String previousChildName) {

        }

        @Override
        public void onChildChanged(DataSnapshot snapshot, String previousChildName) {

        }

        @Override
        public void onChildRemoved(DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

        }

        @Override
        public void onCancelled(DatabaseError error) {

        }
    }
}
