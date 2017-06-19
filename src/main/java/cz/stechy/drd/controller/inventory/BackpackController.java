package cz.stechy.drd.controller.inventory;

import cz.stechy.drd.R;
import cz.stechy.drd.model.Context;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.inventory.Inventory;
import cz.stechy.drd.model.inventory.InventoryRecord.Metadata;
import cz.stechy.drd.model.inventory.ItemClickListener;
import cz.stechy.drd.model.inventory.ItemContainer;
import cz.stechy.drd.model.inventory.container.FlowItemContainer;
import cz.stechy.drd.model.item.Backpack;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.persistent.HeroManager;
import cz.stechy.drd.model.persistent.InventoryManager;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

/**
 * Obecný kontroler pro inventář v batohu či jiném předmětu
 */
public class BackpackController extends BaseController implements Initializable {

    // region Constants

    public static final String BACKPACK_SIZE = "backpack_size";
    public static final String INVENTORY_ID = "inventory_id";

    // endregion

    // region Variables

    // region FXML

    @FXML
    private BorderPane container;

    // endregion

    private ItemContainer itemContainer;
    private ObjectProperty<Hero> hero;
    private HeroManager heroManager;
    // Velikost inventáře = počet slotů v inventáři
    private int backpackSize;
    private String inventoryId;

    // endregion

    // region Constructors

    public BackpackController(Context context) {
        this.heroManager = context.getManager(Context.MANAGER_HERO);
        this.hero = heroManager.getHero();
        this.hero.addListener(heroChangeListener);
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    protected void onCreate(Bundle bundle) {
        backpackSize = bundle.getInt(BACKPACK_SIZE);
        inventoryId = bundle.getString(INVENTORY_ID);
        itemContainer = new FlowItemContainer(backpackSize);
        container.setCenter(itemContainer.getGraphics());
        itemContainer.setItemClickListener(itemClickListener);
    }

    private final ChangeListener<? super Hero> heroChangeListener = (observable, oldValue, newValue) -> {
        final InventoryManager inventoryManager = heroManager.getInventory();
        try {
            final Inventory backpackInventory = inventoryManager.select(InventoryManager.ID_FILTER(inventoryId));
            itemContainer.setInventoryManager(inventoryManager, backpackInventory);
        } catch (DatabaseException e) {
            itemContainer.clear();
        }
    };

    private final ItemClickListener itemClickListener = itemSlot -> {
        final ItemBase item = itemSlot.getItemStack().getItem();
        switch (itemSlot.getItemStack().getItem().getItemType()) {
            case BACKPACK:
                final Backpack backpack = (Backpack) item;
                final Bundle bundle = new Bundle();
                final Metadata metadata = itemSlot.getItemStack().getMetadata();
                final String childInventoryId = (String) metadata.get(Backpack.CHILD_INVENTORY_ID);
                bundle.putInt(BACKPACK_SIZE, backpack.getSize().size);
                bundle.putString(INVENTORY_ID, childInventoryId);
                startNewDialog(R.FXML.BACKPACK, bundle);
                break;
        }
    };
}
