package cz.stechy.drd;

import cz.stechy.drd.pages.MainPage;
import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

/**
 * Základní třída pro testováná GUI
 */
public abstract class GUITestBase extends ApplicationTest {

    protected static ResourceBundle bundle;
    private Stage primaryStage;
    protected MainPage mainPage;

    @BeforeClass
    public static void setupHeadlessMode() {

        if (Boolean.getBoolean("headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
        }

        bundle = ResourceBundle.getBundle("lang.translate");
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    @Before
    public void beforeEachTest() throws Exception {
        FxToolkit.registerPrimaryStage();
        FxToolkit.hideStage();
        final TestApp application = (TestApp) FxToolkit.setupApplication(TestApp.class);
        this.mainPage = new MainPage(this);
    }

    @After
    public void atferEachTest() throws TimeoutException {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        this.primaryStage.show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void ensureEventQueueComplete() {
        WaitForAsyncUtils.waitForFxEvents(1);
    }

    /* Helper method to retrieve Java FX GUI components. */
    @SuppressWarnings("unchecked")
    public <T extends Node> T find(final String query) {
        return (T) lookup(query).queryAll().iterator().next();
    }
}
