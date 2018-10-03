package cz.stechy.drd.service.item;

import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.annotation.Service;
import cz.stechy.drd.db.OfflineOnlineTableWrapper;
import cz.stechy.drd.db.base.Row;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.ItemType;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * Registr všech offline itemů
 */
@Service
public final class ItemRegistry implements IItemRegistry {

    // region Variables

    private final ObservableMap<String, ItemBase> registry = FXCollections.observableHashMap();
    private final Map<ItemType, OfflineOnlineTableWrapper<? extends ItemBase>> itemProviders = new HashMap<>();

    // endregion

    // region Private methods

    /**
     * Přidá kolekci do registru
     *
     * @param items Kolekce, která se má přidat
     */
    private void addColection(ObservableList<? extends Row> items) {
        items.addListener((ListChangeListener<Row>) c -> {
            while (c.next()) {
                this.registry.putAll(
                    c.getAddedSubList()
                        .stream()
                        .map(o -> (ItemBase) o)
                        .collect(Collectors
                            .toMap(Row::getId, databaseItem -> databaseItem)));
                c.getRemoved().forEach(o -> this.registry.remove(o.getId()));
            }
        });
        this.registry.putAll(
            items.stream()
                .map(o -> (ItemBase) o)
                .collect(Collectors
                    .toMap(Row::getId, databaseItem -> databaseItem)));
    }

    // endregion

    // region Public methods


    @Override
    public Optional<ItemBase> getItemById(String id) {
        return Optional.ofNullable(registry.get(id));
    }


    @Override
    public void registerItemProvider(OfflineOnlineTableWrapper<? extends ItemBase> itemProvider, ItemType itemType) {
        itemProviders.put(itemType, itemProvider);
        itemProvider.selectAllAsync().thenAccept(this::addColection);
    }

    @Override
    public CompletableFuture<Integer> merge(Collection<ItemBase> items) {
        return CompletableFuture.supplyAsync(() -> {
            int totalMerged = 0;
            for (Entry<ItemType, OfflineOnlineTableWrapper<? extends ItemBase>> serviceEntry : itemProviders
                .entrySet()) {
                final ItemType itemType = serviceEntry.getKey();
                final OfflineOnlineTableWrapper<? extends ItemBase> service = serviceEntry.getValue();
                // Vyfiltruji si pouze ty předměty, které odpovídají danému typu
                final List<ItemBase> filteredItems = items.parallelStream()
                    .filter(itemBase -> itemBase.getItemType() == itemType)
                    .collect(Collectors.toList());
                final Integer mergedEntries = service.saveAll(filteredItems).join();
                totalMerged += mergedEntries;
            }
            return totalMerged;
        }, ThreadPool.COMMON_EXECUTOR);
    }

    @Override
    public ObservableMap<String, ItemBase> getRegistry() {
        return FXCollections.unmodifiableObservableMap(registry);
    }

    // endregion

}
