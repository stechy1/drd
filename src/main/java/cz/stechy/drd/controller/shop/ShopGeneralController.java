package cz.stechy.drd.controller.shop;

import cz.stechy.drd.Money;
import cz.stechy.drd.R;
import cz.stechy.drd.model.Context;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.db.AdvancedDatabaseManager;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.item.GeneralItem;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.shop.IShoppingCart;
import cz.stechy.drd.model.shop.OnDeleteItem;
import cz.stechy.drd.model.shop.OnDownloadItem;
import cz.stechy.drd.model.shop.OnUploadItem;
import cz.stechy.drd.model.shop.entry.GeneralEntry;
import cz.stechy.drd.model.shop.entry.ShopEntry;
import cz.stechy.drd.model.user.User;
import cz.stechy.drd.util.CellUtils;
import cz.stechy.drd.util.ObservableMergers;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pomocný kontroler pro obchod se obecnými předměty
 */
public class ShopGeneralController implements Initializable, ShopItemController {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(ShopGeneralController.class);

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TableView<GeneralEntry> tableGeneralItems;
    @FXML
    private TableColumn<GeneralEntry, Image> columnImage;
    @FXML
    private TableColumn<GeneralEntry, String> columnName;
    @FXML
    private TableColumn<GeneralEntry, String> columnAuthor;
    @FXML
    private TableColumn<GeneralEntry, Integer> columnWeight;
    @FXML
    private TableColumn<GeneralEntry, Money> columnPrice;
    @FXML
    private TableColumn<GeneralEntry, MaxActValue> columnAmmount;
    @FXML
    private TableColumn<GeneralEntry, ?> columnAction;

    // endregion

    private final ObservableList<GeneralEntry> generalItems = FXCollections.observableArrayList();
    private final AdvancedDatabaseManager<GeneralItem> manager;
    private final User user;

    private IntegerProperty selectedRowIndex;
    private ResourceBundle resources;

    // endregion

    // region Constrollers

    public ShopGeneralController(Context context) {
        this.manager = context.getManager(Context.MANAGER_GENERAL);
        this.user = context.getUserManager().getUser().get();
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        tableGeneralItems.setItems(generalItems);
        tableGeneralItems.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> selectedRowIndex.setValue(newValue));

        columnImage.setCellValueFactory(new PropertyValueFactory<>("image"));
        columnImage.setCellFactory(param -> CellUtils.forImage());
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        columnWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        columnPrice.setCellFactory(param -> CellUtils.forMoney());
        columnAmmount.setCellValueFactory(new PropertyValueFactory<>("ammount"));
        columnAmmount.setCellFactory(param -> CellUtils.forMaxActValue());

        ObservableMergers.mergeList(GeneralEntry::new, generalItems, manager.selectAll());
    }

    @Override
    public void setShoppingCart(IShoppingCart shoppingCart, OnUploadItem uploadHandler,
        OnDownloadItem downloadHandler, OnDeleteItem deleteHandler) {
        columnAction.setCellFactory(param -> CellUtils
            .forActionButtons(shoppingCart::addItem, shoppingCart::removeItem, uploadHandler,
                downloadHandler, deleteHandler, user, resources));
    }

    @Override
    public void setRowSelectedIndexProperty(IntegerProperty rowSelectedIndexProperty) {
        this.selectedRowIndex = rowSelectedIndexProperty;
    }

    @Override
    public void setShowOnlineDatabase(BooleanProperty showOnlineDatabase) {
        showOnlineDatabase.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }

            manager.toggleDatabase(newValue);
        });
    }

    @Override
    public String getEditScreenName() {
        return R.FXML.ITEM_GENERAL;
    }

    @Override
    public void onAddItem(ItemBase item, boolean remote) {
        try {
            manager.insert((GeneralItem) item);
            if (remote) {
                generalItems.get(
                    generalItems.indexOf(
                        new GeneralEntry((GeneralItem) item)))
                    .setDownloaded(true);
            }

        } catch (DatabaseException e) {
            logger.warn("Item {} se nepodařilo vložit do databáze", item.toString());
        }
    }

    @Override
    public void onUpdateItem(ItemBase item) {
        try {
            manager.update((GeneralItem) item);
        } catch (DatabaseException e) {
            logger.warn("Item {} se napodařilo aktualizovat", item.toString());
        }
    }

    @Override
    public void insertItemToBundle(Bundle bundle, int index) {
        ItemGeneralController.toBundle(bundle, (GeneralItem) generalItems.get(index).getItemBase());
    }

    @Override
    public ItemBase fromBundle(Bundle bundle) {
        return ItemGeneralController.fromBundle(bundle);
    }

    @Override
    public void requestRemoveItem(int index) {
        final GeneralEntry entry = generalItems.get(index);
        final String name = entry.getName();
        try {
            manager.delete(entry.getId());
        } catch (DatabaseException e) {
            logger.warn("Item {} se nepodařilo odebrat z databáze", name);
        }
    }

    @Override
    public void requestRemoveItem(ShopEntry item, boolean remote) {
        manager.deleteRemote((GeneralItem) item.getItemBase(), remote);
    }

    @Override
    public void uploadRequest(ItemBase item) {
        manager.upload((GeneralItem) item);
    }

    @Override
    public void clearSelectedRow() {
        tableGeneralItems.getSelectionModel().clearSelection();
    }

    @Override
    public void onClose() {
        manager.toggleDatabase(false);
    }

}
