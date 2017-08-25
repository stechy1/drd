package cz.stechy.drd.controller.main.profession;

import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.entity.hero.profession.Alchemist;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class AlchemistController implements IProfessionController, Initializable {

    // region Variables

    // region FXML



    // endregion

    private Hero hero;
    private Alchemist alchemist;

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void setHero(Hero hero) {
        this.hero = hero;
        this.alchemist = new Alchemist(hero);
    }

    // region Button handlers



    // endregion
}
