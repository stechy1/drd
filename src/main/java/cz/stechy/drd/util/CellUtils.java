package cz.stechy.drd.util;

import cz.stechy.drd.Money;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.inventory.ItemSlot;
import cz.stechy.drd.model.shop.OnAddItemToCart;
import cz.stechy.drd.model.shop.OnDeleteItem;
import cz.stechy.drd.model.shop.OnDownloadItem;
import cz.stechy.drd.model.shop.OnRemoveItemFromCart;
import cz.stechy.drd.model.shop.OnUploadItem;
import cz.stechy.drd.model.shop.entry.ShopEntry;
import cz.stechy.drd.model.user.User;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

/**
 * Pomocná knihovní třída pro generování různých buněk
 */
public final class CellUtils {

    public static <S> TableCell<S, Image> forImage() {
        final ImageView imageView = new ImageView();
        {
            imageView.setFitWidth(ItemSlot.SLOT_SIZE);
            imageView.setFitHeight(ItemSlot.SLOT_SIZE);
        }
        return new TableCell<S, Image>() {
            @Override
            protected void updateItem(Image item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    imageView.setImage(item);
                    setGraphic(imageView);
                }
            }
        };
    }

    public static <S> TableCell<S, MaxActValue> forMaxActValue() {
        return new TableCell<S, MaxActValue>() {
            private final TextField input;

            {
                input = new TextField();
                input.setVisible(false);
                setGraphic(input);
                setText(null);
            }

            @Override
            public void updateItem(MaxActValue item, boolean empty) {
                // unbind old values
                if (getItem() != null) {
                    FormUtils.disposeTextFormater(input, getItem());
                }

                super.updateItem(item, empty);

                // update according to new item
                if (empty || item == null) {
                    input.setVisible(false);
                } else {
                    FormUtils.initTextFormater(input, item);
                    input.setVisible(true);
                }

            }
        };
    }

    public static <S> TableCell<S, Money> forMoney() {
        final Label text = new Label();

        return new TableCell<S, Money>() {
            @Override
            protected void updateItem(Money item, boolean empty) {
                super.updateItem(item, empty);

                if(empty) {
                    setGraphic(null);
                } else {
                    text.textProperty().bind(Bindings.concat(
                        item.gold.asString(), "zl ",
                        item.silver.asString(), "st ",
                        item.copper.asString(), "md"));
                    setGraphic(text);
                }
            }
        };
    }

}
