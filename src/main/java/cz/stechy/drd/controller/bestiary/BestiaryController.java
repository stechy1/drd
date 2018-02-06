package cz.stechy.drd.controller.bestiary;

import cz.stechy.drd.R;
import cz.stechy.drd.R.Translate;
import cz.stechy.drd.model.Rule;
import cz.stechy.drd.model.bestiary.MobEntry;
import cz.stechy.drd.model.db.AdvancedDatabaseService;
import cz.stechy.drd.model.entity.mob.Mob;
import cz.stechy.drd.model.entity.mob.Mob.MobClass;
import cz.stechy.drd.model.persistent.BestiaryService;
import cz.stechy.drd.model.persistent.UserService;
import cz.stechy.drd.model.user.User;
import cz.stechy.drd.util.CellUtils;
import cz.stechy.drd.util.HashGenerator;
import cz.stechy.drd.util.ObservableMergers;
import cz.stechy.drd.util.Translator;
import cz.stechy.drd.util.Translator.Key;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import cz.stechy.screens.Notification;
import java.net.URL;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kontroler pro správu jednotlivých nestvůr ve hře
 */
public class BestiaryController extends BaseController implements Initializable {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(BestiaryController.class);

    private static final int NO_SELECTED_INDEX = -1;

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TableView<MobEntry> tableBestiary;
    @FXML
    private TableColumn<MobEntry, Image> columnImage;
    @FXML
    private TableColumn<MobEntry, String> columnName;
    @FXML
    private TableColumn<MobEntry, String> columnAuthor;
    @FXML
    private TableColumn<MobEntry, MobClass> columnMobClass;
    @FXML
    private TableColumn<MobEntry, Rule> columnRulesType;
    @FXML
    private TableColumn<MobEntry, Integer> columnViability;

    @FXML
    private Button btnAddItem;
    @FXML
    private Button btnRemoveItem;
    @FXML
    private Button btnEditItem;
    @FXML
    private Button btnUploadItem;
    @FXML
    private Button btnDownloadItem;
    @FXML
    private Button btnRemoveOnlineItem;
    @FXML
    private Button btnSynchronize;
    @FXML
    private ToggleButton btnToggleOnline;

    // endregion

    private final ObservableList<MobEntry> mobs = FXCollections.observableArrayList();
    private final SortedList<MobEntry> sortedList = new SortedList<>(mobs,
        Comparator.comparing(MobEntry::getName));
    private final IntegerProperty selectedRowIndex = new SimpleIntegerProperty(
        this, "selectedRowIndex");
    private final BooleanProperty userLogged = new SimpleBooleanProperty(this,
        "userLogged", false);
    private final BooleanProperty showOnlineDatabase = new SimpleBooleanProperty(this,
        "showOnlineDatabase", false);
    private final BooleanProperty disableDownloadBtn = new SimpleBooleanProperty(this,
        "disableDownloadBtn", true);
    private final BooleanProperty disableUploadBtn = new SimpleBooleanProperty(this,
        "disableUploadBtn", true);
    private final BooleanProperty disableRemoveOnlineBtn = new SimpleBooleanProperty(this,
        "disableRemoveOnlineBtn", true);
    private final User user;
    private final Translator translator;

    private AdvancedDatabaseService<Mob> service;

    private String title;

    // endregion

    // region Constructors

    public BestiaryController(UserService userService, BestiaryService bestiaryService,
        Translator translator) {
        this.service = bestiaryService;
        this.translator = translator;
        this.user = userService.getUser();
        if (this.user != null) {
            userLogged.bind(this.user.loggedProperty());
        }
    }

    // endregion

    // region Private methods

    /**
     * Vrátí {@link Optional} obsahující vybranou nestvůru, nebo prázdnou hodnotu
     *
     * @return {@link Optional<Mob>}
     */
    private Optional<Mob> getSelectedEntry() {
        if (selectedRowIndex.getValue() == null || selectedRowIndex.get() < 0) {
            return Optional.empty();
        }

        return Optional.of(sortedList.get(selectedRowIndex.get()).getMobBase());
    }

    private void selectedRowListener(ObservableValue<? extends Number> observable, Number oldValue,
        Number newValue) {
        if (newValue == null || newValue.intValue() < 0) {
            disableDownloadBtn.unbind();
            disableUploadBtn.unbind();
            disableRemoveOnlineBtn.unbind();

            disableDownloadBtn.set(true);
            disableUploadBtn.set(true);
            disableRemoveOnlineBtn.set(true);

            return;
        }

        final MobEntry entry = sortedList.get(newValue.intValue());
        final BooleanBinding authorBinding = Bindings.createBooleanBinding(() ->
                (user != null) && entry.getAuthor().equals(user.getName()),
            entry.authorProperty());
        disableDownloadBtn.bind(entry.downloadedProperty());
        disableUploadBtn.bind(entry.uploadedProperty().or(authorBinding.not()));
        disableRemoveOnlineBtn.bind(authorBinding.not());
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title = resources.getString(R.Translate.BESTIARY_TITLE);

        tableBestiary.setItems(sortedList);
        tableBestiary.setFixedCellSize(BestiaryHelper.MOB_ROW_HEIGHT);
        sortedList.comparatorProperty().bind(tableBestiary.comparatorProperty());

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
        btnDownloadItem.disableProperty().bind(
            userLogged.not().or(
                disableDownloadBtn.or(
                    showOnlineDatabase.not())));
        btnUploadItem.disableProperty().bind(
            userLogged.not().or(
                disableUploadBtn.or(
                    showOnlineDatabase)));
        btnRemoveOnlineItem.disableProperty().bind(
            userLogged.not().or(
                disableRemoveOnlineBtn.or(
                    showOnlineDatabase.not())));

        btnSynchronize.disableProperty().bind(userLogged.not());

        selectedRowIndex.addListener(this::selectedRowListener);
        showOnlineDatabase.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }

