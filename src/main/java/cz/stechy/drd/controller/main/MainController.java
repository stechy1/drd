package cz.stechy.drd.controller.main;

import cz.stechy.drd.R;
import cz.stechy.drd.controller.hero.creator.HeroCreatorHelper;
import cz.stechy.drd.controller.hero.creator.HeroCreatorHelper.ItemEntry;
import cz.stechy.drd.controller.hero.opener.HeroOpenerHelper;
import cz.stechy.drd.model.Context;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.persistent.HeroManager;
import cz.stechy.drd.model.persistent.UserManager;
import cz.stechy.drd.util.Translator;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kontroler pro hlavní screen hry
 */
public class MainController extends BaseController implements Initializable {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private static final int ACTION_NEW_HERO = 1;
    private static final int ACTION_LOAD_HERO = 2;
    private static final int ACTION_LOGIN = 3;

    // endregion

    // region Variables

    // region FXML

    @FXML
    private DefaultStaffController defaultStaffController;

    @FXML
    private InventoryController inventoryController;

    @FXML
    private VBox defaultStaff;

    @FXML
    private BorderPane inventory;

    // endregion

    private final ObjectProperty<Hero> hero;
    private final HeroManager heroManager;
    private final Translator translator;
    private final UserManager userManager;

    private MainScreen[] controllers;
    private String title;

    // endregion

    public MainController(Context context) {
        heroManager = context.getManager(Context.MANAGER_HERO);
        translator = context.getTranslator();
        userManager = context.getUserManager();
        hero = heroManager.getHero();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = resources.getString(R.Translate.MAIN_TITLE);

        controllers = new MainScreen[] {
            defaultStaffController,
            inventoryController
        };

        for (MainScreen controller : controllers) {
            controller.setHero(hero);
        }

        hero.setValue(new Hero.Builder().build());

    }

    @Override
    protected void onResume() {
        setTitle(title);
        setScreenSize(550, 440);
    }

    @Override
    protected void onScreenResult(int statusCode, int actionId, Bundle bundle) {
        switch (actionId) {
            case ACTION_NEW_HERO:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }
                closeChildScreens();
                Hero hero = HeroCreatorHelper.fromBundle(bundle);
                hero.setAuthor(userManager.getUser().getName());
                ObservableList<HeroCreatorHelper.ItemEntry> itemsToInventory = (ObservableList<ItemEntry>) bundle.get(HeroCreatorHelper.INVENTORY);
                try {
                    heroManager.insert(hero, itemsToInventory);
                    this.hero.setValue(hero);
                } catch (DatabaseException e) {
                    logger.warn("Nepodařilo se vytvořit nového hrdinu");
                }
                break;
            case ACTION_LOAD_HERO:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }
                this.hero.setValue((Hero) bundle.get(HeroOpenerHelper.HERO));
                break;
            case ACTION_LOGIN:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }

                break;
        }
    }

    // region Button handle

    public void handleMenuNewHero(ActionEvent actionEvent) {
        startNewDialogForResult(R.FXML.NEW_HERO_1, ACTION_NEW_HERO);
    }

    public void handleMenuLoadHero(ActionEvent actionEvent) {
        startNewDialogForResult(R.FXML.OPEN_HERO, ACTION_LOAD_HERO);
    }

    public void handleExportHero(ActionEvent actionEvent) {

    }

    public void handleImportHero(ActionEvent actionEvent) {

    }

    public void handleMenuCloseHero(ActionEvent actionEvent) {
        hero.setValue(new Hero.Builder().build());
    }

    public void handleMenuLogin(ActionEvent actionEvent) {
        startNewDialogForResult(R.FXML.LOGIN, ACTION_LOGIN);
    }

    public void handleMenuDice(ActionEvent actionEvent) {
        startNewDialog(R.FXML.DICE);
    }

    public void handleMenuBestiary(ActionEvent actionEvent) {

    }

    public void handleMenuShop(ActionEvent actionEvent) {
        startNewDialog(R.FXML.SHOP1);
    }

    public void handleMenuFight(ActionEvent actionEvent) {

    }

    public void handleMenuAbout(ActionEvent actionEvent) {

    }

    public void handleMenuChangelog(ActionEvent actionEvent) {

    }

    public void handleMenuHelp(ActionEvent actionEvent) {

    }

    // endregion
}
