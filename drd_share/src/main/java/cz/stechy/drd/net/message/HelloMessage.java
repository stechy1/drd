package cz.stechy.drd.net.message;

public class HelloMessage implements IMessage {

    private static final long serialVersionUID = 5760345530080462033L;

    private final MessageSource messageSource;

    public static final String MESSAGE_TYPE = "hello";

    public HelloMessage(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String getType() {
        return MESSAGE_TYPE;
    }

    @Override
    public MessageSource getSource() {
        return messageSource;
    }

    @Override
    public Object getData() {
        return "Hello";
    }

    @Override
    public String toString() {
        return (String) getData();
    }
}
