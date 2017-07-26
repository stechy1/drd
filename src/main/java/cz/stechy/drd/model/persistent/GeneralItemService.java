package cz.stechy.drd.model.persistent;

import com.google.firebase.database.DataSnapshot;
import cz.stechy.drd.model.db.AdvancedDatabaseService;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.db.base.Database;
import cz.stechy.drd.model.item.GeneralItem;
import cz.stechy.drd.model.item.ItemRegistry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Služba spravující CRUD operace nad třídou {@link GeneralItem}
 */
public final class GeneralItemService extends AdvancedDatabaseService<GeneralItem> {

    // region Constants

    private static final String TABLE = "general_items";
    private static final String FIREBASE_CHILD_NAME = "items/general";

    private static final String COLUMN_ID = TABLE + "_id";
    private static final String COLUMN_NAME = TABLE + "_name";
    private static final String COLUMN_DESCRIPTION = TABLE + "_description";
    private static final String COLUMN_AUTHOR = TABLE + "_author";
    private static final String COLUMN_WEIGHT = TABLE + "_weight";
    private static final String COLUMN_PRICE = TABLE + "_price";
    private static final String COLUMN_IMAGE = TABLE + "_image";
    private static final String COLUMN_STACK_SIZE = TABLE + "_stack_size";
    private static final String COLUMN_DOWNLOADED = TABLE + "_downloaded";
    private static final String COLUMN_UPLOADED = TABLE + "_uploaded";
    private static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION,
        COLUMN_AUTHOR, COLUMN_WEIGHT, COLUMN_PRICE, COLUMN_IMAGE, COLUMN_STACK_SIZE,
        COLUMN_DOWNLOADED, COLUMN_UPLOADED};
    private static final String COLUMNS_KEYS = GENERATE_COLUMN_KEYS(COLUMNS);
    private static final String COLUMNS_VALUES = GENERATE_COLUMNS_VALUES(COLUMNS);
    private static final String COLUMNS_UPDATE = GENERATE_COLUMNS_UPDATE(COLUMNS);
    private static final String QUERY_CREATE = String.format("CREATE TABLE IF NOT EXISTS %s("
            + "%s VARCHAR(255) PRIMARY KEY NOT NULL UNIQUE,"    // id
            + "%s VARCHAR(255) NOT NULL,"                       // name
            + "%s VARCHAR(255),"                                // description
            + "%s VARCHAR(255) NOT NULL,"                       // author
            + "%s INT NOT NULL,"                                // weight
            + "%s INT NOT NULL,"                                // price
            + "%s BLOB,"                                        // image
            + "%s INT NOT NULL,"                                // stack size
            + "%s BOOLEAN NOT NULL,"                            // je položka stažená
            + "%s BOOLEAN NOT NULL"                             // je položka nahraná
            + "); ", TABLE, COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_AUTHOR,
        COLUMN_WEIGHT, COLUMN_PRICE, COLUMN_IMAGE, COLUMN_STACK_SIZE, COLUMN_DOWNLOADED,
        COLUMN_UPLOADED);

    // endregion

    // region Variables

    private static boolean tableInitialized = false;

    // endregion

    // region Constructors

    /**
     * Vytvoří nového správce běžnýc předmětů
     *
     * @param db {@link Database}
     */
    public GeneralItemService(Database db) {
        super(db);

        ItemRegistry.getINSTANCE().addColection(items);
    }

    // endregion

    // region Private methods

    @Override
    protected GeneralItem parseDataSnapshot(DataSnapshot snapshot) {
        return new GeneralItem.Builder()
            .id(snapshot.child(COLUMN_ID).getValue(String.class))
            .name(snapshot.child(COLUMN_NAME).getValue(String.class))
            .description(snapshot.child(COLUMN_DESCRIPTION).getValue(String.class))
            .author(snapshot.child(COLUMN_AUTHOR).getValue(String.class))
            .weight(snapshot.child(COLUMN_WEIGHT).getValue(Integer.class))
            .price(snapshot.child(COLUMN_PRICE).getValue(Integer.class))
            .image(base64ToBlob(snapshot.child(COLUMN_IMAGE).getValue(String.class)))
            .stackSize(snapshot.child(COLUMN_STACK_SIZE).getValue(Integer.class))
            .build();
    }

    @Override
    protected GeneralItem parseResultSet(ResultSet resultSet) throws SQLException {
        return new GeneralItem.Builder()
            .id(resultSet.getString(COLUMN_ID))
            .author(resultSet.getString(COLUMN_AUTHOR))
            .name(resultSet.getString(COLUMN_NAME))
            .description(resultSet.getString(COLUMN_DESCRIPTION))
            .weight(resultSet.getInt(COLUMN_WEIGHT))
            .price(resultSet.getInt(COLUMN_PRICE))
            .image(readBlob(resultSet, COLUMN_IMAGE))
            .stackSize(resultSet.getInt(COLUMN_STACK_SIZE))
            .downloaded(resultSet.getBoolean(COLUMN_DOWNLOADED))
            .uploaded(resultSet.getBoolean(COLUMN_UPLOADED))
            .build();
    }

    @Override
    protected List<Object> itemToParams(GeneralItem item) {
        return new ArrayList<>(Arrays.asList(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getAuthor(),
            item.getWeight(),
            item.getPrice().getRaw(),
            item.getImage(),
            item.getStackSize(),
            item.isDownloaded(),
            item.isUploaded()
        ));
    }

    @Override
    protected String getTable() {
        return TABLE;
    }

    @Override
    protected String getFirebaseChildName() {
        return FIREBASE_CHILD_NAME;
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

    @Override
    protected Map<String, Object> toFirebaseMap(GeneralItem item) {
        final Map<String, Object> map = super.toFirebaseMap(item);
        map.put(COLUMN_IMAGE, blobToBase64(item.getImage()));
        return map;
    }

    // endregion

    // region Public methods

    @Override
    public void createTable() throws DatabaseException {
        if (tableInitialized) {
            return;
        }

        super.createTable();
        tableInitialized = true;
    }

    // endregion

}
