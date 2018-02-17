package cz.stechy.drd.app.shop;

import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.app.shop.entry.ShopEntry;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.inventory.InventoryHelper;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.service.HeroService;
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
    private final HeroService heroService;

    private ShoppingCart shoppingCart;

    // endregion

    // region Constructors

    public ShopController2(HeroService heroService) {
        this.heroService = heroService;
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
        // TODO reimplementovat transakce
//        try {
            //heroService.beginTransaction();
            final Hero heroCopy = heroService.getHero().duplicate();
            heroCopy.getMoney().subtract(shoppingCart.totalPrice);
            heroService.updateAsync(heroCopy)
                .thenCompose(hero ->
                    heroService.getInventoryAsync()
                        .thenCompose(inventoryService ->
                            InventoryHelper.insertItemsToInventoryAsync(inventoryService, items)))
            .thenAcceptAsync(aVoid -> {
//                try {
//                    heroService.commit();
//                    finish();
//                } catch (DatabaseException e) {
//                    e.printStackTrace();
//                }
            }, ThreadPool.JAVAFX_EXECUTOR)
            .exceptionally(throwable -> {
//                try {
//                    heroService.rollback();
//                } catch (DatabaseException e) {
//                    e.printStackTrace();
//                }
                throwable.printStackTrace();
                throw new RuntimeException(throwable);
            });
//        } catch (DatabaseException e) {
//            e.printStackTrace();
//        }
    }

    @FXML
    private void handleBack(ActionEvent actionEvent) {
        back();
    }

    // endregion

    public static class ItemResultEntry implements InventoryHelper.ItemRecord {

        // region Variables

        private final ItemBase itemBase;
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
            this.itemBase = entry.getItemBase();
            this.id.set(entry.getId());
            this.name.set(entry.getName());
            this.ammount.set(entry.getAmmount().getActValue().intValue());
            this.price = new Money(entry.getPrice().getRaw());
        }

        // endregion

        // region Getters & Setters

        @Override
        public ItemBase getItemBase() {
            return itemBase;
        }

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
