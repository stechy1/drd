package cz.stechy.drd.firebase;

import cz.stechy.drd.R;
import cz.stechy.drd.R.Database.Spellbook;
import cz.stechy.drd.model.SpellParser;
import cz.stechy.drd.model.item.ItemType;
import cz.stechy.drd.util.Base64Util;
import java.util.HashMap;
import java.util.Map;

/**
 * Pomocná knihovní třída obsahující pomocné metody pro konverzi
 * {@link com.google.firebase.database.DataSnapshot} na {@link java.util.Map}
 */
final class FirebaseConvertors {

    private static final Map<String, FirebaseConvertor> CONVERTORS = new HashMap<>();

    static FirebaseConvertor forKey(String key) {
        final FirebaseConvertor convertor = CONVERTORS.get(key);
        if (convertor == null) {
            throw new IllegalArgumentException("Konvertor nebyl nalezen");
        }

        return convertor;
    }

    /**
     * Převede formát Base64 na pole bytu
     *
     * @param source Base64
     * @return Pole bytu
     */
    private static byte[] base64ToBlob(String source) {
        return Base64Util.decode(source);
    }

    /**
     * Převede poly bytu na Base64
     *
     * @param source Pole bytu
     * @return Base64
     */
    private static String blobToBase64(byte[] source) {
        return Base64Util.encode(source);
    }

    private static final FirebaseConvertor ARMOR_CONVERTOR = snapshot -> {
        final Map<String, Object> map = new HashMap<>();
        map.put(R.Database.Armor.COLUMN_ID, snapshot.child(R.Database.Armor.COLUMN_ID).getValue(String.class));
        map.put(R.Database.Armor.COLUMN_NAME, snapshot.child(R.Database.Armor.COLUMN_NAME).getValue(String.class));
        map.put(R.Database.Armor.COLUMN_DESCRIPTION, snapshot.child(R.Database.Armor.COLUMN_DESCRIPTION).getValue(String.class));
        map.put(R.Database.Armor.COLUMN_AUTHOR, snapshot.child(R.Database.Armor.COLUMN_AUTHOR).getValue(String.class));
        map.put(R.Database.Armor.COLUMN_DEFENCE, snapshot.child(R.Database.Armor.COLUMN_DEFENCE).getValue(Integer.class));
        map.put(R.Database.Armor.COLUMN_MINIMUM_STRENGTH, snapshot.child(R.Database.Armor.COLUMN_MINIMUM_STRENGTH).getValue(Integer.class));
        map.put(R.Database.Armor.COLUMN_TYPE, snapshot.child(R.Database.Armor.COLUMN_TYPE).getValue(Integer.class));
        map.put(R.Database.Armor.COLUMN_WEIGHT_A, snapshot.child(R.Database.Armor.COLUMN_WEIGHT_A).getValue(Integer.class));
        map.put(R.Database.Armor.COLUMN_WEIGHT_B, snapshot.child(R.Database.Armor.COLUMN_WEIGHT_B).getValue(Integer.class));
        map.put(R.Database.Armor.COLUMN_WEIGHT_C, snapshot.child(R.Database.Armor.COLUMN_WEIGHT_C).getValue(Integer.class));
        map.put(R.Database.Armor.COLUMN_PRICE_A, snapshot.child(R.Database.Armor.COLUMN_PRICE_A).getValue(Integer.class));
        map.put(R.Database.Armor.COLUMN_PRICE_B, snapshot.child(R.Database.Armor.COLUMN_PRICE_B).getValue(Integer.class));
        map.put(R.Database.Armor.COLUMN_PRICE_C, snapshot.child(R.Database.Armor.COLUMN_PRICE_C).getValue(Integer.class));
        map.put(R.Database.Armor.COLUMN_IMAGE, base64ToBlob(snapshot.child(R.Database.Armor.COLUMN_IMAGE).getValue(String.class)));
        map.put(R.Database.Armor.COLUMN_STACK_SIZE, snapshot.child(R.Database.Armor.COLUMN_STACK_SIZE).getValue(Integer.class));
        return map;
    };

