package cz.stechy.drd.model.entity.hero.profession;

import cz.stechy.drd.model.entity.hero.Hero;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Třída obsahující dovednosti, která dokáže alchymista na 1. až 5. úrovní
 */
public class Alchemist {

    // region Constants

    private static final int MINIMUM_LEVEL = 1;
    private static final int MAXIMUM_LEVEL = 5;

    private static final int[][] BASIC_MAGENERGY = {
        {7, 7, 7, 7, 8, 8, 8, 8, 8, 9, 9, 9, 9}, // 1. úroveň
        {15, 15, 16, 16, 17, 17, 18, 19, 19, 20, 20, 21, 21}, // 2. úroveň
        {31, 31, 35, 35, 38, 38, 40, 42, 42, 45, 45, 49, 49}, // 3. úroveň
        {62, 62, 70, 70, 76, 76, 80, 84, 84, 90, 90, 98, 98}, // 4. úroveň
        {126, 126, 131, 131, 142, 142, 150, 158, 158, 169, 169, 184, 184} // 5. úroveň
    };

    private static final int[] PROBABILITY_OF_FIND_MAGENERGY = {
        2, 3, 3, 3, 4, 4, 4, 5, 5, 6, 7, 8, 9, 11, 13, 15, 18, 21, 25, 29, 33, 38, 43, 49
    };

    private static final int[] PROBABILITY_OF_SUCCESS = {
        // Od stupně pravděpodobnosti 8 do 20
        32, 32, 47, 47, 47, 61, 61, 74, 74, 85, 85, 95, 95
    };

    private static final int PROBABILITY_OF_SUCCESS_MIN_VALUE = 8;
    private static final int PROBABILITY_OF_SUCCESS_MAX_VALUE = 20;

    // endregion

    // region Variables

    private final IntegerProperty maxMag = new SimpleIntegerProperty(this, "maxMag");
    private final IntegerProperty probabilityOfSuccess = new SimpleIntegerProperty(this,
        "probabilityOfSuccess");

    protected final Hero hero;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou instanci povolání se zaměřením na alchymistu
     *
     * @param hero {@link Hero}
     */
    public Alchemist(Hero hero) {
        this.hero = hero;

        probabilityOfSuccess.bind(Bindings.createIntegerBinding(() -> {
            int dexterity = hero.getDexterity().getValue();
            dexterity = Math.max(PROBABILITY_OF_SUCCESS_MIN_VALUE,
                Math.min(PROBABILITY_OF_SUCCESS_MAX_VALUE, dexterity));
            dexterity -= 8;
            return PROBABILITY_OF_SUCCESS[dexterity];
        }, hero.getDexterity().valueProperty()));

        maxMag.bind(Bindings.createIntegerBinding(() -> {
            int level = hero.getLevel();
            int dexterity = hero.getDexterity().getValue();
            // Normalizace úrovně a obratnosti, abych nevylezl mimo hranice pole
            level = Math.max(MINIMUM_LEVEL, Math.min(level, MAXIMUM_LEVEL));
            dexterity = Math.max(PROBABILITY_OF_SUCCESS_MIN_VALUE,
                Math.min(PROBABILITY_OF_SUCCESS_MAX_VALUE, dexterity));
            level -= MINIMUM_LEVEL;
            dexterity -= PROBABILITY_OF_SUCCESS_MIN_VALUE;

            return BASIC_MAGENERGY[level][dexterity];
        }, hero.levelProperty(), hero.getDexterity().valueProperty()));

        hero.getMag().maxValueProperty().bind(maxMag);
    }

    // endregion

    // region Pulic static methods

    /**
     * Najde správný index na základě zadaného množství magenergie
     *
     * @param count Množství magenergie
     * @return Index do tabulky pravděpodobnosti nalezení magenergie
     */
    public static int findIndexForProbabilityOfMagenergy(int count) {
        if (count <= 100) {
            return count / 10;
        }

        if (count <= 1000) {
            return 9 + count / 100;
        }

        if (count <= 10000) {
            return 19 + count / 1000;
        }

        return 0;
    }

    // endregion

    // region Public methods

    /**
     * Spočítá pravděpodobnost objevení zadaného počtu magenergie
     *
     * @param count Množství magenergie, které se zkoumá
     * @return Pravděpodobnost, že alchymista objeví zadaný počet magenergie
     */
    public int findMagEnergy(int count) {
        final int index = Alchemist.findIndexForProbabilityOfMagenergy(count);

        return PROBABILITY_OF_FIND_MAGENERGY[hero.getLevel() - 1 + index];
    }

    // endregion

    // region Getters & Setters

    /**
     * Získá z tabulky maximální počet magů pro zadanou úroveň Počet se získá z úrovně a stupně
     * obratnosti
     *
     * @return Maximální množství magů pro aktuální úroveň
     */
    public int getMaxMag() {
        return maxMag.get();
    }

    public ReadOnlyIntegerProperty maxMagProperty() {
        return maxMag;
    }

    /**
     * Vrátí pravděpodobnost úspěchu alchymisty
     *
     * @return Pravděpodobnost úspěchu
     */
    public int getProbabilityOfSuccess() {
        return probabilityOfSuccess.get();
    }

    /**
     * Vlastnost pravděpodobnost úspěchu
     *
     * @return Pravděpodobnost úspěchu
     */
    public ReadOnlyIntegerProperty probabilityOfSuccessProperty() {
        return probabilityOfSuccess;
    }

    // endregion

}
