package cz.stechy.drd.controller.hero.opener;

import com.jfoenix.controls.JFXButton;
import cz.stechy.drd.Context;
import cz.stechy.drd.R;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.persistent.HeroService;
import cz.stechy.drd.util.ObservableMergers;
import cz.stechy.drd.util.Translator;
import cz.stechy.drd.util.Translator.Key;
import cz.stechy.drd.widget.LabeledHeroProperty;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kontroler pro načtení hrdiny
 */
public class HeroOpenerController extends BaseController implements Initializable {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(HeroOpenerController.class);

    public static final String HERO = "hero";

    // endregion

    // region Variables

    // region FXML

    @FXML
    private ListView<Hero> lvHeroes;
    @FXML
    private Label lblName;
    @FXML
    private Label lblConviction;
    @FXML
    private Label lblRace;
    @FXML
    private Label lblProfession;
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
    private Button btnOpen;
    @FXML
    private JFXButton btnDelete;

    // endregion

    private final ObservableList<Hero> heroes = FXCollections.observableArrayList();
    private final FilteredList<Hero> filteredHeroes = new FilteredList<>(heroes);
    private final ObjectProperty<Hero> selectedHero = new SimpleObjectProperty<>();
    private final HeroService heroManager;
    private final Translator translator;

    private String title;

    // endregion

    // region Constructors

    public HeroOpenerController(Context context) {
        heroManager = context.getService(Context.SERVICE_HERO);
        translator = context.getTranslator();
    }

    // endregion

    // region Private methods

    private void openHero() {
        setResult(selectedHero.isNull().get() ? RESULT_FAIL : RESULT_SUCCESS);
        Bundle bundle = new Bundle();
        bundle.put(HERO, selectedHero.getValue().getId());
        finish(bundle);
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = resources.getString(R.Translate.OPENER_TITLE);
        selectedHero.bind(lvHeroes.getSelectionModel().selectedItemProperty());
        selectedHero.addListener((observable, oldValue, newValue) -> {
            lblName.textProperty().setValue(newValue.getName());
            lblConviction.textProperty()
                .setValue(translator.getTranslationFor(Key.CONVICTIONS).get(newValue.getConviction().ordinal()));
            lblRace.textProperty()
                .setValue(translator.getTranslationFor(Key.RACES).get(newValue.getRace().ordinal()));
            lblProfession.textProperty()
                .setValue(translator.getTranslationFor(Key.PROFESSIONS).get(newValue.getProfession().ordinal()));
            lblStrength.setHeroProperty(newValue.getStrength());
            lblDexterity.setHeroProperty(newValue.getDexterity());
            lblImmunity.setHeroProperty(newValue.getImmunity());
            lblIntelligence.setHeroProperty(newValue.getIntelligence());
            lblCharisma.setHeroProperty(newValue.getCharisma());
        });

        btnOpen.disableProperty().bind(selectedHero.isNull());
        btnDelete.disableProperty().bind(selectedHero.isNull());

        filteredHeroes.setPredicate(hero -> !hero.equals(heroManager.getHero()) &&
            heroManager.heroProperty().get() != null &&
            hero.getAuthor().equals(heroManager.heroProperty().get().getAuthor())
        );

        lvHeroes.setItems(filteredHeroes);
        lvHeroes.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                openHero();
            }
        });

        ObservableMergers.mergeList(heroes, heroManager.selectAll());
    }

    @Override
    protected void onResume() {
        setScreenSize(400, 400);
        setTitle(title);
    }

    // region Buton handlers

    @FXML
    private void handleOpenHero(ActionEvent actionEvent) {
        openHero();
    }

    @FXML
    private void handleDeleteHero(ActionEvent actionEvent) {
        if (selectedHero.get() == null) {
            return;
        }

        try {
            heroManager.delete(selectedHero.get().getId());
        } catch (DatabaseException e) {
            logger.error(e.getMessage(), e);
        }
    }

    // endregion
}
