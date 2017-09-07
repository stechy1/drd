package cz.stechy.drd.controller.main.profession;

import cz.stechy.drd.model.Dice;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.entity.hero.profession.Thief;
import cz.stechy.drd.model.entity.hero.profession.Thief.Ability;
import cz.stechy.drd.util.Translator;
import cz.stechy.drd.util.Translator.Key;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;

public class ThiefController implements IProfessionController, Initializable {

    // region Variables

    // region FXML

    @FXML
    private ComboBox<Ability> cmbAbilities;

    @FXML
    private Text txtSuccess;

    @FXML
    private Button btnUseAbility;

    // endregion

    private final ObservableList<Ability> abilities = FXCollections.observableArrayList(Ability.values());
    private final ObjectProperty<Ability> selectedAbility = new SimpleObjectProperty<>();
    private final IntegerProperty success = new SimpleIntegerProperty();
    private final Translator translator;
    private Hero hero;
    private Thief thief;

    // endregion

    // region Constructors

    public ThiefController(Translator translator) {
        this.translator = translator;
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbAbilities.setItems(abilities);
        cmbAbilities.setConverter(translator.getConvertor(Key.THIEF_ABILITIES));

        selectedAbility.bind(cmbAbilities.getSelectionModel().selectedItemProperty());
        btnUseAbility.disableProperty().bind(selectedAbility.isNull());

        success.bind(Bindings.createIntegerBinding(() -> {
            if (selectedAbility.get() == null) {
                return 0;
            }

            final Ability ability = selectedAbility.get();
            return thief.getProbabilityOfSuccessForAction(ability);
        }, selectedAbility));
        txtSuccess.textProperty().bind(Bindings.createStringBinding(() ->
            String.format("%d%%", success.get()), success));
    }

    @Override
    public void setHero(Hero hero) {
        this.hero = hero;
        this.thief = new Thief(hero);
    }

    // region Button handlers

    public void handleUseAbility(ActionEvent actionEvent) {
        final boolean fail = Dice.K100.roll() > success.get();

        final Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("title");
        alert.setHeaderText("header");
        alert.setContentText(String.valueOf(!fail));
        alert.showAndWait();
    }

    // endregion
}
