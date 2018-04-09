package cz.stechy.drd.util;

import cz.stechy.drd.R;
import cz.stechy.drd.app.dice.DiceHelper.AdditionType;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.model.Rule;
import cz.stechy.drd.model.entity.Conviction;
import cz.stechy.drd.model.entity.Vulnerabilities.VulnerabilityType;
import cz.stechy.drd.model.entity.hero.Hero.Profession;
import cz.stechy.drd.model.entity.hero.Hero.Race;
import cz.stechy.drd.model.entity.hero.profession.Ranger.Terrain;
import cz.stechy.drd.model.entity.hero.profession.Thief;
import cz.stechy.drd.model.entity.mob.Mob.MobClass;
import cz.stechy.drd.model.item.Armor;
import cz.stechy.drd.model.item.Backpack;
import cz.stechy.drd.model.item.MeleWeapon;
import cz.stechy.drd.model.item.RangedWeapon;
import cz.stechy.drd.model.spell.Spell.SpellProfessionType;
import cz.stechy.drd.model.spell.Spell.SpellTarget;
import cz.stechy.drd.model.spell.price.VariableSpellPrice.VariableType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.util.StringConverter;

/**
 * Pomocní knihovní třída pro překládání textů
 */
@Singleton
public final class Translator {

    // region Constants

    private static final String[] CONVICTIONS_VALUES;
    private static final String[] RACES_VALUES;
    private static final String[] PROFESSIONS_VALUES;
    private static final String[] WEAPON_MELE_CLASSES_VALUES;
    private static final String[] WEAPON_MELE_TYPES_VALUES;
    private static final String[] WEAPON_RANGED_TYPES_VALUES;
    private static final String[] ARMOR_TYPES_VALUES;
    private static final String[] DICE_ADDITION_PROPERTIES_VALUES;
    private static final String[] SHOP_ITEMS_VALUES;
    private static final String[] BACKPACK_SIZES_VALUES;
    private static final String[] RULES_VALUES;
    private static final String[] MOB_CLASSES_VALUES;
    private static final String[] VULNERABILITIES_VALUES;
    private static final String[] THIEF_ABILITY_VALUES;
    private static final String[] TERRAIN_DIFICULTY_VALUES;
    private static final String[] SPELL_PROFESSION_TYPES_VALUES;
    private static final String[] SPELL_TARGET_TYPES_VALUES;
    private static final String[] SPELL_VARIABLE_TYPES_VALUES;

    public enum Key {
        CONVICTIONS(CONVICTIONS_VALUES, Conviction.class),
        RACES(RACES_VALUES, Race.class),
        PROFESSIONS(PROFESSIONS_VALUES, Profession.class),
        WEAPON_MELE_CLASSES(WEAPON_MELE_CLASSES_VALUES, MeleWeapon.MeleWeaponClass.class),
        WEAPON_MELE_TYPES(WEAPON_MELE_TYPES_VALUES, MeleWeapon.MeleWeaponType.class),
        WEAPON_RANGED_TYPES(WEAPON_RANGED_TYPES_VALUES, RangedWeapon.RangedWeaponType.class),
        ARMOR_TYPES(ARMOR_TYPES_VALUES, Armor.ArmorType.class),
        DICE_ADDITION_PROPERTIES(DICE_ADDITION_PROPERTIES_VALUES, AdditionType.class),
        SHOP_ITEMS(SHOP_ITEMS_VALUES, null),
        BACKPACK_SIZES(BACKPACK_SIZES_VALUES, Backpack.Size.class),
        RULES(RULES_VALUES, Rule.class),
        MOB_CLASSES(MOB_CLASSES_VALUES, MobClass.class),
        VULNERABILITIES(VULNERABILITIES_VALUES, VulnerabilityType.class),
        THIEF_ABILITIES(THIEF_ABILITY_VALUES, Thief.Ability.class),
        TERRAIN_DIFICULTY(TERRAIN_DIFICULTY_VALUES, Terrain.class),
        SPELL_PROFESSION_TYPES(SPELL_PROFESSION_TYPES_VALUES, SpellProfessionType.class),
        SPELL_TARGET_TYPES(SPELL_TARGET_TYPES_VALUES, SpellTarget.class),
        SPELL_VARIABLE_TYPES(SPELL_VARIABLE_TYPES_VALUES, VariableType.class);

        private final String[] values;
        public final Class<? extends Enum> enumType;

        Key(String[] values, Class<? extends Enum> enumType) {
            this.values = values;
            this.enumType = enumType;
        }
    }

    // endregion

