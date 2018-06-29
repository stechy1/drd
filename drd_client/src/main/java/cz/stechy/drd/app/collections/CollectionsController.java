package cz.stechy.drd.app.collections;

import cz.stechy.drd.R;
import cz.stechy.drd.dao.ItemCollectionDao;
import cz.stechy.drd.model.User;
import cz.stechy.drd.model.item.ItemCollection;
import cz.stechy.drd.service.UserService;
import cz.stechy.drd.util.DialogUtils.ChoiceEntry;
import cz.stechy.drd.util.ObservableMergers;
import cz.stechy.drd.util.Translator;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Notification;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectionsController extends BaseController implements Initializable {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionsController.class);

    private static final int NO_SELECTED_INDEX = -1;

    // endregion

    // region Variables

    // region FXML

    @FXML
    private Accordion accordionCollectionsContentType;

    @FXML
    private CollectionsItemsController tableCollectionsItemsController;
    @FXML
    private CollectionsBestiaryController tableCollectionsBestiaryController;
    @FXML
    private CollectionsSpellsController tableCollectionsSpellsController;

    @FXML
    private TableView tableCollectionsItems;
    @FXML
    private TableView tableCollectionsBestiary;
    @FXML
    private TableView tableCollectionsSpells;

    @FXML
    private ListView<ItemCollection> lvCollections;
    @FXML
    private Button btnCollectionAdd;
    @FXML
    private Button btnCollectionRemove;
    @FXML
    private Button btnCollectionDownload;
    @FXML
    private TextField txtCollectionName;
    @FXML
    private Button btnCollectionItemAdd;
    @FXML
    private Button btnCollectionItemRemove;

    // endregion

    private final ObservableList<ItemCollection> collections = FXCollections.observableArrayList();
    private final List<String> translatedItemType = new ArrayList<>(
        Arrays.asList("Předměty", "Nestvůry", "Kouzla"));

    private final ObjectProperty<ItemCollection> selectedCollection = new SimpleObjectProperty<>(
        this, "selectedCollection", null);
    private final StringProperty selectedEntry = new SimpleStringProperty(this,
        "selectedEntry", null);
    private final IntegerProperty selectedAccordionPaneIndex = new SimpleIntegerProperty(this,
        "selectedAccordionPaneIndex", NO_SELECTED_INDEX);

    private final User user;
    private final Translator translator;
    private final ItemCollectionDao collectionService;

    private CollectionsControllerChild[] controllers;
    private String title;
    // endregion

    // region Constructors

    public CollectionsController(UserService userService, Translator translator,
        ItemCollectionDao collectionService) {
        this.user = userService.getUser();
        this.translator = translator;
        this.collectionService = collectionService;
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title = resources.getString(R.Translate.COLLECTIONS_TITLE);

        this.controllers = new CollectionsControllerChild[]{
            tableCollectionsItemsController,
            tableCollectionsBestiaryController,
            tableCollectionsSpellsController
        };

        accordionCollectionsContentType.expandedPaneProperty()
            .addListener((observable, oldValue, newValue) -> {
                selectedEntry.setValue(null);

                if (newValue == null) {
                    selectedAccordionPaneIndex.setValue(NO_SELECTED_INDEX);
                    return;
                }

                selectedAccordionPaneIndex.setValue(translatedItemType.indexOf(newValue.getText()));
            });

        lvCollections.setItems(collections);
        lvCollections.setCellFactory(param -> new ItemCollectionCell());
        final BooleanBinding loggedBinding = Bindings.createBooleanBinding(() -> user != null);
        btnCollectionAdd.disableProperty()
            .bind(loggedBinding.not().or(txtCollectionName.textProperty().isEmpty()));
        final BooleanBinding noSelectedCollection = selectedCollection.isNull();
        final BooleanBinding authorBinding = Bindings.createBooleanBinding(() -> {
            final ItemCollection collection = selectedCollection.get();
            if (collection == null || user == null) {
                return false;
            }

            return collection.getAuthor().equals(user.getName());
        }, selectedCollection);
        btnCollectionDownload.disableProperty().bind(loggedBinding.not()
            .or(noSelectedCollection));
        btnCollectionRemove.disableProperty().bind(authorBinding.not()
            .or(noSelectedCollection));

        btnCollectionItemAdd.disableProperty().bind(authorBinding.not()
                .or(noSelectedCollection)
                .or(selectedAccordionPaneIndex.isEqualTo(NO_SELECTED_INDEX)));
        btnCollectionItemRemove.disableProperty().bind(authorBinding.not()
                .or(noSelectedCollection)
            .or(selectedEntry.isNull()));

        selectedCollection.bind(lvCollections.getSelectionModel().selectedItemProperty());

        for (CollectionsControllerChild controller : controllers) {
            controller
                .setSelectedCollection(lvCollections.getSelectionModel().selectedItemProperty());
            controller.setSelectedEntryProperty(selectedEntry);
            controller.setNotificationProvider(this::showNotification);
        }

        ObservableMergers.mergeList(collections, collectionService.getCollections());
    }

    @Override
    protected void onResume() {
        setTitle(title);
        setScreenSize(700, 300);
    }

    // region Button handlers

    @FXML
    private void handleCollectionAdd(ActionEvent actionEvent) {
        ItemCollection collection = new ItemCollection.Builder()
            .name(txtCollectionName.getText())
            .author(user != null ? user.getName() : "")
            .build();
        collectionService.uploadAsync(collection)
            .exceptionally(throwable -> {
                showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_RECORD_IS_NOT_UPLOADED), collection.getName())));
                LOGGER.error("Položku {} se nepodařilo nahrát do online databáze",
                    collection.getName());
                throw new RuntimeException(throwable);
            }).thenAccept(ignored -> {
            showNotification(new Notification(String.format(translator.translate(
                R.Translate.NOTIFY_RECORD_IS_UPLOADED), collection.getName())));
            txtCollectionName.clear();
        });
    }

    @FXML
    private void handleCollectionRemove(ActionEvent actionEvent) {
        final ItemCollection collection = selectedCollection.get();
        collectionService.deleteRemoteAsync(collection)
            .exceptionally(throwable -> {
                showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_RECORD_IS_NOT_DELETED_FROM_ONLINE_DATABASE),
                    collection.getName())));
                LOGGER.error("Položku {} se nepodařilo odstranit z online databáze",
                    collection.getName());
                throw new RuntimeException(throwable);
            }).thenAccept(ignored -> {
            showNotification(new Notification(String.format(translator.translate(
                R.Translate.NOTIFY_RECORD_IS_DELETED_FROM_ONLINE_DATABASE),
                collection.getName())));
            lvCollections.getSelectionModel().clearSelection();
        });
    }

    @FXML
    private void handleCollectionDownload(ActionEvent actionEvent) {
        for (CollectionsControllerChild controller : controllers) {
            controller.mergeEntries();
        }
    }

    @FXML
    private void handleCollectionItemAdd(ActionEvent actionEvent) {
        final CollectionsControllerChild controller = controllers[selectedAccordionPaneIndex.get()];
        final Optional<ChoiceEntry> entryOptional = controller.getSelectedEntry();
        final ItemCollection collection = selectedCollection.get();
        entryOptional.ifPresent(choiceEntry -> {
            final String itemName = choiceEntry.getName();
            final String collectionName = collection.getName();
            collectionService.addItemToCollection(collection, controller.getCollectionType(),
                choiceEntry.getId())
                .exceptionally(throwable -> {
                    showNotification(new Notification(String.format(translator.translate(
                        R.Translate.NOTIFY_COLLECTION_RECORD_IS_NOT_INSERTED), itemName,
                        collectionName)));
                    LOGGER.error("Položku se nepodařilo přidat do kolekce");
                    throw new RuntimeException(throwable);
                })
                .thenAccept(ignored -> showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_COLLECTION_RECORD_IS_INSERTED), itemName,
                    collectionName))));
        });
    }

    @FXML
    private void handleCollectionItemRemove(ActionEvent actionEvent) {
        final CollectionsControllerChild controller = controllers[selectedAccordionPaneIndex.get()];
        final ItemCollection collection = selectedCollection.get();
        final String id = selectedEntry.get();
        final String collectionName = collection.getName();

        collectionService
            .removeItemFromCollection(collection, controller.getCollectionType(), id)
            .exceptionally(throwable -> {
                showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_COLLECTION_RECORD_IS_NOT_DELETED), collectionName)));
                LOGGER.error("Položku se nepodařilo odebrat z kolekce");
                throw new RuntimeException(throwable);
            })
            .thenAccept(ignored -> showNotification(new Notification(String.format(translator.translate(
                R.Translate.NOTIFY_COLLECTION_RECORD_IS_DELETED), collectionName))));

    }

    // endregion
}
