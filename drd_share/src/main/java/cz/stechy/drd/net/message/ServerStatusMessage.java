package cz.stechy.drd.net.message;

public class ServerStatusMessage implements IMessage {

    private static final long serialVersionUID = -1429760060957272567L;

    private final ServerStatus serverStatus;

    public ServerStatusMessage(ServerStatus serverStatus) {
        this.serverStatus = serverStatus;
    }

    @Override
    public MessageType getType() {
        return MessageType.SERVER_FULL;
    }

    @Override
    public MessageSource getSource() {
        return MessageSource.SERVER;
    }

    @Override
    public Object getData() {
        return serverStatus;
    }

    public enum ServerStatus {
        EMPTY, HAVE_SPACE, FULL
    }
}
