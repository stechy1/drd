package cz.stechy.drd.db.base;

import cz.stechy.drd.model.DiffEntry;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

public interface OfflineOnlineTableWrapper<T extends OnlineRecord> extends OfflineTable<T>, OnlineTable<T> {

    /**
     * Vrátí pozorovatelnou kolekci s offline/online záznamy
     *
     * @return {@link ObservableList<T>} Pozorovatelnou kolekci se záznamy
     */
    ObservableList<T> getUsed();

    /**
     * Přepne databázi podle parametru
     *
     * @param showOnline True, pokud se má zobrazit online databáze, jinak offline databáze
     */
    void toggleDatabase(boolean showOnline);

    /**
     * Stáhne všechny online předměty do offline databáze
     *
     * @param author Autor předmětů, které chci stáhnout
     */
    CompletableFuture<Integer> synchronize(String author);

    /**
     * Uloží všechny záznamy z předané kolekce, které ještě uloženy nejsou
     *
     * @param records Kolekce předmětů, která se má uložit
     * @return {@link CompletableFuture} Počet uložených záznamů
     */
    CompletableFuture<Integer> saveAll(Collection records);

    /**
     * Vytvoří kolekci všech záznamů, které se liší ve svých vlastnostech v porovníná offline a online záznamů
     *
     * @return {@link CompletableFuture<ObservableSet>>} Budoucnost s kolekcí všech záznamů, které se liší ve svých vlastnostech
     */
    CompletableFuture<Set<DiffEntry<T>>> getDiff();
}
