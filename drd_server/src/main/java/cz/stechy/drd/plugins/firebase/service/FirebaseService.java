package cz.stechy.drd.plugins.firebase.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.inject.Inject;
import cz.stechy.drd.cmd.CmdParser;
import cz.stechy.drd.cmd.IParameterFactory;
import cz.stechy.drd.plugins.firebase.FirebaseEntryEvent;
import cz.stechy.drd.plugins.firebase.FirebaseEntryEventListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FirebaseService implements IFirebaseService {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseService.class);

    private static final String FB_URL_TEMPLATE = "https://%s.firebaseio.com";

    // endregion

   // region Variables

    private final Map<String, List<FirebaseEntryEventListener>> listeners = new HashMap<>();
    private final Map<String, List<Map<String, Object>>> entries = new HashMap<>();
    private final IParameterFactory parameterFactory;

    private DatabaseReference entriesReference;
    private boolean initialized = false;

   // endregion

    // region Constructors

    @Inject
    public FirebaseService(IParameterFactory parameterFactory) {
        this.parameterFactory = parameterFactory;
    }

    // endregion

    // region Private methods

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
        while ((length = reader.read(buffer)) != -1) {
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
    private static String resolveFirebaseUrl(InputStream inputStream) {
        String id = "";
        try {
            JSONObject json = new JSONObject(streamToString(inputStream));
            id = json.getString("project_id");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return String.format(FB_URL_TEMPLATE, id);
    }

    // endregion

    @Override
    public void init() {
        if (initialized) {
            LOGGER.warn("Firebase již byla inicializována.");
            return;
        }

        LOGGER.info("Inicializuji Firebase...");
        final String credentialsPath = parameterFactory.getParameters().getString(CmdParser.FB_URL);
        LOGGER.info(String.format("Čtu přístupové údaje ze souboru: %s.", credentialsPath));
        try (FileInputStream serviceAccount = new FileInputStream(credentialsPath)) {
            final String fbURL = resolveFirebaseUrl(new FileInputStream(credentialsPath));
            final Map<String, Object> auth = new HashMap<>();
            auth.put("uid", "my_resources");
            LOGGER.info(String.format("Připojuji se k firebase na adresu: %s.", fbURL));
            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(fbURL)
                .setDatabaseAuthVariableOverride(auth)
                .build();
            FirebaseApp.initializeApp(options);
            this.entriesReference = FirebaseDatabase.getInstance().getReference();
            initialized = true;
            LOGGER.info("Inicializace Firebase se zdařila.");
        } catch (IOException e) {
            LOGGER.error("Nepodařilo se inicializovat Firebase.", e);
        }
    }

    @Override
    public void performInsert(String tableName, Map<String, Object> item, String id) {
        try {
            this.entriesReference
                .child(tableName)
                .child(id)
                .setValueAsync(item).get();
        } catch (Exception ignored) {}
    }

    @Override
    public void performUpdate(String tableName, Map<String, Object> item, String id) {
        try {
            this.entriesReference
                .child(tableName)
                .child(id)
                .updateChildrenAsync(item).get();
        } catch (Exception ignored) {}
    }

    @Override
    public void performDelete(String tableName, String id) {
        try {
            this.entriesReference
                .child(tableName)
                .child(id)
                .removeValueAsync().get();
        } catch (Exception ignored) {}

        final List<Map<String, Object>> maps = entries.get(tableName);
    }

    @Override
    public void registerListener(String tableName, FirebaseEntryEventListener listener) {
        List<FirebaseEntryEventListener> observables = listeners.get(tableName);
        // Pokud jsem ještě neprovedl žádnou registraci pro daný typ předmětu
        if (observables == null) {
            observables = new ArrayList<>();
            entriesReference
                .child(tableName)
                .addChildEventListener(
                    new FirebaseItemListener(FirebaseConvertors.forKey(tableName), observables, tableName)
                );
            listeners.put(tableName, observables);
        }

        observables.add(listener);

        // Tímto zajistím, že se ke každému klientovi dostanou všechny lokálně uložené itemy
        // Při prvním průchodu bude toto prázdné...
        final List<Map<String, Object>> itemList = entries.get(tableName);
        itemList.stream()
            .map(item -> FirebaseEntryEvents.forChildAdded(item, tableName))
            .forEach(listener::onEvent);
    }

    @Override
    public void unregisterListener(String tableName, FirebaseEntryEventListener listener) {
        final List<FirebaseEntryEventListener> observables = listeners.get(tableName);
        if (observables == null) {
            return;
        }

        observables.remove(listener);
    }

    @Override
    public void unregisterFromAllListeners(FirebaseEntryEventListener listener) {
        for (List<FirebaseEntryEventListener> itemEventListeners : listeners.values()) {
            itemEventListeners.remove(listener);
        }
    }

    /**
     * Pomocná třída komunikující přímo s firebase databazí
     */
    private final class FirebaseItemListener implements ChildEventListener {

        private final FirebaseConvertor convertor;
        private final List<FirebaseEntryEventListener> listeners;
        private final String tableName;

        private FirebaseItemListener(FirebaseConvertor convertor, List<FirebaseEntryEventListener> listeners, String tableName) {
            this.convertor = convertor;
            this.listeners = listeners;
            this.tableName = tableName;
            entries.put(tableName, new ArrayList<>());
        }

        private void notifyListeners(FirebaseEntryEvent event) {
            listeners.forEach(FirebaseEntryEventListener -> FirebaseEntryEventListener.onEvent(event));
        }

        @Override
        public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
            final Map<String, Object> item = convertor.convert(snapshot);
            // Uložení itemu do lokální kolekce
            final List<Map<String, Object>> list = entries.get(tableName);
            list.add(item);

            final FirebaseEntryEvent event = FirebaseEntryEvents.forChildAdded(item, tableName);
            notifyListeners(event);
        }

        @Override
        public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
            final Map<String, Object> item = convertor.convert(snapshot);
            final List<Map<String, Object>> list = entries.get(tableName);
            final String idKey = tableName + "_id";
            final Object id = item.get(idKey);
            for (final Map<String, Object> map : list) {
                if (map.containsValue(id)) {
                    map.clear();
                    map.putAll(item);
                    break;
                }
            }


            final FirebaseEntryEvent event = FirebaseEntryEvents.forChildChanged(item, tableName);
            notifyListeners(event);
        }

        @Override
        public void onChildRemoved(DataSnapshot snapshot) {
            final Map<String, Object> item = convertor.convert(snapshot);
            final List<Map<String, Object>> list = entries.get(tableName);
            final String idKey = tableName + "_id";
//            item.keySet().parallelStream()
//                .filter(s -> s.contains("id"))
//                .findFirst()
//                .ifPresent(s -> {
            final Object id = item.get(idKey);
            for(Iterator<Map<String, Object>> it = list.iterator(); it.hasNext();) {
                final Map<String, Object> map = it.next();
                if (map.containsValue(id)) {
                    it.remove();
                    break;
                }
            }
//                });

            final FirebaseEntryEvent event = FirebaseEntryEvents.forChildRemoved(item, tableName);
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
