package cz.stechy.drd.controller.main;

import cz.stechy.drd.model.Context;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.inventory.EquipItemContainer;
import cz.stechy.drd.model.inventory.GridItemContainer;
import cz.stechy.drd.model.inventory.Inventory;
import cz.stechy.drd.model.inventory.InventoryType;
import cz.stechy.drd.model.inventory.ItemClickListener;
import cz.stechy.drd.model.inventory.ItemContainer;
import cz.stechy.drd.model.persistent.HeroManager;
import cz.stechy.drd.model.persistent.InventoryContent;
import cz.stechy.drd.model.persistent.InventoryManager;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * Kontroler pro inventář hrdiny
 */
public class InventoryController implements Initializable, MainScreen {

    // region Constants

    private static final Predicate<? super Inventory> MAIN_INVENTORY_FILTER = inventory ->
        inventory.getInventoryType() == InventoryType.MAIN;
    private static final Predicate<? super Inventory> EQUIP_INVENTORY_FILTER = inventory ->
        inventory.getInventoryType() == InventoryType.EQUIP;

    // endregion

    // region Variables

    // region FXML

    @FXML
    private BorderPane container;
    @FXML
    private Label lblWeight;
    // endregion

    // Základní inventář
    private final ItemContainer mainItemContainer = new GridItemContainer(20, 5, 4);
    // Inventář s výbavou hrdiny
    private final ItemContainer equipItemContainer = new EquipItemContainer();

    private HeroManager heroManager;
    private ObjectProperty<Hero> hero;

    // endregion

    // region Constructors

    public InventoryController(Context context) {
        this.heroManager = context.getManager(Context.MANAGER_HERO);

        mainItemContainer.setItemClickListener(itemClickListener);
        equipItemContainer.setItemClickListener(itemClickListener);
    }

    // endregion

    @Override
    public void setHero(final ObjectProperty<Hero> hero) {
        if (this.hero != null) {
            this.hero.removeListener(heroChangeListener);
        }

        this.hero = hero;
        this.hero.addListener(heroChangeListener);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        container.setLeft(equipItemContainer.getGraphics());
        container.setCenter(mainItemContainer.getGraphics());

        lblWeight.textProperty().bind(InventoryContent.getWeight().asString());
    }

    private final ChangeListener<? super Hero> heroChangeListener = (observable, oldValue, newValue) -> {
        // Získám správce inventáře podle hrdiny
        final InventoryManager inventoryManager = heroManager.getInventory();
        InventoryContent.clearWeight();
        try {
            // Získám záznam hlavního inventáře
            final Inventory mainInventory = inventoryManager.select(MAIN_INVENTORY_FILTER);
            mainItemContainer.setInventoryManager(inventoryManager, mainInventory);
            // Inicializace inventáře výbavy hrdiny
            Inventory equipInventory = null;
            try {
                equipInventory = inventoryManager.select(EQUIP_INVENTORY_FILTER);
            } catch (DatabaseException e) {
                // Ještě nebyl vytvořen záznam o equip inventáři pro danou postavu
                equipInventory = new Inventory.Builder()
                    .heroId(mainInventory.getHeroId())
                    .inventoryType(InventoryType.EQUIP)
                    .capacity(EquipItemContainer.CAPACITY)
                    .build();
                inventoryManager.insert(equipInventory);
            } finally {
                assert equipInventory != null;
                equipItemContainer.setInventoryManager(inventoryManager, equipInventory);
            }
        } catch (DatabaseException e) {
            mainItemContainer.clear();
            equipItemContainer.clear();
        }
    };

    private final ItemClickListener itemClickListener = itemSlot -> {
        switch (itemSlot.getItemStack().getItem().getItemType()) {
            case BACKPACK:

                break;
        }
    };
}
