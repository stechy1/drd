package cz.stechy.drd.model.spell.price;

import cz.stechy.drd.R;
import cz.stechy.drd.model.ITranslatedEnum;

public abstract class ModifierPrice implements ISpellPrice {

    public abstract ModifierType getType();

    public enum ModifierType implements ITranslatedEnum {
        ADD(R.Translate.SPELL_PRICE_MODIFIER_TYPE_ADDER, "+"),
        SUBTRACT(R.Translate.SPELL_PRICE_MODIFIER_TYPE_SUBTRACTER, "-"),
        MULTIPLE(R.Translate.SPELL_PRICE_MODIFIER_TYPE_MULTIPLIER, "*"),
        DIVIDE(R.Translate.SPELL_PRICE_MODIFIER_TYPE_DIVIDER, "/");

        private final String key;
        private final String operator;

        ModifierType(String key, String operator) {
            this.key = key;
            this.operator = operator;
        }

        @Override
        public String getKeyForTranslation() {
            return key;
        }

        @Override
        public String toString() {
            return operator;
        }
    }

}
