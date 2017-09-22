package cz.stechy.drd.controller.collections;

import cz.stechy.drd.R;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.ItemCollection;
import cz.stechy.drd.model.persistent.ItemCollectionService;
import cz.stechy.drd.model.persistent.UserService;
import cz.stechy.drd.model.user.User;
import cz.stechy.drd.util.ObservableMergers;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Notification;
import java.io.ByteArrayInputStream;
import java.net.URL;
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
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

public class CollectionsController extends BaseController implements Initializable {

    // region Variables

    // region FXML

    @FXML
    private ListView<ItemCollection> lvCollections;
    @FXML
    private ListView<ItemEntry> lvCollectionItems;
    @FXML
    private Button btnCollectionAdd;
    @FXML
    private Button btnCollectionRemove;
    @FXML
    private Button btnCollectionDownload;
    @FXML
    private TextField txtCollectionName;

    // endregion

    private final ObservableList<ItemCollection> collections = FXCollections.observableArrayList();
    private final ObjectProperty<ItemCollection> selectedCollection = new SimpleObjectProperty<>(this, "selectedCollection", null);

    private final ItemCollectionService collectionService;
    private final User user;

    private String title;

    // endregion

    // region Constructors

    public CollectionsController(ItemCollectionService collectionService, UserService userService) {
        this.collectionService = collectionService;
        this.user = userService.getUser();
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title = resources.getString(R.Translate.COLLECTIONS_TITLE);
        lvCollections.setItems(collections);
        lvCollections.setCellFactory(param -> new ItemCollectionCell());

        btnCollectionAdd.disableProperty().bind(Bindings.createBooleanBinding(() ->
            user == null || txtCollectionName.getText().trim().isEmpty(),
            txtCollectionName.textProperty()));
        final BooleanBinding selectedBinding = selectedCollection.isNull();
        btnCollectionRemove.disableProperty().bind(selectedBinding);
        btnCollectionDownload.disableProperty().bind(selectedBinding);

        selectedCollection.bind(lvCollections.getSelectionModel().selectedItemProperty());

        ObservableMergers.mergeList(collections, collectionService.getCollections());
    }

    @Override
    protected void onResume() {
        setTitle(title);
        setScreenSize(550, 300);
    }

    // region Button handlers

    @FXML
    private void handleCollectionAdd(ActionEvent actionEvent) {
        ItemCollection collection = new ItemCollection.Builder()
            .name(txtCollectionName.getText())
            .author(user != null ? user.getName() : "")
            .build();
        collectionService.upload(collection);
        txtCollectionName.setText("");
    }

    @FXML
    private void handleCollectionRemove(ActionEvent actionEvent) {
        final ItemCollection collection = selectedCollection.get();
        collectionService.deleteRemote(collection, true);
        lvCollections.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleCollectionDownload(ActionEvent actionEvent) {
        showNotification(new Notification("Funkce není implementována..."));
    }

    // endregion

    private static final class ItemEntry {
        final StringProperty name = new SimpleStringProperty(this, "name");
        final IntegerProperty weight = new SimpleIntegerProperty(this, "weight");
        final ObjectProperty<Money> price = new SimpleObjectProperty<>(this, "price");
        final ObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");
        final ItemBase itemBase;

        public ItemEntry(ItemBase itemBase) {
            this.itemBase = itemBase;
            setName(itemBase.getName());
            setWeight(itemBase.getWeight());
            setPrice(itemBase.getPrice());
            ByteArrayInputStream bais = new ByteArrayInputStream(itemBase.getImage());
            setImage(new Image(bais));
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
    }
}
