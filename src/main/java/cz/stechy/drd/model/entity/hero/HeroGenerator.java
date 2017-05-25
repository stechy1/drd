package cz.stechy.drd.model.entity.hero;

import cz.stechy.drd.model.Dice;
import cz.stechy.drd.model.entity.Height;
import cz.stechy.drd.model.entity.hero.Hero.Profession;
import cz.stechy.drd.model.entity.hero.Hero.Race;

/**
 * Pomocná třída pro generování atributů u hrdiny
 */
public final class HeroGenerator {

    // region Constants

    private static final int INDEX_STRENGTH = 0;
    private static final int INDEX_DEXTERITY = 1;
    private static final int INDEX_IMMUNITY = 2;
    private static final int INDEX_INTELLIGENCE = 3;
    private static final int INDEX_CHARISMA = 4;

    // Základní počty životů podle profese Válečník - Zloděj
    private static final int[] LIVES_BASE = {10, 8, 7, 6, 6};
    // Kostka, kterou se hází podle profese Válečník - Zloděj
    private static final Dice[] LIVES_DICE = {Dice.K10, Dice.K6, Dice.K6, Dice.K6, Dice.K6};
    // Hodnota, o kolik se zvýší život
    private static final int[] LIVES_ADDON = {0, 2, 1, 0, 0};

    // Vlastnosti podle povolání Síla[min, max] - Charisma[min, max]
    private static final int[][] PROPERTIES = {
        {3, 8, 11, 16, 8, 13, 10, 15, 8, 18}, // Hobit
        {5, 10, 10, 15, 10, 15, 9, 14, 7, 12}, // Kudůk
        {7, 12, 7, 12, 12, 17, 8, 13, 7, 12}, // Trpaslík
        {6, 11, 10, 15, 6, 11, 12, 17, 8, 18}, // Elf
        {6, 16, 9, 14, 9, 14, 10, 15, 2, 17}, // ČLověk
        {10, 15, 8, 13, 11, 16, 6, 11, 1, 16}, // Barbar
        {11, 16, 5, 10, 13, 18, 2, 7, 1, 11}  //  Kroll
    };

    // Vlastnosti podle profese
    private static final int[][] PROPERTIES_BY_PROFESSION = {
        {13, 18, 0, 0, 13, 18, 0, 0, 0, 0}, // Válečník
        {11, 16, 0, 0, 0, 0, 12, 17, 0, 0}, // Hraničár
        {0, 0, 13, 18, 12, 17, 0, 0, 0, 0}, // Alchymista
        {0, 0, 0, 0, 0, 0, 14, 19, 13, 18}, // Kouzelník
        {0, 0, 14, 19, 0, 0, 0, 0, 12, 17}  // Zloděj
    };

    // Tabulka oprav podle rasy
    private static final int[][] REPAIRS_BY_RACE = {
        {-5, 2, 0, -2, 3}, // Hobit
        {-3, 1, 1, -2, 0}, // Kudůk
        {1, -2, 3, -3, -2}, // Trpaslík
        {0, 1, -4, 2, 2}, // Elf
        {0, 0, 0, 0, 0}, // ČLověk
        {1, -1, 1, 0, -2}, // Barbar
        {3, -4, 3, -6, -5}, // Kroll
    };

    // Tabulka velikostí podle rasy
    private static final Height[] HEIGHT_BY_RACE = {
        // Hobit, Kudůk,    Trpaslík, Elf,      Člověk,   Babar,    Kroll
        Height.A, Height.A, Height.A, Height.B, Height.B, Height.B, Height.C
    };

    // endregion

    // region Variables

    private final Hero.Race race;
    private final Hero.Profession profession;

    // endregion

    // region Constructors

    /**
     * Vytvoří nový generátor postavy podle rasy a povolání
     *
     * @param race Rasa, pro kterou se budou generovat vlastnosti
     * @param profession Profese, pro kterou se budou generovat vlstnosti
     */
    public HeroGenerator(Race race, Profession profession) {
        this.race = race;
        this.profession = profession;
    }

    // endregion

    // region Private methods

    /**
     * Vypočte, kolikrát se bude házet šestistěnnou kostkou
     *
     * @param value Hodnota, která určí, kolikrát se bude házet kostkou
     * @return Kolikrát bude potřeba hodit šestistěnnou kostkou
     */
    private int getRollCount(int value) {
        return (int) Math.floor(value / (double) 5) + 1;
    }

    /**
     * Vygeneruje hodnotu z intervalu
     *
     * @param left Levá strana intervalue
     * @param right Pravá strana intervalue
     * @return Hodnota z intervalu
     */
    private int generateValue(int left, int right) {
        int delta = right - left;
        int rollCount = getRollCount(delta);
        int addon = left - rollCount;
        int result = 0;
        for (int i = 0; i < rollCount; i++) {
            result += Dice.K6.roll();
        }
        result += addon;
        return result;
    }

    // endregion

    // region Public methods

