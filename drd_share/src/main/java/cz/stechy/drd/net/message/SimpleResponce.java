package cz.stechy.drd.net.message;

public class SimpleResponce implements IMessage {

    private static final long serialVersionUID = -3193105978677156128L;

    private final String responce;

    public SimpleResponce(String responce) {
        this.responce = responce;
    }

    @Override
    public MessageType getType() {
        return MessageType.SIMPLE_RESPONCE;
    }

    @Override
    public MessageSource getSource() {
        return MessageSource.SERVER;
    }

    @Override
    public Object getData() {
        return responce;
    }

    @Override
    public String toString() {
        return responce;
    }
}
