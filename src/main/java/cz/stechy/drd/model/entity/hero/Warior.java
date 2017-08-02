package cz.stechy.drd.model.entity.hero;

import cz.stechy.drd.model.Dice;
import cz.stechy.drd.model.Trap;

/**
 * Třída reprezentující válečníka od 1 do 5 úrovně
 */
public final class Warior {

    // region Constants

    private static final int[][] INTIMIDATION_TABLE = {
        // Bojovnost 2, 3, 4,5, 6, 7, 8, 9, 10, 11
        {100, 90, 80, 70, 60, 50, 40, 30, 20, 10},                   // Charisma 1
        {85, 75, 65, 55, 45, 35, 25, 15, 5, -5},                     // 2
        {70, 60, 50, 40, 30, 20, 10, 0, -10, -20},                   // 3
        {55, 45, 35, 25, 15, 5, -5, -15, -25, -35},                  // 4
        {40, 30, 20, 10, 0, -10, -20, -30, -40, -50},                // 5
        {25, 15, 5, -5, -15, -25, -35, -45, -55, -65},               // 6
        {10, 0, -10, -20, -30, -40, -50, -60, -70, -80},             // 7
        {-5, -15, -25, -35, -45, -55, -65, -75, -85, -95},           // 8
        {-20, -30, -40, -50, -60, -70, -80, -90, -100, -110},        // 9
        {-35, -45, -55, -65, -75, -85, -95, -105, -115, -125},       // 10
        {-50, -60, -70, -80, -90, -100, -110, -120, -130, -140},     // 11
        {-65, -75, -85, -95, -105, -115, -125, -135, -145, -155},    // 12
        {-80, -90, -100, -110, -120, -130, -140, -150, -160, -170},  // 13
        {-95, -105, -115, -125, -135, -145, -155, -165, -175, -185}, // 14
        {-80, -90, -100, -110, -120, -130, -140, -150, -160, -170},  // 15
        {-65, -75, -85, -95, -105, -115, -125, -135, -145, -155},    // 16
        {-50, -60, -70, -80, -90, -100, -110, -120, -130, -140},     // 17
        {-35, -45, -55, -65, -75, -85, -95, -105, -115, -125},       // 18
        {-20, -30, -40, -50, -60, -70, -80, -90, -100, -110},        // 19
        {-5, -15, -25, -35, -45, -55, -65, -75, -85, -95},           // 20
        {10, 0, -10, -20, -30, -40, -50, -60, -70, -80},             // 21
    };
    // Minimální bojovnost nestvůry, se kterou lze počítat
    private static final int INTIMIDATION_MIN_METTLE = 2;
    // Maximální bojovnost nestvůry, se kterou lze počítat
    private static final int INTIMIDATION_MAX_METTLE = 11;
    // Multiplikátor úrovně hrdiny pro výpočet zastrašení
    private static final int INTIMIDATION_HERO_LEVEL_MULTIPLIER = 3;

    // endregion

    // region Variables

    private final Hero hero;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou instanci povolání se zaměřením válečníka
     *
     * @param hero {@link Hero}
     */
    public Warior(Hero hero) {
        this.hero = hero;
    }

    // endregion

    // region Private methods

    /**
     * Implementace tabulky odhadu soupeře
     *
     * @param time Doba po kterou válečník bojuje s nepřítelem (kola)
     * @return Pravděpodobnost úspěchu
     */
    private int getEnemyEstimateByTime(int time) {
        time = Math.max(time, 1);
        if (time >=1 && time < 3) {
            return 15;
        } else if (time >= 3 && time < 10) {
            return 20;
        } else if (time >= 10 && time < 30) {
            return 30;
        } else {
            return 35;
        }
    }

    /**
     * Implementace bonusu za boj se stejným typem nestvůry
     *
     * @param times Kolikrát již válečník bojoval s tímto typem nestvůry
     * @return Bonus za boj se stejným typem nestvůry
     */
    private int getEnemyEstimateByTimes(int times) {
        times = Math.max(times, 1);
        switch (times) {
            case 1:
                return 0;
            case 2:
                return 3;
            case 3:
                return 5;
            case 4:
                return 10;
            default:
                return 15;
        }
    }

    // endregion

    // region Public methods

    /**
     * Vypočítá, zda-li válečník dokáže zastrašít nepřítele
     *
     * @param mettle Bojovnost nepřítele z intervalu <2; 11>
     * @return True, pokud se nepřítele podaří zastrašit, jinak False
     */
    public boolean intimidation(int mettle) {
        return intimidation(mettle, 1);
    }

    /**
     * Vypočítá, zda-li válečník dokáže zastrašit určitý počet nepřátel
     *
     * @param mettle Nejvyšší bojovnost nepřátel z intervalu <2; 11>
     * @param enemyCount Počet nepřátel
     * @return True, pokud se nepřátle podaří zastrašit, jinak False
     */
    public boolean intimidation(int mettle, int enemyCount) {
        mettle = Math.max(INTIMIDATION_MIN_METTLE, Math.min(INTIMIDATION_MAX_METTLE, mettle));
        final int roll = Dice.K100.roll() * enemyCount - INTIMIDATION_HERO_LEVEL_MULTIPLIER * hero.getLevel();
        return roll <= INTIMIDATION_TABLE[hero.getCharisma().getValue()][mettle];
    }

    /**
     * Pokusí se rozpoznat artefakt o zadaném věhlasu
     *
     * @param renown Věhlas artefaktu
     * @return True, pokud poznal co je artefakt tač, jinak False
     */
    public boolean detectArtefact(int renown) {
        return new Trap()
            .property(hero.getIntelligence())
            .property(renown)
            .propertyFromLive(hero.getLevel())
            .danger(8)
            .roll()
            .isSuccess();
    }

    /**
     * Pokusí se odhadnout nepřítele
     *
     * @param time Jak dlouho již s nepřítelem bojuje (kola)
     * @param times Po kolikáté již s nepřítelem bojuje (počet)
     * @return
     */
    public boolean enemyEstimate(int time, int times) {
        int probability = hero.getLevel();
        // Přičtu bohus z tabulky odhadu soupeře
        probability += getEnemyEstimateByTime(time);
        // Odečtu postih za nepřirozený výcvik
        probability -= 10;
        // Přičtu bonus za boj se stejným typem nestvůry
        probability += getEnemyEstimateByTimes(times);
        // Pokud je teď pravděpodobnost menší než 0, nemá cenu házet a je to automaticky
        // fatální neúspěch
        if (probability <= 0) {
            return false;
        }

        // Nakonec si hodím kostkou
        // Pokud na kostce padně číslo menší, než je pravděpodobnost, znamená to úspěch
        // jinak neúspěch
        return Dice.K100.roll() <= probability;
    }

    // endregion
}
