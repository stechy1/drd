package cz.stechy.drd.plugins.firebase.service;

import com.google.inject.ImplementedBy;
import cz.stechy.drd.plugins.firebase.FirebaseEntryEventListener;
import java.util.Map;

@ImplementedBy(FirebaseService.class)
public interface IFirebaseService {

    /**
     * Inicializace služby
     */
    void init();

    /**
     * Vloží záznam do firebase
     *
     * @param tableName Název tabulky
     * @param item Záznam, který se má vložit
     * @param id Id záznamu
     * @return True, pokud se záznam podařilo vložit, jinak false
     */
    boolean performInsert(final String tableName, Map<String, Object> item, String id);

    /**
     * Aktualizuje záznam ve firebase
     *
     * @param tableName Název tabulky
     * @param item Záznam, který se má aktualizovat
     * @param id Id záznamu
     * @return True, pokud se záznam podařilo aktualizovat, jinak false
     */
    boolean performUpdate(final String tableName, Map<String, Object> item, String id);

    /**
     * Odstraní záznam z firebase
     *
     * @param tableName Název tabulky
     * @param id Id záznamu
     * @return True, pokud se záznam podařilo odstranit, jinak false
     */
    boolean performDelete(final String tableName, String id);

    /**
     * Zaregistruje posluchače událostí pro danou tabulku
     *
     * @param tableName Název tabulky, pro kterou se registruje posluchač
     * @param listener {@link FirebaseEntryEventListener} posluchač událostí, který se hlásí k odběru
     */
    void registerListener(final String tableName, FirebaseEntryEventListener listener);

    /**
     * Zruší odběr událostí pro danou tabulku
     *
     * @param tableName Název tabulky, pro kterou se má odhlásit odběr událostí
     * @param listener {@link FirebaseEntryEventListener} posluchač událostí, který se má odstranit
     */
    void unregisterListener(final String tableName, FirebaseEntryEventListener listener);

    /**
     * Odhlásí odběr pro daného posluchače ze všech tabulek
     *
     * @param listener {@link FirebaseEntryEventListener} posluchač, který se má odstranit
     */
    void unregisterFromAllListeners(FirebaseEntryEventListener listener);
    
}
