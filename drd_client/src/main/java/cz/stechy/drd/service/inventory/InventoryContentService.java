package cz.stechy.drd.service.inventory;

import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.db.BaseOfflineTable;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.db.table.inventory_content.InventoryContentOfflineTable;
import cz.stechy.drd.model.inventory.Inventory;
import cz.stechy.drd.model.inventory.InventoryContent;
import cz.stechy.drd.model.inventory.InventoryException;
import cz.stechy.drd.model.item.ItemBase;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class InventoryContentService implements IInventoryContentService {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryContentService.class);

    private static final int SLOT_OCCUPIED = 1;
    private static final int SLOT_NOT_OCCUPIED = 0;

    // endregion

    // region Variables

    private final BaseOfflineTable<InventoryContent> inventoryContentTable;
    private final Inventory inventory;
    private final List<InventoryContent> inventoryContent;

    private final int[] occupiedSlots;

    // endregion

    // region Constructors

    InventoryContentService(ITableDefinitionsFactory tableDefinitionsFactory, Database db, Inventory inventory) {
        this.inventoryContentTable = new InventoryContentOfflineTable(tableDefinitionsFactory, db);
        this.inventory = inventory;
        this.occupiedSlots = new int[inventory.getCapacity()];
        this.inventoryContent = inventoryContentTable.selectAllAsync().join();
    }

    // endregion

    // region Public methods

    @Override
    public CompletableFuture<InventoryContent> select(Predicate<InventoryContent> filter) {
        return inventoryContentTable.selectAsync(filter);
    }

    @Override
    public CompletableFuture<ObservableList<InventoryContent>> selectAll() {
        return inventoryContentTable.selectAllAsync();
    }

    @Override
    public CompletableFuture<InventoryContent> insertContent(InventoryContent inventoryContent) {
        return inventoryContentTable.insertAsync(inventoryContent);
    }

    @Override
    public CompletableFuture<InventoryContent> updateContent(InventoryContent inventoryContent) {
        return inventoryContentTable.updateAsync(inventoryContent);
    }

    @Override
    public CompletableFuture<InventoryContent> deleteContent(InventoryContent inventoryContent) {
        return inventoryContentTable.deleteAsync(inventoryContent);
    }

    @Override
    public int getItemSlotIndex(Predicate<InventoryContent> filter) throws InventoryException {
        Optional<InventoryContent> result = inventoryContent.stream()
            .filter(filter)
            .findFirst();
        if (!result.isPresent()) {
            throw new InventoryException("Item not found");
        }

        return result.get().getSlotId();
    }

    @Override
    public CompletableFuture<Map<Integer, Integer>> getFreeSlotAsync(ItemBase item, int ammount) {
        return CompletableFuture.supplyAsync(() -> {
            final Map<Integer, Integer> mapSlots = new HashMap<>();
            final int stackSize = item.getStackSize();
            final Map<Integer, Integer> occupiedItemMap = new HashMap<>();

            // Aktualizuji okupované sloty
            inventoryContent.forEach(inventoryContent -> occupiedSlots[inventoryContent.getSlotId()] = SLOT_OCCUPIED);
            // Projdu všechny sloty a uložím si takové, které obsahují vkládaný předmět a mají místo
            for (InventoryContent slot : inventoryContent) {
                if (slot.getItemId().equals(item.getId())) {
                    if (occupiedItemMap.put(slot.getSlotId(), stackSize - slot.getAmmount()) != null) {
                        throw new IllegalStateException("Duplicate key");
                    }
                }
            }

            int remaining = ammount;
            // Vložím do již obsazených slotů předmět
            for (Entry<Integer, Integer> entry : occupiedItemMap.entrySet()) {
                final int slotId = entry.getKey();
                final int freeSpace = entry.getValue();
                final int insertAmmount = Math.min(remaining, freeSpace);
                mapSlots.put(slotId, insertAmmount);
                remaining -= insertAmmount;
                if (remaining <= 0) {
                    break;
                }
            }

            // Zjistím, zda-li mám v inventáři místo pro vložení zbytku předmětů
            if (remaining - stackSize * (inventory.getCapacity() - inventoryContent.size()) > 0) {
                throw new RuntimeException("V inventáři není dostatek místa.");
            }

            // Mám místo, takže budu vkládat lineárně na volná místa předměty o maximální velikosti stacku
            final int capacity = inventory.getCapacity();
            int index = 0;
            while (remaining > 0) {
                if (occupiedSlots[index] != SLOT_NOT_OCCUPIED) {
                    index++;
                    continue;
                }

                final int insertAmmount = Math.min(remaining, stackSize);
                mapSlots.put(index, insertAmmount);
                remaining -= insertAmmount;
                occupiedSlots[index] = SLOT_OCCUPIED;
                index++;
                assert index != capacity;
            }

            return mapSlots;
        }, ThreadPool.COMMON_EXECUTOR);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    // endregion

}
