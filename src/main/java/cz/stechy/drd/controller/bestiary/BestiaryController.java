package cz.stechy.drd.controller.bestiary;

import cz.stechy.drd.R;
import cz.stechy.drd.model.Context;
import cz.stechy.drd.model.Rule;
import cz.stechy.drd.model.db.AdvancedDatabaseService;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.db.base.Firebase.OnDeleteItem;
import cz.stechy.drd.model.db.base.Firebase.OnDownloadItem;
import cz.stechy.drd.model.db.base.Firebase.OnUploadItem;
import cz.stechy.drd.model.entity.mob.Mob;
import cz.stechy.drd.model.entity.mob.Mob.MobClass;
import cz.stechy.drd.model.user.User;
import cz.stechy.drd.util.HashGenerator;
import cz.stechy.drd.util.StringConvertors;
import cz.stechy.drd.util.Translator;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
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

    private static final int NO_SELECTED_INDEX = -1;

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

    @FXML
    private Button btnAddItem;
    @FXML
    private Button btnRemoveItem;
    @FXML
    private Button btnEditItem;
    @FXML
    private Button btnSynchronize;
    @FXML
    private ToggleButton btnToggleOnline;

    // endregion

    private final IntegerProperty selectedRowIndex = new SimpleIntegerProperty(
        this, "selectedRowIndex");
    private final BooleanProperty showOnlineDatabase = new SimpleBooleanProperty(this,
        "showOnlineDatabase, false");
    private final User user;
    private final Translator translator;

    private AdvancedDatabaseService<Mob> service;
    private ObservableList<Mob> mobs;
    private String title;
    private ResourceBundle resources;

    // endregion

    // region Constructors

    public BestiaryController(Context context) {
        this.service = context.getService(Context.SERVICE_BESTIARY);
        this.user = context.getUserService().getUser().get();
        this.translator = context.getTranslator();
        this.mobs = service.selectAll();
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        this.title = resources.getString(R.Translate.BESTIARY_TITLE);

        tableBestiary.setItems(mobs);

        final BooleanBinding selectedRowBinding = selectedRowIndex.isEqualTo(NO_SELECTED_INDEX);
        selectedRowIndex.bind(tableBestiary.getSelectionModel().selectedIndexProperty());
        showOnlineDatabase.bindBidirectional(btnToggleOnline.selectedProperty());
        btnAddItem.disableProperty().bind(showOnlineDatabase);
        btnRemoveItem.disableProperty().bind(Bindings.or(
            selectedRowBinding,
            showOnlineDatabase));
        btnEditItem.disableProperty().bind(Bindings.or(
            selectedRowBinding,
            showOnlineDatabase));

        showOnlineDatabase.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }

            service.toggleDatabase(newValue);
        });

        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        columnMobClass.setCellValueFactory(new PropertyValueFactory<>("mobClass"));
        columnMobClass.setCellFactory(
            TextFieldTableCell.forTableColumn(StringConvertors.forMobClass(translator)));
        columnRulesType.setCellValueFactory(new PropertyValueFactory<>("rulesType"));
        columnRulesType.setCellFactory(
            TextFieldTableCell.forTableColumn(StringConvertors.forRulesType(translator)));
        columnViability.setCellValueFactory(new PropertyValueFactory<>("viability"));
        columnAction.setCellFactory(param -> BestiaryHelper
            .forActionButtons(uploadHandler, downloadHandler, deleteHandler, user, resources));
    }

    @Override
    protected void onResume() {
        setScreenSize(1000, 600);
        setTitle(title);
    }

    @Override
    protected void onScreenResult(int statusCode, int actionId, Bundle bundle) {
        Mob mob;
        switch (actionId) {
            case BestiaryHelper.MOB_ACTION_ADD:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }
                mob = BestiaryHelper.mobFromBundle(bundle);
                try {
                    mob.setAuthor(user.getName());
                    mob.setId(HashGenerator.createHash());
                    service.insert(mob);
                } catch (DatabaseException e) {
                    e.printStackTrace();
                    logger.warn("Nestvůru {} se nepodařilo vložit do databáze", mob.toString());
                }
                break;

            case BestiaryHelper.MOB_ACTION_UPDATE:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }

                mob = BestiaryHelper.mobFromBundle(bundle);
                try {
                    service.update(mob);
                } catch (DatabaseException e) {
                    logger.warn("Nestvůru {} se napodařilo aktualizovat", mob.toString());
                }
                break;
        }
    }

    // region Button handlers

    public void handleAddItem(ActionEvent actionEvent) {
        Bundle bundle = new Bundle();
        bundle.putInt(BestiaryHelper.MOB_ACTION, BestiaryHelper.MOB_ACTION_ADD);
        startNewDialogForResult(R.FXML.BESTIARY_EDIT, BestiaryHelper.MOB_ACTION_ADD, bundle);
    }

    public void handleRemoveItem(ActionEvent actionEvent) {
        final int rowIndex = selectedRowIndex.get();
        final Mob mob = mobs.get(rowIndex);
        final String name = mob.getName();
        try {
            service.delete(mob.getId());
        } catch (DatabaseException e) {
            logger.warn("Příšeru {} se nepodařilo odebrat z databáze", name);
        }
    }

    public void handleEditItem(ActionEvent actionEvent) {
        final Mob mob = mobs.get(selectedRowIndex.get());
        final Bundle bundle = BestiaryHelper.mobToBundle(mob);
        bundle.putInt(BestiaryHelper.MOB_ACTION, BestiaryHelper.MOB_ACTION_UPDATE);
        startNewDialogForResult(R.FXML.BESTIARY_EDIT, BestiaryHelper.MOB_ACTION_UPDATE, bundle);
    }

    public void handleSynchronize(ActionEvent actionEvent) {
        service.synchronize(user.getName());
    }

    // endregion

    private final OnUploadItem<Mob> uploadHandler = mob -> service.upload(mob);
    private final OnDownloadItem<Mob> downloadHandler = mob -> {
        try {
            service.insert(mob);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    };
    private final OnDeleteItem<Mob> deleteHandler = (mob, remote) -> service.deleteRemote(mob, true);
}
