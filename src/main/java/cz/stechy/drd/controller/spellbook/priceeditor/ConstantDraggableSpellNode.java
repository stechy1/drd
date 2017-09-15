package cz.stechy.drd.controller.spellbook.priceeditor;

import com.jfoenix.controls.JFXTextField;
import cz.stechy.drd.R;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.drd.util.Translator;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.TextField;

public class ConstantDraggableSpellNode extends DraggableSpellNode {

    // region Variables

    private TextField input;
    private MaxActValue value;


    // endregion

    // region Constructors

    public ConstantDraggableSpellNode(Translator translator, double startX, double startY) {
        super(translator, startX, startY);
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        input = new JFXTextField();
        value = new MaxActValue(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);

        lblTitle.setText(translator.translate(R.Translate.SPELL_PRICE_TYPE_CONSTANT));

        FormUtils.initTextFormater(input, value);

        container.getChildren().setAll(input);
    }
}
