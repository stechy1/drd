package cz.stechy.drd.model.spell.price;

import cz.stechy.drd.R;
import cz.stechy.drd.model.ITranslatedEnum;
import cz.stechy.drd.model.spell.parser.SpellParser.SpellPriceType;
import java.util.Map;

/**
 * Základní modifikátor pro výpočet ceny kouzla s proměnným základem ceny
 */
public class VariableSpellPrice implements ISpellPrice {

    // region Constants

    // Typ proměnné : ordinální číslo výčtového typu reprezentující proměnnou
    private static final String PACK_FORMAT = "[%d:%d]";

    private static final String MODIFICATOR_VARIABLE = "variable_";

    // endregion

    // region Variables

    private final VariableType variable;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou cenu kouzla s proměnnou cenou a vlastním názvem proměnné
     *
     * @param variable {@link VariableType}
     */
    public VariableSpellPrice(VariableType variable) {
        this.variable = variable;
    }

    // endregion

    // region Public static methods

    /**
     * Upraví název klíče tak, aby bylo možné ho použít pouze jako proměnná v této třídě
     *
     * @param name Název klíče
     * @return Modifikovaný název klíče
     */
    public static String buildKey(String name) {
        return MODIFICATOR_VARIABLE + name;
    }

    // endregion

    @Override
    public int calculateMainPrice(Map<String, Integer> parameters) {
        final String fullName = MODIFICATOR_VARIABLE + variable.name();
        int price = 0;

        if (parameters.containsKey(fullName)) {
            price = parameters.get(fullName);
        }

        return price;
    }

    @Override
    public int calculateExtention() {
        return 0;
    }

    @Override
    public String pack() {
        return String.format(PACK_FORMAT, SpellPriceType.VARIABLE.ordinal(), variable.ordinal());
    }

    @Override
    public ISpellPrice getLeft() {
        return null;
    }

    @Override
    public ISpellPrice getRight() {
        return null;
    }

    @Override
    public String toString() {
        return variable.getKeyForTranslation();
    }

    public VariableType getVariable() {
        return variable;
    }

    public enum VariableType implements ITranslatedEnum {
        VIABILITY(R.Translate.BESTIARY_VIABILITY),
        STRENGTH(R.Translate.HERO_STRENGTH),
        DEXTERITY(R.Translate.HERO_DEXTERITY),
        IMMUNITY(R.Translate.HERO_IMMUNITY),
        INTELLIGENCE(R.Translate.HERO_INTELLIGENCE),
        CHARISMA(R.Translate.HERO_CHARISMA);

        private final String key;

        VariableType(String key) {
            this.key = key;
        }

        @Override
        public String getKeyForTranslation() {
            return key;
        }
    }
}
