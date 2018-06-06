package cz.stechy.drd.app.spellbook;

import cz.stechy.drd.R;
import cz.stechy.drd.R.Translate;
import cz.stechy.drd.dao.SpellBookDao;
import cz.stechy.drd.model.User;
import cz.stechy.drd.model.entity.mob.Mob;
import cz.stechy.drd.model.spell.Spell;
import cz.stechy.drd.model.spell.Spell.SpellProfessionType;
import cz.stechy.drd.service.UserService;
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
 * Kontroler knihy kouzel.
 */
public class SpellBookController extends BaseController implements Initializable {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(SpellBookController.class);

    private static final int NO_SELECTED_INDEX = -1;

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TableView<SpellEntry> tableSpellBook;
    @FXML
    private TableColumn<SpellEntry, Image> columnImage;
    @FXML
    private TableColumn<SpellEntry, String> columnName;
    @FXML
    private TableColumn<SpellEntry, String> columnMagicName;
    @FXML
    private TableColumn<SpellEntry, String> columnAuthor;
    @FXML
    private TableColumn<SpellEntry, SpellProfessionType> columnType;
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

    private final ObservableList<SpellEntry> spells = FXCollections.observableArrayList();
    private final SortedList<SpellEntry> sortedList = new SortedList<>(spells,
        Comparator.comparing(SpellEntry::getName));
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

    private final SpellBookDao spellBook;
    private String title;

    // endregion

    // region Constructors

    public SpellBookController(SpellBookDao spellBook, UserService userService,
        Translator translator) {
        this.spellBook = spellBook;
        this.user = userService.getUser();
        this.translator = translator;
        if (this.user != null) {
            userLogged.bind(this.user.loggedProperty());
        }
    }

    // endregion

    // region Private methods

    /**
     * Vrátí {@link Optional} obsahující vybranou nestvůru, nebo prázdnou hodnotu
     *
     * @return {@link Optional< Mob >}
     */
    private Optional<Spell> getSelectedEntry() {
        if (selectedRowIndex.getValue() == null || selectedRowIndex.get() < 0) {
            return Optional.empty();
        }

        return Optional.of(sortedList.get(selectedRowIndex.get()).getSpellBase());
    }

