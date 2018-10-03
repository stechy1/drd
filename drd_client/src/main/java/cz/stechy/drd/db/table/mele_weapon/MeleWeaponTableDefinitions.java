package cz.stechy.drd.db.table.mele_weapon;

import static cz.stechy.drd.R.Database.Weaponmele.COLUMN_AUTHOR;
import static cz.stechy.drd.R.Database.Weaponmele.COLUMN_CLASS;
import static cz.stechy.drd.R.Database.Weaponmele.COLUMN_DEFENCE;
import static cz.stechy.drd.R.Database.Weaponmele.COLUMN_DESCRIPTION;
import static cz.stechy.drd.R.Database.Weaponmele.COLUMN_ID;
import static cz.stechy.drd.R.Database.Weaponmele.COLUMN_IMAGE;
import static cz.stechy.drd.R.Database.Weaponmele.COLUMN_NAME;
import static cz.stechy.drd.R.Database.Weaponmele.COLUMN_PRICE;
import static cz.stechy.drd.R.Database.Weaponmele.COLUMN_RAMPANCY;
import static cz.stechy.drd.R.Database.Weaponmele.COLUMN_RENOWN;
import static cz.stechy.drd.R.Database.Weaponmele.COLUMN_STACK_SIZE;
import static cz.stechy.drd.R.Database.Weaponmele.COLUMN_STRENGTH;
import static cz.stechy.drd.R.Database.Weaponmele.COLUMN_TYPE;
import static cz.stechy.drd.R.Database.Weaponmele.COLUMN_UPLOADED;
import static cz.stechy.drd.R.Database.Weaponmele.COLUMN_WEIGHT;
import static cz.stechy.drd.R.Database.Weaponmele.TABLE_NAME;

import cz.stechy.drd.db.base.BaseTableDefinitions;
import cz.stechy.drd.model.item.MeleWeapon;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MeleWeaponTableDefinitions extends BaseTableDefinitions<MeleWeapon> {

    // region Constants

    private static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION,
        COLUMN_AUTHOR, COLUMN_WEIGHT, COLUMN_PRICE, COLUMN_STRENGTH, COLUMN_RAMPANCY,
        COLUMN_DEFENCE, COLUMN_RENOWN, COLUMN_CLASS, COLUMN_TYPE, COLUMN_IMAGE, COLUMN_STACK_SIZE,
        COLUMN_UPLOADED};
    static final String COLUMNS_KEYS = GENERATE_COLUMN_KEYS(COLUMNS);
    static final String COLUMNS_VALUES = GENERATE_COLUMNS_VALUES(COLUMNS);
    static final String COLUMNS_UPDATE = GENERATE_COLUMNS_UPDATE(COLUMNS);
    static final String QUERY_CREATE = String.format("CREATE TABLE IF NOT EXISTS %s("
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

    // region Public methods

    @Override
    protected String getColumnKeys() {
        return COLUMNS_KEYS;
    }

    @Override
    public MeleWeapon parseResultSet(ResultSet resultSet) throws SQLException {
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
    public List<Object> toParamList(MeleWeapon weapon) {
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
    public MeleWeapon fromStringMap(Map<String, Object> map) {
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
    public Map<String, Object> toStringItemMap(MeleWeapon item) {
        final Map<String, Object> map = super.toStringItemMap(item);
        map.put(COLUMN_IMAGE, blobToBase64(item.getImage()));
        return map;
    }

    // endregion

}
