package cz.stechy.drd.app.settings;

import com.google.inject.Inject;
import cz.stechy.drd.AppSettings;
import cz.stechy.drd.R;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * Kontroler pro nastavení aplikace
 */
public class SettingsController extends BaseController implements Initializable {

    // region Variables

    // region FXML

    @FXML
    private Label lblOfflineDatabasePath;

    // endregion

    private final AppSettings settings;

    private String title;
    private Config config = new Config();

    // endregion

    // region Constructors

    @Inject
    public SettingsController(AppSettings settings) {
        this.settings = settings;
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = resources.getString(R.Translate.SETTINGS);

        lblOfflineDatabasePath.textProperty().bind(config.offlineDatabasePath);
    }

    @Override
    protected void beforeShow() {
        setScreenSize(600, 500);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        config.offlineDatabasePath.setValue(settings.getProperty(R.Config.OFFLINE_DATABASE_NAME));
    }

    @Override
    protected void onResume() {
        setTitle(title);
    }

    @Override
    protected void onClose() {
        settings.setProperty(R.Config.OFFLINE_DATABASE_NAME, config.offlineDatabasePath.getValue());
    }

    private static final class Config {
        private final StringProperty offlineDatabasePath = new SimpleStringProperty();
    }
}
