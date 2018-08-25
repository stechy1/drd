package cz.stechy.drd.plugins;

import cz.stechy.drd.core.event.IEventBus;
import java.util.Map;

/**
 * Rozhraní definující plugin
 */
public interface IPlugin extends Comparable<IPlugin> {

    /**
     * Vrátí název pluginu
     *
     * @return Název pluginu
     */
    String getName();

    /**
     * Inicializace pluginu
     * Zde by se měl plugin inicializovat, ne v konstruktoru
     */
    void init();

    /**
     * Zde se musí zaregistrovat posluchače pro příchozí zprávy od klienta
     *
     * @param eventBus {@link IEventBus}
     */
    default void registerMessageHandlers(IEventBus eventBus) {}

    /**
     * Nastavení komunikace mezi pluginy
     *
     * @param otherPlugins Kolekce ostatních pluginů
     */
    default void setupDependencies(Map<String, IPlugin> otherPlugins) {}

    @Override
    default int compareTo(IPlugin o) {
        final PluginConfiguration thisConfiguration = getClass().getAnnotation(PluginConfiguration.class);
        final PluginConfiguration thatConfiguration = o.getClass().getAnnotation(PluginConfiguration.class);

        if (thisConfiguration == null && thatConfiguration == null) {
            return 0;
        }

        final int thisPriority = thisConfiguration == null ? PluginConfiguration.DEFAULT_PRIORITY : thisConfiguration.priority();
        final int thatPriority = thatConfiguration == null ? PluginConfiguration.DEFAULT_PRIORITY : thatConfiguration.priority();

        return Integer.compare(thisPriority, thatPriority);
    }
}
