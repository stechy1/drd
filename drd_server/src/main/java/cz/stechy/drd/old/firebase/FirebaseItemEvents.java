package cz.stechy.drd.old.firebase;

import cz.stechy.drd.net.message.DatabaseMessage.DatabaseMessageCRUD.DatabaseAction;
import java.util.Map;

/**
 * Pomocná knihovní třída poskytující eventy na CRUD akce itemů
 */
public final class FirebaseItemEvents {

    /**
     * Vytvoří {@link ItemEvent} reprezentující přidání záznamu ve firebase
     *
     * @param item {@link Map} Přidaný záznam
     * @param tableName Název tabulky, do které se záznam přidal
     * @return {@link ItemEvent}
     */
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

    /**
     * Vytvoří {@link ItemEvent} reprezentující aktualizaci záznamu ve firebase
     *
     * @param item {@link Map} Aktualizovaný záznam
     * @param tableName Název tabulky, do které patří aktualizovaný záznam
     * @return {@link ItemEvent}
     */
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

    /**
     * Vytvoří {@link ItemEvent} reprezentující odstranění záznamu ve firebase
     *
     * @param item {@link Map} Odstraněný záznam
     * @param tableName Název tabulky, ve které se záznam odstranil
     * @return {@link ItemEvent}
     */
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
