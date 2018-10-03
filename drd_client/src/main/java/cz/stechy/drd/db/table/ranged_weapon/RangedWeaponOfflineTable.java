package cz.stechy.drd.db.table.ranged_weapon;

import static cz.stechy.drd.R.Database.Weaponranged.COLUMN_ID;
import static cz.stechy.drd.R.Database.Weaponranged.TABLE_NAME;
import static cz.stechy.drd.db.table.ranged_weapon.RangedWeaponTableDefinitions.COLUMNS_KEYS;
import static cz.stechy.drd.db.table.ranged_weapon.RangedWeaponTableDefinitions.COLUMNS_UPDATE;
import static cz.stechy.drd.db.table.ranged_weapon.RangedWeaponTableDefinitions.COLUMNS_VALUES;
import static cz.stechy.drd.db.table.ranged_weapon.RangedWeaponTableDefinitions.QUERY_CREATE;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.BaseOfflineTable;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.model.item.RangedWeapon;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Table(clazz = RangedWeapon.class, type = Type.OFFLINE)
public class RangedWeaponOfflineTable extends BaseOfflineTable<RangedWeapon> {

    // region Constructors

    @Inject
    public RangedWeaponOfflineTable(ITableDefinitionsFactory tableDefinitionsFactory, Database db) {
        super(tableDefinitionsFactory.getTableDefinitions(RangedWeapon.class), db);
    }

    // endregion

    // region Public methods

    @Override
    protected RangedWeapon parseResultSet(ResultSet resultSet) throws SQLException {
        return tableDefinitions.parseResultSet(resultSet);
    }

    @Override
    protected List<Object> toParamList(RangedWeapon weapon) {
        return tableDefinitions.toParamList(weapon);
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
