package cz.stechy.drd.model;

/**
 * Třída představující kostku k určování náhodnosti
 */
public final class Dice {

    // region Constants

    // Šesti-stěnná kostka
    public static final Dice K6 = new Dice(6);
    // Deseti-stěnná kostka
    public static final Dice K10 = new Dice(10);
    // Dvaceti-stěnná kostka
    public static final Dice K20 = new Dice(20);
    // Pravděpodobnostní kostka
    public static final Dice K100 = new Dice(0, 100);

    // endregion

    // region Variables

    // Minimální číslo, které může na kostce padnout
    private final int minimum;
    // Maximální číslo, které může na kostce padnout
    private final int maximum;

    // endregion

    // region Constructors

    /**
     * Vytvoři novou standartní kostku s minimální hodnotou 0 a maximální hodnopou podle parametru
     *
     * @param maximum Maximální číslo, které může na kostce padnout
     */
    public Dice(int maximum) {
        this(1, maximum);
    }

    /**
     * Vytvoří novou kostu s nastavitelnou minimální a maximální hodnotou
     *
     * @param minimum Minimální číslo, které může na kostce padnout
     * @param maximum Maximální číslo, které může na kostce padnout
     */
    public Dice(int minimum, int maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    // endregion

    // region Public methods

    /**
     * Hodí kostkou
     *
     * @return Číslo, které padlo na kostce
     */
    public int roll() {
        return minimum + (int) (Math.random() * ((maximum - minimum) + 1));
    }

    // endregion
}
