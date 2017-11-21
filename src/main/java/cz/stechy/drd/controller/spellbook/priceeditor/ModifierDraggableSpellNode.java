package cz.stechy.drd.controller.spellbook.priceeditor;

import com.jfoenix.controls.JFXComboBox;
import cz.stechy.drd.R;
import cz.stechy.drd.model.ITranslatedEnum;
import cz.stechy.drd.model.spell.price.ISpellPrice;
import cz.stechy.drd.model.spell.price.modifier.SpellPriceAdder;
import cz.stechy.drd.model.spell.price.modifier.SpellPriceDivider;
import cz.stechy.drd.model.spell.price.modifier.SpellPriceMultiplier;
import cz.stechy.drd.model.spell.price.modifier.SpellPriceSubtracter;
import cz.stechy.drd.util.Translator;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;

public class ModifierDraggableSpellNode extends DraggableSpellNode {

    // region Variables

    private JFXComboBox<Operation> cmbOperation;

    // endregion

    // region Constructors

    ModifierDraggableSpellNode(INodeManipulator nodeManipulator,
        ILinkListener linkListener,
        Translator translator) {
        super(nodeManipulator, linkListener, translator);
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

        showLeftRightCircles();
    }

    @Override
    public ISpellPrice getPrice() {
        switch (cmbOperation.getValue()) {
            case ADD:
                return new SpellPriceAdder(leftNode.getPrice(), rightNode.getPrice());
            case SUBTRACT:
                return new SpellPriceSubtracter(leftNode.getPrice(), rightNode.getPrice());
            case MULTIPLE:
                return new SpellPriceMultiplier(leftNode.getPrice(), rightNode.getPrice());
            case DIVIDE:
                return new SpellPriceDivider(leftNode.getPrice(), rightNode.getPrice());
            default:
                throw new IllegalStateException("Tohle by nikdy nemelo nastat");
        }
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
