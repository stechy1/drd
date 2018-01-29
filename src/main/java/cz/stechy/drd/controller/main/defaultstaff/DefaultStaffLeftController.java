package cz.stechy.drd.controller.main.defaultstaff;

import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.widget.LabeledHeroProperty;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class DefaultStaffLeftController implements Initializable {

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

    // endregion

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reset();
    }

    // region Private methods

    private void reset() {
        if (!lblHeight.textProperty().isBound()) {
            lblHeight.setText("-");
        }
    }

    private void unbind() {
        lblStrength.unbind();
        lblDexterity.unbind();
        lblImmunity.unbind();
        lblIntelligence.unbind();
        lblCharisma.unbind();
        lblHeight.textProperty().unbind();

        reset();
    }

    private void bind(Hero hero) {
        lblStrength.bind(hero.getStrength());
        lblDexterity.bind(hero.getDexterity());
        lblImmunity.bind(hero.getImmunity());
        lblIntelligence.bind(hero.getIntelligence());
        lblCharisma.bind(hero.getCharisma());
        lblHeight.textProperty().bind(hero.heightProperty().asString());
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
