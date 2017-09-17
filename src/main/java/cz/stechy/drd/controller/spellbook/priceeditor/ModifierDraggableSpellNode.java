package cz.stechy.drd.controller.spellbook.priceeditor;

import com.jfoenix.controls.JFXComboBox;
import cz.stechy.drd.R;
import cz.stechy.drd.model.ITranslatedEnum;
import cz.stechy.drd.util.Translator;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;

public class ModifierDraggableSpellNode extends DraggableSpellNode {

    // region Variables

    private JFXComboBox<Operation> cmbOperation;

    ModifierDraggableSpellNode(Translator translator) {
        super(translator);
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        cmbOperation = new JFXComboBox<>();
        cmbOperation.setLabelFloat(true);
        cmbOperation.setPromptText(translator.translate(R.Translate.SPELL_PRICE_MODIFIER_TYPE));

        lblTitle.textProperty().bind(Bindings.concat(
            translator.translate(R.Translate.SPELL_PRICE_TYPE_MODIFIER),
            Bindings.createStringBinding(() -> {
                final Operation operation = cmbOperation.getSelectionModel().selectedItemProperty()
                    .get();
                if (operation == null) {
                    return "";
                }

                return " - " + translator.translate(operation.getKeyForTranslation());
            }, cmbOperation.getSelectionModel().selectedItemProperty())
        ));

        cmbOperation.getItems().setAll(Operation.values());
        container.getChildren().setAll(cmbOperation);

        circleLeftLink.setVisible(true);
        circleRightLink.setVisible(true);
    }

    private enum Operation implements ITranslatedEnum {
        ADD(R.Translate.SPELL_PRICE_MODIFIER_TYPE_ADDER, "+"),
        SUBTRACT(R.Translate.SPELL_PRICE_MODIFIER_TYPE_SUBTRACTER, "-"),
        MULTIPLE(R.Translate.SPELL_PRICE_MODIFIER_TYPE_MULTIPLIER, "*"),
        DIVIDE(R.Translate.SPELL_PRICE_MODIFIER_TYPE_DIVIDER, "/");

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
