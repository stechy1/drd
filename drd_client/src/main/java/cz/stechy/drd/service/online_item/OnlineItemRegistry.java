package cz.stechy.drd.service.online_item;

import cz.stechy.drd.db.base.Row;
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
public final class OnlineItemRegistry {

    // region Variables

    private static OnlineItemRegistry INSTANCE;

    private final ObservableMap<String, Row> registry = FXCollections.observableHashMap();

    // endregion

    // region Constructors

    /**
     * Privátní konstruktor k zamezení vytvoření instance
     */
    private OnlineItemRegistry() {
    }

    // endregion

    // region Public static methods

    public static OnlineItemRegistry getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new OnlineItemRegistry();
        }

        return INSTANCE;
    }

    // endregion

    // region Public methods

    /**
     * Přidá kolekci do registru
     *
     * @param items Kolekce, která se má přidat
     */
    public void addColection(ObservableList<? extends Row> items) {
        items.addListener((ListChangeListener<Row>) c -> {
            while (c.next()) {
                this.registry.putAll(
                    c.getAddedSubList()
                        .stream()
                        .collect(Collectors
                            .toMap(Row::getId, databaseItem -> databaseItem)));
                c.getRemoved().forEach(o -> this.registry.remove(o.getId()));
            }
        });
        this.registry.putAll(
            items.stream()
                .collect(Collectors
                    .toMap(Row::getId, databaseItem -> databaseItem)));
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

    public ObservableMap<String, Row> getRegistry() {
        return registry;
    }

    // endregion

}
