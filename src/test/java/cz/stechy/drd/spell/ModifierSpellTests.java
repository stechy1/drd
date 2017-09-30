package cz.stechy.drd.spell;

import static org.junit.Assert.assertEquals;

import cz.stechy.drd.model.spell.parser.SpellParser.SpellVariable;
import cz.stechy.drd.model.spell.price.ISpellPrice;
import cz.stechy.drd.model.spell.price.VariableSpellPrice;
import cz.stechy.drd.model.spell.price.modifier.SpellPriceAdder;
import cz.stechy.drd.model.spell.price.modifier.SpellPriceDivider;
import cz.stechy.drd.model.spell.price.modifier.SpellPriceMultiplier;
import cz.stechy.drd.model.spell.price.modifier.SpellPriceSubtracter;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 * Třída obsahující testy zaměřující se na skládání ceny pomocí základních operátorů: +, -, *, /
 * a jejich kombinace
 */
public class ModifierSpellTests {

    @Test
    public void modifierAdderWithBasicSpellPriceTest() throws Exception {
        final int left = 1;
        final int right = 2;
        final String stringRepresentation = String.format("%d + %d", left, right);
        final ISpellPrice spellPrice = new SpellPriceAdder(left, right);

        assertEquals("Chyba, výstup metody toString neodpovídá očekávanému řetězci.", stringRepresentation, spellPrice.toString());
        assertEquals("Chyba, sčítačka ceny kouzla špatně vypočetla cenu.", left + right, spellPrice.calculateMainPrice());
    }

    @Test
    public void modifierAdderWithVariableSpellPriceTest() throws Exception {
        final int left = 1;
        final int right = 2;
        final SpellVariable spellVariable = SpellVariable.VIABILITY;
        final String stringRepresentation = String.format("%s + %d", spellVariable.getKeyForTranslation(), right);
        final Map<String, Integer> modifiers = new HashMap<>();
        modifiers.put(VariableSpellPrice.buildKey(spellVariable.name()), left);
        final ISpellPrice spellPrice = new SpellPriceAdder(new VariableSpellPrice(spellVariable), right);

        assertEquals("Chyba, výstup metody toString neodpovídá očekávanému řetězci.", stringRepresentation, spellPrice.toString());
        assertEquals("Chyba, sčítačka špatně vypočetla cenu kouzla.", left + right, spellPrice.calculateMainPrice(modifiers));
    }

    @Test
    public void modifierAdderWithDoubleVariableSpellTest() throws Exception {
        final SpellVariable spellVariable1 = SpellVariable.VIABILITY;
        final SpellVariable spellVariable2 = SpellVariable.STRENGTH;
        final int value1 = 1;
        final int value2 = 2;
        final String stringRepresentation = String.format("%s + %s", spellVariable1.getKeyForTranslation(), spellVariable2.getKeyForTranslation());
        final Map<String, Integer> modifiers = new HashMap<>();
        modifiers.put(VariableSpellPrice.buildKey(spellVariable1.name()), value1);
        modifiers.put(VariableSpellPrice.buildKey(spellVariable2.name()), value2);
        final ISpellPrice spellPrice = new SpellPriceAdder(new VariableSpellPrice(spellVariable1), new VariableSpellPrice(spellVariable2));

        assertEquals("Chyba, výstup metody toString neodpovídá očekávanému řetězci.", stringRepresentation, spellPrice.toString());
        assertEquals("Chyba, sčítačka špatně vypočetla cenu kouzla.", value1 + value2, spellPrice.calculateMainPrice(modifiers));
    }

    @Test
    public void modifierSubtracterWithBasicSpellPriceTest() throws Exception {
        final int left = 1;
        final int right = 2;
        final String stringRepresentation = String.format("%d - %d", left, right);
        final ISpellPrice spellPrice = new SpellPriceSubtracter(left, right);

        assertEquals("Chyba, výstup metody toString neodpovídá očekávanému řetězci.", stringRepresentation, spellPrice.toString());
        assertEquals("Chyba, odčítačka špatně vypočetla cenu kouzla.", left - right, spellPrice.calculateMainPrice());
    }

    @Test
    public void modifierSubtracterWithVariableSpellPriceTest() throws Exception {
        final int left = 1;
        final int right = 2;
        final SpellVariable spellVariable = SpellVariable.VIABILITY;
        final String stringRepresentation = String.format("%s - %d", spellVariable.getKeyForTranslation(), right);
        final Map<String, Integer> modifiers = new HashMap<>();
        modifiers.put(VariableSpellPrice.buildKey(spellVariable.name()), left);
        final ISpellPrice spellPrice = new SpellPriceSubtracter(new VariableSpellPrice(spellVariable), right);

        assertEquals("Chyba, výstup metody toString neodpovídá očekávanému řetězci.", stringRepresentation, spellPrice.toString());
        assertEquals("Chyba, odčítačka špatně vypočetla cenu kouzla.", left - right, spellPrice.calculateMainPrice(modifiers));
    }

