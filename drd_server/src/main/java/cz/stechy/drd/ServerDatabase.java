package cz.stechy.drd;

import cz.stechy.drd.firebase.ItemEventListener;
import java.util.Map;

/**
 * Rozhraní pro serverovou část databáze
 */
public interface ServerDatabase {

    /**
     * Vloží záznam do firebase
     *
     * @param tableName Název tabulky
     * @param item Záznam, který se má vložit
     * @param id Id záznamu
     */
    void performInsert(final String tableName, Map<String, Object> item, String id);

    /**
     * Aktualizuje záznam ve firebase
     *
     * @param tableName Název tabulky
     * @param item Záznam, který se má aktualizovat
     * @param id Id záznamu
     */
    void performUpdate(final String tableName, Map<String, Object> item, String id);

    /**
     * Odstraní záznam z firebase
     *
     * @param tableName Název tabulky
     * @param id Id záznamu
     */
    void performDelete(final String tableName, String id);

    /**
     * Zaregistruje posluchače událostí pro danou tabulku
     *
     * @param tableName Název tabulky, pro kterou se registruje posluchač
     * @param listener {@link ItemEventListener} posluchač událostí, který se hlásí k odběru
     */
    void registerListener(final String tableName, ItemEventListener listener);

    /**
     * Zruší odběr událostí pro danou tabulku
     *
     * @param tableName Název tabulky, pro kterou se má odhlásit odběr událostí
     * @param listener {@link ItemEventListener} posluchač událostí, který se má odstranit
     */
    void unregisterListener(final String tableName, ItemEventListener listener);

    /**
     * Odhlásí odběr pro daného posluchače ze všech tabulek
     *
     * @param listener {@link ItemEventListener} posluchač, který se má odstranit
     */
    void unregisterFromAllListeners(ItemEventListener listener);
}
