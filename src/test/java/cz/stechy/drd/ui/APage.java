package cz.stechy.drd.ui;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Map;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.testfx.api.FxRobot;

/**
 * Základní třída pro PageObject pattern pro testování kontrolerů
 */
public abstract class APage {

    // region Variables

    protected final FxRobot robot;

    // endregion

    // region Constructors

    public APage(FxRobot parentRobot) {
        this.robot = parentRobot.targetWindow(getTitleFromBundle(getTag()));
        if (!((Stage) robot.targetWindow()).getTitle().equals(getTitleFromBundle(getTag()))) {
            throw new IllegalArgumentException("Nejsem na správném screenu");
        }
    }

    // endregion

    // region Private methods

    public String getTitleFromBundle(String tag) {
        return GUITestBase.getBundle().getString(tag);
    }

    protected abstract String getTag();

    protected void navigateTo(String identifier) {
    }

    protected void clickOn(String identifier) {
    }

    protected Map<String, Class<? extends APage>> getPageMap() {
        return Collections.emptyMap();
    }

    // endregion

    // region Public methods

    public APage showPage(String identifier) throws Exception {
        navigateTo(identifier);
        clickOn(identifier);
        final Map<String, Class<? extends APage>> map = getPageMap();
        final Class<? extends APage> aClass = map.get(identifier);
        final Constructor<? extends APage> constructor = aClass.getConstructor(FxRobot.class);
        return constructor.newInstance(robot);
    }

    @SuppressWarnings("unchecked")
    public <T extends Node> T find(final String query) {
        return (T) robot.lookup(query).queryAll().iterator().next();
    }

    // endregion


}
