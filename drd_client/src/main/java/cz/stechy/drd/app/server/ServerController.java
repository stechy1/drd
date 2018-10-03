package cz.stechy.drd.app.server;

import com.google.inject.Inject;
import cz.stechy.drd.R;
import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.net.ConnectionState;
import cz.stechy.drd.net.LanServerFinder;
import cz.stechy.drd.service.communicator.IClientCommunicator;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import cz.stechy.screens.Notification;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
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
    private ListView<ServerStatusModel> lvServers;
    @FXML
    private Label lblConnectedTo;
    @FXML
    private TextField txtHostPort;
    @FXML
    private Button btnConnect;

    // endregion

    private final IClientCommunicator communicator;
    private final LanServerFinder serverFinder;
    private String title;

    // endregion

    // region Constructors

    @Inject
    public ServerController(IClientCommunicator communicator) {
        this.communicator = communicator;
        LanServerFinder tmpServerFinder;
        try {
            tmpServerFinder = new LanServerFinder();
        } catch (IOException e) {
            tmpServerFinder = null;
            LOGGER.error("Nepodařilo se inicializovat vyhledávač serverů v lokální síti.");
        }

        this.serverFinder = tmpServerFinder;
    }

    // endregion

    // region Private methods

    /**
     * Pokusí se navázat spojení s vybraným serverem.
     *
     * @param scene {@link Scene} Scena, ve které se bude měnit kurzor myši
     */
    private void connect(Scene scene) {
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

        scene.setCursor(Cursor.WAIT);
        this.communicator.connect(host, port)
            .thenAccept(success -> {
                if (!success) {
                    showNotification(new Notification("Spojení se nepodařilo navázat."));
                }
            })
            .whenComplete((aVoid, throwable) -> {
                scene.setCursor(Cursor.DEFAULT);
                finish();
            });
    }

    private void serverSelectionListener(ObservableValue<? extends ServerStatusModel> observable,
        ServerStatusModel oldValue, ServerStatusModel newValue) {
        if (newValue == null) {
            txtHostPort.textProperty().set(null);
            return;
        }
        txtHostPort.textProperty().set(String.format("%s:%d", newValue.getServerAddress().getHostAddress(), newValue.getPort()));
    }

    private void serverClickListener(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            if (lvServers.getSelectionModel().getSelectedItem() != null) {
                connect(((Parent) (mouseEvent.getSource())).getScene());
            }
        }
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title = resources.getString(R.Translate.SERVER_TITLE);

        lblConnectedTo.textProperty().bind(Bindings.createStringBinding(() -> {
                final ConnectionState state = communicator.getConnectionState();
                switch (state) {
                    case CONNECTING:
                        return resources.getString(R.Translate.SERVER_STATUS_CONNECTING);
                    case CONNECTED:
                        return String.format(resources.getString(R.Translate.SERVER_STATUS_CONNECTED), communicator.getConnectedServerName());
                    default:
                        return resources.getString(R.Translate.SERVER_STATUS_DISCONNECTED);
                }
            }, communicator.connectionStateProperty()));

        final BooleanBinding connected = communicator.connectionStateProperty().isEqualTo(ConnectionState.CONNECTED);
        final BooleanBinding disconnected = communicator.connectionStateProperty().isEqualTo(ConnectionState.DISCONNECTED);

        btnDisconnect.disableProperty().bind(connected.not());
        btnConnect.disableProperty()
            .bind(txtHostPort.textProperty().isEmpty()
                .or(disconnected.not()));

        lvServers.setCellFactory(param -> new ServerStatusCell());
        lvServers.setItems(this.serverFinder.getServerList());
        lvServers.getSelectionModel().selectedItemProperty().addListener(this::serverSelectionListener);
        lvServers.setOnMouseClicked(this::serverClickListener);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        CompletableFuture.runAsync(serverFinder, ThreadPool.COMMON_EXECUTOR);
    }

    @Override
    protected void onResume() {
        setScreenSize(400, 300);
        setTitle(title);
    }

    @Override
    protected void onClose() {
        serverFinder.shutdown();
    }

    // region Button handler

    @FXML
    private void handleConnect(ActionEvent actionEvent) {
        connect(((Parent) (actionEvent.getSource())).getScene());
    }

    @FXML
    private void handleDisconnect(ActionEvent actionEvent) {
        communicator.disconnect()
            .thenAccept(success -> {
                if (success) {
                    showNotification(new Notification("Spojení bylo ukončeno."));
                } else {
                    showNotification(new Notification("Spojení se nezdařilo ukončit."));
                }
            });
    }

    // endregion
}
