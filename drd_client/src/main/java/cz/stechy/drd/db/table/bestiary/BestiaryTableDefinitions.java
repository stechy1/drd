package cz.stechy.drd.db.table.bestiary;

import static cz.stechy.drd.R.Database.Bestiary.*;

import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.base.BaseTableDefinitions;
import cz.stechy.drd.model.entity.mob.Mob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Table(clazz = Mob.class, type = Type.DEFINITION)
public class BestiaryTableDefinitions extends BaseTableDefinitions<Mob> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(BestiaryTableDefinitions.class);

    private static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION,
        COLUMN_AUTHOR, COLUMN_IMAGE, COLUMN_MOB_CLASS, COLUMN_RULES_TYPE,
        COLUMN_CONVICTION, COLUMN_HEIGHT, COLUMN_ATTACK, COLUMN_DEFENCE, COLUMN_VIABILITY,
        COLUMN_IMMUNITY, COLUMN_METTLE, COLUMN_VULNERABILITY, COLUMN_MOBILITY, COLUMN_PERSERVANCE,
        COLUMN_CONTROL_ABILITY, COLUMN_INTELLIGENCE, COLUMN_CHARISMA, COLUMN_BASIC_POWER_OF_MIND,
        COLUMN_EXPERIENCE, COLUMN_DOMESTICATION, COLUMN_UPLOADED};

    static final String COLUMNS_KEYS = GENERATE_COLUMN_KEYS(COLUMNS);
    static final String COLUMNS_VALUES = GENERATE_COLUMNS_VALUES(COLUMNS);
    static final String COLUMNS_UPDATE = GENERATE_COLUMNS_UPDATE(COLUMNS);

    static final String QUERY_CREATE = String.format("CREATE TABLE IF NOT EXISTS %s("
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
            + "); ", TABLE_NAME, COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION,
        COLUMN_AUTHOR, COLUMN_IMAGE, COLUMN_MOB_CLASS, COLUMN_RULES_TYPE,
        COLUMN_CONVICTION, COLUMN_HEIGHT, COLUMN_ATTACK, COLUMN_DEFENCE, COLUMN_VIABILITY,
        COLUMN_IMMUNITY, COLUMN_METTLE, COLUMN_VULNERABILITY, COLUMN_MOBILITY, COLUMN_PERSERVANCE,
        COLUMN_CONTROL_ABILITY, COLUMN_INTELLIGENCE, COLUMN_CHARISMA, COLUMN_BASIC_POWER_OF_MIND,
        COLUMN_EXPERIENCE, COLUMN_DOMESTICATION, COLUMN_UPLOADED);

    // endregion

    // region Private methods

    @Override
    protected String getColumnKeys() {
        return COLUMNS_KEYS;
    }

    // endregion

    // region Public methods

    @Override
    public Mob parseResultSet(ResultSet resultSet) throws SQLException {
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
            .basicPowerOfMind(resultSet.getInt(COLUMN_BASIC_POWER_OF_MIND))
            .experience(resultSet.getInt(COLUMN_EXPERIENCE))
            .domestication(resultSet.getInt(COLUMN_DOMESTICATION))
            .downloaded(true)
            .uploaded(resultSet.getBoolean(COLUMN_UPLOADED))
            .build();
    }

    @Override
    public List<Object> toParamList(Mob mob) {
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
    public Mob fromStringMap(Map<String, Object> map) {
        return new Mob.Builder()
            .id((String) map.get(COLUMN_ID))
            .name((String) map.get(COLUMN_NAME))
            .description((String) map.get(COLUMN_DESCRIPTION))
            .author((String) map.get(COLUMN_AUTHOR))
            .image(base64ToBlob((String) map.get(COLUMN_IMAGE)))
            .mobClass((Integer) map.get(COLUMN_MOB_CLASS))
            .rulesType((Integer) map.get(COLUMN_RULES_TYPE))
            .conviction((Integer) map.get(COLUMN_CONVICTION))
            .height((Integer) map.get(COLUMN_HEIGHT))
            .attackNumber((Integer) map.get(COLUMN_ATTACK))
            .defenceNumber((Integer) map.get(COLUMN_DEFENCE))
            .viability((Integer) map.get(COLUMN_VIABILITY))
            .immunity((Integer) map.get(COLUMN_IMMUNITY))
            .mettle((Integer) map.get(COLUMN_METTLE))
            .vulnerability((Integer) map.get(COLUMN_VULNERABILITY))
            .mobility((Integer) map.get(COLUMN_MOBILITY))
            .perservance((Integer) map.get(COLUMN_PERSERVANCE))
            .controlAbility((Integer) map.get(COLUMN_CONTROL_ABILITY))
            .intelligence((Integer) map.get(COLUMN_INTELLIGENCE))
            .charisma((Integer) map.get(COLUMN_CHARISMA))
            .basicPowerOfMind((Integer) map.get(COLUMN_BASIC_POWER_OF_MIND))
            .experience((Integer) map.get(COLUMN_EXPERIENCE))
            .domestication((Integer) map.get(COLUMN_DOMESTICATION))
            .build();
    }

    @Override
    public Map<String, Object> toStringItemMap(Mob mob) {
        final Map<String, Object> map = super.toStringItemMap(mob);
        map.put(COLUMN_IMAGE, blobToBase64(mob.getImage()));
        return map;
    }

    // endregion
}
