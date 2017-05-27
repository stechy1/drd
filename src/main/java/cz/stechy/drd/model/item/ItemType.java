package cz.stechy.drd.model.item;

/**
 * Výčet typů předmětů
 */
public enum ItemType {

    GENERAL, WEAPON_MELE, WEAPON_RANGED, ARMOR, BACKPACK;

    public static ItemType valueOf(int index) {
        if (index < 0) {
            return null;
        }

        return ItemType.values()[index];
    }

    /**
     * Zjistí, zda-li je testovaný typ zbraň (na blízko, či na dálku)
     *
     * @param itemType {@link ItemType}
     * @return True, pokud je testovaný typ zbraň, jinak false
     */
    public static boolean isSword(ItemType itemType) {
        return itemType == WEAPON_MELE || itemType == WEAPON_RANGED;
    }

}
