package cz.stechy.drd.db.table.general_item;

import static cz.stechy.drd.R.Database.Generalitems.*;

import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.base.BaseTableDefinitions;
import cz.stechy.drd.model.item.GeneralItem;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Table(clazz = GeneralItem.class, type = Type.DEFINITION)
public class GeneralItemTableDefinitions extends BaseTableDefinitions<GeneralItem> {

    // region Constants
    private static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION,
        COLUMN_AUTHOR, COLUMN_WEIGHT, COLUMN_PRICE, COLUMN_IMAGE, COLUMN_STACK_SIZE,
        COLUMN_UPLOADED};
    static final String COLUMNS_KEYS = GENERATE_COLUMN_KEYS(COLUMNS);
    static final String COLUMNS_VALUES = GENERATE_COLUMNS_VALUES(COLUMNS);
    static final String COLUMNS_UPDATE = GENERATE_COLUMNS_UPDATE(COLUMNS);
    static final String QUERY_CREATE = String.format("CREATE TABLE IF NOT EXISTS %s("
            + "%s VARCHAR(255) PRIMARY KEY NOT NULL UNIQUE,"    // id
            + "%s VARCHAR(255) NOT NULL,"                       // name
            + "%s VARCHAR(255),"                                // description
            + "%s VARCHAR(255) NOT NULL,"                       // author
            + "%s INT NOT NULL,"                                // weight
            + "%s INT NOT NULL,"                                // price
            + "%s BLOB,"                                        // image
            + "%s INT NOT NULL,"                                // stack size
            + "%s BOOLEAN NOT NULL"                             // je položka nahraná
            + "); ", TABLE_NAME, COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_AUTHOR,
        COLUMN_WEIGHT, COLUMN_PRICE, COLUMN_IMAGE, COLUMN_STACK_SIZE, COLUMN_UPLOADED);

    // endregion

    // region Public methods

    @Override
    protected String getColumnKeys() {
        return COLUMNS_KEYS;
    }

    @Override
    public GeneralItem parseResultSet(ResultSet resultSet) throws SQLException {
        return new GeneralItem.Builder()
            .id(resultSet.getString(COLUMN_ID))
            .author(resultSet.getString(COLUMN_AUTHOR))
            .name(resultSet.getString(COLUMN_NAME))
            .description(resultSet.getString(COLUMN_DESCRIPTION))
            .weight(resultSet.getInt(COLUMN_WEIGHT))
            .price(resultSet.getInt(COLUMN_PRICE))
            .image(readBlob(resultSet, COLUMN_IMAGE))
            .stackSize(resultSet.getInt(COLUMN_STACK_SIZE))
            .downloaded(true)
            .uploaded(resultSet.getBoolean(COLUMN_UPLOADED))
            .build();
    }

    @Override
    public List<Object> toParamList(GeneralItem item) {
        return new ArrayList<>(Arrays.asList(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getAuthor(),
            item.getWeight(),
            item.getPrice().getRaw(),
            item.getImage(),
            item.getStackSize(),
            item.isUploaded()
        ));
    }

    @Override
    public GeneralItem fromStringMap(Map<String, Object> map) {
        return new GeneralItem.Builder()
            .id((String) map.get(COLUMN_ID))
            .name((String) map.get(COLUMN_NAME))
            .description((String) map.get(COLUMN_DESCRIPTION))
            .author((String) map.get(COLUMN_AUTHOR))
            .weight((Integer) map.get(COLUMN_WEIGHT))
            .price((Integer) map.get(COLUMN_PRICE))
            .image(base64ToBlob((String) map.get(COLUMN_IMAGE)))
            .stackSize((Integer) map.get(COLUMN_STACK_SIZE))
            .build();
    }

    @Override
    public Map<String, Object> toStringItemMap(GeneralItem item) {
        final Map<String, Object> map = super.toStringItemMap(item);
        map.put(COLUMN_IMAGE, blobToBase64(item.getImage()));
        return map;
    }

    // endregion

}
