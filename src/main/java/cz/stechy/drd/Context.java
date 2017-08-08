package cz.stechy.drd;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.FirebaseDatabase;
import cz.stechy.drd.model.db.AdvancedDatabaseService;
import cz.stechy.drd.model.db.BaseDatabaseService;
import cz.stechy.drd.model.db.DatabaseService;
import cz.stechy.drd.model.db.FirebaseWrapper;
import cz.stechy.drd.model.db.SQLite;
import cz.stechy.drd.model.db.base.Database;
import cz.stechy.drd.model.persistent.ArmorService;
import cz.stechy.drd.model.persistent.BackpackService;
import cz.stechy.drd.model.persistent.BestiaryService;
import cz.stechy.drd.model.persistent.GeneralItemService;
import cz.stechy.drd.model.persistent.HeroService;
import cz.stechy.drd.model.persistent.MeleWeaponService;
import cz.stechy.drd.model.persistent.RangedWeaponService;
import cz.stechy.drd.model.persistent.UserService;
import cz.stechy.drd.util.Translator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
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
    private static final Logger logger = LoggerFactory.getLogger(Context.class);

    private static final String FIREBASE_URL = "https://drd-personal-diary.firebaseio.com";
    private static final String FIREBASE_CREDENTIALS = "";
    private static final String CREDENTAILS_APP_NAME = "drd_helper";
    private static final String CREDENTAILS_APP_VERSION = "1.0";
    private static final String CREDENTILS_APP_AUTHOR = "stechy1";
    private static final String CONFIG_FILE_NAME = "config.properties";
    private static final char SEPARATOR = File.separatorChar;
    private static final int SERVICES_COUNT = 7;

    private static final String DEFAULT_VALUE_DATABASE = "database.sqlite";

    // Pomocná reference pro žískání uživatelských složek v různých systémech
    private static final AppDirs appDirs = AppDirsFactory.getInstance();

    // Názvy jednotlivých služeb
    public static final String SERVICE_HERO = "hero";
    public static final String SERVICE_WEAPON_MELE = "mele";
    public static final String SERVICE_WEAPON_RANGED = "ranged";
    public static final String SERVICE_ARMOR = "armor";
    public static final String SERVICE_GENERAL = "general";
    public static final String SERVICE_BACKPACK = "backpack";
    public static final String SERVICE_BESTIARY = "bestiary";

    // endregion

    // region Variables

    // Pomocný wrapper na pozdější inicializaci firebase databáze
    private final FirebaseWrapper firebaseWrapper = new FirebaseWrapper();
    // Databáze
    private final Database database;
    // Pracovní adresář, kam můžu ukládat potřebné soubory
    private final File appDirectory;
    // Mapa obsahující všechny služby
    private final Map<String, DatabaseService> serviceMap = new HashMap<>(SERVICES_COUNT);
    // Nastavení aplikace
    private final Properties configuration = new Properties();
    // Služba obsluhující uživatele
    private UserService userService;
    // Překad aplikace
    private final ResourceBundle resources;
    // Překladač aplikace
    private Translator translator;

    // endregion

    // region Constructors

    /**
     * Vytvoří nový kontext apliakce
     *
     * @param resources {@link ResourceBundle}
     * @throws Exception Pokud se inicializace kontextu nezdaří
     */
    Context(ResourceBundle resources) throws Exception {
        this.resources = resources;
        this.appDirectory = new File(appDirs
            .getUserDataDir(CREDENTAILS_APP_NAME, CREDENTAILS_APP_VERSION, CREDENTILS_APP_AUTHOR));
        if (!appDirectory.exists()) {
            if (!appDirectory.mkdirs()) {
                logger.error("Nepodařilo se vytvořit složku aplikace, zavírám...");
                Platform.exit();
            }
        }
        logger.info("Používám pracovní adresář: {}", appDirectory.getPath());
        loadConfiguration();
        database = new SQLite(appDirectory.getPath() + SEPARATOR + getDatabaseName());
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
     * Načte konfiguraci ze souboru
     */
    private void loadConfiguration() {
        final File configFile = getConfigFile();
        try {
            configuration.load(new FileInputStream(configFile));
        } catch (IOException e) {
            // TODO založit nový soubor s konfigurací
        }
    }

    /**
     * Vrátí název databáze přečtený z konfigurace
     *
     * @return Název databáze
     */
    private String getDatabaseName() {
        return getProperty(R.Config.OFFLINE_DATABASE_NAME, DEFAULT_VALUE_DATABASE);
    }

    /**
     * Inicializace firebase služby
     *
     * @param inputStream Stream dat s přístupovými údaji k databázi
     * @throws Exception Pokud se inicializace nezdařila
     */
    private void initFirebase(InputStream inputStream) throws Exception {
        final Map<String, Object> auth = new HashMap<>();
        auth.put("uid", "my_resources");

        final FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredential(FirebaseCredentials.fromCertificate(inputStream))
            .setDatabaseUrl(FIREBASE_URL)
            .setDatabaseAuthVariableOverride(auth)
            .build();

        FirebaseApp.initializeApp(options);
        firebaseWrapper.setFirebase(FirebaseDatabase.getInstance());
    }

    /**
     * Inicializace všech správců předmětů
     */
    private void initServices() {
        serviceMap.put(SERVICE_HERO, initService(HeroService.class));
        serviceMap.put(SERVICE_WEAPON_MELE, initService(MeleWeaponService.class));
        serviceMap.put(SERVICE_WEAPON_RANGED, initService(RangedWeaponService.class));
        serviceMap.put(SERVICE_ARMOR, initService(ArmorService.class));
        serviceMap.put(SERVICE_GENERAL, initService(GeneralItemService.class));
        serviceMap.put(SERVICE_BACKPACK, initService(BackpackService.class));
        serviceMap.put(SERVICE_BESTIARY, initService(BestiaryService.class));
    }

    @SuppressWarnings("unchecked")
    private DatabaseService initService(Class clazz) {
        try {
            DatabaseService service = (DatabaseService) clazz.getConstructor(Database.class)
                .newInstance(database);
            if (service instanceof AdvancedDatabaseService) {
                ((AdvancedDatabaseService) service)
                    .setFirebaseDatabase(firebaseWrapper);
            }
            service.createTable();
            service.selectAll();
            return service;
        } catch (Exception e) {
            // nikdy by se nemělo stát
            // pokud se ale tak stane, tak aplikace není schopná běhu
            logger.error("Chyba při inicializaci služby", e);
            Platform.exit();
            return null;
        }
    }

    /**
     * Zjistí, zda-li má aplikace inicializovat firebase databázi
     *
     * @return True, pokud se má firebase inicializovat, jinak False
     */
    private boolean useOnlineDatabase() {
        return Boolean.parseBoolean(getProperty(R.Config.USE_ONLINE_DATABASE, "false"));
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
        BaseDatabaseService.setNotifier(notifier);
        userService = new UserService(firebaseWrapper);
        initServices();
        if (useOnlineDatabase()) {
            try {
                initFirebase(getProperty(R.Config.ONLINE_DATABASE_CREDENTIALS_PATH,
                    FIREBASE_CREDENTIALS));
            } catch (Exception ex) {
                // Pokud se nepodaří inicializovat firebase při startu, tak se prakticky nic neděje
                // aplikace může bez firebase běžet
                // Pro jistotu nastavíme, že se přiště inicializace konat nebude
                setProperty(R.Config.USE_ONLINE_DATABASE, "false");
            }
        }
    }

    /**
     * Vrátí celkový počet služeb, tedy i tabulek v aplikaci
     *
     * @return Celkový počet služeb (tabulek) v aplikaci
     */
    int getServiceCount() {
        return SERVICES_COUNT;
    }

    /**
     * Uloží aktuální konfiguraci do souboru
     */
    void saveConfiguration() {
        try {
            configuration.store(new FileOutputStream(getConfigFile()), "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // endregion

    // region Public methods

    /**
     * Inicializuje firebase databázi
     *
     * @param credentialsPath Cesta k souboru s přístupovými údaji k databázi
     * @throws Exception Pokud se inicializace nezdařila
     */
    public void initFirebase(String credentialsPath) throws Exception {
        initFirebase(new File(credentialsPath));
    }

    /**
     * Inicializuje firebase databázi
     *
     * @param credentialsFile Soubor s přístupovými údaji k databázi
     * @throws Exception Pokud se inicializace nezdařila
     */
    public void initFirebase(File credentialsFile) throws Exception {
        initFirebase(new FileInputStream(credentialsFile));
    }

    // endregion

    // region Getters & Setters

    @SuppressWarnings("unchecked")
    public <T> T getService(String name) {
        return (T) serviceMap.get(name);
    }

    /**
     * @return {@link Translator}
     */
    public Translator getTranslator() {
        if (translator == null) {
            translator = new Translator(resources);
        }

        return translator;
    }

    public UserService getUserService() {
        return userService;
    }

    /**
     * Vrátí záznam na základě klíče. Použijte pouze v případě, že jste si jistí, že záznam opravdu
     * existuje
     *
     * @param key Klič záznamu
     * @return Hodnotu záznamu
     */
    public String getProperty(String key) {
        return configuration.getProperty(key);
    }

    /**
     * Získá záznam z konfiguračního souboru na základě klíče. Pokud záznam neexistuje, vytvoří se
     * nový s výchozí hodnotou.
     *
     * @param key Klíč záznamu
     * @param defaultValue Výchozí hodnota, která se má vrátit, kdy záznam neexistuje
     * @return {@link String}
     */
    public String getProperty(String key, String defaultValue) {
        final String property = configuration.getProperty(key, defaultValue);
        configuration.setProperty(key, property);
        return property;
    }

    /**
     * Uloží záznam
     *
     * @param key Klíč
     * @param value Hodnota
     */
    public void setProperty(String key, String value) {
        configuration.setProperty(key, value);
    }

    /**
     * Vrátí konfiguraci aplikace s důležitými konstantami
     *
     * @return {@link Properties}
     */
    @Deprecated
    public Properties getConfiguration() {
        return configuration;
    }

    /**
     * Ukončí spojení online databáze s internetem
     */
    public void closeFirebase() {
        firebaseWrapper.closeDatabase();
    }

    // endregion
}
