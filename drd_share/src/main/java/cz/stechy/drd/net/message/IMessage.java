package cz.stechy.drd.net.message;

import java.io.IOException;
import java.io.Serializable;

public interface IMessage extends Serializable {

    String getType();

    MessageSource getSource();

    Object getData();

    default boolean isSuccess() {
        return true;
    }

    default byte[] toByteArray() throws IOException {
        return new byte[0];
    }

    default boolean isResponce() {
        return false;
    }

    default IMessage getResponce(boolean success, Object... responceData) {
        return new ResponceMessage(success, responceData);
    }
}
