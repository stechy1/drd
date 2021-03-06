package cz.stechy.drd.controller.dice;

import cz.stechy.drd.R;
import cz.stechy.drd.controller.dice.DiceHelper.AdditionType;
import cz.stechy.drd.controller.dice.DiceHelper.DiceAddition;
import cz.stechy.drd.controller.dice.DiceHelper.DiceType;
import cz.stechy.drd.model.Context;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.persistent.HeroManager;
import cz.stechy.drd.util.StringConvertors;
import cz.stechy.drd.util.Translator;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

/**
 * Kontroler pro házení kostkou
 */
public class DiceController extends BaseController implements Initializable {

    // region Variables

    // region FXML

    @FXML
    private ListView<DiceType> lvDices;
    @FXML
    private TableView<DiceAddition> tableAdditions;
    @FXML
    private TableColumn<DiceAddition, AdditionType> columnAdditionType;
    @FXML
    private TableColumn<DiceAddition, Boolean> columnUseRepair;
    @FXML
    private TableColumn<DiceAddition, Boolean> columnUseSubtract;
    @FXML
    private Spinner<Integer> spinnerDiceSideCount;
    @FXML
    private Spinner<Integer> spinnerRollCount;
    @FXML
    private Label lblRollResult;

    // endregion

    private final IntegerProperty diceSideCount = new SimpleIntegerProperty();
    private final IntegerProperty diceRollCount = new SimpleIntegerProperty();
    private final ObjectProperty<Hero> hero;

    private DiceHelper diceHelper;
    private String title;
    private Translator translator;

    // endregion

    // region Constructors

    public DiceController(Context context) {
        this.translator = context.getTranslator();
        this.hero = ((HeroManager) context.getManager(Context.MANAGER_HERO)).getHero();
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = resources.getString(R.Translate.DICE_TITLE);

        lvDices.setItems(FXCollections.observableArrayList(DiceHelper.DiceType.values()));
        lvDices.setCellFactory(new Callback<ListView<DiceType>, ListCell<DiceType>>() {
            @Override
            public ListCell<DiceType> call(ListView<DiceType> param) {
                ListCell<DiceType> cell = new ListCell<DiceType>() {
                    @Override
                    protected void updateItem(DiceType item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            if (item == DiceType.CUSTOM) {
                                setText("Vlastní");
                            } else {
                                setText(item.toString());
                            }
                        }
                    }
                };
                return cell;
            }
        });
        spinnerDiceSideCount.disableProperty().bind(
            lvDices.getFocusModel().focusedIndexProperty().isEqualTo(0).not());
        // TODO vymyslet lepší způsob
        spinnerDiceSideCount.valueProperty().addListener((observable, oldValue, newValue) -> {
            diceSideCount.setValue(newValue);
        });
        lvDices.getFocusModel().focusedItemProperty()
            .addListener((observable, oldValue, newValue) -> {
                diceSideCount.setValue(newValue.getSideCount());
            });
        diceRollCount.bind(spinnerRollCount.valueProperty());

        initTable();
    }

    /**
     * Inicializuje tabulku pro přidávání konstant k hodu kostkou
     */
    private void initTable() {
        columnAdditionType.setCellValueFactory(new PropertyValueFactory<>("additionType"));
        columnAdditionType.setCellFactory(ComboBoxTableCell
            .forTableColumn(StringConvertors.forAdditionType(translator), AdditionType.values()));
        columnAdditionType.setOnEditCommit(
            event -> tableAdditions.getItems().get(event.getTablePosition().getRow())
                .setAdditionType(event.getNewValue()));

        columnUseRepair.setCellValueFactory(new PropertyValueFactory<>("useRepair"));
        columnUseRepair.setCellFactory(CheckBoxTableCell.forTableColumn(columnUseRepair));
        columnUseRepair.setOnEditCommit(
            event -> tableAdditions.getItems().get(event.getTablePosition().getRow())
                .setUseRepair(event.getNewValue()));

        columnUseSubtract.setCellValueFactory(new PropertyValueFactory<>("useSubtract"));
        columnUseSubtract.setCellFactory(CheckBoxTableCell.forTableColumn(columnUseSubtract));
        columnUseSubtract.setOnEditCommit(
            event -> tableAdditions.getItems().get(event.getTablePosition().getRow())
                .setUseSubtract(event.getNewValue()));
    }

    @Override
    protected void onCreate(Bundle bundle) {
        diceHelper = new DiceHelper(hero.get());
        tableAdditions.setItems(diceHelper.additions);

        diceHelper.rollResults.addListener((ListChangeListener<Integer>) c -> {
            String rolls = c.getList().stream().map(o -> {
                int value = o.intValue();
                if (value >= 0) {
                    return String.valueOf(value);
                }
                return "(" + value + ")";
            }).collect(Collectors.joining(" + "));
            String result = rolls;
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

    public void handleAddAddition(ActionEvent actionEvent) {
        diceHelper.additions.add(new DiceAddition());
    }

    public void handleRemoveAddition(ActionEvent actionEvent) {
        diceHelper.additions.removeAll(tableAdditions.getSelectionModel().getSelectedItems());
    }

    public void handleRoll(ActionEvent actionEvent) {
        diceHelper.roll(diceSideCount.getValue(), diceRollCount.getValue());
    }

    // endregion
}
