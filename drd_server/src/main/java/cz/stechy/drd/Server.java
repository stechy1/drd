package cz.stechy.drd;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import cz.stechy.drd.cmd.IParameterFactory;
import cz.stechy.drd.cmd.IParameterProvider;
import cz.stechy.drd.core.event.IEventBus;
import cz.stechy.drd.core.server.IServerThread;
import cz.stechy.drd.core.server.IServerThreadFactory;
import cz.stechy.drd.plugins.IPlugin;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    // endregion

    // region Variables

    private final Scanner scanner = new Scanner(System.in);
    private final IParameterFactory parameterFactory;
    private final IServerThreadFactory serverThreadFactory;
    private final IEventBus messageRegistrator;
    private final Map<String, IPlugin> plugins;

    // endregion

    // region Constructors

    @Inject
    public Server(IParameterFactory parameterFactory, IServerThreadFactory serverThreadFactory,
        IEventBus messageRegistrator, Map<String, IPlugin> plugins) {
        this.parameterFactory = parameterFactory;
        this.serverThreadFactory = serverThreadFactory;
        this.messageRegistrator = messageRegistrator;
        this.plugins = plugins;
    }

    // endregion

    // region Private methods

    private void run(String[] args) throws IOException {
        final IParameterProvider parameters = parameterFactory.getParameters(args);
        final IServerThread serverThread = serverThreadFactory.getServerThread(parameters);

        LOGGER.info("Spouštím server...");
        initPlugins();

        // Spuštění vlákna serveru
        serverThread.start();

        while(true) {
            final String input = scanner.nextLine();
            if ("exit".equals(input)) {
                break;
            }
        }

        LOGGER.info("Ukončuji server.");
        serverThread.shutdown();

        LOGGER.info("Server byl ukončen.");
    }

    private List<IPlugin> getSortedPlugins() {
        final List<IPlugin> pluginList = new ArrayList<>(plugins.values());
        Collections.sort(pluginList);
        Collections.reverse(pluginList);

        return pluginList;
    }

    private void initPlugins() {
        LOGGER.info("Inicializuji pluginy.");
        final List<IPlugin> sortedPlugins = getSortedPlugins();
        for (IPlugin plugin : sortedPlugins) {
            LOGGER.info("Inicializace pluginu: {}.", plugin.getName());
            plugin.init();
        }

        LOGGER.info("Registruji handlery pluginů.");
        for (IPlugin plugin : sortedPlugins) {
            plugin.registerMessageHandlers(messageRegistrator);
        }

        LOGGER.info("Nastavuji závislosti mezi pluginy.");
        for (IPlugin plugin : sortedPlugins) {
            plugin.setupDependencies(plugins);
        }

        LOGGER.info("Inicializace pluginů dokončena.");
    }

    // endregion

    public static void main(String[] args) throws Exception {
        final Injector injector = Guice.createInjector(new ServerModule(), new PluginModule());
        final Server server = injector.getInstance(Server.class);
        server.run(args);
    }
}
