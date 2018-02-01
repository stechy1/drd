package cz.stechy.drd.widget;

import cz.stechy.drd.R;
import cz.stechy.drd.model.spell.price.ISpellPrice;
import cz.stechy.drd.model.spell.price.VariableSpellPrice.VariableType;
import cz.stechy.drd.util.Translator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Třída reprezentující textovou podobu ceny kouzla
 */
public class SpellPriceWidget extends VBox {

    // region Constants

    private static final String TEXT_FORMAT = "%s: %s";

    private static final String PRICE_KEY = R.Translate.SPELL_PRICE;
    private static final String EXTENTION_KEY = R.Translate.SPELL_PRICE_EXTENTION;

    // endregion

    // region Variables

    private final Text txtPrice = new Text();
    private final Text txtExtention = new Text();

    private ObjectProperty<ISpellPrice> spellPrice;
    private Translator translator;

    // endregion

    {
        getChildren().setAll(txtPrice, txtExtention);
    }

    // region Private methods

    private void init() {
        final String translatedPrice = translator.translate(PRICE_KEY);
        final String translatedExtention = translator.translate(EXTENTION_KEY);

        txtPrice.textProperty().bind(Bindings.createStringBinding(() -> {
            final ISpellPrice price = spellPrice.get();
            if (price == null) {
                return "";
            }

            String priceText = price.toString();
            for (VariableType variableType : VariableType.values()) {
                final String key = variableType.getKeyForTranslation();
                priceText = priceText.replace(key, translator.translate(key));
            }

            return String.format(TEXT_FORMAT, translatedPrice, priceText);
        }, spellPrice));
        txtExtention.textProperty().bind(Bindings.createStringBinding(() -> {
            final ISpellPrice price = spellPrice.get();
            if (price == null) {
                return "";
            }

            final int extention = price.calculateExtention();
            if (extention == 0) {
                return "";
            }

            return String.format(TEXT_FORMAT, translatedExtention, extention);
        }, spellPrice));

//        txtPrice.setText(String.format(TEXT_FORMAT, translatedPrice, priceText));
//
//        final int extention = spellPrice.calculateExtention();
//        if (extention == 0) {
//            txtExtention.setVisible(false);
//            txtExtention.setManaged(false);
//        } else {
//            txtExtention.setVisible(false);
//            txtExtention.setManaged(false);
//            txtExtention.setText(String.format(TEXT_FORMAT, translatedExtention, extention));
//        }
    }

    // endregion

    // region Public methods

    public void bind(ObjectProperty<ISpellPrice> spellPrice, Translator translator) {
        this.spellPrice = spellPrice;
        this.translator = translator;
        unbind();
        init();
    }

    public void unbind() {

    }

    // endregion

}
