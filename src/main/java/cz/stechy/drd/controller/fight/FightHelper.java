package cz.stechy.drd.controller.fight;

import cz.stechy.drd.model.entity.Height;

/**
 * Pomocní knihovní třída pro souboj
 */
public final class FightHelper {

    private static final int[][] TABLE_REPAIRS_BY_HEIGHT = {
        //A0  A   B   C   D   E Útočník
        { 0,  2,  3,  4,  0, -2}, // Obránce je A0
        {-2,  0,  1,  2,  0, -1}, // Obránce je A
        {-3, -1,  0,  1,  2,  0}, // Obránce je B
        {-4, -2, -1,  0,  1,  2}, // Obránce je C
        {-5, -3, -2, -1,  0,  1}, // Obránce je D
        {-1, -4, -3, -2, -1,  0} // Obránce je E
    };



    private FightHelper() {

    }

    public static int getRepairByHeight(Height attacker, Height defender) {
        return TABLE_REPAIRS_BY_HEIGHT[defender.ordinal()][attacker.ordinal()];
    }

}
