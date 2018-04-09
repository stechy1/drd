package cz.stechy.drd.app.bestiary.edit;

import cz.stechy.drd.R;
import cz.stechy.drd.app.InjectableChild;
import cz.stechy.drd.app.VulnerabilityController;
import cz.stechy.drd.app.bestiary.BestiaryHelper;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.ValidatedModel;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;

/**
 * Kontroler pro editaci ostatních vlastností nestvůry
 */
public class BestiaryBestiaryEditOtherController implements InjectableChild,
    IBestiaryEditController,
    Initializable {

    // region Constants

    private static final int ACTION_VULNERABILITY = 0;

    // endregion

    // region Variables

    // region FXML

    @FXML
    private Hyperlink linkVulnerability;
    @FXML
    private TextField txtMobility;
    @FXML
    private TextField txtPerservance;
    @FXML
    private TextField txtControlAbility;
    @FXML
    private TextField txtBasicPowerOfMind;
    @FXML
    private TextField txtDomestication;

    // endregion

    private final Model model = new Model();
    private BaseController baseController;

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FormUtils.initTextFormater(txtMobility, model.mobility);
        FormUtils.initTextFormater(txtPerservance, model.perservance);
        FormUtils.initTextFormater(txtControlAbility, model.controlAbility);
        FormUtils.initTextFormater(txtBasicPowerOfMind, model.basicPowerOfMind);
        FormUtils.initTextFormater(txtDomestication, model.domestication);
    }

    @Override
    public void onScreenResult(int statusCode, int actionId, Bundle bundle) {
        switch (actionId) {
            case ACTION_VULNERABILITY:
                if (statusCode != BaseController.RESULT_SUCCESS) {
                    return;
                }

                model.vulnerability.setValue(bundle.getInt(VulnerabilityController.VULNERABILITY));
                break;
        }
    }

    @Override
    public void injectParent(BaseController parent) {
        this.baseController = parent;
    }

    @Override
    public void loadMobPropertiesFromBundle(Bundle bundle) {
        model.vulnerability
            .setValue(bundle.getInt(BestiaryHelper.VULNERABILITY));
        model.mobility.setActValue(bundle.getInt(BestiaryHelper.MOBILITY));
        model.perservance.setActValue(bundle.getInt(BestiaryHelper.PERSERVANCE));
        model.controlAbility.setActValue(bundle.getInt(BestiaryHelper.CONTROL_ABILITY));
        model.basicPowerOfMind.setActValue(bundle.getInt(BestiaryHelper.BASIC_BOWER_OF_MIND));
        model.domestication.setActValue(bundle.getInt(BestiaryHelper.DOMESTICATION));
    }

    @Override
    public void saveMobPropertiesToBundle(Bundle bundle) {
        bundle.putInt(BestiaryHelper.VULNERABILITY, model.vulnerability.getValue());
        bundle.putInt(BestiaryHelper.MOBILITY, model.mobility.getActValue().intValue());
        bundle.putInt(BestiaryHelper.PERSERVANCE, model.perservance.getActValue().intValue());
        bundle
            .putInt(BestiaryHelper.CONTROL_ABILITY, model.controlAbility.getActValue().intValue());
        bundle.putInt(BestiaryHelper.BASIC_BOWER_OF_MIND,
            model.basicPowerOfMind.getActValue().intValue());
        bundle.putInt(BestiaryHelper.DOMESTICATION, model.domestication.getActValue().intValue());
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return model.validProperty();
    }

    // region Button handlers

    @FXML
    private void handleShowVulnerabilityPopup(ActionEvent actionEvent) {
        Bundle bundle = new Bundle()
            .putInt(VulnerabilityController.VULNERABILITY, model.vulnerability.getValue());
        baseController.startNewPopupWindowForResult(R.FXML.VULNERABILITY, ACTION_VULNERABILITY,
            bundle, (Node) actionEvent.getSource());
    }

    // endregion

    private static final class Model extends ValidatedModel {

        private static final int FLAG_VULNERABILITY = 1 << 0;
        private static final int FLAG_MOBILITY = 1 << 1;
        private static final int FLAG_PERSERVANCE = 1 << 2;
        private static final int FLAG_CONTROL_ABILITY = 1 << 3;
        private static final int FLAG_BASIC_POWER_OF_MIND = 1 << 4;
        private static final int FLAG_DOMESTICATION = 1 << 5;

        final IntegerProperty vulnerability = new SimpleIntegerProperty();
        final MaxActValue mobility = new MaxActValue(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
        final MaxActValue perservance = new MaxActValue(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
        final MaxActValue controlAbility = new MaxActValue(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
        final MaxActValue basicPowerOfMind = new MaxActValue(Integer.MIN_VALUE, Integer.MAX_VALUE,
            0);
        final MaxActValue domestication = new MaxActValue(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);

        {
            vulnerability.addListener(FormUtils.notEmptyCondition(this, FLAG_VULNERABILITY));
            mobility.actValueProperty().addListener(FormUtils.notEmptyCondition(this, FLAG_MOBILITY));
            perservance.actValueProperty().addListener(FormUtils.notEmptyCondition(this, FLAG_PERSERVANCE));
            controlAbility.actValueProperty().addListener(FormUtils.notEmptyCondition(this, FLAG_CONTROL_ABILITY));
            basicPowerOfMind.actValueProperty().addListener(FormUtils.notEmptyCondition(this, FLAG_BASIC_POWER_OF_MIND));
            domestication.actValueProperty().addListener(FormUtils.notEmptyCondition(this, FLAG_DOMESTICATION));

            // Automatické nastavení validity na true
            validityFlag.set(ValidatedModel.VALID_FLAG_VALUE);
        }

    }
}
