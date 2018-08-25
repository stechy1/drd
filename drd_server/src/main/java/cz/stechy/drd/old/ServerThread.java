package cz.stechy.drd.old;

import cz.stechy.drd.old.module.IModule;
import cz.stechy.drd.net.message.ClientStatusMessage;
import cz.stechy.drd.net.message.ClientStatusMessage.ClientStatus;
import cz.stechy.drd.net.message.ClientStatusMessage.ClientStatusData;
import cz.stechy.drd.net.message.IMessage;
import cz.stechy.drd.net.message.MessageType;
import cz.stechy.drd.net.message.ServerStatusMessage;
import cz.stechy.drd.net.message.ServerStatusMessage.ServerStatus;
import cz.stechy.drd.net.message.ServerStatusMessage.ServerStatusData;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerThread extends Thread implements ServerInfoProvider, IModuleRegistry {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerThread.class);

    // endregion

    // region Variables

    private final List<Client> clients = new ArrayList<>();
    private final Map<MessageType, List<IModule>> modules = new HashMap<>();
    private final ClientDispatcher clientDispatcher;
    private final WriterThread writerThread;
    private final MulticastSender multicastSender;
    private final int port;
    private final int maxClients;
    private final ExecutorService pool;
    private boolean running = false;

    // endregion

    // region Constructors

    public ServerThread(int port, int maxClients, int waitingQueueSize) throws IOException {
        super("ServerThread");
        this.port = port;
        this.maxClients = maxClients;
        this.clientDispatcher = new ClientDispatcher(waitingQueueSize, this);
        this.writerThread = new WriterThread();
        this.multicastSender = new MulticastSender(this);
        pool = Executors.newFixedThreadPool(maxClients);
    }

    // endregion

    // region Private methods

    /**
     * Inicializuje všechny moduly
     */
    private void initModules() {
        modules.values().forEach(modules -> modules.forEach(IModule::init));
    }

    /**
     * Pošle zprávu všem připojeným klientům
     *
     * @param message {@link IMessage} Zpráva
     */
    private void broadcast(final IMessage message) {
        this.broadcast(message, client -> true);
    }

    /**
     * Pošle zprávu všem připojeným klientům, kteří splňují kritéria filteru
     *
     * @param message {@link IMessage} Zpráva
     * @param filter {@link Predicate} Filter klientů
     */
    private void broadcast(final IMessage message, Predicate<? super Client> filter) {
        clients.stream().filter(filter).forEach(client -> client.sendMessage(message));
    }

    /**
     * Vloží klienta mezi připojené a aktivně komunikující klienty, nebo ho zařadí na čekací listinu
     *
     * @param client {@link Client} Klient, který se chce připojit
     */
    private synchronized void insertClientToListOrQueue(Client client) {
        if (clients.size() < maxClients) {
            LOGGER.info("Přidávám klienta do kolekce klientů a spouštím komunikaci.");
            clients.add(client);
            client.setConnectionClosedListener(() -> {
                LOGGER.info("Odebírám klienta ze seznamu na serveru.");
                clients.remove(client);
                modules.values().forEach(modules -> modules.forEach(module -> module.onClientDisconnect(client)));
                LOGGER.info("Počet připojených klientů: {}.", clients.size());
                if (clientDispatcher.hasClientInQueue()) {
                    LOGGER.info("V čekací listině se našel klient, který by rád komunikoval.");
                    this.insertClientToListOrQueue(clientDispatcher.getClientFromQueue());
                } else {
                    broadcast(getServerStatusMessage());
                }
            });
            pool.submit(client);
            broadcast(getServerStatusMessage());
        } else {
            if (clientDispatcher.addClientToQueue(client)) {
                LOGGER.info("Přidávám klienta na čekací listinu.");
            } else {
                LOGGER.warn("Odpojuji klienta od serveru. Je připojeno příliš mnoho uživatelů.");
                client.close();
            }
        }
    }

    /**
     * Zavolá se vždy, když přijde zpráva
     * !!! zpracovává se ve vlákně klienta !!!
     *
     * @param message {@link IMessage} Přijatá zpráva
     * @param client {@link Client} Klient, který přijal zprávu
     */
    private synchronized void messageReceiveListener(IMessage message, Client client) {
        switch (message.getType()) {
            case HELLO:
                IMessage clientStatusMessage = new ClientStatusMessage(
                    new ClientStatusData(client.getId(), ClientStatus.CONNECTED));
                broadcast(clientStatusMessage, client1 -> !client1.getId().equals(client.getId()));
                break;
            default:
                final List<IModule> messageHandlers = modules.get(message.getType());
                if (messageHandlers == null) {
                    return;
                }
                for (IModule handler : messageHandlers) {
                    handler.handleMessage(message, client);
                }
//            case DATABASE:
//                processDatabaseMessage((DatabaseMessage) message, client);
//                break;
//            case AUTH:
//                processAuthMessage((AuthMessage) message, client);
//                break;
        }
    }

    // endregion

    public void shutdown() {
        running = false;
    }

    @Override
    public ServerStatusMessage getServerStatusMessage() {
        final int connectedClients = clients.size();
        final int delta = maxClients - connectedClients;
        ServerStatus status = ServerStatus.EMPTY;
        if (delta == 0) {
            status = ServerStatus.FULL;
        } else if (delta > 0 && delta < maxClients) {
            status = ServerStatus.HAVE_SPACE;
        }

        String serverName = "Default DrD server";
        return new ServerStatusMessage(new ServerStatusData(
            Server.ID, status, connectedClients, maxClients, serverName, port));
    }

    @Override
    public void registerModule(MessageType messageType, IModule module) {
        List<IModule> messageHandlers = modules
            .computeIfAbsent(messageType, k -> new ArrayList<>());

        messageHandlers.add(module);
    }

    @Override
    public synchronized void start() {
        clientDispatcher.start();
        writerThread.start();
        multicastSender.start();
        running = true;
        super.start();
    }

    @Override
    public void run() {
        initModules();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(5000);
            LOGGER.info(String.format("Server naslouchá na portu: %d.", serverSocket.getLocalPort()));
            // Nové vlákno serveru
            while (running) {
                try {
                    final Socket socket = serverSocket.accept();
                    LOGGER.info("Server přijal nové spojení.");

                    final Client client = new Client(socket, writerThread, this::messageReceiveListener);
                    this.insertClientToListOrQueue(client);
                } catch (SocketTimeoutException ignored) {}
            }

            LOGGER.info("Ukončuji server.");
            LOGGER.info("Odpojuji připojené klienty.");
            for (Client client : clients) {
                client.close();
            }
            LOGGER.info("Ukončuji činnost thread poolu.");
            pool.shutdown();
            LOGGER.info("Ukončuji multicast sender thread.");
            multicastSender.shutdown();
            try {
                multicastSender.join();
            } catch (InterruptedException ignored) {}
            LOGGER.info("Ukončuji client dispatcher.");
            clientDispatcher.shutdown();
            try {
                clientDispatcher.join();
            } catch (InterruptedException ignored) {}
            LOGGER.info("Ukončuji writer thread.");
            writerThread.shutdown();
            try {
                writerThread.join();
            } catch (InterruptedException ignored) {}
            LOGGER.info("Naslouchací vlákno bylo úspěšně ukončeno.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
