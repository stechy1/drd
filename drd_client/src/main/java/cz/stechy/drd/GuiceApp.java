package cz.stechy.drd;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.util.Types;
import com.sun.javafx.application.LauncherImpl;
import cz.stechy.drd.db.BaseOfflineTable;
import cz.stechy.drd.db.base.Row;
import cz.stechy.drd.model.MyPreloaderNotification;
import cz.stechy.drd.service.keyboard.KeyboardService;
import cz.stechy.screens.ScreenManager;
import cz.stechy.screens.base.IMainScreen;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

    static {
        // Pro rychlé spuštění - přeskočí úvodní splashscreen
        System.setProperty("quick", "true");
    }

    // region Variables

    private final BooleanProperty initialized = new SimpleBooleanProperty(this, "initialized", false);

    protected ScreenManager manager;
    private Injector injector;

    // endregion

    public static void main(String[] args) {
        LauncherImpl.launchApplication(GuiceApp.class, AppPreloader.class, args);
    }

    @Override
    public void init() throws Exception {
        final boolean quick = Boolean.getBoolean("quick");
        notifier.increaseMaxProgress(4);
        notifier.increaseProgress( 1, "Inicializace...");

        injector = Guice.createInjector(new TableModule(), new AppModule(), new ServiceModule());
        ScreenManager.setKeyPressedHandler(KeyboardService.getINSTANCE()::keyPressHandler);
        ScreenManager.setKeyReleasedHandler(KeyboardService.getINSTANCE()::keyReleasedHandler);

        notifier.increaseProgress( "Nastavování screen managera...");
        manager = injector.getInstance(ScreenManager.class);
        manager.setResources(injector.getInstance(ResourceBundle.class));
        manager.addScreensToBlacklist(SCREENS_BLACKLIST);
        manager.setOnCloseWindowHandler(event -> {
            LOGGER.info("Ukončuji aplikaci...");
            ThreadPool.shutDown();
        });
        manager.setControllerFactory(type -> {
            final Object instance = injector.getInstance(type);
            LOGGER.trace("Vytvářím instanci typu: {}", type.getCanonicalName());
            return instance;
        });
        manager.loadScreens();

        ThreadPool.COMMON_EXECUTOR.execute(() -> {
            notifier.increaseProgress( "Načítání tabulek...");
            final Map<Class, BaseOfflineTable> offlineTableMap = (Map<Class, BaseOfflineTable>) injector.getInstance(Key.get(Types.mapOf(Class.class, BaseOfflineTable.class)));
            notifier.increaseMaxProgress(offlineTableMap.size());
            offlineTableMap.values().stream().forEach(baseOfflineTable -> {
                notifier.increaseProgress("Vytvářím tabulku: " + baseOfflineTable.toString());
                baseOfflineTable.createTableAsync().join();
                final List<Row> records = (List<Row>) baseOfflineTable.selectAllAsync().join();
                if (!quick) {
                    notifier.increaseMaxProgress(records.size());
                    for (Row record : records) {
                        notifier.increaseProgress("Načítám záznam: " + record.toString());
                    }
                }
            });

            Platform.runLater(() -> {
                initialized.setValue(true);
                notifier.closePreloader();
            });
            notifier.increaseProgress("Dokončuji...");
            try {
                Thread.sleep(750);
            } catch (InterruptedException ignored) {}
        });
    }

    @Override
    public void start(Stage primaryStage) {
        initialized.addListener((observableValue, aBoolean, t1) -> {
            try {
                final FXMLLoader loader = new FXMLLoader(manager.getScreenManagerConfiguration().baseFxml);
                loader.setControllerFactory(injector::getInstance);
                loader.setResources(manager.getResources());
                final AnchorPane parent = loader.load();
                final IMainScreen controlledScreen = loader.getController();
                manager.setMainScreen(controlledScreen);
                manager.showNewDialog(parent, primaryStage, false);
                manager.showScreen(R.Fxml.MAIN, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

    }

    private final PreloaderNotifier notifier = new PreloaderNotifier() {
        private int total = 1;
        private double progress = 0;

        @Override
        public void updateProgressDescription(String description) {
            notifyPreloader(new MyPreloaderNotification(description));
        }

        @Override
        public void increaseMaxProgress(int max) {
            total += max;
            notifyPreloader(new MyPreloaderNotification(this.progress / total));
        }

        @Override
        public void increaseProgress(int progress, String description) {
            this.progress += progress;
            notifyPreloader(new MyPreloaderNotification(this.progress / total, description));
            if (!Boolean.getBoolean("quick")) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void closePreloader() {
            notifyPreloader(new MyClosePreloaderNotification());
        }
    };
}
