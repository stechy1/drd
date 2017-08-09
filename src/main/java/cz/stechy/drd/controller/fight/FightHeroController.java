package cz.stechy.drd.controller.fight;

import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.widget.LabeledHeroProperty;
import cz.stechy.drd.widget.LabeledMaxActValue;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * Kontroler pro ovládání levé části reprezentující hrdinu
 */
public class FightHeroController implements Initializable, IFightChild {

    // region Variables

    // region FXML

    @FXML
    private Label lblName;
    @FXML
    private LabeledHeroProperty lblStrength;
    @FXML
    private LabeledHeroProperty lblDexterity;
    @FXML
    private LabeledHeroProperty lblImmunity;
    @FXML
    private LabeledHeroProperty lblIntelligence;
    @FXML
    private LabeledHeroProperty lblCharisma;
    @FXML
    private LabeledMaxActValue lblLive;
    @FXML
    private Label lblAttackNumber;
    @FXML
    private Label lblDefenceNumber;

    // endregion

    private Hero hero;

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void setHero(Hero hero) {
        this.hero = hero;
        lblName.textProperty().bind(hero.nameProperty());
        lblStrength.setHeroProperty(hero.getStrength());
        lblDexterity.setHeroProperty(hero.getDexterity());
        lblImmunity.setHeroProperty(hero.getImmunity());
        lblIntelligence.setHeroProperty(hero.getIntelligence());
        lblCharisma.setHeroProperty(hero.getCharisma());
        lblLive.setMaxActValue(hero.getLive());
        lblAttackNumber.setText(String.valueOf(hero.getAttackNumber()));
        lblDefenceNumber.setText(String.valueOf(hero.getDefenceNumber()));
    }
}
