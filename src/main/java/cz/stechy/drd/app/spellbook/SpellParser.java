package cz.stechy.drd.app.spellbook;

import cz.stechy.drd.model.spell.price.BasicSpellPrice;
import cz.stechy.drd.model.spell.price.ISpellPrice;
import cz.stechy.drd.model.spell.price.VariableSpellPrice;
import cz.stechy.drd.model.spell.price.modifier.SpellPriceAdder;
import cz.stechy.drd.model.spell.price.modifier.SpellPriceDivider;
import cz.stechy.drd.model.spell.price.modifier.SpellPriceMultiplier;
import cz.stechy.drd.model.spell.price.modifier.SpellPriceSubtracter;
import java.util.regex.Pattern;

/**
 * Pomocná knihovní třída pro parsování ceny kouzla
 */
public final class SpellParser {

    // region Constants

    private static final char OPEN_SPELL_PRICE_CHARACTER = '[';
    private static final char CLOSE_SPELL_PRICE_CHARACTER = ']';

    private static final char OPEN_MODIFIER_CHARACTER = '<';
    private static final char CLOSE_MODIFIER_CHARACTER = '>';

    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]");

    // endregion

    // region Variables

    private final SimpleReader reader;

    // endregion

    // region Constructors

    /**
     * Vytvoří nový parser pro zadaný zdrojový text
     *
     * @param source Textová reprezentace ceny kouzla vytvořena metodou {@link ISpellPrice#pack()}
     */
    public SpellParser(String source) {
        this.reader = new SimpleReader(source);
    }

    // endregion

    // region Private methods

    /**
     * Naparsuje cenu z formátu: [0:5|8] nebo: [1:5]
     *
     * @return {@link ISpellPrice}
     */
    private ISpellPrice parsePrice() {
        reader.next();
        int variableType = readNumber();
        reader.next();
        int variableValue = readNumber();
        int extention = 0;
        if (reader.peek() == '|') {
            reader.next();
            extention = readNumber();
        }

        switch (variableType) {
            case 0:
                return new BasicSpellPrice(variableValue, extention);
            case 1:
                return new VariableSpellPrice(VariableSpellPrice.VariableType.values()[variableValue]);
            default:
                return null;
        }
    }

    private ISpellPrice parseModifier() {
        char ch = reader.next();
        ISpellPrice left, right;
        char operator;
        left = parse(ch);
        operator = reader.next();
        while(operator == '>') {
            operator = reader.next();
        }
        ch = reader.next();
        right = parse(ch);
        switch (operator) {
            case '+':
                return new SpellPriceAdder(left, right);
            case '-':
                return new SpellPriceSubtracter(left, right);
            case '*':
                return new SpellPriceMultiplier(left, right);
            case '/':
                return new SpellPriceDivider(left, right);
            default:
                throw new IllegalStateException("Nevalidní znak");
        }
    }

    private int readNumber() {
        StringBuilder builder = new StringBuilder();
        char ch = reader.peek();
        while(NUMBER_PATTERN.matcher("" + ch).matches()) {
            builder.append(ch);
            ch = reader.next();
        }

        return Integer.parseInt(builder.toString());
    }

    private ISpellPrice parse(char ch) {
        switch (ch) {
            case OPEN_SPELL_PRICE_CHARACTER:
                return parsePrice();
            case OPEN_MODIFIER_CHARACTER:
                return parseModifier();
            default:
                throw new IllegalStateException("Nevalidní znak");
        }
    }

    // endregion

    // region Public methods

    /**
     * Metoda naparsuje textovou reprezentaci ceny kouzla a vytvoří podpovídající instanci
     *
     * @return {@link ISpellPrice} Instance odpovídající textové reprezentaci
     */
    public ISpellPrice parse() {
        char ch = reader.next();
        return parse(ch);
    }

    // endregion

    public enum SpellPriceType {
        CONSTANT, VARIABLE
    }

    private static final class SimpleReader {
        final String source;

        int index = 0;

        SimpleReader(String source) {
            this.source = source;
        }

        char next() {
            return source.charAt(index++);
        }
        char peek() {
            return source.charAt(index - 1);
        }

        boolean eol() {
            return index == source.length();
        }

    }
}
