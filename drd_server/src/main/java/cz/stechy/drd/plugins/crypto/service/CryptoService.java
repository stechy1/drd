package cz.stechy.drd.plugins.crypto.service;

import cz.stechy.drd.core.connection.IClient;
import cz.stechy.drd.crypto.ICypher;
import cz.stechy.drd.crypto.RSA;
import cz.stechy.drd.crypto.RSA.CypherKey;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CryptoService implements ICryptoService {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(CryptoService.class);

    private static final int RSA_BIT_LENGTH = 256;

    // endregion

    // region Variables

    private final RSA rsa = new RSA(RSA_BIT_LENGTH);
    private final Map<IClient, RSA> cyphers = new HashMap<>();

    // endregion

    @Override
    public void addPublicKey(IClient client, CypherKey key) {
        cyphers.putIfAbsent(client, new RSA(key));
    }

    @Override
    public void removeKey(IClient client) {
        cyphers.remove(client);
    }

    @Override
    public byte[] encrypt(IClient client, byte[] src) {
        final ICypher cypher = cyphers.get(client);
        if (cypher == null) {
            // Nikdy by nemělo nastat
            throw new IllegalArgumentException("Klient nezaregistroval svůj veřejný klíč.");
        }

        return cypher.encrypt(src);
    }

    @Override
    public byte[] decrypt(byte[] src) {
        return rsa.decrypt(src);
    }

    @Override
    public CypherKey getServerPublicKey() {
        return rsa.getPublicKey();
    }
}
