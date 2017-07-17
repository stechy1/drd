package cz.stechy.drd.ui.gui;

import static org.testfx.api.FxAssert.verifyThat;

import cz.stechy.drd.R.Translate;
import cz.stechy.drd.ui.GUITestBase;
import javafx.scene.control.Button;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Třída obsahující Gui testy pro screen {@link cz.stechy.drd.controller.main.MainController}
 */
@RunWith(JUnitParamsRunner.class)
public final class MainGuiTest extends GUITestBase {

    // region Constants

    private static final String TOOLBAR_BTN_NEW_HERO = "#btnNewHero";
    private static final String TOOLBAR_BTN_LOAD_HERO = "#btnLoadHero";
    private static final String TOOLBAR_BTN_CLOSE_HERO = "#btnCloseHero";
    private static final String TOOLBAR_BTN_DICE = "#btnDice";
    private static final String TOOLBAR_BTN_BESTIARY = "#btnBestiary";
    private static final String TOOLBAR_BTN_SHOP = "#btnShop";

    // endregion

    // region Private methods

    private Object[] parametersForToolbarsButtonTooltip() {
        return new Object[] {
            new Object[] {TOOLBAR_BTN_NEW_HERO, Translate.MAIN_MENU_FILE_NEW_HERO},
            new Object[] {TOOLBAR_BTN_LOAD_HERO, Translate.MAIN_MENU_FILE_LOAD_HERO},
            new Object[] {TOOLBAR_BTN_CLOSE_HERO, Translate.MAIN_MENU_FILE_CLOSE_HERO},
            new Object[] {TOOLBAR_BTN_DICE, Translate.MAIN_MENU_TOOLS_DICE},
            new Object[] {TOOLBAR_BTN_BESTIARY, Translate.MAIN_MENU_TOOLS_BESTIARY},
            new Object[] {TOOLBAR_BTN_SHOP, Translate.MAIN_MENU_TOOLS_SHOP},
        };
    }

    // endregion

    @Test
    @Parameters(method = "parametersForToolbarsButtonTooltip")
    public void testToolbarsButtonTooltip(String identifier, String tag) throws Exception {
        final String translated = mainPage.getTitleFromBundle(tag);
        verifyThat(identifier, (Button button) ->
            translated.equals(button.getTooltip().getText()));
    }
}
