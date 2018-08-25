package cz.stechy.drd.old.module;

import cz.stechy.drd.old.Client;
import cz.stechy.drd.crypto.CryptoService;
import cz.stechy.drd.crypto.RSA.CypherKey;
import cz.stechy.drd.net.message.CryptoMessage;
import cz.stechy.drd.net.message.IMessage;
import cz.stechy.drd.net.message.MessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoModule implements IModule {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoModule.class);

    // endregion

    // region Variables

    private final CryptoService cryptoService;

    // endregion

    // region Constructors

    public CryptoModule(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    // endregion

    @Override
    public void handleMessage(IMessage message, Client client) {
        final CryptoMessage cryptoMessage = (CryptoMessage) message;
        final CypherKey clientKey = (CypherKey) cryptoMessage.getData();
        cryptoService.addPublicKey(client, clientKey);
        LOGGER.info("Ukládám veřejný klíč klienta.");

        client.sendMessage(new CryptoMessage(MessageSource.SERVER, cryptoService.getServerPublicKey()));
    }

    @Override
    public void onClientDisconnect(Client client) {
        cryptoService.removeKey(client);
        LOGGER.info("Odstraňuji veřejný klíč klienta.");
    }
}
