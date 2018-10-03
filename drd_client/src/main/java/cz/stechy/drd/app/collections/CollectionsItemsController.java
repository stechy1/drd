package cz.stechy.drd.app.collections;

import cz.stechy.drd.R;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.OnlineCollection;
import cz.stechy.drd.model.item.OnlineCollection.CollectionType;
import cz.stechy.drd.service.item.ItemRegistry;
import cz.stechy.drd.service.online_item.OnlineItemRegistry;
import cz.stechy.drd.util.CellUtils;
import cz.stechy.drd.util.DialogUtils;
import cz.stechy.drd.util.DialogUtils.ChoiceEntry;
import cz.stechy.drd.service.translator.TranslatorService;
import cz.stechy.screens.Notification;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;
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

public class CollectionsItemsController implements Initializable, CollectionsControllerChild {

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
    private final ObservableList<ChoiceEntry> choiceEntries = FXCollections.observableArrayList();
    private final ItemRegistry itemRegistry;
    private final TranslatorService translator;

    private StringProperty selectedEntry;
    private CollectionsNotificationProvider notificationProvider;

    // endregion

    // region Constructors

    public CollectionsItemsController(ItemRegistry itemRegistry, TranslatorService translator) {
        this.itemRegistry = itemRegistry;
        this.translator = translator;

        this.choiceEntries.setAll(DialogUtils.getItemChoices(itemRegistry.getRegistry().values()));
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
            .addListener((observableValue, oldValue, newValue) -> selectedEntry.setValue(newValue == null ? null : newValue.getId()));
    }

    @Override
    public void setSelectedEntryProperty(StringProperty selectedEntry) {
        this.selectedEntry = selectedEntry;
    }

    @Override
    public void setSelectedCollection(ReadOnlyObjectProperty<OnlineCollection> selectedCollection) {
        selectedCollection.addListener((observableValue, oldValue, newValue) -> {
            collectionItems.clear();
            if (oldValue != null) {
                oldValue.getCollection(CollectionType.ITEMS).removeListener(this.itemCollectionContentListener);
            }
            if (newValue == null) {
                return;
            }

            newValue.getCollection(CollectionType.ITEMS).addListener(this.itemCollectionContentListener);
            collectionItems.setAll(newValue.getCollection(CollectionType.ITEMS)
                .parallelStream()
                .map(ItemEntry::new)
                .collect(Collectors.toList()));
        });
    }

    @Override
    public void setNotificationProvider(CollectionsNotificationProvider notificationProvider) {
        this.notificationProvider = notificationProvider;
    }

    @Override
    public CollectionType getCollectionType() {
        return CollectionType.ITEMS;
    }

    @Override
    public Optional<ChoiceEntry> getSelectedEntry() {
        return DialogUtils.selectItem(choiceEntries);
    }

    @Override
    public void mergeEntries() {
        final List<ItemBase> itemBaseList = collectionItems.parallelStream()
            .map(ItemEntry::getItemBase)
            .collect(Collectors.toList());
        itemRegistry.merge(itemBaseList)
            .exceptionally(throwable -> {
                notificationProvider.showNotification(new Notification(translator.translate(R.Translate.NOTIFY_ITEM_MERGE_FAILED)));
                LOGGER.error("Nepodařilo se uložit všechny předměty.", throwable);
                throw new RuntimeException(throwable);
            })
            .thenAccept(merged ->
                notificationProvider.showNotification(new Notification(String.format(translator.translate(R.Translate.NOTIFY_MERGED_ITEMS), merged))));
    }

    public static final class ItemEntry {

        public final StringProperty name = new SimpleStringProperty(this, "name");
        public final IntegerProperty weight = new SimpleIntegerProperty(this, "weight");
        public final ObjectProperty<Money> price = new SimpleObjectProperty<>(this, "price");
        public final ObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");
        private ItemBase itemBase;

        ItemEntry(String id) {
            final Optional<ItemBase> optionalItem = OnlineItemRegistry.getINSTANCE().getItemById(id);
            optionalItem.ifPresent(itemBase -> {
                this.itemBase = itemBase;
                setName(itemBase.getName());
                setWeight(itemBase.getWeight());
                setPrice(itemBase.getPrice());
                final ByteArrayInputStream bais = new ByteArrayInputStream(itemBase.getImage());
                setImage(new Image(bais));
            });
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
