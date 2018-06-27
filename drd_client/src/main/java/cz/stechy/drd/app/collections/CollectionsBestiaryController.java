package cz.stechy.drd.app.collections;

import cz.stechy.drd.dao.BestiaryDao;
import cz.stechy.drd.model.Rule;
import cz.stechy.drd.model.entity.mob.Mob;
import cz.stechy.drd.model.item.ItemCollection;
import cz.stechy.drd.model.item.ItemCollection.CollectionType;
import cz.stechy.drd.util.CellUtils;
import cz.stechy.drd.util.DialogUtils;
import cz.stechy.drd.util.DialogUtils.ChoiceEntry;
import cz.stechy.drd.util.Translator;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;
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

public class CollectionsBestiaryController implements Initializable, CollectionsControllerChild {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionsBestiaryController.class);

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TableView<BestiaryEntry> tableCollectionsBestiary;
    @FXML
    private TableColumn<BestiaryEntry, Image> columnImage;
    @FXML
    private TableColumn<BestiaryEntry, String> columnName;
    @FXML
    private TableColumn<BestiaryEntry, Rule> columnRulesType;

    // endregion

    private final ObservableList<BestiaryEntry> collectionItems = FXCollections.observableArrayList();
    private final ObservableList<ChoiceEntry> mobRegistry = FXCollections.observableArrayList();

    private final Translator translator;
    // Nemůžu dát "final", protože by mi to brečelo v bestiaryCollectionContentListener
    private BestiaryDao bestiaryService;
    private StringProperty selectedEntry;

    // endregion

    // region Constructors

    public CollectionsBestiaryController(Translator translator, BestiaryDao bestiaryService) {
        this.translator = translator;
        this.bestiaryService = bestiaryService;

        bestiaryService.selectAllAsync()
            .thenAccept(mobs -> this.mobRegistry.setAll(DialogUtils.getMobsChoices(mobs)));
    }

    // endregion

    // region Private methods

    private ListChangeListener<? super String> bestiaryCollectionContentListener = c -> {
        while (c.next()) {
            collectionItems.addAll(c.getAddedSubList().stream()
                .map(BestiaryEntry::new)
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
        tableCollectionsBestiary.setFixedCellSize(40);
        tableCollectionsBestiary.setItems(collectionItems);
        columnImage.setCellFactory(param -> CellUtils.forImage());

        tableCollectionsBestiary.getSelectionModel().selectedItemProperty()
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
                oldValue.getCollection(CollectionType.BESTIARY).removeListener(this.bestiaryCollectionContentListener);
            }
            if (newValue == null) {
                return;
            }

            newValue.getCollection(CollectionType.BESTIARY).addListener(this.bestiaryCollectionContentListener);
            collectionItems.setAll(newValue.getCollection(CollectionType.BESTIARY)
                .parallelStream()
                .map(BestiaryEntry::new)
                .collect(Collectors.toList()));
        });
    }

    @Override
    public void setNotificationProvider(CollectionsNotificationProvider notificationProvider) {}

    @Override
    public CollectionType getCollectionType() {
        return CollectionType.BESTIARY;
    }

    @Override
    public Optional<ChoiceEntry> getSelectedEntry() {
        return DialogUtils.selectItem(mobRegistry);
    }

    @Override
    public void mergeEntries() {
        final List<Mob> mobList = collectionItems.stream()
            .map(BestiaryEntry::getMob)
            .collect(Collectors.toList());
        bestiaryService.saveAll(mobList)
            .exceptionally(throwable -> {
                System.out.println("Něco se zvrtlo");
                throwable.printStackTrace();
                throw new RuntimeException(throwable);
            })
            .thenAccept(integer -> {
                System.out.println("Bylo uloženo: " + integer + " nestvůr.");
            });
    }

    public final class BestiaryEntry {
        private final String id;
        public final StringProperty name = new SimpleStringProperty(this, "name");
        public final ObjectProperty<Rule> ruleType = new SimpleObjectProperty<>(this, "ruleType");
        public final ObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");
        private Mob mob;

        public BestiaryEntry(String id) {
            this.id = id;
            final Optional<Mob> optionalMob = bestiaryService.selectOnline(mob -> mob.getId().equals(id));
            optionalMob.ifPresent(mob -> {
                this.mob = mob;
                setName(mob.getName());
                setRuleType(mob.getRulesType());
                ByteArrayInputStream bais = new ByteArrayInputStream(mob.getImage());
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

        public Rule getRuleType() {
            return ruleType.get();
        }

        public ObjectProperty<Rule> ruleTypeProperty() {
            return ruleType;
        }

        public void setRuleType(Rule ruleType) {
            this.ruleType.set(ruleType);
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

        Mob getMob() {
            return mob;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            BestiaryEntry that = (BestiaryEntry) o;
            return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getRuleType(), that.getRuleType()) &&
                Objects.equals(getImage(), that.getImage());
        }

        @Override
        public int hashCode() {

            return Objects.hash(getName(), getRuleType(), getImage());
        }
    }
}
