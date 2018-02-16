package cz.stechy.drd.app.shop;

import static cz.stechy.drd.app.shop.ShopHelper.SHOP_ROW_HEIGHT;

import cz.stechy.drd.R;
import cz.stechy.drd.R.Translate;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.db.AdvancedDatabaseService;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.MeleWeapon;
import cz.stechy.drd.model.item.MeleWeapon.MeleWeaponClass;
import cz.stechy.drd.model.item.MeleWeapon.MeleWeaponType;
import cz.stechy.drd.dao.MeleWeaponDao;
import cz.stechy.drd.dao.UserDao;
import cz.stechy.drd.app.shop.entry.GeneralEntry;
import cz.stechy.drd.app.shop.entry.MeleWeaponEntry;
import cz.stechy.drd.app.shop.entry.ShopEntry;
import cz.stechy.drd.app.user.User;
import cz.stechy.drd.util.CellUtils;
import cz.stechy.drd.util.ObservableMergers;
import cz.stechy.drd.util.Translator;
import cz.stechy.drd.util.Translator.Key;
import cz.stechy.screens.Bundle;
import cz.stechy.screens.Notification;
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
    private ShopNotificationProvider notifier;
    private ShopFirebaseListener firebaseListener;

    // endregion

    // region Constructors

    public ShopWeaponMeleController(UserDao userDao, MeleWeaponDao meleWeaponDao, Translator translator) {
        this.service = meleWeaponDao;
        this.translator = translator;
        this.user = userDao.getUser();
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
            TextFieldTableCell.forTableColumn(translator.getConvertor(Key.WEAPON_MELE_CLASSES)));
        columnType.setCellFactory(
            TextFieldTableCell.forTableColumn(translator.getConvertor(Key.WEAPON_MELE_TYPES)));
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
            entry = cartEntry.map(shopEntry -> (MeleWeaponEntry) shopEntry)
                .orElseGet(() -> new MeleWeaponEntry(meleWeapon));

            return entry;
        };

        service.selectAllAsync()
            .thenAccept(meleWeaponList -> ObservableMergers.mergeList(mapper, meleWeapons, meleWeaponList));
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
    public void setNotificationProvider(ShopNotificationProvider notificationProvider) {
        this.notifier = notificationProvider;
    }

    @Override
    public void setFirebaseListener(ShopFirebaseListener firebaseListener) {
        this.firebaseListener = firebaseListener;
    }

    @Override
    public String getEditScreenName() {
        return R.FXML.ITEM_MELE_WEAPON;
    }

    @Override
    public void onAddItem(ItemBase item, boolean remote) {
        service.insertAsync((MeleWeapon) item)
            .exceptionally(throwable -> {
                notifier.showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_RECORD_IS_NOT_INSERTED), item.getName())));
                LOGGER.error("Item {} se nepodařilo vložit do databáze", item.getName());
                throw new RuntimeException(throwable);
            })
            .thenAccept(meleWeapon -> notifier
                .showNotification(new Notification(String.format(translator.translate(
                    Translate.NOTIFY_RECORD_IS_INSERTED), item.getName()))));
    }

    @Override
    public void onUpdateItem(ItemBase item) {
        service.updateAsync((MeleWeapon) item)
            .exceptionally(throwable -> {
                notifier.showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_RECORD_IS_NOT_UPDATED), item.getName())));
                LOGGER.error("Položku {} se nepodařilo aktualizovat", item.getName());
                throw new RuntimeException(throwable);
            })
            .thenAccept(meleWeapon -> notifier
                .showNotification(new Notification(String.format(translator.translate(
                    Translate.NOTIFY_RECORD_IS_NOT_UPDATED), item.getName()))));
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
        service.deleteAsync((MeleWeapon) entry.getItemBase())
            .exceptionally(throwable -> {
                notifier.showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_RECORD_IS_NOT_DELETED), entry.getName())));
                LOGGER.error("Položku {} se nepodařilo aktualizovat", entry.getName());
                throw new RuntimeException(throwable);
            })
            .thenAccept(meleWeapon -> notifier
                .showNotification(new Notification(String.format(translator.translate(
                    Translate.NOTIFY_RECORD_IS_DELETED), entry.getName()))));
    }

    @Override
    public void requestRemoveItem(ShopEntry entry, boolean remote) {
        service.deleteRemoteAsync((MeleWeapon) entry.getItemBase(), remote, (error, ref) ->
            firebaseListener.handleItemRemove(entry.getName(), remote, error == null));
    }

    @Override
    public void uploadRequest(ItemBase item) {
        service.uploadAsync((MeleWeapon) item, (error, ref) ->
            firebaseListener.handleItemUpload(item.getName(), error == null));
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
