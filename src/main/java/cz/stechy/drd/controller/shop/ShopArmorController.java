package cz.stechy.drd.controller.shop;

import static cz.stechy.drd.controller.shop.ShopHelper.SHOP_ROW_HEIGHT;

import cz.stechy.drd.Context;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.R;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.db.AdvancedDatabaseService;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.db.base.Firebase.OnDeleteItem;
import cz.stechy.drd.model.db.base.Firebase.OnDownloadItem;
import cz.stechy.drd.model.db.base.Firebase.OnUploadItem;
import cz.stechy.drd.model.entity.Height;
import cz.stechy.drd.model.item.Armor;
import cz.stechy.drd.model.item.Armor.ArmorType;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.shop.IShoppingCart;
import cz.stechy.drd.model.shop.entry.ArmorEntry;
import cz.stechy.drd.model.shop.entry.GeneralEntry;
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
import javafx.scene.control.cell.PropertyValueFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(ShopArmorController.class);

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
    private final ObjectProperty<Height> height = new SimpleObjectProperty<>(Height.B);
    private final AdvancedDatabaseService<Armor> service;
    private final Translator translator;
    private final User user;

    private IntegerProperty selectedRowIndex;
    private ResourceBundle resources;

    // endregion

    // region Constructors

    public ShopArmorController(Context context) {
        this.service = context.getService(Context.SERVICE_ARMOR);
        this.translator = context.getTranslator();
        this.user = context.getUserService().getUser().get();
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
        sortedList.comparatorProperty().bind(tableArmor.comparatorProperty());

        columnImage.setCellValueFactory(new PropertyValueFactory<>("image"));
        columnImage.setCellFactory(param -> CellUtils.forImage());
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        columnArmorType.setCellValueFactory(new PropertyValueFactory<>("armorType"));
        columnArmorType.setCellFactory(
            TextFieldTableCell.forTableColumn(StringConvertors.forArmorType(translator)));
        columnDefenceNumber.setCellValueFactory(new PropertyValueFactory<>("defenceNumber"));
        columnMinimumStrength.setCellValueFactory(new PropertyValueFactory<>("minimumStrength"));
        columnWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        columnPrice.setCellFactory(param -> CellUtils.forMoney());
        columnAmmount.setCellValueFactory(new PropertyValueFactory<>("ammount"));
        columnAmmount.setCellFactory(param -> CellUtils.forMaxActValue(ammountEditable));
    }

    @Override
    public void setShoppingCart(IShoppingCart shoppingCart,
        OnUploadItem<ArmorEntry> uploadHandler,
        OnDownloadItem<ArmorEntry> downloadHandler,
        OnDeleteItem<ArmorEntry> deleteHandler) {
        columnAction.setCellFactory(param -> ShopHelper
            .forActionButtons(shoppingCart::addItem, shoppingCart::removeItem, uploadHandler,
                downloadHandler, deleteHandler, user, resources, ammountEditable));

        ObservableMergers.mergeList(armor -> {
                final ArmorEntry entry;
                final Optional<ShopEntry> cartEntry = shoppingCart.getEntry(armor.getId());
                if (cartEntry.isPresent()) {
                    entry = (ArmorEntry) cartEntry.get();
                } else {
                    entry = new ArmorEntry(armor, height);
                }

                return entry;
            },
            armors, service.selectAll());
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
        return R.FXML.ITEM_ARMOR;
    }

    @Override
    public void onAddItem(ItemBase item, boolean remote) {
        try {
            service.insert((Armor) item);
        } catch (DatabaseException e) {
            logger.warn("Item {} se nepodařilo vložit do databáze", item.toString());
        }
    }

    @Override
    public void onUpdateItem(ItemBase item) {
        try {
            service.update((Armor) item);
        } catch (DatabaseException e) {
            logger.warn("Item {} se napodařilo aktualizovat", item.toString());
        }
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
        final String name = entry.getName();
        try {
            service.delete(entry.getId());
        } catch (DatabaseException e) {
            logger.warn("Item {} se nepodařilo odebrat z databáze", name);
        }
    }

    @Override
    public void requestRemoveItem(ShopEntry item, boolean remote) {
        service.deleteRemote((Armor) item.getItemBase(), remote);
    }

    @Override
    public void uploadRequest(ItemBase item) {
        service.upload((Armor) item);
    }

    @Override
    public void clearSelectedRow() {
        tableArmor.getSelectionModel().clearSelection();
    }

    @Override
    public void synchronizeItems() {
        service.synchronize(this.user.getName(), total ->
            logger.info("Bylo synchronizováno celkem: " + total + " předmětů typu brnění."));
    }
}
