package cz.stechy.drd.controller.inventory;

import cz.stechy.drd.R;
import cz.stechy.drd.model.Context;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.inventory.Inventory;
import cz.stechy.drd.model.inventory.InventoryRecord.Metadata;
import cz.stechy.drd.model.inventory.ItemClickListener;
import cz.stechy.drd.model.inventory.ItemContainer;
import cz.stechy.drd.model.inventory.ItemSlot;
import cz.stechy.drd.model.inventory.container.FlowItemContainer;
import cz.stechy.drd.model.item.Backpack;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.persistent.HeroManager;
import cz.stechy.drd.model.persistent.InventoryManager;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;

/**
 * Obecný kontroler pro inventář v batohu či jiném předmětu
 */
public class BackpackController extends BaseController {

    // region Constants

    private static final int SLOTS_ON_ROW = 5;
    private static final int WIDTH;

    static {
        WIDTH = 16 // Left padding
                + ItemSlot.SLOT_SIZE * SLOTS_ON_ROW
                + ItemContainer.SLOT_SPACING * SLOTS_ON_ROW
                + 16 // Right padding
                + 16; // Width of scrollbar
    }

    public static final String ITEM_NAME = "item_name";
    public static final String BACKPACK_SIZE = "backpack_size";
    public static final String INVENTORY_ID = "inventory_id";

    // endregion

    // region Variables

    // region FXML

    @FXML
    private ScrollPane container;

    // endregion

    private final HeroManager heroManager;
    private ItemContainer itemContainer;
    // Velikost inventáře = počet slotů v inventáři
    private int backpackSize;
    private String inventoryId;

    // endregion

    // region Constructors

    public BackpackController(Context context) {
        this.heroManager = context.getManager(Context.MANAGER_HERO);
    }

    // endregion

    // region Private static methods

    /**
     * Spočítá potřebnou výšku inventáře
     *
     * @param slotCount Celkový počet slotů v inventáři
     * @return Výška inventáře
     */
    private static int computeHeight(final int slotCount) {
        final int rowCount = slotCount / SLOTS_ON_ROW;
        return 8 // Top padding
            + ItemSlot.SLOT_SIZE * rowCount
            + ItemContainer.SLOT_SPACING * rowCount
            + 8 // Bottom padding
            + ItemSlot.SLOT_SIZE; // WTF constant
    }

    // endregion

    @Override
    protected void onCreate(Bundle bundle) {
        backpackSize = bundle.getInt(BACKPACK_SIZE);
        inventoryId = bundle.getString(INVENTORY_ID);
        setTitle(bundle.getString(ITEM_NAME));
        itemContainer = new FlowItemContainer(backpackSize);
        container.setContent(itemContainer.getGraphics());
        itemContainer.setItemClickListener(itemClickListener);
        setScreenSize(WIDTH, BackpackController.computeHeight(backpackSize));

        final InventoryManager inventoryManager = heroManager.getInventory();
        try {
            final Inventory backpackInventory = inventoryManager.select(InventoryManager.ID_FILTER(inventoryId));
            itemContainer.setInventoryManager(inventoryManager, backpackInventory);
        } catch (DatabaseException e) {
            itemContainer.clear();
        }
    }

    private final ItemClickListener itemClickListener = itemSlot -> {
        final ItemBase item = itemSlot.getItemStack().getItem();
        switch (itemSlot.getItemStack().getItem().getItemType()) {
            case BACKPACK:
                final Backpack backpack = (Backpack) item;
                final Bundle bundle = new Bundle();
                final Metadata metadata = itemSlot.getItemStack().getMetadata();
                final String childInventoryId = (String) metadata.get(Backpack.CHILD_INVENTORY_ID);
                final String itemName = backpack.getName();
                bundle.putInt(BACKPACK_SIZE, backpack.getSize().size);
                bundle.putString(INVENTORY_ID, childInventoryId);
                bundle.putString(ITEM_NAME, itemName);
                startNewDialog(R.FXML.BACKPACK, bundle);
                break;
        }
    };
}
