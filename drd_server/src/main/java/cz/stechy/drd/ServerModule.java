package cz.stechy.drd;

import com.google.inject.AbstractModule;
import cz.stechy.drd.cmd.IParameterFactory;
import cz.stechy.drd.cmd.ParameterFactory;
import cz.stechy.drd.core.connection.ConnectionManagerFactory;
import cz.stechy.drd.core.connection.IConnectionManagerFactory;
import cz.stechy.drd.core.dispatcher.ClientDispatcherFactory;
import cz.stechy.drd.core.dispatcher.IClientDispatcherFactory;
import cz.stechy.drd.core.event.EventBus;
import cz.stechy.drd.core.event.IEventBus;
import cz.stechy.drd.core.multicaster.IMulticastSenderFactory;
import cz.stechy.drd.core.multicaster.MulticastSenderFactory;
import cz.stechy.drd.core.server.IServerThreadFactory;
import cz.stechy.drd.core.server.ServerThreadFactory;
import cz.stechy.drd.core.writer.IWriterThread;
import cz.stechy.drd.core.writer.WriterThread;

class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IParameterFactory.class).to(ParameterFactory.class);
        bind(IServerThreadFactory.class).to(ServerThreadFactory.class);
        bind(IWriterThread.class).to(WriterThread.class);
        bind(IClientDispatcherFactory.class).to(ClientDispatcherFactory.class);
        bind(IEventBus.class).to(EventBus.class);
        bind(IConnectionManagerFactory.class).to(ConnectionManagerFactory.class);
        bind(IMulticastSenderFactory.class).to(MulticastSenderFactory.class);
    }
}
