package cz.stechy.drd.model.db;

import com.google.firebase.database.FirebaseDatabase;
import java.util.Optional;
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

    // region Private methods

    private Optional<FirebaseDatabase> getDatabase() {
        return Optional.ofNullable(firebase.getValue());
    }

    // endregion

    // region Public methods

    /**
     * Pokud ji firebase inicializovaná, a je v online stavu, tak se přepne do offline modu
     */
    public void closeDatabase() {
        getDatabase().ifPresent(FirebaseDatabase::goOffline);
    }

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
