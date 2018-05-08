package cz.stechy.drd;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server extends Thread {

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
    private final int maxClients;

    ExecutorService pool;
    private boolean running = false;
    private final List<Client> clients = new ArrayList<>();
    private final ClientDispatcher clientDispatcher;

    public static void main(String[] args) throws InterruptedException {
        final Server server = new Server(new CmdParser().parse(args));
        server.startListening();
        // Výchozí vlákno
        while(true) {
            final String line = scanner.nextLine();
            if (line.equals("end")) {
                LOGGER.info("Spouštím ukončovací sekvenci.");
                server.shutdown();
                server.join();
                LOGGER.info("Server byl ukončen.");
                break;
            }
        }
    }

    private Server(final CmdParser parser) {
        super("ServerThread");
        LOGGER.info("Spouštím server.");
        this.settings = parser;
        this.maxClients = settings.getInteger(CmdParser.CLIENTS_COUNT, DEFAULT_MAX_CLIENTS);
        final int waitingQueueSize = settings.getInteger(CmdParser.MAX_WAITING_QUEUE, DEFAULT_WAITING_QUEUE_SIZE);

        this.clientDispatcher = new ClientDispatcher(waitingQueueSize);
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

    /**
     * Spustí naslouchání serveru
     */
    private void startListening() {
        LOGGER.info("Inicializuji klientská vlákna.");
        pool = Executors.newFixedThreadPool(maxClients);
        running = true;
        super.start();
    }

    public void shutdown() {
        running = false;
    }

    private synchronized void insertClientToListOrQueue(Client client) {
        if (clients.size() < maxClients) {
            LOGGER.info("Přidávám klienta do kolekce klientů a spouštím komunikaci.");
            clients.add(client);
            client.setConnectionClosedListener(() -> {
                LOGGER.info("Odebírám klienta ze seznamu na serveru.");
                clients.remove(client);
                LOGGER.info("Počet připojených klientů: " + clients.size());
                if (clientDispatcher.hasClientInQueue()) {
                    LOGGER.info("V čekací listině se našel klient, který by rád komunikoval.");
                    this.insertClientToListOrQueue(clientDispatcher.getClientFromQueue());
                }
            });
            pool.submit(client);
        } else {
            if (clientDispatcher.addClientToQueue(client)) {
                LOGGER.info("Přidávám klienta na čekací listinu.");
            } else {
                LOGGER.warn("Odpojuji klienta od serveru. Je připojeno příliš mnoho uživatelů.");
                client.close();
            }
        }
    }

    @Override
    public void run() {
        clientDispatcher.start();
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            serverSocket.setSoTimeout(5000);
            LOGGER.info(String.format("Server naslouchá na portu: %d.", serverSocket.getLocalPort()));
            // Nové vlákno serveru
            while (running) {
                try {
                    final Socket socket = serverSocket.accept();
                    LOGGER.info("Server přijal nové spojení.");

                    final Client client = new Client(socket);
                    this.insertClientToListOrQueue(client);
                } catch (SocketTimeoutException e) {}
            }

            LOGGER.info("Ukončuji server.");
            LOGGER.info("Odpojuji připojené klienty.");
            for (Client client : clients) {
                client.disconnect();
            }
            LOGGER.info("Ukončuji činnost thread poolu.");
            pool.shutdown();
            LOGGER.info("Ukončuji client dispatcher.");
            clientDispatcher.shutdown();
            try {
                clientDispatcher.join();
            } catch (InterruptedException e) {}
            LOGGER.info("Naslouchací vlákno bylo úspěšně ukončeno.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
