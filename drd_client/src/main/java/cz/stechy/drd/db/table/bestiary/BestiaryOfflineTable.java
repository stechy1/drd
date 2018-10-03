package cz.stechy.drd.db.table.bestiary;

import static cz.stechy.drd.R.Database.Bestiary.COLUMN_ID;
import static cz.stechy.drd.R.Database.Bestiary.TABLE_NAME;
import static cz.stechy.drd.db.table.bestiary.BestiaryTableDefinitions.COLUMNS_KEYS;
import static cz.stechy.drd.db.table.bestiary.BestiaryTableDefinitions.COLUMNS_UPDATE;
import static cz.stechy.drd.db.table.bestiary.BestiaryTableDefinitions.COLUMNS_VALUES;
import static cz.stechy.drd.db.table.bestiary.BestiaryTableDefinitions.QUERY_CREATE;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.BaseOfflineTable;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.model.entity.mob.Mob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Table(clazz = Mob.class, type = Type.OFFLINE)
public class BestiaryOfflineTable extends BaseOfflineTable<Mob> {

    // region Constructors

    @Inject
    public BestiaryOfflineTable(ITableDefinitionsFactory tableDefinitionsFactory, Database db) {
        super(tableDefinitionsFactory.getTableDefinitions(Mob.class), db);
    }

    // endregion

    // region Private methods

    @Override
    protected Mob parseResultSet(ResultSet resultSet) throws SQLException {
        return tableDefinitions.parseResultSet(resultSet);
    }

    @Override
    protected List<Object> toParamList(Mob mob) {
        return tableDefinitions.toParamList(mob);
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
