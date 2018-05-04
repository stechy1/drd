package cz.stechy.drd.spell;

import static org.junit.Assert.assertEquals;

import cz.stechy.drd.model.spell.price.BasicSpellPrice;
import cz.stechy.drd.model.spell.price.ISpellPrice;
import cz.stechy.drd.model.spell.price.VariableSpellPrice;
import cz.stechy.drd.model.spell.price.VariableSpellPrice.VariableType;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 * Třída obsahující testy zaměřující se na základní funkčnost ceny kouzel
 */
public class SpellTests {

    @Test
    public void basicSpellPriceTest() {
        final int price = 5;
        final ISpellPrice spellPrice = new BasicSpellPrice(price);

        assertEquals("Chyba, byla vytvořena základní konstantní cena se špatnou hodnotou.", price,
            spellPrice.calculateMainPrice());
        assertEquals("Chyba, cena obsahuje neočekávanou cenu za rozšíření kouzla",
            BasicSpellPrice.NO_EXTENTION, spellPrice.calculateExtention());
    }

    @Test
    public void basicSpellPriceWithExtentionTest() throws Exception {
        final int price = 6;
        final int extention = 2;
        final ISpellPrice spellPrice = new BasicSpellPrice(price, extention);

        assertEquals("Chyba, byla vytvořena základní konstantní cena se špatnou hodnotou.", price,
            spellPrice.calculateMainPrice());
        assertEquals("Chyba, cena obsahuje neočekávanou cenu za rozšíření kouzla.", extention,
            spellPrice.calculateExtention());
    }

    @Test
    public void variableSpellTest() throws Exception {
        final VariableType variableType = VariableSpellPrice.VariableType.VIABILITY;
        final int value = 2;
        final Map<String, Integer> modifiers = new HashMap<>();
        modifiers.put(VariableSpellPrice.buildKey(variableType.name()), value);
        final ISpellPrice spellPrice = new VariableSpellPrice(variableType);

        assertEquals("Chyba, cena má špatně přepsanou metodu toString.",
            variableType.getKeyForTranslation(), spellPrice.toString());
        assertEquals("Chyba, cena byla špatně nastavena.", value,
            spellPrice.calculateMainPrice(modifiers));
    }
}
