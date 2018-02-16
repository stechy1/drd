package cz.stechy.drd.model.dao;

import com.google.firebase.database.DataSnapshot;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.model.db.AdvancedDatabaseService;
import cz.stechy.drd.model.db.base.Database;
import cz.stechy.drd.model.spell.Spell;
import cz.stechy.drd.model.spell.parser.SpellParser;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Služba spravující CRUD operace nad třídou {@link Spell}
 */
@Singleton
public class SpellBookDao extends AdvancedDatabaseService<Spell> {

    // region Constants

    // Název tabulky
    private static final String TABLE = "spel";
    private static final String FIREBASE_CHILD_NAME = "spells";

    // Názvy sloupců v databázi
    private static final String COLUMN_ID = TABLE + "_id";
    private static final String COLUMN_AUTHOR = TABLE + "_author";
    private static final String COLUMN_NAME = TABLE + "_name";
    private static final String COLUMN_MAGIC_NAME = TABLE + "_magic_name";
    private static final String COLUMN_DESCRIPTION = TABLE + "_description";
    private static final String COLUMN_PROFESSION_TYPE = TABLE + "_profession_type";
    private static final String COLUMN_PRICE = TABLE + "_price";
    private static final String COLUMN_RADIUS = TABLE + "_radius";
    private static final String COLUMN_RANGE = TABLE + "_range";
    private static final String COLUMN_TARGET = TABLE + "_target";
    private static final String COLUMN_CAST_TIME = TABLE + "_cast_time";
    private static final String COLUMN_DURATION = TABLE + "_duration";
    private static final String COLUMN_IMAGE = TABLE + "_image";
    private static final String COLUMN_UPLOADED = TABLE + "_uploaded";
    private static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_AUTHOR, COLUMN_NAME,
        COLUMN_MAGIC_NAME, COLUMN_DESCRIPTION, COLUMN_PROFESSION_TYPE, COLUMN_PRICE, COLUMN_RADIUS,
        COLUMN_RANGE, COLUMN_TARGET, COLUMN_CAST_TIME, COLUMN_DURATION, COLUMN_IMAGE,
        COLUMN_UPLOADED};
    private static final String COLUMNS_KEYS = GENERATE_COLUMN_KEYS(COLUMNS);
    private static final String COLUMNS_VALUES = GENERATE_COLUMNS_VALUES(COLUMNS);
    private static final String COLUMNS_UPDATE = GENERATE_COLUMNS_UPDATE(COLUMNS);
    private static final String QUERY_CREATE = String.format("CREATE TABLE IF NOT EXISTS %s("
            + "%s VARCHAR(255) PRIMARY KEY NOT NULL UNIQUE,"    // id
            + "%s VARCHAR(255) NOT NULL,"                       // autor
            + "%s VARCHAR(255) NOT NULL,"                       // name
            + "%s VARCHAR(255) NOT NULL,"                       // magic name
            + "%s VARCHAR(255),"                                // description
            + "%s INT NOT NULL,"                                // profession type
            + "%s VARCHAR(255) NOT NULL,"                       // price
            + "%s INT NOT NULL,"                                // radius
            + "%s INT NOT NULL,"                                // range
            + "%s INT NOT NULL,"                                // target
            + "%s INT NOT NULL,"                                // cast time
            + "%s INT NOT NULL,"                                // duration
            + "%s BLOB,"                                        // image
            + "%s BOOLEAN NOT NULL"                             // je položka nahraná
            + "); ", TABLE, COLUMN_ID, COLUMN_AUTHOR, COLUMN_NAME,
        COLUMN_MAGIC_NAME, COLUMN_DESCRIPTION, COLUMN_PROFESSION_TYPE, COLUMN_PRICE, COLUMN_RADIUS,
        COLUMN_RANGE, COLUMN_TARGET, COLUMN_CAST_TIME, COLUMN_DURATION, COLUMN_IMAGE,
        COLUMN_UPLOADED);

    // endregion

    // region Variables

    private static boolean tableInitialized;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou službu starající se o kouzla
     *
     * @param db {@link Database}
     */
    public SpellBookDao(Database db) {
        super(db);
    }

    // endregion

    // region Private methods

    @Override
    public Spell parseDataSnapshot(DataSnapshot snapshot) {
        return new Spell.Builder()
            .id(snapshot.child(COLUMN_ID).getValue(String.class))
            .author(snapshot.child(COLUMN_AUTHOR).getValue(String.class))
            .name(snapshot.child(COLUMN_NAME).getValue(String.class))
            .magicName(snapshot.child(COLUMN_MAGIC_NAME).getValue(String.class))
            .description(snapshot.child(COLUMN_DESCRIPTION).getValue(String.class))
            .type(snapshot.child(COLUMN_PROFESSION_TYPE).getValue(Integer.class))
            .price(new SpellParser(snapshot.child(COLUMN_PRICE).getValue(String.class)).parse())
            .radius(snapshot.child(COLUMN_RADIUS).getValue(Integer.class))
            .range(snapshot.child(COLUMN_RANGE).getValue(Integer.class))
            .target(snapshot.child(COLUMN_TARGET).getValue(Integer.class))
            .castTime(snapshot.child(COLUMN_CAST_TIME).getValue(Integer.class))
            .duration(snapshot.child(COLUMN_DURATION).getValue(Integer.class))
            .image(base64ToBlob(snapshot.child(COLUMN_IMAGE).getValue(String.class)))
            .build();
    }

    @Override
    protected Spell parseResultSet(ResultSet resultSet) throws SQLException {
        return new Spell.Builder()
            .id(resultSet.getString(COLUMN_ID))
            .author(resultSet.getString(COLUMN_AUTHOR))
            .name(resultSet.getString(COLUMN_NAME))
            .magicName(resultSet.getString(COLUMN_MAGIC_NAME))
            .description(resultSet.getString(COLUMN_DESCRIPTION))
            .type(resultSet.getInt(COLUMN_PROFESSION_TYPE))
            .price(new SpellParser(resultSet.getString(COLUMN_PRICE)).parse())
            .radius(resultSet.getInt(COLUMN_RADIUS))
            .range(resultSet.getInt(COLUMN_RANGE))
            .target(resultSet.getInt(COLUMN_TARGET))
            .castTime(resultSet.getInt(COLUMN_CAST_TIME))
            .duration(resultSet.getInt(COLUMN_DURATION))
            .image(readBlob(resultSet, COLUMN_IMAGE))
            .downloaded(true)
            .uploaded(resultSet.getBoolean(COLUMN_UPLOADED))
            .build();
    }

    @Override
    protected List<Object> itemToParams(Spell item) {
        return new ArrayList<>(Arrays.asList(
            item.getId(),
            item.getAuthor(),
            item.getName(),
            item.getMagicName(),
            item.getDescription(),
            item.getType().ordinal(),
            item.getPrice().pack(),
            item.getRadius(),
            item.getRange(),
            item.getTarget().ordinal(),
            item.getCastTime(),
            item.getDuration(),
            item.getImage(),
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
    public Map<String, Object> toFirebaseMap(Spell item) {
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