    static {
        CONVICTIONS_VALUES = new String[]{
            R.Translate.CONVICTION_LAWFUL_GOOD,
            R.Translate.CONVICTION_CONFUSED_GOODNESS,
            R.Translate.CONVICTION_NEUTRAL,
            R.Translate.CONVICTION_CONFUSED_EVIL,
            R.Translate.CONVICTION_LAWFUL_EVIL
        };
        RACES_VALUES = new String[]{
            R.Translate.RACE_HOBIT,
            R.Translate.RACE_KUDUK,
            R.Translate.RACE_DWARF,
            R.Translate.RACE_ELF,
            R.Translate.RACE_HUMAN,
            R.Translate.RACE_BARBAR,
            R.Translate.RACE_KROLL
        };

        PROFESSIONS_VALUES = new String[]{
            R.Translate.PROFESSION_WARIOR,
            R.Translate.PROFESSION_RANGER,
            R.Translate.PROFESSION_ALCHEMIST,
            R.Translate.PROFESSION_MAGICIAN,
            R.Translate.PROFESSION_THIEF
        };

        WEAPON_MELE_CLASSES_VALUES = new String[]{
            R.Translate.ITEM_WEAPON_MELE_CLASS_LIGHT,
            R.Translate.ITEM_WEAPON_MELE_CLASS_MEDIUM,
            R.Translate.ITEM_WEAPON_MELE_CLASS_HEAVY
        };

        WEAPON_MELE_TYPES_VALUES = new String[]{
            R.Translate.ITEM_WEAPON_MELE_TYPE_ONE_HAND,
            R.Translate.ITEM_WEAPON_MELE_TYPE_DOUBLE_HAND
        };

        WEAPON_RANGED_TYPES_VALUES = new String[]{
            R.Translate.ITEM_WEAPON_RANGED_TYPE_FIRE,
            R.Translate.ITEM_WEAPON_RANGED_TYPE_PROJECTILE
        };

        DICE_ADDITION_PROPERTIES_VALUES = new String[]{
            R.Translate.HERO_STRENGTH,
            R.Translate.HERO_DEXTERITY,
            R.Translate.HERO_IMMUNITY,
            R.Translate.HERO_INTELLIGENCE,
            R.Translate.HERO_CHARISMA
        };

        SHOP_ITEMS_VALUES = new String[]{
            R.Translate.ITEM_TYPE_WEAPON_MELE,
            R.Translate.ITEM_TYPE_WEAPON_RANGED,
            R.Translate.ITEM_TYPE_ARMOR,
            R.Translate.ITEM_TYPE_GENERAL,
            R.Translate.ITEM_TYPE_BACKPACK
        };

        BACKPACK_SIZES_VALUES = new String[]{
            R.Translate.ITEM_BACKPACK_SIZE_SMALL,
            R.Translate.ITEM_BACKPACK_SIZE_MEDIUM,
            R.Translate.ITEM_BACKPACK_SIZE_LARGE
        };

        RULES_VALUES = new String[]{
            R.Translate.RULES_PPZ,
            R.Translate.RULES_PPP,
            R.Translate.RULES_PPE
        };

        MOB_CLASSES_VALUES = new String[]{
            R.Translate.BESTIARY_TYPE_DRAGON,
            R.Translate.BESTIARY_TYPE_SNAKE,
            R.Translate.BESTIARY_TYPE_LYCANTROP,
            R.Translate.BESTIARY_TYPE_UNDEATH,
            R.Translate.BESTIARY_TYPE_INVISIBLE,
            R.Translate.BESTIARY_TYPE_STATUE,
            R.Translate.BESTIARY_TYPE_SPIDER,
            R.Translate.BESTIARY_TYPE_INSECT,
            R.Translate.BESTIARY_TYPE_OTHER,
        };

        ARMOR_TYPES_VALUES = new String[]{
            R.Translate.ITEM_ARMOR_TYPE_HELM,
            R.Translate.ITEM_ARMOR_TYPE_BODY,
            R.Translate.ITEM_ARMOR_TYPE_LEGS,
            R.Translate.ITEM_ARMOR_TYPE_BOOTS,
            R.Translate.ITEM_ARMOR_TYPE_GLASES
        };

        VULNERABILITIES_VALUES = new String[]{
            R.Translate.VULNERABILITY_ANIMAL,
            R.Translate.VULNERABILITY_HUMANOID,
            R.Translate.VULNERABILITY_DRAGON,
            R.Translate.VULNERABILITY_LYCANTHROPE,
            R.Translate.VULNERABILITY_UNDEATH,
            R.Translate.VULNERABILITY_INVISIBLE,
            R.Translate.VULNERABILITY_CUSTOM
        };

        THIEF_ABILITY_VALUES = new String[]{
            R.Translate.ABILITY_THIEF_DISGUISES,
            R.Translate.ABILITY_THIEF_CONFIDENCE,
            R.Translate.ABILITY_THIEF_DISCOVERY_MECHANISM,
            R.Translate.ABILITY_THIEF_DISCOVERY_OBJECTS,
            R.Translate.ABILITY_THIEF_DESTROY_MECHANISM,
            R.Translate.ABILITY_THIEF_OPEN_OBJECTS,
            R.Translate.ABILITY_THIEF_CLIMBING,
            R.Translate.ABILITY_THIEF_JUMP_FROM_ABOVE,
            R.Translate.ABILITY_THIEF_SILENT_MOTION,
            R.Translate.ABILITY_THIEF_HIDE_IN_THE_SHADOW,
            R.Translate.ABILITY_THIEF_PICKING_POCKETS
        };

        TERRAIN_DIFICULTY_VALUES = new String[]{
            R.Translate.TERRAIN_OPEN_EASY,
            R.Translate.TERRAIN_OPEN_HARD,
            R.Translate.TERRAIN_OPEN_UNUSABLE,
            R.Translate.TERRAIN_CLOSE_EASY,
            R.Translate.TERRAIN_CLOSE_HARD,
            R.Translate.TERRAIN_CLOSE_UNUSABLE
        };

        SPELL_PROFESSION_TYPES_VALUES = new String[]{
            R.Translate.PROFESSION_RANGER,
            R.Translate.PROFESSION_MAGICIAN
        };

        SPELL_TARGET_TYPES_VALUES = new String[]{
            R.Translate.SPELL_TARGET_TYPE_HERO,
            R.Translate.SPELL_TARGET_TYPE_MONSTER,
            R.Translate.SPELL_TARGET_TYPE_CREATURE,
            R.Translate.SPELL_TARGET_TYPE_THING,
        };

        SPELL_VARIABLE_TYPES_VALUES = new String[]{
            R.Translate.BESTIARY_VIABILITY,
            R.Translate.HERO_STRENGTH,
            R.Translate.HERO_DEXTERITY,
            R.Translate.HERO_IMMUNITY,
            R.Translate.HERO_INTELLIGENCE,
            R.Translate.HERO_CHARISMA
        };
    }

