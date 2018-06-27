package cz.stechy.drd.dao;

import static cz.stechy.drd.R.Database.Armor.*;

import cz.stechy.drd.R;
import cz.stechy.drd.db.AdvancedDatabaseService;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.model.item.Armor;
import cz.stechy.drd.model.item.ItemType;
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
    private static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION,
        COLUMN_AUTHOR, COLUMN_DEFENCE, COLUMN_MINIMUM_STRENGTH, COLUMN_TYPE, COLUMN_WEIGHT_A,
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
            + "); ", TABLE_NAME, COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_AUTHOR,
        COLUMN_DEFENCE, COLUMN_MINIMUM_STRENGTH, COLUMN_TYPE, COLUMN_WEIGHT_A,
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
    public ArmorDao(Database db, ItemRegistry itemRegistry) {
        super(db);

        itemRegistry.registerItemProvider(this, ItemType.ARMOR);
        OnlineItemRegistry.getINSTANCE().addColection(onlineDatabase);
    }

    // endregion

    // region Private methods

    @Override
    public Armor fromStringItemMap(Map<String, Object> map) {
        return new Armor.Builder()
            .id((String) map.get(COLUMN_ID))
            .name((String) map.get(COLUMN_NAME))
            .description((String) map.get(COLUMN_DESCRIPTION))
            .author((String) map.get(COLUMN_AUTHOR))
            .defenceNumber((Integer) map.get(COLUMN_DEFENCE))
            .minimumStrength((Integer) map.get(COLUMN_MINIMUM_STRENGTH))
            .type((Integer) map.get(COLUMN_TYPE))
            .weightA((Integer) map.get(COLUMN_WEIGHT_A))
            .weightB((Integer) map.get(COLUMN_WEIGHT_B))
            .weightC((Integer) map.get(COLUMN_WEIGHT_C))
            .priceA((Integer) map.get(COLUMN_PRICE_A))
            .priceB((Integer) map.get(COLUMN_PRICE_B))
            .priceC((Integer) map.get(COLUMN_PRICE_C))
            .image(base64ToBlob((String) map.get(COLUMN_IMAGE)))
            .stackSize((Integer) map.get(COLUMN_STACK_SIZE))
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
            .type(resultSet.getInt(COLUMN_TYPE))
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
        return TABLE_NAME;
    }

    @Override
    public String getFirebaseChildName() {
        return R.Database.Armor.FIREBASE_CHILD;
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
    public Map<String, Object> toStringItemMap(Armor item) {
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
