package cz.stechy.drd.app.shop;

import static cz.stechy.drd.app.shop.ShopHelper.SHOP_ROW_HEIGHT;

import cz.stechy.drd.R;
import cz.stechy.drd.R.Translate;
import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.app.shop.entry.ArmorEntry;
import cz.stechy.drd.app.shop.entry.GeneralEntry;
import cz.stechy.drd.app.shop.entry.ShopEntry;
import cz.stechy.drd.dao.ArmorDao;
import cz.stechy.drd.db.AdvancedDatabaseService;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.User;
import cz.stechy.drd.model.entity.Height;
import cz.stechy.drd.model.item.Armor;
import cz.stechy.drd.model.item.Armor.ArmorType;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.service.UserService;
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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
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
 * Pomocný kontroler pro obchod se zbrojí
 */
public class ShopArmorController implements Initializable, ShopItemController<ArmorEntry> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopArmorController.class);

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TableView<ArmorEntry> tableArmor;
    @FXML
    private TableColumn<GeneralEntry, Image> columnImage;
    @FXML
    private TableColumn<ArmorEntry, String> columnName;
    @FXML
    private TableColumn<ArmorEntry, String> columnAuthor;
    @FXML
    private TableColumn<ArmorEntry, ArmorType> columnArmorType;
    @FXML
    private TableColumn<ArmorEntry, Integer> columnDefenceNumber;
    @FXML
    private TableColumn<ArmorEntry, Integer> columnMinimumStrength;
    @FXML
    private TableColumn<ArmorEntry, Integer> columnWeight;
    @FXML
    private TableColumn<ArmorEntry, Money> columnPrice;
    @FXML
    private TableColumn<ArmorEntry, MaxActValue> columnAmmount;
    @FXML
    private TableColumn<ArmorEntry, ?> columnAction;

    // endregion

    private final ObservableList<ArmorEntry> armors = FXCollections.observableArrayList();
    private final SortedList<ArmorEntry> sortedList = new SortedList<>(armors,
        Comparator.comparing(ShopEntry::getName));
    private final BooleanProperty ammountEditable = new SimpleBooleanProperty(true);
    private final BooleanProperty highlightDiffItem = new SimpleBooleanProperty(false);
    private final ObjectProperty<Height> height = new SimpleObjectProperty<>(Height.B);
    private final AdvancedDatabaseService<Armor> service;
    private final Translator translator;
    private final User user;

    private IntegerProperty selectedRowIndex;
    private ResourceBundle resources;
    private ShopNotificationProvider notifier;
    private ShopOnlineListener shopOnlineListener;

    // endregion

    // region Constructors

    public ShopArmorController(UserService userService, ArmorDao armorDao,
        Translator translator) {
        this.service = armorDao;
        this.translator = translator;
        this.user = userService.getUser();
    }

    // endregion

    // region Public methods

    /**
     * Nastaví velikost, od které se bude odrážet cena i váha zbroje
     *
     * @param height Výška postavy
     */
    void setHeroHeight(Height height) {
        this.height.setValue(height);
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        tableArmor.setItems(sortedList);
        tableArmor.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> selectedRowIndex.setValue(newValue));
        tableArmor.setFixedCellSize(SHOP_ROW_HEIGHT);
        tableArmor.setRowFactory(param -> new ShopRow<>(highlightDiffItem));
        sortedList.comparatorProperty().bind(tableArmor.comparatorProperty());

        columnWeight.setCellFactory(param -> CellUtils.forWeight());
        columnImage.setCellFactory(param -> CellUtils.forImage());
        columnArmorType.setCellFactory(
            TextFieldTableCell.forTableColumn(translator.getConvertor(Key.ARMOR_TYPES)));
        columnPrice.setCellFactory(param -> CellUtils.forMoney());
        columnAmmount.setCellFactory(param -> CellUtils.forMaxActValue(ammountEditable));
    }

    @Override
    public void setShoppingCart(IShoppingCart shoppingCart) {
        columnAction.setCellFactory(param -> ShopHelper
            .forActionButtons(shoppingCart::addItem, shoppingCart::removeItem,
                resources, ammountEditable));

        final Function<Armor, ArmorEntry> mapper = armor -> {
            final ArmorEntry entry;
            final Optional<ShopEntry> cartEntry = shoppingCart.getEntry(armor.getId());
            entry = cartEntry.map(shopEntry -> (ArmorEntry) shopEntry)
                .orElseGet(() -> new ArmorEntry(armor, height));

            return entry;
        };

        service.selectAllAsync()
            .thenAccept(armorList -> ObservableMergers.mergeList(mapper, armors, armorList));
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
    public void setHighlightDiffItems(BooleanProperty highlightDiffItems) {
        this.highlightDiffItem.bind(highlightDiffItems);
        highlightDiffItems.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue) {
                service.getDiff().thenAcceptAsync(diffEntries -> {
                    diffEntries.forEach(diffEntry -> {
                        final String id = diffEntry.getId();
                        armors
                            .parallelStream()
                            .filter(entry -> id.equals(entry.getId()))
                            .findFirst()
                            .ifPresent(generalEntry -> {
                                generalEntry.setDiffMap(diffEntry.getDiffMap());
                            });
                    });
                }, ThreadPool.JAVAFX_EXECUTOR);
            } else {
                armors.parallelStream().forEach(entry -> entry.clearDiffMap());
            }
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
    public void setOnlineListener(ShopOnlineListener onlineListener) {
        this.shopOnlineListener = onlineListener;
    }

    @Override
    public String getEditScreenName() {
        return R.Fxml.ITEM_ARMOR;
    }

    @Override
    public void onAddItem(ItemBase item, boolean remote) {
        service.insertAsync((Armor) item)
            .exceptionally(throwable -> {
                notifier.showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_RECORD_IS_NOT_INSERTED), item.getName())));
                LOGGER.error("Item {} se nepodařilo vložit do databáze", item.getName());
                throw new RuntimeException(throwable);
            })
            .thenAccept(armor -> notifier
                .showNotification(new Notification(String.format(translator.translate(
                    Translate.NOTIFY_RECORD_IS_INSERTED), item.getName()))));
    }

    @Override
    public void onUpdateItem(ItemBase item) {
        service.updateAsync((Armor) item)
            .exceptionally(throwable -> {
                notifier.showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_RECORD_IS_NOT_UPDATED), item.getName())));
                LOGGER.error("Položku {} se nepodařilo aktualizovat", item.getName());
                throw new RuntimeException(throwable);
            })
            .thenAccept(armor -> notifier
                .showNotification(new Notification(String.format(translator.translate(
                    Translate.NOTIFY_RECORD_IS_NOT_UPDATED), item.getName()))));
    }

    @Override
    public void insertItemToBundle(Bundle bundle, int index) {
        ItemArmorController.toBundle(bundle, (Armor) sortedList.get(index).getItemBase());
    }

    @Override
    public ItemBase fromBundle(Bundle bundle) {
        return ItemArmorController.fromBundle(bundle);
    }

    @Override
    public void requestRemoveItem(int index) {
        final ArmorEntry entry = sortedList.get(index);
        service.deleteAsync((Armor) entry.getItemBase())
            .exceptionally(throwable -> {
                notifier.showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_RECORD_IS_NOT_DELETED), entry.getName())));
                LOGGER.error("Položku {} se nepodařilo aktualizovat", entry.getName());
                throw new RuntimeException(throwable);
            })
            .thenAccept(armor -> notifier
                .showNotification(new Notification(String.format(translator.translate(
                    Translate.NOTIFY_RECORD_IS_DELETED), entry.getName()))));
    }

    @Override
    public void requestRemoveItem(ShopEntry entry, boolean remote) {
        service.deleteRemoteAsync((Armor) entry.getItemBase())
            .exceptionally(throwable -> {
                shopOnlineListener.handleItemRemove(entry.getName(), remote, false);
                throw new RuntimeException(throwable);
            })
            .thenAccept(aVoid -> shopOnlineListener.handleItemRemove(entry.getName(), remote, true));
    }

    @Override
    public void uploadRequest(ItemBase item) {
        service.uploadAsync((Armor) item)
            .exceptionally(throwable -> {
                shopOnlineListener.handleItemUpload(item.getName(), false);
                throw new RuntimeException(throwable);
            })
            .thenAccept(aVoid -> shopOnlineListener.handleItemUpload(item.getName(), true));
    }

    @Override
    public void clearSelectedRow() {
        tableArmor.getSelectionModel().clearSelection();
    }

    @Override
    public void synchronizeItems() {
        service.synchronize(this.user.getName())
            .thenAccept(total -> LOGGER
                .info("Bylo synchronizováno celkem: " + total + " předmětů typu brnění."));
    }

    @Override
    public Optional<ArmorEntry> getSelectedItem() {
        if (selectedRowIndex.getValue() == null || selectedRowIndex.get() < 0) {
            return Optional.empty();
        }

        return Optional.of(sortedList.get(selectedRowIndex.get()));
    }
}
