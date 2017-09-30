package cz.stechy.drd.controller.main;

import cz.stechy.drd.AppSettings;
import cz.stechy.drd.R;
import cz.stechy.drd.controller.InjectableChild;
import cz.stechy.drd.controller.hero.HeroHelper;
import cz.stechy.drd.controller.hero.levelup.LevelUpController;
import cz.stechy.drd.controller.hero.opener.HeroOpenerController;
import cz.stechy.drd.controller.main.defaultstaff.DefaultStaffController;
import cz.stechy.drd.controller.main.inventory.InventoryController;
import cz.stechy.drd.controller.main.profession.ProfessionController;
import cz.stechy.drd.controller.moneyxp.MoneyXpController;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.inventory.InventoryHelper;
import cz.stechy.drd.model.persistent.HeroService;
import cz.stechy.drd.model.persistent.UserService;
import cz.stechy.drd.model.user.User;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import cz.stechy.screens.Notification;
import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kontroler pro hlavní screen hry
 */
public class MainController extends BaseController implements Initializable {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private static final int ACTION_NEW_HERO = 1;
    private static final int ACTION_LOAD_HERO = 2;
    private static final int ACTION_LOGIN = 3;
    private static final int ACTION_MONEY_EXPERIENCE = 4;
    private static final int ACTION_LEVEL_UP = 5;

    // endregion

    // region Variables

    // region FXML

    @FXML
    private DefaultStaffController defaultStaffController;

    @FXML
    private InventoryController inventoryController;

    @FXML
    private ProfessionController professionController;

    @FXML
    private VBox defaultStaff;

    @FXML
    private BorderPane inventory;

    @FXML
    private StackPane profession;

    @FXML
    private MenuItem menuLogin;

    @FXML
    private Button btnLevelUp;

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab tabProfession;

    // endregion

    private final BooleanProperty useFirebase = new SimpleBooleanProperty(this, "useFirebase", false);
    private final ReadOnlyObjectProperty<Hero> hero;
    private final ReadOnlyObjectProperty<User> user;
    private final HeroService heroService;
    private final UserService userService;

    private MainScreen[] controllers;
    private String title;
    private String loginText;
    private String logoutText;
    private String loginSuccess;
    private String actionFailed;
    private String heroNotFound;

    // endregion

    // region Constructors

    public MainController(HeroService heroService, UserService userService, AppSettings settings) {
        this.heroService = heroService;
        this.userService = userService;
        this.hero = heroService.heroProperty();
        this.user = userService.userProperty();
        settings.addListener(R.Config.USE_ONLINE_DATABASE, this::useOnlineDatabaseHandler);
        useFirebase.set(Boolean.parseBoolean(settings.getProperty(R.Config.USE_ONLINE_DATABASE)));
    }

    // endregion

    // region Private methods

    /**
     * Nastaví vlastnosti menu tlačítku pro přihlášení/odhlášení
     */
    private void bindMenuLogin(BooleanProperty loggedProperty) {
        if (loggedProperty == null) {
            this.menuLogin.textProperty().unbind();
            this.menuLogin.onActionProperty().unbind();

            this.menuLogin.setText(loginText);
            this.menuLogin.setOnAction(this::handleMenuLogin);
            return;
        }

        this.menuLogin.textProperty().bind(Bindings
            .when(loggedProperty)
            .then(logoutText)
            .otherwise(loginText));
        this.menuLogin.onActionProperty().bind(Bindings
            .when(loggedProperty)
            .then(new SimpleObjectProperty<EventHandler<ActionEvent>>(this::handleMenuLogout))
            .otherwise(new SimpleObjectProperty<>(this::handleMenuLogin)));
    }

    private void resetChildScreensAndHero() {
        closeChildScreens();
        heroService.resetHero();
    }

    // region Method handlers

    private void useOnlineDatabaseHandler(PropertyChangeEvent event) {
        useFirebase.set(Boolean.parseBoolean((String) event.getNewValue()));
    }

