package cz.stechy.drd.controller.main.profession;

import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.entity.hero.profession.Ranger;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class RangerController implements IProfessionController, Initializable {

    // region Variables

    // region FXML

    // endregion

    private Hero hero;
    private Ranger ranger;

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void setHero(Hero hero) {
        this.hero = hero;
    }
}