    @Test
    public void modifierMultiplierWithBasicSpellPriceTest() throws Exception {
        final int left = 1;
        final int right = 2;
        final String stringRepresentation = String.format("(%d * %d)", left, right);
        final ISpellPrice spellPrice = new SpellPriceMultiplier(left, right);

        assertEquals("Chyba, výstup metody toString neodpovídá očekávanému řetězci.", stringRepresentation, spellPrice.toString());
        assertEquals("Chyba, násobička špatně vypočetla cenu kouzla.", left * right, spellPrice.calculateMainPrice());
    }

    @Test
    public void modifierMultiplierWithVariableSpellPriceTest() throws Exception {
        final int left = 1;
        final int right = 2;
        final SpellVariable spellVariable = SpellVariable.VIABILITY;
        final String stringRepresentation = String.format("(%s * %d)",
            spellVariable.getKeyForTranslation(), right);
        final Map<String, Integer> modifiers = new HashMap<>();
        modifiers.put(VariableSpellPrice.buildKey(spellVariable.name()), left);
        final ISpellPrice spellPrice = new SpellPriceMultiplier(new VariableSpellPrice(spellVariable), right);

        assertEquals("Chyba, výstup metody toString neodpovídá očekávanému řetězci.", stringRepresentation, spellPrice.toString());
        assertEquals("Chyba, násobička špatně vypočetla cenu kouzla.", left * right, spellPrice.calculateMainPrice(modifiers));
    }

    @Test
    public void modifierDividerWithBasicSpellPriceTest() throws Exception {
        final int left = 1;
        final int right = 2;
        final String stringRepresentation = String.format("(%d / %d)", left, right);
        final ISpellPrice spellPrice = new SpellPriceDivider(left, right);

        assertEquals("Chyba, výstup metody toString neodpovídá očekávanému řetězci.", stringRepresentation, spellPrice.toString());
        assertEquals("Chyba, dělička špatně vypočetla cenu kouzla.", left / right, spellPrice.calculateMainPrice());
    }

    @Test
    public void modifierDividerWithVariableSpellPriceTest() throws Exception {
        final int left = 1;
        final int right = 2;
        final SpellVariable spellVariable = SpellVariable.VIABILITY;
        final String stringRepresentation = String.format("(%s / %d)",
            spellVariable.getKeyForTranslation(), right);
        final Map<String, Integer> modifiers = new HashMap<>();
        modifiers.put(VariableSpellPrice.buildKey(spellVariable.name()), left);
        final ISpellPrice spellPrice = new SpellPriceDivider(new VariableSpellPrice(spellVariable), right);

        assertEquals("Chyba, výstup metody toString neodpovídá očekávanému řetězci.", stringRepresentation, spellPrice.toString());
        assertEquals("Chyba, dělička špatně vypočetla cenu kouzla.", left / right, spellPrice.calculateMainPrice(modifiers));
    }

    @Test
    public void modifierAdderWithMultipleModifiers() throws Exception {
        final int left = 2;
        final int rightLeft = 3;
        final int right = 4;
        final String stringRepresentation = String.format("%d + %d + %d", left, rightLeft, right);
        final ISpellPrice spellPrice = new SpellPriceAdder(left, new SpellPriceAdder(rightLeft, right));

        assertEquals("Chyba, metoda toString vrátila řetězec ve špatném formátu.", stringRepresentation, spellPrice.toString());
        assertEquals("Chyba, cena byla špatně vypočítána.", left + rightLeft + right, spellPrice.calculateMainPrice());
    }

    @Test
    public void modifierMultiplierWithMultipleModifiers() throws Exception {
        final int left = 2;
        final int rightLeft = 3;
        final int right = 4;
        final String stringRepresentation = String.format("(%d * %d + %d)", left, rightLeft, right);
        final ISpellPrice spellPrice = new SpellPriceMultiplier(left, new SpellPriceAdder(rightLeft, right));

        assertEquals("Chyba, metoda toString vrátila řetězec ve špatném formátu.", stringRepresentation, spellPrice.toString());
        assertEquals("Chyba, cena byla špatně vypočítána.", left * (rightLeft + right), spellPrice.calculateMainPrice());
    }
}
