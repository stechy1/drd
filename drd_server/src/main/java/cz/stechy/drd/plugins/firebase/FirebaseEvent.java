package cz.stechy.drd.plugins.firebase;

import cz.stechy.drd.core.event.IEvent;

public class FirebaseEvent implements IEvent {

    public static final String EVENT_NAME = "firebase-event";

    private final IFirebaseEventData eventData;

    public FirebaseEvent(IFirebaseEventData eventData) {
        this.eventData = eventData;
    }

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }

    public IFirebaseEventData getEventData() {
        return eventData;
    }

    public interface IFirebaseEventData {

        FirebaseEventDataType getEventDataType();

        enum FirebaseEventDataType {
            DATA_ADMINISTRATION, DATA_MANIPULATION
        }
    }

    public static final class FirebaseAdministrationEventData implements IFirebaseEventData {

        private final String tableName;
        private final FirebaseEntryEventListener eventListener;
        private final FirebaseAdministrationEventAction action;

        public FirebaseAdministrationEventData(FirebaseEntryEventListener eventListener,
            FirebaseAdministrationEventAction action) {
            this(null, eventListener, action);
        }

        public FirebaseAdministrationEventData(String tableName, FirebaseEntryEventListener eventListener,
            FirebaseAdministrationEventAction action) {
            this.tableName = tableName;
            this.eventListener = eventListener;
            this.action = action;
        }

        @Override
        public FirebaseEventDataType getEventDataType() {
            return FirebaseEventDataType.DATA_ADMINISTRATION;
        }

        public String getTableName() {
            return tableName;
        }

        public FirebaseEntryEventListener getEventListener() {
            return eventListener;
        }

        public FirebaseAdministrationEventAction getAction() {
            return action;
        }

        enum FirebaseAdministrationEventAction {
            REGISTER, UNREGISTER, REGISTER_ALL
        }
    }

    public static final class FirebaseDataManipulationEvent implements IFirebaseEventData {

        private final FirebaseEntryEvent entryEvent;
        private final String entryId;

        public FirebaseDataManipulationEvent(FirebaseEntryEvent entryEvent, String entryId) {
            this.entryEvent = entryEvent;
            this.entryId = entryId;
        }

        @Override
        public FirebaseEventDataType getEventDataType() {
            return FirebaseEventDataType.DATA_MANIPULATION;
        }

        public FirebaseEntryEvent getEntryEvent() {
            return entryEvent;
        }

        public String getEntryId() {
            return entryId;
        }
    }
}
