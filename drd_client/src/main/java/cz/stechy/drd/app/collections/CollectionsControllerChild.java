package cz.stechy.drd.app.collections;

import cz.stechy.drd.model.item.ItemCollection;
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
     * Požadavek na přidání nového záznamu do kolekce
     *  @param collection Kolekce, do které se má záznam vložit
     *
     */
    void requestAddEntryToCollection(ItemCollection collection);

    /**
     * Požadavek na odstranění vybraného záznamu z kolekce
     *
     * @param collection Kolekce, ze které se má záznam odstranit
     */
    void requestRemoveSelectedEntryFromCollection(ItemCollection collection);

    /**
     * Uloži ty záznamy, které nejsou dostupné offline
     */
    void mergeEntries();

}
