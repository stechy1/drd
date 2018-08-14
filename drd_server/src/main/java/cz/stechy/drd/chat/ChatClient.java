package cz.stechy.drd.chat;

import cz.stechy.drd.Client;

public final class ChatClient {

    private final Client client;
    private final String name;

    public ChatClient(Client client, String name) {
        this.client = client;
        this.name = name;
    }

    public Client getClient() {
        return client;
    }

    public String getName() {
        return name;
    }
}
