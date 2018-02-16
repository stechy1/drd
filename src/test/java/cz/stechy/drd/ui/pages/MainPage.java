package cz.stechy.drd.ui.pages;

import cz.stechy.drd.ui.APage;
import cz.stechy.drd.R.Translate;
import cz.stechy.drd.ui.pages.shop.ShopPage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.testfx.api.FxRobot;

/**
 * Třída sloužící k interakci s kontrolerem {@link cz.stechy.drd.app.main.MainController}
 */
public final class MainPage extends APage {

    // region Constants

    private static final Map<String, Class<? extends APage>> MAP;

    // endregion

    static {
        Map<String, Class<? extends APage>> map = new HashMap<>();
        map.put("#btnShop", ShopPage.class);
        MAP = Collections.unmodifiableMap(map);
    }

    // region Variables

    // endregion

    // region Constructors

    public MainPage(FxRobot parentRobot) {
        super(parentRobot);
    }

    // endregion

    // region Private methods

    @Override
    protected String getTag() {
        return Translate.MAIN_TITLE;
    }

    @Override
    protected void navigateTo(String identifier) {

    }

    @Override
    protected void clickOn(String identifier) {
        robot.clickOn(identifier);
    }

    @Override
    protected Map<String, Class<? extends APage>> getPageMap() {
        return MAP;
    }

    // endregion

    // region Public methods

    public APage showShop() throws Exception {
        return showPage("#btnShop");
    }

    // endregion

}
