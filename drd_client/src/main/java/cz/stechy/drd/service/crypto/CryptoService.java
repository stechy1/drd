package cz.stechy.drd.service.crypto;

import cz.stechy.drd.annotation.Service;
import cz.stechy.drd.crypto.ICypher;
import cz.stechy.drd.crypto.RSA;
import cz.stechy.drd.crypto.RSA.CypherKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CryptoService implements ICryptoService {

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

    @Override
    public ICypher makeCypher(CypherKey cypherKey) {
        return new RSA(cypherKey);
    }

    // endregion

    // region Getters & Setters

    @Override
    public CypherKey getClientPublicKey() {
        return rsa.getPublicKey();
    }

    @Override
    public void setServerPublicCey(CypherKey key) {
        this.serverCypher = new RSA(key);
    }

    // endregion

}
