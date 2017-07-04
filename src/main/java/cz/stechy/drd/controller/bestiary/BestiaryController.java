package cz.stechy.drd.controller.bestiary;

import cz.stechy.drd.R;
import cz.stechy.drd.model.Context;
import cz.stechy.drd.model.Rule;
import cz.stechy.drd.model.db.AdvancedDatabaseService;
import cz.stechy.drd.model.entity.mob.Mob;
import cz.stechy.drd.model.entity.mob.Mob.MobClass;
import cz.stechy.drd.util.StringConvertors;
import cz.stechy.drd.util.Translator;
import cz.stechy.screens.BaseController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kontroler pro správu jednotlivých nestvůr ve hře
 */
public class BestiaryController extends BaseController implements Initializable {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(BestiaryController.class);

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TableView<Mob> tableBestiary;
    @FXML
    private TableColumn<Mob, String> columnName;
    @FXML
    private TableColumn<Mob, String> columnAuthor;
    @FXML
    private TableColumn<Mob, MobClass> columnMobClass;
    @FXML
    private TableColumn<Mob, Rule> columnRulesType;
    @FXML
    private TableColumn<Mob, Integer> columnViability;
    @FXML
    private TableColumn<Mob, ?> columnAction;

    // endregion

    private final IntegerProperty selectedRowIndex = new SimpleIntegerProperty(
        this, "selectedRowIndex");
    private final ObservableList<Mob> mobs;
    private final AdvancedDatabaseService<Mob> service;

    private final Translator translator;
    private String title;
    private ResourceBundle resources;

    // endregion

    // region Constructors

    public BestiaryController(Context context) {
        this.service = context.getService(Context.SERVICE_BESTIARY);
        this.translator = context.getTranslator();
        this.mobs = service.selectAll();
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        this.title = resources.getString(R.Translate.BESTIARY_TITLE);

        tableBestiary.setItems(mobs);
        selectedRowIndex.bind(tableBestiary.getSelectionModel().selectedIndexProperty());

        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        columnMobClass.setCellValueFactory(new PropertyValueFactory<>("mobClass"));
        columnMobClass.setCellFactory(
            TextFieldTableCell.forTableColumn(StringConvertors.forMobClass(translator)));
        columnRulesType.setCellValueFactory(new PropertyValueFactory<>("rulesType"));
        columnRulesType.setCellFactory(
            TextFieldTableCell.forTableColumn(StringConvertors.forRulesType(translator)));
        columnViability.setCellValueFactory(new PropertyValueFactory<>("viability"));

    }

    @Override
    protected void onResume() {
        setScreenSize(1000, 600);
        setTitle(title);
    }

    public void handleAddItem(ActionEvent actionEvent) {

    }

    public void handleRemoveItem(ActionEvent actionEvent) {

    }

    public void handleEditItem(ActionEvent actionEvent) {
    }

    public void handleSynchronize(ActionEvent actionEvent) {

    }
}
