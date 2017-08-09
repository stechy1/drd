package cz.stechy.drd.controller.fight;

import com.jfoenix.controls.JFXComboBox;
import cz.stechy.drd.Context;
import cz.stechy.drd.controller.bestiary.BestiaryHelper;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.entity.mob.Mob;
import cz.stechy.drd.model.persistent.BestiaryService;
import cz.stechy.drd.widget.LabeledHeroProperty;
import cz.stechy.drd.widget.LabeledMaxActValue;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
    private Label lblAttackNumber;
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

        this.selectedMob.bind(Bindings.createObjectBinding(() -> {
                final Mob selectedItem = cmbBestiary.getSelectionModel().getSelectedItem();
                if (selectedItem == null) {
                    return null;
                }
                return selectedItem.duplicate();
            }, cmbBestiary.getSelectionModel().selectedItemProperty()));
        this.selectedMob.addListener(mobListener);
    }

    @Override
    public void setHero(Hero hero) {
        // Zde opravdu nic není
    }

    private ChangeListener<? super Mob> mobListener = (observable, oldValue, newValue) -> {
        lblName.textProperty().setValue(newValue.getName());
        lblViability.setText(String.valueOf(newValue.getViability()));
        lblImmunity.setHeroProperty(newValue.getImmunity());
        lblIntelligence.setHeroProperty(newValue.getIntelligence());
        lblCharisma.setHeroProperty(newValue.getCharisma());
        lblAttackNumber.setText(String.valueOf(newValue.getAttackNumber()));
        lblDefenceNumber.setText(String.valueOf(newValue.getDefenceNumber()));
        lblLive.setMaxActValue(newValue.getLive());
    };

    // region Button handlers

    @FXML
    private void handleRevive(ActionEvent actionEvent) {
        final Mob mob = selectedMob.get();
        final int live = BestiaryHelper.getLive(mob.getViability(), mob.getImmunity());
        selectedMob.get().getLive().update(new MaxActValue(0, live, live));
    }

    // endregion
}
