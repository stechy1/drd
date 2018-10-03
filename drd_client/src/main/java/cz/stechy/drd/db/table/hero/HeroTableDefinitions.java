package cz.stechy.drd.db.table.hero;

import static cz.stechy.drd.R.Database.Hero.COLUMN_AUTHOR;
import static cz.stechy.drd.R.Database.Hero.COLUMN_CHARISMA;
import static cz.stechy.drd.R.Database.Hero.COLUMN_CONVICTION;
import static cz.stechy.drd.R.Database.Hero.COLUMN_DEFENCE_NUMBER;
import static cz.stechy.drd.R.Database.Hero.COLUMN_DESCRIPTION;
import static cz.stechy.drd.R.Database.Hero.COLUMN_DEXTERITY;
import static cz.stechy.drd.R.Database.Hero.COLUMN_EXPERIENCES;
import static cz.stechy.drd.R.Database.Hero.COLUMN_HEIGHT;
import static cz.stechy.drd.R.Database.Hero.COLUMN_ID;
import static cz.stechy.drd.R.Database.Hero.COLUMN_IMMUNITY;
import static cz.stechy.drd.R.Database.Hero.COLUMN_INTELLIGENCE;
import static cz.stechy.drd.R.Database.Hero.COLUMN_LEVEL;
import static cz.stechy.drd.R.Database.Hero.COLUMN_LIVE;
import static cz.stechy.drd.R.Database.Hero.COLUMN_MAG;
import static cz.stechy.drd.R.Database.Hero.COLUMN_MAX_LIVE;
import static cz.stechy.drd.R.Database.Hero.COLUMN_MAX_MAG;
import static cz.stechy.drd.R.Database.Hero.COLUMN_MONEY;
import static cz.stechy.drd.R.Database.Hero.COLUMN_NAME;
import static cz.stechy.drd.R.Database.Hero.COLUMN_PROFESSION;
import static cz.stechy.drd.R.Database.Hero.COLUMN_RACE;
import static cz.stechy.drd.R.Database.Hero.COLUMN_STRENGTH;
import static cz.stechy.drd.R.Database.Hero.TABLE_NAME;

import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.base.BaseTableDefinitions;
import cz.stechy.drd.model.entity.hero.Hero;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Table(clazz = Hero.class, type = Type.DEFINITION)
public class HeroTableDefinitions extends BaseTableDefinitions<Hero> {

    // region Constants

