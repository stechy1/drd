package cz.stechy.drd.controller.shop;

import cz.stechy.drd.Money;
import cz.stechy.drd.model.Context;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.inventory.InventoryHelper;
import cz.stechy.drd.model.persistent.HeroService;
import cz.stechy.drd.model.persistent.InventoryService;
import cz.stechy.drd.model.shop.ShoppingCart;
import cz.stechy.drd.model.shop.entry.ShopEntry;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Kontroler pro zobrazení nakoupení předmětů
 */
public class ShopController2 extends BaseController implements Initializable {

    // region Constants

    public static final String SHOPPING_CART = "shopping_cart";
    public static final String HERO_ID = "hero_id";

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TableView<ItemResultEntry> tableView;
    @FXML
    private TableColumn<ItemResultEntry, String> columnName;
    @FXML
    private TableColumn<ItemResultEntry, Integer> columnAmmount;
    @FXML
    private TableColumn<ItemResultEntry, Integer> columnPrice;

    // endregion

    private final ObservableList<ItemResultEntry> items = FXCollections.observableArrayList();
    private final HeroService heroManager;

    private ShoppingCart shoppingCart;

    // endregion

    // region Constructors

    public ShopController2(Context context) {
        this.heroManager = context.getManager(Context.MANAGER_HERO);
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableView.setItems(items);

        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        columnAmmount.setCellValueFactory(new PropertyValueFactory<>("ammount"));
    }

    @Override
    protected void onCreate(Bundle bundle) {
        this.shoppingCart = bundle.get(SHOPPING_CART);
        items.setAll(shoppingCart.orderList.stream()
            .map(ItemResultEntry::new)
            .collect(Collectors.toList()));
    }

    @Override
    protected void onResume() {
        setScreenSize(400, 300);
    }

    // region Button handlers

    @FXML
    private void handleFinishShopping(ActionEvent actionEvent) {
        try {
            // Odečtení peněz
            heroManager.beginTransaction();
            final Hero heroCopy = heroManager.getHero().get().duplicate();
            heroCopy.getMoney().subtract(shoppingCart.totalPrice);
            heroManager.update(heroCopy);

            final InventoryService inventoryManager = heroManager.getInventory();
            InventoryHelper.insertItemsToInventory(inventoryManager, items);

            heroManager.commit();

        } catch (DatabaseException e) {
            e.printStackTrace();
            try {
                heroManager.rollback();
            } catch (DatabaseException e1) {
                e1.printStackTrace();
            }
            return;
        }
        finish();
    }

    @FXML
    private void handleBack(ActionEvent actionEvent) {
        back();
    }

    // endregion

    public static class ItemResultEntry implements InventoryHelper.ItemRecord {

        // region Variables

        protected final StringProperty id = new SimpleStringProperty();
        protected final StringProperty name = new SimpleStringProperty();
        protected final IntegerProperty ammount = new SimpleIntegerProperty();
        protected final Money price;

        // endregion

        // region Constructors

        /**
         * Inicitlizuje jeden záznam v tabulce
         *
         * @param entry {@link ShopEntry}
         */
        public ItemResultEntry(ShopEntry entry) {
            this.id.set(entry.getId());
            this.name.set(entry.getName());
            this.ammount.set(entry.getAmmount().getActValue().intValue());
            this.price = new Money(entry.getPrice().getRaw());
        }

        // endregion

        // region Getters & Setters

        public String getId() {
            return id.get();
        }

        public StringProperty idProperty() {
            return id;
        }

        public String getName() {
            return name.get();
        }

        public StringProperty nameProperty() {
            return name;
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public int getAmmount() {
            return ammount.get();
        }

        public IntegerProperty ammountProperty() {
            return ammount;
        }

        public void setAmmount(int ammount) {
            this.ammount.set(ammount);
        }

        public Money getPrice() {
            return price;
        }

        // endregion

    }
}
