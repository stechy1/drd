package cz.stechy.drd.service.inventory;

import cz.stechy.drd.model.inventory.Inventory;
import cz.stechy.drd.model.inventory.InventoryContent;
import cz.stechy.drd.model.inventory.InventoryException;
import cz.stechy.drd.model.item.ItemBase;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javafx.collections.ObservableList;

public interface IInventoryContentService {

    CompletableFuture<InventoryContent> select(final Predicate<InventoryContent> filter);

    CompletableFuture<ObservableList<InventoryContent>> selectAll();

    CompletableFuture<InventoryContent> insertContent(final InventoryContent inventoryContent);

    CompletableFuture<InventoryContent> updateContent(final InventoryContent inventoryContent);

    CompletableFuture<InventoryContent> deleteContent(final InventoryContent inventoryContent);

    /**
     * Najde slot, ve kterém se nachází hledaný item
     *
     * @param item Item, který se hledá
     * @return Id slotu, ve kterém se nachází item
     * @throws InventoryException Pokud item není přítomný
     */
    default int getItemSlotIndexById(final ItemBase item) throws InventoryException {
        return getItemSlotIndex(record -> item.getId().equals(record.getItemId()));
    }

    /**
     * Najde slot, ve kterém se nachází hledaný item
     *
     * @param filter Filter, podle kterého se vyhledává item
     * @return Id slotu, ve kterém se nachází item
     * @throws InventoryException Pokud item není přítomný
     */
    int getItemSlotIndex(final Predicate<InventoryContent> filter) throws InventoryException;

    /**
     * Najde index volného slotu
     *
     * @param item Předmět, pro který se hledá volná slot
     * @param ammount Množství předmětů které se má vložit do inventáře
     * @return Mapu obsahující ID slotu + množství, které se do slotu vejde
     */
    CompletableFuture<Map<Integer, Integer>> getFreeSlotAsync(ItemBase item, int ammount);

    /**
     * Vrátí inventář, ke kterému se vztahuje tento obsah
     *
     * @return {@link Inventory}
     */
    Inventory getInventory();
}
