package cz.stechy.drd.db;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javafx.collections.ObservableList;

/**
 * Rozhraní pro všechny správce komunikující s databází
 */
public interface DatabaseService<T> {

    /**
     * Metoda pro vytvoření nové tabulky v databázi
     */
    CompletableFuture<Void> createTableAsync();

    /**
     * Provede výběr konkrétního předmětu podle zadaného filtru
     *
     * @param filter Filtr pro výběr konkrétního předmětu
     * @return {@link CompletableFuture<T>}
     */
    CompletableFuture<T> selectAsync(Predicate<? super T> filter);

    /**
     * Provede výběr všech předmětů a vrátí ho jako pozorovatelnou kolekci
     *
     * @return {@link CompletableFuture<ObservableList>} Pozorovatelnou kolekci všech předmětů
     */
    CompletableFuture<ObservableList<T>> selectAllAsync();

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
     * Započne novou transakci, během které lze provést více operací
     *
     * @throws DatabaseException Pokud se nepodaří začít novou transakci
     */
    void beginTransaction() throws DatabaseException;

    /**
     * Potvrdí změny v transakci
     *
     * @throws DatabaseException Pokud se nezdaří potvrdit transakci
     */
    void commit() throws DatabaseException;

    /**
     * Zruší změny, které byly provedeny během transakce
     *
     * @throws DatabaseException Pokud se nezdaří zrušit změny
     */
    void rollback() throws DatabaseException;

    /**
     * Metoda se zavolá, pokud je potřeba upgradovat databázi
     *
     * @param newVersion Nová verze databáze
     * @throws DatabaseException Pokud se upgrade nezdaří
     */
    void onUpgrade(int newVersion) throws DatabaseException;

    CompletableFuture<Void> onUpgradeAsync(int newVersion);
}
