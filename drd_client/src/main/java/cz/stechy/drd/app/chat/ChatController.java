package cz.stechy.drd.app.chat;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTreeView;
import cz.stechy.drd.R;
import cz.stechy.screens.BaseController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

public class ChatController extends BaseController implements Initializable {

    // region Variables

    // region FXML

    @FXML
    private JFXListView listContacts;
    @FXML
    private JFXTreeView treeRooms;
    @FXML
    private TabPane tabConversations;
    @FXML
    private TextField txtMessage;

    // endregion

    private String title;

    // endregion


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = resources.getString(R.Translate.CHAT_TITLE);
    }

    @Override
    protected void onResume() {
        setTitle(title);
        setScreenSize(600, 350);
    }

    // region Button handlers

    @FXML
    private void handleNewRoom(ActionEvent actionEvent) {

    }

    @FXML
    private void handleSendMessage(ActionEvent actionEvent) {

    }

    // endregion
}
