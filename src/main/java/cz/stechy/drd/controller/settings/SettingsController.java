package cz.stechy.drd.controller.settings;

import cz.stechy.drd.Context;
import cz.stechy.drd.R;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Notification;
import cz.stechy.screens.Notification.Length;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Kontroler pro nastavení aplikace
 */
public class SettingsController extends BaseController implements Initializable {

    // region Variables

    // region FXML

    @FXML
    private Label lblCredentialsPath;

    // endregion

    private final Context context;

    private String title;

    // endregion

    // region Constructors

    public SettingsController(Context context) {
        this.context = context;
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = resources.getString(R.Translate.SETTINGS);
    }

    @Override
    protected void beforeShow() {
        setScreenSize(600, 500);
    }

    @Override
    protected void onResume() {
        setTitle(title);
    }

    // region Button handlers

    @FXML
    private void handleSelectCredentialsPath(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().setAll(new ExtensionFilter("Credentials file", "*.json"));
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.setTitle("Vyberte soubor s přístupovými údaji");
        final File file = chooser.showOpenDialog(((Node) actionEvent.getSource()).getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            context.initFirebase(file);
            lblCredentialsPath.setText(file.getPath());
            showNotification(new Notification("Inicializace se zdařila."));
        } catch (Exception e) {
            showNotification(new Notification("Databázi se nepodařilo inicializovat", Length.LONG));
        }
    }

    // endregion
}
