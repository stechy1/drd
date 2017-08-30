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
            attackNumber.actValueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_ATTACK_NUMBER, true);
                } else {
                    setValidityFlag(FLAG_ATTACK_NUMBER, false);
                }
                System.out.println(valid);
            });
            defenceNumber.actValueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_DEFENCE_NUMBER, true);
                } else {
                    setValidityFlag(FLAG_DEFENCE_NUMBER, false);
                }
                System.out.println(valid);
            });
            viability.actValueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_VIABILITY, true);
                } else {
                    setValidityFlag(FLAG_VIABILITY, false);
                }
                System.out.println(valid);
            });
            immunity.actValueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_IMMUNITY, true);
                } else {
                    setValidityFlag(FLAG_IMMUNITY, false);
                }
                System.out.println(valid);
            });
            mettle.actValueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_METTLE, true);
                } else {
                    setValidityFlag(FLAG_METTLE, false);
                }
                System.out.println(valid);
            });
            intelligence.actValueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_INTELLIGENCE, true);
                } else {
                    setValidityFlag(FLAG_INTELLIGENCE, false);
                }
                System.out.println(valid);
            });
            charisma.actValueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_CHARISMA, true);
                } else {
                    setValidityFlag(FLAG_CHARISMA, false);
                }
                System.out.println(valid);
            });

            // Automatické nastavení validity na true
            validityFlag.set(0);
            setValid(true);
        }
    }
}
