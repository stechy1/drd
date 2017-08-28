package cz.stechy.drd.controller.main.defaultstaff;

import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.widget.LabeledHeroProperty;
import javafx.fxml.FXML;

public class DefaultStaffRightController {

    // region Variables

    // region FXML

    @FXML
    private LabeledHeroProperty lblAgility;
    @FXML
    private LabeledHeroProperty lblLoadLow;
    @FXML
    private LabeledHeroProperty lblLoadMedium;
    @FXML
    private LabeledHeroProperty lblLoadHigh;
    @FXML
    private LabeledHeroProperty lblObservationObjects;
    @FXML
    private LabeledHeroProperty lblObservationMechanics;

    // endregion

    // endregion

    // region Public methods

    public void bindWithHero(Hero hero) {
        lblAgility.setHeroProperty(hero.getAgility());
        lblLoadLow.setHeroProperty(hero.getLowLoad());
        lblLoadMedium.setHeroProperty(hero.getMediumLoad());
        lblLoadHigh.setHeroProperty(hero.getHighLoad());
        lblObservationObjects.setHeroProperty(hero.getObservationObjects());
        lblObservationMechanics.setHeroProperty(hero.getObservationMechanics());
    }

    // endregion
}
