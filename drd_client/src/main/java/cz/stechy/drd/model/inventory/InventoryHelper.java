package cz.stechy.drd.model.inventory;

import cz.stechy.drd.dao.InventoryDao;
import cz.stechy.drd.model.inventory.InventoryRecord.Metadata;
import cz.stechy.drd.model.item.Backpack;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.ItemType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Pomocná třída ke správci inventáře
 */
public class InventoryHelper {

    private static CompletableFuture<InventoryRecord> buildInventoryRecord(
        Map.Entry<Integer, Integer> entry,
        ItemRecord itemEntry, Inventory inventory, InventoryDao inventoryDao) {
        CompletableFuture<Metadata> futureMetadata;
        if (itemEntry.getItemBase().getItemType() == ItemType.BACKPACK) {
            final Metadata metadata = new Metadata();
            assert entry.getValue() == 1;
            final Backpack backpack = (Backpack) itemEntry.getItemBase();
            futureMetadata = inventoryDao.initSubInventoryAsync(backpack.getSize().size)
                .thenApply(backpackInventory -> {
                    metadata.put(Backpack.CHILD_INVENTORY_ID, backpackInventory.getId());

                    return metadata;
                });
        } else {
            futureMetadata = CompletableFuture.completedFuture(new Metadata());
        }

        return futureMetadata.thenApply(metadata -> new InventoryRecord.Builder()
            .inventoryId(inventory.getId())
            .itemId(itemEntry.getId())
            .ammount(entry.getValue())
            .slotId(entry.getKey())
            .metadata(metadata)
            .build());
    }

    public static CompletableFuture<Void> insertItemsToInventoryAsync(InventoryDao inventoryDao,
        List<? extends ItemRecord> itemsToInventory) {
        return inventoryDao.selectAsync(InventoryDao.MAIN_INVENTORY_FILTER)
            .thenCompose(inventory -> inventoryDao.getInventoryContentAsync(inventory)
                .thenCompose(inventoryContent -> {
                    final List<CompletableFuture> futureItemList = new ArrayList<>(
                        itemsToInventory.size());
                    itemsToInventory.stream()
                        .map(ItemRecord.class::cast)
                        .forEach(itemRecord ->
                            futureItemList.add(
                                inventoryContent.getFreeSlotAsync(itemRecord.getItemBase(),
                                    itemRecord.getAmmount())
                                    .thenCompose(freeSlotMap ->
                                        CompletableFuture.allOf(freeSlotMap
                                            .entrySet()
                                            .stream()
                                            .map(entry ->
                                                buildInventoryRecord(entry, itemRecord, inventory,
                                                    inventoryDao)
                                                    .thenCompose(inventoryContent::insertAsync))
                                            .toArray(CompletableFuture[]::new)))));
                    return CompletableFuture.allOf(
                        futureItemList.toArray(new CompletableFuture[0]));
                }));
    }

    public interface ItemRecord {

        ItemBase getItemBase();

        String getId();

        String getName();

        int getAmmount();
    }

}
