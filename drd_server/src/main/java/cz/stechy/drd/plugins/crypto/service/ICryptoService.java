package cz.stechy.drd.plugins.crypto.service;

import com.google.inject.ImplementedBy;
import cz.stechy.drd.core.connection.IClient;
import cz.stechy.drd.crypto.RSA.CypherKey;

@ImplementedBy(CryptoService.class)
public interface ICryptoService {

    /**
     * Přiřadí nový veřejný klíč klientovi
     *
     * @param client {@link IClient}
     * @param key {@link CypherKey}
     */
    void addPublicKey(IClient client, CypherKey key);

    /**
     * Odstraní záznam o veřejném klíči pro zadaného klienta
     *
     * @param client {@link IClient}
     */
    void removeKey(IClient client);

    /**
     * Zašifruje data veřejným klíčem klienta
     *
     * @param client {@link IClient} Klient, kterému se data posílají
     * @param src {@link Byte} Data, který se mají zašifrovat
     * @return Zašifrovaná data
     */
    byte[] encrypt(IClient client, byte[] src);

    /**
     * Dešifruje příchozí komunikaci privátním klíčem serveru
     *
     * @param src Zašifrovaná data
     * @return Dešifrovaná data
     */
    byte[] decrypt(byte[] src);

    /**
     * Vrátí veřejný klíč serveru
     *
     * @return {@link CypherKey}
     */
    CypherKey getServerPublicKey();

    /**
     * Vrátí veřejný klíč klienta
     *
     * @param client {@link IClient} Klient
     * @return {@link CypherKey} Veřejný klíč klienta
     */
    CypherKey getClientPublicKey(IClient client);
}
