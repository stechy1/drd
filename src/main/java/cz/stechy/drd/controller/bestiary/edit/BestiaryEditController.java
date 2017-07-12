package cz.stechy.drd.controller.bestiary.edit;

import cz.stechy.drd.R;
import cz.stechy.drd.controller.InjectableChild;
import cz.stechy.drd.controller.bestiary.BestiaryHelper;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/**
 * Kontroler pro řížení kontrolerů pro editaci vlastností nestvůry
 */
public class BestiaryEditController extends BaseController implements Initializable {

    // region Constants



    // endregion

    // region Variables

    // region FXML

    @FXML
    private BestiaryEditImageController tabBestiaryImageController;
    @FXML
    private BestiaryEditGeneralController tabBestiaryGeneralController;
    @FXML
    private BestiaryEditPropertiesController tabBestiaryPropertiesController;
    @FXML
    private BestiaryEditOtherController tabBestiaryOtherController;
    @FXML
    private BestiaryEditDescriptionController tabBestiaryDescriptionController;

    @FXML
    private Pane tabBestiaryImage;
    @FXML
    private GridPane tabBestiaryGeneral;
    @FXML
    private GridPane tabBestiaryProperties;
    @FXML
    private GridPane tabBestiaryOther;
    @FXML
    private TextArea tabBestiaryDescription;

    // endregion

    private IEditController[] controllers;
    private String title;

    private int action;
    private String id;
    private boolean downloaded;
    private boolean uploaded;

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = resources.getString(R.Translate.BESTIARY_EDIT_TITLE);

        controllers = new IEditController[] {
            tabBestiaryImageController,
            tabBestiaryGeneralController,
            tabBestiaryPropertiesController,
            tabBestiaryOtherController,
            tabBestiaryDescriptionController
        };
        Arrays.stream(controllers).forEach(controller -> {
            if (controller instanceof InjectableChild) {
                ((InjectableChild) controller).injectParent(this);
            }
        });
    }

    @Override
    protected void onCreate(Bundle bundle) {
        action = bundle.getInt(BestiaryHelper.MOB_ACTION);
        id = bundle.getString(BestiaryHelper.ID);
        downloaded =bundle.getBoolean(BestiaryHelper.DOWNLOADED);
        uploaded = bundle.getBoolean(BestiaryHelper.UPLOADED);
        Arrays.stream(controllers).forEach(controller -> controller.loadMobPropertiesFromBundle(bundle));
    }

    @Override
    protected void onResume() {
        setTitle(title);
        setScreenSize(400, 370);
    }

    @Override
    protected void onScreenResult(int statusCode, int actionId, Bundle bundle) {
        Arrays.stream(controllers).forEach(controller -> {
            if (controller instanceof InjectableChild) {
                ((InjectableChild) controller).onScreenResult(statusCode, actionId, bundle);
            }
        });
    }

    // region Button handlers

    public void handleCancel(ActionEvent actionEvent) {
        finish();
    }

    public void handleFinish(ActionEvent actionEvent) {
        setResult(RESULT_SUCCESS);
        final Bundle bundle = new Bundle();
        bundle.putInt(BestiaryHelper.MOB_ACTION, action);
        bundle.putString(BestiaryHelper.ID, id);
        bundle.putBoolean(BestiaryHelper.DOWNLOADED, downloaded);
        bundle.putBoolean(BestiaryHelper.UPLOADED, uploaded);
        Arrays.stream(controllers).forEach(controllers -> controllers.saveMobPropertiesToBundle(bundle));
        finish(bundle);
    }

    // endregion
}
