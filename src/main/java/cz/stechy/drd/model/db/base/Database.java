package cz.stechy.drd.model.db.base;

import java.sql.SQLException;

/**
 * Rozhraní pro komunikaci s databází
 */
public interface Database extends AsyncDatabase {

    /**
     * Zpracuje a provede zadaný příkaz.
     *
     * @param query Příkaz, kdyrý se má provést
     * @param params Parametry příkazu
     * @return Počet ovlivněných řádek
     * @throws SQLException Pokud nastane chyba při vykonání příkazu
     */
    long query(String query, Object... params) throws SQLException;

    /**
     * Provede příkaz selectAsync.
     *
     * @param handler {@link OnRowHandler}
     * @param query Dotaz pro výběr položek
     * @param params Parametry dotazu
     * @throws SQLException Pokud nastane chyba při vykonání dotazu
     */
    void select(OnRowHandler handler, String query, Object... params) throws SQLException;

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
