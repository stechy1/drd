package cz.stechy.drd.model.db;

import com.google.firebase.database.FirebaseDatabase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Pomocná obalová třída kolem firebase
 */
public final class FirebaseWrapper {

    // region Variables

    private final ObjectProperty<FirebaseDatabase> firebase = new SimpleObjectProperty<>();

    // endregion

    // region Getters & Setters

    public final ReadOnlyObjectProperty<FirebaseDatabase> firebaseProperty() {
        return firebase;
    }

    public final void setFirebase(FirebaseDatabase firebase) {
        this.firebase.set(firebase);
    }

    // endregion
}
