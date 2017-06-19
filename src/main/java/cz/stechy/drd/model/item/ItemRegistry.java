package cz.stechy.drd.model.item;

import cz.stechy.drd.model.db.base.DatabaseItem;
import java.util.Optional;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Registr všech offline itemů
 */
public final class ItemRegistry {

    // region Variables

    private static ItemRegistry INSTANCE;

    private final ObservableList<DatabaseItem> registry = FXCollections.observableArrayList();

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
                this.registry.addAll(c.getAddedSubList());
                this.registry.removeAll(c.getRemoved());
            }
        });
        this.registry.addAll(items);
    }

    /**
     * Získá item z registrů podle Id
     *
     * @param id Id itemu
     * @return {@link ItemBase}
     */
    public Optional<ItemBase> getItemById(String id) {
        return getItem(databaseItem -> id.equals(databaseItem.getId()));
    }

    /**
     * Získá item z registrů
     *
     * @param filter Filter
     * @return {@link ItemBase}
     */
    public Optional<ItemBase> getItem(Predicate<DatabaseItem> filter) {
        final Optional<DatabaseItem> optional = registry.stream()
            .filter(filter)
            .findFirst();

        return optional.map(databaseItem -> (ItemBase) databaseItem);
    }

    public ObservableList<DatabaseItem> getRegistry() {
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
