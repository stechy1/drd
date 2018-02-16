package cz.stechy.drd.app;

import cz.stechy.drd.R;
import cz.stechy.drd.model.inventory.InventoryRecord.Metadata;
import cz.stechy.drd.model.inventory.ItemContainer;
import cz.stechy.drd.model.inventory.ItemSlot;
import cz.stechy.drd.model.inventory.TooltipTranslator;
import cz.stechy.drd.model.inventory.container.FlowItemContainer;
import cz.stechy.drd.model.item.Backpack;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.dao.HeroDao;
import cz.stechy.drd.dao.InventoryDao;
import cz.stechy.drd.util.Translator;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;

/**
 * Obecný kontroler pro inventář v batohu či jiném předmětu
 */
public class BackpackController extends BaseController implements TooltipTranslator {

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

    private final HeroDao heroManager;
    private final Translator translator;
    private ItemContainer itemContainer;
    // Velikost inventáře = počet slotů v inventáři
    private int backpackSize;
    private String inventoryId;

    // endregion

    // region Constructors

    public BackpackController(HeroDao heroManager, Translator translator) {
        this.heroManager = heroManager;
        this.translator = translator;
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

    // region Private methods

    // region Method handlers

    private void itemClickHandler(ItemSlot itemSlot) {
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
    }

    // endregion

    // endregion

    @Override
    protected void onCreate(Bundle bundle) {
        backpackSize = bundle.getInt(BACKPACK_SIZE);
        inventoryId = bundle.getString(INVENTORY_ID);
        setTitle(bundle.getString(ITEM_NAME));
        itemContainer = new FlowItemContainer(this, backpackSize);
        container.setContent(itemContainer.getGraphics());
        itemContainer.setItemClickListener(this::itemClickHandler);
        setScreenSize(WIDTH, BackpackController.computeHeight(backpackSize));

        heroManager.getInventoryAsync()
            .thenCompose(inventoryService ->
                inventoryService.selectAsync(InventoryDao.ID_FILTER(inventoryId))
                    .thenCompose(backpackInventory ->
                        itemContainer.setInventoryManager(inventoryService, backpackInventory)))
            .exceptionally(throwable -> {
                itemContainer.clear();
                throw new RuntimeException(throwable);
            });
    }

    @Override
    public void onTooltipTranslateRequest(Map<String, String> map) {
        translator.translateTooltipKeys(map);
    }
}
