package cz.stechy.drd.controller.fight;

import com.jfoenix.controls.JFXButton;
import cz.stechy.drd.Context;
import cz.stechy.drd.R;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.fight.Battlefield;
import cz.stechy.drd.model.inventory.Inventory;
import cz.stechy.drd.model.persistent.HeroService;
import cz.stechy.drd.model.persistent.InventoryContent;
import cz.stechy.drd.model.persistent.InventoryService;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * Kontroler pro souboj hrdiny a protivníka
 */
public class FightController extends BaseController implements Initializable {

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
    private IFightChild[] controllers;
    private String title;
    private Battlefield battlefield;

    // endregion

    // region Constructors

    public FightController(Context context) {
        this.heroService = context.getService(Context.SERVICE_HERO);
        this.hero = this.heroService.getHero();
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

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title = resources.getString(R.Translate.FIGHT_TITLE);

        controllers = new IFightChild[] {
            fightHeroController,
            fightOpponentController
        };

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
        setScreenSize(1050, 400);
    }

    @Override
    protected void onClose() {
        stopFight();
    }

    @FXML
    private void handleBeginFight(ActionEvent actionEvent) throws DatabaseException {
        stopFight();
        final Inventory inventory = heroService.getInventory()
            .select(InventoryService.EQUIP_INVENTORY_FILTER);
        final InventoryContent equipContent = heroService.getInventory()
            .getInventoryContent(inventory);
        this.battlefield = new Battlefield(new HeroAggresiveEntity(hero, equipContent), fightOpponentController.getMob());
        battlefield.setFightFinishListener(() -> isFighting.set(false));
        battlefield.setOnActionVisualizeListener(lblStatus::setText);
        battlefield.fight();
        isFighting.set(true);
    }

    @FXML
    private void handleStopFight(ActionEvent actionEvent) {
        stopFight();
    }
}
