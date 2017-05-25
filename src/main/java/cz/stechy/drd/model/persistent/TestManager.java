package cz.stechy.drd.model.persistent;

import cz.stechy.drd.model.db.BaseDatabaseManager;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.db.base.Database;
import cz.stechy.drd.model.item.ItemRegistry;
import cz.stechy.drd.model.item.Test;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TestManager extends BaseDatabaseManager<Test> {

    // region Constants

    // Název tabulky
    private static final String TABLE = "TestTable";

    // Názvy sloupců v databázi
    private static final String COLUMN_ID = TABLE + "_id";
    private static final String COLUMN_NAME = TABLE + "_name";
    private static final String COLUMN_DESCRIPTION = TABLE + "_description";
    private static final String COLUMN_AUTHOR = TABLE + "_author";
    private static final String COLUMN_WEIGHT = TABLE + "_weight";
    private static final String COLUMN_PRICE = TABLE + "_price";
    private static final String COLUMN_ID2 = TABLE + "_id2";
private static final String COLUMN_NAME2 = TABLE + "_name2";
private static final String COLUMN_DOWNLOADED2 = TABLE + "_downloaded2";
private static final String COLUMN_BLOB_TYPE2 = TABLE + "_blob_type2";

    private static final String COLUMN_IMAGE = TABLE + "_image";
    private static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION,
        COLUMN_AUTHOR, COLUMN_WEIGHT, COLUMN_PRICE, COLUMN_ID2, COLUMN_NAME2, COLUMN_DOWNLOADED2, COLUMN_BLOB_TYPE2,  COLUMN_IMAGE};
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
+ "%s INT NOT NULL,"
+ "%s VARCHAR(255) NOT NULL,"
+ "%s BOOLEAN NOT NULL,"
+ "%s BLOB,"
            + "%s BLOB,"                                        // image
            + "); ", TABLE, COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_AUTHOR, COLUMN_WEIGHT,
        COLUMN_PRICE, COLUMN_ID2, COLUMN_NAME2, COLUMN_DOWNLOADED2, COLUMN_BLOB_TYPE2,  COLUMN_IMAGE);

    // endregion

    // region Variables

    private static boolean tableInitialized;

    // endregion

    // region Constructors

    public TestManager(Database db) {
        super(db);

        ItemRegistry.getINSTANCE().addColection(items);
    }

    // endregion

    // region Private methods

    @Override
    protected Test parseResultSet(ResultSet resultSet) throws SQLException {
        return new Test.Builder()
            .id(resultSet.getString(COLUMN_ID))
            .name(resultSet.getString(COLUMN_NAME))
            .description(resultSet.getString(COLUMN_DESCRIPTION))
            .author(resultSet.getString(COLUMN_AUTHOR))
            .weight(resultSet.getInt(COLUMN_WEIGHT))
            .price(resultSet.getInt(COLUMN_PRICE))
            .id2(resultSet.getInt(COLUMN_ID2))
.name2(resultSet.getString(COLUMN_NAME2))
.downloaded2(resultSet.getBoolean(COLUMN_DOWNLOADED2))
.blob_type2(readBlob(resultSet, COLUMN_BLOB_TYPE2))

            .image(readBlob(resultSet, COLUMN_IMAGE))
            .build();
    }

    @Override
    protected List<Object> itemToParams(Test item) {
        return new ArrayList<>(Arrays.asList(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getAuthor(),
            item.getWeight(),
            item.getPrice().getRaw(),
            item.getId2(),
item.getName2(),
item.getDownloaded2(),
item.getBlob_Type2(),

            item.getImage()
        ));
    }

    @Override
    protected String getTable() {
        return TABLE;
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
