package cz.stechy.drd.controller.shop;

import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.shop.IShoppingCart;
import cz.stechy.drd.model.shop.OnDeleteItem;
import cz.stechy.drd.model.shop.OnDownloadItem;
import cz.stechy.drd.model.shop.OnUploadItem;
import cz.stechy.drd.model.shop.entry.ShopEntry;
import cz.stechy.screens.Bundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;

/**
 * Rozhraní pro definici kontroleru, který zprostředkovává nakupování
 */
public interface ShopItemController {

    /**
     * Nastaví referenci na nákupní košík
     *
     * @param shoppingCart {@link IShoppingCart}
     * @param uploadHandler {@link OnUploadItem}
     * @param downloadHandler {@link OnDownloadItem}
     */
    void setShoppingCart(IShoppingCart shoppingCart, OnUploadItem uploadHandler,
        OnDownloadItem downloadHandler, OnDeleteItem deleteHandler);

    /**
     * Předá referenci na {@link IntegerProperty} představující vybraný index řádku v tabulce
     *
     * @param rowSelectedIndexProperty Vybraný index řádku v tabulce
     */
    void setRowSelectedIndexProperty(IntegerProperty rowSelectedIndexProperty);

    /**
     * Předá referenci na {@link BooleanProperty} indikující, zda-li se mají zobrazit
     * itemy z online databáze, či nikoliv
     *
     * @param showOnlineDatabase True pro online itemy, jinak offline itemy
     */
    void setShowOnlineDatabase(BooleanProperty showOnlineDatabase);

    /**
     * @return Vrátí název screenu pro editaci itemu / přidání nového itemu
     */
    String getEditScreenName();

    /**
     * Metoda zavolána, se přidal nový item
     *
     * @param item Nově přidaný item
     * @param remote True, pokud se jedná o vzdálený item, jinak se jedná o lokální item
     */
    void onAddItem(ItemBase item, boolean remote);

    /**
     * Metoda zavolána, pokud se item na indexu aktualizoval
     *
     * @param item Item, který se má aktualizovat
     */
    void onUpdateItem(ItemBase item);

    /**
     * Transformuje item na zadaném indexu na bundle
     *
     * @param bundle {@link Bundle} Bundle, do kterého se vloží parametry itemu
     * @param index Index, na kterém se má item hledat
     */
    void insertItemToBundle(Bundle bundle, int index);

    /**
     * Transformuje bundle na konkrétní implementaci itemu
     *
     * @param bundle {@link Bundle} Bundle obsahující parametry itemu
     * @return {@link ItemBase} Konktérní implementaci itemu
     */
    ItemBase fromBundle(Bundle bundle);

    /**
     * Metoda je zavolána, pokud je požadavek na odstranění lokálního itemu
     *
     * @param index Index itemu, který se má odstranit
     */
    void requestRemoveItem(int index);

    /**
     * Požadavek na odstranění itemu
     *
     * @param item Item, který se má odstranit
     * @param remote True, pokud se má odstranit lokální item ze vzdálené databáze, jinak se
     * odstraní vzdálen item z lokální databáze
     */
    void requestRemoveItem(ShopEntry item, boolean remote);

    /**
     * Metoda zavolána, pokud chce uživatel uploadovat item do veřejné databáze
     *
     * @param item Item, který se má nahrát
     */
    void uploadRequest(ItemBase item);

    /**
     * Zruší označení řádky z tabulky
     */
    void clearSelectedRow();

    /**
     * Metoda se zavolá při zavření okna s obchodem
     */
    void onClose();
}
