package cz.stechy.drd.core.connection;

import cz.stechy.drd.core.event.IEvent;

public class ClientDisconnectedEvent implements IEvent {

    public static final String EVENT_TYPE = "client-disonnected";

    private final Client client;

    ClientDisconnectedEvent(Client client) {
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
