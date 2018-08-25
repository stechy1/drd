package cz.stechy.drd.core.server;

import cz.stechy.drd.core.connection.IConnectionManager;
import cz.stechy.drd.core.multicaster.IMulticastSender;
import cz.stechy.drd.core.multicaster.IMulticastSenderFactory;
import cz.stechy.drd.net.message.ServerStatusMessage;
import cz.stechy.drd.net.message.ServerStatusMessage.ServerStatus;
import cz.stechy.drd.net.message.ServerStatusMessage.ServerStatusData;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vlákno serveru
 */
class ServerThread extends Thread implements IServerThread {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerThread.class);
    private static final UUID ID = UUID.randomUUID();

    // Číslo portu
    private final int port;

    // Správce spojení
    private final IConnectionManager connectionManager;
    private final IMulticastSender multicastSender;

    private final int maxClients;

    // Indikátor, zda-li vlákno běží, nebo ne
    private boolean running = false;

    /**
     * Vytvoří novou instanci vlákna serveru
     * @param port Číslo portu
     * @param connectionManager {@link IConnectionManager}
     * @param multicastSenderFactory {@link IMulticastSenderFactory}
     * @param maxClients Maximální počet připojených klientů
     */
    ServerThread(int port, IConnectionManager connectionManager,
        IMulticastSenderFactory multicastSenderFactory,
        int maxClients) {
        super("ServerThread");
        this.port = port;
        this.connectionManager = connectionManager;
        this.multicastSender = multicastSenderFactory.getMulticastSender(this);
        this.maxClients = maxClients;
    }

    @Override
    public ServerStatusMessage getServerStatusMessage() {
        final int connectedClients = connectionManager.getConnectedClientCount();
        final int delta = maxClients - connectedClients;
        ServerStatus status = ServerStatus.EMPTY;
        if (delta == 0) {
            status = ServerStatus.FULL;
        } else if (delta > 0 && delta < maxClients) {
            status = ServerStatus.HAVE_SPACE;
        }

        String serverName = "Default DrD server";
        return new ServerStatusMessage(new ServerStatusData(
            ID, status, connectedClients, maxClients, serverName, port));
    }

    @Override
    public void shutdown() {
        running = false;
        try {
            join();
        } catch (InterruptedException ignored) { }
    }

    @Override
    public synchronized void start() {
        running = true;
        super.start();
    }

    @Override
    public void run() {
        multicastSender.start();
        connectionManager.onServerStart();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // Každých 5 vteřin dojde k vyjímce SocketTimeoutException
            // To proto, že metoda serverSocket.accept() je blokující
            // a my bychom neměli šanci činnost vlákna ukončit
            serverSocket.setSoTimeout(5000);
            LOGGER
                .info(String.format("Server naslouchá na portu: %d.", serverSocket.getLocalPort()));
            // Nové vlákno serveru
            while (running) {
                try {
                    final Socket socket = serverSocket.accept();
                    LOGGER.info("Server přijal nové spojení.");

                    connectionManager.addClient(socket);
                } catch (SocketTimeoutException ignored) {
                }
            }

        } catch (IOException e) {
            LOGGER.error("Chyba v server socketu.", e);
        }

        LOGGER.info("Ukončuji server.");
        connectionManager.onServerStop();
        multicastSender.shutdown();
    }
}
