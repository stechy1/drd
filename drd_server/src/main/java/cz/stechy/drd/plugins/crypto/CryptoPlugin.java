package cz.stechy.drd.plugins.crypto;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.stechy.drd.core.connection.ClientDisconnectedEvent;
import cz.stechy.drd.core.connection.IClient;
import cz.stechy.drd.core.connection.MessageReceivedEvent;
import cz.stechy.drd.core.event.IEvent;
import cz.stechy.drd.core.event.IEventBus;
import cz.stechy.drd.crypto.RSA.CypherKey;
import cz.stechy.drd.net.message.CryptoMessage;
import cz.stechy.drd.net.message.MessageSource;
import cz.stechy.drd.plugins.IPlugin;
import cz.stechy.drd.plugins.crypto.service.ICryptoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class CryptoPlugin implements IPlugin {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoPlugin.class);

    public static final String PLUGIN_NAME = "crypto";

    // endregion

    // region Variables

    private final ICryptoService cryptoService;

    // endregion

    // region Constructors

    @Inject
    public CryptoPlugin(ICryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    // endregion

    // region Private methods

    private void cryptoMessageHandler(IEvent event) {
        assert event instanceof MessageReceivedEvent;
        final MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent) event;
        final CryptoMessage cryptoMessage = (CryptoMessage) messageReceivedEvent.getReceivedMessage();
        final IClient client = messageReceivedEvent.getClient();
        final CypherKey clientKey = (CypherKey) cryptoMessage.getData();

        LOGGER.info("Ukládám veřejný klíč klienta.");
        cryptoService.addPublicKey(client, clientKey);

        client.sendMessageAsync(new CryptoMessage(MessageSource.SERVER, cryptoService.getServerPublicKey()));
    }

    private void clientDisconnectHandler(IEvent event) {
        assert event instanceof ClientDisconnectedEvent;
        final ClientDisconnectedEvent clientDisconnectedEvent = (ClientDisconnectedEvent) event;

        cryptoService.removeKey(clientDisconnectedEvent.getClient());
    }

    // endregion

    @Override
    public String getName() {
        return PLUGIN_NAME;
    }

    @Override
    public void init() { }

    @Override
    public void registerMessageHandlers(IEventBus eventBus) {
        eventBus.registerEventHandler(CryptoMessage.MESSAGE_TYPE, this::cryptoMessageHandler);
        eventBus.registerEventHandler(ClientDisconnectedEvent.EVENT_NAME, this::clientDisconnectHandler);
    }
}
