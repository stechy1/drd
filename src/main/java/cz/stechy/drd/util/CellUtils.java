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

    public static <S extends ShopEntry, T> TableCell<S, T> forActionButtons(
        final OnAddItemToCart<S> addHandler, final OnRemoveItemFromCart<S> removeHandler,
        final OnUploadItem uploadHandler, final OnDownloadItem downloadHandler,
        final OnDeleteItem deleteHandler, User user, ResourceBundle resources) {
        final String resourceAdd = resources.getString("drd_shop_item_cart_add");
        final String resourceRemove = resources.getString("drd_shop_item_cart_remove");
        final String resourceUpload = resources.getString("drd_shop_item_upload");
        final String resourceDownload = resources.getString("drd_shop_item_download");

        return new TableCell<S, T>() {
            final Button btnAddRemove = new Button();
            final Button btnRemote = new Button();
            final HBox container = new HBox(btnAddRemove, btnRemote);

            {
                container.setSpacing(8.0);
                container.setAlignment(Pos.CENTER);

                btnAddRemove.setPrefHeight(15);
                btnAddRemove.setFont(Font.font(10));

                btnRemote.setPrefHeight(15);
                btnRemote.setFont(Font.font(10));
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
                    final ObjectProperty<EventHandler<ActionEvent>> downloadHandlerInternal = new SimpleObjectProperty<>();
                    final ObjectProperty<EventHandler<ActionEvent>> uploadHandlerInternal = new SimpleObjectProperty<>();
                    final ObjectProperty<EventHandler<ActionEvent>> deleteFromLocalDatabaseInternal = new SimpleObjectProperty<>();
                    final ObjectProperty<EventHandler<ActionEvent>> deleteFromRemoteDatabaseInternal = new SimpleObjectProperty<>();

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

                    downloadHandlerInternal.setValue(event -> {
                        if (downloadHandler != null) {
                            downloadHandler.onDownloadRequest(entry);
                        }
                    });

                    uploadHandlerInternal.setValue(event -> {
                        if (uploadHandler != null) {
                            uploadHandler.onUploadRequest(entry);
                        }
                    });

                    deleteFromLocalDatabaseInternal.setValue(event -> {
                        if (deleteHandler != null) {
                            deleteHandler.onDeleteRequest(entry, false);
                        }
                    });

                    deleteFromRemoteDatabaseInternal.setValue(event -> {
                        if (deleteHandler != null) {
                            deleteHandler.onDeleteRequest(entry, true);
                        }
                    });

                    BooleanBinding addRemoveCondition = Bindings
                        .or(entry.inShoppingCartProperty(),
                            entry.getAmmount().actValueProperty().isEqualTo(0));

                    btnAddRemove.disableProperty().bind(Bindings
                        .and(entry.inShoppingCartProperty().not(),
                            entry.getAmmount().actValueProperty().isEqualTo(0)));
                    btnAddRemove.textProperty().bind(Bindings
                        .when(addRemoveCondition)
                        .then(resourceRemove)
                        .otherwise(resourceAdd));
                    btnAddRemove.onActionProperty().bind(Bindings
                        .when(addRemoveCondition)
                        .then(removeHandlerInternal)
                        .otherwise(addHandlerInternal));

                    btnRemote.textProperty().bind(Bindings
                        .when(entry.authorProperty()
                            .isEqualTo(user.nameProperty())) // Autor jsem já
                        .then(Bindings
                            .when(entry.uploadedProperty())
                            .then(resourceRemove).otherwise(resourceUpload))
                        .otherwise(Bindings
                            .when(entry.downloadedProperty())
                            .then(resourceRemove).otherwise(resourceDownload)));
                    btnRemote.onActionProperty().bind(Bindings
                        .when(entry.authorProperty()
                            .isEqualTo(user.nameProperty())) // Autor jsem já
                        .then(Bindings
                            .when(entry.uploadedProperty())
                            .then(deleteFromRemoteDatabaseInternal)
                            .otherwise(uploadHandlerInternal))
                        .otherwise(Bindings
                            .when(entry.downloadedProperty())
                            .then(deleteFromLocalDatabaseInternal)
                            .otherwise(downloadHandlerInternal)));
                    btnRemote.disableProperty().bind(user.loggedProperty().not());
                    setGraphic(container);
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
