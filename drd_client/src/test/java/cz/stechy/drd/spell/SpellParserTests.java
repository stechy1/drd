package cz.stechy.drd.spell;

import static org.junit.Assert.assertEquals;

import cz.stechy.drd.model.SpellParser;
import cz.stechy.drd.model.spell.price.ISpellPrice;
import org.junit.Test;

/**
 * Třída obsahující testy zaměřující se parsování textové podoby ceny
 */
public class SpellParserTests {

    @Test
    public void basicParserTest() throws Exception {
        final String source = "[0:1]";
        final SpellParser parser = new SpellParser(source);
        final ISpellPrice spellPrice = parser.parse();

        assertEquals("Chyba, parser špatně naparsoval cenu kouzla.", 1,
            spellPrice.calculateMainPrice());
    }

    @Test
    public void basicParserWithExtentionTest() throws Exception {
        final String source = "[0:1|5]";
        final SpellParser parser = new SpellParser(source);
        final ISpellPrice spellPrice = parser.parse();

        assertEquals("Chyba, parser špatně naparsoval cenu kouzla.", 1,
            spellPrice.calculateMainPrice());
        assertEquals("Chyba, parser špatně naparsoval cenu pro rozšíření kouzla.", 5,
            spellPrice.calculateExtention());
    }

    @Test
    public void advancedParserWithAdderTest() throws Exception {
        final String source = "<[0:9|0]+[0:8|0]>";
        final SpellParser parser = new SpellParser(source);
        final ISpellPrice spellPrice = parser.parse();

        assertEquals("Chyba, parser špatně naparsoval sčítačku ceny kouzla.", 17,
            spellPrice.calculateMainPrice());
    }

    @Test
    public void advancedParserWithSubtracterTest() throws Exception {
        final String source = "<[0:9|0]-[0:8|0]>";
        final SpellParser parser = new SpellParser(source);
        final ISpellPrice spellPrice = parser.parse();

        assertEquals("Chyba, parser špatně naparsoval odčítačku ceny kouzla.", 1,
            spellPrice.calculateMainPrice());
    }

    @Test
    public void advancedParserWithMultiplierTest() throws Exception {
        final String source = "<[0:9|0]*[0:8|0]>";
        final SpellParser parser = new SpellParser(source);
        final ISpellPrice spellPrice = parser.parse();

        assertEquals("Chyba, parser špatně naparsoval násobičku ceny kouzla.", 72,
            spellPrice.calculateMainPrice());
    }

    @Test
    public void advancedParserWithDividerTest() throws Exception {
        final String source = "<[0:8|0]/[0:4|0]>";
        final SpellParser parser = new SpellParser(source);
        final ISpellPrice spellPrice = parser.parse();

        assertEquals("Chyba, parser špatně naparsoval děličku ceny kouzla.", 2,
            spellPrice.calculateMainPrice());
    }

    @Test
    public void advancedParserWithMultipleModifierTest1() throws Exception {
        final String source = "<<[0:5|0]+[0:6|0]>*[0:2|0]>";
        final SpellParser parser = new SpellParser(source);
        final ISpellPrice spellPrice = parser.parse();

        assertEquals("Chyba, parser špatně naparsoval modifikátory ceny kouzla.", 22,
            spellPrice.calculateMainPrice());
    }

    @Test
    public void advancedParserWithMultipleModifierTest2() throws Exception {
        final String source = "<<[0:5|0]+[0:6|0]>*<[0:3|0]-[0:2|0]>>";
        final SpellParser parser = new SpellParser(source);
        final ISpellPrice spellPrice = parser.parse();

        assertEquals("Chyba, parser špatně naparsoval modifikátory ceny kouzla.", 11,
            spellPrice.calculateMainPrice());
    }
}
