package cz.stechy.drd.plugins;

import cz.stechy.drd.plugins.crypto.CryptoPlugin;
import cz.stechy.drd.plugins.firebase.FirebasePlugin;

/**
 * Výčet základních pluginů dostupných na serveru
 */
public enum Plugin {
    FIREBASE(FirebasePlugin.class),
    CRYPTO(CryptoPlugin.class);

    public final Class<? extends IPlugin> clazz;

    Plugin(Class<? extends IPlugin> clazz) {
        this.clazz = clazz;
    }
}

