package cz.stechy.drd.db.table.invnetory;

import static cz.stechy.drd.R.Database.Inventory.COLUMN_HERO_ID;
import static cz.stechy.drd.R.Database.Inventory.COLUMN_ID;
import static cz.stechy.drd.R.Database.Inventory.TABLE_NAME;
import static cz.stechy.drd.db.table.invnetory.InventoryTableDefinitions.COLUMNS_KEYS;
import static cz.stechy.drd.db.table.invnetory.InventoryTableDefinitions.COLUMNS_UPDATE;
import static cz.stechy.drd.db.table.invnetory.InventoryTableDefinitions.COLUMNS_VALUES;
import static cz.stechy.drd.db.table.invnetory.InventoryTableDefinitions.QUERY_CREATE;

import cz.stechy.drd.db.BaseOfflineTable;
import cz.stechy.drd.db.base.BaseTableDefinitions;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.model.inventory.Inventory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class InventoryOfflineTable extends BaseOfflineTable<Inventory> {

    // region Constructors

    public InventoryOfflineTable(BaseTableDefinitions<Inventory> tableDefinitions, Database db) {
        super(tableDefinitions, db);
    }

    // endregion

    // region Public methods

    @Override
    protected Inventory parseResultSet(ResultSet resultSet) throws SQLException {
        return tableDefinitions.parseResultSet(resultSet);
    }

    @Override
    protected List<Object> toParamList(Inventory inventory) {
        return tableDefinitions.toParamList(inventory);
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
        return String.format("SELECT * FROM %s WHERE %s=?", getTable(), COLUMN_HERO_ID);
    }


    // endregion

}
