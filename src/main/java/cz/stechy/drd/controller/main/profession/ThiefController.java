package cz.stechy.drd.controller.main.profession;

import cz.stechy.drd.Context;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.entity.hero.profession.Thief;
import cz.stechy.drd.model.entity.hero.profession.Thief.Ability;
import cz.stechy.drd.util.Translator;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

public class ThiefController implements IProfessionController, Initializable {

    // region Variables

    // region FXML

    @FXML
    private ComboBox<Ability> cmbAbilities;

    // endregion

    private final ObservableList<Ability> abilities = FXCollections.observableArrayList(Ability.values());
    private final Translator translator;
    private Hero hero;
    private Thief thief;

    // endregion

    // region Constructors

    public ThiefController(Context context) {
        this.translator = context.getTranslator();
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbAbilities.setItems(abilities);
    }

    @Override
    public void setHero(Hero hero) {
        this.hero = hero;
        this.thief = new Thief(hero);
    }

    // region Button handlers

    public void handleUseAbility(ActionEvent actionEvent) {

    }

    // endregion
}
