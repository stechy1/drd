package cz.stechy.drd.db.table.backpack;

import static cz.stechy.drd.R.Database.Backpack.COLUMN_ID;
import static cz.stechy.drd.R.Database.Backpack.TABLE_NAME;
import static cz.stechy.drd.db.table.backpack.BackpackTableDefinitions.COLUMNS_KEYS;
import static cz.stechy.drd.db.table.backpack.BackpackTableDefinitions.COLUMNS_UPDATE;
import static cz.stechy.drd.db.table.backpack.BackpackTableDefinitions.COLUMNS_VALUES;
import static cz.stechy.drd.db.table.backpack.BackpackTableDefinitions.QUERY_CREATE;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.BaseOfflineTable;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.model.item.Backpack;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Table(type = Type.OFFLINE, clazz = Backpack.class)
public class BackpackOfflineTable extends BaseOfflineTable<Backpack> {

    // region Constructors

    @Inject
    public BackpackOfflineTable(ITableDefinitionsFactory tableDefinitionsFactory, Database db) {
        super(tableDefinitionsFactory.getTableDefinitions(Backpack.class), db);
    }

    // endregion

    // region Public methods

    @Override
    protected Backpack parseResultSet(ResultSet resultSet) throws SQLException {
        return tableDefinitions.parseResultSet(resultSet);
    }

    @Override
    protected List<Object> toParamList(Backpack backpack) {
        return tableDefinitions.toParamList(backpack);
    }

    @Override
    protected String getTable() {
        return TABLE_NAME;
    }

    @Override
    protected String getColumnWithId() {
        return COLUMN_ID;
    }

    @Override
    protected String getColumnsKeys() {
        return COLUMNS_KEYS;
    }

    @Override
    protected String getColumnValues() {
        return COLUMNS_VALUES;
    }

    @Override
    protected String getColumnsUpdate() {
        return COLUMNS_UPDATE;
    }

    @Override
    protected String getInitializationQuery() {
        return QUERY_CREATE;
    }

    // endregion

}
