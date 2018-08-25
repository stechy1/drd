package cz.stechy.drd.core.connection;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.stechy.drd.core.dispatcher.IClientDispatcherFactory;
import cz.stechy.drd.core.event.IEventBus;
import cz.stechy.drd.core.writer.IWriterThread;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class ConnectionManagerFactory implements IConnectionManagerFactory {

    private final IWriterThread writerThread;
    private final IClientDispatcherFactory clientDispatcherFactory;
    private final IEventBus eventProcessor;

    @Inject
    public ConnectionManagerFactory(IWriterThread writerThread,
        IClientDispatcherFactory clientDispatcherFactory,
        IEventBus eventProcessor) {
        this.writerThread = writerThread;
        this.clientDispatcherFactory = clientDispatcherFactory;
        this.eventProcessor = eventProcessor;
    }

    @Override
    public IConnectionManager getConnectionManager(int maxClients, int waitingQueueSize) {
        final ExecutorService pool = Executors.newFixedThreadPool(maxClients);
        return new ConnectionManager(writerThread,
            clientDispatcherFactory.getClientDispatcher(waitingQueueSize),
            eventProcessor, pool, maxClients);
    }
}
