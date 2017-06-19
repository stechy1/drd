package cz.stechy.drd.model;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.FirebaseDatabase;
import cz.stechy.drd.model.db.AdvancedDatabaseManager;
import cz.stechy.drd.model.db.DatabaseManager;
import cz.stechy.drd.model.db.SQLite;
import cz.stechy.drd.model.db.base.Database;
import cz.stechy.drd.model.persistent.ArmorManager;
import cz.stechy.drd.model.persistent.BackpackManager;
import cz.stechy.drd.model.persistent.GeneralItemManager;
import cz.stechy.drd.model.persistent.HeroManager;
import cz.stechy.drd.model.persistent.MeleWeaponManager;
import cz.stechy.drd.model.persistent.RangedWeaponManager;
import cz.stechy.drd.model.persistent.UserManager;
import cz.stechy.drd.util.Translator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
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
    private final Map<String, DatabaseManager> managerMap = new HashMap<>(MANAGERS_COUNT);
    // Jediný manažer, který nebude v mapě
    private final UserManager userManager;
    private final ResourceBundle resources;

    // Překladač aplikace
    private Translator translator;

    // endregion

    /**
     * Vytvoří nový kontext apliakce
     *
     * @param databaseName Název databáze
     */
    public Context(String databaseName, ResourceBundle resources) throws FileNotFoundException {
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
        userManager = new UserManager(FirebaseDatabase.getInstance());
        initManagers();
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
        managerMap.put(MANAGER_HERO, initManager(HeroManager.class));
        managerMap.put(MANAGER_WEAPON_MELE, initManager(MeleWeaponManager.class));
        managerMap.put(MANAGER_WEAPON_RANGED, initManager(RangedWeaponManager.class));
        managerMap.put(MANAGER_ARMOR, initManager(ArmorManager.class));
        managerMap.put(MANAGER_GENERAL, initManager(GeneralItemManager.class));
        managerMap.put(MANAGER_BACKPACK, initManager(BackpackManager.class));
    }

    @SuppressWarnings("unchecked")
    public <T> T getManager(String name) {
        return (T) managerMap.get(name);
    }

    @SuppressWarnings("unchecked")
    private DatabaseManager initManager(Class clazz) {
        try {
            DatabaseManager manager = (DatabaseManager) clazz.getConstructor(Database.class)
                .newInstance(database);
            if (manager instanceof AdvancedDatabaseManager) {
                ((AdvancedDatabaseManager) manager)
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

    public UserManager getUserManager() {
        return userManager;
    }
}
