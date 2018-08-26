package cz.stechy.drd.core.connection;

import cz.stechy.drd.core.event.IEvent;
import cz.stechy.drd.net.message.IMessage;

public class MessageReceivedEvent implements IEvent {

    private final IMessage receivedMessage;
    private final IClient client;

    MessageReceivedEvent(IMessage receivedMessage, IClient client) {
        this.receivedMessage = receivedMessage;
        this.client = client;
    }

    @Override
    public String getEventName() {
        return receivedMessage.getType();
    }

    public IMessage getReceivedMessage() {
        return receivedMessage;
    }

    public IClient getClient() {
        return client;
    }
}