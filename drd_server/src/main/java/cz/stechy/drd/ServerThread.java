package cz.stechy.drd;

import cz.stechy.drd.net.message.HelloMessage;
import cz.stechy.drd.net.message.MessageSource;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerThread extends Thread {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerThread.class);

    private final List<Client> clients = new ArrayList<>();
    private final ClientDispatcher clientDispatcher;
    private final WriterThread writerThread;
    private final int port;
    private final int maxClients;
    private final ExecutorService pool;
    private boolean running = false;

    public ServerThread(int port, int maxClients, int waitingQueueSize) {
        super("ServerThread");
        this.port = port;
        this.maxClients = maxClients;
        this.clientDispatcher = new ClientDispatcher(waitingQueueSize);
        this.writerThread = new WriterThread();
        pool = Executors.newFixedThreadPool(maxClients);
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
            client.sendMessage(new HelloMessage(MessageSource.SERVER));
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
    public synchronized void start() {
        clientDispatcher.start();
        writerThread.start();
        running = true;
        super.start();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(5000);
            LOGGER.info(String.format("Server naslouchá na portu: %d.", serverSocket.getLocalPort()));
            // Nové vlákno serveru
            while (running) {
                try {
                    final Socket socket = serverSocket.accept();
                    LOGGER.info("Server přijal nové spojení.");

                    final Client client = new Client(socket, writerThread);
                    this.insertClientToListOrQueue(client);
                } catch (SocketTimeoutException e) {}
            }

            LOGGER.info("Ukončuji server.");
            LOGGER.info("Odpojuji připojené klienty.");
            for (Client client : clients) {
                client.close();
            }
            LOGGER.info("Ukončuji činnost thread poolu.");
            pool.shutdown();
            LOGGER.info("Ukončuji client dispatcher.");
            clientDispatcher.shutdown();
            try {
                clientDispatcher.join();
            } catch (InterruptedException e) {}
            LOGGER.info("Ukončuji writer thread");
            writerThread.shutdown();
            try {
                writerThread.join();
            } catch (InterruptedException e) {}
            LOGGER.info("Naslouchací vlákno bylo úspěšně ukončeno.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        running = false;
    }
}
