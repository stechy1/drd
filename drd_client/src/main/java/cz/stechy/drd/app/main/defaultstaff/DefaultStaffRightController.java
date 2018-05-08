package cz.stechy.drd.app.main.defaultstaff;

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

    // region Private methods

    private void unbind() {
        lblAgility.unbind();
        lblLoadLow.unbind();
        lblLoadMedium.unbind();
        lblLoadHigh.unbind();
        lblObservationObjects.unbind();
        lblObservationMechanics.unbind();
    }

    private void bind(Hero hero) {
        lblAgility.bind(hero.getAgility());
        lblLoadLow.bind(hero.getLowLoad());
        lblLoadMedium.bind(hero.getMediumLoad());
        lblLoadHigh.bind(hero.getHighLoad());
        lblObservationObjects.bind(hero.getObservationObjects());
        lblObservationMechanics.bind(hero.getObservationMechanics());
    }

    // endregion

    // region Public methods

    void bindWithHero(Hero hero) {
        if (hero == null) {
            unbind();
        } else {
            bind(hero);
        }
    }

    // endregion
}
