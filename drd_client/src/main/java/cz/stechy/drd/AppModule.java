package cz.stechy.drd;

import com.google.inject.AbstractModule;
import cz.stechy.drd.annotations.AppDirectory;
import cz.stechy.drd.annotations.ConfigFile;
import cz.stechy.drd.db.SQLite;
import cz.stechy.drd.db.TableFactory;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.db.base.ITableFactory;
import cz.stechy.drd.db.base.ITableWrapperFactory;
import cz.stechy.drd.db.base.TableDefinitionsFactory;
import cz.stechy.drd.db.base.TableWrapperFactory;
import cz.stechy.drd.service.dice.DiceServiceProvider;
import cz.stechy.drd.service.dice.IDiceService;
import cz.stechy.drd.util.UTF8ResourceBundleControl;
import cz.stechy.screens.ScreenManager;
import cz.stechy.screens.ScreenManagerConfiguration;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

public class AppModule extends AbstractModule {

    private static final String FOLDER_FXML = "fxml";
    private static final String FILE_CSS = "css/style.css";
    private static final String FILE_JFOENIX_FONTS = "/css/jfoenix-fonts.css";
    private static final String FILE_JFOENIX_DESIGN = "/css/jfoenix-design.css";
    private static final String FILE_COMPONENTS = "css/components.css";
    private static final String FILE_TABLE_VIEW = "css/table-view.css";
    private static final String FOLDER_LANG = "lang";
    private static final String LANG_FILE_CONVENTION = "lang.translate";
    private static final String FILE_CONFIG = "config.properties";

    private static final String CREDENTAILS_APP_NAME = "drd_helper";
    private static final String CREDENTAILS_APP_VERSION = "1.0";
    private static final String CREDENTILS_APP_AUTHOR = "stechy1";
    private static final String CONFIG_FILE_NAME = "config.properties";
    private static final String DEFAULT_VALUE_DATABASE = "database.sqlite";

    private static final AppDirs APP_DIRS = AppDirsFactory.getInstance();

    private ScreenManagerConfiguration getConfiguration() {
        return new ScreenManagerConfiguration.Builder()
            .fxml(App.class.getClassLoader().getResource(FOLDER_FXML))
            .css(App.class.getResource(FILE_JFOENIX_FONTS))
            .css(App.class.getResource(FILE_JFOENIX_DESIGN))
            .css(App.class.getClassLoader().getResource(FILE_TABLE_VIEW))
            .css(App.class.getClassLoader().getResource(FILE_CSS))
            .css(App.class.getClassLoader().getResource(FILE_COMPONENTS))
            .lang(App.class.getClassLoader().getResource(FOLDER_LANG))
            .config(App.class.getClassLoader().getResource(FILE_CONFIG))
            .build();
    }

    @Override
    protected void configure() {
        final File applicationDirectory = new File(APP_DIRS.getUserDataDir(CREDENTAILS_APP_NAME, CREDENTAILS_APP_VERSION, CREDENTILS_APP_AUTHOR));

        bind(ScreenManager.class).toInstance(new ScreenManager(getConfiguration()));
        bind(ResourceBundle.class).toInstance(ResourceBundle.getBundle(LANG_FILE_CONVENTION, Locale.getDefault(), new UTF8ResourceBundleControl()));
        bind(File.class).annotatedWith(AppDirectory.class).toProvider(() -> applicationDirectory);
        bind(File.class).annotatedWith(ConfigFile.class).toProvider(() -> new File(applicationDirectory, CONFIG_FILE_NAME));
        bind(Database.class).toInstance(new SQLite(new File(applicationDirectory, DEFAULT_VALUE_DATABASE).toString(), 1));
        bind(IAppSettings.class).to(AppSettings.class).asEagerSingleton();

        bind(IDiceService.class).toProvider(DiceServiceProvider.class).asEagerSingleton();

        bind(ITableDefinitionsFactory.class).to(TableDefinitionsFactory.class).asEagerSingleton();
        bind(ITableWrapperFactory.class).to(TableWrapperFactory.class).asEagerSingleton();
        bind(ITableFactory.class).to(TableFactory.class).asEagerSingleton();
    }
}
