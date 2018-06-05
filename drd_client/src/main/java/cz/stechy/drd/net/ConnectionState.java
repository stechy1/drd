package cz.stechy.drd.net;

import cz.stechy.drd.R;
import cz.stechy.drd.model.ITranslatedEnum;

/**
 * Výčet reprezentující různé stavy serveru
 */
public enum ConnectionState implements ITranslatedEnum {
    DISCONNECTED(R.Translate.SERVER_STATUS_DISCONNECTED),
    CONNECTING(R.Translate.SERVER_STATUS_CONNECTING),
    CONNECTED(R.Translate.SERVER_STATUS_CONNECTED);

    private final String key;

    ConnectionState(String key) {
        this.key = key;
    }

    @Override
    public String getKeyForTranslation() {
        return key;
    }
}
