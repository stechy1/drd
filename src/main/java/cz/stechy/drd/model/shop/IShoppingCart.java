package cz.stechy.drd.model.shop;

import cz.stechy.drd.model.shop.entry.ShopEntry;

/**
 * Rozhraní pro definici kontroleru, který pomáhá při nákupu položek
 */
public interface IShoppingCart {

    void addItem(ShopEntry entry);

    void removeItem(ShopEntry entry);

}
