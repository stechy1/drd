package cz.stechy.drd.app.bestiary.edit;

import cz.stechy.drd.R;
import cz.stechy.drd.app.InjectableChild;
import cz.stechy.drd.app.bestiary.BestiaryHelper;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Kontroler pro řížení kontrolerů pro editaci vlastností nestvůry
 */
public class BestiaryEditController extends BaseController implements Initializable {

    // region Variables

    // region FXML

    @FXML
    private BestiaryBestiaryEditImageController tabBestiaryImageController;
    @FXML
    private BestiaryBestiaryEditGeneralController tabBestiaryGeneralController;
    @FXML
    private BestiaryBestiaryEditPropertiesController tabBestiaryPropertiesController;
    @FXML
    private BestiaryBestiaryEditOtherController tabBestiaryOtherController;
    @FXML
    private BestiaryBestiaryEditDescriptionController tabBestiaryDescriptionController;

    @FXML
    private Label lblTitle;
    @FXML
    private Pane tabBestiaryImage;
    @FXML
    private VBox tabBestiaryGeneral;
    @FXML
    private VBox tabBestiaryProperties;
    @FXML
    private VBox tabBestiaryOther;
    @FXML
    private TextArea tabBestiaryDescription;
    @FXML
    private Button btnFinish;

    // endregion

    private final BooleanProperty valid = new SimpleBooleanProperty(this, "valid");

    private IBestiaryEditController[] controllers;
    private String titleNew;
    private String titleUpdate;

    private int action;
    private String id;
    private boolean downloaded;
    private boolean uploaded;

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleNew = resources.getString(R.Translate.BESTIARY_EDIT_NEW_TITLE);
        titleUpdate = resources.getString(R.Translate.BESTIARY_EDIT_UPDATE_TITLE);

        controllers = new IBestiaryEditController[] {
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

        valid.bind(tabBestiaryImageController.validProperty().and(
            tabBestiaryGeneralController.validProperty().and(
                tabBestiaryPropertiesController.validProperty().and(
                    tabBestiaryOtherController.validProperty()
                )
            )
        ));

        btnFinish.disableProperty().bind(valid.not());
    }

    @Override
    protected void onCreate(Bundle bundle) {
        action = bundle.getInt(BestiaryHelper.MOB_ACTION);
        id = bundle.getString(BestiaryHelper.ID);
        downloaded =bundle.getBoolean(BestiaryHelper.DOWNLOADED);
        uploaded = bundle.getBoolean(BestiaryHelper.UPLOADED);
        lblTitle.setText(action == BestiaryHelper.MOB_ACTION_ADD ? titleNew : titleUpdate);
        if (action == BestiaryHelper.MOB_ACTION_UPDATE) {
            Arrays.stream(controllers)
                .forEach(controller -> controller.loadMobPropertiesFromBundle(bundle));
        }
    }

    @Override
    protected void onResume() {
        setTitle(action == BestiaryHelper.MOB_ACTION_ADD ? titleNew : titleUpdate);
        setScreenSize(400, 450);
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
