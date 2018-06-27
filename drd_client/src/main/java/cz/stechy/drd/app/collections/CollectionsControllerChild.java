package cz.stechy.drd.app.collections;

import cz.stechy.drd.model.item.ItemCollection;
import cz.stechy.drd.model.item.ItemCollection.CollectionType;
import cz.stechy.drd.util.DialogUtils.ChoiceEntry;
import java.util.Optional;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;

/**
 * Rozhraní definující metody pro kontrolery jednotlivých kolekcí
 */
public interface CollectionsControllerChild {

    /**
     * Nastaví vybraný záznam
     *
     * @param selectedEntry
     */
    void setSelectedEntryProperty(StringProperty selectedEntry);

    /**
     * Nastaví vybranou kolekci
     *
     * @param selectedCollection {@link ItemCollection} Kolekce, která jse vybraná
     */
    void setSelectedCollection(ReadOnlyObjectProperty<ItemCollection> selectedCollection);

    /**
     * Nastaví poskytovatele notifikací
     *
     * @param notificationProvider {@link CollectionsNotificationProvider}
     */
    void setNotificationProvider(CollectionsNotificationProvider notificationProvider);

    /**
     * Vrátí typ kolekce, který daný potomek reprezentuje
     *
     * @return {@link CollectionType} Typ kolekce
     */
    CollectionType getCollectionType();

    /**
     * Vrátí vybraný záznam
     *
     * @return {@link ChoiceEntry}
     */
    Optional<ChoiceEntry> getSelectedEntry();

    /**
     * Uloži ty záznamy, které nejsou dostupné offline
     */
    void mergeEntries();

}
