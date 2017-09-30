package cz.stechy.drd.model.persistent;

import com.google.firebase.database.DataSnapshot;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.model.db.AdvancedDatabaseService;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.db.base.Database;
import cz.stechy.drd.model.service.ItemRegistry;
import cz.stechy.drd.model.item.RangedWeapon;
import cz.stechy.drd.model.service.OnlineItemRegistry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Služba spravující CRUD operace nad třídou {@link RangedWeapon}
 */
@Singleton
public final class RangedWeaponService extends AdvancedDatabaseService<RangedWeapon> {

    // region Constants

    // Název tabulky
    private static final String TABLE = "weapon_ranged";
    private static final String FIREBASE_CHILD_NAME = "items/weapon/ranged";

    // Názvy sloupců v databázi
    private static final String COLUMN_ID = TABLE + "_id";
    private static final String COLUMN_NAME = TABLE + "_name";
    private static final String COLUMN_DESCRIPTION = TABLE + "_description";
    private static final String COLUMN_AUTHOR = TABLE + "_author";
    private static final String COLUMN_WEIGHT = TABLE + "_weight";
    private static final String COLUMN_PRICE = TABLE + "_price";
    private static final String COLUMN_STRENGTH = TABLE + "_strength";
    private static final String COLUMN_RAMPANCY = TABLE + "_rampancy";
    private static final String COLUMN_TYPE = TABLE + "_type";
    private static final String COLUMN_RANGE_LOW = TABLE + "_range_low";
    private static final String COLUMN_RANGE_MEDIUM = TABLE + "_range_medium";
    private static final String COLUMN_RANGE_LONG = TABLE + "_range_long";
    private static final String COLUMN_RENOWN = TABLE + "_renown";
    private static final String COLUMN_IMAGE = TABLE + "_image";
    private static final String COLUMN_STACK_SIZE = TABLE + "_stack_size";
    private static final String COLUMN_UPLOADED = TABLE + "_uploaded";
    private static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION,
        COLUMN_AUTHOR, COLUMN_WEIGHT, COLUMN_PRICE, COLUMN_STRENGTH, COLUMN_RAMPANCY,
        COLUMN_TYPE, COLUMN_RANGE_LOW, COLUMN_RANGE_MEDIUM, COLUMN_RANGE_LONG, COLUMN_RENOWN,
        COLUMN_IMAGE, COLUMN_STACK_SIZE, COLUMN_UPLOADED};
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
            + "%s INT NOT NULL,"                                // strength
            + "%s INT NOT NULL,"                                // rampancy
            + "%s INT NOT NULL,"                                // type
            + "%s INT NOT NULL,"                                // range_low
            + "%s INT NOT NULL,"                                // range_medium
            + "%s INT NOT NULL,"                                // range_high
            + "%s INT NOT NULL,"                                // renown
            + "%s BLOB,"                                        // image
            + "%s INT NOT NULL,"                                // stack size
            + "%s BOOLEAN NOT NULL"                             // je položka nahraná
            + ");", TABLE, COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_AUTHOR, COLUMN_WEIGHT,
        COLUMN_PRICE, COLUMN_STRENGTH, COLUMN_RAMPANCY, COLUMN_TYPE, COLUMN_RANGE_LOW,
        COLUMN_RANGE_MEDIUM, COLUMN_RANGE_LONG, COLUMN_RENOWN, COLUMN_IMAGE, COLUMN_STACK_SIZE,
        COLUMN_UPLOADED);

    // endregion

    // region Variables

    private static boolean tableInitialized;

    // endregion

    // region Constructors

    /**
     * Vytvoří nového správce zbraní na dálku
     *
     * @param db {@link Database} Databáze, která obsahuje data o hrdinech
     */
    public RangedWeaponService(Database db) {
        super(db);

        ItemRegistry.getINSTANCE().addColection(items);
        OnlineItemRegistry.getINSTANCE().addColection(onlineDatabase);
    }

    // endregion

    // region Private methods

    @Override
    public RangedWeapon parseDataSnapshot(DataSnapshot snapshot) {
        return new RangedWeapon.Builder()
            .id(snapshot.child(COLUMN_ID).getValue(String.class))
            .name(snapshot.child(COLUMN_NAME).getValue(String.class))
            .description(snapshot.child(COLUMN_DESCRIPTION).getValue(String.class))
            .author(snapshot.child(COLUMN_AUTHOR).getValue(String.class))
            .weight(snapshot.child(COLUMN_WEIGHT).getValue(Integer.class))
            .price(snapshot.child(COLUMN_PRICE).getValue(Integer.class))
            .strength(snapshot.child(COLUMN_STRENGTH).getValue(Integer.class))
            .rampancy(snapshot.child(COLUMN_RAMPANCY).getValue(Integer.class))
            .weaponType(snapshot.child(COLUMN_TYPE).getValue(Integer.class))
            .rangeLow(snapshot.child(COLUMN_RANGE_LOW).getValue(Integer.class))
            .rangeMedium(snapshot.child(COLUMN_RANGE_MEDIUM).getValue(Integer.class))
            .rangeLong(snapshot.child(COLUMN_RANGE_LONG).getValue(Integer.class))
            .renown(snapshot.child(COLUMN_RENOWN).getValue(Integer.class))
            .image(base64ToBlob(snapshot.child(COLUMN_IMAGE).getValue(String.class)))
            .stackSize(snapshot.child(COLUMN_STACK_SIZE).getValue(Integer.class))
            .build();
    }

    @Override
    protected RangedWeapon parseResultSet(ResultSet resultSet) throws SQLException {
        return new RangedWeapon.Builder()
            .id(resultSet.getString(COLUMN_ID))
            .name(resultSet.getString(COLUMN_NAME))
            .description(resultSet.getString(COLUMN_DESCRIPTION))
            .author(resultSet.getString(COLUMN_AUTHOR))
            .weight(resultSet.getInt(COLUMN_WEIGHT))
            .price(resultSet.getInt(COLUMN_PRICE))
            .strength(resultSet.getInt(COLUMN_STRENGTH))
            .rampancy(resultSet.getInt(COLUMN_RAMPANCY))
            .weaponType(resultSet.getInt(COLUMN_TYPE))
            .rangeLow(resultSet.getInt(COLUMN_RANGE_LOW))
            .rangeMedium(resultSet.getInt(COLUMN_RANGE_MEDIUM))
            .rangeLong(resultSet.getInt(COLUMN_RANGE_LONG))
            .renown(resultSet.getInt(COLUMN_RENOWN))
            .image(readBlob(resultSet, COLUMN_IMAGE))
            .stackSize(resultSet.getInt(COLUMN_STACK_SIZE))
            .downloaded(true)
            .uploaded(resultSet.getBoolean(COLUMN_UPLOADED))
            .build();
    }

    @Override
    protected List<Object> itemToParams(RangedWeapon weapon) {
        return new ArrayList<>(Arrays.asList(
            weapon.getId(),
            weapon.getName(),
            weapon.getDescription(),
            weapon.getAuthor(),
            weapon.getWeight(),
            weapon.getPrice().getRaw(),
            weapon.getStrength(),
            weapon.getRampancy(),
            weapon.getWeaponType().ordinal(),
            weapon.getRangeLow(),
            weapon.getRangeMedium(),
            weapon.getRangeLong(),
            weapon.getRenown(),
            weapon.getImage(),
            weapon.getStackSize(),
            weapon.isUploaded()
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
    public Map<String, Object> toFirebaseMap(RangedWeapon item) {
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
