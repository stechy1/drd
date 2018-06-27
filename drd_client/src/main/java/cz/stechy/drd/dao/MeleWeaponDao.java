package cz.stechy.drd.dao;

import static cz.stechy.drd.R.Database.Weaponmele.*;

import cz.stechy.drd.R;
import cz.stechy.drd.db.AdvancedDatabaseService;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.model.item.MeleWeapon;
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
 * Služba spravující CRUD operace nad třídou {@link MeleWeapon}
 */
@Singleton
public final class MeleWeaponDao extends AdvancedDatabaseService<MeleWeapon> {

    // region Constants
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
            + "); ", TABLE_NAME, COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_AUTHOR, COLUMN_WEIGHT,
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
    public MeleWeaponDao(Database db, ItemRegistry itemRegistry) {
        super(db);

        itemRegistry.addColection(items);
        OnlineItemRegistry.getINSTANCE().addColection(onlineDatabase);
    }

    // endregion

    // region Private methods

    @Override
    public MeleWeapon fromStringItemMap(Map<String, Object> map) {
        return new MeleWeapon.Builder()
            .id((String) map.get(COLUMN_ID))
            .name((String) map.get(COLUMN_NAME))
            .description((String) map.get(COLUMN_DESCRIPTION))
            .author((String) map.get(COLUMN_AUTHOR))
            .weight((Integer) map.get(COLUMN_WEIGHT))
            .price((Integer) map.get(COLUMN_PRICE))
            .strength((Integer) map.get(COLUMN_STRENGTH))
            .rampancy((Integer) map.get(COLUMN_RAMPANCY))
            .defence((Integer) map.get(COLUMN_DEFENCE))
            .renown((Integer) map.get(COLUMN_RENOWN))
            .weaponClass((Integer) map.get(COLUMN_CLASS))
            .weaponType((Integer) map.get(COLUMN_TYPE))
            .image(base64ToBlob((String) map.get(COLUMN_IMAGE)))
            .stackSize((Integer) map.get(COLUMN_STACK_SIZE))
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
        return TABLE_NAME;
    }

    @Override
    public String getFirebaseChildName() {
        return R.Database.Weaponmele.FIREBASE_CHILD;
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
    public Map<String, Object> toStringItemMap(MeleWeapon item) {
        final Map<String, Object> map = super.toStringItemMap(item);
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
