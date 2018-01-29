package cz.stechy.drd.controller.spellbook;

import cz.stechy.drd.R;
import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.entity.mob.Mob;
import cz.stechy.drd.model.persistent.SpellBookService;
import cz.stechy.drd.model.persistent.UserService;
import cz.stechy.drd.model.spell.Spell;
import cz.stechy.drd.model.spell.SpellEntry;
import cz.stechy.drd.model.spell.SpellProfessionType;
import cz.stechy.drd.model.user.User;
import cz.stechy.drd.util.CellUtils;
import cz.stechy.drd.util.HashGenerator;
import cz.stechy.drd.util.ObservableMergers;
import cz.stechy.drd.util.Translator;
import cz.stechy.drd.util.Translator.Key;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
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
import javafx.concurrent.Task;
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
 *
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
//    @FXML
//    private TableColumn<SpellEntry, String> columnPrice;

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

    private final SpellBookService spellBook;
    private String title;

    // endregion

    // region Constructors

    public SpellBookController(SpellBookService spellBook, UserService userService, Translator translator) {
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

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ObservableMergers.mergeList(SpellEntry::new, spells, spellBook.selectAll());
                return null;
            }
        };
        ThreadPool.getInstance().submit(task);
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
                try {
                    spell.setAuthor((user != null) ? user.getName() : "");
                    spell.setId(HashGenerator.createHash());
                    spellBook.insert(spell);
                    sortedList.stream()
                        .filter(spellEntry -> spellEntry.getName().equals(spell.getName()))
                        .findFirst()
                        .ifPresent(tableSpellBook.getSelectionModel()::select);

                } catch (DatabaseException e) {
                    e.printStackTrace();
                    LOGGER.warn("Kouzlo {} se nepodařilo vložit do databáze", spell.toString());
                }
                break;

            case SpellBookHelper.SPELL_ACTION_UPDATE:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }

                spell = SpellBookHelper.fromBundle(bundle);
                try {
                    spellBook.update(spell);
                } catch (DatabaseException e) {
                    LOGGER.warn("Kouzlo {} se napodařilo aktualizovat", spell.toString());
                }
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
        startNewDialogForResult(R.FXML.SPELLBOOK_EDIT, SpellBookHelper.SPELL_ACTION_ADD, bundle);
    }

    @FXML
    private void handleRemoveItem(ActionEvent actionEvent) {
        final int rowIndex = selectedRowIndex.get();
        final SpellEntry entry = sortedList.get(rowIndex);
        final String name = entry.getName();
        try {
            spellBook.delete(entry.getSpellBase().getId());
        } catch (DatabaseException e) {
            LOGGER.warn("Příšeru {} se nepodařilo odebrat z databáze", name);
        }
    }

    @FXML
    private void handleEditItem(ActionEvent actionEvent) {
        final Spell entry = sortedList.get(selectedRowIndex.get()).getSpellBase();
        final Bundle bundle = new Bundle();
        bundle.putInt(SpellBookHelper.SPELL_ACTION, SpellBookHelper.SPELL_ACTION_UPDATE);
        SpellBookHelper.toBundle(bundle, entry);
        startNewDialogForResult(R.FXML.SPELLBOOK_EDIT, SpellBookHelper.SPELL_ACTION_UPDATE, bundle);
    }

    @FXML
    private void handleUploadItem(ActionEvent actionEvent) {
        getSelectedEntry().ifPresent(spellBook::upload);
    }

    @FXML
    private void handleDownloadItem(ActionEvent actionEvent) {
        getSelectedEntry().ifPresent(spell -> {
            try {
                spellBook.insert(spell);
            } catch (DatabaseException e) {
                LOGGER.error(e.getMessage());
            }
        });
    }

    @FXML
    private void handleRemoveOnlineItem(ActionEvent actionEvent) {
        getSelectedEntry().ifPresent(spell -> spellBook.deleteRemote(spell, true));
    }

    @FXML
    private void handleSynchronize(ActionEvent actionEvent) {
        spellBook.synchronize(user.getName(), total ->
            LOGGER.info("Bylo synchronizováno clekem: {} kouzel.", total));
    }

    // endregion

}
