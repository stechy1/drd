package cz.stechy.drd.app.shop;

import cz.stechy.drd.R;
import cz.stechy.drd.app.shop.entry.GeneralEntry;
import cz.stechy.drd.dao.GeneralItemDao;
import cz.stechy.drd.model.item.GeneralItem;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.service.UserService;
import cz.stechy.drd.util.Translator;
import cz.stechy.screens.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pomocný kontroler pro obchod se obecnými předměty
 */
public class ShopGeneralController extends AShopItemController<GeneralItem, GeneralEntry> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopGeneralController.class);

    // endregion

    // region Constrollers

    public ShopGeneralController(UserService userService, GeneralItemDao generalItemDao, Translator translator) {
        super(generalItemDao, translator, userService);
    }

    // endregion

    @Override
    protected GeneralEntry getEntry(GeneralItem generalItem) {
        return new GeneralEntry(generalItem);
    }

    @Override
    public String getEditScreenName() {
        return R.Fxml.ITEM_GENERAL;
    }

    @Override
    public void insertItemToBundle(Bundle bundle, int index) {
        ItemGeneralController.toBundle(bundle, (GeneralItem) sortedList.get(index).getItemBase());
    }

    @Override
    public ItemBase fromBundle(Bundle bundle) {
        return ItemGeneralController.fromBundle(bundle);
    }

}
