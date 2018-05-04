package cz.stechy.drd.dao;

import com.google.firebase.database.DataSnapshot;
import cz.stechy.drd.db.AdvancedDatabaseService;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.model.entity.mob.Mob;
import cz.stechy.drd.model.entity.mob.Mob.Builder;
import cz.stechy.drd.model.item.Backpack;
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
public class BestiaryDao extends AdvancedDatabaseService<Mob> {

    // region Constants

    // Název tabulky
    public static final String TABLE = "bestiary";
    private static final String FIREBASE_CHILD_NAME = "mobs";

    // Názvy sloupců v databázi
    private static final String COLUMN_ID = TABLE + "_id";
    private static final String COLUMN_NAME = TABLE + "_name";
    private static final String COLUMN_DESCRIPTION = TABLE + "_description";
    private static final String COLUMN_AUTHOR = TABLE + "_author";
    private static final String COLUMN_IMAGE = TABLE + "_image";
    private static final String COLUMN_MOB_CLASS = TABLE + "_mob_class";
    private static final String COLUMN_RULES_TYPE = TABLE + "_rules_type";
    private static final String COLUMN_CONVICTION = TABLE + "_conviction";
    private static final String COLUMN_HEIGHT = TABLE + "_height";
    private static final String COLUMN_ATTACK = TABLE + "_attack";
    private static final String COLUMN_DEFENCE = TABLE + "_defence";
    private static final String COLUMN_VIABILITY = TABLE + "_viability";
    private static final String COLUMN_IMMUNITY = TABLE + "_immunity";
    private static final String COLUMN_METTLE = TABLE + "_mettle";
    private static final String COLUMN_VULNERABILITY = TABLE + "_vulnerability";
    private static final String COLUMN_MOBILITY = TABLE + "_mobility";
    private static final String COLUMN_PERSERVANCE = TABLE + "_perservance";
    private static final String COLUMN_CONTROL_ABILITY = TABLE + "_control_ability";
    private static final String COLUMN_INTELLIGENCE = TABLE + "_intelligence";
    private static final String COLUMN_CHARISMA = TABLE + "_charisma";
    private static final String COLUMN_BACIS_BOWER_OF_MIND = TABLE + "_basic_power_of_mind";
    private static final String COLUMN_EXPERIENCE = TABLE + "_experience";
    private static final String COLUMN_DOMESTICATION = TABLE + "_domestication";
    private static final String COLUMN_UPLOADED = TABLE + "_uploaded";
    private static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION,
        COLUMN_AUTHOR, COLUMN_IMAGE, COLUMN_MOB_CLASS, COLUMN_RULES_TYPE,
        COLUMN_CONVICTION, COLUMN_HEIGHT, COLUMN_ATTACK, COLUMN_DEFENCE, COLUMN_VIABILITY,
        COLUMN_IMMUNITY, COLUMN_METTLE, COLUMN_VULNERABILITY, COLUMN_MOBILITY, COLUMN_PERSERVANCE,
        COLUMN_CONTROL_ABILITY, COLUMN_INTELLIGENCE, COLUMN_CHARISMA, COLUMN_BACIS_BOWER_OF_MIND,
        COLUMN_EXPERIENCE, COLUMN_DOMESTICATION, COLUMN_UPLOADED};
    private static final String COLUMNS_KEYS = GENERATE_COLUMN_KEYS(COLUMNS);
    private static final String COLUMNS_VALUES = GENERATE_COLUMNS_VALUES(COLUMNS);
    private static final String COLUMNS_UPDATE = GENERATE_COLUMNS_UPDATE(COLUMNS);
    private static final String QUERY_CREATE = String.format("CREATE TABLE IF NOT EXISTS %s("
            + "%s VARCHAR(255) PRIMARY KEY NOT NULL UNIQUE,"    // id
            + "%s VARCHAR(255) NOT NULL,"                       // name
            + "%s VARCHAR(255),"                                // description
            + "%s VARCHAR(255) NOT NULL,"                       // autor
            + "%s BLOB,"                                        // image
            + "%s INT NOT NULL,"                                // mob class
            + "%s INT NOT NULL,"                                // rules_type
            + "%s INT NOT NULL,"                                // conviction
            + "%s INT NOT NULL,"                                // height
            + "%s INT NOT NULL,"                                // attack
            + "%s INT NOT NULL,"                                // defence
            + "%s INT NOT NULL,"                                // viability
            + "%s INT NOT NULL,"                                // immunity
            + "%s INT NOT NULL,"                                // mettle
            + "%s INT NOT NULL,"                                // vulnerability
            + "%s INT NOT NULL,"                                // mobility
            + "%s INT NOT NULL,"                                // perservance
            + "%s INT NOT NULL,"                                // control ability
            + "%s INT NOT NULL,"                                // intelligence
            + "%s INT NOT NULL,"                                // charisma
            + "%s INT NOT NULL,"                                // basic power of mind
            + "%s INT NOT NULL,"                                // experience
            + "%s INT NOT NULL,"                                // domestication
            + "%s INT NOT NULL"                                 // uploaded
            + "); ", TABLE, COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION,
        COLUMN_AUTHOR, COLUMN_IMAGE, COLUMN_MOB_CLASS, COLUMN_RULES_TYPE,
        COLUMN_CONVICTION, COLUMN_HEIGHT, COLUMN_ATTACK, COLUMN_DEFENCE, COLUMN_VIABILITY,
        COLUMN_IMMUNITY, COLUMN_METTLE, COLUMN_VULNERABILITY, COLUMN_MOBILITY, COLUMN_PERSERVANCE,
        COLUMN_CONTROL_ABILITY, COLUMN_INTELLIGENCE, COLUMN_CHARISMA, COLUMN_BACIS_BOWER_OF_MIND,
        COLUMN_EXPERIENCE, COLUMN_DOMESTICATION, COLUMN_UPLOADED);

    // endregion

    // region Constructors

    public BestiaryDao(Database db) {
        super(db);
    }

    // endregion

    // region Private methods

    @Override
    public Mob parseDataSnapshot(DataSnapshot snapshot) {
        return new Builder()
            .id(snapshot.child(COLUMN_ID).getValue(String.class))
            .name(snapshot.child(COLUMN_NAME).getValue(String.class))
            .description(snapshot.child(COLUMN_DESCRIPTION).getValue(String.class))
            .author(snapshot.child(COLUMN_AUTHOR).getValue(String.class))
            .image(base64ToBlob(snapshot.child(COLUMN_IMAGE).getValue(String.class)))
            .mobClass(snapshot.child(COLUMN_MOB_CLASS).getValue(Integer.class))
            .rulesType(snapshot.child(COLUMN_RULES_TYPE).getValue(Integer.class))
            .conviction(snapshot.child(COLUMN_CONVICTION).getValue(Integer.class))
            .height(snapshot.child(COLUMN_HEIGHT).getValue(Integer.class))
            .attackNumber(snapshot.child(COLUMN_ATTACK).getValue(Integer.class))
            .defenceNumber(snapshot.child(COLUMN_DEFENCE).getValue(Integer.class))
            .viability(snapshot.child(COLUMN_VIABILITY).getValue(Integer.class))
            .immunity(snapshot.child(COLUMN_IMMUNITY).getValue(Integer.class))
            .mettle(snapshot.child(COLUMN_METTLE).getValue(Integer.class))
            .vulnerability(snapshot.child(COLUMN_VULNERABILITY).getValue(Integer.class))
            .mobility(snapshot.child(COLUMN_MOBILITY).getValue(Integer.class))
            .perservance(snapshot.child(COLUMN_PERSERVANCE).getValue(Integer.class))
            .controlAbility(snapshot.child(COLUMN_CONTROL_ABILITY).getValue(Integer.class))
            .intelligence(snapshot.child(COLUMN_INTELLIGENCE).getValue(Integer.class))
            .charisma(snapshot.child(COLUMN_CHARISMA).getValue(Integer.class))
            .basicPowerOfMind(snapshot.child(COLUMN_BACIS_BOWER_OF_MIND).getValue(Integer.class))
            .experience(snapshot.child(COLUMN_EXPERIENCE).getValue(Integer.class))
            .domestication(snapshot.child(COLUMN_DOMESTICATION).getValue(Integer.class))
            .build();
    }

    @Override
    protected Mob parseResultSet(ResultSet resultSet) throws SQLException {
        return new Mob.Builder()
            .id(resultSet.getString(COLUMN_ID))
            .name(resultSet.getString(COLUMN_NAME))
            .description(resultSet.getString(COLUMN_DESCRIPTION))
            .author(resultSet.getString(COLUMN_AUTHOR))
            .image(readBlob(resultSet, COLUMN_IMAGE))
            .mobClass(resultSet.getInt(COLUMN_MOB_CLASS))
            .rulesType(resultSet.getInt(COLUMN_RULES_TYPE))
            .conviction(resultSet.getInt(COLUMN_CONVICTION))
            .height(resultSet.getInt(COLUMN_HEIGHT))
            .attackNumber(resultSet.getInt(COLUMN_ATTACK))
            .defenceNumber(resultSet.getInt(COLUMN_DEFENCE))
            .viability(resultSet.getInt(COLUMN_VIABILITY))
            .immunity(resultSet.getInt(COLUMN_IMMUNITY))
            .mettle(resultSet.getInt(COLUMN_METTLE))
            .vulnerability(resultSet.getInt(COLUMN_VULNERABILITY))
            .mobility(resultSet.getInt(COLUMN_MOBILITY))
            .perservance(resultSet.getInt(COLUMN_PERSERVANCE))
            .controlAbility(resultSet.getInt(COLUMN_CONTROL_ABILITY))
            .intelligence(resultSet.getInt(COLUMN_INTELLIGENCE))
            .charisma(resultSet.getInt(COLUMN_CHARISMA))
            .basicPowerOfMind(resultSet.getInt(COLUMN_BACIS_BOWER_OF_MIND))
            .experience(resultSet.getInt(COLUMN_EXPERIENCE))
            .domestication(resultSet.getInt(COLUMN_DOMESTICATION))
            .downloaded(true)
            .uploaded(resultSet.getBoolean(COLUMN_UPLOADED))
            .build();
    }

    @Override
    protected List<Object> itemToParams(Mob mob) {
        return new ArrayList<>(Arrays.asList(
            mob.getId(),
            mob.getName(),
            mob.getDescription(),
            mob.getAuthor(),
            mob.getImage(),
            mob.getMobClass().ordinal(),
            mob.getRulesType().ordinal(),
            mob.getConviction().ordinal(),
            mob.getHeight().ordinal(),
            mob.getAttackNumber(),
            mob.getDefenceNumber(),
            mob.getViability(),
            mob.getImmunity().getValue(),
            mob.getMettle(),
            mob.getVulnerability(),
            mob.getMobility(),
            mob.getPerservance(),
            mob.getControlAbility(),
            mob.getIntelligence().getValue(),
            mob.getCharisma().getValue(),
            mob.getBasicPowerOfMind(),
            mob.getExperience(),
            mob.getDomestication(),
            mob.isUploaded()
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
    public Map<String, Object> toFirebaseMap(Mob mob) {
        final Map<String, Object> map = super.toFirebaseMap(mob);
        map.put(COLUMN_IMAGE, blobToBase64(mob.getImage()));
        return map;
    }

    // endregion
}
