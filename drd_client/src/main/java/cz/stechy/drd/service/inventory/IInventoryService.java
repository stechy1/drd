package cz.stechy.drd.service.inventory;

import cz.stechy.drd.model.inventory.Inventory;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public interface IInventoryService {

    default Optional<Inventory> getInventory(String id) {
        return getInventory(inventory -> Objects.equals(inventory.getId(), id));
    }

    Optional<Inventory> getInventory(Predicate<Inventory> filter);

//    CompletableFuture<Inventory> insertInventory(Inventory inventory);

    CompletableFuture<Inventory> updateInventory(Inventory inventory);

    CompletableFuture<Inventory> deleteInventory(Inventory inventory);

    IInventoryContentService getInventoryContentService(Inventory inventory);

    /**
     * Inicializuje nový inventář
     *
     * @param capacity Kapacita inventáře
     * @return {@link Inventory} instanci inicializovaného inventáře
     */
    CompletableFuture<Inventory> initSubInventoryAsync(final int capacity);

}
