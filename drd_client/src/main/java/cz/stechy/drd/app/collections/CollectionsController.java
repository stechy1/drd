package cz.stechy.drd.app.collections;

import cz.stechy.drd.R;
import cz.stechy.drd.dao.ItemCollectionDao;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.User;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.ItemCollection;
import cz.stechy.drd.service.ItemResolver;
import cz.stechy.drd.service.ItemResolver.WithItemBase;
import cz.stechy.drd.service.OnlineItemRegistry;
import cz.stechy.drd.service.UserService;
import cz.stechy.drd.util.CellUtils;
import cz.stechy.drd.util.DialogUtils;
import cz.stechy.drd.util.DialogUtils.ChoiceEntry;
import cz.stechy.drd.util.ObservableMergers;
import cz.stechy.drd.util.Translator;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Notification;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectionsController extends BaseController implements Initializable {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionsController.class);

    // endregion

    // region Variables

    // region FXML

    @FXML
    private ListView<ItemCollection> lvCollections;
    @FXML
    private TableView<ItemEntry> tableCollectionItems;
    @FXML
    private TableColumn<ItemEntry, Image> columnImage;
    @FXML
    private TableColumn<ItemEntry, Integer> columnWeight;
    @FXML
    private TableColumn<ItemEntry, Money> columnPrice;
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
    private final ObservableList<ItemEntry> collectionItems = FXCollections.observableArrayList();
    private final ObservableList<ChoiceEntry> itemRegistry = FXCollections.observableArrayList();
    private final ObjectProperty<ItemCollection> selectedCollection = new SimpleObjectProperty<>(
        this, "selectedCollection", null);
    private final ObjectProperty<ItemEntry> selectedCollectionItem = new SimpleObjectProperty<>(
        this, "selectedCollectionItem", null);
    private final ObjectProperty<ObservableList<String>> collectionContent = new SimpleObjectProperty<>(
        this, "collectionContent", null);

    private final ItemCollectionDao collectionService;
    private final ItemResolver itemResolver;
    private final User user;
    private final Translator translator;

    private String title;
    // endregion

    // region Constructors

    public CollectionsController(ItemCollectionDao collectionService, ItemResolver itemResolver,
        UserService userService, Translator translator) {
        this.collectionService = collectionService;
        this.itemResolver = itemResolver;
        this.user = userService.getUser();
        this.translator = translator;
        itemRegistry.setAll(DialogUtils.getItemRegistryChoices());
    }

    // endregion

    // region Private methods

    private void collectionContentListener(
        ObservableValue<? extends ObservableList<String>> observableValue,
        ObservableList<String> oldValue, ObservableList<String> newValue) {
        collectionItems.clear();
        if (oldValue != null) {
            oldValue.removeListener(this.itemCollectionContentListener);
        }
        if (newValue == null) {
            return;
        }

        collectionItems.setAll(collectionContent.get().parallelStream()
            .map(this::stringToItemMap)
            // TODO nezahazovat neznámý předmět, raději informovat uživatele o nevalidním záznamu
            .filter(itemBase -> itemBase != null)
            .map(ItemEntry::new)
            .collect(Collectors.toList()));
        newValue.addListener(this.itemCollectionContentListener);
    }

    private ItemBase stringToItemMap(String s) {
        final Optional<ItemBase> optionalItem = OnlineItemRegistry.getINSTANCE().getItemById(s);
        if (optionalItem.isPresent()) {
            return optionalItem.get();
        }

        return null;
    }

    // region Method handlers

    private ListChangeListener<? super String> itemCollectionContentListener = c -> {
        while (c.next()) {
            collectionItems.addAll(c.getAddedSubList().stream()
                .map(this::stringToItemMap)
                .map(ItemEntry::new)
                .collect(Collectors.toList()));
            c.getRemoved()
                .forEach(o -> collectionItems.stream()
                    .filter(itemEntry -> o.equals(itemEntry.getId()))
                    .findFirst()
                    .ifPresent(collectionItems::remove));
        }
    };

    // endregion

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title = resources.getString(R.Translate.COLLECTIONS_TITLE);
        lvCollections.setItems(collections);
        lvCollections.setCellFactory(param -> new ItemCollectionCell());
        tableCollectionItems.setFixedCellSize(40);
        tableCollectionItems.setItems(collectionItems);
        columnImage.setCellFactory(param -> CellUtils.forImage());
        columnWeight.setCellFactory(param -> CellUtils.forWeight());
        columnPrice.setCellFactory(param -> CellUtils.forMoney());

        final BooleanBinding loggedBinding = Bindings.createBooleanBinding(() -> user != null);
        btnCollectionAdd.disableProperty()
            .bind(loggedBinding.not().or(txtCollectionName.textProperty().isEmpty()));
        final BooleanBinding selectedBinding = selectedCollection.isNull();
        final BooleanBinding authorBinding = Bindings.createBooleanBinding(() -> {
            final ItemCollection collection = selectedCollection.get();
            if (collection == null || user == null) {
                return false;
            }

            return collection.getAuthor().equals(user.getName());
        }, selectedCollection);
        btnCollectionDownload.disableProperty().bind(loggedBinding.not().or(selectedBinding));
        btnCollectionRemove.disableProperty().bind(authorBinding.not().or(selectedBinding));

        selectedCollectionItem
            .bind(tableCollectionItems.getSelectionModel().selectedItemProperty());

        btnCollectionItemRemove.disableProperty()
            .bind(selectedCollectionItem.isNull().or(authorBinding.not()));
        btnCollectionItemAdd.disableProperty().bind(selectedBinding.or(authorBinding.not()));

        collectionContent.addListener(this::collectionContentListener);
        selectedCollection.bind(lvCollections.getSelectionModel().selectedItemProperty());
        collectionContent.bind(Bindings.createObjectBinding(() -> {
            final ItemCollection collection = selectedCollection.get();
            if (collection == null) {
                return null;
            }

            return collection.getRecords();
        }, selectedCollection));

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
        itemResolver.merge(collectionItems)
            .exceptionally(throwable -> {
                showNotification(new Notification(translator.translate(
                    R.Translate.NOTIFY_ITEM_MERGE_FAILED)));
                LOGGER.error("Položky se nepodařilo zmergovat");
                throw new RuntimeException(throwable);
            })
            .thenAccept(merged ->
                showNotification(new Notification(String.format(translator.translate(
                    R.Translate.NOTIFY_MERGED_ITEMS), merged))));
    }

    @FXML
    private void handleCollectionItemAdd(ActionEvent actionEvent) {
        final Optional<ChoiceEntry> entryOptional = DialogUtils.selectItem(itemRegistry);
        entryOptional.ifPresent(choiceEntry -> {
                final String itemName = choiceEntry.getName();
                final String collectionName = selectedCollection.get().getName();
            collectionService.addItemToCollection(selectedCollection.get(), choiceEntry.getId())
                .exceptionally(throwable -> {
                    showNotification(new Notification(String.format(translator.translate(
                        R.Translate.NOTIFY_COLLECTION_RECORD_IS_NOT_INSERTED), itemName,
                        collectionName)));
                    LOGGER.error("Položku se nepodařilo přidat do kolekce");
                    throw new RuntimeException(throwable);
                })
                .thenAccept(ignored -> {
                    showNotification(new Notification(String.format(translator.translate(
                        R.Translate.NOTIFY_COLLECTION_RECORD_IS_INSERTED), itemName,
                        collectionName)));
                });
        });
    }

    @FXML
    private void handleCollectionItemRemove(ActionEvent actionEvent) {
        final ItemCollection collection = selectedCollection.get();
        final ItemEntry itemEntry = selectedCollectionItem.get();

        if (collection == null || itemEntry == null) {
            return;
        }

        final String itemName = itemEntry.getName();
        final String collectionName = collection.getName();

        collectionService.removeItemFromCollection(collection, itemEntry.getId())
            .exceptionally(throwable -> {
                    showNotification(new Notification(String.format(translator.translate(
                        R.Translate.NOTIFY_COLLECTION_RECORD_IS_NOT_DELETED), itemName,
                        collectionName)));
                    LOGGER.error("Položku se nepodařilo odebrat z kolekce");
                throw new RuntimeException(throwable);
            })
            .thenAccept(ignored -> {
                    showNotification(new Notification(String.format(translator.translate(
                        R.Translate.NOTIFY_COLLECTION_RECORD_IS_DELETED), itemName,
                        collectionName)));
            });
    }

    // endregion

    public static final class ItemEntry implements WithItemBase {

        public final StringProperty name = new SimpleStringProperty(this, "name");
        public final IntegerProperty weight = new SimpleIntegerProperty(this, "weight");
        public final ObjectProperty<Money> price = new SimpleObjectProperty<>(this, "price");
        public final ObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");
        final ItemBase itemBase;

        public ItemEntry(ItemBase itemBase) {
            this.itemBase = itemBase;
            setName(itemBase.getName());
            setWeight(itemBase.getWeight());
            setPrice(itemBase.getPrice());
            ByteArrayInputStream bais = new ByteArrayInputStream(itemBase.getImage());
            setImage(new Image(bais));
        }

        public String getId() {
            return itemBase.getId();
        }

        public String getName() {
            return name.get();
        }

        public StringProperty nameProperty() {
            return name;
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public int getWeight() {
            return weight.get();
        }

        public IntegerProperty weightProperty() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight.set(weight);
        }

        public Money getPrice() {
            return price.get();
        }

        public ObjectProperty<Money> priceProperty() {
            return price;
        }

        public void setPrice(Money price) {
            this.price.set(price);
        }

        public Image getImage() {
            return image.get();
        }

        public ObjectProperty<Image> imageProperty() {
            return image;
        }

        public void setImage(Image image) {
            this.image.set(image);
        }

        public ItemBase getItemBase() {
            return itemBase;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ItemEntry itemEntry = (ItemEntry) o;

            return itemBase.equals(itemEntry.itemBase);
        }

        @Override
        public int hashCode() {
            return itemBase.hashCode();
        }
    }
}
