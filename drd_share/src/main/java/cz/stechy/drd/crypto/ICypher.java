package cz.stechy.drd.crypto;

/**
 * Rozhraní definující metody pro šifrovací algoritmy
 */
public interface ICypher {

    /**
     * Zašifruje zprávu
     *
     * @param src Zpráva, která se má zašifrovat
     * @return Zašifrovaná zpráva
     */
    byte[] encrypt(byte[] src);

    /**
     * Dešifruje zprávu
     *
     * @param src Zašifrovaná zpráva
     * @return Dešifrovaná zpráva
     */
    byte[] decrypt(byte[] src);

}
