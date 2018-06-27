package cz.stechy.drd.service;

import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.db.AdvancedDatabaseService;
import cz.stechy.drd.db.BaseDatabaseService;
import cz.stechy.drd.db.base.DatabaseItem;
import cz.stechy.drd.di.Singleton;
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
@Singleton
public final class ItemRegistry {

    // region Variables

    private final ObservableMap<String, ItemBase> registry = FXCollections.observableHashMap();
    private final Map<ItemType, AdvancedDatabaseService<? extends ItemBase>> itemProviders = new HashMap<>();

    // endregion

    // region Public methods

    /**
     * Přidá kolekci do registru
     *
     * @param items Kolekce, která se má přidat
     */
    private void addColection(ObservableList<? extends DatabaseItem> items) {
        items.addListener((ListChangeListener<DatabaseItem>) c -> {
            while (c.next()) {
                this.registry.putAll(
                    c.getAddedSubList()
                        .stream()
                        .map(o -> (ItemBase) o)
                        .collect(Collectors
                            .toMap(DatabaseItem::getId, databaseItem -> databaseItem)));
                c.getRemoved().forEach(o -> this.registry.remove(o.getId()));
            }
        });
        this.registry.putAll(
            items.stream()
                .map(o -> (ItemBase) o)
                .collect(Collectors
                    .toMap(DatabaseItem::getId, databaseItem -> databaseItem)));
    }

    /**
     * Zaregistruje poskytovatele předmětů
     *
     * @param itemProvider {@link BaseDatabaseService} Služba poskytující předměty
     * @param itemType {@link ItemType} Typ předmětů, které služba poskytuje
     */
    public void registerItemProvider(AdvancedDatabaseService<? extends ItemBase> itemProvider,
        ItemType itemType) {
        itemProviders.put(itemType, itemProvider);
        itemProvider.selectAllAsync().thenAccept(this::addColection);
    }

    /**
     * Získá item z registrů podle Id
     *
     * @param id Id itemu
     * @return {@link ItemBase}
     */
    public Optional<ItemBase> getItemById(String id) {
        return Optional.ofNullable(registry.get(id));
    }

    public ObservableMap<String, ItemBase> getRegistry() {
        return registry;
    }

    /**
     * Uloží dosud neuložené předměty do offline databáze
     *
     * @param items {@link Collection} Kolekce předmětů, které se mají uložit
     * @return {@link CompletableFuture} Počet uložených předmětů
     */
    public CompletableFuture<Integer> merge(Collection<ItemBase> items) {
        return CompletableFuture.supplyAsync(() -> {
            int totalMerged = 0;
            for (Entry<ItemType, AdvancedDatabaseService<? extends ItemBase>> serviceEntry : itemProviders
                .entrySet()) {
                final ItemType itemType = serviceEntry.getKey();
                final AdvancedDatabaseService<? extends ItemBase> service = serviceEntry.getValue();
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

    // endregion

}
