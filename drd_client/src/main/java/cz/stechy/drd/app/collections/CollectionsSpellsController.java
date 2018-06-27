package cz.stechy.drd.app.collections;

import cz.stechy.drd.R;
import cz.stechy.drd.dao.ItemCollectionDao;
import cz.stechy.drd.dao.SpellBookDao;
import cz.stechy.drd.model.item.ItemCollection;
import cz.stechy.drd.model.item.ItemCollection.CollectionType;
import cz.stechy.drd.model.spell.Spell;
import cz.stechy.drd.model.spell.Spell.SpellProfessionType;
import cz.stechy.drd.util.CellUtils;
import cz.stechy.drd.util.DialogUtils;
import cz.stechy.drd.util.DialogUtils.ChoiceEntry;
import cz.stechy.drd.util.Translator;
import cz.stechy.screens.Notification;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
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

public class CollectionsSpellsController implements Initializable, CollectionsControllerChild {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionsSpellsController.class);

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TableView<SpellEntry> tableCollectionsSpells;
    @FXML
    private TableColumn<SpellEntry, Image> columnImage;
    @FXML
    private TableColumn<SpellEntry, String> columnName;
    @FXML
    private TableColumn<SpellEntry, SpellProfessionType> columnType;


    // endregion
    private final ObservableList<SpellEntry> collectionItems = FXCollections.observableArrayList();
    private final ObservableList<ChoiceEntry> spellRegistry = FXCollections.observableArrayList();

    private final SpellBookDao spellService;
    private final Translator translator;

    // Nemůžu dát "final", protože by mi to brečelo v bestiaryCollectionContentListener
    private ItemCollectionDao collectionService;
    private StringProperty selectedEntry;
    private CollectionsNotificationProvider notificationProvider;


    // endregion

    // region Constructors

    public CollectionsSpellsController(SpellBookDao spellService,
        Translator translator, ItemCollectionDao collectionService) {
        this.spellService = spellService;
        this.translator = translator;
        this.collectionService = collectionService;

        spellService.selectAllAsync()
            .thenAccept(spells -> this.spellRegistry.setAll(DialogUtils.getSpellChoices(spells)));
    }

    // endregion

    // region Private methods

    private ListChangeListener<? super String> bestiaryCollectionContentListener = c -> {
        while (c.next()) {
            collectionItems.addAll(c.getAddedSubList().stream()
                .map(SpellEntry::new)
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
        tableCollectionsSpells.setFixedCellSize(40);
        tableCollectionsSpells.setItems(collectionItems);
        columnImage.setCellFactory(param -> CellUtils.forImage());

        tableCollectionsSpells.getSelectionModel().selectedItemProperty()
            .addListener((observableValue, oldValue, newValue) -> {
                selectedEntry.setValue(newValue == null ? null : newValue.getId());
            });
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
                oldValue.getCollection(CollectionType.SPELLS).removeListener(this.bestiaryCollectionContentListener);
            }
            if (newValue == null) {
                return;
            }

            newValue.getCollection(CollectionType.SPELLS).addListener(this.bestiaryCollectionContentListener);
            collectionItems.setAll(newValue.getCollection(CollectionType.SPELLS)
                .parallelStream()
                .map(SpellEntry::new)
                .collect(Collectors.toList()));
        });
    }

    @Override
    public void setNotificationProvider(CollectionsNotificationProvider notificationProvider) {
        this.notificationProvider = notificationProvider;
    }

    @Override
    public void requestAddEntryToCollection(ItemCollection collection) {
        final Optional<ChoiceEntry> entryOptional = DialogUtils.selectItem(spellRegistry);
        entryOptional.ifPresent(choiceEntry -> {
            final String itemName = choiceEntry.getName();
            final String collectionName = collection.getName();
            collectionService.addItemToCollection(collection, CollectionType.SPELLS, choiceEntry.getId())
                .exceptionally(throwable -> {
                    notificationProvider.showNotification(new Notification(String.format(translator.translate(
                        R.Translate.NOTIFY_COLLECTION_RECORD_IS_NOT_INSERTED), itemName,
                        collectionName)));
                    LOGGER.error("Položku se nepodařilo přidat do kolekce");
                    throw new RuntimeException(throwable);
                })
                .thenAccept(ignored -> {
                    notificationProvider.showNotification(new Notification(String.format(translator.translate(
                        R.Translate.NOTIFY_COLLECTION_RECORD_IS_INSERTED), itemName,
                        collectionName)));
                });
        });
    }

    @Override
    public void requestRemoveSelectedEntryFromCollection(ItemCollection collection) {
        final String id = selectedEntry.get();
        collectionService.removeItemFromCollection(collection, CollectionType.SPELLS, id);
    }

    @Override
    public void mergeEntries() {

    }

    public final class SpellEntry {
        private final String id;
        public final StringProperty name = new SimpleStringProperty(this, "name");
        public final ObjectProperty<SpellProfessionType> type = new SimpleObjectProperty<>(this, "type");
        public final ObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");

        public SpellEntry(String id) {
            this.id = id;
            final Optional<Spell> optionalSpell = spellService
                .selectOnline(spell -> spell.getId().equals(id));
            optionalSpell.ifPresent(spell -> {
                setName(spell.getName());
                setType(spell.getType());
                ByteArrayInputStream bais = new ByteArrayInputStream(spell.getImage());
                setImage(new Image(bais));
            });
        }

        public String getId() {
            return id;
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

        public SpellProfessionType getType() {
            return type.get();
        }

        public ObjectProperty<SpellProfessionType> typeProperty() {
            return type;
        }

        public void setType(SpellProfessionType type) {
            this.type.set(type);
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
    }
}
