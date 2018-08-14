package cz.stechy.drd.service;

import cz.stechy.drd.crypto.ICypher;
import cz.stechy.drd.crypto.RSA;
import cz.stechy.drd.crypto.RSA.CypherKey;
import cz.stechy.drd.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class CryptoService implements ICypher {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoService.class);

    private static final int RSA_KEY_LENGTH = 256;

    // endregion

    // region Variables

    private final RSA rsa = new RSA(RSA_KEY_LENGTH);

    private RSA serverCypher;

    // endregion

    // region Public methods

    @Override
    public byte[] encrypt(byte[] src) {
        return serverCypher.encrypt(src);
    }

    @Override
    public byte[] decrypt(byte[] src) {
        return rsa.decrypt(src);
    }

    public ICypher makeCypher(CypherKey cypherKey) {
        return new RSA(cypherKey);
    }

    // endregion

    // region Getters & Setters

    public CypherKey getClientPublicKey() {
        return rsa.getPublicKey();
    }

    public void setServerPublicCey(CypherKey key) {
        this.serverCypher = new RSA(key);
    }

    // endregion

}