    private static final FirebaseConvertor GENERAL_CONVERTOR = snapshot -> {
        final Map<String, Object> map = new HashMap<>();
        map.put(R.Database.Generalitems.COLUMN_ID, snapshot.child(R.Database.Generalitems.COLUMN_ID).getValue(String.class));
        map.put(R.Database.Generalitems.COLUMN_NAME, snapshot.child(R.Database.Generalitems.COLUMN_NAME).getValue(String.class));
        map.put(R.Database.Generalitems.COLUMN_DESCRIPTION, snapshot.child(R.Database.Generalitems.COLUMN_DESCRIPTION).getValue(String.class));
        map.put(R.Database.Generalitems.COLUMN_AUTHOR, snapshot.child(R.Database.Generalitems.COLUMN_AUTHOR).getValue(String.class));
        map.put(R.Database.Generalitems.COLUMN_WEIGHT, snapshot.child(R.Database.Generalitems.COLUMN_WEIGHT).getValue(Integer.class));
        map.put(R.Database.Generalitems.COLUMN_PRICE, snapshot.child(R.Database.Generalitems.COLUMN_PRICE).getValue(Integer.class));
        map.put(R.Database.Generalitems.COLUMN_IMAGE, base64ToBlob(snapshot.child(R.Database.Generalitems.COLUMN_IMAGE).getValue(String.class)));
        map.put(R.Database.Generalitems.COLUMN_STACK_SIZE, snapshot.child(R.Database.Generalitems.COLUMN_STACK_SIZE).getValue(Integer.class));
        return map;
    };

    private static final FirebaseConvertor BACKPACK_CONVERTOR = snapshot -> {
        final Map<String, Object> map = new HashMap<>();
        map.put(R.Database.Backpack.COLUMN_ID, snapshot.child(R.Database.Backpack.COLUMN_ID).getValue(String.class));
        map.put(R.Database.Backpack.COLUMN_NAME, snapshot.child(R.Database.Backpack.COLUMN_NAME).getValue(String.class));
        map.put(R.Database.Backpack.COLUMN_DESCRIPTION, snapshot.child(R.Database.Backpack.COLUMN_DESCRIPTION).getValue(String.class));
        map.put(R.Database.Backpack.COLUMN_AUTHOR, snapshot.child(R.Database.Backpack.COLUMN_AUTHOR).getValue(String.class));
        map.put(R.Database.Backpack.COLUMN_WEIGHT, snapshot.child(R.Database.Backpack.COLUMN_WEIGHT).getValue(Integer.class));
        map.put(R.Database.Backpack.COLUMN_PRICE, snapshot.child(R.Database.Backpack.COLUMN_PRICE).getValue(Integer.class));
        map.put(R.Database.Backpack.COLUMN_MAX_LOAD, snapshot.child(R.Database.Backpack.COLUMN_MAX_LOAD).getValue(Integer.class));
        map.put(R.Database.Backpack.COLUMN_SIZE, snapshot.child(R.Database.Backpack.COLUMN_SIZE).getValue(Integer.class));
        map.put(R.Database.Backpack.COLUMN_IMAGE, base64ToBlob(snapshot.child(R.Database.Backpack.COLUMN_IMAGE).getValue(String.class)));
        map.put(R.Database.Backpack.COLUMN_STACK_SIZE, snapshot.child(R.Database.Backpack.COLUMN_STACK_SIZE).getValue(Integer.class));
        return map;
    };

