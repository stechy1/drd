package cz.stechy.drd.controller.bestiary.edit;

import cz.stechy.drd.controller.InjectableChild;
import cz.stechy.drd.controller.bestiary.BestiaryHelper;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.entity.Vulnerability;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;

/**
 * Kontroler pro editaci ostatních vlastností nestvůry
 */
public class BestiaryEditOtherController implements InjectableChild, IEditController, Initializable {

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
    public void injectParent(BaseController baseController) {
        this.baseController = baseController;
    }

    @Override
    public void loadMobPropertiesFromBundle(Bundle bundle) {
        model.vulnerability.setValue(new Vulnerability(bundle.getInt(BestiaryHelper.VULNERABILITY)));
        model.mobility.setActValue(bundle.getInt(BestiaryHelper.MOBILITY));
        model.perservance.setActValue(bundle.getInt(BestiaryHelper.PERSERVANCE));
        model.controlAbility.setActValue(bundle.getInt(BestiaryHelper.CONTROL_ABILITY));
        model.basicPowerOfMind.setActValue(bundle.getInt(BestiaryHelper.BASIC_BOWER_OF_MIND));
        model.domestication.setActValue(bundle.getInt(BestiaryHelper.DOMESTICATION));
    }

    @Override
    public void saveMobPropertiesToBundle(Bundle bundle) {
        bundle.putInt(BestiaryHelper.VULNERABILITY, model.vulnerability.getValue().value);
        bundle.putInt(BestiaryHelper.MOBILITY, model.mobility.getActValue().intValue());
        bundle.putInt(BestiaryHelper.PERSERVANCE, model.perservance.getActValue().intValue());
        bundle.putInt(BestiaryHelper.CONTROL_ABILITY, model.controlAbility.getActValue().intValue());
        bundle.putInt(BestiaryHelper.BASIC_BOWER_OF_MIND, model.basicPowerOfMind.getActValue().intValue());
        bundle.putInt(BestiaryHelper.DOMESTICATION, model.domestication.getActValue().intValue());
    }

    public void handleShowVulnerabilityPopup(ActionEvent actionEvent) {

    }

    private static final class Model {
        final ObjectProperty<Vulnerability> vulnerability = new SimpleObjectProperty<>();
        final MaxActValue mobility = new MaxActValue(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
        final MaxActValue perservance = new MaxActValue(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
        final MaxActValue controlAbility = new MaxActValue(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
        final MaxActValue basicPowerOfMind = new MaxActValue(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
        final MaxActValue domestication = new MaxActValue(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);

    }
}
