package cz.stechy.drd.net.message;

public class KeepAliveMessage implements IMessage {

    private static final long serialVersionUID = -5139668102683412472L;

    @Override
    public MessageType getType() {
        return MessageType.KEEP_ALIVE;
    }

    @Override
    public MessageSource getSource() {
        return MessageSource.SERVER;
    }

    @Override
    public Object getData() {
        return "Keep alive";
    }

    @Override
    public String toString() {
        return (String) getData();
    }
}
