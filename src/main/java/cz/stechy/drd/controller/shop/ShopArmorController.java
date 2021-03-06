package cz.stechy.drd.controller.shop;

import cz.stechy.drd.R;
import cz.stechy.drd.model.Context;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.db.AdvancedDatabaseManager;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.entity.Height;
import cz.stechy.drd.model.item.Armor;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.shop.IShoppingCart;
import cz.stechy.drd.model.shop.OnDeleteItem;
import cz.stechy.drd.model.shop.OnDownloadItem;
import cz.stechy.drd.model.shop.OnUploadItem;
import cz.stechy.drd.model.shop.entry.ArmorEntry;
import cz.stechy.drd.model.shop.entry.GeneralEntry;
import cz.stechy.drd.model.shop.entry.ShopEntry;
import cz.stechy.drd.model.user.User;
import cz.stechy.drd.util.CellUtils;
import cz.stechy.drd.util.ObservableMergers;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pomocný kontroler pro obchod se zbrojí
 */
public class ShopArmorController implements Initializable, ShopItemController {

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
    private TableColumn<ArmorEntry, Integer> columnDefenceNumber;
    @FXML
    private TableColumn<ArmorEntry, Integer> columnMinimumStrength;
    @FXML
    private TableColumn<ArmorEntry, Integer> columnWeight;
    @FXML
    private TableColumn<ArmorEntry, Integer> columnPrice;
    @FXML
    private TableColumn<ArmorEntry, MaxActValue> columnAmmount;
    @FXML
    private TableColumn<ArmorEntry, ?> columnAction;

    // endregion

    private final ObservableList<ArmorEntry> armors = FXCollections.observableArrayList();
    private final ObjectProperty<Height> height = new SimpleObjectProperty<>(Height.B);
    private final AdvancedDatabaseManager<Armor> manager;
    private final User user;

    private IntegerProperty selectedRowIndex;
    private ResourceBundle resources;

    // endregion

    // region Constructors

    public ShopArmorController(Context context) {
        this.manager = context.getManager(Context.MANAGER_ARMOR);
        user = context.getUserManager().getUser().get();
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
        tableArmor.setItems(armors);
        tableArmor.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> selectedRowIndex.setValue(newValue));

        columnImage.setCellValueFactory(new PropertyValueFactory<>("image"));
        columnImage.setCellFactory(param -> CellUtils.forImage());
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        columnDefenceNumber.setCellValueFactory(new PropertyValueFactory<>("defenceNumber"));
        columnMinimumStrength.setCellValueFactory(new PropertyValueFactory<>("minimumStrength"));
        columnWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        columnAmmount.setCellValueFactory(new PropertyValueFactory<>("ammount"));
        columnAmmount.setCellFactory(param -> CellUtils.forMaxActValue());

        ObservableMergers.mergeList(armor -> new ArmorEntry(armor, height),
            armors, manager.selectAll());
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
        return R.FXML.ITEM_ARMOR;
    }

    @Override
    public void onAddItem(ItemBase item, boolean remote) {
        try {
            manager.insert((Armor) item);
            armors.get(
                armors.indexOf(
                    new ArmorEntry((Armor) item, height)))
                .setDownloaded(true);
        } catch (DatabaseException e) {
            logger.warn("Item {} se nepodařilo vložit do databáze", item.toString());
        }
    }

    @Override
    public void onUpdateItem(ItemBase item) {
        try {
            manager.update((Armor) item);
        } catch (DatabaseException e) {
            logger.warn("Item {} se napodařilo aktualizovat", item.toString());
        }
    }

    @Override
    public void insertItemToBundle(Bundle bundle, int index) {
        ItemArmorController.toBundle(bundle, (Armor) armors.get(index).getItemBase());
    }

    @Override
    public ItemBase fromBundle(Bundle bundle) {
        return ItemArmorController.fromBundle(bundle);
    }

    @Override
    public void requestRemoveItem(int index) {
        final ArmorEntry entry = armors.get(index);
        final String name = entry.getName();
        try {
            manager.delete(entry.getId());
        } catch (DatabaseException e) {
            logger.warn("Item {} se nepodařilo odebrat z databáze", name);
        }
    }

    @Override
    public void requestRemoveItem(ShopEntry item, boolean remote) {
        manager.deleteRemote((Armor) item.getItemBase(), remote);
    }

    @Override
    public void uploadRequest(ItemBase item) {
        manager.upload((Armor) item);
    }

    @Override
    public void clearSelectedRow() {
        tableArmor.getSelectionModel().clearSelection();
    }

    @Override
    public void onClose() {
        manager.toggleDatabase(false);
    }
}
