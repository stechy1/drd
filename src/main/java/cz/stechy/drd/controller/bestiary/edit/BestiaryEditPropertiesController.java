package cz.stechy.drd.controller.bestiary.edit;

import cz.stechy.drd.controller.bestiary.BestiaryHelper;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.ValidatedModel;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

/**
 * Kontroler pro editaci důležitých vlastností nestvůry
 */
public class BestiaryEditPropertiesController implements IEditController, Initializable {

    // region Variables

    // region FXML

    @FXML
    private TextField txtAttackNumber;
    @FXML
    private TextField txtDefenceNumber;
    @FXML
    private TextField txtViability;
    @FXML
    private TextField txtImmunity;
    @FXML
    private TextField txtMettle;
    @FXML
    private TextField txtIntelligence;
    @FXML
    private TextField txtCharisma;

    // endregion

    private final Model model = new Model();

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FormUtils.initTextFormater(txtAttackNumber, model.attackNumber);
        FormUtils.initTextFormater(txtDefenceNumber, model.defenceNumber);
        FormUtils.initTextFormater(txtViability, model.viability);
        FormUtils.initTextFormater(txtImmunity, model.immunity);
        FormUtils.initTextFormater(txtMettle, model.mettle);
        FormUtils.initTextFormater(txtIntelligence, model.intelligence);
        FormUtils.initTextFormater(txtCharisma, model.charisma);
    }

    @Override
    public void loadMobPropertiesFromBundle(Bundle bundle) {
        model.attackNumber.setActValue(bundle.getInt(BestiaryHelper.ATTACK));
        model.defenceNumber.setActValue(bundle.getInt(BestiaryHelper.DEFENCE));
        model.viability.setActValue(bundle.getInt(BestiaryHelper.VIABILITY));
        model.immunity.setActValue(bundle.getInt(BestiaryHelper.IMMUNITY));
        model.mettle.setActValue(bundle.getInt(BestiaryHelper.METTLE));
        model.intelligence.setActValue(bundle.getInt(BestiaryHelper.INTELLIGENCE));
        model.charisma.setActValue(bundle.getInt(BestiaryHelper.CHARISMA));
    }

    @Override
    public void saveMobPropertiesToBundle(Bundle bundle) {
        bundle.putInt(BestiaryHelper.ATTACK, model.attackNumber.getActValue().intValue());
        bundle.putInt(BestiaryHelper.DEFENCE, model.defenceNumber.getActValue().intValue());
        bundle.putInt(BestiaryHelper.VIABILITY, model.viability.getActValue().intValue());
        bundle.putInt(BestiaryHelper.IMMUNITY, model.immunity.getActValue().intValue());
        bundle.putInt(BestiaryHelper.METTLE, model.mettle.getActValue().intValue());
        bundle.putInt(BestiaryHelper.INTELLIGENCE, model.intelligence.getActValue().intValue());
        bundle.putInt(BestiaryHelper.CHARISMA, model.charisma.getActValue().intValue());
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return model.validProperty();
    }

    private static class Model extends ValidatedModel {
        private static final int FLAG_ATTACK_NUMBER = 1 << 0;
        private static final int FLAG_DEFENCE_NUMBER = 1 << 1;
        private static final int FLAG_VIABILITY = 1 << 2;
        private static final int FLAG_IMMUNITY = 1 << 3;
        private static final int FLAG_METTLE = 1 << 4;
        private static final int FLAG_INTELLIGENCE = 1 << 5;
        private static final int FLAG_CHARISMA = 1 << 6;

        private final MaxActValue attackNumber = new MaxActValue(Integer.MAX_VALUE);
        private final MaxActValue defenceNumber = new MaxActValue(Integer.MAX_VALUE);
        private final MaxActValue viability = new MaxActValue(Integer.MAX_VALUE);
        private final MaxActValue immunity = new MaxActValue(Integer.MAX_VALUE);
        private final MaxActValue mettle = new MaxActValue(Integer.MAX_VALUE);
        private final MaxActValue intelligence = new MaxActValue(Integer.MAX_VALUE);
        private final MaxActValue charisma = new MaxActValue(Integer.MAX_VALUE);

        {
            attackNumber.actValueProperty().addListener(FormUtils.notEmptyCondition(this, FLAG_ATTACK_NUMBER));
            defenceNumber.actValueProperty().addListener(FormUtils.notEmptyCondition(this, FLAG_DEFENCE_NUMBER));
            viability.actValueProperty().addListener(FormUtils.notEmptyCondition(this, FLAG_VIABILITY));
            immunity.actValueProperty().addListener(FormUtils.notEmptyCondition(this, FLAG_IMMUNITY));
            mettle.actValueProperty().addListener(FormUtils.notEmptyCondition(this, FLAG_METTLE));
            intelligence.actValueProperty().addListener(FormUtils.notEmptyCondition(this, FLAG_INTELLIGENCE));
            charisma.actValueProperty().addListener(FormUtils.notEmptyCondition(this, FLAG_CHARISMA));

            // Automatické nastavení validity na true
            validityFlag.set(ValidatedModel.VALID_FLAG_VALUE);
        }
    }
}
