package cz.stechy.drd.app.collections;

import cz.stechy.drd.R;
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
import java.util.List;
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

    private StringProperty selectedEntry;
    private CollectionsNotificationProvider notificationProvider;

    // endregion

    // region Constructors

    public CollectionsSpellsController(SpellBookDao spellService, Translator translator) {
        this.spellService = spellService;
        this.translator = translator;

        spellService.selectAllAsync()
            .thenAccept(spells ->
                this.spellRegistry.setAll(DialogUtils.getSpellChoices(spells)));
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
            .addListener((observableValue, oldValue, newValue) -> selectedEntry.setValue(newValue == null ? null : newValue.getId()));
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
    public CollectionType getCollectionType() {
        return CollectionType.SPELLS;
    }

    @Override
    public Optional<ChoiceEntry> getSelectedEntry() {
        return DialogUtils.selectItem(spellRegistry);
    }

    @Override
    public void mergeEntries() {
        final List<Spell> spellList = collectionItems.stream()
            .map(SpellEntry::getSpell)
            .collect(Collectors.toList());
        spellService.saveAll(spellList)
            .exceptionally(throwable -> {
                notificationProvider.showNotification(new Notification(translator.translate(R.Translate.NOTIFY_ITEM_MERGE_FAILED)));
                LOGGER.error("Nepodařilo se uložit všechna kouzla.", throwable);
                throw new RuntimeException(throwable);
            })
            .thenAccept(merged ->
                notificationProvider.showNotification(new Notification(String.format(translator.translate(R.Translate.NOTIFY_MERGED_ITEMS), merged))));
    }

    public final class SpellEntry {
        private final String id;
        public final StringProperty name = new SimpleStringProperty(this, "name");
        public final ObjectProperty<SpellProfessionType> type = new SimpleObjectProperty<>(this, "type");
        public final ObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");
        private Spell spell;

        public SpellEntry(String id) {
            this.id = id;
            final Optional<Spell> optionalSpell = spellService.selectOnline(spell -> spell.getId().equals(id));
            optionalSpell.ifPresent(spell -> {
                this.spell = spell;
                setName(spell.getName());
                setType(spell.getType());
                final ByteArrayInputStream bais = new ByteArrayInputStream(spell.getImage());
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

        public Spell getSpell() {
            return spell;
        }
    }
}
