package cz.stechy.drd.model.entity.hero.profession;

import cz.stechy.drd.model.entity.hero.Hero;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Třída obsahující dovednosti, která dokáže kouzelník na 1. až 5. úrovní
 */
public class Wizard {

    // region Constants

    private static final int MINIMUM_LEVEL = 1;
    private static final int MAXIMUM_LEVEL = 5;

    private static final int[][] BASIC_MAGENERGY = {
        {7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 9, 9, 9, 9}, // 1. úroveň
        {10, 10, 11, 11, 12, 12, 12, 12, 12, 12, 13, 13, 14, 14}, // 2. úroveň
        {12, 12, 14, 14, 15, 15, 16, 16, 17, 17, 18, 18, 20, 20}, // 3. úroveň
        {14, 14, 17, 17, 19, 19, 20, 20, 21, 21, 23, 23, 26, 26}, // 4. úroveň
        {17, 17, 20, 20, 22, 22, 24, 24, 26, 26, 28, 28, 31, 31}  // 5. úroveň
    };

    private static final int[] MAX_SPELL_COUNT = {
        3, 5, 7, 9, 11
    };

    private static final int[] PROBABILITY_OF_SUCCESS = {
        32, 32, 47, 47, 47, 61, 61, 74, 74, 85, 85, 95, 95, 99
    };

    private static final int PROBABILITY_OF_SUCCESS_MIN_VALUE = 8;
    private static final int PROBABILITY_OF_SUCCESS_MAX_VALUE = 21;

    // 13 značí, že odvrátit nelze
    private static final int[][] INVISIBLE_MOBS = {
        {11, 11, 13, 13, 13, 13}, // 1. úroveň
        {9, 11, 11, 13, 13, 13}, // 2. úroveň
        {9, 9, 11, 11, 13, 13}, //3. úroveň
        {6, 9, 9, 11, 11, 13}, // 4. úroveň
        {6, 6, 9, 9, 11, 11} // 5. úroveň
    };

    // endregion

    // region Variables

    private final IntegerProperty maxMag = new SimpleIntegerProperty(this, "maxMag");
    private final IntegerProperty probabilityOfSuccess = new SimpleIntegerProperty(this,
        "probabilityOfSuccess");
    private final IntegerProperty maxSpellCount = new SimpleIntegerProperty(this,
        "maxSpellCount");

    protected final Hero hero;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou instanci povolání se zaměřením na kouzelníka
     *
     * @param hero {@link Hero}
     */
    public Wizard(Hero hero) {
        this.hero = hero;

        probabilityOfSuccess.bind(Bindings.createIntegerBinding(() -> {
            int intelligence = hero.getIntelligence().getValue();
            intelligence = Math.max(PROBABILITY_OF_SUCCESS_MIN_VALUE,
                Math.min(PROBABILITY_OF_SUCCESS_MAX_VALUE, intelligence));
            intelligence -= 8;
            return PROBABILITY_OF_SUCCESS[intelligence];
        }, hero.getIntelligence().valueProperty()));
        maxMag.bind(Bindings.createIntegerBinding(() -> {
            int level = hero.getLevel();
            int intelligence = hero.getIntelligence().getValue();
            // Normalizace úrovně a inteligence, abych nevylezl mimo hranice pole
            level = Math.max(MINIMUM_LEVEL, Math.min(level, MAXIMUM_LEVEL));
            intelligence = Math.max(PROBABILITY_OF_SUCCESS_MIN_VALUE,
                Math.min(PROBABILITY_OF_SUCCESS_MAX_VALUE, intelligence));
            level -= MINIMUM_LEVEL;
            intelligence -= PROBABILITY_OF_SUCCESS_MIN_VALUE;

            return BASIC_MAGENERGY[level][intelligence];
        }, hero.levelProperty(), hero.getIntelligence().valueProperty()));
        maxSpellCount.bind(Bindings.createIntegerBinding(() -> {
            int level = hero.getLevel();
            // Normalizace úrovně, abych nevylezl mimo hranici pole
            level = Math.max(MINIMUM_LEVEL, Math.min(level, MAXIMUM_LEVEL));
            level -= MINIMUM_LEVEL;

            return MAX_SPELL_COUNT[level];
        }, hero.levelProperty()));

        maxMag.addListener((observable, oldValue, newValue) ->
            hero.getMag().setMaxValue(newValue));
    }

    // endregion

    // region Public methods

    /**
     * Vypočítá pravděpodobnost, že se podaří kouzelníkovi odvrátít neviděného protivníka
     *
     * @param typeOfInvisible Typ neviděného protivníka
     * @return Pravděpodobnost úspěchu
     */
    public int getProbabilityOfAvoidInvisibility(int typeOfInvisible) {
        int level = hero.getLevel();
        // Normalizace úrovně, abych nevylezl mimo hranici pole
        level = Math.max(MINIMUM_LEVEL, Math.min(level, MAXIMUM_LEVEL));
        level -= MINIMUM_LEVEL;
        final int index = Math.max(0, Math.min(typeOfInvisible, 6));

        return INVISIBLE_MOBS[level][index];
    }

    // endregion

    // region Getters & Setters

    /**
     * Získá z tabulky maximální počet magů pro zadanou úroveň.
     * Počet se získá z úrovně a stupně inteligence
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
     * Vrátí pravděpodobnost úspěchu kouzelníka
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

    /**
     * Vrátí maximální možný počet kouzel v závislosti na aktuální úrovni kouzelníka
     *
     * @return Maximální počet kouzel
     */
    public int getMaxSpellCount() {
        return maxSpellCount.get();
    }

    /**
     * Vlastnost maximální počet kouzel
     *
     * @return Maximální počet kouzel
     */
    public ReadOnlyIntegerProperty maxSpellCountProperty() {
        return maxSpellCount;
    }

    // endregion
}
