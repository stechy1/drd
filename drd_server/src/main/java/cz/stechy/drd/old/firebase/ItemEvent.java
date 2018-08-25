package cz.stechy.drd.old.firebase;

import cz.stechy.drd.net.message.DatabaseMessage.DatabaseMessageCRUD.DatabaseAction;
import java.util.Map;

/**
 * Rozhraní definující jednu akci ve firebase
 */
public interface ItemEvent {

    /**
     * Vrátí název tabulky, které se událost týká
     *
     * @return Název tabulky
     */
    String getTableName();

    /**
     * Vrátí {@link DatabaseAction} typ akce, která nastala
     *
     * @return {@link DatabaseAction}
     */
    DatabaseAction getAction();

    /**
     * Vrátí samotný item
     *
     * @return {@link Map}
     */
    Map<String, Object> getItem();
}
