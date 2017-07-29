package cz.stechy.drd.controller.hero;

import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.entity.hero.HeroGenerator;
import cz.stechy.screens.Bundle;

/**
 * Pomocná knihovní třída obsahující užitečné metody pro hrdiny
 */
public final class HeroHelper {

    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String CONVICTION = "conviction";
    public static final String RACE = "race";
    public static final String PROFESSION = "profession";
    public static final String LIVE = "baseLive";
    public static final String STRENGTH = "strength";
    public static final String DEXTERITY = "dexterity";
    public static final String IMMUNITY = "immunity";
    public static final String INTELLIGENCE = "intelligence";
    public static final String CHARISMA = "charisma";
    public static final String HEIGHT = "height";
    public static final String INVENTORY = "inventory";
    public static final String LEVEL_UP_PRICE = "level_up_price";

    private HeroHelper() {

    }

    /**
     * Vytvoří novou postavu z bundle
     *
     * @param bundle {@link Bundle}
     * @return {@link Hero}
     */
    public static Hero fromBundle(final Bundle bundle) {
        return new Hero.Builder()
            .name(bundle.getString(NAME))
            .description(bundle.getString(DESCRIPTION))
            .conviction(bundle.getInt(CONVICTION))
            .race(bundle.getInt(RACE))
            .profession(bundle.getInt(PROFESSION))
            .strength(bundle.getInt(STRENGTH))
            .dexterity(bundle.getInt(DEXTERITY))
            .immunity(bundle.getInt(IMMUNITY))
            .intelligence(bundle.getInt(INTELLIGENCE))
            .charisma(bundle.getInt(CHARISMA))
            .height(bundle.getInt(HEIGHT))
            .live(bundle.getInt(LIVE))
            .maxLive(bundle.getInt(LIVE))
            .build();
    }

    /**
     * Pomocná metoda pro správné nastavení nových parametrů pro hrdinu po přestupu na novou úroveň
     *
     * @param hero {@link Hero} Hrdina, který postupuje na novou úroveň
     * @param bundle {@link Bundle} Bundle, který obsahuje údaje o přestupu
     */
    public static void levelUp(final Hero hero, final Bundle bundle) {
        hero.levelUp();
        hero.getLive().setMaxValue(bundle.getInt(LIVE));
        hero.getStrength().setValue(bundle.getInt(STRENGTH));
        hero.getDexterity().setValue(bundle.getInt(DEXTERITY));
        hero.getImmunity().setValue(bundle.getInt(IMMUNITY));
        hero.getIntelligence().setValue(bundle.getInt(INTELLIGENCE));
        hero.getCharisma().setValue(bundle.getInt(CHARISMA));
        Money price = new Money(bundle.getInt(LEVEL_UP_PRICE));
        hero.getMoney().subtract(price);
        hero.getExperiences().setMaxValue(HeroGenerator.experience(hero.getRace(), hero.getLevel()));
    }
}
