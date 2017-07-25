package cz.stechy.drd.controller.shop;

import cz.stechy.drd.Context;
import cz.stechy.drd.Money;
import cz.stechy.drd.R;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.db.AdvancedDatabaseService;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.db.base.Firebase.OnDeleteItem;
import cz.stechy.drd.model.db.base.Firebase.OnDownloadItem;
import cz.stechy.drd.model.db.base.Firebase.OnUploadItem;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.MeleWeapon;
import cz.stechy.drd.model.item.MeleWeapon.MeleWeaponClass;
import cz.stechy.drd.model.item.MeleWeapon.MeleWeaponType;
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
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
 * Pomocný kontroler pro obchod se zbraněmi na blízko
 */
public class ShopWeaponMeleController implements Initializable,
    ShopItemController<MeleWeaponEntry> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(ShopWeaponMeleController.class);

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
    private final BooleanProperty ammountEditable = new SimpleBooleanProperty(true);
    private final AdvancedDatabaseService<MeleWeapon> service;
    private final Translator translator;
    private final User user;

    private IntegerProperty selectedRowIndex;
    private ResourceBundle resources;

    // endregion

    // region Constructors

    public ShopWeaponMeleController(Context context) {
        this.service = context
            .getService(Context.SERVICE_WEAPON_MELE);
        this.translator = context.getTranslator();
        this.user = context.getUserService().getUser().get();
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        tableMeleWeapon.setItems(meleWeapons);
        tableMeleWeapon.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> selectedRowIndex.setValue(newValue));

        columnImage.setCellValueFactory(new PropertyValueFactory<>("image"));
        columnImage.setCellFactory(param -> CellUtils.forImage());
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        columnStrength.setCellValueFactory(new PropertyValueFactory<>("strength"));
        columnRampancy.setCellValueFactory(new PropertyValueFactory<>("rampancy"));
        columnDefence.setCellValueFactory(new PropertyValueFactory<>("defence"));
        columnClass.setCellValueFactory(new PropertyValueFactory<>("weaponClass"));
        columnClass.setCellFactory(
            TextFieldTableCell.forTableColumn(StringConvertors.forMeleWeaponClass(translator)));
        columnType.setCellValueFactory(new PropertyValueFactory<>("weaponType"));
        columnType.setCellFactory(
            TextFieldTableCell.forTableColumn(StringConvertors.forMeleWeaponType(translator)));
        columnWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        columnPrice.setCellFactory(param -> CellUtils.forMoney());
        columnAmmount.setCellValueFactory(new PropertyValueFactory<>("ammount"));
        columnAmmount.setCellFactory(param -> CellUtils.forMaxActValue(ammountEditable));
    }

    @Override
    public void setShoppingCart(IShoppingCart shoppingCart,
        OnUploadItem<MeleWeaponEntry> uploadHandler,
        OnDownloadItem<MeleWeaponEntry> downloadHandler,
        OnDeleteItem<MeleWeaponEntry> deleteHandler) {
        columnAction.setCellFactory(param -> ShopHelper
            .forActionButtons(shoppingCart::addItem, shoppingCart::removeItem, uploadHandler,
                downloadHandler, deleteHandler, user, resources));

        ObservableMergers.mergeList(meleWeapon -> {
            final MeleWeaponEntry entry;
            final Optional<ShopEntry> cartEntry = shoppingCart.getEntry(meleWeapon.getId());
            if (cartEntry.isPresent()) {
                entry = (MeleWeaponEntry) cartEntry.get();
            } else {
                entry = new MeleWeaponEntry(meleWeapon);
            }

            return entry;
        }, meleWeapons, service.selectAll());
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
        return R.FXML.ITEM_MELE_WEAPON;
    }

    @Override
    public void onAddItem(ItemBase item, boolean remote) {
        try {
            if (remote) {
                item.setDownloaded(true);
            }

            service.insert((MeleWeapon) item);
        } catch (DatabaseException e) {
            logger.warn("Item {} se nepodařilo vložit do databáze", item.toString());
        }
    }

    @Override
    public void onUpdateItem(ItemBase item) {
        try {
            service.update((MeleWeapon) item);
        } catch (DatabaseException e) {
            logger.warn("Item {} se napodařilo aktualizovat", item.toString());
        }
    }

    @Override
    public void insertItemToBundle(Bundle bundle, int index) {
        ItemWeaponMeleController
            .toBundle(bundle, (MeleWeapon) meleWeapons.get(index).getItemBase());
    }

    @Override
    public ItemBase fromBundle(Bundle bundle) {
        return ItemWeaponMeleController.fromBundle(bundle);
    }

    @Override
    public void requestRemoveItem(int index) {
        final MeleWeaponEntry entry = meleWeapons.get(index);
        final String name = entry.getName();
        try {
            service.delete(entry.getId());
        } catch (DatabaseException e) {
            logger.warn("Item {} se nepodařilo odebrat z databáze", name);
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
    public void onClose() {
        service.toggleDatabase(false);
    }

    @Override
    public void synchronizeItems() {
        service.synchronize(this.user.getName());
    }
}
