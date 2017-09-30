package cz.stechy.drd.model.entity.hero.profession;

import cz.stechy.drd.model.entity.hero.Hero;

/**
 * Třída obsahující dovednosti, která dokáže zloděj na 1. až 5. úrovní
 */
public  class Thief {

    // region Constants

    private static final int[][] TABLE_OF_PROBABILITY_SUCCESS = {
        {5, 10, 15, 20, 25}, // Převleky
        {7, 11, 15, 19, 23}, // Získání důvěry
        {10, 15, 20, 25, 30}, // Objevení mechanismů
        {15, 20, 25, 30, 35}, // Objevení objektu
        {15, 20, 25, 30, 35}, // Zneškodnění mechanismu
        {25, 30, 35, 40, 45}, // Otevření objektu
        {70, 72, 74, 76, 78}, // Šplhání po zdech
        {65, 68, 71, 74, 77}, // Skok z výšky
        {15, 18, 21, 23, 26}, // Tichý pohyb
        {30, 35, 40, 45, 50}, // Schování se ve stínu
        {10, 13, 16, 19, 22}, // Vybírání kapes
    };

    // endregion

    // region Variables

    protected final Hero hero;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou instanci povolání se zaměřením na zloděje
     *
     * @param hero {@link Hero}
     */
    public Thief(Hero hero) {
        this.hero = hero;
    }

    // endregion

    // region Public methods

    /**
     * Vypočítá pravděpodobnost úspěchu akce
     *
     * @param ability {@link Ability} Schopnost, která se testuje
     * @return Pravděpodobnost úspěchu
     */
    public int getProbabilityOfSuccessForAction(Ability ability) {
        final int level = Math.min(hero.getLevel(), 5);
        return TABLE_OF_PROBABILITY_SUCCESS[ability.ordinal()][level - 1] + ability.getModifier().getModifiers(hero);
    }

    // endregion

    public enum Ability {
        DISGUISES(new CharismaModifier(3)), // Převleky
        CONFIDENCE(new CharismaModifier(3)), // Získání důvěry
        DISCOVERY_MECHANISM(new DexterityModifier(2)), // Objevení mechanismů
        DISCOVERY_OBJECTS(new DexterityModifier(3)), // Objevení objektů
        DESTROY_MECHANISM(new DexterityModifier(2)), // Zneškodnění mechanismů
        OPEN_OBJECTS(new DexterityModifier(3)), // Otevření objektů
        CLIMBING(new DexterityModifier(1)), // Šplhání po zdi
        JUMP_FROM_ABOVE(new DexterityModifier(1)), // Skok z výšky
        SILENT_MOTION(new DexterityModifier(1)), // Tichý pohyb
        HIDE_IN_THE_SHADOW(new DexterityModifier(2)), // Schování se ve stínu
        PICKING_POCKETS(new CharismaModifier(new DexterityModifier(1), 1)); // Vybírání kapes

        private final AbilityModifier modifier;

        Ability(AbilityModifier modifier) {
            this.modifier = modifier;
        }

        public AbilityModifier getModifier() {
            return modifier;
        }
    }

    private interface AbilityModifier {

        /**
         * Vrátí modifikátory na základě vlastností hrdiny
         *
         * @param hero {@link Hero}
         * @return Modifikátor úspěchu
         */
        int getModifiers(Hero hero);

    }

    /**
     * Základní modifikátor vlastnosti
     * Pouze implementuje rozhraní a deleguje práci na ostatní
     */
    private static abstract class BasicModifier implements AbilityModifier {

        private final AbilityModifier modifier;

        BasicModifier(AbilityModifier modifier) {
            this.modifier = modifier;
        }

        @Override
        public int getModifiers(Hero hero) {
            if (modifier == null) {
                return 0;
            }

            return modifier.getModifiers(hero);
        }
    }

    /**
     * Speciální modifikátor, který upravuje výsledek podle opravy charisma zloděje
     */
    private static final class CharismaModifier extends BasicModifier {

        private final int count;

        CharismaModifier(int count) {
            this(null, count);
        }

        CharismaModifier(AbilityModifier modifier, int count) {
            super(modifier);
            this.count = count;
        }

        @Override
        public int getModifiers(Hero hero) {
            final int modifier = hero.getCharisma().getRepair() * count;
            return modifier + super.getModifiers(hero);
        }
    }

    /**
     * Speciání modifikátor, který upravuje výsledek podle opravy obratnosti zloděje
     */
    private static final class DexterityModifier extends BasicModifier {
        private final int count;

        DexterityModifier(int count) {
            this(null, count);
        }

        DexterityModifier(AbilityModifier modifier, int count) {
            super(modifier);
            this.count = count;
        }

        @Override
        public int getModifiers(Hero hero) {
            final int modifier = hero.getDexterity().getRepair() * count;
            return modifier + super.getModifiers(hero);
        }
    }
}
