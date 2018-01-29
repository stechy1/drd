package cz.stechy.drd.model.spell.price.modifier;

import cz.stechy.drd.model.spell.price.BasicSpellPrice;
import cz.stechy.drd.model.spell.price.ISpellPrice;
import cz.stechy.drd.model.spell.price.ModifierPrice;
import java.util.Map;

/**
 * Třída reprezentující jednoduchou násobičku ceny kouzla
 */
public class SpellPriceMultiplier extends ModifierPrice {

    // region Constants

    private static final String PACK_FORMAT = "<%s*%s>";

    // endregion

    // region Variables

    private final ISpellPrice left;
    private final ISpellPrice right;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou násobičku ceny kouzla s konstantou na levé straně
     *
     * @param left Konstanta, kterou se cena vynásobí
     * @param right {@link ISpellPrice} Složitější výpočet ceny
     */
    public SpellPriceMultiplier(int left, ISpellPrice right) {
        this(new BasicSpellPrice(left), right);
    }

    /**
     * Vytvoří novou násobičku ceny kouzla s konstantou na pravé straně
     *
     * @param left {@link ISpellPrice} Složitější výpočet ceny
     * @param right Konstanta, kterou se cena vynásobí
     */
    public SpellPriceMultiplier(ISpellPrice left, int right) {
        this(left, new BasicSpellPrice(right));
    }

    /**
     * Vytvoří novou násobičku ceny kouzla ze dvou konstant
     *
     * @param left První konstanta
     * @param right Druhá konstanta kterou se bude násobit
     */
    public SpellPriceMultiplier(int left, int right) {
        this(new BasicSpellPrice(left), new BasicSpellPrice(right));
    }

    /**
     * Vytvoří novou násobičku ceny kouzla
     *
     * @param left První cena kouzla
     * @param right Druhá cena kouzla
     */
    public SpellPriceMultiplier(ISpellPrice left, ISpellPrice right) {
        this.left = left;
        this.right = right;
    }

    // endregion

    @Override
    public int calculateMainPrice(Map<String, Integer> parameters) {
        return left.calculateMainPrice(parameters) * right.calculateMainPrice(parameters);
    }

    @Override
    public int calculateExtention() {
        return left.calculateExtention() * right.calculateExtention();
    }

    @Override
    public String pack() {
        return String.format(PACK_FORMAT, left.pack(), right.pack());
    }

    @Override
    public ISpellPrice getLeft() {
        return left;
    }

    @Override
    public ISpellPrice getRight() {
        return right;
    }

    @Override
    public ModifierType getType() {
        return ModifierType.MULTIPLE;
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " * " + right.toString() + ")";
    }
}
