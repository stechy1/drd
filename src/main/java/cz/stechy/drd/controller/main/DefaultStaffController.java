package cz.stechy.drd.controller.main;

import cz.stechy.drd.model.Context;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.util.StringConvertors;
import cz.stechy.drd.util.Translator;
import cz.stechy.drd.widget.LabeledProgressBar;
import cz.stechy.drd.widget.LabeledText;
import cz.stechy.drd.widget.MoneyLabel;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

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
    private LabeledText lblHeroName;
    @FXML
    private LabeledText lblHeroRaceAndProfession;
    @FXML
    private LabeledProgressBar lblLive;
    @FXML
    private LabeledProgressBar lblMag;
    @FXML
    private LabeledProgressBar lblExperience;
    @FXML
    private MoneyLabel lblMoney;
    @FXML
    private GridPane defaultStaffLeft;
    @FXML
    private GridPane defaultStaffRight;

    // endregion

    private final Translator translator;

    // endregion

    // region Constructors

    public DefaultStaffController(Context context) {
        translator = context.getTranslator();
    }

    // endregion

    @Override
    public void setHero(ObjectProperty<Hero> hero) {
        hero.addListener((observable, oldValue, newValue) -> {
            lblHeroName.textProperty().bind(newValue.nameProperty());
            lblHeroRaceAndProfession.textProperty()
                .bind(StringConvertors.forRaceAndProfessionConverter(translator, newValue));
            lblLive.setMaxActValue(newValue.getLive());
            lblMag.setMaxActValue(newValue.getMag());
            lblExperience.setMaxActValue(newValue.getExperiences());
            defaultStaffLeftController.bindWithHero(newValue);
            defaultStaffRightController.bindWithHero(newValue);
            lblMoney.forMoney(newValue.getMoney());
        });
    }
}
