package cz.stechy.drd.core.connection;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import cz.stechy.drd.core.dispatcher.IClientDispatcher;
import cz.stechy.drd.core.event.IEventBus;
import cz.stechy.drd.core.writer.IWriterThread;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("all")
public class ConnectionManager implements IConnectionManager {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);

    // Kolekce klientů, se kterými server aktivně komunikuje
    private final List<Client> clients = new ArrayList<>();

    // Threadpool s vlákny pro jednotlivé klienty
    private final ExecutorService pool;
    // Vlákno, které se stará o odeslání dat zpět k uživateli
    private final IWriterThread writerThread;
    // Client dispatcher
    private final IClientDispatcher clientDispatcher;
    // Třída starající se o zpracování přijatých zpráv
    private final IEventBus eventProcessor;
    // Maximální počet aktívně komunikujících klientů
    final int maxClients;

    @Inject
    public ConnectionManager(IWriterThread writerThread, IClientDispatcher clientDispatcher,
        IEventBus eventProcessor, @Assisted ExecutorService pool, @Assisted int maxClients) {
        this.writerThread = writerThread;
        this.clientDispatcher = clientDispatcher;
        this.eventProcessor = eventProcessor;
        this.pool = pool;
        this.maxClients = maxClients;
    }

    private synchronized void insertClientToListOrQueue(Client client) {
        if (clients.size() < maxClients) {
            clients.add(client);
            client.setConnectionClosedListener(() -> {
                clients.remove(client);
                eventProcessor.publishEvent(new ClientDisconnectedEvent(client));
                LOGGER.info("Počet připojených klientů: {}.", clients.size());
                if (clientDispatcher.hasClientInQueue()) {
                    LOGGER.info("V čekací listině se našel klient, který by rád komunikoval.");
                    this.insertClientToListOrQueue(clientDispatcher.getClientFromQueue());
                }
            });
            pool.submit(client);
            eventProcessor.publishEvent(new ClientConnectedEvent(client));
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
    public int getConnectedClientCount() {
        return clients.size();
    }

    @Override
    public void addClient(Socket socket) throws IOException {
        insertClientToListOrQueue(new Client(socket, writerThread, eventProcessor));
    }

    @Override
    public void onServerStart() {
        clientDispatcher.start();
        writerThread.start();
    }

    @Override
    public void onServerStop() {
        LOGGER.info("Odpojuji připojené klienty.");
        for (Client client : clients) {
            client.close();
        }
        LOGGER.info("Ukončuji činnost thread poolu.");
        pool.shutdown();

        LOGGER.info("Ukončuji client dispatcher.");
        clientDispatcher.shutdown();

        LOGGER.info("Ukončuji writer thread.");
        writerThread.shutdown();
    }
}
