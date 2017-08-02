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
    // Pravděpodobnostní kostka
    public static final Dice K100 = new Dice(100);

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

    /**
     * Vygeneruje hodnotu podle pravidel pro generování hodnot ze zadaného rozsahu
     *
     * @param min Dolní mez
     * @param max Horní mez
     * @return Hodnotu ze zadaného intervalu
     */
    public static int generateValue(int min, int max) {
        // Odečteme dolní mez od horní
        final int delta = max - min;
        Dice dice = K6;
        int rollCount = 1;
        boolean special = false;
        // Test, jestli budu házet 6-stěnnou kostkou
        if (delta % 5 == 0) {
            dice = K6;
            rollCount = (int) Math.floor(delta / 5.0);
         // Test, jestli budu házet 10-stěnnou kostkou
        } else if (delta % 9 == 0) {
            dice = K10;
            rollCount = (int) Math.floor(delta / 9.0);
        } else {
            special = true;
            if (max == 3) {
                dice = K6;
            } else if (max == 5) {
                dice = K10;
            }
        }

        // Určený počet hodů odečtu od dolní meze
        // tím získám číslo které musím přičíst na konci
        final int addon = min - rollCount;
        int result = 0;
        do {
            int roll = dice.roll();
            if (special) {
                roll = (int) Math.round(roll / 2.0);
            }
            result += roll;
            rollCount--;
        } while (rollCount > 0);
        result += addon;

        return result;
    }

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

    @Override
    public String toString() {
        return "K" + maximum;
    }
}
