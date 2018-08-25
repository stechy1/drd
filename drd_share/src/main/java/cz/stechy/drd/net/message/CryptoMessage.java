package cz.stechy.drd.net.message;

import cz.stechy.drd.crypto.RSA.CypherKey;

public class CryptoMessage implements IMessage {

    private static final long serialVersionUID = 7416273624613153526L;

    public static final String MESSAGE_TYPE = "crypto";

    private final MessageSource source;
    private final CypherKey data;

    public CryptoMessage(MessageSource source, CypherKey data) {
        this.source = source;
        this.data = data;
    }

    @Override
    public String getType() {
        return MESSAGE_TYPE;
    }

    @Override
    public MessageSource getSource() {
        return source;
    }

    @Override
    public Object getData() {
        return data;
    }
}
