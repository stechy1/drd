package cz.stechy.drd.app.shop;

import com.google.inject.Inject;
import cz.stechy.drd.R;
import cz.stechy.drd.app.shop.entry.GeneralEntry;
import cz.stechy.drd.db.base.ITableWrapperFactory;
import cz.stechy.drd.model.item.GeneralItem;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.service.translator.ITranslatorService;
import cz.stechy.drd.service.user.IUserService;
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

    @Inject
    public ShopGeneralController(IUserService userService, ITableWrapperFactory tableFactory, ITranslatorService translator) {
        super(tableFactory.getTableWrapper(GeneralItem.class), translator, userService);
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
