package cz.stechy.drd.model.entity;

/**
 * Výška entity
 */
public enum Height {
    A0, A, B, C, D, E, F;

    public static Height valueOf(int index) {
        if (index < 0) {
            return null;
        }

        return Height.values()[index];
    }
}
