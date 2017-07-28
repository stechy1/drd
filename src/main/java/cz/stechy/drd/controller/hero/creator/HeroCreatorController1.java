package cz.stechy.drd.controller.hero.creator;

import cz.stechy.drd.R;
import cz.stechy.drd.Context;
import cz.stechy.drd.model.entity.Conviction;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.entity.hero.Hero.Profession;
import cz.stechy.drd.model.entity.hero.Hero.Race;
import cz.stechy.drd.util.BitUtils;
import cz.stechy.drd.util.StringConvertors;
import cz.stechy.drd.util.Translator;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * První kontroler z průvodce vytvořením postavy
 * Vyplnění základních údajů
 */
public class HeroCreatorController1 extends BaseController implements Initializable {

    // region Variables

    // region FXML

    @FXML
    private Button btnNext;

    @FXML
    private TextField txtName;
    @FXML
    private ComboBox<Conviction> cmbConviction;
    @FXML
    private ComboBox<Race> cmbRace;
    @FXML
    private ComboBox<Profession> cmbProfession;
    @FXML
    private TextArea txtDescription;

    // endregion

    private final NewHeroModel1 model = new NewHeroModel1();
    private final Translator translator;

    private String title;
    private Bundle bundle;

    // endregion

    public HeroCreatorController1(Context context) {
        translator = context.getTranslator();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = resources.getString(R.Translate.GUIDE_NEW_HERO_1_TITLE);

        cmbConviction.converterProperty().setValue(
            StringConvertors.forConvictionConverter(translator));
        cmbRace.converterProperty().setValue(
            StringConvertors.forRaceConverter(translator));
        cmbProfession.converterProperty().setValue(
            StringConvertors.forProfessionConverter(translator));

        txtName.textProperty().bindBidirectional(model.name);
        txtDescription.textProperty().bindBidirectional(model.description);
        cmbConviction.valueProperty().bindBidirectional(model.conviction);
        cmbRace.valueProperty().bindBidirectional(model.race);
        cmbProfession.valueProperty().bindBidirectional(model.profession);
        btnNext.disableProperty().bind(model.valid.not());
    }

    @Override
    protected void onCreate(Bundle bundle) {
        this.bundle = bundle;

        model.name.setValue(bundle.getString(HeroCreatorHelper.NAME));
        model.conviction.setValue(
            Conviction.valueOf(bundle.getInt(HeroCreatorHelper.CONVICTION, -1)));
        model.race.setValue(Hero.Race.valueOf(bundle.getInt(HeroCreatorHelper.RACE, -1)));
        model.profession.setValue(
            Hero.Profession.valueOf(bundle.getInt(HeroCreatorHelper.PROFESSION, -1)));
        model.description.setValue(bundle.getString(HeroCreatorHelper.DESCRIPTION));
    }

    @Override
    protected void onResume() {
        setScreenSize(600, 400);
        setTitle(title);
    }

    // region Button handles

    @FXML
    private void handleBack(ActionEvent actionEvent) {
        back();
    }

    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        finish();
    }

    @FXML
    private void handleReset(ActionEvent actionEvent) {
        model.resetValues();
        bundle.remove(HeroCreatorHelper.NAME);
        bundle.remove(HeroCreatorHelper.CONVICTION);
        bundle.remove(HeroCreatorHelper.RACE);
        bundle.remove(HeroCreatorHelper.PROFESSION);
        bundle.remove(HeroCreatorHelper.DESCRIPTION);
    }

    @FXML
    private void handleNext(ActionEvent actionEvent) {
        bundle.putString(HeroCreatorHelper.NAME, model.name.getValue());
        bundle.putInt(HeroCreatorHelper.CONVICTION, model.conviction.getValue().ordinal());
        bundle.putInt(HeroCreatorHelper.RACE, model.race.getValue().ordinal());
        bundle.putInt(HeroCreatorHelper.PROFESSION, model.profession.getValue().ordinal());
        bundle.putString(HeroCreatorHelper.DESCRIPTION, model.description.getValue());
        startScreen(R.FXML.HERO_CREATOR_2, bundle);
    }

    // endregion

    private static class NewHeroModel1 {

        private static final int FLAG_NAME = 1 << 0;
        private static final int FLAG_CONVICTION = 1 << 1;
        private static final int FLAG_RACE = 1 << 2;
        private static final int FLAG_PROFESSION = 1 << 3;

        private final StringProperty name = new SimpleStringProperty();
        private final ObjectProperty<Conviction> conviction = new SimpleObjectProperty<>();
        private final ObjectProperty<Hero.Race> race = new SimpleObjectProperty<>();
        private final ObjectProperty<Hero.Profession> profession = new SimpleObjectProperty<>();
        private final IntegerProperty flags = new SimpleIntegerProperty(
            FLAG_NAME + FLAG_CONVICTION + FLAG_RACE + FLAG_PROFESSION);
        private final BooleanProperty valid = new SimpleBooleanProperty();
        private final StringProperty description = new SimpleStringProperty();

        NewHeroModel1() {
            name.addListener(nameChangeListener);
            conviction.addListener(convictionChangeListener);
            race.addListener(raceChangeListener);
            profession.addListener(professionChangeListener);
            flags.addListener(
                (observable, oldValue, newValue) -> valid.setValue(newValue.intValue() == 0));
        }

        void resetValues() {
            name.setValue("");
            conviction.setValue(null);
            race.setValue(null);
            profession.setValue(null);
            description.setValue(null);
        }

        private final ChangeListener<String> nameChangeListener = (observable, oldValue, newValue) -> {
            int oldFlags = flags.get();
            boolean isNameValid = Hero.NAME_PATTERN.matcher(newValue).matches();
            int result = BitUtils.setBit(oldFlags, FLAG_NAME, !isNameValid);

            flags.setValue(result);
        };

        private final ChangeListener<Conviction> convictionChangeListener = (observable, oldValue, newValue) -> {
            int oldFlags = flags.get();
            boolean isConvictionValid = newValue != null;
            int result = BitUtils.setBit(oldFlags, FLAG_CONVICTION, !isConvictionValid);

            flags.setValue(result);
        };

        private final ChangeListener<Hero.Race> raceChangeListener = (observable, oldValue, newValue) -> {
            int oldFlags = flags.get();
            boolean isRaceValid = newValue != null;
            int result = BitUtils.setBit(oldFlags, FLAG_RACE, !isRaceValid);

            flags.setValue(result);
        };

        private final ChangeListener<Hero.Profession> professionChangeListener = (observable, oldValue, newValue) -> {
            int oldFlags = flags.get();
            boolean isProfessionValid = newValue != null;
            int result = BitUtils.setBit(oldFlags, FLAG_PROFESSION, !isProfessionValid);

            flags.setValue(result);
        };

    }


}
