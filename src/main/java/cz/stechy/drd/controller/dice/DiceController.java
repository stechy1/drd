package cz.stechy.drd.controller.dice;

import cz.stechy.drd.R;
import cz.stechy.drd.controller.dice.DiceHelper.AdditionType;
import cz.stechy.drd.controller.dice.DiceHelper.DiceAddition;
import cz.stechy.drd.controller.dice.DiceHelper.DiceType;
import cz.stechy.drd.Context;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.persistent.HeroService;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.drd.util.StringConvertors;
import cz.stechy.drd.util.Translator;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    private TextField txtDiceSideCount;
    @FXML
    private TextField txtRollCount;
    @FXML
    private Label lblRollResult;

    // endregion

    private final MaxActValue diceSideCount = new MaxActValue(1, Integer.MAX_VALUE, 1);
    private final MaxActValue diceRollCount = new MaxActValue(1, Integer.MAX_VALUE, 1);
    private final ObjectProperty<Hero> hero;
    private final Translator translator;

    private DiceHelper diceHelper;
    private String title;

    // endregion

    // region Constructors

    public DiceController(Context context) {
        this.translator = context.getTranslator();
        this.hero = ((HeroService) context.getService(Context.SERVICE_HERO)).getHero();
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
        txtDiceSideCount.disableProperty().bind(
            lvDices.getFocusModel().focusedIndexProperty().isEqualTo(0).not());

        FormUtils.initTextFormater(txtDiceSideCount, diceSideCount);
        FormUtils.initTextFormater(txtRollCount, diceRollCount);

        lvDices.getFocusModel().focusedItemProperty()
            .addListener((observable, oldValue, newValue) ->
                diceSideCount.setActValue(newValue.getSideCount()));

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
