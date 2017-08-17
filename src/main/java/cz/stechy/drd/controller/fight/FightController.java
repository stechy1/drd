package cz.stechy.drd.controller.fight;

import com.jfoenix.controls.JFXButton;
import cz.stechy.drd.Context;
import cz.stechy.drd.R;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.fight.Battlefield;
import cz.stechy.drd.model.fight.Battlefield.BattlefieldAction;
import cz.stechy.drd.model.inventory.Inventory;
import cz.stechy.drd.model.persistent.HeroService;
import cz.stechy.drd.model.persistent.InventoryContent;
import cz.stechy.drd.model.persistent.InventoryService;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import cz.stechy.screens.Notification;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * Kontroler pro souboj hrdiny a protivníka
 */
public class FightController extends BaseController implements Initializable {

    // region Constants

    // Konstanty komentářů
    private static final String[] COMMENTS = {
        R.Translate.FIGHT_COMMENT_ACTION_ATTACK,
        R.Translate.FIGHT_COMMENT_ACTION_DEFENCE,
        R.Translate.FIGHT_COMMENT_ACTION_HEALTH,
        R.Translate.FIGHT_COMMENT_ACTION_BLOCK,
        R.Translate.FIGHT_COMMENT_ACTION_DEATH,
        R.Translate.FIGHT_COMMENT_ACTION_TURN,
        R.Translate.FIGHT_COMMENT_ACTION_SEPARATOR,
        R.Translate.FIGHT_COMMENT_ACTION_ATTACK_INFO
    };

    // endregion

    // region Variables

    // region FXML

    @FXML
    private FightHeroController fightHeroController;
    @FXML
    private FightOpponentController fightOpponentController;

    @FXML
    private AnchorPane fightHero;
    @FXML
    private AnchorPane fightOpponent;

    @FXML
    private Label lblStatus;

    @FXML
    private JFXButton btnStartFight;
    @FXML
    private JFXButton btnStopFight;

    // endregion

    private final HeroService heroService;
    private final Hero hero;

    private final BooleanProperty isFighting = new SimpleBooleanProperty();
    private final StringProperty comment = new SimpleStringProperty();
    private IFightChild[] controllers;
    private String title;
    private ResourceBundle resources;
    private Battlefield battlefield;

    // endregion

    // region Constructors

    public FightController(Context context) {
        this.heroService = context.getService(Context.SERVICE_HERO);
        this.hero = this.heroService.getHero().duplicate();
    }

    // endregion

    // region Private methods

    /**
     * Ukončí souboj, pokud nějaký je
     */
    private void stopFight() {
        if (this.battlefield != null) {
            battlefield.stopFight();
        }
        isFighting.set(false);
    }

    private String processComment(BattlefieldAction action, Object... params) {
        String formater = resources.getString(COMMENTS[action.ordinal()]);
        return String.format(formater, params);
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title = resources.getString(R.Translate.FIGHT_TITLE);
        this.resources = resources;

        controllers = new IFightChild[] {
            fightHeroController,
            fightOpponentController
        };

        fightOpponentController.injectParent(this);

        btnStartFight.disableProperty().bind(Bindings.createBooleanBinding(() ->
            isFighting.get() || fightOpponentController.selectedMobProperty().get() == null,
            isFighting, fightOpponentController.selectedMobProperty()));
        btnStopFight.disableProperty().bind(isFighting.not());
    }

    @Override
    protected void onCreate(Bundle bundle) {
        Arrays.stream(controllers).forEach(controller -> {
            controller.setHero(hero);
        });
    }

    @Override
    protected void onResume() {
        setTitle(title);
        setScreenSize(800, 580);
    }

    @Override
    protected void onScreenResult(int statusCode, int actionId, Bundle bundle) {
        fightOpponentController.onScreenResult(statusCode, actionId, bundle);
    }

    @Override
    protected void onClose() {
        stopFight();
    }

    @FXML
    private void handleBeginFight(ActionEvent actionEvent) throws DatabaseException {
        stopFight();

        Bundle bundle = new Bundle();
        bundle.put(FightCommentController.COMMENT, comment);
        startNewDialog(R.FXML.FIGHT_COMMENT, bundle);

        final Inventory inventory = heroService.getInventory()
            .select(InventoryService.EQUIP_INVENTORY_FILTER);
        final InventoryContent equipContent = heroService.getInventory()
            .getInventoryContent(inventory);
        this.battlefield = new Battlefield(new HeroAggresiveEntity(hero, equipContent), fightOpponentController.getMob());
        battlefield.setFightFinishListener(() -> {
            isFighting.set(false);
            try {
                heroService.update(hero);
                showNotification(new Notification(resources.getString(R.Translate.NOTIFY_FIGHT_LIVE_UPDATE)));
            } catch (DatabaseException e) {}
        });
        battlefield.setOnActionVisualizeListener((action, params) ->
            comment.setValue(processComment(action, params)));
        battlefield.fight();
        isFighting.set(true);
    }

    @FXML
    private void handleStopFight(ActionEvent actionEvent) {
        stopFight();
        closeChildScreens();
    }
}
