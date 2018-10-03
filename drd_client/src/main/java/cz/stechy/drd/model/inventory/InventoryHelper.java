package cz.stechy.drd.model.inventory;

import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.model.inventory.InventoryContent.Metadata;
import cz.stechy.drd.model.item.Backpack;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.ItemType;
import cz.stechy.drd.service.inventory.IInventoryContentService;
import cz.stechy.drd.service.inventory.IInventoryService;
import cz.stechy.drd.service.inventory.InventoryService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Pomocná třída ke správci inventáře
 */
public class InventoryHelper {

    private static CompletableFuture<InventoryContent> buildInventoryRecord(Map.Entry<Integer, Integer> entry, ItemRecord itemEntry, Inventory inventory, IInventoryService inventoryService) {
        CompletableFuture<Metadata> futureMetadata;
        if (itemEntry.getItemBase().getItemType() == ItemType.BACKPACK) {
            final Metadata metadata = new Metadata();
            assert entry.getValue() == 1;
            final Backpack backpack = (Backpack) itemEntry.getItemBase();
            futureMetadata = inventoryService.initSubInventoryAsync(backpack.getSize().size)
                .thenApply(backpackInventory -> {
                    metadata.put(Backpack.CHILD_INVENTORY_ID, backpackInventory.getId());

                    return metadata;
                });
        } else {
            futureMetadata = CompletableFuture.completedFuture(new Metadata());
        }

        return futureMetadata.thenApply(metadata -> new InventoryContent.Builder()
            .inventoryId(inventory.getId())
            .itemId(itemEntry.getId())
            .ammount(entry.getValue())
            .slotId(entry.getKey())
            .metadata(metadata)
            .build());
    }

    public static CompletableFuture<Void> insertItemsToInventoryAsync(IInventoryService inventoryDao, List<? extends ItemRecord> itemsToInventory) {
        final Optional<Inventory> optionalInventory = inventoryDao.getInventory(InventoryService.MAIN_INVENTORY_FILTER);
        if (!optionalInventory.isPresent()) {
            return CompletableFuture.supplyAsync(() -> {
                throw new RuntimeException("Inventory not found.");
            }, ThreadPool.JAVAFX_EXECUTOR);
        }

        final Inventory inventory = optionalInventory.get();
        final IInventoryContentService inventoryContentService = inventoryDao.getInventoryContentService(inventory);
        final List<CompletableFuture> futureItemList = new ArrayList<>(itemsToInventory.size());
        itemsToInventory.stream()
            .map(ItemRecord.class::cast)
            .forEach(itemRecord ->
                futureItemList.add(inventoryContentService.getFreeSlotAsync(itemRecord.getItemBase(), itemRecord.getAmmount())
                        .thenCompose(freeSlotMap ->
                            CompletableFuture.allOf(freeSlotMap
                                .entrySet()
                                .stream()
                                .map(entry -> buildInventoryRecord(entry, itemRecord, inventory, inventoryDao).thenCompose(inventoryContentService::insertContent))
                                .toArray(CompletableFuture[]::new)))));
        return CompletableFuture.allOf(futureItemList.toArray(new CompletableFuture[0]));
    }

    public interface ItemRecord {

        ItemBase getItemBase();

        String getId();

        String getName();

        int getAmmount();
    }

}
