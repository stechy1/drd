package cz.stechy.drd.model.db;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.json.JSONObject;

/**
 * Pomocná obalová třída kolem firebase
 */
public final class FirebaseWrapper {

    // region Constants

    //private static final String FIREBASE_URL = "https://drd-personal-diary.firebaseio.com";
    private static final String FIREBASE_URL = "https://%s.firebaseio.com";

    // endregion

    // region Variables

    private final ObjectProperty<FirebaseDatabase> firebase = new SimpleObjectProperty<>();

    // endregion

    // region Private methods

    private Optional<FirebaseDatabase> getDatabase() {
        return Optional.ofNullable(firebase.getValue());
    }

    /**
     * Pomocná metoda pro převedení {@link InputStream} na {@link String}
     *
     * @param inputStream {@link InputStream} Vstupní proud dat
     * @return Textový řetězec
     * @throws IOException Pokud se to nepovede
     */
    private static String streamToString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        char[] buffer = new char[256];

        int length;
        while((length = reader.read(buffer)) != -1) {
            stringBuilder.append(buffer, 0, length);
        }

        inputStream.close();
        return stringBuilder.toString();
    }

    /**
     * Přečte credentials soubor a vytáhne ID projektu databáze, který koresponduje s url adresou
     * databáze
     *
     * @param inputStream {@link InputStream}
     * @return ID projektu databáze
     */
    private String resolveFirebaseUrl(InputStream inputStream) {
        String id = "";
        try {
            JSONObject json = new JSONObject(streamToString(inputStream));
            id = json.getString("project_id");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return String.format(FIREBASE_URL, id);
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
     * @param inputFile Soubor s přístupovými údaji k firebase databázi
     * @throws Exception Pokud se inicializace nezdařila
     */
    public void initDatabase(File inputFile) throws IOException {
        final Map<String, Object> auth = new HashMap<>();
        auth.put("uid", "my_resources");
        final String url = resolveFirebaseUrl(new FileInputStream(inputFile));

        final FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(new FileInputStream(inputFile)))
            .setDatabaseUrl(url)
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