    private static final FirebaseConvertor WEAPON_MELE_CONVERTOR = snapshot -> {
        final Map<String, Object> map = new HashMap<>();
        map.put(R.Database.Weaponmele.COLUMN_ID, snapshot.child(R.Database.Weaponmele.COLUMN_ID).getValue(String.class));
        map.put(R.Database.Weaponmele.COLUMN_NAME, snapshot.child(R.Database.Weaponmele.COLUMN_NAME).getValue(String.class));
        map.put(R.Database.Weaponmele.COLUMN_DESCRIPTION, snapshot.child(R.Database.Weaponmele.COLUMN_DESCRIPTION).getValue(String.class));
        map.put(R.Database.Weaponmele.COLUMN_AUTHOR, snapshot.child(R.Database.Weaponmele.COLUMN_AUTHOR).getValue(String.class));
        map.put(R.Database.Weaponmele.COLUMN_WEIGHT, snapshot.child(R.Database.Weaponmele.COLUMN_WEIGHT).getValue(Integer.class));
        map.put(R.Database.Weaponmele.COLUMN_PRICE, snapshot.child(R.Database.Weaponmele.COLUMN_PRICE).getValue(Integer.class));
        map.put(R.Database.Weaponmele.COLUMN_STRENGTH, snapshot.child(R.Database.Weaponmele.COLUMN_STRENGTH).getValue(Integer.class));
        map.put(R.Database.Weaponmele.COLUMN_RAMPANCY, snapshot.child(R.Database.Weaponmele.COLUMN_RAMPANCY).getValue(Integer.class));
        map.put(R.Database.Weaponmele.COLUMN_DEFENCE, snapshot.child(R.Database.Weaponmele.COLUMN_DEFENCE).getValue(Integer.class));
        map.put(R.Database.Weaponmele.COLUMN_RENOWN, snapshot.child(R.Database.Weaponmele.COLUMN_RENOWN).getValue(Integer.class));
        map.put(R.Database.Weaponmele.COLUMN_CLASS, snapshot.child(R.Database.Weaponmele.COLUMN_CLASS).getValue(Integer.class));
        map.put(R.Database.Weaponmele.COLUMN_TYPE, snapshot.child(R.Database.Weaponmele.COLUMN_TYPE).getValue(Integer.class));
        map.put(R.Database.Weaponmele.COLUMN_IMAGE, base64ToBlob(snapshot.child(R.Database.Weaponmele.COLUMN_IMAGE).getValue(String.class)));
        map.put(R.Database.Weaponmele.COLUMN_STACK_SIZE, snapshot.child(R.Database.Weaponmele.COLUMN_STACK_SIZE).getValue(Integer.class));
        return map;
    };

    private static final FirebaseConvertor WEAPON_RANGED_CONVERTOR = snapshot -> {
        final Map<String, Object> map = new HashMap<>();
        map.put(R.Database.Weaponranged.COLUMN_ID, snapshot.child(R.Database.Weaponranged.COLUMN_ID).getValue(String.class));
        map.put(R.Database.Weaponranged.COLUMN_NAME, snapshot.child(R.Database.Weaponranged.COLUMN_NAME).getValue(String.class));
        map.put(R.Database.Weaponranged.COLUMN_DESCRIPTION, snapshot.child(R.Database.Weaponranged.COLUMN_DESCRIPTION).getValue(String.class));
        map.put(R.Database.Weaponranged.COLUMN_AUTHOR, snapshot.child(R.Database.Weaponranged.COLUMN_AUTHOR).getValue(String.class));
        map.put(R.Database.Weaponranged.COLUMN_WEIGHT, snapshot.child(R.Database.Weaponranged.COLUMN_WEIGHT).getValue(Integer.class));
        map.put(R.Database.Weaponranged.COLUMN_PRICE, snapshot.child(R.Database.Weaponranged.COLUMN_PRICE).getValue(Integer.class));
        map.put(R.Database.Weaponranged.COLUMN_STRENGTH, snapshot.child(R.Database.Weaponranged.COLUMN_STRENGTH).getValue(Integer.class));
        map.put(R.Database.Weaponranged.COLUMN_RAMPANCY, snapshot.child(R.Database.Weaponranged.COLUMN_RAMPANCY).getValue(Integer.class));
        map.put(R.Database.Weaponranged.COLUMN_TYPE, snapshot.child(R.Database.Weaponranged.COLUMN_TYPE).getValue(Integer.class));
        map.put(R.Database.Weaponranged.COLUMN_RANGE_LOW, snapshot.child(R.Database.Weaponranged.COLUMN_RANGE_LOW).getValue(Integer.class));
        map.put(R.Database.Weaponranged.COLUMN_RANGE_MEDIUM, snapshot.child(R.Database.Weaponranged.COLUMN_RANGE_MEDIUM).getValue(Integer.class));
        map.put(R.Database.Weaponranged.COLUMN_RANGE_LONG, snapshot.child(R.Database.Weaponranged.COLUMN_RANGE_LONG).getValue(Integer.class));
        map.put(R.Database.Weaponranged.COLUMN_RENOWN, snapshot.child(R.Database.Weaponranged.COLUMN_RENOWN).getValue(Integer.class));
        map.put(R.Database.Weaponranged.COLUMN_IMAGE, base64ToBlob(snapshot.child(R.Database.Weaponranged.COLUMN_IMAGE).getValue(String.class)));
        map.put(R.Database.Weaponranged.COLUMN_STACK_SIZE, snapshot.child(R.Database.Weaponranged.COLUMN_STACK_SIZE).getValue(Integer.class));
        return map;
    };

