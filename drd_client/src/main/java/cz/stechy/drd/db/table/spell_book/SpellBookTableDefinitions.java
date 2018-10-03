package cz.stechy.drd.db.table.spell_book;

import static cz.stechy.drd.R.Database.Spells.*;

import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.base.BaseTableDefinitions;
import cz.stechy.drd.model.SpellParser;
import cz.stechy.drd.model.spell.Spell;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Table(clazz = Spell.class, type = Type.DEFINITION)
public class SpellBookTableDefinitions extends BaseTableDefinitions<Spell> {

    // region Constants
    private static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_AUTHOR, COLUMN_NAME,
        COLUMN_MAGIC_NAME, COLUMN_DESCRIPTION, COLUMN_PROFESSION_TYPE, COLUMN_PRICE, COLUMN_RADIUS,
        COLUMN_RANGE, COLUMN_TARGET, COLUMN_CAST_TIME, COLUMN_DURATION, COLUMN_IMAGE,
        COLUMN_UPLOADED};
    static final String COLUMNS_KEYS = GENERATE_COLUMN_KEYS(COLUMNS);
    static final String COLUMNS_VALUES = GENERATE_COLUMNS_VALUES(COLUMNS);
    static final String COLUMNS_UPDATE = GENERATE_COLUMNS_UPDATE(COLUMNS);
    static final String QUERY_CREATE = String.format("CREATE TABLE IF NOT EXISTS %s("
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
            + "); ", TABLE_NAME, COLUMN_ID, COLUMN_AUTHOR, COLUMN_NAME,
        COLUMN_MAGIC_NAME, COLUMN_DESCRIPTION, COLUMN_PROFESSION_TYPE, COLUMN_PRICE, COLUMN_RADIUS,
        COLUMN_RANGE, COLUMN_TARGET, COLUMN_CAST_TIME, COLUMN_DURATION, COLUMN_IMAGE,
        COLUMN_UPLOADED);

    // endregion

    // region Private methods

    @Override
    protected String getColumnKeys() {
        return COLUMNS_KEYS;
    }

    // endregion

    // region Public methods

    @Override
    public Spell parseResultSet(ResultSet resultSet) throws SQLException {
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
    public List<Object> toParamList(Spell item) {
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
    public Spell fromStringMap(Map<String, Object> map) {
        return new Spell.Builder()
            .id((String) map.get(COLUMN_ID))
            .author((String) map.get(COLUMN_AUTHOR))
            .name((String) map.get(COLUMN_NAME))
            .magicName((String) map.get(COLUMN_MAGIC_NAME))
            .description((String) map.get(COLUMN_DESCRIPTION))
            .type((Integer) map.get(COLUMN_PROFESSION_TYPE))
            .price(new SpellParser((String) map.get(COLUMN_PRICE)).parse())
            .radius((Integer) map.get(COLUMN_RADIUS))
            .range((Integer) map.get(COLUMN_RANGE))
            .target((Integer) map.get(COLUMN_TARGET))
            .castTime((Integer) map.get(COLUMN_CAST_TIME))
            .duration((Integer) map.get(COLUMN_DURATION))
            .image(base64ToBlob((String) map.get(COLUMN_IMAGE)))
            .build();
    }

    @Override
    public Map<String, Object> toStringItemMap(Spell item) {
        final Map<String, Object> map = super.toStringItemMap(item);
        map.put(COLUMN_IMAGE, blobToBase64(item.getImage()));
        return map;
    }

    // endregion
}
