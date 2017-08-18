package cz.stechy.drd.model.entity.hero.profession;

import cz.stechy.drd.model.Dice;
import cz.stechy.drd.model.Trap;
import cz.stechy.drd.model.entity.EntityProperty;
import cz.stechy.drd.model.entity.hero.Hero;
import java.util.Arrays;

/**
 * Třída obsahující dovednosti, která dokáže hraničář na 1. až 5. úrovní
 */
public final class Ranger {

    // region Constants

    // Tabulka telekineze obsahující součit náhy a vzdálenosti
    private static final int[] TELEKINESIS_TABLE = {
        150, 300, 450, 600, 750, 900, 1000, 1100, 1200, 1300, 1400, 1500, 1600, 1700, 1800, 1900,
        2000, 2100, 2200, 2300, 2400, 2500, 2600, 2700, 2800, 2900, 3000, 3100, 3200, 3300, 3400,
        3500, 3600
    };
    // Tabulka pyrokineze obsahující součin času a vzdálenosti
    private static final int[] PYROKINESIS_TABLE = {
        75, 150, 225, 300, 375, 450, 500, 550, 600, 650, 700, 750, 800, 850, 900, 950, 1000, 1050,
        1100, 1150, 1200, 1250, 1300, 1350, 1400, 1450, 1500, 1550, 1600, 1650, 1700, 1750, 1800
    };
    // Tabulka telepatie obsahující: základní vzdálenost; zkrácení vzdálenosti za dřevo/kámen/kov
    private static final int[][] TELEPATY_TABLE = {
        {100, 50, 10, 1}, {150, 60, 12, 2}, {200, 70, 14, 3}, {250, 80, 16, 4}, {300, 90, 18, 5},
        {350, 100, 20, 6}, {375, 105, 21, 6}, {400, 110, 22, 7}, {425, 115, 23, 7},
        {450, 120, 24, 8}, {475, 125, 25, 8}, {500, 130, 26, 9}, {525, 135, 27, 9},
        {550, 140, 28, 10}, {575, 145, 29, 10}, {600, 150, 30, 11}, {625, 155, 31, 11},
        {650, 160, 32, 12}, {675, 165, 33, 12}, {700, 170, 34, 13}, {725, 175, 35, 13},
        {750, 180, 36, 14}, {775, 185, 37, 14}, {800, 190, 38, 15}, {825, 192, 39, 15},
        {850, 200, 40, 16}, {875, 205, 41, 16}, {900, 210, 42, 17}, {925, 215, 43, 17},
        {950, 220, 44, 18}, {975, 225, 45, 18}, {1000, 230, 46, 19}, {1025, 235, 47, 19}
    };

    private static final int MAX_LEVEL = 33;
    private static final int TELEPATY_INDEX_BASIC_DISTANCE = 0;

    private static final int[][] BASIC_MAGENERGY = {
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // 1. úroveň
        {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3}, // 2. úroveň
        {6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8}, // 3. úroveň
        {10, 10, 10, 10, 11, 11, 11, 11, 11, 11, 12, 12, 13, 13}, // 4. úroveň
        {12, 12, 13, 13, 13, 13, 14, 14, 15, 15, 15, 15, 16, 16}  // 5. úroveň
    };


    // endregion

    // region Variables

    private final Hero hero;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou instanci povolání se zaměřením na hraničáře
     *
     * @param hero {@link Hero}
     */
    public Ranger(Hero hero) {
        this.hero = hero;
    }

    // endregion

    // region Public static methods

    /**
     * Vypočítá maximální možnou magenergii na základě úrovní a inteligenci postavy
     *
     * @param intelligence {@link EntityProperty} Inteligence postavy
     * @param level Úroveň postavy (maximálně do 5. úrovně)
     * @return Maximální magenergii
     */
    public static int getMaxMag(EntityProperty intelligence, int level) {
        // Normalizace úrovně a inteligence abych nevylezl mimo hranice pole
        final int levelIndex = Math.min(level, 5);
        final int intelligenceIndex = Math.min(intelligence.getValue(), 19);
        return BASIC_MAGENERGY[levelIndex][intelligenceIndex];
    }

    // endregion

    // region Private methods



    // endregion

    // region Public methods

    /**
     * Zjistí, zda-li hraničář udrží stopu
     *
     * @param terrain Náročnost terénu % (větší je lepší)
     * @param age Stáří stopy (co se hraničář domnívá)
     * @param count Počet tvorů, kteří zanechali stopu
     * @param continueTracking Pokud hraničář házi po druhé a více
     * @return True, pokud se hraničáři podaří pokračovat ve stopování, jinak False
     */
    public boolean tracking(int terrain, int age, int count, boolean continueTracking) {
        // Základní pravděpodobnost vychází z náročnosti terénu
        int probability = terrain;
        // Pokud není terén nepoužitelný, tak připočítám k pravděpodobnosti 2% za každou úroveň
        probability += ((terrain > 0) ? 2 * hero.getLevel() : 0);
        // Odečtu stáří stopy
        probability -= 10 * age;
        // Přičtu 3% za každého tvora, co stopuji, pokud jich je více
        probability += 3 * count;
        // Pokud stopuji delší dobu, přidám do pravděodobnosti ještě 10%
        probability += continueTracking ? 10 : 0;
        // Nakonec hodím kostkou a uvidím, co mi padne
        return Dice.K100.roll() <= probability;
    }

