package cz.stechy.drd.model.chat;

import cz.stechy.drd.crypto.ICypher;

public class ChatContact {

    private final String name;
    private final ICypher cypher;

    public ChatContact(String name, ICypher cypher) {
        this.name = name;
        this.cypher = cypher;
    }

    public String getName() {
        return name;
    }

    public byte[] encrypt(byte[] src) {
        return cypher.encrypt(src);
    }
}
