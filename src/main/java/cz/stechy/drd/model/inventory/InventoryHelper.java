package cz.stechy.drd.model.inventory;

import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.inventory.InventoryRecord.Metadata;
import cz.stechy.drd.model.item.Backpack;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.service.ItemRegistry;
import cz.stechy.drd.model.item.ItemType;
import cz.stechy.drd.model.persistent.InventoryContent;
import cz.stechy.drd.model.persistent.InventoryService;
import java.util.List;
import java.util.Map;
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
            .forEach(itemEntry -> {
                final Optional<ItemBase> itemOptional = ItemRegistry.getINSTANCE()
                    .getItemById(itemEntry.getId());
                if (!itemOptional.isPresent()) {
                    return;
                }

                final ItemBase itemBase = itemOptional.get();
                try {
                    final Map<Integer, Integer> freeSlotMap = inventoryContent
                        .getFreeSlot(itemBase, itemEntry.getAmmount());
                    freeSlotMap.entrySet().stream().map(entry -> {
                        final Metadata metadata = new Metadata();
                        if (itemBase.getItemType() == ItemType.BACKPACK) {
                            assert entry.getValue() == 1;
                            final Backpack backpack = (Backpack) itemBase;
                            try {
                                final String childInventoryId = inventoryManager
                                    .initSubInventory(backpack.getSize().size);
                                metadata.put(Backpack.CHILD_INVENTORY_ID, childInventoryId);
                            } catch (DatabaseException e) {
                                e.printStackTrace();
                            }
                        }
                        return new InventoryRecord.Builder()
                            .inventoryId(inventory.getId())
                            .itemId(itemEntry.getId())
                            .ammount(entry.getValue())
                            .slotId(entry.getKey())
                            .metadata(metadata)
                            .build();
                    }).forEach(record -> {
                        try {
                            inventoryContent.insert(record);
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (InventoryException e) {
                    return;
                }
            });
    }

    public interface ItemRecord {

        String getId();

        String getName();

        int getAmmount();
    }

}
