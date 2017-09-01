package cz.stechy.drd.controller.main.profession;

import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.ValidatedModel;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.entity.hero.profession.Ranger;
import cz.stechy.drd.model.entity.hero.profession.Ranger.Terrain;
import cz.stechy.drd.model.entity.hero.profession.Ranger.TrackingProperties;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.drd.util.StringConvertors;
import cz.stechy.drd.util.Translator;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

public class RangerController implements IProfessionController, Initializable {

    // region Variables

    // region FXML

    @FXML
    private ComboBox cmbTerrain;
    @FXML
    private TextField txtAgeOfTrail;
    @FXML
    private TextField txtEnemyCount;
    @FXML
    private ToggleButton toggleContinueTracking;
    @FXML
    private Button btnTracking;

    // endregion

    private final TrackingModel trackingModel = new TrackingModel();
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
        cmbTerrain.setConverter(StringConvertors.forTerrain(translator));
        trackingModel.terrain.bind(cmbTerrain.getSelectionModel().selectedItemProperty());

        FormUtils.initTextFormater(txtAgeOfTrail, trackingModel.age);
        FormUtils.initTextFormater(txtEnemyCount, trackingModel.count);
        toggleContinueTracking.selectedProperty().bindBidirectional(trackingModel.repeating);

        btnTracking.disableProperty().bind(trackingModel.validProperty().not());
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
            terrain.addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_TERRAIN, true);
                } else {
                    setValidityFlag(FLAG_TERRAIN, false);
                }
            });
            age.actValueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_AGE, true);
                } else {
                    setValidityFlag(FLAG_AGE, false);
                }
            });
            count.actValueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_COUNT, true);
                } else {
                    setValidityFlag(FLAG_COUNT, false);
                }
            });

            validityFlag.set(FLAG_TERRAIN);
            setValid(false);
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
