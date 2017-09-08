package cz.stechy.drd.controller.fight;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import cz.stechy.drd.controller.InjectableChild;
import cz.stechy.drd.controller.MoneyController;
import cz.stechy.drd.controller.bestiary.BestiaryHelper;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.entity.mob.Mob;
import cz.stechy.drd.model.persistent.BestiaryService;
import cz.stechy.drd.widget.LabeledHeroProperty;
import cz.stechy.drd.widget.LabeledMaxActValue;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
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
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

/**
 * Kontroler pro ovládání pravé částireprezentující protivníka
 */
public class FightOpponentController implements Initializable, IFightChild, InjectableChild {

    private static final int ACTION_MONEY = 1;

    // region Variables

    // region FXML

    @FXML
    private JFXComboBox<Mob> cmbBestiary;
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
    @FXML
    private Hyperlink lblPrice;
    @FXML
    private JFXButton btnRevive;

    // endregion

    private final ObservableList<Mob> mobs;
    private final BestiaryService bestiary;
    private final ObjectProperty<Mob> selectedMob = new SimpleObjectProperty<>();
    private final Money treasure = new Money();
    private BaseController parent;

    // endregion

    public FightOpponentController(BestiaryService bestiaryService) {
        this.bestiary = bestiaryService;
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
        btnRevive.disableProperty().bind(cmbBestiary.getSelectionModel().selectedItemProperty().isNull());
        lblPrice.textProperty().bind(treasure.text);
    }

    @Override
    public void injectParent(BaseController parent) {
        this.parent = parent;
    }

    @Override
    public void onScreenResult(int statusCode, int actionId, Bundle bundle) {
        switch (actionId) {
            case ACTION_MONEY:
                if (statusCode != BaseController.RESULT_SUCCESS) {
                    return;
                }

                treasure.setRaw(bundle.getInt(MoneyController.MONEY));
                break;
        }
    }

    @Override
    public void setHero(Hero hero) {
        // Zde opravdu nic není
    }

    private ChangeListener<? super Mob> mobListener = (observable, oldValue, newValue) -> {
        lblViability.setText(String.valueOf(newValue.getViability()));
        lblImmunity.bind(newValue.getImmunity());
        lblIntelligence.bind(newValue.getIntelligence());
        lblCharisma.bind(newValue.getCharisma());
        lblAttackNumber.setText(String.valueOf(newValue.getAttackNumber()));
        lblDefenceNumber.setText(String.valueOf(newValue.getDefenceNumber()));
        lblLive.bind(newValue.getLive());
    };

    // region Button handlers

    @FXML
    private void handleRevive(ActionEvent actionEvent) {
        final Mob mob = selectedMob.get();
        if (mob == null) {
            return;
        }
        final int live = BestiaryHelper.getLive(mob.getViability(), mob.getImmunity());
        selectedMob.get().getLive().update(new MaxActValue(0, live, live));
    }

    @FXML
    private void handleShowMoneyPopup(ActionEvent actionEvent) {
        Bundle bundle = new Bundle().put(MoneyController.MONEY, treasure.getRaw());
        parent.startNewPopupWindowForResult("money", ACTION_MONEY, bundle, (Node) actionEvent.getSource());
    }

    // endregion

    // region Getters & Setters

    Mob getMob() {
        return selectedMob.get();
    }

    ObjectProperty<Mob> selectedMobProperty() {
        return selectedMob;
    }

    Money getTreasure() {
        return treasure;
    }

    // endregion
}
