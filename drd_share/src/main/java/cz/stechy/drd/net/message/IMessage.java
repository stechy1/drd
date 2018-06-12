package cz.stechy.drd.net.message;

import java.io.IOException;
import java.io.Serializable;

public interface IMessage extends Serializable {

    MessageType getType();

    MessageSource getSource();

    Object getData();

    default boolean isSuccess() {
        return true;
    }

    default byte[] toByteArray() throws IOException {
        return new byte[0];
    }
}
