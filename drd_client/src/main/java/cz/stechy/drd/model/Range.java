package cz.stechy.drd.model;

/**
 * Třída představuje rozpětí na nějakém intervalu
 */
public final class Range {

    // region Variables

    public final int left;
    public final int right;

    // endregion

    // region Constructors

    /**
     * Vytvoří nový rozsah o zadaných rozměrech
     *
     * @param left Spodní mez rozsahu
     * @param right Horní mez rozsahu
     */
    public Range(int left, int right) {
        this.left = left;
        this.right = right;
    }

    // endregion
}
