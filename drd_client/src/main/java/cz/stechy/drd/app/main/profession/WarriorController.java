package cz.stechy.drd.app.main.profession;

import com.google.inject.Inject;
import cz.stechy.drd.R;
import cz.stechy.drd.db.BaseOfflineTable;
import cz.stechy.drd.db.base.ITableFactory;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.entity.hero.profession.Warior;
import cz.stechy.drd.model.entity.mob.Mob;
import cz.stechy.drd.model.item.ItemType;
import cz.stechy.drd.model.item.WeaponBase;
import cz.stechy.drd.service.item.ItemRegistry;
import cz.stechy.drd.util.CellUtils;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.drd.util.ObservableMergers;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class WarriorController implements IProfessionController, Initializable {

    // region Variables

    // region FXML

    @FXML
    private ComboBox<Mob> cmbBestiaryIntimidation;

    @FXML
    private TextField txtEnemyCountIntimidation;

    @FXML
    private Button btnIntimidation;

    @FXML
    private ComboBox<WeaponBase> cmbItemsDetectArtefact;

    @FXML
    private Button btnDetectArtefact;

    // endregion

    private final ObservableList<Mob> mobs = FXCollections.observableArrayList();
    private final ObservableList<WeaponBase> items = FXCollections.observableArrayList();
    private final BaseOfflineTable<Mob> bestiary;
    private final ItemRegistry itemRegistry;

    private String successText;
    private String failText;
    private Warior warior;

    // endregion

    // region Constructors

    @Inject
    public WarriorController(ITableFactory tableFactory, ItemRegistry itemRegistry) {
        this.bestiary = tableFactory.getOfflineTable(Mob.class);
        this.itemRegistry = itemRegistry;
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.successText = resources.getString(R.Translate.SUCCESS);
        this.failText = resources.getString(R.Translate.UNSUCCESS);
        cmbBestiaryIntimidation.setItems(mobs);
        cmbBestiaryIntimidation.setCellFactory(param -> new CellUtils.RawImageListCell());

        cmbItemsDetectArtefact.setItems(items);
        cmbItemsDetectArtefact.setCellFactory(param -> new CellUtils.RawImageListCell());

        btnIntimidation.disableProperty().bind(
            cmbBestiaryIntimidation.getSelectionModel().selectedItemProperty().isNull()
                .or(txtEnemyCountIntimidation.textProperty().isEmpty()));

        btnDetectArtefact.disableProperty().bind(
            cmbItemsDetectArtefact.getSelectionModel().selectedItemProperty().isNull()
        );

        FormUtils.initTextFormater(txtEnemyCountIntimidation,
            new MaxActValue(1, Integer.MAX_VALUE, 1));

        bestiary.selectAllAsync().thenAccept(mobList -> ObservableMergers.mergeList(mobs, mobList));
        final List<WeaponBase> list = itemRegistry.getRegistry().values().stream()
            .filter(databaseItem -> ItemType.isSword(databaseItem.getItemType()))
            .map(databaseItem -> (WeaponBase) databaseItem).collect(
                Collectors.toList());
        items.setAll(list);
    }

    @Override
    public void setHero(Hero hero) {
        Hero hero1 = hero;
        this.warior = new Warior(hero);
    }

    // region Button handlers

    @FXML
    private void btnHandleIntimidation(ActionEvent actionEvent) {
        final Mob enemy = cmbBestiaryIntimidation.getValue();
        final int mettle = enemy.getMettle();
        final int count = Integer.parseInt(txtEnemyCountIntimidation.getText());
        final boolean success = warior.intimidation(mettle, count);

        final Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText("Zastrašení nepřátel");
        alert.setTitle("Úspěšnost válečníka");
        alert.setContentText(success ? successText : failText);
        alert.showAndWait();
    }

    @FXML
    private void handleDetectArtefact(ActionEvent actionEvent) {
        final WeaponBase weaponBase = cmbItemsDetectArtefact.getValue();
        final int fame = weaponBase.getRenown();
        final boolean success = warior.detectArtefact(fame);

        final Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText("Odhad artefaktu");
        alert.setTitle("Úspěšnost válečníka");
        alert.setContentText(success ? successText : failText);
        alert.showAndWait();
    }

    // endregion
}
