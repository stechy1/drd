package cz.stechy.drd.controller.spellbook.priceeditor;

import com.jfoenix.controls.JFXTextField;
import cz.stechy.drd.R;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.drd.util.Translator;
import java.net.URL;
import java.util.ResourceBundle;

class ConstantDraggableSpellNode extends DraggableSpellNode {

    // region Variables

    private JFXTextField inputValue;
    private JFXTextField inputExtention;
    private MaxActValue value;
    private MaxActValue valueExtention;

    // endregion

    // region Constructors

    ConstantDraggableSpellNode(Translator translator) {
        super(translator);
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inputValue = new JFXTextField();
        inputExtention = new JFXTextField();
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
}
