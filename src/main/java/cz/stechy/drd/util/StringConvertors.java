package cz.stechy.drd.util;

import cz.stechy.drd.controller.dice.DiceHelper;
import cz.stechy.drd.controller.dice.DiceHelper.AdditionType;
import cz.stechy.drd.model.Rule;
import cz.stechy.drd.model.entity.Conviction;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.entity.hero.Hero.Profession;
import cz.stechy.drd.model.entity.hero.Hero.Race;
import cz.stechy.drd.model.entity.mob.Mob.MobClass;
import cz.stechy.drd.model.item.Armor.ArmorType;
import cz.stechy.drd.model.item.Backpack;
import cz.stechy.drd.model.item.Backpack.Size;
import cz.stechy.drd.model.item.MeleWeapon;
import cz.stechy.drd.model.item.MeleWeapon.MeleWeaponClass;
import cz.stechy.drd.model.item.MeleWeapon.MeleWeaponType;
import cz.stechy.drd.model.item.RangedWeapon;
import cz.stechy.drd.model.item.RangedWeapon.RangedWeaponType;
import cz.stechy.drd.util.Translator.Key;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.util.StringConverter;

/**
 * Pomocná knihovní třída obsahující konvertory výčtových typů na řetězce
 */
public final class StringConvertors {

    /**
     * Privátní konstruktor k zamezení vytvoření instance třídy
     */
    private StringConvertors() {
        throw new AssertionError();
    }

    public static StringConverter<Conviction> forConvictionConverter(Translator translator) {
        return new StringConverter<Conviction>() {
            @Override
            public String toString(Conviction object) {
                if (object == null) {
                    return "";
                }

                return translator.getTranslationFor(Key.CONVICTIONS).get(object.ordinal());
            }

            @Override
            public Conviction fromString(String string) {
                return Conviction.valueOf(string);
            }
        };
    }

    public static StringConverter<Race> forRaceConverter(Translator translator) {
        return new StringConverter<Race>() {
            @Override
            public String toString(Race object) {
                if (object == null) {
                    return "";
                }

                return translator.getTranslationFor(Key.RACES).get(object.ordinal());
            }

            @Override
            public Race fromString(String string) {
                return Race.valueOf(string);
            }
        };
    }

    public static StringConverter<Profession> forProfessionConverter(Translator translator) {
        return new StringConverter<Profession>() {
            @Override
            public String toString(Profession object) {
                if (object == null) {
                    return "";
                }

                return translator.getTranslationFor(Key.PROFESSIONS).get(object.ordinal());
            }

            @Override
            public Profession fromString(String string) {
                return Profession.valueOf(string);
            }
        };
    }
    
    public static StringConverter<DiceHelper.AdditionType> forAdditionType(Translator translator) {
        return new StringConverter<AdditionType>() {
            @Override
            public String toString(AdditionType object) {
                if (object == null) {
                    return "";
                }

                return translator.getTranslationFor(Key.DICE_ADDITION_PROPERTIES).get(object.ordinal());
            }

            @Override
            public AdditionType fromString(String string) {
                return AdditionType.valueOf(string);
            }
        };
    }

    public static StringConverter<MeleWeapon.MeleWeaponType> forMeleWeaponType(
        Translator translator) {
        return new StringConverter<MeleWeaponType>() {
            @Override
            public String toString(MeleWeaponType object) {
                if (object == null) {
                    return "";
                }

                return translator.getTranslationFor(Key.WEAPON_MELE_TYPES).get(object.ordinal());
            }

            @Override
            public MeleWeaponType fromString(String string) {
                return MeleWeaponType.valueOf(string);
            }
        };
    }

    public static StringConverter<MeleWeapon.MeleWeaponClass> forMeleWeaponClass(
        Translator translator) {
        return new StringConverter<MeleWeaponClass>() {
            @Override
            public String toString(MeleWeaponClass object) {
                if (object == null) {
                    return "";
                }

                return translator.getTranslationFor(Key.WEAPON_MELE_CLASSES).get(object.ordinal());
            }

            @Override
            public MeleWeaponClass fromString(String string) {
                return MeleWeaponClass.valueOf(string);
            }
        };
    }

    public static StringConverter<ArmorType> forArmorType(
        Translator translator) {
        return new StringConverter<ArmorType>() {
            @Override
            public String toString(ArmorType object) {
                if (object == null) {
                    return "";
                }

                return translator.getTranslationFor(Key.ARMOR_TYPES).get(object.ordinal());
            }

            @Override
            public ArmorType fromString(String string) {
                return ArmorType.valueOf(string);
            }
        };
    }

    public static StringConverter<RangedWeapon.RangedWeaponType> forRangedWeaponType(
        Translator translator) {
        return new StringConverter<RangedWeaponType>() {
            @Override
            public String toString(RangedWeaponType object) {
                if (object == null) {
                    return "";
                }

                return translator.getTranslationFor(Key.WEAPON_RANGED_TYPES).get(object.ordinal());
            }

            @Override
            public RangedWeaponType fromString(String string) {
                return RangedWeaponType.valueOf(string);
            }
        };
    }

    public static StringExpression forRaceAndProfessionConverter(Translator translator, Hero hero) {
        Race race = hero.getRace();
        Profession profession = hero.getProfession();
        if (race == null || profession == null) {
            return Bindings.concat();
        }

        String translatedRace = translator.getTranslationFor(Key.RACES).get(hero.getRace().ordinal());
        String translatedProfession = translator.getTranslationFor(Key.PROFESSIONS)
            .get(hero.getProfession().ordinal());
        return Bindings.concat(translatedRace, " ", translatedProfession);
    }

    public static StringConverter<Backpack.Size> forBackpackSize(Translator translator) {
        return new StringConverter<Size>() {
            @Override
            public String toString(Size object) {
                if (object == null) {
                    return "";
                }

                return translator.getTranslationFor(Key.BACKPACK_SIZES).get(object.ordinal());
            }

            @Override
            public Size fromString(String string) {
                return Size.valueOf(string);
            }
        };
    }
    
    public static StringConverter<Rule> forRulesType(Translator translator) {
        return new StringConverter<Rule>() {
            @Override
            public String toString(Rule object) {
                if (object == null) {
                    return "";
                }

                return translator.getTranslationFor(Key.RULES).get(object.ordinal());
            }

            @Override
            public Rule fromString(String string) {
                return Rule.valueOf(string);
            }
        };
    }
    
    public static StringConverter<MobClass> forMobClass(Translator translator) {
        return new StringConverter<MobClass>() {
            @Override
            public String toString(MobClass object) {
                if (object == null) {
                    return "";
                }

                return translator.getTranslationFor(Key.MOB_CLASSES).get(object.ordinal());
            }

            @Override
            public MobClass fromString(String string) {
                return MobClass.valueOf(string);
            }
        };
    }
}
