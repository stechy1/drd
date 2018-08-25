package cz.stechy.drd.core.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.stechy.drd.cmd.CmdParser;
import cz.stechy.drd.cmd.IParameterProvider;
import cz.stechy.drd.core.connection.IConnectionManager;
import cz.stechy.drd.core.connection.IConnectionManagerFactory;
import cz.stechy.drd.core.multicaster.IMulticastSenderFactory;

/**
 * Továrna serverového vlákna
 */
@Singleton
public class ServerThreadFactory implements IServerThreadFactory {

    // Výchozí hodnota portu
    private static final int DEFAULT_SERVER_PORT = 15378;
    // Výchozí maximální počet klientů
    private static final int DEFAULT_MAX_CLIENTS = 1;
    // Výchozí velikost čekací fronty
    private static final int DEFAULT_WAITING_QUEUE_SIZE = 1;

    private final IConnectionManagerFactory connectionManagerFactory;
    private final IMulticastSenderFactory multicastSenderFactory;

    @Inject
    ServerThreadFactory(IConnectionManagerFactory connectionManagerFactory,
        IMulticastSenderFactory multicastSenderFactory) {
        this.connectionManagerFactory = connectionManagerFactory;
        this.multicastSenderFactory = multicastSenderFactory;
    }

    @Override
    public IServerThread getServerThread(IParameterProvider parameters) {
        final int port = parameters.getInteger(CmdParser.PORT, DEFAULT_SERVER_PORT);
        final int maxClients = parameters.getInteger(CmdParser.CLIENTS, DEFAULT_MAX_CLIENTS);
        final int waitingQueueSize = parameters.getInteger(CmdParser.MAX_WAITING_QUEUE, DEFAULT_WAITING_QUEUE_SIZE);

        final IConnectionManager connectionManager = connectionManagerFactory
            .getConnectionManager(maxClients, waitingQueueSize);

        return new ServerThread(port, connectionManager, multicastSenderFactory, maxClients);
    }
}