    /**
     * Pokus o pohyb s volným předmětem
     *
     * @param distance Vzdálenost hraničáře od předmětu
     * @param weight Váha předmětu, kterým chce hraničář pohnout
     * @return True, pokud se hraničáři podaří předmět přesunout, jinak False
     */
    public boolean telekinesis(int distance, int weight) {
        // TODO vymyslet kam uložit stupeň vlastnosti telekineze hraničáře
        final int property = 1;
        // Normalizace na index abych neutekl mimo hranici pole
        final int index = Math.min(property - 1, MAX_LEVEL);

        return distance * weight <= TELEKINESIS_TABLE[index];
    }

    /**
     * Vypočítá počet kol, který je potřeba pro zapálení požadovaného místa
     *
     * @param distance Vzdálenos od místa, kde chce hraničář zapálit oheň (v sázích)
     * @return Počet kol, který má hraničář na zapálení
     */
    public int pyrokinesis(int distance) {
        final int property = 1;
        // Normalizace na index abych neutekl mimo hranici pole
        final int index = Math.min(property - 1, MAX_LEVEL);
        final int product = PYROKINESIS_TABLE[index];
        return product / distance;
    }

    /**
     * Určí, zda-li se může hraničář pokusit o čtení myšlenek
     *
     * @param distance Vzdálenost mezi hraničářem a terčem
     * @param walls {@link MindMaterialWall}
     * @return True, pokud má hraničář dostatečné zkušenosti pro použití telekineze, jinak False
     */
    public boolean canReadMind(int distance, MindMaterialWall ...walls) {
        final int property = 1;
        // Normalizace na index abych neutekl mimo hranici pole
        final int index = Math.min(property - 1, MAX_LEVEL);
        int basicDistance = TELEPATY_TABLE[index][TELEPATY_INDEX_BASIC_DISTANCE];
        int modifier = Arrays.stream(walls)
            // Protože ordinal() vrací hodnotu od 0, je třeba přičíst 1 abych se dostal na správný
            // index
            .mapToInt(value -> TELEPATY_TABLE[index][value.wallType.ordinal() + 1] * value.thickness)
            .sum();
        basicDistance -= modifier;

        return distance <= basicDistance;
    }

    /**
     * Pokus o čtení mysli
     *
     * @param action {@link MindAction} Akce, kterou se hraničář pokouší vykonat
     * @param targetIntelligence Inteligence cíle
     * @param targetLevel Úroveň/Životaschopnost cíle
     * @return True, pokud byl pokus úspěšný, jinak false
     */
    public boolean readMind(MindAction action, EntityProperty targetIntelligence, int targetLevel) {
        Trap trap = new Trap();
        switch (action) {
            case ALLOWED_INSPIRATION:
            case ALLOWED_READING:
                trap.danger(action.danger - targetIntelligence.getRepair())
                    .property(hero.getIntelligence());
                break;
            case UNAUTHORIZED_INSPIRATION:
            case UNAUTHORIZED_READING:
                trap.danger(action.danger + targetIntelligence.getRepair())
                    .property(hero.getLevel() - targetLevel);
                break;
            case REVELATION:
            case CLOSURE:
                trap.danger(action.danger + hero.getIntelligence().getRepair())
                    .property(targetLevel - hero.getLevel());
                break;
        }
        return trap.roll().isSuccess();
    }

    // endregion

    /**
     * Výčet činností při čtení mysli
     */
    public enum MindAction {
        ALLOWED_INSPIRATION(1),      // Povolené vnuknutí
        ALLOWED_READING(2),          // Povolené čtení
        UNAUTHORIZED_INSPIRATION(6), // Nepovolené vnuknutí
        UNAUTHORIZED_READING(7),     // Nepovolené čtení
        REVELATION(4),               // Odhalení
        CLOSURE(6);                  // Uzavření

        // Nebezpečnost činnosti
        final int danger;

        /**
         * Činnost při čtení mysli
         *
         * @param danger
         */
        MindAction(int danger) {
            this.danger = danger;
        }
    }

    public static final class MindMaterialWall {

        // region Variables

        // Tloušťka stěny/překážky
        private final int thickness;
        private final MindMaterialWallType wallType;

        // endregion

        // region Constructors

        /**
         * Vytvoří novou překážku pro telekinezi
         *
         * @param wallType Materiál, ze kterého je překážka mezi hraničářem a terčem
         * @param thickness Tloušťka překážky, která jse mezi hraničářem a terčem
         */
        public MindMaterialWall(MindMaterialWallType wallType, int thickness) {
            this.wallType = wallType;
            this.thickness = thickness;
        }

        // endregion

        /**
         * Výčet možných překážek při použití telekineze
         */
        public enum MindMaterialWallType {
            WOOD, STONE, METAL
        }

    }

}
