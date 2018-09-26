package cz.stechy.drd.app.shop;

import cz.stechy.drd.app.shop.entry.ShopEntry;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.screens.Bundle;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TableRow;

/**
 * Rozhraní pro definici kontroleru, který zprostředkovává nakupování
 */
interface ShopItemController<T extends ShopEntry> {

    /**
     * Nastaví referenci na nákupní košík
     *
     * @param shoppingCart {@link IShoppingCart}
     */
    void setShoppingCart(IShoppingCart shoppingCart);

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
     * Přidá referenci na {@link BooleanProperty} indikující, zda-li se mají zvýraznit
     * itemy, které mají odlišné hodnoty od online záznamů
     *
     * @param highlightDiffItems True, pokud se mají zvýraznit rozdílné itemy, jinak False
     */
    void setHighlightDiffItems(BooleanProperty highlightDiffItems);

    /**
     * Přidá referenci na {@link BooleanProperty} indikující, zda-li je možné přidávat věci do
     * košíku či nikoliv
     *
     * @param ammountEditable True, pokud je možné editovat košík, jinak False
     */
    void setAmmountEditableProperty(BooleanProperty ammountEditable);

    /**
     * Přidá referenci na {@link ShopNotificationProvider} který zpřístupnění zobrazování notifikací
     *
     * @param notificationProvider {@link ShopNotificationProvider}
     */
    void setNotificationProvider(ShopNotificationProvider notificationProvider);

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
     * Synchronizuje online předměty s offline databází podle přihlášeného uživatele.
     * Všechny předměty, které uživatel nahrál do online databáze se uloží do offline
     * databáze pouze v případě, se ještě tak nestalo.
     */
    void synchronizeItems();

    /**
     * Vrátí {@link Optional} který může obsahovat referenci na vybraný předmět
     *
     * @return {@link Optional<ItemBase>}
     */
    Optional<T> getSelectedItem();

    /**
     * Aktualizuje lokální záznam předmětu z online databáze
     *
     * @param itemBase {@link ItemBase} Lokální předmět, který se aktualizuje z online databáze
     */
    void updateLocalItem(ShopEntry itemBase);

    /**
     * Aktualizuje online záznam předmětu z lokální databáze
     *
     * @param itemBase {@link ItemBase} Lokální předmět, který aktualizuje online záznam
     */
    void updateOnlineItem(ShopEntry itemBase);

    class ShopRow<T extends ShopEntry> extends TableRow<T> {

        private final BooleanProperty highlightDiffItem;
        private final StringProperty style = new SimpleStringProperty("");

        private T oldItem;

        ShopRow(BooleanProperty highlightDiffItem) {
            this.highlightDiffItem = highlightDiffItem;
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                if (oldItem != null) {
                    oldItem.hasDiffProperty().removeListener(diffListener);
                }
                styleProperty().unbind();
                setStyle("");
                highlightDiffItem.removeListener(ShopRow.this.diffListener);
            } else {
                styleProperty().bind(style);
                highlightDiffItem.addListener(ShopRow.this.diffListener);
                if (highlightDiffItem.get() && item.hasDiff()) {
                    style.setValue("-fx-background-color: tomato;");
                }
                item.hasDiffProperty().addListener(diffListener);
                oldItem = item;
            }
        }

        private final ChangeListener<? super Boolean> diffListener = (observable, oldValue, newValue) -> {
            if (newValue == null || !newValue || getItem() == null) {
                style.setValue("");
            } else {
                if (getItem().hasDiff()) {
                    style.setValue("-fx-background-color: tomato;");
                } else {
                    style.setValue("");
                }
            }
        };
    }
}
