package cz.stechy.drd.app.server;

import cz.stechy.drd.net.ClientCommunicator;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerController extends BaseController implements Initializable {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerController.class);

    private final StringProperty message = new SimpleStringProperty(this, "message", null);

    @FXML
    private TextArea txtArea;
    @FXML
    private TextField txtMessage;

    private ClientCommunicator communicator;

    private Socket getSocket() {
        try {
            return new Socket("localhost", 15378);
        } catch (IOException e) {
            return null;
        }
    }

    private void dataReceivedHandler(Object data) {
        Platform.runLater(() -> txtArea.appendText(data + "\n"));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        message.bindBidirectional(txtMessage.textProperty());
    }

    @Override
    protected void onCreate(Bundle bundle) {
        Socket socket = getSocket();
        if (socket == null) {
            return;
        }

        try {
            communicator = new ClientCommunicator(socket, data -> dataReceivedHandler(data));
        } catch (IOException e) {
            LOGGER.error("Nepodařilo se navázat spojení se serverem.", e);
        }
    }

    @Override
    protected void onClose() {
        communicator.close();
    }

    public void handleSend(ActionEvent actionEvent) {
        final String msg = message.get();
        communicator.sendMessage(msg);
        message.set("");
    }

}
