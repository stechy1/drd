package cz.stechy.drd.model.db;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.FirebaseDatabase;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Pomocná obalová třída kolem firebase
 */
public final class FirebaseWrapper {

    // region Constants

    private static final String FIREBASE_URL = "https://drd-personal-diary.firebaseio.com";

    // endregion

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

    /**
     * Inicializuje firebase databázi
     *
     * @param inputStream Stream dat s přístupovými údaji k databázi
     * @throws Exception Pokud se inicializace nezdařila
     */
    public void initDatabase(InputStream inputStream) {
        final Map<String, Object> auth = new HashMap<>();
        auth.put("uid", "my_resources");

        final FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredential(FirebaseCredentials.fromCertificate(inputStream))
            .setDatabaseUrl(FIREBASE_URL)
            .setDatabaseAuthVariableOverride(auth)
            .build();

        FirebaseApp.initializeApp(options);
        this.firebase.set(FirebaseDatabase.getInstance());
    }

    // endregion

    // region Getters & Setters

    public final ReadOnlyObjectProperty<FirebaseDatabase> firebaseProperty() {
        return firebase;
    }

    // endregion
}
