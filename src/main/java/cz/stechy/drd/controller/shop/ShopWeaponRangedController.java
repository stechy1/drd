package cz.stechy.drd.controller.shop;

import cz.stechy.drd.Money;
import cz.stechy.drd.R;
import cz.stechy.drd.model.Context;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.db.AdvancedDatabaseService;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.RangedWeapon;
import cz.stechy.drd.model.item.RangedWeapon.RangedWeaponType;
import cz.stechy.drd.model.shop.IShoppingCart;
import cz.stechy.drd.model.shop.OnDeleteItem;
import cz.stechy.drd.model.shop.OnDownloadItem;
import cz.stechy.drd.model.shop.OnUploadItem;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pomocný kontroler pro obchod se zbraněmi na dálku
 */
public class ShopWeaponRangedController implements Initializable, ShopItemController {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(ShopWeaponRangedController.class);

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
    private final AdvancedDatabaseService<RangedWeapon> manager;
    private final Translator translator;
    private final User user;

    private IntegerProperty selectedRowIndex;
    private ResourceBundle resources;

    // endregion

    // region Constructors

    @SuppressWarnings("unchecked")
    public ShopWeaponRangedController(Context context) {
        this.manager = context
            .getService(Context.SERVICE_WEAPON_RANGED);
        this.translator = context.getTranslator();
        this.user = context.getUserService().getUser().get();
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        tableRangedWeapons.setItems(rangedWeapons);
        tableRangedWeapons.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> selectedRowIndex.setValue(newValue));

        columnImage.setCellValueFactory(new PropertyValueFactory<>("image"));
        columnImage.setCellFactory(param -> CellUtils.forImage());
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        columnStrength.setCellValueFactory(new PropertyValueFactory<>("strength"));
        columnRampancy.setCellValueFactory(new PropertyValueFactory<>("rampancy"));
        columnType.setCellValueFactory(new PropertyValueFactory<>("weaponType"));
        columnRangeLow.setCellValueFactory(new PropertyValueFactory<>("rangeLow"));
        columnRangeMedium.setCellValueFactory(new PropertyValueFactory<>("rangeMedium"));
        columnRangeLong.setCellValueFactory(new PropertyValueFactory<>("rangeLong"));
        columnType.setCellFactory(
            TextFieldTableCell.forTableColumn(StringConvertors.forRangedWeaponType(translator)));
        columnWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        columnPrice.setCellFactory(param -> CellUtils.forMoney());
        columnAmmount.setCellValueFactory(new PropertyValueFactory<>("ammount"));
        columnAmmount.setCellFactory(param -> CellUtils.forMaxActValue());

        ObservableMergers.mergeList(RangedWeaponEntry::new, rangedWeapons, manager.selectAll());
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
        return R.FXML.ITEM_RANGED_WEAPON;
    }

    @Override
    public void onAddItem(ItemBase item, boolean remote) {
        try {
            manager.insert((RangedWeapon) item);
            rangedWeapons.get(
                rangedWeapons.indexOf(
                    new RangedWeaponEntry((RangedWeapon) item)))
                .setDownloaded(true);
        } catch (DatabaseException e) {
            logger.warn("Item {} se nepodařilo vložit do databáze", item.toString());
        }
    }

    @Override
    public void onUpdateItem(ItemBase item) {
        try {
            manager.update((RangedWeapon) item);
        } catch (DatabaseException e) {
            logger.warn("Item {} se napodařilo aktualizovat", item.toString());
        }
    }

    @Override
    public void insertItemToBundle(Bundle bundle, int index) {
        ItemWeaponRangedController.toBundle(bundle,
            (RangedWeapon) rangedWeapons.get(index).getItemBase());
    }

    @Override
    public ItemBase fromBundle(Bundle bundle) {
        return ItemWeaponRangedController.fromBundle(bundle);
    }

    @Override
    public void requestRemoveItem(int index) {
        final RangedWeaponEntry entry = rangedWeapons.get(index);
        final String name = entry.getName();
        try {
            manager.delete(entry.getId());
        } catch (DatabaseException e) {
            logger.warn("Item {} se nepodařilo odebrat z databáze", name);
        }
    }

    @Override
    public void requestRemoveItem(ShopEntry item, boolean remote) {
        manager.deleteRemote((RangedWeapon) item.getItemBase(), remote);
    }

    @Override
    public void uploadRequest(ItemBase item) {
        manager.upload((RangedWeapon) item);
    }

    @Override
    public void clearSelectedRow() {
        tableRangedWeapons.getSelectionModel().clearSelection();
    }

    @Override
    public void onClose() {
        manager.toggleDatabase(false);
    }

    @Override
    public void synchronizeItems() {
        manager.synchronize(this.user.getName());
    }
}
