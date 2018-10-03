package cz.stechy.drd.db.base;

import cz.stechy.drd.db.BaseOfflineTable;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javafx.collections.ObservableList;

/**
 * Rozhraní pro všechny správce komunikující s databází
 */
public interface OfflineTable<T> {

    /**
     * Metoda pro vytvoření nové tabulky v databázi
     */
    CompletableFuture<Void> createTableAsync();

    /**
     * Provede výběr konkrétního předmětu podle zadaného filtru
     *
     * @param filter Filtr pro výběr konkrétního předmětu
     * @return {@link Optional<T>}
     */
    Optional<T> selectAsync(Predicate<? super T> filter);

    /**
     * Provede výběr všech záznamů a vrátí je jako pozorovatelnou kolekci
     *
     * @return {@link CompletableFuture<ObservableList>} Pozorovatelnou kolekci všech záznamů
     */
    CompletableFuture<ObservableList<T>> selectAllAsync();

    /**
     * Provede výběr všech záznamů odpovídajících zadaným hodnotám parametrů
     * Dotaz na select musí podporovat parametry
     * Implementace konkrétní tabulky musí přepsat metodu {@link BaseOfflineTable#getQuerySelectAll()}
     *
     * @param params Object... Parametry výběru záznamů
     * @return {@link CompletableFuture<ObservableList>} Pozorovatelnou kolekci všech záznamů
     */
    CompletableFuture<ObservableList<T>> selectAllAsync(Object... params);

    /**
     * Vloží předmět do databáze
     *
     * @param item {@link T}
     * @return {@link CompletableFuture<T>} Vložený předmět do databáze
     */
    CompletableFuture<T> insertAsync(T item);

    /**
     * Aktualizuje předmět v databázi
     *
     * @param item {@link T}
     * @return {@link CompletableFuture<T>} Aktualizovaný předmět
     */
    CompletableFuture<T> updateAsync(T item);

    /**
     * Smaže vybraný předmět z databáze
     *
     * @param item Předmět, který má být smazán z databáze
     * @return {@link CompletableFuture<T>} Smazaný předmět
     */
    CompletableFuture<T> deleteAsync(T item);

    /**
     * Metoda se zavolá, pokud je potřeba upgradovat databázi
     *
     * @param newVersion Nová verze databáze
     */
    void onUpgrade(int newVersion);

    CompletableFuture<Void> onUpgradeAsync(int newVersion);

    /**
     * Vyčistí cache tabulky
     */
    void clearCache();
}
