package cz.stechy.drd.model.entity.hero;

import cz.stechy.drd.model.Dice;
import cz.stechy.drd.model.entity.EntityProperty;
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

    private static final int EXPERIENCE_INDEX = 0;
    private static final int PRICE_INDEX = 1;
    private static final int[][][] EXPERIENCES = {
        // Válečník
        {{0, 0}, {450, 2}, {900, 4}, {1825, 8}, {3675, 13}, {7400, 19}, {15000, 26}, {30000, 34}, {55000, 43}, {80000, 53}, {105000, 64}, {130000, 77}, {155000, 90}, {180000, 105}, {205000, 120}, {230000, 137}, {255000, 155}, {280000, 173}, {305000, 193}, {330000, 214}, {355000, 237}, {380000, 260}, {405000, 284}, {430000, 309}, {455000, 336}, {480000, 363}, {505000, 392}, {530000, 422}, {555000, 452}, {580000, 484}, {605000, 517}, {630000, 551}, {655000, 486}, {680000, 622}, {705000, 660}, {730000,698}},
        // Hraničář
        {{0, 0}, {450, 2}, {900, 4}, {1825, 8}, {3675, 13}, {7400, 19}, {15000, 26}, {30000, 34}, {55000, 44}, {80000, 54}, {105000, 66}, {130000, 78}, {155000, 92}, {180000, 107}, {205000, 123}, {230000, 140}, {255000, 159}, {280000, 178}, {305000, 199}, {330000, 220}, {355000, 243}, {380000, 267}, {405000, 292}, {430000, 318}, {455000, 345}, {480000, 373}, {505000, 403}, {530000, 433}, {555000, 465}, {580000, 498}, {605000, 532}, {630000, 567}, {655000, 603}, {680000, 640}, {705000, 678}, {730000,718}},
        // Kouzelník
        {{0, 0}, {575, 2}, {1150, 4}, {2300, 8}, {4650, 13}, {9325, 19}, {18700, 27}, {37500, 35}, {62500, 45}, {87500, 55}, {112500, 67}, {137500, 80}, {162500, 94}, {187500, 110}, {212500, 126}, {237500, 144}, {262500, 162}, {287500, 182}, {312500, 203}, {337500, 225}, {362500, 249}, {387500, 273}, {412500, 298}, {437500, 325}, {462500, 353}, {487500, 382}, {512500, 412}, {537500, 443}, {562500, 476}, {587500, 509}, {612500, 544}, {637500, 580}, {662500, 617}, {687500, 655}, {712500, 694}, {737500, 734}},
        // Alchymista
        {{0, 0}, {610, 2}, {1250, 4}, {2575, 8}, {5250, 13}, {10750, 19}, {22000, 26}, {45000, 35}, {70000, 44}, {95000, 55}, {120000, 67}, {145000, 79}, {170000, 94}, {195000, 109}, {220000, 125}, {245000, 142}, {270000, 161}, {295000, 181}, {320000, 201}, {345000, 223}, {370000, 246}, {395000, 271}, {420000, 296}, {445000, 322}, {470000, 350}, {495000, 379}, {520000, 409}, {545000, 440}, {570000, 472}, {595000, 505}, {620000, 539}, {645000, 575}, {670000, 611}, {695000, 649}, {720000, 688}, {745000, 728}},
        // Zloděj
        {{0, 0}, {325, 2}, {730, 4}, {1575, 8}, {3450, 12}, {7450, 18}, {16150, 25}, {35000, 32}, {60000, 41}, {85000, 51}, {110000, 61}, {135000, 73}, {160000, 86}, {185000, 99}, {210000, 114}, {235000, 130}, {260000, 147}, {285000, 164}, {310000, 183}, {335000, 203}, {360000, 224}, {385000, 245}, {410000, 268}, {435000, 292}, {460000, 317}, {485000, 343}, {510000, 369}, {535000, 397}, {560000, 426}, {585000, 456}, {610000, 487}, {635000, 519}, {660000, 552}, {685000, 585}, {710000, 620}, {735000, 656}}
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

    // region Public methods

    /**
     * Vygeneruje sílu postavy
     *
     * @return Síla postavy
     */
    public int strength() {
        final int left, right;
        final int raceOrdinal = race.ordinal();
        final int professionOrdinal = profession.ordinal();
        if (profession == Profession.WARIOR || profession == Profession.RANGER) {
            final int repair = REPAIRS_BY_RACE[raceOrdinal][INDEX_STRENGTH];
            left = PROPERTIES_BY_PROFESSION[professionOrdinal][0] + repair;
            right = PROPERTIES_BY_PROFESSION[professionOrdinal][1] + repair;
        } else {
            left = PROPERTIES[professionOrdinal][2 * INDEX_STRENGTH];
            right = PROPERTIES[professionOrdinal][2 * INDEX_STRENGTH + 1];
        }
        return Dice.generateValue(left, right);
    }

    /**
     * Vygeneruje obratnost postavy
     *
     * @return Obratnost postavy
     */
    public int dexterity() {
        final int left, right;
        final int raceOrdinal = race.ordinal();
        final int professionOrdinal = profession.ordinal();
        if (profession == Profession.ALCHEMIST || profession == Profession.THIEF) {
            final int repair = REPAIRS_BY_RACE[raceOrdinal][INDEX_DEXTERITY];
            left = PROPERTIES_BY_PROFESSION[professionOrdinal][2 * INDEX_DEXTERITY] + repair;
            right = PROPERTIES_BY_PROFESSION[professionOrdinal][2 * INDEX_DEXTERITY + 1] + repair;
        } else {
            left = PROPERTIES[professionOrdinal][2 * INDEX_DEXTERITY];
            right = PROPERTIES[professionOrdinal][2 * INDEX_DEXTERITY + 1];
        }
        return Dice.generateValue(left, right);
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
            final int repair = REPAIRS_BY_RACE[raceOrdinal][INDEX_IMMUNITY];
            left = PROPERTIES_BY_PROFESSION[professionOrdinal][2 * INDEX_IMMUNITY] + repair;
            right = PROPERTIES_BY_PROFESSION[professionOrdinal][2 * INDEX_IMMUNITY + 1] + repair;
        } else {
            left = PROPERTIES[professionOrdinal][2 * INDEX_IMMUNITY];
            right = PROPERTIES[professionOrdinal][2 * INDEX_IMMUNITY + 1];
        }
        return Dice.generateValue(left, right);
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
            final int repair = REPAIRS_BY_RACE[raceOrdinal][INDEX_INTELLIGENCE];
            left = PROPERTIES_BY_PROFESSION[professionOrdinal][2 * INDEX_INTELLIGENCE] + repair;
            right = PROPERTIES_BY_PROFESSION[professionOrdinal][2 * INDEX_INTELLIGENCE + 1] + repair;
        } else {
            left = PROPERTIES[professionOrdinal][2 * INDEX_INTELLIGENCE];
            right = PROPERTIES[professionOrdinal][2 * INDEX_INTELLIGENCE + 1];
        }
        return Dice.generateValue(left, right);
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
            final int repair = REPAIRS_BY_RACE[raceOrdinal][INDEX_CHARISMA];
            left = PROPERTIES_BY_PROFESSION[professionOrdinal][2 * INDEX_CHARISMA] + repair;
            right = PROPERTIES_BY_PROFESSION[professionOrdinal][2 * INDEX_CHARISMA + 1] + repair;
        } else {
            left = PROPERTIES[professionOrdinal][2 * INDEX_CHARISMA];
            right = PROPERTIES[professionOrdinal][2 * INDEX_CHARISMA + 1];
        }
        return Dice.generateValue(left, right);
    }

    /**
     * Vygeneruje počet životů pro postavu
     * baseLive = baseLive + immunity
     *
     * @param immunity Odolnost hrdiny
     * @return Počet životů
     */
    public int baseLive(EntityProperty immunity) {
        return LIVES_BASE[profession.ordinal()] + immunity.getRepair();
    }

    /**
     * Vypočítá kolik životů se přidá při přechodu na novou úroveň
     * live = max(1, dice.roll() + addon
     *
     * @param immunity Odolnost hrdiny
     * @return Počet životů, který se má přičíst ke stávající hodnotě
     */
    public int live(EntityProperty immunity) {
            Dice dice = LIVES_DICE[profession.ordinal()];
            int addon = LIVES_ADDON[profession.ordinal()];

            return Math.max(1, dice.roll() + addon + immunity.getRepair());
        }

    /**
     * @return Vrátí výšku postavy ve formé výčtového typu {@link Height}
     */
    public Height height() {
        return HEIGHT_BY_RACE[race.ordinal()];
    }

    /**
     * Vypočítá zkušenosti na základě rasy a úrovni postavy
     *
     * @param profession Rasa postavy
     * @param level Úroveň postavy
     * @return Počet zkušeností k dosažení další úrovně
     */
    public static int experience(Profession profession, int level) {
        if (profession == null) {
            return 0;
        }
        return EXPERIENCES[profession.ordinal()][level][EXPERIENCE_INDEX];
    }

    /**
     * Vypočítá cenu za přechod na vyšší úroveň
     *
     * @param profession Rasa za kterou postava
     * @param level Úroveň postavy
     * @return Cena za přestup na novou úroveň
     */
    public static int priceForLevelUp(Profession profession, int level) {
        if (profession == null) {
            return 0;
        }
        return EXPERIENCES[profession.ordinal()][level][PRICE_INDEX];
    }

    // endregion

}
