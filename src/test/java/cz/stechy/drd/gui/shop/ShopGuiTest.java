package cz.stechy.drd.gui.shop;

import cz.stechy.drd.GUITestBase;
import cz.stechy.drd.pages.shop.ShopPage;
import org.junit.Before;

/**
 * Třída obsahující Gui testy pro screen {@link cz.stechy.drd.controller.shop.ShopController1}
 */
public final class ShopGuiTest extends GUITestBase {

    // region Variables

    private ShopPage shopPage;

    // endregion

    // region Private methods

    @Before
    public void beforeEachTest() throws Exception {
        super.beforeEachTest();
        this.shopPage = (ShopPage) super.mainPage.showShop();
    }

    // endregion
}
