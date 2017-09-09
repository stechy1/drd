package cz.stechy.drd.controller.main.profession;

import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.entity.hero.profession.Wizard;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class WizardController implements IProfessionController, Initializable {



    // region Variables

    // region FXML

    @FXML
    private Label lblProbabilityOfSuccess;

    // endregion

    private Hero hero;
    private Wizard wizard;

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void setHero(Hero hero) {
        this.hero = hero;
        this.wizard = new Wizard(hero);

        lblProbabilityOfSuccess.textProperty().bind(wizard.probabilityOfSuccessProperty().asString().concat(" %"));

    }
}
