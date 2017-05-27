package cz.stechy.drd.util;

import cz.stechy.drd.R;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Pomocní knihovní třída pro překládání textů
 */
public final class Translator {

    // region Constants

    private static final String[] CONVICTIONS;
    private static final String[] RACES;
    private static final String[] PROFESSIONS;
    private static final String[] WEAPON_MELE_CLASSES;
    private static final String[] WEAPON_MELE_TYPES;
    private static final String[] WEAPON_RANGED_TYPES;
    private static final String[] DICE_ADDITION_PROPERTIES;
    private static final String[] SHOP_ITEMS;

    // endregion

    static {
        CONVICTIONS = new String[]{
            R.Translate.CONVICTION_LAWFUL_GOOD,
            R.Translate.CONVICTION_CONFUSED_GOODNESS,
            R.Translate.CONVICTION_NEUTRAL,
            R.Translate.CONVICTION_CONFUSED_EVIL,
            R.Translate.CONVICTION_LAWFUL_EVIL
        };
        RACES = new String[]{
            R.Translate.RACE_HOBIT,
            R.Translate.RACE_KUDUK,
            R.Translate.RACE_DWARF,
            R.Translate.RACE_ELF,
            R.Translate.RACE_HUMAN,
            R.Translate.RACE_BARBAR,
            R.Translate.RACE_KROLL
        };
        PROFESSIONS = new String[]{
            R.Translate.PROFESSION_WARIOR,
            R.Translate.PROFESSION_RANGER,
            R.Translate.PROFESSION_ALCHEMIST,
            R.Translate.PROFESSION_MAGICIAN,
            R.Translate.PROFESSION_THIEF
        };

        WEAPON_MELE_CLASSES = new String[]{
            R.Translate.ITEM_WEAPON_MELE_CLASS_LIGHT,
            R.Translate.ITEM_WEAPON_MELE_CLASS_MEDIUM,
            R.Translate.ITEM_WEAPON_MELE_CLASS_HEAVY
        };
        WEAPON_MELE_TYPES = new String[]{
            R.Translate.ITEM_WEAPON_MELE_TYPE_ONE_HAND,
            R.Translate.ITEM_WEAPON_MELE_TYPE_DOUBLE_HAND
        };
        WEAPON_RANGED_TYPES = new String[]{
            R.Translate.ITEM_WEAPON_RANGED_TYPE_FIRE,
            R.Translate.ITEM_WEAPON_RANGED_TYPE_PROJECTILE
        };

        DICE_ADDITION_PROPERTIES = new String[]{
            R.Translate.HERO_STRENGTH,
            R.Translate.HERO_DEXTERITY,
            R.Translate.HERO_IMMUNITY,
            R.Translate.HERO_INTELLIGENCE,
            R.Translate.HERO_CHARISMA
        };

        SHOP_ITEMS = new String[]{
            R.Translate.ITEM_TYPE_WEAPON_MELE,
            R.Translate.ITEM_TYPE_WEAPON_RANGED,
            R.Translate.ITEM_TYPE_ARMOR,
            R.Translate.ITEM_TYPE_GENERAL,
            R.Translate.ITEM_TYPE_BACKPACK
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
