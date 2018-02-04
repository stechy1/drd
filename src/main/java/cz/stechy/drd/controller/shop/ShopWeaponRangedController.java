package cz.stechy.drd.controller.shop;

import static cz.stechy.drd.controller.shop.ShopHelper.SHOP_ROW_HEIGHT;

import cz.stechy.drd.R;
import cz.stechy.drd.R.Translate;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.db.AdvancedDatabaseService;
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
    private ShopNotificationProvider notifier;

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
            TextFieldTableCell.forTableColumn(translator.getConvertor(Key.WEAPON_RANGED_TYPES)));
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

        service.selectAllAsync()
            .thenAccept(rangedWeaponList -> ObservableMergers.mergeList(mapper, rangedWeapons, rangedWeaponList));
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
    public String getEditScreenName() {
        return R.FXML.ITEM_RANGED_WEAPON;
    }

    @Override
    public void onAddItem(ItemBase item, boolean remote) {
        service.insertAsync((RangedWeapon) item)
            .exceptionally(throwable -> {
                notifier.showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_RECORD_IS_NOT_INSERTED), item.getName())));
                LOGGER.error("Item {} se nepodařilo vložit do databáze", item.getName());
                throw new RuntimeException(throwable);
            })
            .thenAccept(rangedWeapon -> notifier
                .showNotification(new Notification(String.format(translator.translate(
                    Translate.NOTIFY_RECORD_IS_INSERTED), item.getName()))));
    }

    @Override
    public void onUpdateItem(ItemBase item) {
        service.updateAsync((RangedWeapon) item)
            .exceptionally(throwable -> {
                notifier.showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_RECORD_IS_NOT_UPDATED), item.getName())));
                LOGGER.error("Položku {} se napodařilo aktualizovat", item.getName());
                throw new RuntimeException(throwable);
            })
            .thenAccept(rangedWeapon -> notifier
                .showNotification(new Notification(String.format(translator.translate(
                    Translate.NOTIFY_RECORD_IS_NOT_UPDATED), item.getName()))));
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
        service.deleteAsync((RangedWeapon) entry.getItemBase())
            .exceptionally(throwable -> {
                notifier.showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_RECORD_IS_NOT_DELETED), entry.getName())));
                LOGGER.error("Položku {} se napodařilo aktualizovat", entry.getName());
                throw new RuntimeException(throwable);
            })
            .thenAccept(rangedWeapon -> notifier
                .showNotification(new Notification(String.format(translator.translate(
                    Translate.NOTIFY_RECORD_IS_DELETED), entry.getName()))));
    }

    @Override
    public void requestRemoveItem(ShopEntry entry, boolean remote) {
        service.deleteRemoteAsync((RangedWeapon) entry.getItemBase(), remote)
            .exceptionally(throwable -> {
                final String key = remote
                    ? R.Translate.NOTIFY_RECORD_IS_NOT_DELETED_FROM_ONLINE_DATABASE
                    : R.Translate.NOTIFY_RECORD_IS_NOT_DELETED;
                notifier.showNotification(new Notification(String.format(translator.translate(
                    key), entry.getName())));
                LOGGER.error("Položku {} se napodařilo aktualizovat", entry.getName());
                throw new RuntimeException(throwable);
            })
            .thenAccept(rangedWeapon -> {
                final String key = remote
                    ? R.Translate.NOTIFY_RECORD_IS_DELETED_FROM_ONLINE_DATABASE
                    : R.Translate.NOTIFY_RECORD_IS_DELETED;
                notifier.showNotification(new Notification(String.format(translator.translate(
                    key), entry.getName())));
            });
    }

    @Override
    public void uploadRequest(ItemBase item) {
        service.uploadAsync((RangedWeapon) item)
            .exceptionally(throwable -> {
                notifier.showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_RECORD_IS_NOT_UPLOADED), item.getName())));
                LOGGER.error("Položku {} se napodařilo aktualizovat", item.getName());
                throw new RuntimeException(throwable);
            })
            .thenAccept(rangedWeapon -> notifier
                .showNotification(new Notification(String.format(translator.translate(
                    Translate.NOTIFY_RECORD_IS_DELETED), item.getName()))));
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