    // region Variables

    private final ResourceBundle resources;
    private final Map<Key, List<String>> translateMap;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou třídu pro překlad kolekcí
     *
     * @param resources Zdroje
     */
    public Translator(ResourceBundle resources) {
        this.resources = resources;
        this.translateMap = new HashMap<>();
    }

    // endregion

    // region Public methods

    /**
     * Získá kolekci s přeloženými konstantami podle zvoleného klíče
     *
     * @param key {@link Key}
     * @return Přeloženou kolekci
     */
    public List<String> getTranslationFor(Key key) {
        final List<String> stringList;
        if (!translateMap.containsKey(key)) {
            stringList = Arrays.stream(key.values).map(resources::getString)
                .collect(Collectors.toList());
        } else {
            stringList = translateMap.get(key);
        }

        return stringList;
    }

    public String getSingleTranslationFor(Key key, Enum e) {
        if (e == null) {
            return "";
        }
        return getTranslationFor(key).get(e.ordinal());
    }

    /**
     * Přeloží názvy atributů podle konstant
     *
     * @param tooltipMap Mapa obsahující hodnoty pro tooltip
     */
    public void translateTooltipKeys(Map<String, String> tooltipMap) {
        final Map<String, String> dummy = new LinkedHashMap<>(tooltipMap.size());
        tooltipMap.entrySet().forEach(entry -> {
            String value = entry.getValue();
            dummy.put(resources.getString(entry.getKey()), resources.containsKey(value) ? resources.getString(value) : value);
        });
        tooltipMap.clear();
        tooltipMap.putAll(dummy);
    }

    /**
     * Obecná metoda pro vytvoření konvertoru
     *
     * @param key {@link Key} Klíč, pod kterým se nachází překlad
     * @param <T> Konkrétní datový typ výčtu
     * @return {@link StringConverter <T>}
     */
    public <T> StringConverter<T> getConvertor(final Translator.Key key) {
        return new StringConverter<T>() {
            @Override
            public String toString(T object) {
                if (object == null) {
                    return "";
                }

                return getTranslationFor(key).get(((Enum) object).ordinal());
            }

            @Override
            public T fromString(String string) {
                return (T) Enum.valueOf(key.enumType, string);
            }
        };
    }

    public String translate(String key) {
        return resources.getString(key);
    }

    // endregion
}
