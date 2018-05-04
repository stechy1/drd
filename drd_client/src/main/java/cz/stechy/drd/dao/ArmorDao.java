package cz.stechy.drd.dao;

import com.google.firebase.database.DataSnapshot;
import cz.stechy.drd.db.AdvancedDatabaseService;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.model.item.Armor;
import cz.stechy.drd.service.ItemRegistry;
import cz.stechy.drd.service.OnlineItemRegistry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Služba spravující CRUD operace nad třídou {@link Armor}
 */
@Singleton
public final class ArmorDao extends AdvancedDatabaseService<Armor> {

    // region Constants

    private static final String TABLE = "armor";
    private static final String FIREBASE_CHILD_NAME = "items/armor";

    private static final String COLUMN_ID = TABLE + "_id";
    private static final String COLUMN_NAME = TABLE + "_name";
    private static final String COLUMN_DESCRIPTION = TABLE + "_description";
    private static final String COLUMN_AUTHOR = TABLE + "_author";
    private static final String COLUMN_DEFENCE = TABLE + "_defence";
    private static final String COLUMN_MINIMUM_STRENGTH = TABLE + "_minimum_strength";
    private static final String COLUMN_ARMOR_TYPE = TABLE + "_type";
    private static final String COLUMN_WEIGHT_A = TABLE + "_weight_a";
    private static final String COLUMN_WEIGHT_B = TABLE + "_weight_b";
    private static final String COLUMN_WEIGHT_C = TABLE + "_weight_c";
    private static final String COLUMN_PRICE_A = TABLE + "_price_a";
    private static final String COLUMN_PRICE_B = TABLE + "_price_b";
    private static final String COLUMN_PRICE_C = TABLE + "_price_c";
    private static final String COLUMN_IMAGE = TABLE + "_image";
    private static final String COLUMN_STACK_SIZE = TABLE + "_stack_size";
    private static final String COLUMN_UPLOADED = TABLE + "_uploaded";
    private static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION,
        COLUMN_AUTHOR, COLUMN_DEFENCE, COLUMN_MINIMUM_STRENGTH, COLUMN_ARMOR_TYPE, COLUMN_WEIGHT_A,
        COLUMN_WEIGHT_B, COLUMN_WEIGHT_C, COLUMN_PRICE_A, COLUMN_PRICE_B, COLUMN_PRICE_C,
        COLUMN_IMAGE, COLUMN_STACK_SIZE, COLUMN_UPLOADED};
    private static final String COLUMNS_KEYS = GENERATE_COLUMN_KEYS(COLUMNS);
    private static final String COLUMNS_VALUES = GENERATE_COLUMNS_VALUES(COLUMNS);
    private static final String COLUMNS_UPDATE = GENERATE_COLUMNS_UPDATE(COLUMNS);
    private static final String QUERY_CREATE = String.format("CREATE TABLE IF NOT EXISTS %s("
            + "%s VARCHAR(255) PRIMARY KEY NOT NULL UNIQUE,"    // id
            + "%s VARCHAR(255) NOT NULL,"                       // name
            + "%s VARCHAR(255),"                                // description
            + "%s VARCHAR(255) NOT NULL,"                       // author
            + "%s INT NOT NULL,"                                // defence number
            + "%s INT NOT NULL,"                                // minimum strength
            + "%s INT NOT NULL,"                                // armor type
            + "%s INT NOT NULL,"                                // weight A
            + "%s INT NOT NULL,"                                // weight B
            + "%s INT NOT NULL,"                                // weight C
            + "%s INT NOT NULL,"                                // price A
            + "%s INT NOT NULL,"                                // price B
            + "%s INT NOT NULL,"                                // price C
            + "%s BLOB,"                                        // image
            + "%s INT NOT NULL,"                                // stack size
            + "%s BOOLEAN NOT NULL"                             // je položka nahraná
            + "); ", TABLE, COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_AUTHOR,
        COLUMN_DEFENCE, COLUMN_MINIMUM_STRENGTH, COLUMN_ARMOR_TYPE, COLUMN_WEIGHT_A,
        COLUMN_WEIGHT_B,
        COLUMN_WEIGHT_C, COLUMN_PRICE_A, COLUMN_PRICE_B, COLUMN_PRICE_C, COLUMN_IMAGE,
        COLUMN_STACK_SIZE, COLUMN_UPLOADED);
    // endregion

    // region Variables

    private static boolean tableInitialized;

    // endregion

    // region Constructors

    /**
     * Vytvoří nového správce brnění
     *
     * @param db {@link Database}
     */
    public ArmorDao(Database db) {
        super(db);

        ItemRegistry.getINSTANCE().addColection(items);
        OnlineItemRegistry.getINSTANCE().addColection(onlineDatabase);
    }

    // endregion

    // region Private methods

    @Override
    public Armor parseDataSnapshot(DataSnapshot snapshot) {
        return new Armor.Builder()
            .id(snapshot.child(COLUMN_ID).getValue(String.class))
            .name(snapshot.child(COLUMN_NAME).getValue(String.class))
            .description(snapshot.child(COLUMN_DESCRIPTION).getValue(String.class))
            .author(snapshot.child(COLUMN_AUTHOR).getValue(String.class))
            .defenceNumber(snapshot.child(COLUMN_DEFENCE).getValue(Integer.class))
            .minimumStrength(snapshot.child(COLUMN_MINIMUM_STRENGTH).getValue(Integer.class))
            .type(snapshot.child(COLUMN_ARMOR_TYPE).getValue(Integer.class))
            .weightA(snapshot.child(COLUMN_WEIGHT_A).getValue(Integer.class))
            .weightB(snapshot.child(COLUMN_WEIGHT_B).getValue(Integer.class))
            .weightC(snapshot.child(COLUMN_WEIGHT_C).getValue(Integer.class))
            .priceA(snapshot.child(COLUMN_PRICE_A).getValue(Integer.class))
            .priceB(snapshot.child(COLUMN_PRICE_B).getValue(Integer.class))
            .priceC(snapshot.child(COLUMN_PRICE_C).getValue(Integer.class))
            .image(base64ToBlob(snapshot.child(COLUMN_IMAGE).getValue(String.class)))
            .stackSize(snapshot.child(COLUMN_STACK_SIZE).getValue(Integer.class))
            .build();
    }

    @Override
    protected Armor parseResultSet(ResultSet resultSet) throws SQLException {
        return new Armor.Builder()
            .id(resultSet.getString(COLUMN_ID))
            .name(resultSet.getString(COLUMN_NAME))
            .description(resultSet.getString(COLUMN_DESCRIPTION))
            .author(resultSet.getString(COLUMN_AUTHOR))
            .defenceNumber(resultSet.getInt(COLUMN_DEFENCE))
            .minimumStrength(resultSet.getInt(COLUMN_MINIMUM_STRENGTH))
            .type(resultSet.getInt(COLUMN_ARMOR_TYPE))
            .weightA(resultSet.getInt(COLUMN_WEIGHT_A))
            .weightB(resultSet.getInt(COLUMN_WEIGHT_B))
            .weightC(resultSet.getInt(COLUMN_WEIGHT_C))
            .priceA(resultSet.getInt(COLUMN_PRICE_A))
            .priceB(resultSet.getInt(COLUMN_PRICE_B))
            .priceC(resultSet.getInt(COLUMN_PRICE_C))
            .image(readBlob(resultSet, COLUMN_IMAGE))
            .stackSize(resultSet.getInt(COLUMN_STACK_SIZE))
            .downloaded(true)
            .uploaded(resultSet.getBoolean(COLUMN_UPLOADED))
            .build();
    }

    @Override
    protected List<Object> itemToParams(Armor armor) {
        return new ArrayList<>(Arrays.asList(
            armor.getId(),
            armor.getName(),
            armor.getDescription(),
            armor.getAuthor(),
            armor.getDefenceNumber(),
            armor.getMinimumStrength(),
            armor.getType().ordinal(),
            armor.getWeightA(),
            armor.getWeightB(),
            armor.getWeightC(),
            armor.getPriceA().getRaw(),
            armor.getPriceB().getRaw(),
            armor.getPriceC().getRaw(),
            armor.getImage(),
            armor.getStackSize(),
            armor.isUploaded()
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
    public Map<String, Object> toFirebaseMap(Armor item) {
        final Map<String, Object> map = super.toFirebaseMap(item);
        map.put(COLUMN_IMAGE, blobToBase64(item.getImage()));
        return map;
    }

    // endregion

    // region Public methods

    @Override
    public CompletableFuture<Void> createTableAsync() {
        if (tableInitialized) {
            return CompletableFuture.completedFuture(null);
        }

        return super.createTableAsync()
            .thenAccept(ignore -> tableInitialized = true);
    }

    // endregion

}
