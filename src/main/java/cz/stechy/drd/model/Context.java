package cz.stechy.drd.model;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.FirebaseDatabase;
import cz.stechy.drd.model.db.AdvancedDatabaseService;
import cz.stechy.drd.model.db.DatabaseService;
import cz.stechy.drd.model.db.SQLite;
import cz.stechy.drd.model.db.base.Database;
import cz.stechy.drd.model.persistent.ArmorService;
import cz.stechy.drd.model.persistent.BackpackService;
import cz.stechy.drd.model.persistent.GeneralItemService;
import cz.stechy.drd.model.persistent.HeroService;
import cz.stechy.drd.model.persistent.MeleWeaponService;
import cz.stechy.drd.model.persistent.RangedWeaponService;
import cz.stechy.drd.model.persistent.UserService;
import cz.stechy.drd.model.service.KeyboardService;
import cz.stechy.drd.util.Translator;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
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
    private static final String FIREBASE_CREDENTAILS = "/other/firebase_credentials.json";
    private static final String CREDENTAILS_APP_NAME = "drd_helper";
    private static final String CREDENTAILS_APP_VERSION = "1.0";
    private static final String CREDENTILS_APP_AUTHOR = "stechy1";
    private static final char SEPARATOR = File.separatorChar;
    private static final int MANAGERS_COUNT = 4;
    private static final int EXECUTORS_COUNT = 4;

    // Databáze
    private static final AppDirs appDirs = AppDirsFactory.getInstance();

    // Názvy jednotlivých manažerů
    public static final String MANAGER_HERO = "hero";
    public static final String MANAGER_WEAPON_MELE = "mele";
    public static final String MANAGER_WEAPON_RANGED = "ranged";
    public static final String MANAGER_ARMOR = "armor";
    public static final String MANAGER_GENERAL = "general";
    public static final String MANAGER_BACKPACK = "backpack";

    // endregion

    // region Variables

    // Databáze
    private final Database database;
    // Pracovní adresář, kam můžu ukládat potřebné soubory
    private final File appDirectory;
    // Mapa obsahující všechny manažery
    private final Map<String, DatabaseService> managerMap = new HashMap<>(MANAGERS_COUNT);
    // Jediný manažer, který nebude v mapě
    private final UserService userService;
    private final ResourceBundle resources;

    // Překladač aplikace
    private Translator translator;

    // endregion

    /**
     * Vytvoří nový kontext apliakce
     *
     * @param databaseName Název databáze
     */
    public Context(String databaseName, ResourceBundle resources) throws Exception {
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
        database = new SQLite(appDirectory.getPath() + SEPARATOR + databaseName);

        initFirebase();
        userService = new UserService(FirebaseDatabase.getInstance());
        initManagers();
        initNativeHandlers();
    }

    /**
     * Inicializace firebase služby
     */
    private void initFirebase() {
        try {
            InputStream serviceAccount = getClass().getResourceAsStream(FIREBASE_CREDENTAILS);

            Map<String, Object> auth = new HashMap<>();
            auth.put("uid", "my_resources");

            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl(FIREBASE_URL)
                .setDatabaseAuthVariableOverride(auth)
                .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            logger.info("Nemůžu se připojit k firebase", e);
        }
    }

    /**
     * Inicializace všech správců předmětů
     */
    private void initManagers() {
        managerMap.put(MANAGER_HERO, initManager(HeroService.class));
        managerMap.put(MANAGER_WEAPON_MELE, initManager(MeleWeaponService.class));
        managerMap.put(MANAGER_WEAPON_RANGED, initManager(RangedWeaponService.class));
        managerMap.put(MANAGER_ARMOR, initManager(ArmorService.class));
        managerMap.put(MANAGER_GENERAL, initManager(GeneralItemService.class));
        managerMap.put(MANAGER_BACKPACK, initManager(BackpackService.class));
    }

    private void initNativeHandlers() throws Exception {
        // Get the logger for "org.jnativehook" and set the level to warning.
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(java.util.logging.Level.WARNING);

        // Don't forget to disable the parent handlers.
        logger.setUseParentHandlers(false);

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            throw new Exception(e);
        }

        GlobalScreen.addNativeKeyListener(new KeyboardService());
    }

    @SuppressWarnings("unchecked")
    public <T> T getManager(String name) {
        return (T) managerMap.get(name);
    }

    @SuppressWarnings("unchecked")
    private DatabaseService initManager(Class clazz) {
        try {
            DatabaseService manager = (DatabaseService) clazz.getConstructor(Database.class)
                .newInstance(database);
            if (manager instanceof AdvancedDatabaseService) {
                ((AdvancedDatabaseService) manager)
                    .setFirebaseDatabase(FirebaseDatabase.getInstance());
            }
            manager.createTable();
            manager.selectAll();
            return manager;
        } catch (Exception e) {
            // nikdy by se nemělo stát
            // pokud se ale tak stane, tak aplikace není schopná běhu
            logger.error("Chyba při inicializaci manažeru", e);
            Platform.exit();
            return null;
        }
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
}
