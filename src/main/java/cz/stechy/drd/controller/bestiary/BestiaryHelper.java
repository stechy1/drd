package cz.stechy.drd.controller.bestiary;

import cz.stechy.drd.model.entity.mob.Mob;
import cz.stechy.screens.Bundle;

/**
 * Pomocná knihovní třída k usnadnění práce s bestiářem
 */
public final class BestiaryHelper {

    // region Constants

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String AUTHOR = "author";
    public static final String IMAGE = "image";
    public static final String MOB_CLASS = "mob_class";
    public static final String RULES_TYPE = "rules_type";
    public static final String CONVICTION = "conviction";
    public static final String HEIGHT = "height";
    public static final String ATTACK = "attack";
    public static final String DEFENCE = "defence";
    public static final String VIABILITY = "viability";
    public static final String IMMUNITY = "immunity";
    public static final String METTLE = "mettle";
    public static final String VULNERABILITY = "vulnerability";
    public static final String MOBILITY = "mobility";
    public static final String PERSERVANCE = "perservance";
    public static final String CONTROL_ABILITY = "control_ability";
    public static final String INTELLIGENCE = "intelligence";
    public static final String CHARISMA = "charisma";
    public static final String BASIC_BOWER_OF_MIND = "basic_power_of_mind";
    public static final String EXPERIENCE = "experience";
    public static final String DOMESTICATION = "domestication";

    public static final String MOB_ACTION = "action_type";
    public static final int MOB_ACTION_ADD = 1;
    public static final int MOB_ACTION_UPDATE = 2;

    // endregion

    // region Constructors

    private BestiaryHelper() {}

    // endregion

    public static Bundle mobToBundle(Mob mob) {
        final Bundle bundle = new Bundle();

            bundle.putString(ID, mob.getId());
            bundle.putString(NAME, mob.getName());
            bundle.putString(DESCRIPTION, mob.getDescription());
            bundle.putString(AUTHOR, mob.getAuthor());
            bundle.put(IMAGE, mob.getImageRaw());
            bundle.putInt(MOB_CLASS, mob.getMobClass().ordinal());
            bundle.putInt(RULES_TYPE, mob.getRulesType().ordinal());
            bundle.putInt(CONVICTION, mob.getConviction().ordinal());
            bundle.putInt(HEIGHT, mob.getHeight().ordinal());
            bundle.putInt(ATTACK, mob.getAttackNumber().getValue());
            bundle.putInt(DEFENCE, mob.getDefenceNumber());
            bundle.putInt(VIABILITY, mob.getViability());
            bundle.putInt(IMMUNITY, mob.getImmunity().getValue());
            bundle.putInt(METTLE, mob.getMettle());
            bundle.putInt(VULNERABILITY, mob.getVulnerability().value);
            bundle.putInt(MOBILITY, mob.getMobility());
            bundle.putInt(PERSERVANCE, mob.getPerservance());
            bundle.putInt(CONTROL_ABILITY, mob.getControlAbility());
            bundle.putInt(INTELLIGENCE, mob.getIntelligence().getValue());
            bundle.putInt(CHARISMA, mob.getCharisma().getValue());
            bundle.putInt(BASIC_BOWER_OF_MIND, mob.getBasicPowerOfMind());
            bundle.putInt(EXPERIENCE, mob.getExperience());
            bundle.putInt(DOMESTICATION, mob.getDomestication());

        return bundle;
    }

    public static Mob mobFromBundle(Bundle bundle) {
        return new Mob.Builder()
            .id(bundle.getString(ID))
            .name(bundle.getString(NAME))
            .description(bundle.getString(DESCRIPTION))
            .author(bundle.getString(AUTHOR))
            .image(bundle.get(IMAGE))
            .mobClass(bundle.getInt(MOB_CLASS))
            .rulesType(bundle.getInt(RULES_TYPE))
            .conviction(bundle.getInt(CONVICTION))
            .height(bundle.getInt(HEIGHT))
            .attackNumber(bundle.getInt(ATTACK))
            .defenceNumber(bundle.getInt(DEFENCE))
            .viability(bundle.getInt(VIABILITY))
            .immunity(bundle.getInt(IMMUNITY))
            .mettle(bundle.getInt(METTLE))
            .vulnerability(bundle.getInt(VULNERABILITY))
            .mobility(bundle.getInt(MOBILITY))
            .perservance(bundle.getInt(PERSERVANCE))
            .controlAbility(bundle.getInt(CONTROL_ABILITY))
            .intelligence(bundle.getInt(INTELLIGENCE))
            .charisma(bundle.getInt(CHARISMA))
            .basicPowerOfMind(bundle.getInt(BASIC_BOWER_OF_MIND))
            .experience(bundle.getInt(EXPERIENCE))
            .domestication(bundle.getInt(DOMESTICATION))
            .build();
    }

}
