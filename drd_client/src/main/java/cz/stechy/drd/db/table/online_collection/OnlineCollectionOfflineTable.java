package cz.stechy.drd.db.table.online_collection;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.BaseOfflineTable;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.model.item.OnlineCollection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Table(clazz = OnlineCollection.class, type = Type.OFFLINE)
public class OnlineCollectionOfflineTable extends BaseOfflineTable<OnlineCollection> {

    // region Constructors

    @Inject
    public OnlineCollectionOfflineTable(ITableDefinitionsFactory tableDefinitionsFactory, Database db) {
        super(tableDefinitionsFactory.getTableDefinitions(OnlineCollection.class), db);
    }

    // endregion

    // region Public methods

    @Override
    protected OnlineCollection parseResultSet(ResultSet resultSet) throws SQLException {
        return null;
    }

    @Override
    protected List<Object> toParamList(OnlineCollection item) {
        return null;
    }

    @Override
    protected String getTable() {
        return null;
    }

    @Override
    protected String getColumnWithId() {
        return null;
    }

    @Override
    protected String getColumnsKeys() {
        return null;
    }

    @Override
    protected String getColumnValues() {
        return null;
    }

    @Override
    protected String getColumnsUpdate() {
        return null;
    }

    @Override
    protected String getInitializationQuery() {
        return null;
    }

    // endregion

}
