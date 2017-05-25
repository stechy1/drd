package cz.stechy.drd.model.shop;

import cz.stechy.drd.model.shop.entry.ShopEntry;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

/**
 * Třída představující nákupní košík
 */
public class ShoppingCart implements IShoppingCart {

    public final ObservableSet<ShopEntry> orderList = FXCollections.observableSet();
    public final IntegerProperty totalPrice = new SimpleIntegerProperty();

    {
        orderList.addListener((SetChangeListener<ShopEntry>) change -> {
            if (change.wasAdded()) {
                totalPrice.add(change.getElementAdded().getPrice().getRaw());
            }
            if (change.wasRemoved()) {
                totalPrice.subtract(change.getElementRemoved().getPrice().getRaw());
            }
        });
    }

    @Override
    public void addItem(ShopEntry entry) {
        orderList.add(entry);
        entry.setInShoppingCart(true);
    }

    @Override
    public void removeItem(ShopEntry entry) {
        orderList.remove(entry);
        entry.setInShoppingCart(false);
    }

}
