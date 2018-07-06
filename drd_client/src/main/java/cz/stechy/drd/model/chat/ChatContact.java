package cz.stechy.drd.model.chat;

import cz.stechy.drd.crypto.ICypher;

/**
 * Třída reprezentující jeden kontakt v seznamu kontaktů v chatu
 */
public class ChatContact {

    private final String id;
    private final String name;
    private final ICypher cypher;

    public ChatContact(String id, String name, ICypher cypher) {
        this.id = id;
        this.name = name;
        this.cypher = cypher;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public byte[] encrypt(byte[] src) {
        return cypher.encrypt(src);
    }

    @Override
    public String toString() {
        return name;
    }
}
