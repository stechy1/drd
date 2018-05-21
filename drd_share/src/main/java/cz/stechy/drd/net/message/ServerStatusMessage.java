package cz.stechy.drd.net.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;

public class ServerStatusMessage implements IMessage {

    private static final long serialVersionUID = -1429760060957272567L;

    private final ServerStatusData statusData;

    public ServerStatusMessage(ServerStatusData statusData) {
        this.statusData = statusData;
    }

    @Override
    public MessageType getType() {
        return MessageType.SERVER_STATUS;
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
        oos.writeObject(statusData);
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

        public final ServerStatus serverStatus;
        public final int clientCount;
        public final int maxClient;
        public final String serverName;

        /**
         * Vytvoří novou instanci reprezentující informace o stavu serveru
         *
         * @param serverStatus {@link ServerStatus} stav serveru
         * @param clientCount Počet aktuálně připojených klientů
         * @param maxClient Počet maximálně připojených klientů
         * @param serverName Název serveru
         */
        public ServerStatusData(ServerStatus serverStatus, int clientCount, int maxClient,
            String serverName) {
            this.serverStatus = serverStatus;
            this.clientCount = clientCount;
            this.maxClient = maxClient;
            this.serverName = serverName;
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
                maxClient == that.maxClient &&
                serverStatus == that.serverStatus &&
                Objects.equals(serverName, that.serverName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serverStatus, clientCount, maxClient, serverName);
        }

        @Override
        public String toString() {
            return String.format("%s: %d/%d - %s", serverName, clientCount, maxClient, serverStatus);
        }
    }
}
