package cz.stechy.drd.util;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Pomocní knihovní třída pro překládání textů
 */
public final class Translator {

    // region Constants

    private static final String CONVICTION_LAWFUL_GOOD = "drd_conviction_lawful_good";
    private static final String CONVICTION_CONFUSED_GOODNESS = "drd_conviction_confused_goodness";
    private static final String CONVICTION_NEUTRAL = "drd_conviction_neutral";
    private static final String CONVICTION_CONFUSED_EVIL = "drd_conviction_confused_evil";
    private static final String CONVICTION_LAWFUL_EVIL = "drd_conviction_lawful_evil";
    private static final String[] CONVICTIONS;

    private static final String RACE_HOBIT = "drd_race_hobit";
    private static final String RACE_KUDUK = "drd_race_kuduk";
    private static final String RACE_DWARF = "drd_race_dwarf";
    private static final String RACE_ELF = "drd_race_elf";
    private static final String RACE_HUMAN = "drd_race_human";
    private static final String RACE_BARBAR = "drd_race_barbar";
    private static final String RACE_KROLL = "drd_race_kroll";
    private static final String[] RACES;

    private static final String PROFESSION_WARIOR = "drd_profession_warior";
    private static final String PROFESSION_RANGER = "drd_profession_ranger";
    private static final String PROFESSION_ALCHEMIST = "drd_profession_alchemist";
    private static final String PROFESSION_MAGICIAN = "drd_profession_magician";
    private static final String PROFESSION_THIEF = "drd_profession_thief";
    private static final String[] PROFESSIONS;

    private static final String WEAPON_MELE_CLASS_LIGHT = "drd_item_weapon_mele_class_light";
    private static final String WEAPON_MELE_CLASS_MEDIUM = "drd_item_weapon_mele_class_medium";
    private static final String WEAPON_MELE_CLASS_HEAVY = "drd_item_weapon_mele_class_heavy";
    private static final String[] WEAPON_MELE_CLASSES;

    private static final String WEAPON_MELE_TYPE_ONE_HAND = "drd_item_weapon_mele_type_one_hand";
    private static final String WEAPON_MELE_TYPE_DOUBLE_HAND = "drd_item_weapon_mele_type_double_hand";
    private static final String[] WEAPON_MELE_TYPES;

    private static final String WEAPON_RANGED_TYPE_FIRE = "drd_item_weapon_ranged_type_fire";
    private static final String WEAPON_RANGED_TYPE_PROJECTILE = "drd_item_weapon_ranged_type_projectile";
    private static final String[] WEAPON_RANGED_TYPES;

    private static final String CUSTOM_CONSTANT = "drd_custom_constant";
    private static final String HERO_STRENGTH = "drd_hero_strength";
    private static final String HERO_DEXTERITY = "drd_hero_dexterity";
    private static final String HERO_IMMUNITY = "drd_hero_immunity";
    private static final String HERO_INTELLIGENCE = "drd_hero_intelligence";
    private static final String HERO_CHARISMA = "drd_hero_charisma";
    private static final String[] DICE_ADDITION_PROPERTIES;

    private static final String SHOP_ITEM_WEAPON_MELE = "drd_item_type_weapon_mele";
    private static final String SHOP_ITEM_WEAPON_RANGED = "drd_item_type_weapon_ranged";
    private static final String SHOP_ITEM_ARMOR = "drd_item_type_armor";
    private static final String SHOP_ITEM_GENERAL = "drd_item_type_general";
    private static final String[] SHOP_ITEMS;

    // endregion

