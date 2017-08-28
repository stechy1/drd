package cz.stechy.drd.model.persistent;

import com.google.firebase.database.DataSnapshot;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.model.db.AdvancedDatabaseService;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.db.base.Database;
import cz.stechy.drd.model.item.Backpack;
import cz.stechy.drd.model.item.ItemRegistry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Služba spravující CRUD operace nad třídou {@link Backpack}
 */
@Singleton
public final class BackpackService extends AdvancedDatabaseService<Backpack> {

    // region Constants

    // Název tabulky
    private static final String TABLE = "backpack";
    private static final String FIREBASE_CHILD_NAME = "items/backpack";

    // Názvy sloupců v databázi
    private static final String COLUMN_ID = TABLE + "_id";
    private static final String COLUMN_NAME = TABLE + "_name";
    private static final String COLUMN_DESCRIPTION = TABLE + "_description";
    private static final String COLUMN_AUTHOR = TABLE + "_author";
    private static final String COLUMN_WEIGHT = TABLE + "_weight";
    private static final String COLUMN_PRICE = TABLE + "_price";
    private static final String COLUMN_MAX_LOAD = TABLE + "_max_load";
    private static final String COLUMN_SIZE = TABLE + "_size";
    private static final String COLUMN_IMAGE = TABLE + "_image";
    private static final String COLUMN_STACK_SIZE = TABLE + "_stack_size";
    private static final String COLUMN_UPLOADED = TABLE + "_uploaded";
    private static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION,
        COLUMN_AUTHOR, COLUMN_WEIGHT, COLUMN_PRICE, COLUMN_MAX_LOAD, COLUMN_SIZE, COLUMN_IMAGE,
        COLUMN_STACK_SIZE, COLUMN_UPLOADED};
    private static final String COLUMNS_KEYS = GENERATE_COLUMN_KEYS(COLUMNS);
    private static final String COLUMNS_VALUES = GENERATE_COLUMNS_VALUES(COLUMNS);
    private static final String COLUMNS_UPDATE = GENERATE_COLUMNS_UPDATE(COLUMNS);
    private static final String QUERY_CREATE = String.format("CREATE TABLE IF NOT EXISTS %s("
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
            + "); ", TABLE, COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_AUTHOR, COLUMN_WEIGHT,
        COLUMN_PRICE, COLUMN_MAX_LOAD, COLUMN_SIZE, COLUMN_IMAGE, COLUMN_STACK_SIZE,
        COLUMN_UPLOADED);

    // endregion

    // region Variables

    private static boolean tableInitialized;

    // endregion

    // region Constructors

    public BackpackService(Database db) {
        super(db);

        ItemRegistry.getINSTANCE().addColection(items);
    }

    // endregion

    // region Private methods

    @Override
    protected Backpack parseDataSnapshot(DataSnapshot snapshot) {
        return new Backpack.Builder()
            .id(snapshot.child(COLUMN_ID).getValue(String.class))
            .name(snapshot.child(COLUMN_NAME).getValue(String.class))
            .description(snapshot.child(COLUMN_DESCRIPTION).getValue(String.class))
            .author(snapshot.child(COLUMN_AUTHOR).getValue(String.class))
            .weight(snapshot.child(COLUMN_WEIGHT).getValue(Integer.class))
            .price(snapshot.child(COLUMN_PRICE).getValue(Integer.class))
            .maxLoad(snapshot.child(COLUMN_MAX_LOAD).getValue(Integer.class))
            .size(snapshot.child(COLUMN_SIZE).getValue(Integer.class))
            .image(base64ToBlob(snapshot.child(COLUMN_IMAGE).getValue(String.class)))
            .stackSize(snapshot.child(COLUMN_STACK_SIZE).getValue(Integer.class))
            .build();
    }

    @Override
    protected Backpack parseResultSet(ResultSet resultSet) throws SQLException {
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
    protected List<Object> itemToParams(Backpack backpack) {
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
    protected Map<String, Object> toFirebaseMap(Backpack item) {
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
