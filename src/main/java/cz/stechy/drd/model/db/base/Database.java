package cz.stechy.drd.model.db.base;

import java.sql.SQLException;

/**
 * Rozhraní pro komunikaci s databází
 */
public interface Database {

    /**
     * Zpracuje a provede zadaný příkaz
     *
     * @param query Příkaz, kdyrý se má provést
     * @param params Parametry příkazu
     * @return Počet ovlivněných řádek
     */
    long query(String query, Object... params) throws SQLException;

    /**
     * Provede příkaz select
     *
     * @param query Dotaz pro výběr položek
     * @param params Parametry dotazu
     */
    void select(OnRowHandler handler, String query, Object... params) throws SQLException;

    /**
     * Započne novou transakci.
     */
    void beginTransaction() throws SQLException;

    /**
     * Potvrdí veškeré změny transakce.
     */
    void commit() throws SQLException;

    /**
     * Vrátí všechny transakce do původního stavu.
     */
    void rollback() throws SQLException;

    /**
     * Zjistí, zda-li byla započata nová transakce
     *
     * @return True, pokud byla započata nová transakce, jinak false
     */
    boolean isTransactional();

    void addCommitHandler(CommitHandler handler);
}
