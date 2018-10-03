package cz.stechy.drd.db.table.backpack;

import static cz.stechy.drd.R.Database.Backpack.COLUMN_AUTHOR;
import static cz.stechy.drd.R.Database.Backpack.COLUMN_DESCRIPTION;
import static cz.stechy.drd.R.Database.Backpack.COLUMN_ID;
import static cz.stechy.drd.R.Database.Backpack.COLUMN_IMAGE;
import static cz.stechy.drd.R.Database.Backpack.COLUMN_MAX_LOAD;
import static cz.stechy.drd.R.Database.Backpack.COLUMN_NAME;
import static cz.stechy.drd.R.Database.Backpack.COLUMN_PRICE;
import static cz.stechy.drd.R.Database.Backpack.COLUMN_SIZE;
import static cz.stechy.drd.R.Database.Backpack.COLUMN_STACK_SIZE;
import static cz.stechy.drd.R.Database.Backpack.COLUMN_UPLOADED;
import static cz.stechy.drd.R.Database.Backpack.COLUMN_WEIGHT;
import static cz.stechy.drd.R.Database.Backpack.TABLE_NAME;

import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.base.BaseTableDefinitions;
import cz.stechy.drd.model.item.Backpack;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Table(type = Type.DEFINITION, clazz = Backpack.class)
public class BackpackTableDefinitions extends BaseTableDefinitions<Backpack> {

    // region Constants

    private static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION,
        COLUMN_AUTHOR, COLUMN_WEIGHT, COLUMN_PRICE, COLUMN_MAX_LOAD, COLUMN_SIZE, COLUMN_IMAGE,
        COLUMN_STACK_SIZE, COLUMN_UPLOADED};
    static final String COLUMNS_KEYS = GENERATE_COLUMN_KEYS(COLUMNS);
    static final String COLUMNS_VALUES = GENERATE_COLUMNS_VALUES(COLUMNS);
    static final String COLUMNS_UPDATE = GENERATE_COLUMNS_UPDATE(COLUMNS);
    static final String QUERY_CREATE = String.format("CREATE TABLE IF NOT EXISTS %s("
            + "%s VARCHAR(255) PRIMARY KEY NOT NULL UNIQUE,"    // id
            + "%s VARCHAR(255) NOT NULL,"                       // name
            + "%s VARCHAR(255),"                                // description
            + "%s VARCHAR(255) NOT NULL,"                       // autor
            + "%s INT NOT NULL,"                                // weight
            + "%s INT NOT NULL,"                                // price
            + "%s INT NOT NULL,"                                // max load
            + "%s INT NOT NULL,"                                // size
            + "%s BLOB,"                                        // image
            + "%s INT NOT NULL,"                                // stack size
            + "%s BOOLEAN NOT NULL"                             // je položka nahraná
            + "); ", TABLE_NAME, COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_AUTHOR, COLUMN_WEIGHT,
        COLUMN_PRICE, COLUMN_MAX_LOAD, COLUMN_SIZE, COLUMN_IMAGE, COLUMN_STACK_SIZE,
        COLUMN_UPLOADED);

    // endregion

    // region Public methods

    @Override
    protected String getColumnKeys() {
        return COLUMNS_KEYS;
    }

    @Override
    public Backpack parseResultSet(ResultSet resultSet) throws SQLException {
        return new Backpack.Builder()
            .id(resultSet.getString(COLUMN_ID))
            .name(resultSet.getString(COLUMN_NAME))
            .description(resultSet.getString(COLUMN_DESCRIPTION))
            .author(resultSet.getString(COLUMN_AUTHOR))
            .weight(resultSet.getInt(COLUMN_WEIGHT))
            .price(resultSet.getInt(COLUMN_PRICE))
            .maxLoad(resultSet.getInt(COLUMN_MAX_LOAD))
            .size(resultSet.getInt(COLUMN_SIZE))
            .image(readBlob(resultSet, COLUMN_IMAGE))
            .stackSize(resultSet.getInt(COLUMN_STACK_SIZE))
            .downloaded(true)
            .uploaded(resultSet.getBoolean(COLUMN_UPLOADED))
            .build();
    }

    @Override
    public List<Object> toParamList(Backpack backpack) {
        return new ArrayList<>(Arrays.asList(
            backpack.getId(),
            backpack.getName(),
            backpack.getDescription(),
            backpack.getAuthor(),
            backpack.getWeight(),
            backpack.getPrice().getRaw(),
            backpack.getMaxLoad(),
            backpack.getSize().ordinal(),
            backpack.getImage(),
            backpack.getStackSize(),
            backpack.isUploaded()
        ));
    }

    @Override
    public Backpack fromStringMap(Map<String, Object> map) {
        return new Backpack.Builder()
            .id((String) map.get(COLUMN_ID))
            .name((String) map.get(COLUMN_NAME))
            .description((String) map.get(COLUMN_DESCRIPTION))
            .author((String) map.get(COLUMN_AUTHOR))
            .weight((Integer) map.get(COLUMN_WEIGHT))
            .price((Integer) map.get(COLUMN_PRICE))
            .maxLoad((Integer) map.get(COLUMN_MAX_LOAD))
            .size((Integer) map.get(COLUMN_SIZE))
            .image(base64ToBlob((String) map.get(COLUMN_IMAGE)))
            .stackSize((Integer) map.get(COLUMN_STACK_SIZE))
            .build();
    }

    @Override
    public Map<String, Object> toStringItemMap(Backpack item) {
        final Map<String, Object> map = super.toStringItemMap(item);
        map.put(COLUMN_IMAGE, blobToBase64(item.getImage()));
        return map;
    }

    // endregion

}
