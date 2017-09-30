package cz.stechy.drd.model.shop;

import cz.stechy.drd.model.shop.entry.ShopEntry;
import java.util.Optional;

/**
 * Rozhraní pro definici kontroleru, který pomáhá při nákupu položek
 */
public interface IShoppingCart {

    /**
     * Přidá obchodní položku do košíku
     *
     * @param entry {@link ShopEntry}
     */
    void addItem(ShopEntry entry);

    /**
     * Odebere obchodní položku z košíku
     *
     * @param entry {@link ShopEntry}
     */
    void removeItem(ShopEntry entry);

    /**
     * Zjistí, zda-li nákupní košík obsahuje položku
     *
     * @param entry {@link ShopEntry}
     * @return True, pokud nákupní košík obsahuje položku, jinak false
     */
    boolean containsEntry(ShopEntry entry);

    /**
     * Vrátí nákupní položku podle ID
     *
     * @param id ID nákupní položky
     * @return {@link ShopEntry}
     */
    Optional<ShopEntry> getEntry(String id);

}
