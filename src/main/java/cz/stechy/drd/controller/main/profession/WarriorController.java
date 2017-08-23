package cz.stechy.drd.controller.main.profession;

import cz.stechy.drd.Context;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.entity.hero.profession.Warior;
import cz.stechy.drd.model.entity.mob.Mob;
import cz.stechy.drd.model.persistent.BestiaryService;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.drd.util.ObservableMergers;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class WarriorController implements IProfessionController, Initializable {

    // region Variables

    // region FXML

    @FXML
    private ComboBox<Mob> cmbBestiaryIntimidation;

    @FXML
    private TextField txtEnemyCountIntimidation;

    @FXML
    private Button btnIntimidation;

    // endregion

    private final ObservableList<Mob> mobs = FXCollections.observableArrayList();
    private final BestiaryService bestiary;

    private Hero hero;
    private Warior warior;

    // endregion

    // region Constructors

    public WarriorController(Context context) {
        this.bestiary = context.getService(Context.SERVICE_BESTIARY);
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbBestiaryIntimidation.setItems(mobs);
        cmbBestiaryIntimidation.setCellFactory(param -> new BestiaryCell());

        btnIntimidation.disableProperty().bind(
            cmbBestiaryIntimidation.getSelectionModel().selectedItemProperty().isNull()
                .or(txtEnemyCountIntimidation.textProperty().isEmpty()));

        FormUtils.initTextFormater(txtEnemyCountIntimidation,
            new MaxActValue(1, Integer.MAX_VALUE, 1));

        ObservableMergers.mergeList(mobs, bestiary.selectAll());
    }

    @Override
    public void setHero(Hero hero) {
        this.hero = hero;
        this.warior = new Warior(hero);
    }

    // region Public methods



    // endregion

    // region Button handlers

    @FXML
    private void btnHandleIntimidation(ActionEvent actionEvent) {
        final Mob enemy = cmbBestiaryIntimidation.getValue();
        final int mettle = enemy.getMettle();
        final int count = Integer.parseInt(txtEnemyCountIntimidation.getText());

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText("Header");
        alert.setContentText(String.valueOf(warior.intimidation(mettle, count)));
        alert.setTitle("Title");
    }

    // endregion

    private static final class BestiaryCell extends ListCell<Mob> {

        final ImageView imageView = new ImageView();
        final Label label = new Label();
        final HBox container = new HBox(imageView, label);

        {
            imageView.setFitWidth(40);
            imageView.setFitHeight(40);

            label.setTextFill(Color.BLACK);
            label.setMinHeight(40);
            label.setAlignment(Pos.CENTER_LEFT);
            label.setTextAlignment(TextAlignment.CENTER);

            container.setSpacing(8);
        }

        @Override
        protected void updateItem(Mob item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                final ByteArrayInputStream inputStream = new ByteArrayInputStream(item.getImage());
                imageView.setImage(new Image(inputStream));
                label.setText(item.getName());
                setGraphic(container);
            }
        }
    }
}
