package cz.stechy.drd;

import com.google.firebase.database.FirebaseDatabase;
import com.sun.javafx.application.LauncherImpl;
import cz.stechy.drd.R.FXML;
import cz.stechy.drd.model.MyPreloaderNotification;
import cz.stechy.drd.util.UTF8ResourceBundleControl;
import cz.stechy.screens.ScreenManager;
import cz.stechy.screens.ScreenManagerConfiguration;
import cz.stechy.screens.base.IMainScreen;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vstupní bod aplikace
 */
public class App extends Application {

    // region Constants

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    private static final String BASE_FXML = "root.fxml";
    private static final String FOLDER_FXML = "fxml";
    private static final String FILE_CSS = "css/style.css";
    private static final String FOLDER_LANG = "lang";
    private static final String LANG_FILE_CONVENTION = "lang.translate";
    private static final String FILE_CONFIG = "config.properties";
    private static final String[] SCREENS_BLACKLIST = {
        R.FXML.DEFAULT_STAFF_LEFT, R.FXML.DEFAULT_STAFF_RIGHT};

    // endregion

    static {
        // Pro rychlé spuštění - přeskočí úvodní splashscreen
        System.setProperty("quick", "true");
    }

    // region Variables

    protected Context context;

    protected ScreenManager manager;

    // endregion

    // region Private methods

    /**
     * Inicializuje screen manager
     */
    private void initScreenManager() {
        ScreenManagerConfiguration configuration = new ScreenManagerConfiguration.Builder()
            .baseFxml(App.class.getClassLoader().getResource(BASE_FXML))
            .fxml(App.class.getClassLoader().getResource(FOLDER_FXML))
            .css(App.class.getClassLoader().getResource(FILE_CSS))
            .lang(App.class.getClassLoader().getResource(FOLDER_LANG))
            .config(App.class.getClassLoader().getResource(FILE_CONFIG))
            .build();
        manager = new ScreenManager(configuration);
        ResourceBundle resources = ResourceBundle.getBundle(
            LANG_FILE_CONVENTION, Locale.getDefault(), new UTF8ResourceBundleControl());
        manager.setResources(resources);
        manager.addScreensToBlacklist(SCREENS_BLACKLIST);
    }

    /**
     * Vrátí konfiguraci aplikace
     *
     * @return {@link Properties} obsahující konfiguraci aplikace
     */
    private Properties getProperties() {
        final Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(
                manager.getScreenManagerConfiguration().config.toExternalForm()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

    /**
     * Pomalá inicializace nasazená do produkčního prostředí
     *
     * @throws Exception Pokud se inicializace nepovede
     */
    private void lazyInit() throws Exception {
        // Pouze pro soutěž
        final long start = System.nanoTime();
        notifyPreloader(new MyPreloaderNotification("Inicializace aplikace..."));
        Thread.sleep(1000);
        initScreenManager();
        notifyPreloader(new MyPreloaderNotification("Inicializace databáze"));
        Thread.sleep(1000);
        context = new Context(getProperties(), manager.getResources());
        notifier.increaseMaxProgress(context.getServiceCount());
        context.init(notifier);
        manager.setControllerFactory(new ControllerFactory(context));

        notifyPreloader(new MyPreloaderNotification(0.99, "Dokončování..."));
        Thread.sleep(1000);

        // Pouze pro soutěž
        final long end = System.nanoTime();
        final long delta = (end - start) / 1000000;
        final long seconds = 4000;
        if (delta < seconds) {
            Thread.sleep(seconds - delta);
        }
        notifyPreloader(new MyPreloaderNotification(1, "Done"));
        Thread.sleep(500);
    }

    /**
     * Rychlá inicializace bez zbytečného zdržování
     * Určeno pro vývoj
     *
     * @throws Exception Pokud se inicializace nepovede
     */
    private void quickInit() throws Exception {
        // Pouze pro soutěž
        notifyPreloader(new MyPreloaderNotification("Inicializace aplikace..."));
        initScreenManager();
        notifyPreloader(new MyPreloaderNotification("Inicializace databáze"));
        context = new Context(getProperties(), manager.getResources());
        notifier.increaseMaxProgress(context.getServiceCount());
        context.init(notifier);
        manager.setControllerFactory(new ControllerFactory(context));

        notifyPreloader(new MyPreloaderNotification(0.99, "Dokončování..."));
        notifyPreloader(new MyPreloaderNotification(1, "Done"));
    }

    // endregion

    public static void main(String[] args) {
        logger.info("Spouštím aplikaci...");

        if (Boolean.getBoolean("quick")) {
            launch(args);
        } else {
            LauncherImpl.launchApplication(App.class, AppPreloader.class, args);
        }
    }

    @Override
    public void init() throws Exception {
        if (Boolean.getBoolean("quick")) {
            quickInit();
        } else {
            lazyInit();
        }
    }

    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(manager.getScreenManagerConfiguration().baseFxml);
        loader.setResources(manager.getResources());
        Parent parent = loader.load();
        IMainScreen controlledScreen = loader.getController();
        manager.setMainScreen(controlledScreen);
        manager.loadScreens();
        manager.showNewDialog(parent, primaryStage);
        manager.showScreen(FXML.MAIN, null);
        primaryStage.setOnCloseRequest(event -> {
            logger.info("Ukončuji aplikaci");
            FirebaseDatabase.getInstance().goOffline();
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e) {}
            //ThreadPool.getInstance().shutDown();
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
    };
}
