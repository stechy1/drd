package cz.stechy.drd.model.db.base;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Rozhraní definující metodu pro reakci na výsledek selectu z databáze
 */
@FunctionalInterface
public interface OnRowHandler {

    /**
     * Metoda je zavolána pro každý řádek z výsledku selectu z databáze
     *
     * @param resultSet {@link ResultSet}
     */
    void onRow(ResultSet resultSet) throws SQLException;

}
