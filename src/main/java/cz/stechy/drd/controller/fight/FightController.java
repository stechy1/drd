package cz.stechy.drd.controller.fight;

import cz.stechy.drd.Context;
import cz.stechy.drd.R;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.fight.Battlefield;
import cz.stechy.drd.model.persistent.HeroService;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

    // endregion

    private final HeroService heroService;
    private final Hero hero;

    private IFightChild[] controllers;
    private String title;

    // endregion

    // region Constructors

    public FightController(Context context) {
        this.heroService = context.getService(Context.SERVICE_HERO);
        this.hero = this.heroService.getHero();
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title = resources.getString(R.Translate.FIGHT_TITLE);

        controllers = new IFightChild[] {
            fightHeroController,
            fightOpponentController
        };
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

    public void handleBeginFight(ActionEvent actionEvent) {
        Battlefield battlefield = new Battlefield(hero, fightOpponentController.getMob());
        battlefield.fight();
    }
}
