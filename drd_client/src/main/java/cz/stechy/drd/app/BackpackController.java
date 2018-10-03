package cz.stechy.drd.app;

import com.google.inject.Inject;
import cz.stechy.drd.R;
import cz.stechy.drd.model.inventory.InventoryContent.Metadata;
import cz.stechy.drd.model.inventory.ItemContainer;
import cz.stechy.drd.model.inventory.ItemSlot;
import cz.stechy.drd.model.inventory.ItemStack;
import cz.stechy.drd.model.inventory.TooltipTranslator;
import cz.stechy.drd.model.inventory.container.FlowItemContainer;
import cz.stechy.drd.model.item.Backpack;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.ItemType;
import cz.stechy.drd.service.hero.IHeroService;
import cz.stechy.drd.service.item.IItemRegistry;
import cz.stechy.drd.service.translator.ITranslatorService;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Obecný kontroler pro inventář v batohu či jiném předmětu
 */
public class BackpackController extends BaseController implements TooltipTranslator {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(BackpackController.class);

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

    private final IHeroService heroService;
    private final ITranslatorService translator;
    private final IItemRegistry itemRegistry;
    private ItemContainer itemContainer;
    private String inventoryId;

    // endregion

    // region Constructors

    @Inject
    public BackpackController(IHeroService heroService, ITranslatorService translator, IItemRegistry itemRegistry) {
        this.heroService = heroService;
        this.translator = translator;
        this.itemRegistry = itemRegistry;
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
                startNewDialog(R.Fxml.BACKPACK, bundle);
                break;
        }
    }

    private boolean backpackFilter(ItemStack itemStack) {
        if (itemStack.getItem().getItemType() == ItemType.BACKPACK) {
            final Metadata metadata = itemStack.getMetadata();
            final String id = (String) metadata.get(Backpack.CHILD_INVENTORY_ID);
            return !this.inventoryId.equals(id);
        }

        return true;
    }

    // endregion

    // endregion

    @Override
    protected void onCreate(Bundle bundle) {
        // Velikost inventáře = počet slotů v inventáři
        int backpackSize = bundle.getInt(BACKPACK_SIZE);
        inventoryId = bundle.getString(INVENTORY_ID);
        setTitle(bundle.getString(ITEM_NAME));
        itemContainer = new FlowItemContainer(itemRegistry, this, backpackSize);
        container.setContent(itemContainer.getGraphics());
        itemContainer.setItemClickListener(this::itemClickHandler);
        itemContainer.setInventoryFilter(this::backpackFilter);
        setScreenSize(WIDTH, BackpackController.computeHeight(backpackSize));

        heroService.getInventoryService()
            .thenAcceptAsync(inventoryService -> {
                inventoryService.getInventory(inventoryId).ifPresent(backpackInventory ->
                    itemContainer.setInventoryManager(inventoryService, backpackInventory));
            });
//                    .thenCompose(backpackInventory ->
//                        itemContainer.setInventoryManager(inventoryService, backpackInventory)));
//            .exceptionally(throwable -> {
//                itemContainer.clear();
//                LOGGER.error("Nepodařilo se načíst data inventáře.", throwable);
//                throw new RuntimeException(throwable);
//            });
    }

    @Override
    public void onTooltipTranslateRequest(Map<String, String> map) {
        translator.translateTooltipKeys(map);
    }
}
