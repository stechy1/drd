package cz.stechy.drd.plugins;

/**
 * Výčet základních pluginů dostupných na serveru
 */
public enum Plugin {
//    HELLO(HelloPlugin.class),
//    DATABASE(DatabasePlugin.class),
//    AUTH(AuthPlugin.class)

    ;

    public final Class<? extends IPlugin> clazz;

    Plugin(Class<? extends IPlugin> clazz) {
        this.clazz = clazz;
    }
}

