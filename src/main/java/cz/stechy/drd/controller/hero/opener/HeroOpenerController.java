package cz.stechy.drd.controller.hero.opener;

import cz.stechy.drd.R;
import cz.stechy.drd.model.Context;
import cz.stechy.drd.model.db.DatabaseManager;
import cz.stechy.drd.model.entity.hero.Hero;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Kontroler pro načtení hrdiny
 */
public class HeroOpenerController extends BaseController implements Initializable {

    // region Constants

    private static final String TITLE_KEY = "drd_opener_title";

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

    // endregion

    private final ObservableList<Hero> heroes = FXCollections.observableArrayList();
    private final ObjectProperty<Hero> selectedHero = new SimpleObjectProperty<>();
    private final DatabaseManager heroManager;
    private Translator translator;
    private String title;

    // endregion

    // region Constructors

    public HeroOpenerController(Context context) {
        heroManager = context.getManager(Context.MANAGER_HERO);
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
        lvHeroes.setItems(heroes);
        lvHeroes.setCellFactory(new Callback<ListView<Hero>, ListCell<Hero>>() {
            @Override
            public ListCell<Hero> call(ListView<Hero> param) {
                ListCell<Hero> cell = new ListCell<Hero>() {
                    @Override
                    protected void updateItem(Hero item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                };

                return cell;
            }
        });
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
