package cz.stechy.drd.net.message;

import java.io.Serializable;
import java.util.UUID;

public class ClientStatusMessage implements IMessage {

    private static final long serialVersionUID = -7383579587998947723L;

    public static final String MESSAGE_TYPE = "client-status";

    private final ClientStatusData data;

    public ClientStatusMessage(ClientStatusData data) {
        this.data = data;
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
        return data;
    }

    public static final class ClientStatusData implements Serializable {

        private static final long serialVersionUID = -8775817684472782497L;

        public final UUID clientID;
        public final ClientStatus status;

        public ClientStatusData(UUID clientID,
            ClientStatus status) {
            this.clientID = clientID;
            this.status = status;
        }
    }

    public enum ClientStatus {
        CONNECTED, AUTHENTICATED, DISCONNECT, LOST_CONNECTION
    }
}