    private void insertSpell(Spell spell) {
        spellBook.insertAsync(spell)
            .exceptionally(throwable -> {
                showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_RECORD_IS_NOT_INSERTED), spell.getName())));
                LOGGER.warn("Kouzlo {} se nepodařilo vložit do databáze", spell.toString());
                throw new RuntimeException(throwable);
            })
            .thenAccept(spell1 -> {
                showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_RECORD_IS_INSERTED), spell1.getName())));
                sortedList.stream()
                    .filter(spellEntry -> spellEntry.getName().equals(spell1.getName()))
                    .findFirst()
                    .ifPresent(tableSpellBook.getSelectionModel()::select);
            });
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title = resources.getString(R.Translate.SPELL_BOOK_TITLE);

        tableSpellBook.setItems(sortedList);
        tableSpellBook.setFixedCellSize(SpellBookHelper.SPELL_ROW_HEIGHT);
        sortedList.comparatorProperty().bind(tableSpellBook.comparatorProperty());

        final BooleanBinding selectedRowBinding = selectedRowIndex.isEqualTo(NO_SELECTED_INDEX);
        selectedRowIndex.bind(tableSpellBook.getSelectionModel().selectedIndexProperty());
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

        selectedRowIndex.addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.intValue() < 0) {
                disableDownloadBtn.unbind();
                disableUploadBtn.unbind();
                disableRemoveOnlineBtn.unbind();

                disableDownloadBtn.set(true);
                disableUploadBtn.set(true);
                disableRemoveOnlineBtn.set(true);

                return;
            }

            final Spell entry = sortedList.get(newValue.intValue()).getSpellBase();
            final BooleanBinding authorBinding = Bindings.createBooleanBinding(() ->
                    (user != null) && entry.getAuthor().equals(user.getName()),
                entry.authorProperty());
            disableDownloadBtn.bind(entry.downloadedProperty());
            disableUploadBtn.bind(entry.uploadedProperty().or(authorBinding.not()));
            disableRemoveOnlineBtn.bind(authorBinding.not());
        });
        showOnlineDatabase.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }

            spellBook.toggleDatabase(newValue);
        });

        columnImage.setCellFactory(param -> CellUtils.forImage());
        columnType.setCellFactory(TextFieldTableCell.forTableColumn(translator.getConvertor(
            Key.SPELL_PROFESSION_TYPES)));

        spellBook.selectAllAsync()
            .thenAccept(
                spellList -> ObservableMergers.mergeList(SpellEntry::new, spells, spellList));
    }

    @Override
    protected void onResume() {
        setScreenSize(1000, 600);
        setTitle(title);
    }

    @Override
    protected void onScreenResult(int statusCode, int actionId, Bundle bundle) {
        Spell spell;
        switch (actionId) {
            case SpellBookHelper.SPELL_ACTION_ADD:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }
                spell = SpellBookHelper.fromBundle(bundle);
                spell.setAuthor((user != null) ? user.getName() : "");
                spell.setId(HashGenerator.createHash());
                insertSpell(spell);
                break;

            case SpellBookHelper.SPELL_ACTION_UPDATE:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }

                spell = SpellBookHelper.fromBundle(bundle);
                spellBook.updateAsync(spell)
                    .exceptionally(throwable -> {
                        showNotification(new Notification(String.format(translator.translate(
                            R.Translate.NOTIFY_RECORD_IS_NOT_UPDATED), spell.getName())));
                        LOGGER.error("Kouzlo {} se nepodařilo aktualizovat", spell.getName());
                        throw new RuntimeException(throwable);
                    })
                    .thenAccept(generalItem ->
                        showNotification(new Notification(String.format(translator.translate(
                            Translate.NOTIFY_RECORD_IS_NOT_UPDATED), spell.getName()))));
                break;
        }
    }

    @Override
    protected void onClose() {
        showOnlineDatabase.setValue(false);
    }

    // region Button handlers

    @FXML
    private void handleAddItem(ActionEvent actionEvent) {
        Bundle bundle = new Bundle();
        bundle.putInt(SpellBookHelper.SPELL_ACTION, SpellBookHelper.SPELL_ACTION_ADD);
        startNewDialogForResult(R.Fxml.SPELLBOOK_EDIT, SpellBookHelper.SPELL_ACTION_ADD, bundle);
    }

    @FXML
    private void handleRemoveItem(ActionEvent actionEvent) {
        final int rowIndex = selectedRowIndex.get();
        final SpellEntry entry = sortedList.get(rowIndex);
        spellBook.deleteAsync(entry.getSpellBase())
            .exceptionally(throwable -> {
                showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_RECORD_IS_NOT_DELETED), entry.getName())));
                LOGGER.warn("Kouzlo {} se nepodařilo odebrat z databáze", entry.getName());
                throw new RuntimeException(throwable);
            })
            .thenAccept(
                spell -> showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_RECORD_IS_DELETED), entry.getName()))));
    }

    @FXML
    private void handleEditItem(ActionEvent actionEvent) {
        final Spell entry = sortedList.get(selectedRowIndex.get()).getSpellBase();
        final Bundle bundle = new Bundle();
        bundle.putInt(SpellBookHelper.SPELL_ACTION, SpellBookHelper.SPELL_ACTION_UPDATE);
        SpellBookHelper.toBundle(bundle, entry);
        startNewDialogForResult(R.Fxml.SPELLBOOK_EDIT, SpellBookHelper.SPELL_ACTION_UPDATE, bundle);
    }

    @FXML
    private void handleUploadItem(ActionEvent actionEvent) {
        getSelectedEntry().ifPresent(spell -> {
            spellBook.uploadAsync(spell)
                .exceptionally(throwable -> {
                    showNotification(new Notification(String.format(translator.translate(
                        R.Translate.NOTIFY_RECORD_IS_NOT_UPLOADED), spell.getName())));
                    LOGGER.warn("Položku {} se nepodařilo nahrát", spell.toString());
                    throw new RuntimeException(throwable);
                })
                .thenAccept(ignored ->
                    showNotification(new Notification(String.format(translator.translate(
                        R.Translate.NOTIFY_RECORD_IS_UPLOADED), spell.getName()))));
        });
    }

    @FXML
    private void handleDownloadItem(ActionEvent actionEvent) {
        getSelectedEntry().ifPresent(this::insertSpell);
    }

    @FXML
    private void handleRemoveOnlineItem(ActionEvent actionEvent) {
        getSelectedEntry().ifPresent(spell -> {
            spellBook.deleteRemoteAsync(spell)
                .exceptionally(throwable -> {
                    showNotification(new Notification(String.format(translator.translate(
                        R.Translate.NOTIFY_RECORD_IS_NOT_DELETED_FROM_ONLINE_DATABASE),
                        spell.getName())));
                    LOGGER.error("Položku {} se nepodařilo odebrat z online databáze",
                        spell.getName());
                    throw new RuntimeException(throwable);
                })
                .thenAccept(ignored ->
                    showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_RECORD_IS_DELETED_FROM_ONLINE_DATABASE),
                    spell.getName()))));
        });
    }

    @FXML
    private void handleSynchronize(ActionEvent actionEvent) {
        spellBook.synchronize(user.getName())
            .thenAccept(total -> LOGGER.info("Bylo synchronizováno clekem: {} kouzel.", total));
    }

    // endregion

}
