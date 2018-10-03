package cz.stechy.drd.db.table.inventory_content;

import static cz.stechy.drd.R.Database.Inventorycontent.COLUMN_AMMOUNT;
import static cz.stechy.drd.R.Database.Inventorycontent.COLUMN_ID;
import static cz.stechy.drd.R.Database.Inventorycontent.COLUMN_INVENTORY_ID;
import static cz.stechy.drd.R.Database.Inventorycontent.COLUMN_ITEM_ID;
import static cz.stechy.drd.R.Database.Inventorycontent.COLUMN_METADATA;
import static cz.stechy.drd.R.Database.Inventorycontent.COLUMN_SLOT;
import static cz.stechy.drd.R.Database.Inventorycontent.TABLE_NAME;

import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.base.BaseTableDefinitions;
import cz.stechy.drd.model.inventory.InventoryContent;
import cz.stechy.drd.model.inventory.InventoryContent.Metadata;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Table(clazz = InventoryContent.class, type = Type.DEFINITION)
public class InventoryContentTableDefinitions extends BaseTableDefinitions<InventoryContent> {

    // region Constants

    private static final int SLOT_OCCUPIED = 1;
    private static final int SLOT_NOT_OCCUPIED = 0;

    // TODO poškození itemu?
    private static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_INVENTORY_ID,
        COLUMN_ITEM_ID, COLUMN_AMMOUNT, COLUMN_SLOT, COLUMN_METADATA};
    static final String COLUMNS_KEYS = GENERATE_COLUMN_KEYS(COLUMNS);
    static final String COLUMNS_VALUES = GENERATE_COLUMNS_VALUES(COLUMNS);
    static final String COLUMNS_UPDATE = GENERATE_COLUMNS_UPDATE(COLUMNS);
    static final String QUERY_CREATE = String.format("CREATE TABLE IF NOT EXISTS %s("
            + "%s VARCHAR(255) PRIMARY KEY NOT NULL UNIQUE,"    // id
            + "%s VARCHAR(255) NOT NULL,"                       // inventory id
            + "%s VARCHAR(255) NOT NULL,"                       // item id
            + "%S INT NOT NULL,"                                // ammount
            + "%s INT NOT NULL,"                                // slot index
            + "%s BLOB"                                         // image
            + ");", TABLE_NAME, COLUMN_ID, COLUMN_INVENTORY_ID, COLUMN_ITEM_ID, COLUMN_AMMOUNT,
        COLUMN_SLOT, COLUMN_METADATA);

    // endregion

    // region Public methods

    @Override
    protected String getColumnKeys() {
        return COLUMNS_KEYS;
    }

    @Override
    public InventoryContent parseResultSet(ResultSet resultSet) throws SQLException {
        return new InventoryContent.Builder()
            .id(resultSet.getString(COLUMN_ID))
            .inventoryId(resultSet.getString(COLUMN_INVENTORY_ID))
            .itemId(resultSet.getString(COLUMN_ITEM_ID))
            .ammount(resultSet.getInt(COLUMN_AMMOUNT))
            .slotId(resultSet.getInt(COLUMN_SLOT))
            .metadata(Metadata.deserialize(readBlob(resultSet, COLUMN_METADATA)))
            .build();
    }

    @Override
    public List<Object> toParamList(InventoryContent content) {
        return new ArrayList<>(Arrays.asList(
            content.getId(),
            content.getInventoryId(),
            content.getItemId(),
            content.getAmmount(),
            content.getSlotId(),
            Metadata.serialize(content.getMetadata())
        ));
    }

    @Override
    public InventoryContent fromStringMap(Map<String, Object> map) {
        return null;
    }

    // endregion

}