    private static final FirebaseConvertor SPELLBOOK_CONVERTOR = snapshot -> {
        final Map<String, Object> map = new HashMap<>();
        map.put(R.Database.Spellbook.COLUMN_ID, snapshot.child(R.Database.Spellbook.COLUMN_ID).getValue(String.class));
        map.put(R.Database.Spellbook.COLUMN_AUTHOR, snapshot.child(R.Database.Spellbook.COLUMN_AUTHOR).getValue(String.class));
        map.put(R.Database.Spellbook.COLUMN_NAME, snapshot.child(R.Database.Spellbook.COLUMN_NAME).getValue(String.class));
        map.put(R.Database.Spellbook.COLUMN_MAGIC_NAME, snapshot.child(R.Database.Spellbook.COLUMN_MAGIC_NAME).getValue(String.class));
        map.put(R.Database.Spellbook.COLUMN_DESCRIPTION, snapshot.child(R.Database.Spellbook.COLUMN_DESCRIPTION).getValue(String.class));
        map.put(R.Database.Spellbook.COLUMN_PROFESSION_TYPE, snapshot.child(R.Database.Spellbook.COLUMN_PROFESSION_TYPE).getValue(Integer.class));
        map.put(R.Database.Spellbook.COLUMN_PRICE, new SpellParser(snapshot.child(R.Database.Spellbook.COLUMN_PRICE).getValue(String.class)).parse());
        map.put(R.Database.Spellbook.COLUMN_RADIUS, snapshot.child(R.Database.Spellbook.COLUMN_RADIUS).getValue(Integer.class));
        map.put(R.Database.Spellbook.COLUMN_RANGE, snapshot.child(R.Database.Spellbook.COLUMN_RANGE).getValue(Integer.class));
        map.put(R.Database.Spellbook.COLUMN_TARGET, snapshot.child(R.Database.Spellbook.COLUMN_TARGET).getValue(Integer.class));
        map.put(R.Database.Spellbook.COLUMN_CAST_TIME, snapshot.child(R.Database.Spellbook.COLUMN_CAST_TIME).getValue(Integer.class));
        map.put(R.Database.Spellbook.COLUMN_DURATION, snapshot.child(R.Database.Spellbook.COLUMN_DURATION).getValue(Integer.class));
        map.put(R.Database.Spellbook.COLUMN_IMAGE, base64ToBlob(snapshot.child(R.Database.Spellbook.COLUMN_IMAGE).getValue(String.class)));
        return map;
    };

    static {
        CONVERTORS.put(ItemType.ARMOR.path, ARMOR_CONVERTOR);
        CONVERTORS.put(ItemType.GENERAL.path, GENERAL_CONVERTOR);
        CONVERTORS.put(ItemType.BACKPACK.path, BACKPACK_CONVERTOR);
        CONVERTORS.put(ItemType.WEAPON_MELE.path, WEAPON_MELE_CONVERTOR);
        CONVERTORS.put(ItemType.WEAPON_RANGED.path, WEAPON_RANGED_CONVERTOR);
        CONVERTORS.put(Spellbook.TABLE_NAME, SPELLBOOK_CONVERTOR);
    }

}
