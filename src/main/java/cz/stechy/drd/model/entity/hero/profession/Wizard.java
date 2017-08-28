package cz.stechy.drd.model.entity.hero.profession;

import cz.stechy.drd.model.entity.hero.Hero;

/**
 * Třída obsahující dovednosti, která dokáže kouzelník na 1. až 5. úrovní
 */
public class Wizard {

    // region Constants

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
    }

    // endregion

    // region Public methods

    /**
     * Získá z tabulky maximální počet magů pro zadanou úroveň
     * Počet se získá z úrovně a stupně inteligence
     *
     * @return Maximální množství magů pro aktuální úroveň
     */
    public int getMaxMag() {
        // Normalizace úrovně a obratnosti, abych nevylezl mimo hranice pole
        final int level = Math.max(1, Math.min(hero.getLevel(), 5));
        final int intelligence = Math.max(1, Math.min(hero.getIntelligence().getValue() - 8, 14));

        return BASIC_MAGENERGY[level - 1][intelligence - 1];
    }

    /**
     * Získá z tabulky maximální možný počet kouzel na hrdinově úrovni
     *
     * @return Maximální možný počet kouzel na hrdinově úrovni
     */
    public int getMaxSpellCount() {
        return MAX_SPELL_COUNT[hero.getLevel() - 1];
    }

    /**
     * Spočítá z tabulky pravděpodobnost úspěchu, která je závislá na obratnosti
     *
     * @return Pravděpodobnost úspěchu
     */
    public int getProbabilityOfSuccess() {
        return PROBABILITY_OF_SUCCESS[hero.getIntelligence().getValue() - 1];
    }

    public int getProbabilityOfAvoidInvisibility(int typeOfInvisible) {
        final int level = Math.max(1, Math.min(hero.getLevel(), 5));
        final int index = Math.max(0, Math.min(typeOfInvisible, 6));

        return INVISIBLE_MOBS[level - 1][index];
    }

    // endregion
}
