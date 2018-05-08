package cz.stechy.drd.app.server;

import cz.stechy.drd.net.ClientCommunicator;
import cz.stechy.drd.net.OnDataReceivedListener;
import cz.stechy.drd.net.message.MessageType;
import cz.stechy.drd.net.message.SimpleResponce;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import cz.stechy.screens.Notification;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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

    private final ClientCommunicator communicator;

    public ServerController(ClientCommunicator communicator) {
        this.communicator = communicator;
        this.communicator.registerMessageObserver(MessageType.HELLO, this.messageListener);
        this.communicator.registerMessageObserver(MessageType.SIMPLE_RESPONCE, this.messageListener);
    }

    private OnDataReceivedListener messageListener = message1 -> {
        Platform.runLater(() -> txtArea.appendText(message1.toString() + "\n"));
    };

    private void sendMessage() {
        final String msg = message.get();
        communicator.sendMessage(new SimpleResponce(msg));
        message.set("");
        txtMessage.requestFocus();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        message.bindBidirectional(txtMessage.textProperty());
    }

    @Override
    protected void onCreate(Bundle bundle) {
        this.communicator.connect("localhost", 15378)
            .thenAccept(aVoid -> {
                showNotification(new Notification("Spojení bylo navázáno."));
            }).exceptionally(throwable -> {
                LOGGER.error("Spojení se nepodařilo navázat.");
                return null;
        });
    }

    @Override
    protected void onClose() {
        communicator.unregisterMessageObserver(MessageType.HELLO, this.messageListener);
        communicator.unregisterMessageObserver(MessageType.SIMPLE_RESPONCE, this.messageListener);
        communicator.disconnect();
    }

    public void handleSend(ActionEvent actionEvent) {
        sendMessage();
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() != KeyCode.ENTER) {
            return;
        }

        sendMessage();
    }
}
