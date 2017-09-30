package cz.stechy.drd.model.spell.price;

import cz.stechy.drd.model.spell.parser.SpellParser.SpellPriceType;
import java.util.Map;

/**
 * Základní modifikátor pro výpočet ceny kouzla s konstantou
 */
public class BasicSpellPrice implements ISpellPrice {

    // region Constants

    // Typ proměnné : konstantní cena : konstantní rozšíření ceny
    private static final String PACK_FORMAT = "[%d:%d|%d]";

    public static final int NO_EXTENTION = 0;

    // endregion

    // region Variables

    private final int price;
    private final int extention;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou konstantní cennu kouzla bez možnosti udržování/prodloužení kouzla
     *
     * @param price
     */
    public BasicSpellPrice(int price) {
        this(price, NO_EXTENTION);
    }

    /**
     * Vytvoří novou konstantní cenu kouzla s možností udržování/prodloužení kouzla
     *
     * @param price Konstantní cena kouzla
     * @param extention Cena, která je potřeba pro udržení/prodloužení kouzla
     */
    public BasicSpellPrice(int price, int extention) {
        this.price = price;
        this.extention = extention;
    }

    // endregion

    @Override
    public int calculateMainPrice(Map<String, Integer> parameters) {
        return price;
    }

    @Override
    public int calculateExtention() {
        return extention;
    }

    @Override
    public String pack() {
        return String.format(PACK_FORMAT, SpellPriceType.CONSTANT.ordinal(), price, extention);
    }

    @Override
    public String toString() {
        return String.valueOf(price);
    }
}
