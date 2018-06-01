package cz.stechy.drd.firebase;

import cz.stechy.drd.firebase.FirebaseItemRepository.ItemConvertor;
import cz.stechy.drd.model.item.ItemType;

/**
 * Pomocná knihovní třída obsahující pomocné metody pro konverzi
 * {@link com.google.firebase.database.DataSnapshot} na {@link java.util.Map}
 */
public final class FirebaseItemConvertors {

    public static ItemConvertor forItem(ItemType itemType) {
        switch (itemType) {
            case ARMOR:
                return ARMOR_CONVERTOR;
            case GENERAL:
                return GENERAL_CONVERTOR;
            case BACKPACK:
                return BACKPACK_CONVERTOR;
            case WEAPON_MELE:
                return WEAPON_MELE_CONVERTOR;
            case WEAPON_RANGED:
                return WEAPON_RANGED_CONVERTOR;
            default:
                throw new IllegalArgumentException("Konvertor nebyl rozpoznán");
        }
    }

    private static final ItemConvertor ARMOR_CONVERTOR = snapshot -> {

    };

    private static final ItemConvertor GENERAL_CONVERTOR = snapshot -> {

    };

    private static final ItemConvertor BACKPACK_CONVERTOR = snapshot -> {

    };

    private static final ItemConvertor WEAPON_MELE_CONVERTOR = snapshot -> {

    };

    private static final ItemConvertor WEAPON_RANGED_CONVERTOR = snapshot -> {

    };

}