    private void levelUpHandler(ObservableValue<? extends Boolean> observable,
        Boolean oldValue, Boolean newValue) {
        showNotification(new Notification("levelUp"));
    }

    private void heroHandler(ObservableValue<? extends Hero> observable, Hero oldValue, Hero newValue) {
        if (newValue == null) {
            if (oldValue != null) {
                oldValue.levelUpProperty().removeListener(this::levelUpHandler);
            }
            btnLevelUp.visibleProperty().unbind();
            btnLevelUp.setVisible(false);
        } else {
            newValue.levelUpProperty().addListener(this::levelUpHandler);
            btnLevelUp.visibleProperty().bind(newValue.levelUpProperty());
        }

        tabPane.getSelectionModel().selectFirst();
    }

    private void userHandler(ObservableValue<? extends User> observable, User oldValue, User newValue) {
        if (newValue == null) {
            bindMenuLogin(null);
        } else {
            bindMenuLogin(newValue.loggedProperty());
            newValue.loggedProperty().addListener((observable1, oldValue1, newValue1) ->
                resetChildScreensAndHero());
        }

        resetChildScreensAndHero();
    }

    // endregion

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title = resources.getString(R.Translate.MAIN_TITLE);
        this.loginText = resources.getString(R.Translate.MAIN_MENU_FILE_LOGIN);
        this.logoutText = resources.getString(R.Translate.MAIN_MENU_FILE_LOGOUT);
        this.loginSuccess = resources.getString(R.Translate.NOTIFY_LOGIN_SUCCESS);
        this.actionFailed = resources.getString(R.Translate.ACTION_FAILED);
        this.heroNotFound = resources.getString(R.Translate.NOTIFY_HERO_NOT_FOUND);

        this.controllers = new MainScreen[]{
            defaultStaffController,
            inventoryController,
            professionController
        };

        for (MainScreen controller : controllers) {
            controller.setHero(hero);
            if (controller instanceof InjectableChild) {
                ((InjectableChild) controller).injectParent(this);
            }
        }

        tabProfession.disableProperty().bind(this.hero.isNull());
        menuLogin.disableProperty().bind(useFirebase.not());

