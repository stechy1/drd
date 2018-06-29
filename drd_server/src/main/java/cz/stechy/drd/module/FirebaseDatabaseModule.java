package cz.stechy.drd.module;

import cz.stechy.drd.Client;
import cz.stechy.drd.firebase.FirebaseRepository;
import cz.stechy.drd.firebase.ItemEventListener;
import cz.stechy.drd.net.message.DatabaseMessage;
import cz.stechy.drd.net.message.DatabaseMessage.DatabaseMessageAdministration;
import cz.stechy.drd.net.message.DatabaseMessage.DatabaseMessageCRUD;
import cz.stechy.drd.net.message.DatabaseMessage.DatabaseMessageDataType;
import cz.stechy.drd.net.message.DatabaseMessage.IDatabaseMessageData;
import cz.stechy.drd.net.message.IMessage;
import cz.stechy.drd.net.message.MessageSource;
import java.util.HashMap;
import java.util.Map;

public class FirebaseDatabaseModule implements IModule {

    // region Variables

    private final Map<Client, Map<String, ItemEventListener>> databaseListeners = new HashMap<>();
    private final FirebaseRepository serverDatabase;

    // endregion

    // region Constructors

    public FirebaseDatabaseModule(FirebaseRepository serverDatabase) {
        this.serverDatabase = serverDatabase;
    }

    // endregion

    // region Private methods

    private ItemEventListener addDatabaseListener(Client client, String tableName) {
        Map<String, ItemEventListener> clientListeners = databaseListeners
            .computeIfAbsent(client, k -> new HashMap<>());

        final ItemEventListener listener = buildListenerForClient(client);
        clientListeners.put(tableName, listener);
        return listener;
    }

    private ItemEventListener removeDatabaseListener(Client client, String tableName) {
        Map<String, ItemEventListener> clientListeners = databaseListeners.get(client);
        if (clientListeners == null) {
            return null;
        }

        final ItemEventListener listener = clientListeners.get(tableName);
        clientListeners.remove(tableName);
        return listener;
    }

    private ItemEventListener buildListenerForClient(Client client) {
        return event -> {
            final IMessage databaseMessage = new DatabaseMessage(
                MessageSource.SERVER,
                new DatabaseMessageCRUD(event.getItem(), event.getTableName(), event.getAction())
            );
            client.sendMessage(databaseMessage);
        };
    }

    // endregion

    @Override
    public void init() {
        serverDatabase.init();
    }

    @Override
    public void handleMessage(IMessage message, Client client) {
        final IDatabaseMessageData data = (IDatabaseMessageData) message.getData();
        final DatabaseMessageDataType messageDataType = data.getDataType();
        String tableName;
        switch (messageDataType) {
            case DATA_ADMINISTRATION:
                final DatabaseMessageAdministration databaseMessageAdministration = (DatabaseMessageAdministration) data;
                final DatabaseMessageAdministration.DatabaseAction action = databaseMessageAdministration
                    .getAction();
                tableName = (String) databaseMessageAdministration.getData();
                switch (action) {
                    case REGISTER:
                        serverDatabase.registerListener(tableName, addDatabaseListener(client, tableName));
                        break;
                    case UNGERISTER:
                        serverDatabase.unregisterListener(tableName, removeDatabaseListener(client, tableName));
                        break;
                    default:
                        throw new IllegalArgumentException("Neplatný parametr");
                }
                break;
            case DATA_MANIPULATION:
                final DatabaseMessageCRUD databaseMessageCRUD = (DatabaseMessageCRUD) data;
                final DatabaseMessageCRUD.DatabaseAction crudAction = databaseMessageCRUD
                    .getAction();
                final String itemId = databaseMessageCRUD.getItemId();
                tableName = databaseMessageCRUD.getTableName();
                switch (crudAction) {
                    case CREATE:
                        serverDatabase.performInsert(tableName,
                            (Map<String, Object>) databaseMessageCRUD.getData(), itemId);
                        break;
                    case UPDATE:
                        serverDatabase.performUpdate(tableName,
                            (Map<String, Object>) databaseMessageCRUD.getData(), itemId);
                        break;
                    case DELETE:
                        serverDatabase.performDelete(tableName, itemId);
                        break;
                    default:
                        throw new IllegalArgumentException("Neplatný parametr");
                }
                break;
            default:
                throw new IllegalArgumentException("Neplatný parametr");
        }
    }

    @Override
    public void onClientDisconnect(Client client) {
        final Map<String, ItemEventListener> clientListeners = databaseListeners.get(client);
        if (clientListeners == null) {
            return;
        }

        clientListeners.values().forEach(serverDatabase::unregisterFromAllListeners);
    }
}
