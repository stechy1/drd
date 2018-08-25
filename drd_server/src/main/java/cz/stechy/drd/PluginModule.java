package cz.stechy.drd;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import cz.stechy.drd.plugins.IPlugin;
import cz.stechy.drd.plugins.Plugin;

class PluginModule extends AbstractModule {

    @Override
    protected void configure() {
        MapBinder<String, IPlugin> pluginBinder = MapBinder.newMapBinder(binder(), String.class, IPlugin.class);

        for (Plugin plugin : Plugin.values()) {
            pluginBinder.addBinding(plugin.name()).to(plugin.clazz).asEagerSingleton();
        }

        // TODO načíst externí pluginy
    }

}
