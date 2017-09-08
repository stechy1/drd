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
        if (hero == null) {
            lblName.setText("-");
            lblStrength.unbind();
            lblDexterity.unbind();
            lblImmunity.unbind();
            lblIntelligence.unbind();
            lblCharisma.unbind();
            lblAttackNumber.setText("0");
            lblDefenceNumber.setText("0");
            return;
        }

        lblName.textProperty().bind(hero.nameProperty());
        lblStrength.bind(hero.getStrength());
        lblDexterity.bind(hero.getDexterity());
        lblImmunity.bind(hero.getImmunity());
        lblIntelligence.bind(hero.getIntelligence());
        lblCharisma.bind(hero.getCharisma());
        lblLive.bind(hero.getLive());
        lblAttackNumber.setText(String.valueOf(hero.getAttackNumber()));
        lblDefenceNumber.setText(String.valueOf(hero.getDefenceNumber()));
    }
}
