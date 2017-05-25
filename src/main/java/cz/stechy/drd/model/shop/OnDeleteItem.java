package cz.stechy.drd.model.shop;

import cz.stechy.drd.model.shop.entry.ShopEntry;

/**
 *
 */
@FunctionalInterface
public interface OnDeleteItem {

    void onDeleteRequest(ShopEntry item, boolean remote);

}
