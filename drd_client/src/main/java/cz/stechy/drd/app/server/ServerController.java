package cz.stechy.drd.app.server;

import cz.stechy.drd.R;
import cz.stechy.drd.net.ClientCommunicator;
import cz.stechy.drd.net.ConnectionState;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Notification;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerController extends BaseController implements Initializable {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerController.class);

    // region Variables

    // region FXML

    @FXML
    private Button btnDisconnect;
    @FXML
    private ListView lvServers; // TODO implementovat vyhledávání serverů
    @FXML
    private Label lblConnectedTo;
    @FXML
    private TextField txtHostPort;
    @FXML
    private Button btnConnect;

    // endregion

    private final ClientCommunicator communicator;
    private String title;

    // endregion

    public ServerController(ClientCommunicator communicator) {
        this.communicator = communicator;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title = resources.getString(R.Translate.SERVER_TITLE);

        lblConnectedTo.textProperty().bind(Bindings.createStringBinding(
            () -> {
                final ConnectionState state = communicator.getConnectionState();
                switch (state) {
                    case CONNECTING:
                        return resources.getString(R.Translate.SERVER_STATUS_CONNECTING);
                    case CONNECTED:
                        return String.format(resources.getString(R.Translate.SERVER_STATUS_CONNECTED), communicator.getConnectedServer());
                    default:
                        return resources.getString(R.Translate.SERVER_STATUS_DISCONNECTED);
                }
            }, communicator.connectionStateProperty()));

        final BooleanBinding connected = communicator.connectionStateProperty()
            .isEqualTo(ConnectionState.CONNECTED);
        final BooleanBinding disconnected = communicator.connectionStateProperty()
            .isEqualTo(ConnectionState.DISCONNECTED);

        btnDisconnect.disableProperty().bind(connected.not());
        btnConnect.disableProperty()
            .bind(txtHostPort.textProperty().isEmpty()
                .or(disconnected.not()));
    }

    @Override
    protected void onResume() {
        setScreenSize(400, 300);
        setTitle(title);
    }

    // region Button handler

    @FXML
    private void handleConnect(ActionEvent actionEvent) {
        final String hostPort = txtHostPort.textProperty().get();
        final String host = hostPort.substring(0, hostPort.indexOf(":"));
        final String portRaw = hostPort.substring(hostPort.indexOf(":") + 1);
        int port;
        try {
            port = Integer.parseInt(portRaw);
        } catch (Exception ex) {
            showNotification(new Notification("Port serveru není správně zadán."));
            return;
        }

        ((Parent)(actionEvent.getSource())).getScene().setCursor(Cursor.WAIT);
        this.communicator.connect(host, port)
            .thenAccept(success -> {
                if (success) {
                    showNotification(new Notification("Spojení bylo navázáno."));
                } else {
                    showNotification(new Notification("Spojení se nezdařilo."));
                }
            })
            .whenComplete((aVoid, throwable) -> {
            ((Parent)(actionEvent.getSource())).getScene().setCursor(Cursor.DEFAULT);
        });
    }

    @FXML
    private void handleDisconnect(ActionEvent actionEvent) {
        communicator.disconnect();
    }

    // endregion
}
