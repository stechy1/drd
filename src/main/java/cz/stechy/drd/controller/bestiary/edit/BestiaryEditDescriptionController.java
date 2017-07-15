package cz.stechy.drd.controller.bestiary.edit;

import cz.stechy.drd.controller.bestiary.BestiaryHelper;
import cz.stechy.screens.Bundle;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * Kontroler pro editaci popisu nestvůry
 */
public class BestiaryEditDescriptionController implements IEditController {

    // region Variables

    // region FXML

    @FXML
    private TextArea txtDescription;


    // endregion

    // endregion

    @Override
    public void loadMobPropertiesFromBundle(Bundle bundle) {
        txtDescription.setText(bundle.getString(BestiaryHelper.DESCRIPTION));
    }

    @Override
    public void saveMobPropertiesToBundle(Bundle bundle) {
        bundle.putString(BestiaryHelper.DESCRIPTION, txtDescription.getText());
    }
}
