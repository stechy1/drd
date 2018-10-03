package cz.stechy.drd.db.table.inventory_content;

import static cz.stechy.drd.R.Database.Inventorycontent.COLUMN_ID;
import static cz.stechy.drd.R.Database.Inventorycontent.COLUMN_INVENTORY_ID;
import static cz.stechy.drd.R.Database.Inventorycontent.TABLE_NAME;
import static cz.stechy.drd.db.table.inventory_content.InventoryContentTableDefinitions.COLUMNS_KEYS;
import static cz.stechy.drd.db.table.inventory_content.InventoryContentTableDefinitions.COLUMNS_UPDATE;
import static cz.stechy.drd.db.table.inventory_content.InventoryContentTableDefinitions.COLUMNS_VALUES;
import static cz.stechy.drd.db.table.inventory_content.InventoryContentTableDefinitions.QUERY_CREATE;

import cz.stechy.drd.db.BaseOfflineTable;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.model.inventory.InventoryContent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class InventoryContentOfflineTable extends BaseOfflineTable<InventoryContent> {

    // region Constructors

    public InventoryContentOfflineTable(ITableDefinitionsFactory tableDefinitionsFactory, Database db) {
        super(tableDefinitionsFactory.getTableDefinitions(InventoryContent.class), db);
    }

    // endregion

    // region Public methods

    @Override
    protected InventoryContent parseResultSet(ResultSet resultSet) throws SQLException {
        return tableDefinitions.parseResultSet(resultSet);
    }

    @Override
    protected List<Object> toParamList(InventoryContent content) {
        return tableDefinitions.toParamList(content);
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

    @Override
    protected String getQuerySelectAll() {
        return String.format("SELECT * FROM %s WHERE %s=?", getTable(), COLUMN_INVENTORY_ID);
    }

//    @Override
//    protected Object[] getParamsForSelectAll() {
//        return new Object[]{inventory.getId()};
//    }

    // endregion

}
