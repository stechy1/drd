package cz.stechy.drd.model.shop;

import cz.stechy.drd.model.shop.entry.ShopEntry;

/**
 *
 */
@FunctionalInterface
public interface OnDownloadItem {

    void onDownloadRequest(ShopEntry item);

}
