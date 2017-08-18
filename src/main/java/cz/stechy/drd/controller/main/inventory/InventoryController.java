package cz.stechy.drd.controller.main.inventory;

import cz.stechy.drd.Context;
import cz.stechy.drd.R;
import cz.stechy.drd.controller.InjectableChild;
import cz.stechy.drd.controller.inventory.BackpackController;
import cz.stechy.drd.controller.main.MainScreen;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.inventory.Inventory;
import cz.stechy.drd.model.inventory.InventoryRecord.Metadata;
import cz.stechy.drd.model.inventory.InventoryType;
import cz.stechy.drd.model.inventory.ItemClickListener;
import cz.stechy.drd.model.inventory.ItemContainer;
import cz.stechy.drd.model.inventory.TooltipTranslator;
import cz.stechy.drd.model.inventory.container.EquipItemContainer;
import cz.stechy.drd.model.inventory.container.GridItemContainer;
import cz.stechy.drd.model.item.Backpack;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.persistent.HeroService;
import cz.stechy.drd.model.persistent.InventoryContent;
import cz.stechy.drd.model.persistent.InventoryService;
import cz.stechy.drd.util.Translator;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * Kontroler pro inventář hrdiny
 */
public class InventoryController implements Initializable, MainScreen, InjectableChild,
    TooltipTranslator {

    // region Variables

    // region FXML

    @FXML
    private BorderPane container;
    @FXML
    private Label lblWeight;
    // endregion

    // Základní inventář
    private final ItemContainer mainItemContainer = new GridItemContainer(this, 20, 5, 4);
    // Inventář s výbavou hrdiny
    private final ItemContainer equipItemContainer = new EquipItemContainer(this);
    private final Translator translator;

    private HeroService heroManager;
    private ReadOnlyObjectProperty<Hero> hero;
    private BaseController parent;

    // endregion

    // region Constructors

    public InventoryController(Context context) {
        this.heroManager = context.getService(Context.SERVICE_HERO);
        this.translator = context.getTranslator();

        mainItemContainer.setItemClickListener(itemClickListener);
        equipItemContainer.setItemClickListener(itemClickListener);
    }

    // endregion

    @Override
    public void setHero(final ReadOnlyObjectProperty<Hero> hero) {
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

    @Override
    public void injectParent(BaseController parent) {
        this.parent = parent;
    }

    @Override
    public void onTooltipTranslateRequest(Map<String, String> map) {
        translator.translateTooltipKeys(map);
    }

    private final ChangeListener<? super Hero> heroChangeListener = (observable, oldValue, newValue) -> {
        // Získám správce inventáře podle hrdiny
        final InventoryService inventoryManager = heroManager.getInventory();
        InventoryContent.clearWeight();
        try {
            // Získám záznam hlavního inventáře
            final Inventory mainInventory = inventoryManager.select(InventoryService.MAIN_INVENTORY_FILTER);
            mainItemContainer.setInventoryManager(inventoryManager, mainInventory);
            // Inicializace inventáře výbavy hrdiny
            Inventory equipInventory = null;
            try {
                equipInventory = inventoryManager.select(InventoryService.EQUIP_INVENTORY_FILTER);
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
        final ItemBase item = itemSlot.getItemStack().getItem();
        switch (item.getItemType()) {
            case BACKPACK:
                final Backpack backpack = (Backpack) item;
                final Bundle bundle = new Bundle();
                final Metadata metadata = itemSlot.getItemStack().getMetadata();
                final String childInventoryId = (String) metadata.get(Backpack.CHILD_INVENTORY_ID);
                final String itemName = backpack.getName();
                bundle.putInt(BackpackController.BACKPACK_SIZE, backpack.getSize().size);
                bundle.putString(BackpackController.INVENTORY_ID, childInventoryId);
                bundle.putString(BackpackController.ITEM_NAME, itemName);
                parent.startNewDialog(R.FXML.BACKPACK, bundle);
                break;
        }
    };
}