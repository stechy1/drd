package cz.stechy.drd.app.shop;

import cz.stechy.drd.R;
import cz.stechy.drd.app.MoneyController;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.app.shop.entry.ShopEntry;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

/**
 * Pomocní třída pro kontrolery s obchodem
 */
final class ShopHelper {

    // region Constants

    public static final int SHOP_ROW_HEIGHT = 40;
    public static final String ITEM_ACTION = "item_action";
    public static final int ITEM_ACTION_ADD = 1;
    public static final int ITEM_ACTION_UPDATE = 2;

    // endregion

    // region Constructors

    /**
     * Privátní konstruktor pro zabránění vytvoření instance
     */
    private ShopHelper() {
        throw new AssertionError();
    }

    public static <S extends ShopEntry, T> TableCell<S, T> forActionButtons(
        final OnAddItemToCart<S> addHandler, final OnRemoveItemFromCart<S> removeHandler,
        ResourceBundle resources, BooleanProperty cartEditable) {
        final String resourceAdd = resources.getString(R.Translate.SHOP_ITEM_CART_ADD);
        final String resourceRemove = resources.getString(R.Translate.SHOP_ITEM_CART_REMOVE);
        final String resourceNoAction = resources.getString(R.Translate.NO_ACTION);

        return new TableCell<S, T>() {
            final Button btnAddRemove = new Button();
            final HBox container = new HBox(btnAddRemove);
            final ObjectProperty<EventHandler<ActionEvent>> addHandlerInternal = new SimpleObjectProperty<>();
            final ObjectProperty<EventHandler<ActionEvent>> removeHandlerInternal = new SimpleObjectProperty<>();
            boolean initialized = false;
            int oldIndex = -1;

            {
                container.setSpacing(8.0);
                container.setAlignment(Pos.CENTER);

                btnAddRemove.setPrefHeight(15);
                btnAddRemove.setFont(Font.font(10));
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                    initialized = false;
                    oldIndex = -1;
                } else {
                    if (oldIndex != -1 && oldIndex != getIndex()) {
                        btnAddRemove.disableProperty().unbind();
                        btnAddRemove.textProperty().unbind();
                        btnAddRemove.onActionProperty().unbind();
                        addHandlerInternal.setValue(null);
                        removeHandlerInternal.setValue(null);
                        initialized = false;
                    }
                    if (!initialized) {
                        oldIndex = getIndex();
                        final S entry = getTableView().getItems().get(oldIndex);
                        addHandlerInternal.setValue(event -> {
                            if (addHandler != null) {
                                addHandler.onAdd(entry);
                            }
                        });

                        removeHandlerInternal.setValue(event -> {
                            if (removeHandler != null) {
                                removeHandler.onRemove(entry);
                            }
                            entry.getAmmount().setActValue(0);
                        });

                        final BooleanBinding addRemoveCondition = Bindings.createBooleanBinding(() ->
                                entry.getAmmount().actValueProperty().get() == null
                                    || entry.getAmmount().actValueProperty().get().intValue() == 0,
                            entry.getAmmount().actValueProperty());

                        btnAddRemove.disableProperty().bind(Bindings
                            .or(entry.inShoppingCartProperty().not().and(addRemoveCondition),
                                cartEditable));
                        btnAddRemove.textProperty().bind(Bindings
                            .when(entry.inShoppingCartProperty())
                            .then(resourceRemove)
                            .otherwise(resourceAdd));
                        btnAddRemove.onActionProperty().bind(Bindings
                            .when(entry.inShoppingCartProperty())
                            .then(removeHandlerInternal)
                            .otherwise(addHandlerInternal));

                        initialized = true;
                    }
                    setGraphic(container);
                }
            }
        };
    }

    // endregion

    // region Public static methods

    /**
     * Nastaví cenu předmětu z bundlu, pokud byl požadavek úspěšný
     *
     * @param statusCode Výsledek (ne)úspěchu requestu
     * @param actionId Kód akce
     * @param requestedAction Požadovaný kód akce
     * @param bundle {@link Bundle}
     * @param price {@link Money}
     */
    public static void setItemPrice(int statusCode, int actionId, int requestedAction, Bundle bundle, Money price) {
        if (actionId != requestedAction || statusCode != BaseController.RESULT_SUCCESS) {
            return;
        }

        price.setRaw(bundle.getInt(MoneyController.MONEY));
    }

    // endregion
}
