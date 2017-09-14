package cz.stechy.drd.controller.spellbook;

import cz.stechy.drd.model.spell.Spell;
import cz.stechy.screens.Bundle;

final class SpellBookHelper {

    // region Constants

    public static final String SPELL_ACTION = "action_type";
    public static final int SPELL_ACTION_ADD = 1;
    public static final int SPELL_ACTION_UPDATE = 2;
    public static final int SPELL_ROW_HEIGHT = 40;

    // endregion

    // region Constructors

    /**
     * Privátní konstruktor k zabránění vytvoření instance
     */
    private SpellBookHelper() {

    }

    public static Spell spellFromBundle(Bundle bundle) {
        return null;
    }

    // endregion

}
