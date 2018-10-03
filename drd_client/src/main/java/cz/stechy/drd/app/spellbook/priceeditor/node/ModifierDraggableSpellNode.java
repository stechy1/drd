package cz.stechy.drd.app.spellbook.priceeditor.node;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXComboBox;
import cz.stechy.drd.R;
import cz.stechy.drd.app.spellbook.priceeditor.ILinkListener;
import cz.stechy.drd.app.spellbook.priceeditor.INodeManipulator;
import cz.stechy.drd.model.spell.price.ISpellPrice;
import cz.stechy.drd.model.spell.price.ModifierPrice;
import cz.stechy.drd.model.spell.price.ModifierPrice.ModifierType;
import cz.stechy.drd.model.spell.price.modifier.SpellPriceAdder;
import cz.stechy.drd.model.spell.price.modifier.SpellPriceDivider;
import cz.stechy.drd.model.spell.price.modifier.SpellPriceMultiplier;
import cz.stechy.drd.model.spell.price.modifier.SpellPriceSubtracter;
import cz.stechy.drd.service.translator.ITranslatorService;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;

public class ModifierDraggableSpellNode extends DraggableSpellNode {

    // region Variables

    private JFXComboBox<ModifierType> cmbOperation;

    // endregion

    // region Constructors

    @Inject
    public ModifierDraggableSpellNode(INodeManipulator nodeManipulator, ILinkListener linkListener, ITranslatorService translator) {
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
                final ModifierType modifierType = cmbOperation.getSelectionModel()
                    .selectedItemProperty()
                    .get();
                if (modifierType == null) {
                    return "";
                }

                return " - " + translator.translate(modifierType.getKeyForTranslation());
            }, cmbOperation.getSelectionModel().selectedItemProperty())
        ));

        cmbOperation.getItems().setAll(ModifierType.values());
        container.getChildren().setAll(cmbOperation);

        showLeftRightCircles();
    }

    @Override
    public void initValues(ISpellPrice spellPrice) {
        if (!(spellPrice instanceof ModifierPrice)) {
            return;
        }

        ModifierPrice price = (ModifierPrice) spellPrice;
        cmbOperation.getSelectionModel().select(price.getType());

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
}
