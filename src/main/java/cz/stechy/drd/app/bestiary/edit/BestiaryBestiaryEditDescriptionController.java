package cz.stechy.drd.app.bestiary.edit;

import cz.stechy.drd.app.bestiary.BestiaryHelper;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

/**
 * Kontroler pro editaci popisu nestvůry
 */
public class BestiaryBestiaryEditDescriptionController implements IBestiaryEditController, Initializable {

    // region Variables

    // region FXML

    @FXML
    private TextArea txtDescription;


    // endregion

    private final BooleanProperty valid = new SimpleBooleanProperty(this, "valid");

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        valid.bind(txtDescription.textProperty().isNotEmpty());
    }

    @Override
    public void loadMobPropertiesFromBundle(Bundle bundle) {
        txtDescription.setText(bundle.getString(BestiaryHelper.DESCRIPTION));
    }

    @Override
    public void saveMobPropertiesToBundle(Bundle bundle) {
        bundle.putString(BestiaryHelper.DESCRIPTION, txtDescription.getText());
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid;
    }
}
