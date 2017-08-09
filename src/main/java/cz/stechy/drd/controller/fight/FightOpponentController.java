package cz.stechy.drd.controller.fight;

import com.jfoenix.controls.JFXComboBox;
import cz.stechy.drd.Context;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.entity.mob.Mob;
import cz.stechy.drd.model.persistent.BestiaryService;
import cz.stechy.drd.widget.LabeledHeroProperty;
import cz.stechy.drd.widget.LabeledMaxActValue;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * Kontroler pro ovládání pravé částireprezentující protivníka
 */
public class FightOpponentController implements Initializable, IFightChild {

    // region Variables

    // region FXML

    @FXML
    private JFXComboBox<Mob> cmbBestiary;
    @FXML
    private Label lblName;
    @FXML
    private Label lblViability;
    @FXML
    private LabeledHeroProperty lblImmunity;
    @FXML
    private LabeledHeroProperty lblIntelligence;
    @FXML
    private LabeledHeroProperty lblCharisma;
    @FXML
    private LabeledHeroProperty lblAttackNumber;
    @FXML
    private Label lblDefenceNumber;
    @FXML
    private LabeledMaxActValue lblLive;

    // endregion

    private final ObservableList<Mob> mobs;
    private final BestiaryService bestiary;
    private final ObjectProperty<Mob> selectedMob = new SimpleObjectProperty<>();

    // endregion

    public FightOpponentController(Context context) {
        this.bestiary = context.getService(Context.SERVICE_BESTIARY);
        this.mobs = bestiary.selectAll();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.cmbBestiary.setItems(mobs);
        this.selectedMob.bind(cmbBestiary.getSelectionModel().selectedItemProperty());
        this.selectedMob.addListener(mobListener);
    }

    @Override
    public void setHero(Hero hero) {

    }

    private ChangeListener<? super Mob> mobListener = (observable, oldValue, newValue) -> {
        lblName.textProperty().setValue(newValue.getName());
        lblImmunity.setHeroProperty(newValue.getImmunity());
        lblIntelligence.setHeroProperty(newValue.getIntelligence());
        lblCharisma.setHeroProperty(newValue.getCharisma());
        lblAttackNumber.setHeroProperty(newValue.getAttackNumber());
        lblDefenceNumber.setText(String.valueOf(newValue.getDefenceNumber()));
        lblLive.setMaxActValue(newValue.getLive());
    };
}
