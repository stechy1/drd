package cz.stechy.drd.model.entity;

/**
 * Přesvědčení entity
 */
public enum Conviction {
    LAWFUL_GOOD, CONFUSED_GOODNESS, NEUTRAL, CONFUSED_EVIL, LAWFUL_EVIL;

    public static Conviction valueOf(int index) {
        if (index < 0) {
            return null;
        }

        return Conviction.values()[index];
    }

}
