package cz.stechy.drd.model.inventory;

import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.inventory.InventoryRecord.Builder;
import cz.stechy.drd.model.inventory.InventoryRecord.Metadata;
import cz.stechy.drd.model.item.Backpack;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.ItemRegistry;
import cz.stechy.drd.model.item.ItemType;
import cz.stechy.drd.model.persistent.InventoryContent;
import cz.stechy.drd.model.persistent.InventoryService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Pomocná třída ke správci inventáře
 */
public class InventoryHelper {

    public static void insertItemsToInventory(InventoryService inventoryManager, List<? extends ItemRecord> itemsToInventory) throws DatabaseException {
        final Inventory inventory = inventoryManager.select(InventoryService.MAIN_INVENTORY_FILTER);
        final InventoryContent inventoryContent = inventoryManager.getInventoryContent(inventory);
        itemsToInventory.stream()
            .filter(itemRecord -> itemRecord.getAmmount() > 0)
            .map(itemEntry -> {
                final Optional<ItemBase> itemOptional = ItemRegistry.getINSTANCE()
                    .getItemById(itemEntry.getId());
                if (!itemOptional.isPresent()) {
                    return null;
                }

                final ItemBase itemBase = itemOptional.get();
                try {
                    final InventoryRecord inventoryRecord = new Builder()
                        .inventoryId(inventory.getId())
                        .itemId(itemEntry.getId())
                        .ammount(itemEntry.getAmmount())
                        .slotId(inventoryContent.getFreeSlot())
                        .build();

                    if (itemBase.getItemType() == ItemType.BACKPACK) {
                        final Metadata metadata = inventoryRecord.getMetadata();
                        final Backpack backpack = (Backpack) itemBase;
                        final String childInventoryId = inventoryManager
                            .initSubInventory(backpack.getSize().size);
                        metadata.put(Backpack.CHILD_INVENTORY_ID, childInventoryId);
                    }

                    return inventoryRecord;
                } catch (InventoryException | DatabaseException e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .forEach(record -> {
                try {
                    inventoryContent.insert(record);
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
            });
    }

    public interface ItemRecord {

        String getId();

        String getName();

        int getAmmount();
    }

}
