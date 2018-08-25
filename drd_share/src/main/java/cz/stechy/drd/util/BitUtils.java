package cz.stechy.drd.util;

/**
 * Pomocná knihovní třída pro nastavování bitů
 */
public final class BitUtils {

    // region Constants

    public static final byte[] BUFFER_64 = new byte[64];

    // endregion

    // region Constructors

    /**
     * Privátní konstruktor k zamezení vytvoření instance
     */
    private BitUtils() {
    }

    // endregion

    // region Public static methods

    public static int setBit(int original, int flag, boolean value) {
        if (value) {
            original |= flag;
        } else {
            original &= ~flag;
        }

        return original;
    }

    public static boolean isBitSet(int original, int value) {
        return (original & value) == 1;
    }

    public static int clearBit(int original, int index) {
        return setBit(original, index, false);
    }

    /**
     * Provede operaci XOR nad poli.
     * Kontrolují se délky polí.
     * Výsledkem bude pole o velikosti většího z parametrů.
     *
     * @param left Levé pole
     * @param right Pravé pole
     * @return Pole po operaci XOR
     */
    public static byte[] xor(byte[] left, byte[] right) {
        final int leftLength = left.length;
        final int rightLength = right.length;
        final int minLength = Math.min(leftLength, rightLength);
        final int maxLength = Math.max(leftLength, rightLength);
        final byte[] longer = leftLength > rightLength ? left : right;
        final byte[] result = new byte[maxLength];

        for (int i = 0; i < minLength; i++) {
            result[i] = (byte) (left[i] ^ right[i]);
        }

        if (maxLength - minLength >= 0) {
            System.arraycopy(longer, minLength, result, minLength, maxLength - minLength);
        }

        return result;
    }

    // endregion
}
