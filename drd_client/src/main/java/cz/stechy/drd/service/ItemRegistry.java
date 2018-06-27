package cz.stechy.drd.service;

import cz.stechy.drd.db.base.DatabaseItem;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.model.item.ItemBase;
import java.util.Optional;
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

    // endregion

    // region Constructors

    /**
     * Privátní konstruktor k zabránění vytvoření instance
     */
    public ItemRegistry() {
    }

    // endregion

    // region Public methods

    /**
     * Přidá kolekci do registru
     *
     * @param items Kolekce, která se má přidat
     */
    public void addColection(ObservableList<? extends DatabaseItem> items) {
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

    // endregion

}
