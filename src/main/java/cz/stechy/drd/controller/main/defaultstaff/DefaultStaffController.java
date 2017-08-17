package cz.stechy.drd.controller.main.defaultstaff;

import cz.stechy.drd.Context;
import cz.stechy.drd.controller.main.MainScreen;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.util.StringConvertors;
import cz.stechy.drd.util.Translator;
import cz.stechy.drd.widget.Card;
import cz.stechy.drd.widget.LabeledMaxActValue;
import cz.stechy.drd.widget.MoneyWidget;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Kontroler pro základní parametry postavy
 */
public class DefaultStaffController implements MainScreen {

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

    private final Translator translator;

    // endregion

    // region Constructors

    public DefaultStaffController(Context context) {
        translator = context.getTranslator();
    }

    // endregion

    @Override
    public void setHero(ReadOnlyObjectProperty<Hero> hero) {
        hero.addListener((observable, oldValue, newValue) -> {
            lblName.textProperty().bind(newValue.nameProperty());
            lblRace.textProperty()
                .bind(StringConvertors.forRaceConverter(translator, newValue.getRace()));
            lblProfession.textProperty()
                .bind(StringConvertors.forProfessionConverter(translator, newValue.getProfession()));
            lblLive.setMaxActValue(newValue.getLive());
            lblMag.setMaxActValue(newValue.getMag());
            lblExperience.setMaxActValue(newValue.getExperiences());
            defaultStaffLeftController.bindWithHero(newValue);
            defaultStaffRightController.bindWithHero(newValue);
            lblMoney.bind(newValue.getMoney());
        });
    }

}
