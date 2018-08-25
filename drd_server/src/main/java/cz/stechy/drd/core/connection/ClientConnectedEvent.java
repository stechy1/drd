package cz.stechy.drd.core.connection;

import cz.stechy.drd.core.event.IEvent;

public class ClientConnectedEvent implements IEvent {

    public static final String EVENT_NAME = "client-connected";

    private final Client client;

    ClientConnectedEvent(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }
}
