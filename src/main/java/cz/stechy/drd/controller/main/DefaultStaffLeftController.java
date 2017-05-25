package cz.stechy.drd.controller.main;

import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.widget.LabeledHeroProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DefaultStaffLeftController {

    // region Variables

    // region FXML

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
    private Label lblHeight;

//    @FXML
//    private LabeledText lblStrengthRepair;
//    @FXML
//    private Label lblDexterityRepair;
//    @FXML
//    private Label lblImmunityRepair;
//    @FXML
//    private Label lblIntelligenceRepair;
//    @FXML
//    private Label lblCharismaRepair;

    // endregion

    // endregion

    void bindWithHero(Hero hero) {
        lblStrength.setHeroProperty(hero.getStrength());
        lblDexterity.setHeroProperty(hero.getDexterity());
        lblImmunity.setHeroProperty(hero.getImmunity());
        lblIntelligence.setHeroProperty(hero.getIntelligence());
        lblCharisma.setHeroProperty(hero.getCharisma());
        lblHeight.textProperty().bind(hero.heightProperty().asString());
    }


}