    private static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_AUTHOR,
        COLUMN_DESCRIPTION, COLUMN_CONVICTION, COLUMN_RACE, COLUMN_PROFESSION, COLUMN_LEVEL,
        COLUMN_MONEY, COLUMN_EXPERIENCES, COLUMN_STRENGTH, COLUMN_DEXTERITY, COLUMN_IMMUNITY,
        COLUMN_INTELLIGENCE, COLUMN_CHARISMA, COLUMN_HEIGHT, COLUMN_DEFENCE_NUMBER, COLUMN_LIVE,
        COLUMN_MAX_LIVE, COLUMN_MAG, COLUMN_MAX_MAG};
    static final String COLUMNS_KEYS = GENERATE_COLUMN_KEYS(COLUMNS);
    static final String COLUMNS_VALUES = GENERATE_COLUMNS_VALUES(COLUMNS);
    static final String COLUMNS_UPDATE = GENERATE_COLUMNS_UPDATE(COLUMNS);
    static final String QUERY_CREATE = String.format("CREATE TABLE IF NOT EXISTS %s("
            + "%s VARCHAR PRIMARY KEY NOT NULL UNIQUE,"         // id
            + "%s VARCHAR(255) NOT NULL,"                       // name
            + "%s VARCHAR(255),"                                // author
            + "%s VARCHAR(255),"                                // description
            + "%s INT NOT NULL,"                                // conviction
            + "%s INT NOT NULL,"                                // race
            + "%s INT NOT NULL,"                                // profession
            + "%s INT NOT NULL,"                                // level
            + "%s INT NOT NULL,"                                // money
            + "%s INT NOT NULL,"                                // experiences
            + "%s INT NOT NULL,"                                // strength
            + "%s INT NOT NULL,"                                // dexterity
            + "%s INT NOT NULL,"                                // immunity
            + "%s INT NOT NULL,"                                // intelligence
            + "%s INT NOT NULL,"                                // charisma
            + "%s INT NOT NULL,"                                // height
            + "%s INT NOT NULL,"                                // defence number
            + "%s INT NOT NULL,"                                // baseLive
            + "%s INT NOT NULL,"                                // max baseLive
            + "%s INT NOT NULL,"                                // mag
            + "%s INT NOT NULL"                                 // max mag
            + ");", TABLE_NAME, COLUMN_ID, COLUMN_NAME, COLUMN_AUTHOR, COLUMN_DESCRIPTION,
        COLUMN_CONVICTION, COLUMN_RACE, COLUMN_PROFESSION, COLUMN_LEVEL, COLUMN_MONEY,
        COLUMN_EXPERIENCES, COLUMN_STRENGTH, COLUMN_DEXTERITY, COLUMN_IMMUNITY, COLUMN_INTELLIGENCE,
        COLUMN_CHARISMA, COLUMN_HEIGHT, COLUMN_DEFENCE_NUMBER, COLUMN_LIVE, COLUMN_MAX_LIVE,
        COLUMN_MAG, COLUMN_MAX_MAG);

    // endregion

    // region Public methods

    @Override
    protected String getColumnKeys() {
        return COLUMNS_KEYS;
    }

    @Override
    public Hero parseResultSet(ResultSet resultSet) throws SQLException {
        return new Hero.Builder()
            .id(resultSet.getString(COLUMN_ID))
            .name(resultSet.getString(COLUMN_NAME))
            .author(resultSet.getString(COLUMN_AUTHOR))
            .description(resultSet.getString(COLUMN_DESCRIPTION))
            .conviction(resultSet.getInt(COLUMN_CONVICTION))
            .race(resultSet.getInt(COLUMN_RACE))
            .profession(resultSet.getInt(COLUMN_PROFESSION))
            .level(resultSet.getInt(COLUMN_LEVEL))
            .money(resultSet.getInt(COLUMN_MONEY))
            .experiences(resultSet.getInt(COLUMN_EXPERIENCES))
            .strength(resultSet.getInt(COLUMN_STRENGTH))
            .dexterity(resultSet.getInt(COLUMN_DEXTERITY))
            .immunity(resultSet.getInt(COLUMN_IMMUNITY))
            .intelligence(resultSet.getInt(COLUMN_INTELLIGENCE))
            .charisma(resultSet.getInt(COLUMN_CHARISMA))
            .height(resultSet.getInt(COLUMN_HEIGHT))
            .defenceNumber(resultSet.getInt(COLUMN_DEFENCE_NUMBER))
            .live(resultSet.getInt(COLUMN_LIVE))
            .maxLive(resultSet.getInt(COLUMN_MAX_LIVE))
            .mag(resultSet.getInt(COLUMN_MAG))
            .maxMag(resultSet.getInt(COLUMN_MAX_MAG))
            .build();
    }

    @Override
    public List<Object> toParamList(Hero hero) {
        return new ArrayList<>(Arrays.asList(
            hero.getId(),
            hero.getName(),
            hero.getAuthor(),
            hero.getDescription(),
            hero.getConviction().ordinal(),
            hero.getRace().ordinal(),
            hero.getProfession().ordinal(),
            hero.getLevel(),
            hero.getMoney().getRaw(),
            hero.getExperiences().getActValue().intValue(),
            hero.getStrength().getValue(),
            hero.getDexterity().getValue(),
            hero.getImmunity().getValue(),
            hero.getIntelligence().getValue(),
            hero.getCharisma().getValue(),
            hero.getHeight().ordinal(),
            hero.getDefenceNumber(),
            hero.getLive().getActValue().intValue(),
            hero.getLive().getMaxValue().intValue(),
            hero.getMag().getActValue().intValue(),
            hero.getMag().getMaxValue().intValue()
        ));
    }

    @Override
    public Hero fromStringMap(Map<String, Object> map) {
        return null;
    }

    // endregion

}
