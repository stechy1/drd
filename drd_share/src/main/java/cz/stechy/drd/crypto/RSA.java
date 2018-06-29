package cz.stechy.drd.crypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

/**
 * Implementace šifrovacího algoritmu RSA
 */
public class RSA implements ICypher {

    // region Variables

    private final BigInteger n;
    private final BigInteger e;
    private final BigInteger d;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou instanci RSA
     * Vygeneruje privátní i veřejný klíč
     * Tato instance umí dešifrovat zprávy
     *
     * @param bitLength Délka klíče
     */
    public RSA(int bitLength) {
        final Random r = new Random();
        final BigInteger p = BigInteger.probablePrime(bitLength, r);
        final BigInteger q = BigInteger.probablePrime(bitLength, r);
        n = p.multiply(q);
        final BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = BigInteger.probablePrime(bitLength / 2, r);
        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0) {
            e.add(BigInteger.ONE);
        }
        d = e.modInverse(phi);
    }

    /**
     * Vytvoří novou instanci na základě veřejného klíče
     * Privátní klíč nebude existovat -> není možné přijaté zprávy dešifrovat
     * Použijte pouze pro šifrování
     *
     * @param cypherKey {@link CypherKey}
     */
    public RSA(CypherKey cypherKey) {
        n = cypherKey.val1;
        e = cypherKey.val2;
        d = null;
    }

    // endregion

    // region Private methods

    /**
     * Šifrovací algoritmus
     *
     * @param message Zpráva, která se má zpracovat
     * @param e
     * @return
     */
    private byte[] crypt(byte[] message, BigInteger e) {
        final int bitLength = n.bitLength();
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(message.length);
        final int iteration = (int) Math.round(Math.ceil(message.length / (double) bitLength));

        int offset = 0;
        int remaining = message.length;

        for (int i = 0; i < iteration; i++) {
            final int count = (remaining > bitLength) ? bitLength : remaining;
            final byte[] data = new byte[count];
            System.arraycopy(message, offset, data, 0, count);
            try {
                outputStream.write(new BigInteger(data).modPow(e, n).toByteArray());
            } catch (IOException ex) {
                System.out.println("Data se nepodařilo zašifrovat");
            }

            offset += count;
            remaining -= count;
        }

        return outputStream.toByteArray();
    }

    // endregion

    @Override
    public byte[] encrypt(byte[] message) {
        return crypt(message, e);
    }

    @Override
    public byte[] decrypt(byte[] message) {
        return crypt(message, d);
    }

    // region Getters & Setters

    /**
     * Vrátí veřejný klíč
     *
     * @return {@link CypherKey} Veřejný klíč aktuální instance šifry
     */
    public final CypherKey getPublicKey() {
        return new CypherKey(n, e);
    }

    /**
     * Vrátí privátní klíř
     *
     * @return {@link CypherKey} Privátní klíč aktuální instance šifry
     */
    public final CypherKey getPrivateKey() {
        return new CypherKey(n, d);
    }

    // endregion

    public static class CypherKey implements Serializable {

        private static final long serialVersionUID = -8201912914817438690L;

        final BigInteger val1;
        final BigInteger val2;

        CypherKey(BigInteger val1, BigInteger val2) {
            this.val1 = val1;
            this.val2 = val2;
        }
    }
}