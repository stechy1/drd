package cz.stechy.drd.model.spell.price.modifier;

import cz.stechy.drd.model.spell.price.BasicSpellPrice;
import cz.stechy.drd.model.spell.price.ISpellPrice;
import java.util.Map;

/**
 * Třída reprezentující jednoduchou sčítačku ceny kouzla
 */
// TODO vylepšit modifikátory o proměnný počet proměnných
public class SpellPriceAdder implements ISpellPrice {

    // region Constants

    private static final String PACK_FORMAT = "<%s+%s>";

    // endregion

    // region Variables

    private final ISpellPrice left;
    private final ISpellPrice right;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou sčítačku ceny kouzla s konstantou na levé straně
     *
     * @param left Konstanta, která se přičte k ceně
     * @param right {@link ISpellPrice} Složitější výpočet ceny
     */
    public SpellPriceAdder(int left, ISpellPrice right) {
        this(new BasicSpellPrice(left), right);
    }

    /**
     * Vytvoří novou sčítačku ceny kouzla s konstantou na pravé straně
     *
     * @param left {@link ISpellPrice} Složitější výpočet ceny
     * @param right Konstanta, která se přičte k ceně
     */
    public SpellPriceAdder(ISpellPrice left, int right) {
        this(left, new BasicSpellPrice(right));
    }

    /**
     * Vytvoří novou sčítačku ceny kouzla ze dvou konstant
     *
     * @param left První konstanta
     * @param right Druhá konstanta
     */
    public SpellPriceAdder(int left, int right) {
        this(new BasicSpellPrice(left), new BasicSpellPrice(right));
    }

    /**
     * Vytvoří novou sčítačku ceny kouzla
     *
     * @param left První cena kouzla
     * @param right Druhá cena kouzla která se přičte
     */
    public SpellPriceAdder(ISpellPrice left, ISpellPrice right) {
        this.left = left;
        this.right = right;
    }

    // endregion

    @Override
    public int calculateMainPrice(Map<String, Integer> parameters) {
        return left.calculateMainPrice(parameters) + right.calculateMainPrice(parameters);
    }

    @Override
    public int calculateExtention() {
        return left.calculateExtention() + right.calculateExtention();
    }

    @Override
    public String pack() {
        return String.format(PACK_FORMAT, left.pack(), right.pack());
    }

    @Override
    public String toString() {
        return left.toString() + " + " + right.toString();
    }
}
