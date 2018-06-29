package cz.stechy.drd;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import cz.stechy.drd.auth.AuthService;
import cz.stechy.drd.firebase.FirebaseRepository;
import cz.stechy.drd.module.AuthModule;
import cz.stechy.drd.module.FirebaseDatabaseModule;
import cz.stechy.drd.module.IModule;
import cz.stechy.drd.net.message.MessageType;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private static final String FB_URL_TEMPLATE = "https://%s.firebaseio.com";

    public static final UUID ID = UUID.randomUUID();
    // Výchozí port serveru = 0 --> automaticky se přiřadí
    private static final int SERVER_PORT = 15378;
    private static final int DEFAULT_MAX_CLIENTS = 3;
    private static final int DEFAULT_WAITING_QUEUE_SIZE = 1;
    // Scanner pro interakci s uživatelem
    private static final Scanner scanner = new Scanner(System.in);

    // Nastavení serveru
    private final CmdParser settings;
    private final ServerThread serverThread;

    public static void main(String[] args) throws IOException {
        final Server server = new Server(new CmdParser().parse(args));
        server.run();
    }

    private Server(final CmdParser parser) throws IOException {
        LOGGER.info("Spouštím server.");
        this.settings = parser;
        final int port = settings.getInteger(CmdParser.PORT, SERVER_PORT);
        final int maxClients = settings.getInteger(CmdParser.CLIENTS_COUNT, DEFAULT_MAX_CLIENTS);
        final int waitingQueueSize = settings.getInteger(CmdParser.MAX_WAITING_QUEUE, DEFAULT_WAITING_QUEUE_SIZE);
        this.serverThread = new ServerThread(port, maxClients, waitingQueueSize);

        init();
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
    private String resolveFirebaseUrl(InputStream inputStream) {
        String id = "";
        try {
            JSONObject json = new JSONObject(streamToString(inputStream));
            id = json.getString("project_id");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return String.format(FB_URL_TEMPLATE, id);
    }

    /**
     * Inicializuje spojení s firebase
     */
    private void initFirebase() {
        LOGGER.info("Inicializuji firebase.");
        final String credentialsPath = settings.getString(CmdParser.FB_CREDENTIALS_PATH);
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
            LOGGER.info("Inicializace firebase se zdařila.");
        } catch (IOException e) {
            LOGGER.error("Nepodařilo se inicializovat firebase.", e);
        }
    }

    /**
     * Inicializace jednotlivých modulů
     */
    private void initModules() {
        LOGGER.info("Registruji moduly.");
        final FirebaseRepository serverDatabase = new FirebaseRepository();
        final IModule firebaseDatabaseModule = new FirebaseDatabaseModule(serverDatabase);
        final AuthService authService = new AuthService(serverDatabase);
        final IModule authModule = new AuthModule(authService);

        // Pozor, záleží na pořadí!!!
        // Firebase se musí registrovat jako první aby se závisející moduly mohly v klidu registrovat
        serverThread.registerModule(MessageType.DATABASE, firebaseDatabaseModule);
        serverThread.registerModule(MessageType.AUTH, authModule);
        LOGGER.info("Registrace modulů dokončena.");
    }

    /**
     * Inicializace serveru
     */
    private void init() {
        LOGGER.info("Inicializuji server.");
        initFirebase();
        initModules();

        LOGGER.info("Inicializace dokončena.");
    }

    private void run() {
        serverThread.start();
        while(true) {
            final String line = scanner.nextLine();
            if (line.equals("end")) {
                LOGGER.info("Spouštím ukončovací sekvenci.");
                break;
            }
        }

        serverThread.shutdown();
        try {
            serverThread.join();
        } catch (InterruptedException ignored) {}

        LOGGER.info("Server byl ukončen.");
        scanner.close();
    }
}
