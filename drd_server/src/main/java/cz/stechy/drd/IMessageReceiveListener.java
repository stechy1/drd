package cz.stechy.drd;

import cz.stechy.drd.net.message.IMessage;

public interface IMessageReceiveListener {

    void onMessageReceive(IMessage message, Client client);

}
