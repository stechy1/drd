package cz.stechy.drd.controller.shop;

import static cz.stechy.drd.controller.shop.ShopHelper.SHOP_ROW_HEIGHT;

import cz.stechy.drd.R;
import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.db.AdvancedDatabaseService;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.MeleWeapon;
import cz.stechy.drd.model.item.MeleWeapon.MeleWeaponClass;
import cz.stechy.drd.model.item.MeleWeapon.MeleWeaponType;
import cz.stechy.drd.model.persistent.MeleWeaponService;
import cz.stechy.drd.model.persistent.UserService;
import cz.stechy.drd.model.shop.IShoppingCart;
import cz.stechy.drd.model.shop.entry.GeneralEntry;
import cz.stechy.drd.model.shop.entry.MeleWeaponEntry;
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
 * Pomocný kontroler pro obchod se zbraněmi na blízko
 */
public class ShopWeaponMeleController implements Initializable,
    ShopItemController<MeleWeaponEntry> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopWeaponMeleController.class);

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TableView<MeleWeaponEntry> tableMeleWeapon;
    @FXML
    private TableColumn<GeneralEntry, Image> columnImage;
    @FXML
    private TableColumn<MeleWeaponEntry, String> columnName;
    @FXML
    private TableColumn<MeleWeaponEntry, String> columnAuthor;
    @FXML
    private TableColumn<MeleWeaponEntry, Integer> columnStrength;
    @FXML
    private TableColumn<MeleWeaponEntry, Integer> columnRampancy;
    @FXML
    private TableColumn<MeleWeaponEntry, Integer> columnDefence;
    @FXML
    private TableColumn<MeleWeaponEntry, MeleWeaponClass> columnClass;
    @FXML
    private TableColumn<MeleWeaponEntry, MeleWeaponType> columnType;
    @FXML
    private TableColumn<MeleWeaponEntry, Integer> columnWeight;
    @FXML
    private TableColumn<MeleWeaponEntry, Money> columnPrice;
    @FXML
    private TableColumn<MeleWeaponEntry, MaxActValue> columnAmmount;
    @FXML
    private TableColumn<MeleWeaponEntry, ?> columnAction;

    // endregion

    private final ObservableList<MeleWeaponEntry> meleWeapons = FXCollections.observableArrayList();
    private final SortedList<MeleWeaponEntry> sortedList = new SortedList<>(meleWeapons,
        Comparator.comparing(ShopEntry::getName));
    private final BooleanProperty ammountEditable = new SimpleBooleanProperty(true);
    private final AdvancedDatabaseService<MeleWeapon> service;
    private final Translator translator;
    private final User user;

    private IntegerProperty selectedRowIndex;
    private ResourceBundle resources;

    // endregion

    // region Constructors

    public ShopWeaponMeleController(UserService userService, MeleWeaponService meleWeaponService, Translator translator) {
        this.service = meleWeaponService;
        this.translator = translator;
        this.user = userService.getUser();
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        tableMeleWeapon.setItems(sortedList);
        tableMeleWeapon.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> selectedRowIndex.setValue(newValue));
        tableMeleWeapon.setFixedCellSize(SHOP_ROW_HEIGHT);
        sortedList.comparatorProperty().bind(tableMeleWeapon.comparatorProperty());

        columnImage.setCellFactory(param -> CellUtils.forImage());
        columnClass.setCellFactory(
            TextFieldTableCell.forTableColumn(StringConvertors.forMeleWeaponClass(translator)));
        columnType.setCellFactory(
            TextFieldTableCell.forTableColumn(StringConvertors.forMeleWeaponType(translator)));
        columnWeight.setCellFactory(param -> CellUtils.forWeight());
        columnPrice.setCellFactory(param -> CellUtils.forMoney());
        columnAmmount.setCellFactory(param -> CellUtils.forMaxActValue(ammountEditable));
    }

    @Override
    public void setShoppingCart(IShoppingCart shoppingCart) {
        columnAction.setCellFactory(param -> ShopHelper
            .forActionButtons(shoppingCart::addItem, shoppingCart::removeItem,
                resources, ammountEditable));

        Function<MeleWeapon, MeleWeaponEntry> mapper = meleWeapon -> {
            final MeleWeaponEntry entry;
            final Optional<ShopEntry> cartEntry = shoppingCart.getEntry(meleWeapon.getId());
            if (cartEntry.isPresent()) {
                entry = (MeleWeaponEntry) cartEntry.get();
            } else {
                entry = new MeleWeaponEntry(meleWeapon);
            }

            return entry;
        };

        final Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ObservableMergers.mergeList(mapper, meleWeapons, service.selectAll());
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
    }

    @Override
    public void setAmmountEditableProperty(BooleanProperty ammountEditable) {
        this.ammountEditable.bind(ammountEditable);
    }

    @Override
    public String getEditScreenName() {
        return R.FXML.ITEM_MELE_WEAPON;
    }

    @Override
    public void onAddItem(ItemBase item, boolean remote) {
        try {
            service.insert((MeleWeapon) item);
        } catch (DatabaseException e) {
            LOGGER.warn("Item {} se nepodařilo vložit do databáze", item.toString());
        }
    }

    @Override
    public void onUpdateItem(ItemBase item) {
        try {
            service.update((MeleWeapon) item);
        } catch (DatabaseException e) {
            LOGGER.warn("Item {} se napodařilo aktualizovat", item.toString());
        }
    }

    @Override
    public void insertItemToBundle(Bundle bundle, int index) {
        ItemWeaponMeleController
            .toBundle(bundle, (MeleWeapon) sortedList.get(index).getItemBase());
    }

    @Override
    public ItemBase fromBundle(Bundle bundle) {
        return ItemWeaponMeleController.fromBundle(bundle);
    }

    @Override
    public void requestRemoveItem(int index) {
        final MeleWeaponEntry entry = sortedList.get(index);
        final String name = entry.getName();
        try {
            service.delete(entry.getId());
        } catch (DatabaseException e) {
            LOGGER.warn("Item {} se nepodařilo odebrat z databáze", name);
        }
    }

    @Override
    public void requestRemoveItem(ShopEntry item, boolean remote) {
        service.deleteRemote((MeleWeapon) item.getItemBase(), remote);
    }

    @Override
    public void uploadRequest(ItemBase item) {
        service.upload((MeleWeapon) item);
    }

    @Override
    public void clearSelectedRow() {
        tableMeleWeapon.getSelectionModel().clearSelection();
    }

    @Override
    public void synchronizeItems() {
        service.synchronize(this.user.getName(), total ->
            LOGGER.info("Bylo synchronizováno celkem: " + total + " předmětů typu weapon mele."));
    }

    @Override
    public Optional<MeleWeaponEntry> getSelectedItem() {
        if (selectedRowIndex.getValue() == null || selectedRowIndex.get() < 0) {
            return Optional.empty();
        }

        return Optional.of(sortedList.get(selectedRowIndex.get()));
    }
}
