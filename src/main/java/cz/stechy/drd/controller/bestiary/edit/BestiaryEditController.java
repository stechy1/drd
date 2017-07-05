package cz.stechy.drd.controller.bestiary.edit;

import cz.stechy.drd.R;
import cz.stechy.drd.controller.bestiary.BestiaryHelper;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;

/**
 * Kontroler pro řížení kontrolerů pro editaci vlastností nestvůry
 */
public class BestiaryEditController extends BaseController implements Initializable {

    // region Constants



    // endregion

    // region Variables

    // region FXML

    @FXML
    private BestiaryEditGeneralController tabBestiaryGeneralController;
    @FXML
    private BestiaryEditPropertiesController tabBestiaryPropertiesController;
    @FXML
    private BestiaryEditOtherController tabBestiaryOtherController;

    @FXML
    private GridPane tabBestiaryGeneral;
    @FXML
    private GridPane tabBestiaryProperties;
    @FXML
    private GridPane tabBestiaryOther;

    // endregion

    private IEditController[] controllers;
    private String title;
    private int action;

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = resources.getString(R.Translate.BESTIARY_EDIT_TITLE);

        controllers = new IEditController[] {
            tabBestiaryGeneralController,
            tabBestiaryPropertiesController,
            tabBestiaryOtherController
        };
    }

    @Override
    protected void onCreate(Bundle bundle) {
        action = bundle.getInt(BestiaryHelper.MOB_ACTION);
        Arrays.stream(controllers).forEach(controller -> controller.loadMobPropertiesFromBundle(bundle));
    }

    @Override
    protected void onResume() {
        setTitle(title);
        setScreenSize(400, 370);
    }

    // region Button handlers

    public void handleCancel(ActionEvent actionEvent) {
        finish();
    }

    public void handleFinish(ActionEvent actionEvent) {
        final Bundle bundle = new Bundle();
        bundle.putInt(BestiaryHelper.MOB_ACTION, action);
        Arrays.stream(controllers).forEach(controllers -> controllers.saveMobPropertiesToBundle(bundle));
        finish(bundle);
    }

    // endregion
}
