package cz.stechy.drd.old;

import cz.stechy.drd.net.message.IMessage;

public interface IMessageReceiveListener {

    void onMessageReceive(IMessage message, Client client);

}
