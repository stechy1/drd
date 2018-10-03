package cz.stechy.drd.db.table.armor;

import static cz.stechy.drd.R.Database.Armor.COLUMN_ID;
import static cz.stechy.drd.R.Database.Armor.TABLE_NAME;
import static cz.stechy.drd.db.table.armor.ArmorTableDefinitions.COLUMNS_KEYS;
import static cz.stechy.drd.db.table.armor.ArmorTableDefinitions.COLUMNS_UPDATE;
import static cz.stechy.drd.db.table.armor.ArmorTableDefinitions.COLUMNS_VALUES;
import static cz.stechy.drd.db.table.armor.ArmorTableDefinitions.QUERY_CREATE;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.BaseOfflineTable;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.model.item.Armor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Table(clazz = Armor.class, type = Type.OFFLINE)
public class ArmorOfflineTable extends BaseOfflineTable<Armor> {

    // region Constructors

    @Inject
    public ArmorOfflineTable(ITableDefinitionsFactory tableDefinitionsFactory, Database db) {
        super(tableDefinitionsFactory.getTableDefinitions(Armor.class), db);
    }

    // endregion

    // region Public methods

    @Override
    protected Armor parseResultSet(ResultSet resultSet) throws SQLException {
        return tableDefinitions.parseResultSet(resultSet);
    }

    @Override
    protected List<Object> toParamList(Armor armor) {
        return tableDefinitions.toParamList(armor);
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
