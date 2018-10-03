package cz.stechy.drd.app.main.defaultstaff;

import com.google.inject.Inject;
import cz.stechy.drd.app.main.MainScreen;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.service.translator.ITranslatorService;
import cz.stechy.drd.service.translator.TranslatorService.Key;
import cz.stechy.drd.widget.Card;
import cz.stechy.drd.widget.LabeledMaxActValue;
import cz.stechy.drd.widget.MoneyWidget;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * Kontroler pro základní parametry postavy
 */
public class DefaultStaffController implements MainScreen, Initializable {

    // region Variables

    // region FXML

    @FXML
    private DefaultStaffLeftController defaultStaffLeftController;
    @FXML
    private DefaultStaffRightController defaultStaffRightController;

    @FXML
    private Label lblName;
    @FXML
    private Label lblRace;
    @FXML
    private Label lblProfession;
    @FXML
    private Label lblSpecialization;
    @FXML
    private LabeledMaxActValue lblLive;
    @FXML
    private LabeledMaxActValue lblMag;
    @FXML
    private LabeledMaxActValue lblExperience;
    @FXML
    private MoneyWidget lblMoney;
    @FXML
    private Card defaultStaffLeft;
    @FXML
    private Card defaultStaffRight;

    // endregion

    private final ITranslatorService translator;

    // endregion

    // region Constructors

    @Inject
    public DefaultStaffController(ITranslatorService translatorService) {
        translator = translatorService;
    }

    // endregion

    // region Private methods

    private void reset() {
        if (!lblName.textProperty().isBound()) {
            lblName.setText("-");
        }
        if (!lblRace.textProperty().isBound()) {
            lblRace.setText("-");
        }
        if (!lblProfession.textProperty().isBound()) {
            lblProfession.setText("-");
        }
        if (!lblSpecialization.textProperty().isBound()) {
            lblSpecialization.setText("-");
        }

    }

    private void unbindHero(Hero oldValue) {
        lblName.textProperty().unbind();
        lblRace.textProperty().unbind();
        lblProfession.textProperty().unbind();
        lblMoney.unbind();
        if (oldValue != null) {
            lblLive.unbind(oldValue.getLive());
            lblMag.unbind(oldValue.getMag());
            lblExperience.unbind(oldValue.getExperiences());
        }

        reset();
    }

    private void bindHero(final Hero hero) {
        lblName.textProperty().bind(hero.nameProperty());
        lblRace.textProperty().bind(Bindings.createStringBinding(() ->
                translator.getSingleTranslationFor(Key.RACES, hero.getRace()),
            hero.raceProperty()));
        lblProfession.textProperty().bind(Bindings.createStringBinding(() ->
                translator.getSingleTranslationFor(Key.PROFESSIONS, hero.getProfession()),
            hero.professionProperty()));
        lblLive.bind(hero.getLive());
        lblMag.bind(hero.getMag());
        lblExperience.bind(hero.getExperiences());
        lblMoney.bind(hero.getMoney());
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reset();
    }

    @Override
    public void setHero(ReadOnlyObjectProperty<Hero> hero) {
        hero.addListener((observable, oldValue, newValue) -> {
            unbindHero(oldValue);
            if (newValue != null) {
                bindHero(newValue);
            }

            defaultStaffLeftController.bindWithHero(newValue);
            defaultStaffRightController.bindWithHero(newValue);
        });
    }

}
