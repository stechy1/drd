package cz.stechy.drd.controller.shop;

import cz.stechy.drd.model.shop.OnAddItemToCart;
import cz.stechy.drd.model.shop.OnRemoveItemFromCart;
import cz.stechy.drd.model.shop.entry.ShopEntry;
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
import javafx.scene.control.ContentDisplay;
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
        final String resourceAdd = resources.getString("drd_shop_item_cart_add");
        final String resourceRemove = resources.getString("drd_shop_item_cart_remove");

        return new TableCell<S, T>() {
            final Button btnAddRemove = new Button();
            final HBox container = new HBox(btnAddRemove);

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
                } else {
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    final S entry = getTableView().getItems().get(getIndex());
                    final ObjectProperty<EventHandler<ActionEvent>> addHandlerInternal = new SimpleObjectProperty<>();
                    final ObjectProperty<EventHandler<ActionEvent>> removeHandlerInternal = new SimpleObjectProperty<>();

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

                    BooleanBinding addRemoveCondition = Bindings
                        .or(entry.inShoppingCartProperty(),
                            entry.getAmmount().actValueProperty().isEqualTo(0));

                    btnAddRemove.disableProperty().bind(Bindings
                        .or(Bindings
                            .and(entry.inShoppingCartProperty().not(),
                                entry.getAmmount().actValueProperty().isEqualTo(0)),
                            cartEditable));
                    btnAddRemove.textProperty().bind(Bindings
                        .when(addRemoveCondition)
                        .then(resourceRemove)
                        .otherwise(resourceAdd));
                    btnAddRemove.onActionProperty().bind(Bindings
                        .when(addRemoveCondition)
                        .then(removeHandlerInternal)
                        .otherwise(addHandlerInternal));

                    setGraphic(container);
                }
            }
        };
    }

    // endregion

}
