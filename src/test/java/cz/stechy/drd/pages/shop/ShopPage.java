package cz.stechy.drd.pages.shop;

import cz.stechy.drd.APage;
import cz.stechy.drd.R.Translate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.TitledPane;
import org.testfx.api.FxRobot;

/**
 * Třída sloužící k interakci s kontrolerem {@link cz.stechy.drd.controller.shop.ShopController1}
 */
public final class ShopPage extends APage {

    // region Constants

    private static final Map<String, Class<? extends APage>> MAP;

    // endregion

    static {
        Map<String, Class<? extends APage>> map = new HashMap<>();
        map.put("drd_item_type_general", GeneralShopPage.class);
        MAP = Collections.unmodifiableMap(map);
    }

    // region Constructors

    public ShopPage(FxRobot parentRobot) {
        super(parentRobot);
    }

    // endregion

    // region Private methods

    @Override
    protected String getTag() {
        return Translate.SHOP_TITLE;
    }

    @Override
    protected void navigateTo(String identifier) {
        robot.clickOn(node -> node instanceof TitledPane && ((TitledPane) node).getText()
            .equals(super.getTitleFromBundle(identifier)));
    }

    @Override
    protected void clickOn(String identifier) {
        robot.clickOn("#btnAddItem");
    }

    @Override
    protected Map<String, Class<? extends APage>> getPageMap() {
        return MAP;
    }

    // endregion

    // region Public methods

    public APage showGeneralPage() throws Exception {
        return showPage("drd_item_type_general");
    }

    // endregion
}
