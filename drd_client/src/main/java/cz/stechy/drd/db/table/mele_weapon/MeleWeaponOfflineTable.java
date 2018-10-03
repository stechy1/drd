package cz.stechy.drd.db.table.mele_weapon;

import static cz.stechy.drd.R.Database.Weaponmele.COLUMN_ID;
import static cz.stechy.drd.R.Database.Weaponmele.TABLE_NAME;
import static cz.stechy.drd.db.table.mele_weapon.MeleWeaponTableDefinitions.COLUMNS_KEYS;
import static cz.stechy.drd.db.table.mele_weapon.MeleWeaponTableDefinitions.COLUMNS_UPDATE;
import static cz.stechy.drd.db.table.mele_weapon.MeleWeaponTableDefinitions.COLUMNS_VALUES;
import static cz.stechy.drd.db.table.mele_weapon.MeleWeaponTableDefinitions.QUERY_CREATE;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.BaseOfflineTable;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.model.item.MeleWeapon;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Table(clazz = MeleWeapon.class, type = Type.OFFLINE)
public class MeleWeaponOfflineTable extends BaseOfflineTable<MeleWeapon> {

    // region Constructors

    @Inject
    public MeleWeaponOfflineTable(ITableDefinitionsFactory tableDefinitionsFactory, Database db) {
        super(tableDefinitionsFactory.getTableDefinitions(MeleWeapon.class), db);
    }

    // endregion

    // region Public methods

    @Override
    protected MeleWeapon parseResultSet(ResultSet resultSet) throws SQLException {
        return tableDefinitions.parseResultSet(resultSet);
    }

    @Override
    protected List<Object> toParamList(MeleWeapon weapon) {
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
