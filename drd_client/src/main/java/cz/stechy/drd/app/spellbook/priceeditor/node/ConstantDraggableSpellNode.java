package cz.stechy.drd.app.spellbook.priceeditor.node;

import com.jfoenix.controls.JFXTextField;
import cz.stechy.drd.R;
import cz.stechy.drd.app.spellbook.priceeditor.ILinkListener;
import cz.stechy.drd.app.spellbook.priceeditor.INodeManipulator;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.spell.price.BasicSpellPrice;
import cz.stechy.drd.model.spell.price.ISpellPrice;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.drd.util.Translator;
import java.net.URL;
import java.util.ResourceBundle;

public class ConstantDraggableSpellNode extends DraggableSpellNode {

    // region Variables

    private MaxActValue value;
    private MaxActValue valueExtention;

    // endregion

    // region Constructors

    public ConstantDraggableSpellNode(INodeManipulator nodeManipulator, ILinkListener linkListener,
        Translator translator) {
        super(nodeManipulator, linkListener, translator);
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        JFXTextField inputValue = new JFXTextField();
        JFXTextField inputExtention = new JFXTextField();
        value = new MaxActValue(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
        valueExtention = new MaxActValue(0, Integer.MAX_VALUE, 0);
        inputValue.setLabelFloat(true);
        inputExtention.setLabelFloat(true);
        inputValue.setPromptText(translator.translate(R.Translate.SPELL_PRICE));
        inputExtention.setPromptText(translator.translate(R.Translate.SPELL_PRICE_EXTENTION));

        lblTitle.setText(translator.translate(R.Translate.SPELL_PRICE_TYPE_CONSTANT));

        FormUtils.initTextFormater(inputValue, value);
        FormUtils.initTextFormater(inputExtention, valueExtention);

        container.getChildren().setAll(inputValue, inputExtention);
        anchorRoot.setPrefHeight(150);
    }

    @Override
    public void initValues(ISpellPrice spellPrice) {
        if (!(spellPrice instanceof BasicSpellPrice)) {
            return;
        }

        BasicSpellPrice price = (BasicSpellPrice) spellPrice;
        value.setActValue(price.calculateMainPrice());
        valueExtention.setActValue(price.calculateExtention());
    }

    @Override
    public ISpellPrice getPrice() {
        return new BasicSpellPrice(value.getActValue().intValue(),
            valueExtention.getActValue().intValue());
    }
}
