package cz.stechy.drd.plugins.firebase.service;

import cz.stechy.drd.net.message.DatabaseMessage.DatabaseMessageCRUD.DatabaseAction;
import cz.stechy.drd.plugins.firebase.FirebaseEntryEvent;
import java.util.Map;

/**
 * Pomocná knihovní třída poskytující eventy na CRUD akce itemů
 */
final class FirebaseEntryEvents {

    /**
     * Vytvoří {@link FirebaseEntryEvent} reprezentující přidání záznamu ve firebase
     *
     * @param entry {@link Map} Přidaný záznam
     * @param tableName Název tabulky, do které se záznam přidal
     * @return {@link FirebaseEntryEvent}
     */
    public static FirebaseEntryEvent forChildAdded(Map<String, Object> entry, String tableName) {
        return new FirebaseEntryEvent() {
            @Override
            public String getTableName() {
                return tableName;
            }

            @Override
            public DatabaseAction getAction() {
                return DatabaseAction.CREATE;
            }

            @Override
            public Map<String, Object> getEntry() {
                return entry;
            }
        };
    }

    /**
     * Vytvoří {@link FirebaseEntryEvent} reprezentující aktualizaci záznamu ve firebase
     *
     * @param entry {@link Map} Aktualizovaný záznam
     * @param tableName Název tabulky, do které patří aktualizovaný záznam
     * @return {@link FirebaseEntryEvent}
     */
    public static FirebaseEntryEvent forChildChanged(Map<String, Object> entry, String tableName) {
        return new FirebaseEntryEvent() {
            @Override
            public String getTableName() {
                return tableName;
            }

            @Override
            public DatabaseAction getAction() {
                return DatabaseAction.UPDATE;
            }

            @Override
            public Map<String, Object> getEntry() {
                return entry;
            }
        };
    }

    /**
     * Vytvoří {@link FirebaseEntryEvent} reprezentující odstranění záznamu ve firebase
     *
     * @param entry {@link Map} Odstraněný záznam
     * @param tableName Název tabulky, ve které se záznam odstranil
     * @return {@link FirebaseEntryEvent}
     */
    public static FirebaseEntryEvent forChildRemoved(Map<String, Object> entry, String tableName) {
        return new FirebaseEntryEvent() {
            @Override
            public String getTableName() {
                return tableName;
            }

            @Override
            public DatabaseAction getAction() {
                return DatabaseAction.DELETE;
            }

            @Override
            public Map<String, Object> getEntry() {
                return entry;
            }
        };
    }
}
