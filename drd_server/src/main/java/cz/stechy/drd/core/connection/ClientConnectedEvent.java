package cz.stechy.drd.core.connection;

import cz.stechy.drd.core.event.IEvent;

public class ClientConnectedEvent implements IEvent {

    public static final String EVENT_TYPE = "client-connected";

    private final Client client;

    ClientConnectedEvent(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }
}
