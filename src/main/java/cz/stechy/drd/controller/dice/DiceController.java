package cz.stechy.drd.controller.dice;

import cz.stechy.drd.R;
import cz.stechy.drd.controller.dice.DiceHelper.AdditionType;
import cz.stechy.drd.controller.dice.DiceHelper.DiceAddition;
import cz.stechy.drd.controller.dice.DiceHelper.DiceType;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.drd.util.Translator;
import cz.stechy.drd.util.Translator.Key;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.VBox;

/**
 * Kontroler pro házení kostkou
 */
public class DiceController extends BaseController implements Initializable {

    // region Variables

    // region FXML

    @FXML
    private VBox diceContainer;
    @FXML
    private TableView<DiceAddition> tableAdditions;
    @FXML
    private TableColumn<DiceAddition, AdditionType> columnAdditionType;
    @FXML
    private TableColumn<DiceAddition, Boolean> columnUseRepair;
    @FXML
    private TableColumn<DiceAddition, Boolean> columnUseSubtract;
    @FXML
    private TextField txtDiceSideCount;
    @FXML
    private TextField txtRollCount;
    @FXML
    private Label lblRollResult;
    @FXML
    private Button btnAddAddition;

    // endregion

    private final ToggleGroup diceGroup = new ToggleGroup();
    private final MaxActValue diceSideCount = new MaxActValue(1, Integer.MAX_VALUE, 1);
    private final MaxActValue diceRollCount = new MaxActValue(1, Integer.MAX_VALUE, 1);
    private final Hero hero;
    private final Translator translator;

    private DiceHelper diceHelper;
    private String title;

    // endregion

    // region Constructors

    public DiceController(Hero hero, Translator translator) {
        this.hero = hero;
        this.translator = translator;
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = resources.getString(R.Translate.DICE_TITLE);

        final String customDiceTranslate = resources.getString(R.Translate.DICE_CUSTOM);
        final List<RadioButton> radioButtons = Arrays.stream(DiceType.values()).map(diceType -> {
            final RadioButton radio = new RadioButton(diceType == DiceType.CUSTOM ? customDiceTranslate : diceType.toString());
            radio.setToggleGroup(diceGroup);
            radio.setUserData(diceType == DiceType.CUSTOM ? 0 : diceType.getSideCount());
            if (diceType == DiceType.CUSTOM) {
                radio.setSelected(true);
            }
            return radio;
        }).collect(Collectors.toList());
        diceContainer.getChildren().setAll(radioButtons);
        diceGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            Integer value = (Integer) newValue.getUserData();
            diceSideCount.setActValue(value);
            txtDiceSideCount.setDisable(value != 0);
        });

        FormUtils.initTextFormater(txtDiceSideCount, diceSideCount);
        FormUtils.initTextFormater(txtRollCount, diceRollCount);

        initTable();
    }

    /**
     * Inicializuje tabulku pro přidávání konstant k hodu kostkou
     */
    private void initTable() {
        columnAdditionType.setCellFactory(ComboBoxTableCell.forTableColumn(translator.getConvertor(
            Key.DICE_ADDITION_PROPERTIES), AdditionType.values()));
        columnAdditionType.setOnEditCommit(
            event -> tableAdditions.getItems().get(event.getTablePosition().getRow())
                .setAdditionType(event.getNewValue()));

        columnUseRepair.setCellFactory(CheckBoxTableCell.forTableColumn(columnUseRepair));
        columnUseRepair.setOnEditCommit(
            event -> tableAdditions.getItems().get(event.getTablePosition().getRow())
                .setUseRepair(event.getNewValue()));

        columnUseSubtract.setCellFactory(CheckBoxTableCell.forTableColumn(columnUseSubtract));
        columnUseSubtract.setOnEditCommit(
            event -> tableAdditions.getItems().get(event.getTablePosition().getRow())
                .setUseSubtract(event.getNewValue()));
        btnAddAddition.setDisable(hero == null);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        diceHelper = new DiceHelper(hero);
        tableAdditions.setItems(diceHelper.additions);

        diceHelper.rollResults.addListener((ListChangeListener<Integer>) c -> {
            String result = c.getList().stream().map(o -> {
                int value1 = o.intValue();
                if (value1 >= 0) {
                    return String.valueOf(value1);
                }
                return "(" + value1 + ")";
            }).collect(Collectors.joining(" + "));
            if (c.getList().size() > 1) {
                result += " = " + c.getList().stream().mapToInt(value -> value).sum();
            }
            lblRollResult.setText(result);
        });
    }

    @Override
    protected void onResume() {
        setScreenSize(470, 300);
        setTitle(title);
    }

    // region Button handles

    @FXML
    private void handleAddAddition(ActionEvent actionEvent) {
        diceHelper.additions.add(new DiceAddition());
    }

    @FXML
    private void handleRemoveAddition(ActionEvent actionEvent) {
        diceHelper.additions.removeAll(tableAdditions.getSelectionModel().getSelectedItems());
    }

    @FXML
    private void handleRoll(ActionEvent actionEvent) {
        diceHelper.roll(diceSideCount.getActValue().intValue(), diceRollCount.getActValue().intValue());
    }

    // endregion
}
