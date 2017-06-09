package cz.stechy.drd.model.shop;

import cz.stechy.drd.Money;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.shop.entry.ShopEntry;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

/**
 * Třída představující nákupní košík
 */
public class ShoppingCart implements IShoppingCart {

    public final ObservableSet<ShopEntry> orderList = FXCollections.observableSet();
    public final Money totalPrice = new Money();
    private final Map<ShopEntry, AmmountListener> ammountListeners = new HashMap<>();

    {
        orderList.addListener((SetChangeListener<ShopEntry>) change -> {
            if (change.wasAdded()) {
                final ShopEntry entry = change.getElementAdded();
                final Money price = entry.getPrice();
                final MaxActValue ammount = entry.getAmmount();
                final AmmountListener listener = new AmmountListener(price);
                ammountListeners.put(entry, listener);
                ammount.actValueProperty().addListener(listener);
                totalPrice.add(new Money(ammount.getActValue().intValue() * price.getRaw()));
            }
            if (change.wasRemoved()) {
                final ShopEntry entry = change.getElementRemoved();
                final Money price = entry.getPrice();
                final MaxActValue ammount = entry.getAmmount();
                ammount.actValueProperty().removeListener(ammountListeners.get(entry));
                totalPrice.subtract(new Money(ammount.getActValue().intValue() * price.getRaw()));
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

    private class AmmountListener implements ChangeListener<Number> {

        private final Money price;

        private AmmountListener(Money price) {
            this.price = price;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue,
            Number newValue) {
            System.out.println("Changed: " + oldValue + " -> " + newValue);
            if (oldValue.intValue() == newValue.intValue()) {
                System.out.println("Hodnoty se rovnaji, nebudu aktualizovat");
                return;
            }
            int rawTotalPrice = totalPrice.getRaw();
            rawTotalPrice -= oldValue.intValue() * price.getRaw();
            rawTotalPrice += newValue.intValue() * price.getRaw();
            System.out.println(rawTotalPrice);
            totalPrice.setRaw(rawTotalPrice);
        }
    }

}
