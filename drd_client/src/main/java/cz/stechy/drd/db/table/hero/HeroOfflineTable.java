package cz.stechy.drd.db.table.hero;

import static cz.stechy.drd.R.Database.Hero.COLUMN_ID;
import static cz.stechy.drd.R.Database.Hero.TABLE_NAME;
import static cz.stechy.drd.db.table.hero.HeroTableDefinitions.COLUMNS_KEYS;
import static cz.stechy.drd.db.table.hero.HeroTableDefinitions.COLUMNS_UPDATE;
import static cz.stechy.drd.db.table.hero.HeroTableDefinitions.COLUMNS_VALUES;
import static cz.stechy.drd.db.table.hero.HeroTableDefinitions.QUERY_CREATE;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.BaseOfflineTable;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.model.entity.hero.Hero;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Table(clazz = Hero.class, type = Type.OFFLINE)
public class HeroOfflineTable extends BaseOfflineTable<Hero> {

    // region Constructors

    @Inject
    public HeroOfflineTable(ITableDefinitionsFactory tableDefinitionsFactory, Database db) {
        super(tableDefinitionsFactory.getTableDefinitions(Hero.class), db);
    }

    // endregion

    // region Public methods

    @Override
    protected Hero parseResultSet(ResultSet resultSet) throws SQLException {
        return tableDefinitions.parseResultSet(resultSet);
    }

    @Override
    protected List<Object> toParamList(Hero hero) {
        return tableDefinitions.toParamList(hero);
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
