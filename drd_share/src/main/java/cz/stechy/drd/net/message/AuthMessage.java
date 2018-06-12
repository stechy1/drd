package cz.stechy.drd.net.message;

import java.io.Serializable;

public class AuthMessage implements IMessage {

    private static final long serialVersionUID = 2410714674227462122L;

    private final MessageSource source;
    private final AuthAction action;
    private final boolean success;
    private final AuthMessageData data;

    public AuthMessage(MessageSource source, AuthAction action, AuthMessageData data) {
        this(source, action, true, data);
    }

    public AuthMessage(MessageSource source, AuthAction action, boolean success,
        AuthMessageData data) {
        this.source = source;
        this.action = action;
        this.success = success;
        this.data = data;
    }

    @Override
    public MessageType getType() {
        return MessageType.AUTH;
    }

    public AuthAction getAction() {
        return action;
    }

    @Override
    public MessageSource getSource() {
        return source;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    public enum AuthAction {
        REGISTER, LOGIN, LOGOUT
    }

    public static final class AuthMessageData implements Serializable {

        private static final long serialVersionUID = -9036266648628886210L;

        public final String id;
        public final String name;
        public final String password;

        public AuthMessageData(String name, String password) {
            this("", name, password);
        }

        public AuthMessageData(String id, String name, String password) {
            this.id = id;
            this.name = name;
            this.password = password;
        }
    }
}
