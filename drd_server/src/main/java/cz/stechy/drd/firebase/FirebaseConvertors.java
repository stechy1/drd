package cz.stechy.drd.firebase;

import com.google.firebase.database.GenericTypeIndicator;
import cz.stechy.drd.AuthService;
import cz.stechy.drd.R;
import cz.stechy.drd.R.Database.Collectionsitems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pomocná knihovní třída obsahující pomocné metody pro konverzi
 * {@link com.google.firebase.database.DataSnapshot} na {@link java.util.Map}
 */
final class FirebaseConvertors {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseConvertors.class);

    // Kolekce konvertorů
    private static final Map<String, FirebaseConvertor> CONVERTORS = new HashMap<>();

    /**
     * Nalezne {@link FirebaseConvertor} konvertor pro zadaný klíč.
     * Pokud konvertor není nalezen, je vyhozena vyjímka
     *
     * @param key Klíč, pod kterým se nachází konvertor
     * @return {@link FirebaseConvertor}
     */
    static FirebaseConvertor forKey(String key) {
        final FirebaseConvertor convertor = CONVERTORS.get(key);
        if (convertor == null) {
            LOGGER.error("Konvertor pro klíč {} nebyl nalezen", key);
            throw new IllegalArgumentException("Konvertor nebyl nalezen");
        }

        return convertor;
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
        map.put(R.Database.Armor.COLUMN_IMAGE, snapshot.child(R.Database.Armor.COLUMN_IMAGE).getValue(String.class));
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
        map.put(R.Database.Generalitems.COLUMN_IMAGE, snapshot.child(R.Database.Generalitems.COLUMN_IMAGE).getValue(String.class));
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
        map.put(R.Database.Backpack.COLUMN_IMAGE, snapshot.child(R.Database.Backpack.COLUMN_IMAGE).getValue(String.class));
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
        map.put(R.Database.Weaponmele.COLUMN_IMAGE, snapshot.child(R.Database.Weaponmele.COLUMN_IMAGE).getValue(String.class));
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
        map.put(R.Database.Weaponranged.COLUMN_IMAGE, snapshot.child(R.Database.Weaponranged.COLUMN_IMAGE).getValue(String.class));
        map.put(R.Database.Weaponranged.COLUMN_STACK_SIZE, snapshot.child(R.Database.Weaponranged.COLUMN_STACK_SIZE).getValue(Integer.class));
        return map;
    };

    private static final FirebaseConvertor SPELLS_CONVERTOR = snapshot -> {
        final Map<String, Object> map = new HashMap<>();
        map.put(R.Database.Spells.COLUMN_ID, snapshot.child(R.Database.Spells.COLUMN_ID).getValue(String.class));
        map.put(R.Database.Spells.COLUMN_AUTHOR, snapshot.child(R.Database.Spells.COLUMN_AUTHOR).getValue(String.class));
        map.put(R.Database.Spells.COLUMN_NAME, snapshot.child(R.Database.Spells.COLUMN_NAME).getValue(String.class));
        map.put(R.Database.Spells.COLUMN_MAGIC_NAME, snapshot.child(R.Database.Spells.COLUMN_MAGIC_NAME).getValue(String.class));
        map.put(R.Database.Spells.COLUMN_DESCRIPTION, snapshot.child(R.Database.Spells.COLUMN_DESCRIPTION).getValue(String.class));
        map.put(R.Database.Spells.COLUMN_PROFESSION_TYPE, snapshot.child(R.Database.Spells.COLUMN_PROFESSION_TYPE).getValue(Integer.class));
        map.put(R.Database.Spells.COLUMN_PRICE, snapshot.child(R.Database.Spells.COLUMN_PRICE).getValue(String.class));
        map.put(R.Database.Spells.COLUMN_RADIUS, snapshot.child(R.Database.Spells.COLUMN_RADIUS).getValue(Integer.class));
        map.put(R.Database.Spells.COLUMN_RANGE, snapshot.child(R.Database.Spells.COLUMN_RANGE).getValue(Integer.class));
        map.put(R.Database.Spells.COLUMN_TARGET, snapshot.child(R.Database.Spells.COLUMN_TARGET).getValue(Integer.class));
        map.put(R.Database.Spells.COLUMN_CAST_TIME, snapshot.child(R.Database.Spells.COLUMN_CAST_TIME).getValue(Integer.class));
        map.put(R.Database.Spells.COLUMN_DURATION, snapshot.child(R.Database.Spells.COLUMN_DURATION).getValue(Integer.class));
        map.put(R.Database.Spells.COLUMN_IMAGE, snapshot.child(R.Database.Spells.COLUMN_IMAGE).getValue(String.class));
        return map;
    };

    private static final FirebaseConvertor BESTIARY_CONVERTOR = snapshot -> {
        final Map<String, Object> map = new HashMap<>();
        map.put(R.Database.Bestiary.COLUMN_ID, snapshot.child(R.Database.Bestiary.COLUMN_ID).getValue(String.class));
        map.put(R.Database.Bestiary.COLUMN_NAME, snapshot.child(R.Database.Bestiary.COLUMN_NAME).getValue(String.class));
        map.put(R.Database.Bestiary.COLUMN_DESCRIPTION,snapshot.child(R.Database.Bestiary.COLUMN_DESCRIPTION).getValue(String.class));
        map.put(R.Database.Bestiary.COLUMN_AUTHOR, snapshot.child(R.Database.Bestiary.COLUMN_AUTHOR).getValue(String.class));
        map.put(R.Database.Bestiary.COLUMN_IMAGE,snapshot.child(R.Database.Bestiary.COLUMN_IMAGE).getValue(String.class));
        map.put(R.Database.Bestiary.COLUMN_MOB_CLASS,snapshot.child(R.Database.Bestiary.COLUMN_MOB_CLASS).getValue(Integer.class));
        map.put(R.Database.Bestiary.COLUMN_RULES_TYPE,snapshot.child(R.Database.Bestiary.COLUMN_RULES_TYPE).getValue(Integer.class));
        map.put(R.Database.Bestiary.COLUMN_CONVICTION,snapshot.child(R.Database.Bestiary.COLUMN_CONVICTION).getValue(Integer.class));
        map.put(R.Database.Bestiary.COLUMN_HEIGHT, snapshot.child(R.Database.Bestiary.COLUMN_HEIGHT).getValue(Integer.class));
        map.put(R.Database.Bestiary.COLUMN_ATTACK,snapshot.child(R.Database.Bestiary.COLUMN_ATTACK).getValue(Integer.class));
        map.put(R.Database.Bestiary.COLUMN_DEFENCE,snapshot.child(R.Database.Bestiary.COLUMN_DEFENCE).getValue(Integer.class));
        map.put(R.Database.Bestiary.COLUMN_VIABILITY,snapshot.child(R.Database.Bestiary.COLUMN_VIABILITY).getValue(Integer.class));
        map.put(R.Database.Bestiary.COLUMN_IMMUNITY,snapshot.child(R.Database.Bestiary.COLUMN_IMMUNITY).getValue(Integer.class));
        map.put(R.Database.Bestiary.COLUMN_METTLE, snapshot.child(R.Database.Bestiary.COLUMN_METTLE).getValue(Integer.class));
        map.put(R.Database.Bestiary.COLUMN_VULNERABILITY,snapshot.child(R.Database.Bestiary.COLUMN_VULNERABILITY).getValue(Integer.class));
        map.put(R.Database.Bestiary.COLUMN_MOBILITY,snapshot.child(R.Database.Bestiary.COLUMN_MOBILITY).getValue(Integer.class));
        map.put(R.Database.Bestiary.COLUMN_PERSERVANCE,snapshot.child(R.Database.Bestiary.COLUMN_PERSERVANCE).getValue(Integer.class));
        map.put(R.Database.Bestiary.COLUMN_CONTROL_ABILITY,snapshot.child(R.Database.Bestiary.COLUMN_CONTROL_ABILITY).getValue(Integer.class));
        map.put(R.Database.Bestiary.COLUMN_INTELLIGENCE,snapshot.child(R.Database.Bestiary.COLUMN_INTELLIGENCE).getValue(Integer.class));
        map.put(R.Database.Bestiary.COLUMN_CHARISMA,snapshot.child(R.Database.Bestiary.COLUMN_CHARISMA).getValue(Integer.class));
        map.put(R.Database.Bestiary.COLUMN_BASIC_POWER_OF_MIND, snapshot.child(R.Database.Bestiary.COLUMN_BASIC_POWER_OF_MIND).getValue(Integer.class));
        map.put(R.Database.Bestiary.COLUMN_EXPERIENCE,snapshot.child(R.Database.Bestiary.COLUMN_EXPERIENCE).getValue(Integer.class));
        map.put(R.Database.Bestiary.COLUMN_DOMESTICATION,snapshot.child(R.Database.Bestiary.COLUMN_DOMESTICATION).getValue(Integer.class));
        return map;
    };

    private static final FirebaseConvertor USER_CONVERTOR = snapshot -> {
        final Map<String, Object> map = new HashMap<>();
        map.put(AuthService.COLUMN_ID, snapshot.getKey());
        map.put(AuthService.COLUMN_NAME, snapshot.child(AuthService.COLUMN_NAME).getValue(String.class));
        map.put(AuthService.COLUMN_PASSWORD, snapshot.child(AuthService.COLUMN_PASSWORD).getValue(String.class));
        return map;
    };

    private static final FirebaseConvertor ITEM_COLLECTIONS_CONVERTOR = snapshot -> {
        final GenericTypeIndicator<HashMap<String, String>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, String>>() {};
        final Map<String, Object> map = new HashMap<>();
        map.put(Collectionsitems.COLUMN_ID, snapshot.getKey());
        map.put(Collectionsitems.COLUMN_NAME, snapshot.child(Collectionsitems.COLUMN_NAME).getValue(String.class));
        map.put(Collectionsitems.COLUMN_AUTHOR, snapshot.child(Collectionsitems.COLUMN_AUTHOR).getValue(String.class));
        final HashMap<String, String> records = snapshot.child(Collectionsitems.COLUMN_RECORDS)
            .getValue(genericTypeIndicator);
        if (records == null) {
            map.put(Collectionsitems.COLUMN_RECORDS, new ArrayList<>());
        } else {
            map.put(Collectionsitems.COLUMN_RECORDS, new ArrayList<>(records.values()));
        }
        return map;
    };

    static {
        CONVERTORS.put(R.Database.Armor.FIREBASE_CHILD, ARMOR_CONVERTOR);
        CONVERTORS.put(R.Database.Generalitems.FIREBASE_CHILD, GENERAL_CONVERTOR);
        CONVERTORS.put(R.Database.Backpack.FIREBASE_CHILD, BACKPACK_CONVERTOR);
        CONVERTORS.put(R.Database.Weaponmele.FIREBASE_CHILD, WEAPON_MELE_CONVERTOR);
        CONVERTORS.put(R.Database.Weaponranged.FIREBASE_CHILD, WEAPON_RANGED_CONVERTOR);
        CONVERTORS.put(R.Database.Spells.FIREBASE_CHILD, SPELLS_CONVERTOR);
        CONVERTORS.put(R.Database.Bestiary.FIREBASE_CHILD, BESTIARY_CONVERTOR);
        CONVERTORS.put(AuthService.FIREBASE_CHILD, USER_CONVERTOR);
        CONVERTORS.put(R.Database.Collectionsitems.FIREBASE_CHILD, ITEM_COLLECTIONS_CONVERTOR);
    }

}
