package cz.stechy.drd.app.shop;

import static cz.stechy.drd.app.shop.ShopHelper.SHOP_ROW_HEIGHT;

import cz.stechy.drd.R;
import cz.stechy.drd.R.Translate;
import cz.stechy.drd.app.shop.entry.GeneralEntry;
import cz.stechy.drd.app.shop.entry.ShopEntry;
import cz.stechy.drd.dao.GeneralItemDao;
import cz.stechy.drd.db.AdvancedDatabaseService;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.User;
import cz.stechy.drd.model.item.GeneralItem;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.service.UserService;
import cz.stechy.drd.util.CellUtils;
import cz.stechy.drd.util.ObservableMergers;
import cz.stechy.drd.util.Translator;
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
    private final Translator translator;
    private final User user;

    private IntegerProperty selectedRowIndex;
    private ResourceBundle resources;
    private ShopNotificationProvider notifier;
    private ShopOnlineListener shopOnlineListener;

    // endregion

    // region Constrollers

    public ShopGeneralController(UserService userService, GeneralItemDao generalItemDao,
        Translator translator) {
        this.service = generalItemDao;
        this.translator = translator;
        this.user = userService.getUser();
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
            entry = cartEntry.map(shopEntry -> (GeneralEntry) shopEntry)
                .orElseGet(() -> new GeneralEntry(generalItem));

            return entry;
        };

        service.selectAllAsync()
            .thenAccept(generalItemsList -> ObservableMergers
                .mergeList(mapper, generalItems, generalItemsList));
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
    public void setOnlineListener(ShopOnlineListener onlineListener) {
        this.shopOnlineListener = onlineListener;
    }

    @Override
    public String getEditScreenName() {
        return R.Fxml.ITEM_GENERAL;
    }

    @Override
    public void onAddItem(ItemBase item, boolean remote) {
        service.insertAsync((GeneralItem) item)
            .exceptionally(throwable -> {
                notifier.showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_RECORD_IS_NOT_INSERTED), item.getName())));
                LOGGER.error("Item {} se nepodařilo vložit do databáze", item.getName());
                throw new RuntimeException(throwable);
            })
            .thenAccept(generalItem -> notifier
                .showNotification(new Notification(String.format(translator.translate(
                    Translate.NOTIFY_RECORD_IS_INSERTED), item.getName()))));
    }

    @Override
    public void onUpdateItem(ItemBase item) {
        service.updateAsync((GeneralItem) item)
            .exceptionally(throwable -> {
                notifier.showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_RECORD_IS_NOT_UPDATED), item.getName())));
                LOGGER.error("Položku {} se nepodařilo aktualizovat", item.getName());
                throw new RuntimeException(throwable);
            })
            .thenAccept(generalItem -> notifier
                .showNotification(new Notification(String.format(translator.translate(
                    Translate.NOTIFY_RECORD_IS_NOT_UPDATED), item.getName()))));
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
        service.deleteAsync((GeneralItem) entry.getItemBase())
            .exceptionally(throwable -> {
                notifier.showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_RECORD_IS_NOT_DELETED), entry.getName())));
                LOGGER.error("Položku {} se nepodařilo aktualizovat", entry.getName());
                throw new RuntimeException(throwable);
            })
            .thenAccept(generalItem -> notifier
                .showNotification(new Notification(String.format(translator.translate(
                    Translate.NOTIFY_RECORD_IS_DELETED), entry.getName()))));
    }

    @Override
    public void requestRemoveItem(ShopEntry entry, boolean remote) {
        service.deleteRemoteAsync((GeneralItem) entry.getItemBase())
            .exceptionally(throwable -> {
                shopOnlineListener.handleItemRemove(entry.getName(), remote, false);
                throw new RuntimeException(throwable);
            })
            .thenAccept(aVoid -> shopOnlineListener.handleItemRemove(entry.getName(), remote, true));
    }

    @Override
    public void uploadRequest(ItemBase item) {
        service.uploadAsync((GeneralItem) item)
            .exceptionally(throwable -> {
                shopOnlineListener.handleItemUpload(item.getName(), false);
                throw new RuntimeException(throwable);
            })
            .thenAccept(aVoid -> shopOnlineListener.handleItemUpload(item.getName(), true));
    }

    @Override
    public void clearSelectedRow() {
        tableGeneralItems.getSelectionModel().clearSelection();
    }

    @Override
    public void synchronizeItems() {
        service.synchronize(this.user.getName())
            .thenAccept(total -> LOGGER
                .info("Bylo synchronizováno celkem: " + total + " předmětů typu general item."));
    }

    @Override
    public Optional<GeneralEntry> getSelectedItem() {
        if (selectedRowIndex.getValue() == null || selectedRowIndex.get() < 0) {
            return Optional.empty();
        }

        return Optional.of(sortedList.get(selectedRowIndex.get()));
    }

    @Override
    public void showDiffDialog() {
        service.getDiff().thenAccept(diffEntries -> {
            System.out.println(diffEntries.toString());
        });
    }
}
