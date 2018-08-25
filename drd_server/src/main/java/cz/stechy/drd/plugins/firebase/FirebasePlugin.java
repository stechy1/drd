package cz.stechy.drd.plugins.firebase;

import com.google.inject.Inject;
import cz.stechy.drd.core.event.IEvent;
import cz.stechy.drd.core.event.IEventBus;
import cz.stechy.drd.plugins.IPlugin;
import cz.stechy.drd.plugins.PluginPriority;
import cz.stechy.drd.plugins.firebase.FirebaseEvent.FirebaseAdministrationEventData;
import cz.stechy.drd.plugins.firebase.FirebaseEvent.FirebaseDataManipulationEvent;
import cz.stechy.drd.plugins.firebase.FirebaseEvent.IFirebaseEventData;
import cz.stechy.drd.plugins.firebase.service.IFirebaseService;
import java.util.Map;

@PluginPriority(priority = 10)
public class FirebasePlugin implements IPlugin {

    // region Constants

    public static final String PLUGIN_NAME = "firebase";

    // endregion

    // region Variables

    private final IFirebaseService firebaseService;

    // endregion

    // region Constructors

    @Inject
    public FirebasePlugin(IFirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    // endregion

    // region Private methods

    private void callInsert(final String tableName, Map<String, Object> item, String id) {
        firebaseService.performInsert(tableName, item, id);
    }

    private void callUpdate(final String tableName, Map<String, Object> item, String id) {
        firebaseService.performUpdate(tableName, item, id);
    }

    private void callDelete(final String tableName, String id) {
        firebaseService.performDelete(tableName, id);
    }

    private void callRegisterListener(final String tableName, FirebaseEntryEventListener listener) {
        firebaseService.registerListener(tableName, listener);
    }

    private void callUnregisterListener(final String tableName, FirebaseEntryEventListener listener) {
        firebaseService.unregisterListener(tableName, listener);
    }

    private void callUnregisterFromAllListeners(FirebaseEntryEventListener listener) {
        firebaseService.unregisterFromAllListeners(listener);
    }

    private void firebaseMessageHandler(IEvent event) {
        assert event instanceof FirebaseEvent;
        FirebaseEvent firebaseEvent = (FirebaseEvent) event;
        final IFirebaseEventData eventData = firebaseEvent.getEventData();
        switch (eventData.getEventDataType()) {
            case DATA_ADMINISTRATION:
                FirebaseAdministrationEventData firebaseAdministrationEventData = (FirebaseAdministrationEventData) eventData;
                switch (firebaseAdministrationEventData.getAction()) {
                    case REGISTER:
                        callRegisterListener(firebaseAdministrationEventData.getTableName(), firebaseAdministrationEventData.getEventListener());
                        break;
                    case UNREGISTER:
                        callUnregisterListener(firebaseAdministrationEventData.getTableName(), firebaseAdministrationEventData.getEventListener());
                        break;
                    case REGISTER_ALL:
                        callUnregisterFromAllListeners(firebaseAdministrationEventData.getEventListener());
                        break;
                    default:
                        throw new IllegalArgumentException("Neplatná akce.");
                }
                break;
            case DATA_MANIPULATION:
                FirebaseDataManipulationEvent firebaseDataManipulationEvent = (FirebaseDataManipulationEvent) eventData;
                final FirebaseEntryEvent entryEvent = firebaseDataManipulationEvent.getEntryEvent();
                switch (entryEvent.getAction()) {
                    case CREATE:
                        callInsert(entryEvent.getTableName(), entryEvent.getEntry(), firebaseDataManipulationEvent
                            .getEntryId());
                        break;
                    case UPDATE:
                        callUpdate(entryEvent.getTableName(), entryEvent.getEntry(), firebaseDataManipulationEvent
                            .getEntryId());
                        break;
                    case DELETE:
                        callDelete(entryEvent.getTableName(), firebaseDataManipulationEvent.getEntryId());
                        break;
                    default:
                        throw new IllegalArgumentException("Neplatná akce.");
                }
                break;
            default:
                throw new IllegalArgumentException("Neplatný typ události.");
        }
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
        eventBus.registerEventHandler(PLUGIN_NAME, this::firebaseMessageHandler);
    }
}
