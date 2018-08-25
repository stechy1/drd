package cz.stechy.drd.net.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ServerStatusMessage implements IMessage {

    private static final long serialVersionUID = -1429760060957272567L;

    public static final String MESSAGE_TYPE = "server-status";

    private final ServerStatusData statusData;

    public ServerStatusMessage(ServerStatusData statusData) {
        this.statusData = statusData;
    }

    @Override
    public String getType() {
        return MESSAGE_TYPE;
    }

    @Override
    public MessageSource getSource() {
        return MessageSource.SERVER;
    }

    @Override
    public Object getData() {
        return statusData;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.writeByte(0);
        final byte[] bytes = baos.toByteArray();
        assert bytes.length < 1024;

        return bytes;
    }

    @Override
    public String toString() {
        return String.valueOf(getData());
    }

    public enum ServerStatus {
        EMPTY, HAVE_SPACE, FULL
    }

    public static final class ServerStatusData implements Serializable {

        private static final long serialVersionUID = -4288671744361722044L;

        public final UUID serverID;
        public final ServerStatus serverStatus;
        public final int clientCount;
        public final int maxClients;
        public final String serverName;
        public final int port;

        /**
         * Vytvoří novou instanci reprezentující informace o stavu serveru
         *  @param serverID
         * @param serverStatus {@link ServerStatus} stav serveru
         * @param clientCount Počet aktuálně připojených klientů
         * @param maxClients Počet maximálně připojených klientů
         * @param serverName Název serveru
         * @param port
         */
        public ServerStatusData(UUID serverID, ServerStatus serverStatus, int clientCount,
            int maxClients, String serverName, int port) {
            this.serverID = serverID;
            this.serverStatus = serverStatus;
            this.clientCount = clientCount;
            this.maxClients = maxClients;
            this.serverName = serverName;
            this.port = port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ServerStatusData that = (ServerStatusData) o;
            return clientCount == that.clientCount &&
                maxClients == that.maxClients &&
                port == that.port &&
                Objects.equals(serverID, that.serverID) &&
                serverStatus == that.serverStatus &&
                Objects.equals(serverName, that.serverName);
        }

        @Override
        public int hashCode() {

            return Objects.hash(serverID, serverStatus, clientCount, maxClients, serverName, port);
        }

        @Override
        public String toString() {
            return String.format("%s: %d/%d - %s; port=%d", serverName, clientCount, maxClients, serverStatus, port);
        }
    }
}
