package cz.stechy.drd.controller.spellbook;

import cz.stechy.drd.model.spell.Spell;
import cz.stechy.drd.model.spell.parser.SpellParser;
import cz.stechy.screens.Bundle;

public final class SpellBookHelper {

    // region Constants

    public static final String ID = "id";
    public static final String AUTHOR = "author";
    public static final String NAME = "name";
    public static final String MAGIC_NAME = "magic_name";
    public static final String DESCRIPTION = "description";
    public static final String PROFESSION_TYPE = "profession_type";
    public static final String PRICE = "price";
    public static final String RADIUS = "radius";
    public static final String RANGE = "range";
    public static final String TARGET = "target";
    public static final String CAST_TIME = "cast_time";
    public static final String DURATION = "duration";
    public static final String IMAGE = "image";
    public static final String UPLOADED = "uploaded";
    public static final String DOWNLOADED = "downloaded";

    public static final String SPELL_ACTION = "action_type";
    public static final int SPELL_ACTION_ADD = 1;
    public static final int SPELL_ACTION_UPDATE = 2;
    public static final int SPELL_ROW_HEIGHT = 40;

    // endregion

    // region Constructors

    /**
     * Privátní konstruktor k zabránění vytvoření instance
     */
    private SpellBookHelper() {

    }

    // endregion

    // region Public static methods

    public static void toBundle(Bundle bundle, Spell spell) {
        bundle.putString(ID, spell.getId());
            bundle.putString(AUTHOR, spell.getAuthor());
            bundle.putString(NAME, spell.getName());
            bundle.putString(MAGIC_NAME, spell.getMagicName());
            bundle.putString(DESCRIPTION, spell.getDescription());
            bundle.putInt(PROFESSION_TYPE, spell.getType().ordinal());
            bundle.putString(PRICE, spell.getPrice().pack());
            bundle.putInt(RADIUS, spell.getRadius());
            bundle.putInt(RANGE, spell.getRange());
            bundle.putInt(TARGET, spell.getTarget().ordinal());
            bundle.putInt(CAST_TIME, spell.getCastTime());
            bundle.putInt(DURATION, spell.getDuration());
            bundle.put(IMAGE, spell.getImage());
            bundle.putBoolean(UPLOADED, spell.isUploaded());
            bundle.putBoolean(DOWNLOADED, spell.isDownloaded());
    }

    public static Spell fromBundle(Bundle bundle) {
        return new Spell.Builder()
            .id(bundle.getString(ID))
            .author(bundle.getString(AUTHOR))
            .name(bundle.getString(NAME))
            .magicName(bundle.getString(MAGIC_NAME))
            .description(bundle.getString(DESCRIPTION))
            .type(bundle.getInt(PROFESSION_TYPE))
            .price(new SpellParser(bundle.getString(PRICE)).parse())
            .radius(bundle.getInt(RADIUS))
            .range(bundle.getInt(RANGE))
            .target(bundle.getInt(TARGET))
            .castTime(bundle.getInt(CAST_TIME))
            .duration(bundle.getInt(DURATION))
            .image(bundle.get(IMAGE))
            .uploaded(bundle.getBoolean(UPLOADED))
            .downloaded(bundle.getBoolean(DOWNLOADED))
            .build();
    }

    public static void showGraph(ISpellGraphNode node) {
        System.out.println("Aktualni node: " + node);
        System.out.println("Rodice============================");
        System.out.println("Levy: " + node.getParentNodes().getKey());
        System.out.println("Pravy: " + node.getParentNodes().getValue());
        ISpellGraphNode child = node.getChildNode();
        if (child != null) {
            showGraph(child);
        } else {
            System.out.println("Toto je startovni node: " + node);
        }
    }

    // endregion
}
