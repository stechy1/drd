package cz.stechy.drd.model.db.base;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowTransformHandler<T> {

    T transofrm(ResultSet resultSet) throws SQLException;

}
