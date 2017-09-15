package cz.stechy.drd.controller.spellbook.priceeditor;

import com.jfoenix.controls.JFXComboBox;
import cz.stechy.drd.R;
import cz.stechy.drd.model.ITranslatedEnum;
import cz.stechy.drd.util.Translator;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.ComboBox;

public class ModifierDraggableSpellNode extends DraggableSpellNode {

    // region Variables

    private ComboBox<Operation> cmbOperation;

    public ModifierDraggableSpellNode(Translator translator, double startX, double startY) {
        super(translator, startX, startY);
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
         cmbOperation = new JFXComboBox<>();

        lblTitle.setText(translator.translate(R.Translate.SPELL_PRICE_TYPE_MODIFIER));

        cmbOperation.getItems().setAll(Operation.values());

        circleLeftLink.setVisible(true);
        circleRightLink.setVisible(true);
    }

    private enum Operation implements ITranslatedEnum {
        ADD(R.Translate.SPELL_PRICE, "+"),
        SUBTRACT(R.Translate.SPELL_PRICE, "-"),
        MULTIPLE(R.Translate.SPELL_PRICE, "*"),
        DIVIDE(R.Translate.SPELL_PRICE, "/");

        private final String key;
        private final String operator;

        Operation(String key, String operator) {
            this.key = key;
            this.operator = operator;
        }

        @Override
        public String getKeyForTranslation() {
            return key;
        }

        @Override
        public String toString() {
            return operator;
        }
    }

}
