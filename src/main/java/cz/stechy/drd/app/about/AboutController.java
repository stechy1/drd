package cz.stechy.drd.app.about;

import cz.stechy.drd.R;
import cz.stechy.screens.BaseController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;

/**
 * Kontroler okna s informacemi o aplikaci
 */
public class AboutController extends BaseController implements Initializable {

    // region Variables

    private String title;

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title = resources.getString(R.Translate.MAIN_MENU_HELP_ABOUT);
    }

    @Override
    protected void onResume() {
        setTitle(title);
        setScreenSize(600, 400);
    }

    // region Button handlers

    public void handleShowBrowser(ActionEvent actionEvent) {
        final String link = ((Hyperlink) actionEvent.getSource()).getTooltip().getText();
        try {
            new ProcessBuilder("x-www-browser", link).start();
        } catch (IOException e) {}
    }

    // endregion
}
