package cz.stechy.drd.controller.fight;

import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

public class FightCommentController extends BaseController implements Initializable {

    // region Constants

    public static final String COMMENT = "comment";

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TextArea txtComment;

    // endregion

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtComment.textProperty().addListener((observable, oldValue, newValue) ->
            txtComment.setScrollTop(Double.MAX_VALUE));
    }

    @Override
    protected void onCreate(Bundle bundle) {
        StringProperty commentProperty = bundle.get(COMMENT);
        commentProperty.addListener((observable, oldValue, newValue) -> addComment(newValue));
    }

    @Override
    protected void onResume() {
        setTitle("Komentář souboje");
        setScreenSize(350, 210);
    }

    private void addComment(String comment) {
        txtComment.appendText(comment + "\n");
    }
}
