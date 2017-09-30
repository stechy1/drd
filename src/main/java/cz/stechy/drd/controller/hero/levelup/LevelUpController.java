package cz.stechy.drd.controller.hero.levelup;

import cz.stechy.drd.AppSettings;
import cz.stechy.drd.R;
import cz.stechy.drd.controller.hero.HeroHelper;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.entity.EntityProperty;
import cz.stechy.drd.model.entity.SimpleEntityProperty;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.entity.hero.HeroGenerator;
import cz.stechy.drd.widget.LabeledHeroProperty;
import cz.stechy.drd.widget.MoneyWidget;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Průvodce zvýšením úrovně postavy
 */
public class LevelUpController extends BaseController implements Initializable {

    // region Constants

    private static final String DEFAULT_VALUE_POINTS_PER_LEVEL = "1";

    public static final String HERO = "hero";

    // endregion

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
    private Button btnLive;
    @FXML
    private Button btnStrength;
    @FXML
    private Button btnDexterity;
    @FXML
    private Button btnImmunity;
    @FXML
    private Button btnIntelligence;
    @FXML
    private Button btnCharisma;
    @FXML
    private MoneyWidget widgetMoney;
    @FXML
    private Button btnFinish;

    // endregion

    private final IntegerProperty remaingPoints = new SimpleIntegerProperty(this, "remaingPoints");
    private final BooleanProperty rollFinish = new SimpleBooleanProperty(this, "rollFinish", true);
    private final BooleanProperty finish = new SimpleBooleanProperty(this, "finish", false);
    private final Money money = new Money();
    private final Model model = new Model();

    private Hero hero;
    private HeroGenerator heroGenerator;
    private String title;

    // endregion

    // region Constructors

    public LevelUpController(AppSettings settings) {
        remaingPoints.setValue(Integer.parseInt(settings
            .getProperty(R.Config.HERO_LEVELUP_POINTS_PER_LEVEL, DEFAULT_VALUE_POINTS_PER_LEVEL)));

        rollFinish.bind(remaingPoints.greaterThan(0).not());
    }

    // endregion

    // region Private methods

    private void decreasePoints() {
        final int points = remaingPoints.get();
        remaingPoints.setValue(points - 1);
    }

    /**
     * Zvýší hodnotu vlastnosti
     *
     * @param property {@link EntityProperty}
     */
    private void increaseProperty(EntityProperty property) {
        final int value = property.getValue();
        property.setValue(value + 1);
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title = resources.getString(R.Translate.LEVEL_UP_TITLE);

        lblLive.textProperty().bind(model.live.asString());
        lblStrength.bind(model.strength);
        lblDexterity.bind(model.dexterity);
        lblImmunity.bind(model.immunity);
        lblIntelligence.bind(model.intelligence);
        lblCharisma.bind(model.charisma);
        btnStrength.disableProperty().bind(rollFinish);
        btnDexterity.disableProperty().bind(rollFinish);
        btnImmunity.disableProperty().bind(rollFinish);
        btnIntelligence.disableProperty().bind(rollFinish);
        btnCharisma.disableProperty().bind(rollFinish);

        // Až když vylepším základní vlastnosti tak povolím hod kostkou
        rollFinish.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                btnLive.setDisable(false);
            }
        });
        btnFinish.disableProperty().bind(finish.not());
    }

    @Override
    protected void onCreate(Bundle bundle) {
        this.hero = bundle.get(HERO);
        this.heroGenerator = new HeroGenerator(this.hero.getRace(), this.hero.getProfession());
        this.money.setGold(
            HeroGenerator.priceForLevelUp(this.hero.getProfession(), this.hero.getLevel()));
        this.widgetMoney.bind(this.money);

        model.live.setValue(hero.getLive().getMaxValue());
        model.strength.update(hero.getStrength());
        model.dexterity.update(hero.getDexterity());
        model.immunity.update(hero.getImmunity());
        model.intelligence.update(hero.getIntelligence());
        model.charisma.update(hero.getCharisma());

    }

    @Override
    protected void onResume() {
        setTitle(title);
        setScreenSize(600, 400);
    }

    // region Button handlers

    @FXML
    private void handleIncreaseLive(ActionEvent actionEvent) {
        final int live = model.live.get();
        model.live.setValue(live + heroGenerator.live(model.immunity));
        btnLive.setDisable(true);
        finish.setValue(true);
    }

    @FXML
    private void handleIncreaseStrength(ActionEvent actionEvent) {
        increaseProperty(model.strength);
        decreasePoints();
    }

    @FXML
    private void handleIncreaseDexterity(ActionEvent actionEvent) {
        increaseProperty(model.dexterity);
        decreasePoints();
    }

    @FXML
    private void handleIncreaseImmunity(ActionEvent actionEvent) {
        increaseProperty(model.immunity);
        decreasePoints();
    }

    @FXML
    private void handleIncreaseIntelligence(ActionEvent actionEvent) {
        increaseProperty(model.intelligence);
        decreasePoints();
    }

    @FXML
    private void handleIncreaseCharisma(ActionEvent actionEvent) {
        increaseProperty(model.charisma);
        decreasePoints();
    }

    @FXML
    private void handleFinish(ActionEvent actionEvent) {
        setResult(RESULT_SUCCESS);
        final Bundle bundle = new Bundle();

        bundle.putInt(HeroHelper.LIVE, model.live.getValue());
        bundle.putInt(HeroHelper.STRENGTH, model.strength.getValue());
        bundle.putInt(HeroHelper.DEXTERITY, model.dexterity.getValue());
        bundle.putInt(HeroHelper.IMMUNITY, model.immunity.getValue());
        bundle.putInt(HeroHelper.INTELLIGENCE, model.intelligence.getValue());
        bundle.putInt(HeroHelper.CHARISMA, model.charisma.getValue());
        bundle.putInt(HeroHelper.LEVEL_UP_PRICE, money.getRaw());

        finish(bundle);
    }

    // endregion

    private static final class Model {

        private final IntegerProperty live = new SimpleIntegerProperty();
        private final EntityProperty strength = new SimpleEntityProperty();
        private final EntityProperty dexterity = new SimpleEntityProperty();
        private final EntityProperty immunity = new SimpleEntityProperty();
        private final EntityProperty intelligence = new SimpleEntityProperty();
        private final EntityProperty charisma = new SimpleEntityProperty();
    }
}
