package cz.stechy.drd.spell;

import static org.junit.Assert.assertEquals;

import cz.stechy.drd.model.spell.price.BasicSpellPrice;
import cz.stechy.drd.model.spell.price.ISpellPrice;
import cz.stechy.drd.model.spell.price.VariableSpellPrice;
import cz.stechy.drd.model.spell.price.VariableSpellPrice.VariableType;
import cz.stechy.drd.model.spell.price.modifier.SpellPriceAdder;
import cz.stechy.drd.model.spell.price.modifier.SpellPriceDivider;
import cz.stechy.drd.model.spell.price.modifier.SpellPriceMultiplier;
import cz.stechy.drd.model.spell.price.modifier.SpellPriceSubtracter;
import org.junit.Test;

/**
 * Třída obsahující testy zaměřující se na packování ceny kouzel do textové podoby
 */
public class SpelPackTests {

    @Test
    public void basicSpellPricePackTest() throws Exception {
        final int price = 5;
        final ISpellPrice spellPrice = new BasicSpellPrice(price);
        final String expectedPackResult = "[0:5|0]";

        assertEquals("Chyba, packovací metoda vrátila řetězec ve špatném formátu.",
            expectedPackResult, spellPrice.pack());
    }

    @Test
    public void variableSpellPricePackTest() throws Exception {
        final VariableType variable = VariableSpellPrice.VariableType.VIABILITY;
        final ISpellPrice spellPrice = new VariableSpellPrice(variable);
        final String expectedPackResult = "[1:0]";

        assertEquals("Chyba, packovací metoda vrátila řetězec ve špatném formátu.",
            expectedPackResult, spellPrice.pack());
    }

    @Test
    public void adderSpellPricePackTest() throws Exception {
        final int price1 = 9;
        final int price2 = 8;
        final ISpellPrice spellPrice = new SpellPriceAdder(price1, price2);
        final String expectedPackResult = "<[0:9|0]+[0:8|0]>";

        assertEquals("Chyba, packovací metoda vrátila řetězec ve špatném formátu.",
            expectedPackResult, spellPrice.pack());
    }

    @Test
    public void subtracterSpellPricePackTest() throws Exception {
        final int price1 = 9;
        final int price2 = 8;
        final ISpellPrice spellPrice = new SpellPriceSubtracter(price1, price2);
        final String expectedPackResult = "<[0:9|0]-[0:8|0]>";

        assertEquals("Chyba, packovací metoda vrátila řetězec ve špatném formátu.",
            expectedPackResult, spellPrice.pack());
    }

    @Test
    public void multiplierSpellPricePackTest() throws Exception {
        final int price1 = 9;
        final int price2 = 8;
        final ISpellPrice spellPrice = new SpellPriceMultiplier(price1, price2);
        final String expectedPackResult = "<[0:9|0]*[0:8|0]>";

        assertEquals("Chyba, packovací metoda vrátila řetězec ve špatném formátu.",
            expectedPackResult, spellPrice.pack());
    }

    @Test
    public void dividerSpellPricePackTest() throws Exception {
        final int price1 = 9;
        final int price2 = 8;
        final ISpellPrice spellPrice = new SpellPriceDivider(price1, price2);
        final String expectedPackResult = "<[0:9|0]/[0:8|0]>";

        assertEquals("Chyba, packovací metoda vrátila řetězec ve špatném formátu.",
            expectedPackResult, spellPrice.pack());
    }

    @Test
    public void multipleModifierSpellPricePackTest() throws Exception {
        final int price1 = 5;
        final int price2 = 6;
        final int price3 = 7;
        final ISpellPrice spellPrice = new SpellPriceMultiplier(new SpellPriceAdder(price1, price2),
            price3);
        final String expectedPackResult = "<<[0:5|0]+[0:6|0]>*[0:7|0]>";

        assertEquals("Chyba, packovací metoda vrátila řetězec ve špatném formátu.",
            expectedPackResult, spellPrice.pack());
    }
}
