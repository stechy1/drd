package cz.stechy.drd;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private static final String FB_URL_TEMPLATE = "https://%s.firebaseio.com";
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
     * Inicializuje spojení s firebase
     */
    private void initFirebase() {
        LOGGER.info("Inicializuji firebase.");
        final String credentialsPath = settings.getString(CmdParser.FB_CREDENTIALS_PATH);
        LOGGER.info(String.format("Čtu přístupové údaje ze souboru: %s.", credentialsPath));
        try (FileInputStream serviceAccount = new FileInputStream(credentialsPath)) {
            final String fbURL = settings.getString(CmdParser.FB_URL);
            LOGGER.info(String.format("Připojuji se k firebase na adresu: %s.", fbURL));
            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(String.format(FB_URL_TEMPLATE, fbURL))
                .setDatabaseAuthVariableOverride(null)
                .build();
            FirebaseApp.initializeApp(options);
            LOGGER.info("Inicializace firebase se zdařila.");
        } catch (IOException e) {
            LOGGER.error("Nepodařilo se inicializovat firebase.", e);
        }
    }

    /**
     * Inicializace serveru
     */
    private void init() {
        LOGGER.info("Inicializuji server.");
        initFirebase();

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
        } catch (InterruptedException e) {}

        LOGGER.info("Server byl ukončen.");
    }
}
