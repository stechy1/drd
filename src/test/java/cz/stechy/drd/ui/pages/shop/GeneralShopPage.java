package cz.stechy.drd.ui.pages.shop;

import cz.stechy.drd.ui.APage;
import cz.stechy.drd.R.Translate;
import java.util.Map;
import org.testfx.api.FxRobot;

/**
 * Třída sloužící k interakci s kontrolerem {@link cz.stechy.drd.controller.shop.ItemGeneralController}
 */
public class GeneralShopPage extends APage {

    // region Constructors

    public GeneralShopPage(FxRobot parentRobot) {
        super(parentRobot);
    }

    // endregion

    // region Private methods

    @Override
    protected String getTag() {
        return Translate.ITEM_TYPE_GENERAL;
    }

    @Override
    protected Map<String, Class<? extends APage>> getPageMap() {
        return super.getPageMap();
    }

    // endregion
}
