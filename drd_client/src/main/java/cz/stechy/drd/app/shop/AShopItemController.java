package cz.stechy.drd.app.shop;

import static cz.stechy.drd.app.shop.ShopHelper.SHOP_ROW_HEIGHT;

import cz.stechy.drd.R;
import cz.stechy.drd.R.Translate;
import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.app.shop.entry.ShopEntry;
import cz.stechy.drd.db.AdvancedDatabaseService;
import cz.stechy.drd.db.base.OnlineItem;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.User;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.service.UserService;
import cz.stechy.drd.util.CellUtils;
import cz.stechy.drd.util.ObservableMergers;
import cz.stechy.drd.util.Translator;
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

@SuppressWarnings("unchecked")
public abstract class AShopItemController<T extends OnlineItem, E extends ShopEntry> implements Initializable, ShopItemController<E> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(AShopItemController.class);

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TableView<E> tableEntries;
    @FXML
    protected TableColumn<E, Image> columnImage;
    @FXML
    protected TableColumn<E, String> columnName;
    @FXML
    protected TableColumn<E, String> columnAuthor;
    @FXML
    protected TableColumn<E, Integer> columnWeight;
    @FXML
    protected TableColumn<E, Money> columnPrice;
    @FXML
    protected TableColumn<E, MaxActValue> columnAmmount;
    @FXML
    protected TableColumn<E, ?> columnAction;

    // endregion

    private final ObservableList<E> entries = FXCollections.observableArrayList();
    final SortedList<E> sortedList = new SortedList<>(entries, Comparator.comparing(ShopEntry::getName));
    private final BooleanProperty ammountEditable = new SimpleBooleanProperty(true);
    private final BooleanProperty highlightDiffItem = new SimpleBooleanProperty(false);
    protected final AdvancedDatabaseService<T> service;
    protected final Translator translator;
    protected final User user;

    private IntegerProperty selectedRowIndex;
    protected ResourceBundle resources;
    private ShopNotificationProvider notifier;
    private ShopOnlineListener shopOnlineListener;

    // endregion

    // region Constructors

    AShopItemController(AdvancedDatabaseService<T> service, Translator translator, UserService userService) {
        this.service = service;
        this.translator = translator;
        this.user = userService.getUser();
    }

    // endregion

    protected abstract E getEntry(T item);

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        this.resources = resources;
        tableEntries.setItems(sortedList);
        tableEntries.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> selectedRowIndex.setValue(newValue));
        tableEntries.setFixedCellSize(SHOP_ROW_HEIGHT);
        tableEntries.setRowFactory(param -> new ShopRow<>(highlightDiffItem));
        sortedList.comparatorProperty().bind(tableEntries.comparatorProperty());

        columnWeight.setCellFactory(param -> CellUtils.forWeight());
        columnImage.setCellFactory(param -> CellUtils.forImage());
        columnPrice.setCellFactory(param -> CellUtils.forMoney());
        columnAmmount.setCellFactory(param -> CellUtils.forMaxActValue(ammountEditable));
    }

    @Override
    public void setShoppingCart(IShoppingCart shoppingCart) {
        columnAction.setCellFactory(param -> ShopHelper.forActionButtons(shoppingCart::addItem, shoppingCart::removeItem, resources, ammountEditable));

        final Function<T, E> mapper = generalItem -> {
            final E entry;
            final Optional<ShopEntry> cartEntry = shoppingCart.getEntry(generalItem.getId());
            entry = cartEntry.map(shopEntry -> (E) shopEntry).orElseGet(() -> getEntry(generalItem));

            return entry;
        };

        service.selectAllAsync()
            .thenAccept(entryList -> ObservableMergers.mergeList(mapper, entries, entryList));
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
        highlightDiffItem.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue) {
                service.getDiff().thenAcceptAsync(diffEntries ->
                    diffEntries.forEach(diffEntry -> {
                        final String id = diffEntry.getId();
                        entries.parallelStream()
                            .filter(entry -> id.equals(entry.getId()))
                            .findFirst()
                            .ifPresent(entry -> entry.setDiffMap(diffEntry.getDiffMap()));
                    }), ThreadPool.JAVAFX_EXECUTOR);
            } else {
                entries.parallelStream().forEach(ShopEntry::clearDiffMap);
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
    public void onAddItem(ItemBase item, boolean remote) {
        service.insertAsync((T) item)
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
        service.updateAsync((T) item)
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
    public void requestRemoveItem(int index) {
        final E entry = sortedList.get(index);
        service.deleteAsync((T) entry.getItemBase())
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
        service.deleteRemoteAsync((T) entry.getItemBase())
            .exceptionally(throwable -> {
                shopOnlineListener.handleItemRemove(entry.getName(), remote, false);
                throw new RuntimeException(throwable);
            })
            .thenAccept(aVoid -> shopOnlineListener.handleItemRemove(entry.getName(), remote, true));
    }

    @Override
    public void uploadRequest(ItemBase item) {
        service.uploadAsync((T) item)
            .exceptionally(throwable -> {
                shopOnlineListener.handleItemUpload(item.getName(), false);
                throw new RuntimeException(throwable);
            })
            .thenAccept(aVoid -> shopOnlineListener.handleItemUpload(item.getName(), true));
    }

    @Override
    public void clearSelectedRow() {
        tableEntries.getSelectionModel().clearSelection();
    }

    @Override
    public void synchronizeItems() {
        service.synchronize(this.user.getName())
            .thenAccept(total -> LOGGER.info("Bylo synchronizováno celkem: " + total + " předmětů typu general item."));
    }

    @Override
    public Optional<E> getSelectedItem() {
        if (selectedRowIndex.getValue() == null || selectedRowIndex.get() < 0) {
            return Optional.empty();
        }

        return Optional.of(sortedList.get(selectedRowIndex.get()));
    }

    @Override
    public void updateLocalItem(ShopEntry itemBase) {
        service.selectOnline(AdvancedDatabaseService.ID_FILTER(itemBase.getId()))
            .ifPresent(generalItem -> {
                service.updateAsync(generalItem).thenAccept(entry -> {
                    LOGGER.info("Aktualizace proběhla v pořádku, jdu vymazat mapu rozdílů.");
                    itemBase.clearDiffMap();
                });
            });
    }

    @Override
    public void updateOnlineItem(ShopEntry itemBase) {
        service.uploadAsync((T) itemBase.getItemBase()).thenAccept(ignored -> {
            LOGGER.info("Aktualizace online záznamu proběhla úspěšně.");
        });
    }
}
