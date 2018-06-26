package cz.stechy.drd.app.collections;

import cz.stechy.drd.app.InjectableChild;
import cz.stechy.drd.dao.ItemCollectionDao;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.ItemCollection;
import cz.stechy.drd.service.ItemResolver;
import cz.stechy.drd.service.ItemResolver.WithItemBase;
import cz.stechy.drd.service.OnlineItemRegistry;
import cz.stechy.drd.util.CellUtils;
import cz.stechy.drd.util.DialogUtils;
import cz.stechy.drd.util.DialogUtils.ChoiceEntry;
import cz.stechy.drd.util.Translator;
import cz.stechy.screens.BaseController;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectionsItemsController implements Initializable, InjectableChild,
    CollectionsControllerChild {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionsItemsController.class);

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TableView<ItemEntry> tableCollectionItems;
    @FXML
    private TableColumn<ItemEntry, Image> columnImage;
    @FXML
    private TableColumn<ItemEntry, Integer> columnWeight;
    @FXML
    private TableColumn<ItemEntry, Money> columnPrice;

    // endregion
    private final ObservableList<ItemEntry> collectionItems = FXCollections.observableArrayList();
    private final ObservableList<ChoiceEntry> itemRegistry = FXCollections.observableArrayList();
    private final ItemCollectionDao collectionService;
    private final ItemResolver itemResolver;
    private final Translator translator;

    private BaseController parent;
    private StringProperty selectedEntry;

    // endregion

    // region Constructors

    public CollectionsItemsController(ItemCollectionDao collectionService,
        ItemResolver itemResolver, Translator translator) {
        this.collectionService = collectionService;
        this.itemResolver = itemResolver;
        this.translator = translator;
        this.itemRegistry.setAll(DialogUtils.getItemRegistryChoices());
    }

    // endregion

    // region Private methods

    private ListChangeListener<? super String> itemCollectionContentListener = c -> {
        while (c.next()) {
            collectionItems.addAll(c.getAddedSubList().stream()
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableCollectionItems.setFixedCellSize(40);
        tableCollectionItems.setItems(collectionItems);
        columnImage.setCellFactory(param -> CellUtils.forImage());
        columnWeight.setCellFactory(param -> CellUtils.forWeight());
        columnPrice.setCellFactory(param -> CellUtils.forMoney());

        tableCollectionItems.getSelectionModel().selectedItemProperty()
            .addListener((observableValue, oldValue, newValue) -> {
                selectedEntry.setValue(newValue == null ? null : newValue.getId());
            });
    }

    @Override
    public void injectParent(BaseController parent) {
        this.parent = parent;
    }

    @Override
    public void setSelectedEntryProperty(StringProperty selectedEntry) {
        this.selectedEntry = selectedEntry;
    }

    @Override
    public void setSelectedCollection(ReadOnlyObjectProperty<ItemCollection> selectedCollection) {
        selectedCollection.addListener((observableValue, oldValue, newValue) -> {
            collectionItems.clear();
            if (oldValue != null) {
                oldValue.getRecords().removeListener(this.itemCollectionContentListener);
            }
            if (newValue == null) {
                return;
            }

            newValue.getRecords().addListener(this.itemCollectionContentListener);
            collectionItems.setAll(newValue.getRecords()
                .parallelStream()
                .map(ItemEntry::new)
                .collect(Collectors.toList()));
        });
    }

    @Override
    public void requestAddEntryToCollection(ItemCollection collection) {
        final Optional<ChoiceEntry> entryOptional = DialogUtils.selectItem(itemRegistry);
        entryOptional.ifPresent(choiceEntry -> {
            final String itemName = choiceEntry.getName();
            final String collectionName = collection.getName();
            collectionService.addItemToCollection(collection, choiceEntry.getId());
//                .exceptionally(throwable -> {
//                    parent.showNotification(new Notification(String.format(translator.translate(
//                        R.Translate.NOTIFY_COLLECTION_RECORD_IS_NOT_INSERTED), itemName,
//                        collectionName)));
//                    LOGGER.error("Položku se nepodařilo přidat do kolekce");
//                    throw new RuntimeException(throwable);
//                })
//                .thenAccept(ignored -> {
//                    parent.showNotification(new Notification(String.format(translator.translate(
//                        R.Translate.NOTIFY_COLLECTION_RECORD_IS_INSERTED), itemName,
//                        collectionName)));
//                });
        });
    }

    @Override
    public void requestRemoveSelectedEntryFromCollection(ItemCollection collection) {
        final String id = selectedEntry.get();
        collectionService.removeItemFromCollection(collection, id);
    }

    @Override
    public void mergeEntries() {
//        itemResolver.merge(collectionItems)
//            .exceptionally(throwable -> {
//                showNotification(new Notification(translator.translate(
//                    R.Translate.NOTIFY_ITEM_MERGE_FAILED)));
//                LOGGER.error("Položky se nepodařilo zmergovat");
//                throw new RuntimeException(throwable);
//            })
//            .thenAccept(merged ->
//                showNotification(new Notification(String.format(translator.translate(
//                    R.Translate.NOTIFY_MERGED_ITEMS), merged))));
    }

    public static final class ItemEntry implements WithItemBase {

        public final StringProperty name = new SimpleStringProperty(this, "name");
        public final IntegerProperty weight = new SimpleIntegerProperty(this, "weight");
        public final ObjectProperty<Money> price = new SimpleObjectProperty<>(this, "price");
        public final ObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");
        final ItemBase itemBase;

        public ItemEntry(String id) {
            final Optional<ItemBase> optionalItem = OnlineItemRegistry.getINSTANCE()
                .getItemById(id);
            this.itemBase = optionalItem.isPresent() ? optionalItem.get() : null;
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