        this.hero.addListener(this::heroHandler);
        this.user.addListener(this::userHandler);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        heroService.resetHero();
    }

    @Override
    protected void onResume() {
        setTitle(title);
        setScreenSize(880, 700);
    }

    @Override
    protected void onScreenResult(int statusCode, int actionId, Bundle bundle) {
        switch (actionId) {
            case ACTION_NEW_HERO:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }

                closeChildScreens();
                final Hero hero = HeroHelper.fromBundle(bundle);
                hero.setAuthor((user.get() != null) ? user.get().getName() : "");
                ObservableList<InventoryHelper.ItemRecord> itemsToInventory = bundle
                    .get(HeroHelper.INVENTORY);
                try {
                    heroService.insert(hero, itemsToInventory);
                    heroService.load(hero.getId());
                } catch (DatabaseException e) {
                    LOGGER.warn("Nepodařilo se vytvořit nového hrdinu");
                }
                break;
            case ACTION_LOAD_HERO:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }

                final String heroId = bundle.getString(HeroOpenerController.HERO);
                try {
                    this.heroService.load(heroId);
                } catch (DatabaseException e) {
                    LOGGER.warn(e.getMessage());
                    showNotification(new Notification(heroNotFound));
                }
                break;
            case ACTION_LOGIN:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }

                heroService.resetHero();
                showNotification(new Notification(loginSuccess));
                break;
            case ACTION_MONEY_EXPERIENCE:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }
                final Hero heroCopy = this.hero.get().duplicate();
                heroCopy.getMoney().setRaw(bundle.getInt(MoneyXpController.MONEY));
                heroCopy.getExperiences().setActValue(bundle.getInt(MoneyXpController.EXPERIENCE));
                try {
                    heroService.update(heroCopy);
                } catch (DatabaseException e) {
                    LOGGER.warn("Hrdinu se nepodařilo aktualizovat", e);
                    showNotification(new Notification(actionFailed));
                }
                break;
            case ACTION_LEVEL_UP:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }

                final Hero clone = this.hero.get().duplicate();
                HeroHelper.levelUp(clone, bundle);
                try {
                    heroService.update(clone);
                    showNotification(new Notification("Hrdina povýšil na novou úroveň"));
                } catch (DatabaseException e) {
                    LOGGER.warn("Hrdinovi se nepodařilo přejít na novou úroveň", e);
                    showNotification(new Notification(actionFailed));
                }
                break;
        }
    }

    @Override
    protected void onClose() {
        closeChildScreens();
    }

    // region Button handlers

    @FXML
    private void handleMenuNewHero(ActionEvent actionEvent) {
        startNewDialogForResult(R.FXML.HERO_CREATOR_1, ACTION_NEW_HERO);
    }

    @FXML
    private void handleMenuLoadHero(ActionEvent actionEvent) {
        startNewDialogForResult(R.FXML.OPEN_HERO, ACTION_LOAD_HERO);
    }

    @FXML
    private void handleExportHero(ActionEvent actionEvent) {

    }

    @FXML
    private void handleImportHero(ActionEvent actionEvent) {

    }

    @FXML
    private void handleMenuCloseHero(ActionEvent actionEvent) {
        closeChildScreens();
        heroService.resetHero();
    }

    @FXML
    private void handleMenuLogin(ActionEvent actionEvent) {
        startNewDialogForResult(R.FXML.LOGIN, ACTION_LOGIN);
    }

    @FXML
    private void handleMenuLogout(ActionEvent actionEvent) {
        userService.logout();
    }

    @FXML
    private void handleCloseApplication(ActionEvent actionEvent) {
        final Stage stage = (Stage) getRoot().getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @FXML
    private void handleMenuDice(ActionEvent actionEvent) {
        startNewDialog(R.FXML.DICE);
    }

    @FXML
    private void handleMenuMoney(ActionEvent actionEvent) {
        startNewDialogForResult(R.FXML.MONEY_XP, ACTION_MONEY_EXPERIENCE);
    }

    @FXML
    private void handleMenuBestiary(ActionEvent actionEvent) {
        startNewDialog(R.FXML.BESTIARY);
    }

    @FXML
    private void handleMenuShop(ActionEvent actionEvent) {
        startNewDialog(R.FXML.SHOP1);
    }

    @FXML
    private void handleMenuCollections(ActionEvent actionEvent) {
        startNewDialog(R.FXML.COLLECTIONS);
    }

    @FXML
    private void handleMenuSpellBook(ActionEvent actionEvent) {
        startNewDialog(R.FXML.SPELLBOOK);
    }

    @FXML
    private void handleMenuFight(ActionEvent actionEvent) {
        startNewDialog(R.FXML.FIGHT);
    }

    @FXML
    private void handleMenuSettings(ActionEvent actionEvent) {
        startNewDialog(R.FXML.SETTINGS);
    }

    @FXML
    private void handleMenuAbout(ActionEvent actionEvent) {
        startNewDialog(R.FXML.ABOUT);
    }

    @FXML
    private void handleMenuChangelog(ActionEvent actionEvent) {

    }

    @FXML
    private void handleMenuHelp(ActionEvent actionEvent) {
        startNewDialog(R.FXML.HELP);
    }

    @FXML
    private void handleMenuLevelUp(ActionEvent actionEvent) {
        final Bundle bundle = new Bundle();
        bundle.put(LevelUpController.HERO, hero.get());
        startNewDialogForResult(R.FXML.LEVELUP, ACTION_LEVEL_UP, bundle);
    }

    // endregion
}
