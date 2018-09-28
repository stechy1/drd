package cz.stechy.drd.plugins.firebase;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.stechy.drd.core.connection.Client;
import cz.stechy.drd.core.connection.ClientDisconnectedEvent;
import cz.stechy.drd.core.connection.IClient;
import cz.stechy.drd.core.connection.MessageReceivedEvent;
import cz.stechy.drd.core.event.IEvent;
import cz.stechy.drd.core.event.IEventBus;
import cz.stechy.drd.net.message.DatabaseMessage;
import cz.stechy.drd.net.message.DatabaseMessage.DatabaseMessageAdministration;
import cz.stechy.drd.net.message.DatabaseMessage.DatabaseMessageCRUD;
import cz.stechy.drd.net.message.DatabaseMessage.IDatabaseMessageData;
import cz.stechy.drd.net.message.IMessage;
import cz.stechy.drd.net.message.MessageSource;
import cz.stechy.drd.plugins.IPlugin;
import cz.stechy.drd.plugins.PluginConfiguration;
import cz.stechy.drd.plugins.firebase.service.IFirebaseService;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@PluginConfiguration(priority = 10)
public class FirebasePlugin implements IPlugin {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(FirebasePlugin.class);

    public static final String PLUGIN_NAME = "firebase";

    // endregion

    // region Variables

    private final Map<IClient, Map<String, FirebaseEntryEventListener>> databaseListeners = new HashMap<>();
    private final IFirebaseService firebaseService;

    // endregion

    // region Constructors

    @Inject
    public FirebasePlugin(IFirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    // endregion

    // region Private methods

    private FirebaseEntryEventListener addDatabaseListener(IClient client, String tableName) {
        LOGGER.debug("Vytvářím listener pro klienta: {} na tabulku: {}", client, tableName);
        Map<String, FirebaseEntryEventListener> clientListeners = databaseListeners
            .computeIfAbsent(client, k -> new HashMap<>());

        final FirebaseEntryEventListener listener = buildListenerForClient(client);
        clientListeners.put(tableName, listener);
        return listener;
    }

    private FirebaseEntryEventListener removeDatabaseListener(IClient client, String tableName) {
        Map<String, FirebaseEntryEventListener> clientListeners = databaseListeners.get(client);
        if (clientListeners == null) {
            return null;
        }

        final FirebaseEntryEventListener listener = clientListeners.get(tableName);
        clientListeners.remove(tableName);
        return listener;
    }

    private FirebaseEntryEventListener buildListenerForClient(IClient client) {
        return event -> {
            final IMessage databaseMessage = new DatabaseMessage(
                MessageSource.SERVER,
                new DatabaseMessageCRUD(event.getEntry(), event.getTableName(), event.getAction())
            );
            client.sendMessageAsync(databaseMessage);
        };
    }

    @SuppressWarnings("unchecked")
    private void firebaseMessageHandler(IEvent event) {
        assert event instanceof MessageReceivedEvent;
        final MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent) event;
        final IClient client = messageReceivedEvent.getClient();
        final DatabaseMessage databaseMessage = (DatabaseMessage) messageReceivedEvent.getReceivedMessage();
        final IDatabaseMessageData databaseMessageData = (IDatabaseMessageData) databaseMessage.getData();
        String tableName;
        switch (databaseMessageData.getDataType()) {
            case DATA_ADMINISTRATION:
                final DatabaseMessageAdministration databaseMessageAdministration = (DatabaseMessageAdministration) databaseMessageData;
                final DatabaseMessageAdministration.DatabaseAction action = databaseMessageAdministration.getAction();
                tableName = (String) databaseMessageAdministration.getData();
                switch (action) {
                    case REGISTER:
                        firebaseService.registerListener(tableName, addDatabaseListener(client, tableName));
                        break;
                    case UNGERISTER:
                        firebaseService.unregisterListener(tableName, removeDatabaseListener(client, tableName));
                        break;
                    default:
                        throw new IllegalArgumentException("Neplatný parametr.");
                }
                break;
            case DATA_MANIPULATION:
                final DatabaseMessageCRUD databaseMessageCRUD = (DatabaseMessageCRUD) databaseMessageData;
                final DatabaseMessageCRUD.DatabaseAction crudAction = databaseMessageCRUD.getAction();
                final String itemId = databaseMessageCRUD.getItemId();
                tableName = databaseMessageCRUD.getTableName();
                boolean success;
                switch (crudAction) {
                    case CREATE:
                        success = firebaseService.performInsert(tableName, (Map<String, Object>) databaseMessageCRUD.getData(), itemId);
                        break;
                    case UPDATE:
                        success = firebaseService.performUpdate(tableName, (Map<String, Object>) databaseMessageCRUD.getData(), itemId);
                        break;
                    case DELETE:
                        success = firebaseService.performDelete(tableName, itemId);
                        break;
                    default:
                        throw new IllegalArgumentException("Neplatný parametr.");
                }

                client.sendMessageAsync(databaseMessage.getResponce(success));
                break;
            default:
                throw new IllegalArgumentException("Neplatný parametr.");
        }
    }

    private void clientDisconnectHandler(IEvent event) {
        assert event instanceof ClientDisconnectedEvent;
        final ClientDisconnectedEvent clientDisconnectedEvent = (ClientDisconnectedEvent) event;
        final Client client = clientDisconnectedEvent.getClient();
        final Map<String, FirebaseEntryEventListener> clientListeners = databaseListeners.get(client);

        if (clientListeners == null) {
            return;
        }

        clientListeners.values().forEach(firebaseService::unregisterFromAllListeners);
    }

    // endregion

    @Override
    public String getName() {
        return PLUGIN_NAME;
    }

    @Override
    public void init() {
        firebaseService.init();
    }

    @Override
    public void registerMessageHandlers(IEventBus eventBus) {
        eventBus.registerEventHandler(DatabaseMessage.MESSAGE_TYPE, this::firebaseMessageHandler);
        eventBus.registerEventHandler(ClientDisconnectedEvent.EVENT_NAME, this::clientDisconnectHandler);
    }
}
