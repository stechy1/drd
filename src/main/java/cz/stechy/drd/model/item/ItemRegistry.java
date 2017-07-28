package cz.stechy.drd.model.item;

import cz.stechy.drd.model.db.base.DatabaseItem;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * Registr všech offline itemů
 */
public final class ItemRegistry {

    // region Variables

    private static ItemRegistry INSTANCE;

    private final ObservableMap<String, DatabaseItem> registry = FXCollections.observableHashMap();

    // endregion

    // region Constructors

    private ItemRegistry() {
    }

    // endregion

    // region Public static methods

    public static ItemRegistry getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new ItemRegistry();
        }

        return INSTANCE;
    }

    // endregion

    // region Public methods

    /**
     * Pridá kolekci do registru
     *
     * @param items Kolekce, která se má přidat
     */
    public void addColection(ObservableList<? extends DatabaseItem> items) {
        items.addListener((ListChangeListener<DatabaseItem>) c -> {
            while (c.next()) {
                this.registry.putAll(
                    c.getAddedSubList()
                        .stream()
                        .collect(Collectors
                            .toMap(DatabaseItem::getId, databaseItem -> databaseItem)));
                c.getRemoved().forEach(o -> this.registry.remove(o.getId()));
            }
        });
        this.registry.putAll(
            items.stream()
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
        return Optional.ofNullable((ItemBase) registry.get(id));
    }

    public ObservableMap<String, DatabaseItem> getRegistry() {
        return registry;
    }

    // endregion

    public static class ItemException extends Exception {

        public ItemException() {
        }

        public ItemException(String message) {
            super(message);
        }

        public ItemException(String message, Throwable cause) {
            super(message, cause);
        }

        public ItemException(Throwable cause) {
            super(cause);
        }
    }

}
