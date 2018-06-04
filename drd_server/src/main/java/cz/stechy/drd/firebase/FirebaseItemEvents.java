package cz.stechy.drd.firebase;

import cz.stechy.drd.net.message.DatabaseMessage.DatabaseMessageCRUD.DatabaseAction;
import java.util.Map;

/**
 * Pomocná knihovní třída poskytující eventy na CRUD akce itemů
 */
public final class FirebaseItemEvents {

    public static ItemEvent forChildAdded(Map<String, Object> item, String tableName) {
        return new ItemEvent() {
            @Override
            public String getTableName() {
                return tableName;
            }

            @Override
            public DatabaseAction getAction() {
                return DatabaseAction.CREATE;
            }

            @Override
            public Map<String, Object> getItem() {
                return item;
            }
        };
    }

    public static ItemEvent forChildChanged(Map<String, Object> item, String tableName) {
        return new ItemEvent() {
            @Override
            public String getTableName() {
                return tableName;
            }

            @Override
            public DatabaseAction getAction() {
                return DatabaseAction.UPDATE;
            }

            @Override
            public Map<String, Object> getItem() {
                return item;
            }
        };
    }

    public static ItemEvent forChildRemoved(Map<String, Object> item, String tableName) {
        return new ItemEvent() {
            @Override
            public String getTableName() {
                return tableName;
            }

            @Override
            public DatabaseAction getAction() {
                return DatabaseAction.DELETE;
            }

            @Override
            public Map<String, Object> getItem() {
                return item;
            }
        };
    }
}
