package cz.stechy.drd.app.server;

import cz.stechy.drd.R;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

public class ServerStatusCell extends ListCell<ServerStatusModel> {

    // region Constants

    private static final String FXML_PATH = "/fxml/" + R.FXML.SERVER_STATUS_CELL + ".fxml";

    // endregion

    // region Variables

    // region FXML

    @FXML
    private Label lblName;

    @FXML
    private Label lblClients;

    @FXML
    private Label lblAddress;

    // endregion

    private Parent container;

    // endregion

    public ServerStatusCell() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
        loader.setController(this);
        try {
            container = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(ServerStatusModel item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
            lblName.textProperty().unbind();
            lblClients.textProperty().unbind();
        } else {
            lblName.textProperty().bind(item.serverName);
            lblClients.textProperty().bind(item.clientsProperty());
            lblAddress.textProperty().set(item.getServerAddress().getHostAddress());
            setGraphic(container);
        }
    }
}
