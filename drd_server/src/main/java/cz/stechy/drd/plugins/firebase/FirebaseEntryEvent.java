package cz.stechy.drd.plugins.firebase;

import cz.stechy.drd.core.event.IEvent;
import cz.stechy.drd.net.message.DatabaseMessage.DatabaseMessageCRUD.DatabaseAction;
import java.util.Map;

/**
 * Rozhraní definující jednu akci ve firebase
 */
public interface FirebaseEntryEvent extends IEvent {

    String EVENT_NAME = "firebase-entry-event";

    @Override
    default String getEventName() {
        return EVENT_NAME;
    }

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
     * Vrátí samotný záznam
     *
     * @return {@link Map}
     */
    Map<String, Object> getEntry();
}
