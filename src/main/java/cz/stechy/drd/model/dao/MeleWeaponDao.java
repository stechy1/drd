package cz.stechy.drd.model.dao;

import com.google.firebase.database.DataSnapshot;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.model.db.AdvancedDatabaseService;
import cz.stechy.drd.model.db.base.Database;
import cz.stechy.drd.model.item.MeleWeapon;
import cz.stechy.drd.model.service.ItemRegistry;
import cz.stechy.drd.model.service.OnlineItemRegistry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Služba spravující CRUD operace nad třídou {@link MeleWeapon}
 */
@Singleton
public final class MeleWeaponDao extends AdvancedDatabaseService<MeleWeapon> {

    // region Constants

    // Název tabulky
    private static final String TABLE = "weapon_mele";
    private static final String FIREBASE_CHILD_NAME = "items/weapon/mele";

    // Názvy sloupců v databázi
    private static final String COLUMN_ID = TABLE + "_id";
    private static final String COLUMN_NAME = TABLE + "_name";
    private static final String COLUMN_DESCRIPTION = TABLE + "_description";
    private static final String COLUMN_AUTHOR = TABLE + "_author";
    private static final String COLUMN_WEIGHT = TABLE + "_weight";
    private static final String COLUMN_PRICE = TABLE + "_price";
    private static final String COLUMN_STRENGTH = TABLE + "_strength";
    private static final String COLUMN_RAMPANCY = TABLE + "_rampancy";
    private static final String COLUMN_DEFENCE = TABLE + "_defence";
    private static final String COLUMN_RENOWN = TABLE + "_renown";
    private static final String COLUMN_CLASS = TABLE + "_class";
    private static final String COLUMN_TYPE = TABLE + "_type";
    private static final String COLUMN_IMAGE = TABLE + "_image";
    private static final String COLUMN_STACK_SIZE = TABLE + "_stack_size";
    private static final String COLUMN_UPLOADED = TABLE + "_uploaded";
    private static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION,
        COLUMN_AUTHOR, COLUMN_WEIGHT, COLUMN_PRICE, COLUMN_STRENGTH, COLUMN_RAMPANCY,
        COLUMN_DEFENCE, COLUMN_RENOWN, COLUMN_CLASS, COLUMN_TYPE, COLUMN_IMAGE, COLUMN_STACK_SIZE,
        COLUMN_UPLOADED};
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
            + "%s INT NOT NULL,"                                // strength
            + "%s INT NOT NULL,"                                // rampancy
            + "%s INT NOT NULL,"                                // defence
            + "%s INT NOT NULL,"                                // renown
            + "%s INT NOT NULL,"                                // class
            + "%s INT NOT NULL,"                                // type
            + "%s BLOB,"                                        // image
            + "%s INT NOT NULL,"                                // stack size
            + "%s BOOLEAN NOT NULL"                             // je položka nahraná
            + "); ", TABLE, COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_AUTHOR, COLUMN_WEIGHT,
        COLUMN_PRICE, COLUMN_STRENGTH, COLUMN_RAMPANCY, COLUMN_DEFENCE, COLUMN_RENOWN, COLUMN_CLASS,
        COLUMN_TYPE, COLUMN_IMAGE, COLUMN_STACK_SIZE, COLUMN_UPLOADED);

    // endregion

    // region Variables

    private static boolean tableInitialized;

    // endregion

    // region Constructors

    /**
     * Vytvoří nového správce zbraní na blízko
     *
     * @param db {@link Database} Databáze, která obsahuje data o hrdinech
     */
    public MeleWeaponDao(Database db) {
        super(db);

        ItemRegistry.getINSTANCE().addColection(items);
        OnlineItemRegistry.getINSTANCE().addColection(onlineDatabase);
    }

    // endregion

    // region Private methods

    @Override
    public MeleWeapon parseDataSnapshot(DataSnapshot snapshot) {
        return new MeleWeapon.Builder()
            .id(snapshot.child(COLUMN_ID).getValue(String.class))
            .name(snapshot.child(COLUMN_NAME).getValue(String.class))
            .description(snapshot.child(COLUMN_DESCRIPTION).getValue(String.class))
            .author(snapshot.child(COLUMN_AUTHOR).getValue(String.class))
            .weight(snapshot.child(COLUMN_WEIGHT).getValue(Integer.class))
            .price(snapshot.child(COLUMN_PRICE).getValue(Integer.class))
            .strength(snapshot.child(COLUMN_STRENGTH).getValue(Integer.class))
            .rampancy(snapshot.child(COLUMN_RAMPANCY).getValue(Integer.class))
            .defence(snapshot.child(COLUMN_DEFENCE).getValue(Integer.class))
            .renown(snapshot.child(COLUMN_RENOWN).getValue(Integer.class))
            .weaponClass(snapshot.child(COLUMN_CLASS).getValue(Integer.class))
            .weaponType(snapshot.child(COLUMN_TYPE).getValue(Integer.class))
            .image(base64ToBlob(snapshot.child(COLUMN_IMAGE).getValue(String.class)))
            .stackSize(snapshot.child(COLUMN_STACK_SIZE).getValue(Integer.class))
            .build();
    }

    @Override
    protected MeleWeapon parseResultSet(ResultSet resultSet) throws SQLException {
        return new MeleWeapon.Builder()
            .id(resultSet.getString(COLUMN_ID))
            .name(resultSet.getString(COLUMN_NAME))
            .description(resultSet.getString(COLUMN_DESCRIPTION))
            .author(resultSet.getString(COLUMN_AUTHOR))
            .weight(resultSet.getInt(COLUMN_WEIGHT))
            .price(resultSet.getInt(COLUMN_PRICE))
            .strength(resultSet.getInt(COLUMN_STRENGTH))
            .rampancy(resultSet.getInt(COLUMN_RAMPANCY))
            .defence(resultSet.getInt(COLUMN_DEFENCE))
            .renown(resultSet.getInt(COLUMN_RENOWN))
            .weaponClass(resultSet.getInt(COLUMN_CLASS))
            .weaponType(resultSet.getInt(COLUMN_TYPE))
            .image(readBlob(resultSet, COLUMN_IMAGE))
            .stackSize(resultSet.getInt(COLUMN_STACK_SIZE))
            .downloaded(true)
            .uploaded(resultSet.getBoolean(COLUMN_UPLOADED))
            .build();
    }

    @Override
    protected List<Object> itemToParams(MeleWeapon weapon) {
        return new ArrayList<>(Arrays.asList(
            weapon.getId(),
            weapon.getName(),
            weapon.getDescription(),
            weapon.getAuthor(),
            weapon.getWeight(),
            weapon.getPrice().getRaw(),
            weapon.getStrength(),
            weapon.getRampancy(),
            weapon.getDefence(),
            weapon.getRenown(),
            weapon.getWeaponClass().ordinal(),
            weapon.getWeaponType().ordinal(),
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
    public Map<String, Object> toFirebaseMap(MeleWeapon item) {
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
