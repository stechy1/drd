package cz.stechy.drd.util;

import java.security.SecureRandom;
import java.util.Date;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Pomocná knihovní třída pro generování hashů
 */
public final class HashGenerator {

    // region Constants

    private static final String SEPARATOR_CHAR = ":";

    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";

    private static final int SALT_BYTE_SIZE = 24;
    private static final int HASH_BYTE_SIZE = 18;
    private static final int PBKDF2_ITERATIONS = 64000;

    // endregion

    // region Constructors

    private HashGenerator() {
        throw new AssertionError();
    }

    // endregion

    // region Private static methods

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes) {
        try {
            final PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
            final SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (Exception ex) {
            return null;
        }
    }

    // endregion

    // region Public static methods

    /**
     * Vytvoří nový hash na základě aktuálního času
     *
     * @return Nový hash na základě aktuálního času
     */
    public static String createHash() {
        return createHash(new Date().toString());
    }

    /**
     * Vytvoří nový hash na základě hesla
     *
     * @param password Heslo hashe
     * @return Hash
     */
    public static String createHash(String password) {
        return createHash(password.toCharArray());
    }

    /**
     * Vytvoří hash na základě hesla ve znakové podobě
     *
     * @param password Heslo pro hash
     * @return Hash
     */
    public static String createHash(char[] password) {
        final SecureRandom random = new SecureRandom();
        final byte[] salt = new byte[SALT_BYTE_SIZE];
        random.nextBytes(salt);

        return createHash(password, salt);
    }

    /**
     * Vytvoří hash na základě hesla a soli
     *
     * @param password Heslo
     * @param salt Sůl
     * @return Hash
     */
    public static String createHash(char[] password, byte[] salt) {
        final byte[] hash = pbkdf2(password, salt, PBKDF2_ITERATIONS, HASH_BYTE_SIZE);

        return String
            .format("%s%s%s", Base64Util.encode(hash), SEPARATOR_CHAR, Base64Util.encode(salt));
    }

    /**
     * Zjistí, zda-li přiloené heslo generuje stejný hash
     *
     * @param hash Hash, proti kterému se kontroluje heslo
     * @param password Heslo které se kontroluje
     * @return True, pokud heslo generuje hash, jinak false
     */
    public static boolean checkSame(String hash, String password) {
        byte[] salt = Base64Util.decode(hash.substring(hash.lastIndexOf(SEPARATOR_CHAR) + 1));
        String testHash = createHash(password.toCharArray(), salt);
        return hash.equals(testHash);
    }

    // endregion

}
