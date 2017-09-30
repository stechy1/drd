package cz.stechy.drd.controller.hero.creator;

import cz.stechy.drd.R;
import cz.stechy.drd.controller.hero.HeroHelper;
import cz.stechy.drd.model.entity.EntityProperty;
import cz.stechy.drd.model.entity.SimpleEntityProperty;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.entity.hero.HeroGenerator;
import cz.stechy.drd.util.BitUtils;
import cz.stechy.drd.widget.LabeledHeroProperty;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Druhý kontroler z průvodce vytvořením postavy
 * Nastavení základních vlastností postavy
 */
public class HeroCreatorController2 extends BaseController implements Initializable {

    // region Variables

    // region FXML

    @FXML
    private Label lblLive;
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
    private Button btnNext;
    // endregion

    private final NewHeroModel2 model = new NewHeroModel2();

    private String title;
    private Bundle bundle;
    private Hero.Race race;
    private Hero.Profession profession;
    private HeroGenerator generator;
    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = resources.getString(R.Translate.GUIDE_NEW_HERO_2_TITLE);

        // TODO zjistit, jak změnit traversal policy pro tlačítka
        lblLive.textProperty().bind(model.live.asString());
        lblStrength.bind(model.strength);
        lblDexterity.bind(model.dexterity);
        lblImmunity.bind(model.immunity);
        lblIntelligence.bind(model.intelligence);
        lblCharisma.bind(model.charisma);

