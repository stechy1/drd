package cz.stechy.drd.model.shop.entry;

import cz.stechy.drd.model.item.GeneralItem;

/**
 * Položka v obchodě představující standartní předmět
 */
public class GeneralEntry extends ShopEntry {

    /**
     * Vytvoří novou standartní nákupní položku
     *
     * @param itemBase {@link GeneralItem}
     */
    public GeneralEntry(GeneralItem itemBase) {
        super(itemBase);
    }
}
