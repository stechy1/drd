package cz.stechy.drd.util;

/**
 * Pomocná knihovní třída pro nastavování bitů
 */
public final class BitUtils {

    // region Constructors

    /**
     * Privátní konstruktor k zamezení vytvoření instance
     */
    private BitUtils() { }

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

    // endregion
}