        btnNext.disableProperty().bind(model.valid.not());
    }

    @Override
    protected void onCreate(Bundle bundle) {
        this.bundle = bundle;

        race = Hero.Race.valueOf(bundle.getInt(HeroHelper.RACE));
        profession = Hero.Profession.valueOf(bundle.getInt(HeroHelper.PROFESSION));
        generator = new HeroGenerator(race, profession);

        bundle.putInt(HeroHelper.HEIGHT, generator.height().ordinal());
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
        bundle.remove(HeroHelper.LIVE);
        bundle.remove(HeroHelper.STRENGTH);
        bundle.remove(HeroHelper.DEXTERITY);
        bundle.remove(HeroHelper.IMMUNITY);
        bundle.remove(HeroHelper.INTELLIGENCE);
        bundle.remove(HeroHelper.CHARISMA);

        model.live.setValue(generator.baseLive(model.immunity));
        model.strength.setValue(generator.strength());
        model.dexterity.setValue(generator.dexterity());
        model.immunity.setValue(generator.immunity());
        model.intelligence.setValue(generator.intelligence());
        model.charisma.setValue(generator.charisma());
    }

    @FXML
    public void handleResetLive(ActionEvent actionEvent) {
        bundle.remove(HeroHelper.LIVE);
        model.live.setValue(generator.baseLive(model.immunity));
    }

    @FXML
    public void handleResetStrenght(ActionEvent actionEvent) {
        bundle.remove(HeroHelper.STRENGTH);
        model.strength.setValue(generator.strength());
    }

    @FXML
    public void handleResetDexterity(ActionEvent actionEvent) {
        bundle.remove(HeroHelper.DEXTERITY);
        model.dexterity.setValue(generator.dexterity());
    }

    @FXML
    public void handleResetImmunity(ActionEvent actionEvent) {
        bundle.remove(HeroHelper.IMMUNITY);
        model.immunity.setValue(generator.immunity());
    }

    @FXML
    public void handleResetIntelligence(ActionEvent actionEvent) {
        bundle.remove(HeroHelper.INTELLIGENCE);
        model.intelligence.setValue(generator.intelligence());
    }

    @FXML
    public void handleResetCharisma(ActionEvent actionEvent) {
        bundle.remove(HeroHelper.CHARISMA);
        model.charisma.setValue(generator.charisma());
    }

    @FXML
    private void handleNext(ActionEvent actionEvent) {
        bundle.putInt(HeroHelper.LIVE, model.live.getValue());
        bundle.putInt(HeroHelper.STRENGTH, model.strength.getValue());
        bundle.putInt(HeroHelper.DEXTERITY, model.dexterity.getValue());
        bundle.putInt(HeroHelper.IMMUNITY, model.immunity.getValue());
        bundle.putInt(HeroHelper.INTELLIGENCE, model.intelligence.getValue());
        bundle.putInt(HeroHelper.CHARISMA, model.charisma.getValue());
        startScreen(R.FXML.HERO_CREATOR_3, bundle);
    }

    // endregion

    private static class NewHeroModel2 {

        private static final int FLAG_LIVE = 1 << 0;
        private static final int FLAG_STRENGTH = 1 << 1;
        private static final int FLAG_DEXTERITY = 1 << 2;
        private static final int FLAG_IMMUNITY = 1 << 3;
        private static final int FLAG_INTELLIGENCE = 1 << 4;
        private static final int FLAG_CHARISMA = 1 << 5;

        private final IntegerProperty live = new SimpleIntegerProperty();
        private final EntityProperty strength = new SimpleEntityProperty();
        private final EntityProperty dexterity = new SimpleEntityProperty();
        private final EntityProperty immunity = new SimpleEntityProperty();
        private final EntityProperty intelligence = new SimpleEntityProperty();
        private final EntityProperty charisma = new SimpleEntityProperty();
        private final IntegerProperty flags = new SimpleIntegerProperty(
            FLAG_LIVE + FLAG_STRENGTH + FLAG_DEXTERITY + FLAG_IMMUNITY
                + FLAG_INTELLIGENCE + FLAG_CHARISMA
        );
        private final BooleanProperty valid = new SimpleBooleanProperty();

        NewHeroModel2() {
            live.addListener(liveChangeListener);
            strength.valueProperty().addListener(strengthChangeListener);
            dexterity.valueProperty().addListener(dexterityChangeListener);
            immunity.valueProperty().addListener(immunityChangeListener);
            intelligence.valueProperty().addListener(intelligenceChangeListener);
            charisma.valueProperty().addListener(charismaChangeListener);
            flags.addListener(
                (observable, oldValue, newValue) -> valid.setValue(flags.intValue() == 0));
        }

        private final ChangeListener<Number> liveChangeListener = (observable, oldValue, newValue) -> {
            int oldFlags = flags.get();
            boolean isLiveValid = newValue != null;
            int result = BitUtils.setBit(oldFlags, FLAG_LIVE, !isLiveValid);

            flags.setValue(result);
        };

        private final ChangeListener<Number> strengthChangeListener = (observable, oldValue, newValue) -> {
            int oldFlags = flags.get();
            boolean isStrengthValid = newValue != null;
            int result = BitUtils.setBit(oldFlags, FLAG_STRENGTH, !isStrengthValid);

            flags.setValue(result);
        };

        private final ChangeListener<Number> dexterityChangeListener = (observable, oldValue, newValue) -> {
            int oldFlags = flags.get();
            boolean isDexterityValid = newValue != null;
            int result = BitUtils.setBit(oldFlags, FLAG_DEXTERITY, !isDexterityValid);

            flags.setValue(result);
        };

        private final ChangeListener<Number> immunityChangeListener = (observable, oldValue, newValue) -> {
            int oldFlags = flags.get();
            boolean isImmunityValid = newValue != null;
            int result = BitUtils.setBit(oldFlags, FLAG_IMMUNITY, !isImmunityValid);

            flags.setValue(result);
        };

        private final ChangeListener<Number> intelligenceChangeListener = (observable, oldValue, newValue) -> {
            int oldFlags = flags.get();
            boolean isIntelligenceValid = newValue != null;
            int result = BitUtils.setBit(oldFlags, FLAG_INTELLIGENCE, !isIntelligenceValid);

            flags.setValue(result);
        };

        private final ChangeListener<Number> charismaChangeListener = (observable, oldValue, newValue) -> {
            int oldFlags = flags.get();
            boolean isCharismaValid = newValue != null;
            int result = BitUtils.setBit(oldFlags, FLAG_CHARISMA, !isCharismaValid);

            flags.setValue(result);
        };

    }

}
