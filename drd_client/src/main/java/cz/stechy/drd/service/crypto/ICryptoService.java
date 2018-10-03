package cz.stechy.drd.service.crypto;

import cz.stechy.drd.crypto.ICypher;
import cz.stechy.drd.crypto.RSA.CypherKey;

public interface ICryptoService extends ICypher {

    ICypher makeCypher(CypherKey cypherKey);

    CypherKey getClientPublicKey();

    void setServerPublicCey(CypherKey key);
}
