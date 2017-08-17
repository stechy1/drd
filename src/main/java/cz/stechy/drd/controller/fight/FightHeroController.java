package cz.stechy.drd.controller.fight;

import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.widget.LabeledHeroProperty;
import cz.stechy.drd.widget.LabeledMaxActValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Kontroler pro ovládání levé části reprezentující hrdinu
 */
public class FightHeroController implements IFightChild {

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

    @Override
    public void setHero(Hero hero) {
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