    /**
     * Vygeneruje sílu postavy
     *
     * @return Síla postavy
     */
    public int strength() {
        int left, right;
        int raceOrdinal = race.ordinal();
        int professionOrdinal = profession.ordinal();
        if (profession == Profession.WARIOR || profession == Profession.RANGER) {
            left = PROPERTIES_BY_PROFESSION[professionOrdinal][0];
            right = PROPERTIES_BY_PROFESSION[professionOrdinal][1];
        } else {
            left = PROPERTIES[professionOrdinal][2 * INDEX_STRENGTH];
            right = PROPERTIES[professionOrdinal][2 * INDEX_STRENGTH + 1];
        }
        return generateValue(left, right) + REPAIRS_BY_RACE[raceOrdinal][INDEX_STRENGTH];
    }

    /**
     * Vygeneruje obratnost postavy
     *
     * @return Obratnost postavy
     */
    public int dexterity() {
        int left, right;
        int raceOrdinal = race.ordinal();
        int professionOrdinal = profession.ordinal();
        if (profession == Profession.ALCHEMIST || profession == Profession.THIEF) {
            left = PROPERTIES_BY_PROFESSION[professionOrdinal][2 * INDEX_DEXTERITY];
            right = PROPERTIES_BY_PROFESSION[professionOrdinal][2 * INDEX_DEXTERITY + 1];
        } else {
            left = PROPERTIES[professionOrdinal][2 * INDEX_DEXTERITY];
            right = PROPERTIES[professionOrdinal][2 * INDEX_DEXTERITY + 1];
        }
        return generateValue(left, right) + REPAIRS_BY_RACE[raceOrdinal][INDEX_DEXTERITY];
    }

    /**
     * Vygeneruje odolnost postavy
     *
     * @return Odolnost postavy
     */
    public int immunity() {
        int left, right;
        int raceOrdinal = race.ordinal();
        int professionOrdinal = profession.ordinal();
        if (profession == Profession.WARIOR || profession == Profession.ALCHEMIST) {
            left = PROPERTIES_BY_PROFESSION[professionOrdinal][2 * INDEX_IMMUNITY];
            right = PROPERTIES_BY_PROFESSION[professionOrdinal][2 * INDEX_IMMUNITY + 1];
        } else {
            left = PROPERTIES[professionOrdinal][2 * INDEX_IMMUNITY];
            right = PROPERTIES[professionOrdinal][2 * INDEX_IMMUNITY + 1];
        }
        return generateValue(left, right) + REPAIRS_BY_RACE[raceOrdinal][INDEX_IMMUNITY];
    }

    /**
     * Vygeneruje inteligenci postavy
     *
     * @return Inteligenci postavy
     */
    public int intelligence() {
        int left, right;
        int raceOrdinal = race.ordinal();
        int professionOrdinal = profession.ordinal();
        if (profession == Profession.RANGER || profession == Profession.MAGICIAN) {
            left = PROPERTIES_BY_PROFESSION[professionOrdinal][2 * INDEX_INTELLIGENCE];
            right = PROPERTIES_BY_PROFESSION[professionOrdinal][2 * INDEX_INTELLIGENCE + 1];
        } else {
            left = PROPERTIES[professionOrdinal][2 * INDEX_INTELLIGENCE];
            right = PROPERTIES[professionOrdinal][2 * INDEX_INTELLIGENCE + 1];
        }
        return generateValue(left, right) + REPAIRS_BY_RACE[raceOrdinal][INDEX_INTELLIGENCE];
    }

    /**
     * Vygeneruje charisma postavy
     *
     * @return Charisma postavy
     */
    public int charisma() {
        int left, right;
        int raceOrdinal = race.ordinal();
        int professionOrdinal = profession.ordinal();
        if (profession == Profession.MAGICIAN || profession == Profession.THIEF) {
            left = PROPERTIES_BY_PROFESSION[professionOrdinal][2 * INDEX_CHARISMA];
            right = PROPERTIES_BY_PROFESSION[professionOrdinal][2 * INDEX_CHARISMA + 1];
        } else {
            left = PROPERTIES[professionOrdinal][2 * INDEX_CHARISMA];
            right = PROPERTIES[professionOrdinal][2 * INDEX_CHARISMA + 1];
        }
        return generateValue(left, right) + REPAIRS_BY_RACE[raceOrdinal][INDEX_CHARISMA];
    }

    /**
     * Vygeneruje počet životů pro postavu
     * live = baseLive + dice.roll() + addon
     *
     * @return Počet životů
     */
    public int live() {
        int baseLive = LIVES_BASE[profession.ordinal()];
        Dice dice = LIVES_DICE[profession.ordinal()];
        int addon = LIVES_ADDON[profession.ordinal()];

        return baseLive + dice.roll() + addon;
    }

    /**
     * @return Vrátí výšku postavy ve formé výčtového typu {@link Height}
     */
    public Height height() {
        return HEIGHT_BY_RACE[race.ordinal()];
    }

    // endregion

}
