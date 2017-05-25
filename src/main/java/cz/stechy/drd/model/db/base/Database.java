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
}
