package cz.stechy.drd;

import cz.stechy.drd.R.Config;
import cz.stechy.drd.di.DiContainer;
import cz.stechy.drd.di.IDependencyManager;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.db.DatabaseService;
import cz.stechy.drd.model.db.FirebaseWrapper;
import cz.stechy.drd.model.db.SQLite;
import cz.stechy.drd.model.db.base.Database;
import cz.stechy.drd.model.persistent.ArmorService;
import cz.stechy.drd.model.persistent.BackpackService;
import cz.stechy.drd.model.persistent.BestiaryService;
import cz.stechy.drd.model.persistent.GeneralItemService;
import cz.stechy.drd.model.persistent.HeroService;
import cz.stechy.drd.model.persistent.ItemCollectionService;
import cz.stechy.drd.model.persistent.MeleWeaponService;
import cz.stechy.drd.model.persistent.RangedWeaponService;
import cz.stechy.drd.model.persistent.SpellBookService;
import cz.stechy.drd.model.persistent.UserService;
import cz.stechy.drd.util.Translator;
import java.io.File;
import java.util.ResourceBundle;
import javafx.application.Platform;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kontext aplikace
 */
public class Context {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(Context.class);

    private static final String FIREBASE_CREDENTIALS = "";
    private static final String CREDENTAILS_APP_NAME = "drd_helper";
    private static final String CREDENTAILS_APP_VERSION = "1.0";
    private static final String CREDENTILS_APP_AUTHOR = "stechy1";
    private static final String CONFIG_FILE_NAME = "config.properties";
    private static final char SEPARATOR = File.separatorChar;

    private static final String DEFAULT_VALUE_DATABASE = "database.sqlite";

    // Pomocná reference pro získání uživatelských složek v různých systémech
    private static final AppDirs APP_DIRS = AppDirsFactory.getInstance();

    private static final Class[] SERVICES = new Class[]{
        HeroService.class,
        MeleWeaponService.class,
        RangedWeaponService.class,
        ArmorService.class,
        GeneralItemService.class,
        BackpackService.class,
        BestiaryService.class,
        SpellBookService.class
    };

    // endregion

    // region Variables

    // DI kontejner obsahující všechny služby v aplikaci
    private final IDependencyManager container = new DiContainer();
    // Pomocný wrapper na pozdější inicializaci firebase databáze
    private final FirebaseWrapper firebaseWrapper = new FirebaseWrapper();
    // Pracovní adresář, kam můžu ukládat potřebné soubory
    private final File appDirectory;
    // Nastavení aplikace
    private final AppSettings settings;

    // endregion

    // region Constructors

    /**
     * Vytvoří nový kontext apliakce
     *
     * @param resources {@link ResourceBundle}
     * @throws Exception Pokud se inicializace kontextu nezdaří
     */
    Context(ResourceBundle resources) throws Exception {
        this.appDirectory = new File(APP_DIRS
            .getUserDataDir(CREDENTAILS_APP_NAME, CREDENTAILS_APP_VERSION, CREDENTILS_APP_AUTHOR));
        if (!appDirectory.exists()) {
            if (!appDirectory.mkdirs()) {
                LOGGER.error("Nepodařilo se vytvořit složku aplikace, zavírám...");
                Platform.exit();
            }
        }
        LOGGER.info("Používám pracovní adresář: {}", appDirectory.getPath());

        settings = new AppSettings(getConfigFile());
        container.addService(AppSettings.class, settings);

        // Získání aktuální verze databáze z nastavení
        final int localDatabaseVersion = Integer.parseInt(
            settings.getProperty(R.Config.DATABASE_VERSION, String.valueOf(R.DATABASE_VERSION)));
        settings.setProperty(Config.DATABASE_VERSION, String.valueOf(R.DATABASE_VERSION));

        final Database database = new SQLite(appDirectory.getPath() + SEPARATOR + getDatabaseName(),
            localDatabaseVersion);
        container.addService(Database.class, database);
        container.addService(Translator.class, new Translator(resources));
        container.addService(FirebaseWrapper.class, firebaseWrapper);
    }

    // endregion

    // region Private methods

    /**
     * Vrátí soubor s konfigurací aplikace
     *
     * @return {@link File} s konfigurací aplikace
     */
    private File getConfigFile() {
        return new File(appDirectory, CONFIG_FILE_NAME);
    }

    /**
     * Vrátí název databáze přečtený z konfigurace
     *
     * @return Název databáze
     */
    private String getDatabaseName() {
        return settings.getProperty(R.Config.OFFLINE_DATABASE_NAME, DEFAULT_VALUE_DATABASE);
    }

    /**
     * Inicializace všech správců předmětů
     */
    private void initServices() {
        // Inicializace jednotlivých služeb
        for (Class service : SERVICES) {
            final DatabaseService instance = container.getInstance(service);
            try {
                instance.createTable();
                instance.selectAll();
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }

        // Inicializace UserService
        container.getInstance(UserService.class);
        // Inicializace ItemCollectionService
        container.getInstance(ItemCollectionService.class);
    }

    /**
     * Zjistí, zda-li má aplikace inicializovat firebase databázi
     *
     * @return True, pokud se má firebase inicializovat, jinak False
     */
    private boolean useOnlineDatabase() {
        return Boolean.parseBoolean(settings.getProperty(R.Config.USE_ONLINE_DATABASE, "false"));
    }

    /**
     * Inicializuje firebase databázi
     *
     * @param credentialsPath Cesta k souboru s přístupovými údaji k databázi
     * @throws Exception Pokud se inicializace nezdařila
     */
    private void initFirebase(String credentialsPath) throws Exception {
        firebaseWrapper.initDatabase(new File(credentialsPath));
    }

    // endregion

    // region Package private methods

    /**
     * Inicializuje tabulky databáze
     *
     * @param notifier {@link PreloaderNotifier}
     * @throws Exception Pokud se inicializace nezdaří
     */
    void init(PreloaderNotifier notifier) throws Exception {
        initServices();
        if (useOnlineDatabase()) {
            try {
                initFirebase(settings.getProperty(R.Config.ONLINE_DATABASE_CREDENTIALS_PATH,
                    FIREBASE_CREDENTIALS));
            } catch (Exception ex) {
                // Pokud se nepodaří inicializovat firebase při startu, tak se prakticky nic neděje
                // aplikace může bez firebase běžet
                // Pro jistotu nastavíme, že se přiště inicializace konat nebude
                settings.setProperty(R.Config.USE_ONLINE_DATABASE, "false");
            }
        }
    }

    /**
     * Vrátí celkový počet služeb, tedy i tabulek v aplikaci
     *
     * @return Celkový počet služeb (tabulek) v aplikaci
     */
    int getServiceCount() {
        return SERVICES.length + 1;
    }

    // endregion

    // region Public methods

    /**
     * Uloží aktuální konfiguraci do souboru
     */
    void saveConfiguration() {
        settings.save();
    }
    // endregion

    // region Getters & Setters

    /**
     * Ukončí spojení online databáze s internetem
     */
    public void closeFirebase() {
        firebaseWrapper.closeDatabase();
    }

    public IDependencyManager getContainer() {
        return container;
    }

    /**
     * Přidá do DI kontejneru preloader notifikátor
     *
     * @param notifier {@link PreloaderNotifier}
     */
    public void setPreloaderNotifier(PreloaderNotifier notifier) {
        container.addService(PreloaderNotifier.class, notifier);
    }

    // endregion
}
