package cz.stechy.drd.controller.settings;

import cz.stechy.drd.Context;
import cz.stechy.drd.R;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import cz.stechy.screens.Notification;
import cz.stechy.screens.Notification.Length;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Kontroler pro nastavení aplikace
 */
public class SettingsController extends BaseController implements Initializable {

    // region Variables

    // region FXML

    @FXML
    private Label lblOfflineDatabasePath;
    @FXML
    private ToggleButton toggleOnlineDatabase;
    @FXML
    private Button btnSelectCredentials;
    @FXML
    private Label lblCredentialsPath;

    // endregion

    private final Context context;

    private String title;
    private Config config = new Config();

    // endregion

    // region Constructors

    public SettingsController(Context context) {
        this.context = context;
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = resources.getString(R.Translate.SETTINGS);

        lblOfflineDatabasePath.textProperty().bind(config.offlineDatabasePath);
        btnSelectCredentials.disableProperty().bind(config.useOnlineDatabase.not());
        lblCredentialsPath.textProperty().bind(config.onlineDatabaseCredentials);

        config.useOnlineDatabase.bindBidirectional(toggleOnlineDatabase.selectedProperty());
    }

    @Override
    protected void beforeShow() {
        setScreenSize(600, 500);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        config.offlineDatabasePath.setValue(context.getProperty(R.Config.OFFLINE_DATABASE_NAME));
        config.useOnlineDatabase.setValue(Boolean.parseBoolean(context.getProperty(R.Config.USE_ONLINE_DATABASE, "false")));
        config.onlineDatabaseCredentials.setValue(context.getProperty(R.Config.ONLINE_DATABASE_CREDENTIALS_PATH, ""));
    }

    @Override
    protected void onResume() {
        setTitle(title);
    }

    @Override
    protected void onClose() {
        context.setProperty(R.Config.OFFLINE_DATABASE_NAME, config.offlineDatabasePath.getValue());
        context.setProperty(R.Config.USE_ONLINE_DATABASE, Boolean.toString(config.useOnlineDatabase.getValue() && !config.onlineDatabaseCredentials.get().isEmpty()));
        context.setProperty(R.Config.ONLINE_DATABASE_CREDENTIALS_PATH, config.onlineDatabaseCredentials.getValue());
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

    private static final class Config {
        private final StringProperty offlineDatabasePath = new SimpleStringProperty();
        private final BooleanProperty useOnlineDatabase = new SimpleBooleanProperty();
        private final StringProperty onlineDatabaseCredentials = new SimpleStringProperty();


    }
}
