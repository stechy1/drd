package cz.stechy.drd.controller.hero.opener;

import cz.stechy.drd.R;
import cz.stechy.drd.model.Context;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.persistent.HeroManager;
import cz.stechy.drd.model.persistent.UserManager;
import cz.stechy.drd.util.Translator;
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

/**
 * Kontroler pro načtení hrdiny
 */
public class HeroOpenerController extends BaseController implements Initializable {



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

    // endregion

    private final ObservableList<Hero> heroes = FXCollections.observableArrayList();
    private final FilteredList<Hero> filteredHeroes = new FilteredList<>(heroes);
    private final ObjectProperty<Hero> selectedHero = new SimpleObjectProperty<>();
    private final HeroManager heroManager;
    private final UserManager userManager;
    private Translator translator;
    private String title;

    // endregion

    // region Constructors

    public HeroOpenerController(Context context) {
        heroManager = context.getManager(Context.MANAGER_HERO);
        userManager = context.getUserManager();
        translator = context.getTranslator();
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = resources.getString(R.Translate.OPENER_TITLE);
        selectedHero.bind(lvHeroes.getSelectionModel().selectedItemProperty());
        selectedHero.addListener((observable, oldValue, newValue) -> {
            lblName.textProperty().setValue(newValue.getName());
            lblConviction.textProperty()
                .setValue(translator.getConvictionList().get(newValue.getConviction().ordinal()));
            lblRace.textProperty()
                .setValue(translator.getRaceList().get(newValue.getRace().ordinal()));
            lblProfession.textProperty()
                .setValue(translator.getProfessionList().get(newValue.getProfession().ordinal()));
            lblStrength.setHeroProperty(newValue.getStrength());
            lblDexterity.setHeroProperty(newValue.getDexterity());
            lblImmunity.setHeroProperty(newValue.getImmunity());
            lblIntelligence.setHeroProperty(newValue.getIntelligence());
            lblCharisma.setHeroProperty(newValue.getCharisma());
        });

        btnOpen.disableProperty().bind(selectedHero.isNull());

        filteredHeroes.setPredicate(hero -> !hero.equals(heroManager.getHero().get()) &&
                heroManager.getHero().get() != null &&
                hero.getAuthor().equals(heroManager.getHero().get().getAuthor())
            );

        lvHeroes.setItems(filteredHeroes);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        heroes.setAll(heroManager.selectAll());
    }

    @Override
    protected void onResume() {
        setScreenSize(400, 250);
        setTitle(title);
    }

    public void handleOpenHero(ActionEvent actionEvent) {
        setResult(selectedHero.isNull().get() ? RESULT_FAIL : RESULT_SUCCESS);
        Bundle bundle = new Bundle();
        bundle.put(HeroOpenerHelper.HERO, selectedHero.getValue());
        finish(bundle);
    }
}
