package cz.stechy.drd.service.item;

import cz.stechy.drd.db.BaseOfflineTable;
import cz.stechy.drd.db.OfflineOnlineTableWrapper;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.ItemType;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javafx.collections.ObservableMap;

public interface IItemRegistry {

    /**
     * Získá item z registrů podle Id
     *
     * @param id Id itemu
     * @return {@link ItemBase}
     */
    Optional<ItemBase> getItemById(String id);

    /**
     * Zaregistruje poskytovatele předmětů
     *
     * @param itemProvider {@link BaseOfflineTable} Služba poskytující předměty
     * @param itemType {@link ItemType} Typ předmětů, které služba poskytuje
     */
    void registerItemProvider(OfflineOnlineTableWrapper<? extends ItemBase> itemProvider, ItemType itemType);

    /**
     * Uloží dosud neuložené předměty do offline databáze
     *
     * @param items {@link Collection} Kolekce předmětů, které se mají uložit
     * @return {@link CompletableFuture} Počet uložených předmětů
     */
    CompletableFuture<Integer> merge(Collection<ItemBase> items);

    /**
     * Vrátí nemodifikovatelnou kolekci všech předmětů
     *
     * @return {@link ObservableMap<String,   ItemBase  >} Kolekci všech předmětů
     */
    ObservableMap<String, ItemBase> getRegistry();
}
