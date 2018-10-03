package cz.stechy.drd.db.table.general_item;

import static cz.stechy.drd.R.Database.Generalitems.COLUMN_ID;
import static cz.stechy.drd.R.Database.Generalitems.TABLE_NAME;
import static cz.stechy.drd.db.table.general_item.GeneralItemTableDefinitions.COLUMNS_KEYS;
import static cz.stechy.drd.db.table.general_item.GeneralItemTableDefinitions.COLUMNS_UPDATE;
import static cz.stechy.drd.db.table.general_item.GeneralItemTableDefinitions.COLUMNS_VALUES;
import static cz.stechy.drd.db.table.general_item.GeneralItemTableDefinitions.QUERY_CREATE;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.BaseOfflineTable;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.model.item.GeneralItem;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Table(clazz = GeneralItem.class, type = Type.OFFLINE)
public class GeneralItemOfflineTable extends BaseOfflineTable<GeneralItem> {

    // region Constructors

    @Inject
    public GeneralItemOfflineTable(ITableDefinitionsFactory tableDefinitionsFactory, Database db) {
        super(tableDefinitionsFactory.getTableDefinitions(GeneralItem.class), db);
    }

    // endregion

    // region Public methods

    @Override
    protected GeneralItem parseResultSet(ResultSet resultSet) throws SQLException {
        return tableDefinitions.parseResultSet(resultSet);
    }

    @Override
    protected List<Object> toParamList(GeneralItem item) {
        return tableDefinitions.toParamList(item);
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
