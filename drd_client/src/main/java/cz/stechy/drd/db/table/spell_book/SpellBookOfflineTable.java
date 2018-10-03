package cz.stechy.drd.db.table.spell_book;

import static cz.stechy.drd.R.Database.Spells.COLUMN_ID;
import static cz.stechy.drd.R.Database.Spells.TABLE_NAME;
import static cz.stechy.drd.db.table.spell_book.SpellBookTableDefinitions.COLUMNS_KEYS;
import static cz.stechy.drd.db.table.spell_book.SpellBookTableDefinitions.COLUMNS_UPDATE;
import static cz.stechy.drd.db.table.spell_book.SpellBookTableDefinitions.COLUMNS_VALUES;
import static cz.stechy.drd.db.table.spell_book.SpellBookTableDefinitions.QUERY_CREATE;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.BaseOfflineTable;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.model.spell.Spell;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Table(clazz = Spell.class, type = Type.OFFLINE)
public class SpellBookOfflineTable extends BaseOfflineTable<Spell> {

    // region Constructors

    /**
     * Vytvoří nového správce
     *
     * @param tableDefinitionsFactory {@link ITableDefinitionsFactory}
     * @param db {@link Database}
     */
    @Inject
    protected SpellBookOfflineTable(ITableDefinitionsFactory tableDefinitionsFactory, Database db) {
        super(tableDefinitionsFactory.getTableDefinitions(Spell.class), db);
    }

    // endregion

    // region Public methods

    @Override
    protected Spell parseResultSet(ResultSet resultSet) throws SQLException {
        return tableDefinitions.parseResultSet(resultSet);
    }

    @Override
    protected List<Object> toParamList(Spell spell) {
        return tableDefinitions.toParamList(spell);
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
