package cz.stechy.drd.controller.shop;

import cz.stechy.drd.model.db.base.Firebase.OnDeleteItem;
import cz.stechy.drd.model.db.base.Firebase.OnDownloadItem;
import cz.stechy.drd.model.db.base.Firebase.OnUploadItem;
import cz.stechy.drd.model.shop.OnAddItemToCart;
import cz.stechy.drd.model.shop.OnRemoveItemFromCart;
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
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

/**
 * Pomocní třída pro kontrolery s obchodem
 */
final class ShopHelper {

    // region Constants

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
        final OnUploadItem<S> uploadHandler, final OnDownloadItem<S> downloadHandler,
        final OnDeleteItem<S> deleteHandler, User user, ResourceBundle resources) {
        final String resourceAdd = resources.getString("drd_shop_item_cart_add");
        final String resourceRemove = resources.getString("drd_firebase_entry_remove");
        final String resourceUpload = resources.getString("drd_firebase_entry_upload");
        final String resourceDownload = resources.getString("drd_firebase_entry_download");

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

    // endregion

}
