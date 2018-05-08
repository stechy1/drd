package cz.stechy.drd.db.base;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Rozhraní pro komunikaci s databází
 */
public interface Database {

    /**
     * Provede příkaz selectAsync.
     *
     * @param handler {@link RowTransformHandler} Transformační handler
     * @param query Dotaz pro výběr položek
     * @param params Parametry dotazu
     * @return {@link CompletableFuture<List>}
     */
    <T> CompletableFuture<List<T>> selectAsync(RowTransformHandler<T> handler, String query,
        Object... params);

    /**
     * Zpracuje a provede zadaný příkaz.
     *
     * @param query Příkaz, kdyrý se má provést
     * @param params Parametry příkazu
     * @return {@link CompletableFuture<Long>} Počet ovlivněných řádek
     */
    CompletableFuture<Long> queryAsync(String query, Object... params);

    /**
     * Započne novou transakci.
     *
     * @throws SQLException Pokud se nepodaří začít novou transakci
     */
    void beginTransaction() throws SQLException;

    /**
     * Potvrdí veškeré změny transakce.
     *
     * @throws SQLException Pokud se nepodaří potvrdit rozpracovanou transakci
     */
    void commit() throws SQLException;

    /**
     * Vrátí všechny transakce do původního stavu.
     *
     * @throws SQLException Pokud se rollback nepodaří úspěšně vykonat
     */
    void rollback() throws SQLException;

    /**
     * Zjistí, zda-li byla započata nová transakce.
     *
     * @return True, pokud byla započata nová transakce, jinak false
     */
    boolean isTransactional();

    /**
     * Přidá handler, který bude reagovat na commit.
     *
     * @param handler {@link TransactionHandler}
     */
    void addCommitHandler(TransactionHandler handler);

    /**
     * Získá verzi databáze
     *
     * @return Číslo verze databáze
     */
    int getVersion();
}
