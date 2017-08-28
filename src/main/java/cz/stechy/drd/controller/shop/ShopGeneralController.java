package cz.stechy.drd.controller.shop;

import static cz.stechy.drd.controller.shop.ShopHelper.SHOP_ROW_HEIGHT;

import cz.stechy.drd.R;
import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.db.AdvancedDatabaseService;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.item.GeneralItem;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.persistent.GeneralItemService;
import cz.stechy.drd.model.persistent.UserService;
import cz.stechy.drd.model.shop.IShoppingCart;
import cz.stechy.drd.model.shop.entry.GeneralEntry;
import cz.stechy.drd.model.shop.entry.ShopEntry;
import cz.stechy.drd.model.user.User;
import cz.stechy.drd.util.CellUtils;
import cz.stechy.drd.util.ObservableMergers;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pomocný kontroler pro obchod se obecnými předměty
 */
public class ShopGeneralController implements Initializable, ShopItemController<GeneralEntry> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopGeneralController.class);

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
    private final SortedList<GeneralEntry> sortedList = new SortedList<>(generalItems,
        Comparator.comparing(ShopEntry::getName));
    private final BooleanProperty ammountEditable = new SimpleBooleanProperty(true);
    private final AdvancedDatabaseService<GeneralItem> service;
    private final User user;

    private IntegerProperty selectedRowIndex;
    private ResourceBundle resources;

    // endregion

    // region Constrollers

    public ShopGeneralController(UserService userService, GeneralItemService generalItemService) {
        this.service = generalItemService;
        this.user = userService.getUser().get();
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        tableGeneralItems.setItems(sortedList);
        tableGeneralItems.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> selectedRowIndex.setValue(newValue));
        tableGeneralItems.setFixedCellSize(SHOP_ROW_HEIGHT);
        sortedList.comparatorProperty().bind(tableGeneralItems.comparatorProperty());

        columnWeight.setCellFactory(param -> CellUtils.forWeight());
        columnImage.setCellFactory(param -> CellUtils.forImage());
        columnPrice.setCellFactory(param -> CellUtils.forMoney());
        columnAmmount.setCellFactory(param -> CellUtils.forMaxActValue(ammountEditable));
    }

    @Override
    public void setShoppingCart(IShoppingCart shoppingCart) {
        columnAction.setCellFactory(param -> ShopHelper
            .forActionButtons(shoppingCart::addItem, shoppingCart::removeItem,
                resources, ammountEditable));

        final Function<GeneralItem, GeneralEntry> mapper = generalItem -> {
            final GeneralEntry entry;
            final Optional<ShopEntry> cartEntry = shoppingCart.getEntry(generalItem.getId());
            if (cartEntry.isPresent()) {
                entry = (GeneralEntry) cartEntry.get();
            } else {
                entry = new GeneralEntry(generalItem);
            }

            return entry;
        };
        final Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ObservableMergers.mergeList(mapper, generalItems, service.selectAll());
                return null;
            }
        };
        ThreadPool.getInstance().submit(task);
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
        return R.FXML.ITEM_GENERAL;
    }

    @Override
    public void onAddItem(ItemBase item, boolean remote) {
        try {
            service.insert((GeneralItem) item);
        } catch (DatabaseException e) {
            LOGGER.warn("Item {} se nepodařilo vložit do databáze", item.toString());
        }
    }

    @Override
    public void onUpdateItem(ItemBase item) {
        try {
            service.update((GeneralItem) item);
        } catch (DatabaseException e) {
            LOGGER.warn("Item {} se napodařilo aktualizovat", item.toString());
        }
    }

    @Override
    public void insertItemToBundle(Bundle bundle, int index) {
        ItemGeneralController.toBundle(bundle, (GeneralItem) sortedList.get(index).getItemBase());
    }

    @Override
    public ItemBase fromBundle(Bundle bundle) {
        return ItemGeneralController.fromBundle(bundle);
    }

    @Override
    public void requestRemoveItem(int index) {
        final GeneralEntry entry = sortedList.get(index);
        final String name = entry.getName();
        try {
            service.delete(entry.getId());
        } catch (DatabaseException e) {
            LOGGER.warn("Item {} se nepodařilo odebrat z databáze", name);
        }
    }

    @Override
    public void requestRemoveItem(ShopEntry item, boolean remote) {
        service.deleteRemote((GeneralItem) item.getItemBase(), remote);
    }

    @Override
    public void uploadRequest(ItemBase item) {
        service.upload((GeneralItem) item);
    }

    @Override
    public void clearSelectedRow() {
        tableGeneralItems.getSelectionModel().clearSelection();
    }

    @Override
    public void synchronizeItems() {
        service.synchronize(this.user.getName(), total ->
            LOGGER.info("Bylo synchronizováno celkem: " + total + " předmětů typu general item."));
    }

    @Override
    public Optional<GeneralEntry> getSelectedItem() {
        if (selectedRowIndex.getValue() == null || selectedRowIndex.get() < 0) {
            return Optional.empty();
        }

        return Optional.of(sortedList.get(selectedRowIndex.get()));
    }
}
