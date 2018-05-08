package cz.stechy.drd.ui.gui.shop;

import cz.stechy.drd.ui.GUITestBase;
import cz.stechy.drd.ui.pages.shop.GeneralShopPage;
import cz.stechy.drd.ui.pages.shop.ShopPage;
import org.junit.Before;
import org.junit.Test;

/**
 * Třída obsahující Gui testy pro screen {@link cz.stechy.drd.app.shop.ItemGeneralController}
 */
public final class ShopGeneralGuiTest extends GUITestBase {

    // region Variables

    private GeneralShopPage generalShopPage;

    @Before
    public void beforeEachTest() throws Exception {
        super.beforeEachTest();
        ShopPage shopPage = (ShopPage) super.mainPage.showShop();
        this.generalShopPage = (GeneralShopPage) shopPage.showGeneralPage();
    }

    // endregion

    // region Public methods

    // endregion

    @Test
    public void test() throws Exception {
        generalShopPage.fillValues(GeneralShopPage.ITEM_1).confirm();


    }
}
