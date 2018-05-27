package cz.stechy.drd.app.server;

import cz.stechy.drd.net.message.ServerStatusMessage.ServerStatus;
import cz.stechy.drd.net.message.ServerStatusMessage.ServerStatusData;
import java.net.InetAddress;
import java.util.Objects;
import java.util.UUID;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Třída reprezentující jednu položku v seznamu serverů
 */
public final class ServerStatusModel {

    // region Variables

    private final UUID serverID;
    private final InetAddress serverAddress;
    public final StringProperty serverName = new SimpleStringProperty(this, "serverName", null);
    public final IntegerProperty connectedClients = new SimpleIntegerProperty(this, "connectedClients", 0);
    public final IntegerProperty maxClients = new SimpleIntegerProperty(this, "maxClients", Integer.MAX_VALUE);
    public final ObjectProperty<ServerStatus> serverStatus = new SimpleObjectProperty<>(this, "serverStatus", ServerStatus.EMPTY);
    public final BooleanProperty connected = new SimpleBooleanProperty(this, "connected", false);
    public final IntegerProperty port = new SimpleIntegerProperty(this, "port", 0);

    // endregion

    // region Constructors

    public ServerStatusModel(ServerStatusData serverStatusData, InetAddress serverAddress) {
        this.serverID = serverStatusData.serverID;
        this.serverAddress = serverAddress;
        this.serverName.set(serverStatusData.serverName);
        this.connectedClients.set(serverStatusData.clientCount);
        this.maxClients.set(serverStatusData.maxClients);
        this.serverStatus.set(serverStatusData.serverStatus);
        this.port.set(serverStatusData.port);
    }

    // endregion

    // region Public methods

    /**
     * Aktualizuje data
     *
     * @param newServerStatusData {@link ServerStatusData} Nová data
     */
    public void update(ServerStatusData newServerStatusData) {
        this.serverName.set(newServerStatusData.serverName);
        this.connectedClients.set(newServerStatusData.clientCount);
        this.maxClients.set(newServerStatusData.maxClients);
        this.serverStatus.set(newServerStatusData.serverStatus);
        this.port.set(newServerStatusData.port);
    }

    // endregion

    // region Getters & Setters

    public String getServerName() {
        return serverName.get();
    }

    public StringProperty serverNameProperty() {
        return serverName;
    }

    public int getConnectedClients() {
        return connectedClients.get();
    }

    public IntegerProperty connectedClientsProperty() {
        return connectedClients;
    }

    public int getMaxClients() {
        return maxClients.get();
    }

    public IntegerProperty maxClientsProperty() {
        return maxClients;
    }

    public ServerStatus getServerStatus() {
        return serverStatus.get();
    }

    public ObjectProperty<ServerStatus> serverStatusProperty() {
        return serverStatus;
    }

    public InetAddress getServerAddress() {
        return serverAddress;
    }

    public StringBinding clientsProperty() {
        return Bindings.createStringBinding(
            () -> String.format("%d/%d", connectedClients.get(), maxClients.get()),
            connectedClients, maxClients);
    }

    public int getPort() {
        return port.get();
    }

    public IntegerProperty portProperty() {
        return port;
    }

    public void setPort(int port) {
        this.port.set(port);
    }

    public boolean isConnected() {
        return connected.get();
    }

    public BooleanProperty connectedProperty() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected.set(connected);
    }

    // endregion

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServerStatusModel that = (ServerStatusModel) o;
        return Objects.equals(serverID, that.serverID);
    }

    @Override
    public int hashCode() {

        return Objects.hash(serverID);
    }
}
