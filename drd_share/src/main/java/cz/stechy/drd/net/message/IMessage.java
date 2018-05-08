package cz.stechy.drd.net.message;

import java.io.Serializable;

public interface IMessage extends Serializable {

    MessageType getType();

    MessageSource getSource();

    Object getData();

}
