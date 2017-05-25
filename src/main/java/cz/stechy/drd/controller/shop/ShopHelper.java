package cz.stechy.drd.controller.shop;

/**
 * Pomocní třída pro kontrolery s obchodem
 */
public final class ShopHelper {

    // region Constants

    public static final String ITEM_ACTION = "item_action";
    public static final int ITEM_ACTION_ADD = 1;
    public static final int ITEM_ACTION_UPDATE = 2;

    // endregion

    // region Constructors

    /**
     * Privátní konstruktor pro zabránění vytvoření instance
     */
    private ShopHelper() {
        throw new AssertionError();
    }

    // endregion

}
