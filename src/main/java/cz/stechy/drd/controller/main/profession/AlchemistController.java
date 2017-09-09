package cz.stechy.drd.controller.main.profession;

import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.entity.hero.profession.Alchemist;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class AlchemistController implements IProfessionController, Initializable {

    // region Variables

    // region FXML

    @FXML
    private Label lblProbabilityOfSuccess;

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

        lblProbabilityOfSuccess.textProperty().bind(alchemist.probabilityOfSuccessProperty().asString().concat(" %"));
    }

    // region Button handlers



    // endregion
}
