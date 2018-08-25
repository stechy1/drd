package cz.stechy.drd.plugins.chat;

import cz.stechy.drd.core.connection.IClient;

public final class ChatClient {

    private final IClient client;
    private final String name;

    public ChatClient(IClient client, String name) {
        this.client = client;
        this.name = name;
    }

    public IClient getClient() {
        return client;
    }

    public String getName() {
        return name;
    }
}
