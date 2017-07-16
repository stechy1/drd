package cz.stechy.drd.gui.shop;

import cz.stechy.drd.GUITestBase;
import cz.stechy.drd.pages.shop.GeneralShopPage;
import cz.stechy.drd.pages.shop.ShopPage;
import org.junit.Before;
import org.junit.Test;

/**
 * Třída obsahující Gui testy pro screen {@link cz.stechy.drd.controller.shop.ItemGeneralController}
 */
public class ShopGeneralGuiTest extends GUITestBase {

    private GeneralShopPage generalShopPage;

    @Before
    public void beforeEachTest() throws Exception {
        super.beforeEachTest();
        ShopPage shopPage = (ShopPage) super.mainPage.showShop();
        this.generalShopPage = (GeneralShopPage) shopPage.showGeneralPage();
    }

    @Test
    public void test() throws Exception {

    }
}
