package cz.stechy.drd.core.dispatcher;

import com.google.inject.Singleton;

@Singleton
public class ClientDispatcherFactory implements IClientDispatcherFactory {

    @Override
    public IClientDispatcher getClientDispatcher(int waitingQueueSize) {
        return new ClientDispatcher(waitingQueueSize);
    }
}
