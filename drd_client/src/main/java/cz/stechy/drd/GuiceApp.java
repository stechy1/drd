package cz.stechy.drd;

import com.google.inject.Guice;
import com.google.inject.Injector;
import cz.stechy.drd.service.keyboard.KeyboardService;
import cz.stechy.screens.ScreenManager;
import cz.stechy.screens.base.IMainScreen;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("all")
public class GuiceApp extends Application {
    // region Constants

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static final String[] SCREENS_BLACKLIST = {
        R.Fxml.DEFAULT_STAFF_LEFT, R.Fxml.DEFAULT_STAFF_RIGHT};

    // endregion

    // region Variables

    protected ScreenManager manager;
    private Injector injector;

    // endregion

    @Override
    public void init() throws Exception {
        injector = Guice.createInjector(new TableModule(), new AppModule(), new ServiceModule());
        ScreenManager.setKeyPressedHandler(KeyboardService.getINSTANCE()::keyPressHandler);
        ScreenManager.setKeyReleasedHandler(KeyboardService.getINSTANCE()::keyReleasedHandler);

        manager = injector.getInstance(ScreenManager.class);
        manager.setResources(injector.getInstance(ResourceBundle.class));
        manager.addScreensToBlacklist(SCREENS_BLACKLIST);
        manager.setOnCloseWindowHandler(event -> {
            LOGGER.info("Ukončuji aplikace...");
            ThreadPool.shutDown();
        });
        manager.setControllerFactory(type -> {
            final Object instance = injector.getInstance(type);
            LOGGER.info("Vytvářím instanci typu: {}", type.getCanonicalName());
            return instance;
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(manager.getScreenManagerConfiguration().baseFxml);
        loader.setControllerFactory(injector::getInstance);
        loader.setResources(manager.getResources());
        AnchorPane parent = loader.load();
        IMainScreen controlledScreen = loader.getController();
        manager.setMainScreen(controlledScreen);
        manager.loadScreens();
        manager.showNewDialog(parent, primaryStage, false);
        manager.showScreen(R.Fxml.MAIN, null);
    }
}
