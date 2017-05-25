package cz.stechy.drd.model.shop;

import cz.stechy.drd.model.shop.entry.ShopEntry;

/**
 *
 */
@FunctionalInterface
public interface OnUploadItem {

    void onUploadRequest(ShopEntry item);

}
