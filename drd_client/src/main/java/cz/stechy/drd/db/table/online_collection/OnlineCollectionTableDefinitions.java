package cz.stechy.drd.db.table.online_collection;

import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.base.BaseTableDefinitions;
import cz.stechy.drd.model.item.OnlineCollection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Table(type = Type.DEFINITION, clazz = OnlineCollection.class)
public class OnlineCollectionTableDefinitions extends BaseTableDefinitions<OnlineCollection> {

    // region Public methods

    @Override
    protected String getColumnKeys() {
        return null;
    }

    @Override
    public OnlineCollection parseResultSet(ResultSet resultSet) throws SQLException {
        return null;
    }

    @Override
    public List<Object> toParamList(OnlineCollection item) {
        return null;
    }

    @Override
    public OnlineCollection fromStringMap(Map<String, Object> map) {
        return null;
    }

    // endregion

}
