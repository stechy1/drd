package cz.stechy.drd.controller.shop;

import static cz.stechy.drd.controller.shop.ShopHelper.SHOP_ROW_HEIGHT;

import cz.stechy.drd.Context;
import cz.stechy.drd.R;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.db.AdvancedDatabaseService;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.db.base.Firebase.OnDeleteItem;
import cz.stechy.drd.model.db.base.Firebase.OnDownloadItem;
import cz.stechy.drd.model.db.base.Firebase.OnUploadItem;
import cz.stechy.drd.model.item.Backpack;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.shop.IShoppingCart;
import cz.stechy.drd.model.shop.entry.BackpackEntry;
import cz.stechy.drd.model.shop.entry.ShopEntry;
import cz.stechy.drd.model.user.User;
import cz.stechy.drd.util.CellUtils;
import cz.stechy.drd.util.ObservableMergers;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pomocný kontroler pro obchod s batohy
 */
public class ShopBackpackController implements Initializable, ShopItemController<BackpackEntry> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopGeneralController.class);

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TableView<BackpackEntry> tableBackpacks;
    @FXML
    private TableColumn<BackpackEntry, Image> columnImage;
    @FXML
    private TableColumn<BackpackEntry, String> columnName;
    @FXML
    private TableColumn<BackpackEntry, String> columnAuthor;
    @FXML
    private TableColumn<BackpackEntry, Integer> columnWeight;
    @FXML
    private TableColumn<BackpackEntry, Integer> columnMaxLoad;
    @FXML
    private TableColumn<BackpackEntry, Money> columnPrice;
    @FXML
    private TableColumn<BackpackEntry, MaxActValue> columnAmmount;
    @FXML
    private TableColumn<BackpackEntry, ?> columnAction;

    // endregion

    private final ObservableList<BackpackEntry> backpacks = FXCollections.observableArrayList();
    private final SortedList<BackpackEntry> sortedList = new SortedList<>(backpacks,
        Comparator.comparing(ShopEntry::getName));
    private final BooleanProperty ammountEditable = new SimpleBooleanProperty(true);
    private final AdvancedDatabaseService<Backpack> service;
    private final User user;

    private IntegerProperty selectedRowIndex;
    private ResourceBundle resources;

    // endregion

    // region Constrollers

    public ShopBackpackController(Context context) {
        this.service = context.getService(Context.SERVICE_BACKPACK);
        this.user = context.getUserService().getUser().get();
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        tableBackpacks.setItems(sortedList);
        tableBackpacks.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> selectedRowIndex.setValue(newValue));
        tableBackpacks.setFixedCellSize(SHOP_ROW_HEIGHT);
        sortedList.comparatorProperty().bind(tableBackpacks.comparatorProperty());

        columnMaxLoad.setCellFactory(param -> CellUtils.forWeight());
        columnWeight.setCellFactory(param -> CellUtils.forWeight());
        columnImage.setCellFactory(param -> CellUtils.forImage());
        columnPrice.setCellFactory(param -> CellUtils.forMoney());
        columnAmmount.setCellFactory(param -> CellUtils.forMaxActValue(ammountEditable));
    }

    @Override
    public void setShoppingCart(IShoppingCart shoppingCart,
        OnUploadItem<BackpackEntry> uploadHandler,
        OnDownloadItem<BackpackEntry> downloadHandler,
        OnDeleteItem<BackpackEntry> deleteHandler) {
        columnAction.setCellFactory(param -> ShopHelper
            .forActionButtons(shoppingCart::addItem, shoppingCart::removeItem, uploadHandler,
                downloadHandler, deleteHandler, user, resources, ammountEditable));

        ObservableMergers.mergeList(backpack -> {
            final BackpackEntry entry;
            final Optional<ShopEntry> cartEntry = shoppingCart.getEntry(backpack.getId());
            if (cartEntry.isPresent()) {
                entry = (BackpackEntry) cartEntry.get();
            } else {
                entry = new BackpackEntry(backpack);
            }

            return entry;
        }, backpacks, service.selectAll());
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

            service.toggleDatabase(newValue);
        });
        ammountEditable.bind(showOnlineDatabase);
    }

    @Override
    public String getEditScreenName() {
        return R.FXML.ITEM_BACKPACK;
    }

    @Override
    public void onAddItem(ItemBase item, boolean remote) {
        try {
            service.insert((Backpack) item);
        } catch (DatabaseException e) {
            LOGGER.warn("Item {} se nepodařilo vložit do databáze", item.toString());
        }
    }

    @Override
    public void onUpdateItem(ItemBase item) {
        try {
            service.update((Backpack) item);
        } catch (DatabaseException e) {
            LOGGER.warn("Item {} se napodařilo aktualizovat", item.toString());
        }
    }

    @Override
    public void insertItemToBundle(Bundle bundle, int index) {
        ItemBackpackController.toBundle(bundle, (Backpack) sortedList.get(index).getItemBase());
    }

    @Override
    public ItemBase fromBundle(Bundle bundle) {
        return ItemBackpackController.fromBundle(bundle);
    }

    @Override
    public void requestRemoveItem(int index) {
        final BackpackEntry entry = sortedList.get(index);
        final String name = entry.getName();
        try {
            service.delete(entry.getId());
        } catch (DatabaseException e) {
            LOGGER.warn("Item {} se nepodařilo odebrat z databáze", name);
        }
    }

    @Override
    public void requestRemoveItem(ShopEntry item, boolean remote) {
        service.deleteRemote((Backpack) item.getItemBase(), remote);
    }

    @Override
    public void uploadRequest(ItemBase item) {
        service.upload((Backpack) item);
    }

    @Override
    public void clearSelectedRow() {
        tableBackpacks.getSelectionModel().clearSelection();
    }

    @Override
    public void synchronizeItems() {
        service.synchronize(this.user.getName(), total ->
            LOGGER.info("Bylo synchronizováno celkem: " + total + " předmětů typu backpack."));
    }
}
