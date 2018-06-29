package cz.stechy.drd.crypto;

import cz.stechy.drd.Client;
import cz.stechy.drd.crypto.RSA.CypherKey;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CryptoService {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(CryptoService.class);

    private static final int RSA_BIT_LENGTH = 256;

    // endregion

    // region Variables

    private final RSA rsa = new RSA(RSA_BIT_LENGTH);
    private final Map<Client, ICypher> cyphers = new HashMap<>();

    // endregion

    // region Public methods

    /**
     * Přiřadí nový veřejný klíč klientovi
     *
     * @param client {@link Client}
     * @param key {@link CypherKey}
     */
    public void addPublicKey(Client client, CypherKey key) {
        cyphers.putIfAbsent(client, new RSA(key));
    }

    /**
     * Odstraní záznam o veřejném klíči pro zadaného klienta
     *
     * @param client {@link Client}
     */
    public void removeKey(Client client) {
        cyphers.remove(client);
    }

    /**
     * Zašifruje data veřejným klíčem klienta
     *
     * @param client {@link Client} Klient, kterému se data posílají
     * @param src {@link Byte} Data, který se mají zašifrovat
     * @return Zašifrovaná data
     */
    public byte[] encrypt(Client client, byte[] src) {
        final ICypher cypher = cyphers.get(client);
        if (cypher == null) {
            // Nikdy by nemělo nastat
            throw new IllegalArgumentException("Klient nezaregistroval svůj veřejný klíč.");
        }

        return cypher.encrypt(src);
    }

    /**
     * Dešifruje příchozí komunikaci privátním klíčem serveru
     *
     * @param src Zašifrovaná data
     * @return Dešifrovaná data
     */
    public byte[] decrypt(byte[] src) {
        return rsa.decrypt(src);
    }

    // region Getters & Setters

    public CypherKey getServerPublicKey() {
        return rsa.getPublicKey();
    }

    // endregion

}