    static {
        CONVICTIONS = new String[]{
            CONVICTION_LAWFUL_GOOD,
            CONVICTION_CONFUSED_GOODNESS,
            CONVICTION_NEUTRAL,
            CONVICTION_CONFUSED_EVIL,
            CONVICTION_LAWFUL_EVIL
        };
        RACES = new String[]{
            RACE_HOBIT,
            RACE_KUDUK,
            RACE_DWARF,
            RACE_ELF,
            RACE_HUMAN,
            RACE_BARBAR,
            RACE_KROLL
        };
        PROFESSIONS = new String[]{
            PROFESSION_WARIOR,
            PROFESSION_RANGER,
            PROFESSION_ALCHEMIST,
            PROFESSION_MAGICIAN,
            PROFESSION_THIEF
        };

        WEAPON_MELE_CLASSES = new String[]{
            WEAPON_MELE_CLASS_LIGHT,
            WEAPON_MELE_CLASS_MEDIUM,
            WEAPON_MELE_CLASS_HEAVY
        };
        WEAPON_MELE_TYPES = new String[]{
            WEAPON_MELE_TYPE_ONE_HAND,
            WEAPON_MELE_TYPE_DOUBLE_HAND
        };
        WEAPON_RANGED_TYPES = new String[]{
            WEAPON_RANGED_TYPE_FIRE,
            WEAPON_RANGED_TYPE_PROJECTILE
        };

        DICE_ADDITION_PROPERTIES = new String[]{
            HERO_STRENGTH,
            HERO_DEXTERITY,
            HERO_IMMUNITY,
            HERO_INTELLIGENCE,
            HERO_CHARISMA
        };

        SHOP_ITEMS = new String[]{
            SHOP_ITEM_WEAPON_MELE,
            SHOP_ITEM_WEAPON_RANGED,
            SHOP_ITEM_ARMOR,
            SHOP_ITEM_GENERAL
        };
    }

    // region Variables

    private final ResourceBundle resources;
    private List<String> raceList;
    private List<String> convictionList;
    private List<String> professionList;
    private List<String> weaponMeleClassList;
    private List<String> weaponMeleTypeList;
    private List<String> weaponRangedTypeList;
    private List<String> diceAdditionProertyList;
    private List<String> shopItemList;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou třídu pro překlad kolekcí
     *
     * @param resources Zdroje
     */
    public Translator(ResourceBundle resources) {
        this.resources = resources;
    }

    // endregion

    // region Public methods

    /**
     * @return Přeloženou kolekci pro přesvědčení
     */
    public List<String> getConvictionList() {
        if (convictionList == null) {
            convictionList = Arrays.stream(CONVICTIONS).map(resources::getString)
                .collect(Collectors.toList());
        }

        return convictionList;
    }

    /**
     * @return Přeloženou kolekci pro rasy
     */
    public List<String> getRaceList() {
        if (raceList == null) {
            raceList = Arrays.stream(RACES).map(resources::getString).collect(Collectors.toList());
        }

        return raceList;
    }

    /**
     * @return Přeloženou kolekci pro profese
     */
    public List<String> getProfessionList() {
        if (professionList == null) {
            professionList = Arrays.stream(PROFESSIONS).map(resources::getString)
                .collect(Collectors.toList());
        }

        return professionList;
    }

    /**
     * @return Přeloženou kolekci pro třídy zbraní pro boj z blízka
     */
    public List<String> getWeaponMeleClassList() {
        if (weaponMeleClassList == null) {
            weaponMeleClassList = Arrays.stream(WEAPON_MELE_CLASSES).map(resources::getString)
                .collect(Collectors.toList());
        }

        return weaponMeleClassList;
    }

    /**
     * @return Přeloženou kolekci pro typy zbraní pro boj z blízka
     */
    public List<String> getWeaponMeleTypeList() {
        if (weaponMeleTypeList == null) {
            weaponMeleTypeList = Arrays.stream(WEAPON_MELE_TYPES).map(resources::getString)
                .collect(Collectors.toList());
        }

        return weaponMeleTypeList;
    }

    /**
     * @return Přeloženou kolekci pro typy zbraní pro boj na dálku
     */
    public List<String> getWeaponRangedTypeList() {
        if (weaponRangedTypeList == null) {
            weaponRangedTypeList = Arrays.stream(WEAPON_RANGED_TYPES).map(resources::getString)
                .collect(Collectors.toList());
        }

        return weaponRangedTypeList;
    }

    /**
     * @return Přeloženou kolekci
     */
    public List<String> getDiceAdditionProertyList() {
        if (diceAdditionProertyList == null) {
            diceAdditionProertyList = Arrays.stream(DICE_ADDITION_PROPERTIES)
                .map(resources::getString)
                .collect(Collectors.toList());
        }

        return diceAdditionProertyList;
    }

    /**
     * @return Přeloženou kolekci s typy předmětů v obchodu
     */
    public List<String> getShopTypeList() {
        if (shopItemList == null) {
            shopItemList = Arrays.stream(SHOP_ITEMS).map(resources::getString)
                .collect(Collectors.toList());
        }

        return shopItemList;
    }

    // endregion
}
