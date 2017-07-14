package cz.stechy.drd.model.shop;

import cz.stechy.drd.model.shop.entry.ShopEntry;

/**
 *
 */
@FunctionalInterface
@Deprecated
public interface OnUploadItem {

    void onUploadRequest(ShopEntry item);

}
