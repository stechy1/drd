package cz.stechy.drd.db.table.invnetory;

import static cz.stechy.drd.R.Database.Inventory.COLUMN_CAPACITY;
import static cz.stechy.drd.R.Database.Inventory.COLUMN_HERO_ID;
import static cz.stechy.drd.R.Database.Inventory.COLUMN_ID;
import static cz.stechy.drd.R.Database.Inventory.COLUMN_INVENTORY_TYPE;
import static cz.stechy.drd.R.Database.Inventory.TABLE_NAME;

import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.base.BaseTableDefinitions;
import cz.stechy.drd.model.inventory.Inventory;
import cz.stechy.drd.model.inventory.InventoryType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Table(clazz = Inventory.class, type = Type.DEFINITION)
public class InventoryTableDefinitions extends BaseTableDefinitions<Inventory> {

    // region Constants

    private static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_HERO_ID,
        COLUMN_INVENTORY_TYPE, COLUMN_CAPACITY};
    static final String COLUMNS_KEYS = GENERATE_COLUMN_KEYS(COLUMNS);
    static final String COLUMNS_VALUES = GENERATE_COLUMNS_VALUES(COLUMNS);
    static final String COLUMNS_UPDATE = GENERATE_COLUMNS_UPDATE(COLUMNS);
    static final String QUERY_CREATE = String.format("CREATE TABLE IF NOT EXISTS %s("
        + "%s VARCHAR(255) PRIMARY KEY NOT NULL UNIQUE,"     // id
        + "%s VARCHAR(255) NOT NULL,"                        // hero id
        + "%s INT NOT NULL,"                                 // inventory type
        + "%s INT NOT NULL"                                  // capacity
        + ");", TABLE_NAME, COLUMN_ID, COLUMN_HERO_ID, COLUMN_INVENTORY_TYPE, COLUMN_CAPACITY);

    public static final Predicate<? super Inventory> MAIN_INVENTORY_FILTER = inventory ->
        inventory.getInventoryType() == InventoryType.MAIN;
    public static final Predicate<? super Inventory> EQUIP_INVENTORY_FILTER = inventory ->
        inventory.getInventoryType() == InventoryType.EQUIP;
    // endregion

    // region Public methods

    @Override
    protected String getColumnKeys() {
        return COLUMNS_KEYS;
    }

    @Override
    public Inventory parseResultSet(ResultSet resultSet) throws SQLException {
        return new Inventory.Builder()
            .id(resultSet.getString(COLUMN_ID))
            .heroId(resultSet.getString(COLUMN_HERO_ID))
            .inventoryType(resultSet.getInt(COLUMN_INVENTORY_TYPE))
            .capacity(resultSet.getInt(COLUMN_CAPACITY))
            .build();
    }

    @Override
    public List<Object> toParamList(Inventory inventory) {
        return new ArrayList<>(Arrays.asList(
            inventory.getId(),
            inventory.getHeroId(),
            inventory.getInventoryType().ordinal(),
            inventory.getCapacity()
        ));
    }

    @Override
    public Inventory fromStringMap(Map<String, Object> map) {
        return null;
    }

    // endregion

}
