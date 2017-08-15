package cz.stechy.drd.controller.help;

import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebView;

/**
 * Kontroler pro zobrazení nápovědy aplikace
 */
public class HelpController extends BaseController implements Initializable {

    // region Constants

    private static final String WIKI_URL = "https://github.com/stechy1/drd/wiki";

    // endregion

    // region Variables

    // region FXML

    @FXML
    private WebView webView;

    // endregion

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webView.getEngine().setJavaScriptEnabled(false);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        webView.getEngine().load(WIKI_URL);
    }

    @Override
    protected void onResume() {
        setScreenSize(600, 400);
    }
}
