package cz.stechy.drd.net.message;

public class HelloMessage implements IMessage {

    private static final long serialVersionUID = 5760345530080462033L;

    private final MessageSource messageSource;

    public HelloMessage(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public MessageType getType() {
        return MessageType.HELLO;
    }

    @Override
    public MessageSource getSource() {
        return messageSource;
    }

    @Override
    public Object getData() {
        return "Hello";
    }
}
