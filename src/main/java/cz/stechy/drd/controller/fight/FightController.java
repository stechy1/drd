package cz.stechy.drd.controller.fight;

import cz.stechy.drd.Context;
import cz.stechy.drd.R;
import cz.stechy.drd.model.persistent.BestiaryService;
import cz.stechy.drd.model.user.User;
import cz.stechy.screens.BaseController;
import java.net.URL;
import java.util.ResourceBundle;
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

    private final User user;
    private final BestiaryService bestiary;

    private String title;

    // endregion

    // region Constructors

    public FightController(Context context) {
        this.user = context.getUserService().getUser().get();
        this.bestiary = context.getService(Context.SERVICE_BESTIARY);
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title = resources.getString(R.Translate.FIGHT_TITLE);
    }

    @Override
    protected void onResume() {
        setTitle(title);
        setScreenSize(600, 400);
    }
}
