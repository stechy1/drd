package cz.stechy.drd.model.shop;

import cz.stechy.drd.Money;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.shop.entry.ShopEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

/**
 * Třída představující nákupní košík
 */
public class ShoppingCart implements IShoppingCart {

    // region Variables

    private final Map<ShopEntry, AmmountListener> ammountListeners = new HashMap<>();
    private final ReadOnlyBooleanWrapper enoughtMoney = new ReadOnlyBooleanWrapper();

    public final ObservableSet<ShopEntry> orderList = FXCollections.observableSet();
    public final Money totalPrice = new Money();

    private Hero hero;

    // endregion

    /**
     * Vytvoří novou instanci nákupnho košíku
     *
     * @param hero Hrdina, pro který je nákupní košík určen
     */
    public ShoppingCart(Hero hero) {
        this.hero = hero;
        orderList.addListener(orderListListener);
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

    @Override
    public boolean containsEntry(ShopEntry entry) {
        return orderList.contains(entry);
    }

    @Override
    public Optional<ShopEntry> getEntry(String id) {
        return orderList.stream()
            .filter(entry -> id.equals(entry.getId())).findFirst();
    }

    public ReadOnlyBooleanProperty enoughtMoneyProperty() {
        return enoughtMoney.getReadOnlyProperty();
    }

    private final SetChangeListener<ShopEntry> orderListListener = change -> {
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
        enoughtMoney.set(hero.getMoney().getRaw() >= totalPrice.getRaw());
    };

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
            System.out.println(price.getRaw());
            totalPrice.setRaw(rawTotalPrice);
        }
    }

}
