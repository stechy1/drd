package cz.stechy.drd.model.db.base;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Transformační rozhraní pro přeměnu {@link ResultSet} na objekt typu {@link T}
 *
 * @param <T> Typ objektu, na který se bude {@link ResultSet} parsovat
 */
@FunctionalInterface
public interface RowTransformHandler<T> {

    /**
     * Transformuje {@link ResultSet} na objekt {@link T}
     *
     * @param resultSet {@link ResultSet}
     * @return {@link T} Zkonstruovaný objekt
     * @throws SQLException Pokud je {@link ResultSet} špatně zpracován
     */
    T transofrm(ResultSet resultSet) throws SQLException;

}