            service.toggleDatabase(newValue);
        });

        columnImage.setCellFactory(param -> CellUtils.forImage());
        columnMobClass.setCellFactory(
            TextFieldTableCell.forTableColumn(translator.getConvertor(Key.MOB_CLASSES)));
        columnRulesType.setCellFactory(
            TextFieldTableCell.forTableColumn(translator.getConvertor(Key.RULES)));

        service.selectAllAsync()
            .thenAccept(m -> ObservableMergers.mergeList(MobEntry::new, mobs, m));
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
                mob.setAuthor((user != null) ? user.getName() : "");
                mob.setId(HashGenerator.createHash());
                service.insertAsync(mob)
                    .exceptionally(throwable -> {
                        throwable.printStackTrace();
                        throw new RuntimeException(throwable);
                    })
                    .thenAccept(m -> {
                        sortedList.stream()
                            .filter(mobEntry -> mobEntry.getName().equals(m.getName()))
                            .findFirst()
                            .ifPresent(tableBestiary.getSelectionModel()::select);
                        showNotification(new Notification(String.format(
                            translator.translate(R.Translate.NOTIFY_RECORD_IS_INSERTED),
                            m.getName())));
                    });
                break;

            case BestiaryHelper.MOB_ACTION_UPDATE:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }

                mob = BestiaryHelper.mobFromBundle(bundle);
                service.updateAsync(mob)
                    .exceptionally(throwable -> {
                        LOGGER.warn("Nestvůru {} se nepodařilo aktualizovat", mob.getName());
                        throw new RuntimeException(throwable);
                    })
                    .thenAccept(m -> showNotification(new Notification(String
                        .format(translator.translate(R.Translate.NOTIFY_RECORD_IS_UPDATED),
                            m.getName()))));
                break;
        }
    }

    // region Button handlers

    @FXML
    private void handleAddItem(ActionEvent actionEvent) {
        Bundle bundle = new Bundle();
        bundle.putInt(BestiaryHelper.MOB_ACTION, BestiaryHelper.MOB_ACTION_ADD);
        startNewDialogForResult(R.FXML.BESTIARY_EDIT, BestiaryHelper.MOB_ACTION_ADD, bundle);
    }

    @FXML
    private void handleRemoveItem(ActionEvent actionEvent) {
        final int rowIndex = selectedRowIndex.get();
        final MobEntry entry = sortedList.get(rowIndex);
        final String name = entry.getName();
        service.deleteAsync(entry.getMobBase())
            .exceptionally(throwable -> {
                LOGGER.error("Příšeru {} se nepodařilo odebrat z databáze", name, throwable);
                throw new RuntimeException(throwable);
            })
            .thenAccept(m -> showNotification(new Notification(String
                .format(translator.translate(R.Translate.NOTIFY_RECORD_IS_DELETED), m.getName()))));
    }

    @FXML
    private void handleEditItem(ActionEvent actionEvent) {
        final MobEntry entry = sortedList.get(selectedRowIndex.get());
        final Bundle bundle = BestiaryHelper.mobToBundle(entry.getMobBase());
        bundle.putInt(BestiaryHelper.MOB_ACTION, BestiaryHelper.MOB_ACTION_UPDATE);
        startNewDialogForResult(R.FXML.BESTIARY_EDIT, BestiaryHelper.MOB_ACTION_UPDATE, bundle);
    }

    @FXML
    private void handleUploadItem(ActionEvent actionEvent) {
        getSelectedEntry().ifPresent(mob ->
            service.uploadAsync(mob, (error, ref) -> {
                if (error != null) {
                    LOGGER.error("Položku {} se nepodařilo nahrát", mob);
                } else {
                    showNotification(new Notification(String
                        .format(translator.translate(Translate.NOTIFY_RECORD_IS_UPLOADED),
                            mob.getName())));
                }
            }));
    }

    @FXML
    private void handleDownloadItem(ActionEvent actionEvent) {
        getSelectedEntry().ifPresent(item -> {
            service.insertAsync(item)
                .exceptionally(throwable -> {
                    LOGGER.error(throwable.getMessage(), throwable);
                    throw new RuntimeException(throwable);
                });
        });
    }

    @FXML
    private void handleRemoveOnlineItem(ActionEvent actionEvent) {
        getSelectedEntry().ifPresent(mob ->
            service.deleteRemoteAsync(mob, true, (error, ref) -> {
                if (error != null) {
                    LOGGER.error("Položku {} se nepodařilo odstranit z online databáze", mob);
                } else {
                    showNotification(new Notification(String.format(
                        translator
                            .translate(Translate.NOTIFY_RECORD_IS_DELETED_FROM_ONLINE_DATABASE),
                        mob.getName())));
                }
            }));
    }

    @FXML
    private void handleSynchronize(ActionEvent actionEvent) {
        service.synchronize(user.getName(), total -> {
            LOGGER.info("Bylo synchronizováno celkem: " + total + " nestvůr.");
        });
    }

    // endregion
}
