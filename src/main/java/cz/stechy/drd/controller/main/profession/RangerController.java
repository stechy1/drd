package cz.stechy.drd.controller.main.profession;

import com.jfoenix.controls.JFXButton;
import cz.stechy.drd.R;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.ValidatedModel;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.entity.hero.profession.Ranger;
import cz.stechy.drd.model.entity.hero.profession.Ranger.Terrain;
import cz.stechy.drd.model.entity.hero.profession.Ranger.TrackingProperties;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.drd.util.Translator;
import cz.stechy.drd.util.Translator.Key;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

public class RangerController implements IProfessionController, Initializable {

    // region Variables

    // region FXML

    @FXML
    private ComboBox<Terrain> cmbTerrain;
    @FXML
    private TextField txtAgeOfTrail;
    @FXML
    private TextField txtEnemyCount;
    @FXML
    private ToggleButton toggleContinueTracking;
    @FXML
    private Button btnTracking;

    @FXML
    private TextField txtDistance;
    @FXML
    private TextField txtWeight;
    @FXML
    private Label lblSuccessTelekinesisResult;

    @FXML
    private TextField txtPyroDistance;
    @FXML
    private JFXButton btnPyroCalculate;
    @FXML
    private Button btnPyroCancel;
    @FXML
    private Button btnPyroFire;

    // endregion

    private final TrackingModel trackingModel = new TrackingModel();
    private final MaxActValue teleDistance = new MaxActValue(1, Integer.MAX_VALUE, null);
    private final MaxActValue teleWeight = new MaxActValue(1, Integer.MAX_VALUE, null);
    private final MaxActValue pyroDistance = new MaxActValue(1, Integer.MAX_VALUE, null);
    private final IntegerProperty pyroRemainingAttempts = new SimpleIntegerProperty(this, "pyroRemainingAttempts", 0);
    private final BooleanProperty pyroInicialized = new SimpleBooleanProperty(this, "pyroInicialized", false);
    private final Translator translator;

    private Hero hero;
    private Ranger ranger;

    // endregion

    // region Constructors

    public RangerController(Translator translator) {
        this.translator = translator;
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbTerrain.setConverter(translator.getConvertor(Key.TERRAIN_DIFICULTY));
        trackingModel.terrain.bind(cmbTerrain.getSelectionModel().selectedItemProperty());

        FormUtils.initTextFormater(txtAgeOfTrail, trackingModel.age);
        FormUtils.initTextFormater(txtEnemyCount, trackingModel.count);
        FormUtils.initTextFormater(txtDistance, teleDistance);
        FormUtils.initTextFormater(txtWeight, teleWeight);
        FormUtils.initTextFormater(txtPyroDistance, pyroDistance);
        toggleContinueTracking.selectedProperty().bindBidirectional(trackingModel.repeating);

        btnTracking.disableProperty().bind(trackingModel.validProperty().not());
        lblSuccessTelekinesisResult.textProperty().bind(Bindings.createStringBinding(() -> {
            if (teleDistance.getActValue() == null || teleWeight.getActValue() == null || ranger == null) {
                return resources.getString(R.Translate.NO_ACTION);
            }

            final int d = teleDistance.getActValue().intValue();
            final int w = teleWeight.getActValue().intValue();
            final boolean telekinesis = ranger.telekinesis(d, w);
            final String translateKey = telekinesis ? R.Translate.SUCCESS : R.Translate.UNSUCCESS;

            return resources.getString(translateKey);
        }, teleDistance.actValueProperty(), teleWeight.actValueProperty()));

        pyroInicialized.bind(Bindings.createBooleanBinding(() -> pyroRemainingAttempts.get() > 0,
            pyroRemainingAttempts));

        btnPyroFire.disableProperty().bind(pyroInicialized.not());
        btnPyroCancel.disableProperty().bind(pyroInicialized.not());
        btnPyroCalculate.disableProperty().bind(pyroInicialized);
    }

    @Override
    public void setHero(Hero hero) {
        this.hero = hero;
        this.ranger = new Ranger(hero);
    }

    // region Button handlers

    @FXML
    private void handleTracking(ActionEvent actionEvent) {
        final boolean success = ranger.tracking(trackingModel.getProperties());

        System.out.println("Handle tracking: " + success);
    }

    public void handlePyroCalculate(ActionEvent actionEvent) {
        final int attempts = ranger.pyrokinesis(pyroDistance.getActValue().intValue());
        pyroRemainingAttempts.set(attempts);
    }

    @FXML
    private void handlePyroFire(ActionEvent actionEvent) {
        final int remaining = pyroRemainingAttempts.get();
        pyroRemainingAttempts.set(remaining - 1);
        System.out.println(pyroRemainingAttempts);
    }

    @FXML
    private void handlePyroCancel(ActionEvent actionEvent) {
        pyroRemainingAttempts.set(0);
    }

    // endregion

    private static final class TrackingModel extends ValidatedModel {

        private static final int FLAG_TERRAIN = 1 << 0;
        private static final int FLAG_AGE = 1 << 1;
        private static final int FLAG_COUNT = 1 << 2;

        private final ObjectProperty<Terrain> terrain = new SimpleObjectProperty<>(this, "terrain", null);
        private final MaxActValue age = new MaxActValue(Integer.MAX_VALUE);
        private final MaxActValue count = new MaxActValue(1, Integer.MAX_VALUE, 1);
        private final BooleanProperty repeating = new SimpleBooleanProperty(this, "repeating", false);

        {
            terrain.addListener(FormUtils.notEmptyCondition(this, FLAG_TERRAIN));
            age.actValueProperty().addListener(FormUtils.notEmptyCondition(this, FLAG_AGE));
            count.actValueProperty().addListener(FormUtils.notEmptyCondition(this, FLAG_COUNT));

            validityFlag.set(FLAG_TERRAIN);
        }

        public TrackingProperties getProperties() {
            return new TrackingProperties.Builder()
                .terrain(terrain.get())
                .age(age.getActValue().intValue())
                .count(count.getActValue().intValue())
                .repeating(repeating.get())
                .build();
        }
    }
}
