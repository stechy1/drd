package cz.stechy.drd.pages;

import cz.stechy.drd.APage;
import cz.stechy.drd.R.Translate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.testfx.api.FxRobot;

/**
 * Třída sloužící k interakci s kontrolerem {@link cz.stechy.drd.controller.main.MainController}
 */
public final class MainPage extends APage {

    // region Constants

    private static final Map<String, Class<? extends APage>> MAP;

    // endregion

    static {
        Map<String, Class<? extends APage>> map = new HashMap<>();
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



    // endregion

}
