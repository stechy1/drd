package cz.stechy.drd.controller.shop;

import static cz.stechy.drd.controller.shop.ShopHelper.SHOP_ROW_HEIGHT;

import cz.stechy.drd.R;
import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.db.AdvancedDatabaseService;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.RangedWeapon;
import cz.stechy.drd.model.item.RangedWeapon.RangedWeaponType;
import cz.stechy.drd.model.persistent.RangedWeaponService;
import cz.stechy.drd.model.persistent.UserService;
import cz.stechy.drd.model.shop.IShoppingCart;
import cz.stechy.drd.model.shop.entry.GeneralEntry;
import cz.stechy.drd.model.shop.entry.RangedWeaponEntry;
import cz.stechy.drd.model.shop.entry.ShopEntry;
import cz.stechy.drd.model.user.User;
import cz.stechy.drd.util.CellUtils;
import cz.stechy.drd.util.ObservableMergers;
import cz.stechy.drd.util.StringConvertors;
import cz.stechy.drd.util.Translator;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pomocný kontroler pro obchod se zbraněmi na dálku
 */
public class ShopWeaponRangedController implements Initializable,
    ShopItemController<RangedWeaponEntry> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopWeaponRangedController.class);

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TableView<RangedWeaponEntry> tableRangedWeapons;
    @FXML
    private TableColumn<GeneralEntry, Image> columnImage;
    @FXML
    private TableColumn<RangedWeaponEntry, String> columnName;
    @FXML
    private TableColumn<RangedWeaponEntry, String> columnAuthor;
    @FXML
    private TableColumn<RangedWeaponEntry, String> columnStrength;
    @FXML
    private TableColumn<RangedWeaponEntry, Integer> columnRampancy;
    @FXML
    private TableColumn<RangedWeaponEntry, RangedWeaponType> columnType;
    @FXML
    private TableColumn<RangedWeaponEntry, Integer> columnRangeLow;
    @FXML
    private TableColumn<RangedWeaponEntry, Integer> columnRangeMedium;
    @FXML
    private TableColumn<RangedWeaponEntry, Integer> columnRangeLong;
    @FXML
    private TableColumn<RangedWeaponEntry, Integer> columnWeight;
    @FXML
    private TableColumn<RangedWeaponEntry, Money> columnPrice;
    @FXML
    private TableColumn<RangedWeaponEntry, MaxActValue> columnAmmount;
    @FXML
    private TableColumn<RangedWeaponEntry, ?> columnAction;

    // endregion

    private final ObservableList<RangedWeaponEntry> rangedWeapons = FXCollections
        .observableArrayList();
    private final SortedList<RangedWeaponEntry> sortedList = new SortedList<>(rangedWeapons,
        Comparator.comparing(ShopEntry::getName));
    private final BooleanProperty ammountEditable = new SimpleBooleanProperty(true);
    private final AdvancedDatabaseService<RangedWeapon> service;
    private final Translator translator;
    private final User user;

    private IntegerProperty selectedRowIndex;
    private ResourceBundle resources;

    // endregion

    // region Constructors

    @SuppressWarnings("unchecked")
    public ShopWeaponRangedController(UserService userService, RangedWeaponService rangedWeaponService, Translator translator) {
        this.service = rangedWeaponService;
        this.translator = translator;
        this.user = userService.getUser();
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        tableRangedWeapons.setItems(sortedList);
        tableRangedWeapons.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> selectedRowIndex.setValue(newValue));
        tableRangedWeapons.setFixedCellSize(SHOP_ROW_HEIGHT);
        sortedList.comparatorProperty().bind(tableRangedWeapons.comparatorProperty());

        columnImage.setCellFactory(param -> CellUtils.forImage());
        columnType.setCellFactory(
            TextFieldTableCell.forTableColumn(StringConvertors.forRangedWeaponType(translator)));
        columnWeight.setCellFactory(param -> CellUtils.forWeight());
        columnPrice.setCellFactory(param -> CellUtils.forMoney());
        columnAmmount.setCellFactory(param -> CellUtils.forMaxActValue(ammountEditable));
    }

    @Override
    public void setShoppingCart(IShoppingCart shoppingCart) {
        columnAction.setCellFactory(param -> ShopHelper
            .forActionButtons(shoppingCart::addItem, shoppingCart::removeItem,
                resources, ammountEditable));

        final Function<RangedWeapon, RangedWeaponEntry> mapper = weapon -> {
            final RangedWeaponEntry entry;
            final Optional<ShopEntry> cartEntry = shoppingCart.getEntry(weapon.getId());
            if (cartEntry.isPresent()) {
                entry = (RangedWeaponEntry) cartEntry.get();
            } else {
                entry = new RangedWeaponEntry(weapon);
            }

            return entry;
        };

        final Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ObservableMergers.mergeList(mapper, rangedWeapons, service.selectAll());
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
        return R.FXML.ITEM_RANGED_WEAPON;
    }

    @Override
    public void onAddItem(ItemBase item, boolean remote) {
        try {
            service.insert((RangedWeapon) item);
        } catch (DatabaseException e) {
            LOGGER.warn("Item {} se nepodařilo vložit do databáze", item.toString());
        }
    }

    @Override
    public void onUpdateItem(ItemBase item) {
        try {
            service.update((RangedWeapon) item);
        } catch (DatabaseException e) {
            LOGGER.warn("Item {} se napodařilo aktualizovat", item.toString());
        }
    }

    @Override
    public void insertItemToBundle(Bundle bundle, int index) {
        ItemWeaponRangedController.toBundle(bundle,
            (RangedWeapon) sortedList.get(index).getItemBase());
    }

    @Override
    public ItemBase fromBundle(Bundle bundle) {
        return ItemWeaponRangedController.fromBundle(bundle);
    }

    @Override
    public void requestRemoveItem(int index) {
        final RangedWeaponEntry entry = sortedList.get(index);
        final String name = entry.getName();
        try {
            service.delete(entry.getId());
        } catch (DatabaseException e) {
            LOGGER.warn("Item {} se nepodařilo odebrat z databáze", name);
        }
    }

    @Override
    public void requestRemoveItem(ShopEntry item, boolean remote) {
        service.deleteRemote((RangedWeapon) item.getItemBase(), remote);
    }

    @Override
    public void uploadRequest(ItemBase item) {
        service.upload((RangedWeapon) item);
    }

    @Override
    public void clearSelectedRow() {
        tableRangedWeapons.getSelectionModel().clearSelection();
    }

    @Override
    public void synchronizeItems() {
        service.synchronize(this.user.getName(), total ->
            LOGGER.info("Bylo synchronizováno celkem: " + total + " předmětů typu weapon ranged."));
    }

    @Override
    public Optional<RangedWeaponEntry> getSelectedItem() {
        if (selectedRowIndex.getValue() == null || selectedRowIndex.get() < 0) {
            return Optional.empty();
        }

        return Optional.of(sortedList.get(selectedRowIndex.get()));
    }
}
