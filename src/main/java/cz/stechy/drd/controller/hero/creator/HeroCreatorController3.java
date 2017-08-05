package cz.stechy.drd.controller.hero.creator;

import cz.stechy.drd.R;
import cz.stechy.drd.controller.hero.HeroHelper;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.db.base.DatabaseItem;
import cz.stechy.drd.model.inventory.ItemSlot;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.ItemRegistry;
import cz.stechy.drd.util.CellUtils;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

/**
 * Třetí kontroler z průvodce vytvoření postavy Nastavení základních předmětů a zbraní
 */
public class HeroCreatorController3 extends BaseController implements Initializable {

    // region Variables

    // region FXML

    @FXML
    private TableView<ItemEntry> tableItems;
    @FXML
    private TableColumn<ItemEntry, Image> columnImage;
    @FXML
    private TableColumn<ItemEntry, String> columnName;
    @FXML
    private TableColumn<ItemEntry, MaxActValue> columnItemCount;
    @FXML
    private TableColumn<ItemEntry, Integer> columnWeight;

    @FXML
    private Button btnAddItem;
    @FXML
    private Button btnRemoveItem;

    // endregion

    private final ObservableList<ItemEntry> items = FXCollections.observableArrayList();
    private final ObservableList<ChoiceEntry> itemRegistry = FXCollections.observableArrayList();
    private final IntegerProperty selectedItem = new SimpleIntegerProperty();

    private String title;
    private Bundle bundle;

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = resources.getString(R.Translate.GUIDE_NEW_HERO_3_TITLE);
        tableItems.setItems(items);
        selectedItem.bind(tableItems.getSelectionModel().selectedIndexProperty());

        columnImage.setCellFactory(param -> CellUtils.forImage());
        columnItemCount.setCellFactory(param -> CellUtils.forMaxActValue());

        final List<ChoiceEntry> items = ItemRegistry.getINSTANCE().getRegistry().entrySet()
            .stream()
            .map(entry -> new ChoiceEntry(entry.getValue()))
            .collect(Collectors.toList());
        itemRegistry.setAll(items);

        btnRemoveItem.disableProperty().bind(selectedItem.lessThan(0));
    }

    @Override
    protected void onCreate(Bundle bundle) {
        this.bundle = bundle;
    }

    @Override
    protected void onResume() {
        setTitle(title);
        setScreenSize(600, 400);
    }

    // region Button handles

    @FXML
    private void handleBack(ActionEvent actionEvent) {
        back();
    }

    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        finish();
    }

    @FXML
    private void handleReset(ActionEvent actionEvent) {
        items.clear();
    }

    @FXML
    private void handleFinish(ActionEvent actionEvent) {
        setResult(RESULT_SUCCESS);
        bundle.put(HeroHelper.INVENTORY, items);
        finish(bundle);
    }

    @FXML
    private void handleAddItem(ActionEvent actionEvent) {
        final ChoiceDialog<ChoiceEntry> dialog = new ChoiceDialog<>(null, itemRegistry);
        dialog.setTitle("Přidat item");
        dialog.setHeaderText("Výběr itemu");
        dialog.setContentText("Vyberte...");
        // Trocha čarování k získání reference na combobox abych ho mohl upravit
        @SuppressWarnings("unchecked") final ComboBox<ChoiceEntry> comboBox = (ComboBox) (((GridPane) dialog
            .getDialogPane()
            .getContent())
            .getChildren().get(1));
        comboBox.setPrefWidth(100);
        comboBox.setButtonCell(new ChoiceEntryCell());
        comboBox.setCellFactory(param -> new ChoiceEntryCell());
        comboBox.setMinWidth(200);
        comboBox.setMinHeight(40);
        final Optional<ChoiceEntry> result = dialog.showAndWait();
        result.ifPresent(choiceEntry -> {
            final Optional<ItemEntry> entry = items.stream()
                .filter(itemEntry -> itemEntry.getId().equals(choiceEntry.id.get()))
                .findFirst();
            if (!entry.isPresent()) {
                items.add(new ItemEntry(choiceEntry));
            }
        });
    }

    @FXML
    private void handleRemoveItem(ActionEvent actionEvent) {
        items.remove(selectedItem.get());
    }

    // endregion

    static final class ChoiceEntry {

        final StringProperty id = new SimpleStringProperty();
        final StringProperty name = new SimpleStringProperty();
        final ObjectProperty<Image> image = new SimpleObjectProperty<>();

        public ChoiceEntry(DatabaseItem databaseItem) {
            assert databaseItem instanceof ItemBase;
            final ItemBase itemBase = (ItemBase) databaseItem;
            this.id.setValue(itemBase.getId());
            this.name.setValue(itemBase.getName());
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(itemBase.getImage());
            image.set(new Image(inputStream));
        }

        @Override
        public String toString() {
            return name.get();
        }
    }

    private static final class ChoiceEntryCell extends ListCell<ChoiceEntry> {

        final ImageView imageView = new ImageView();
        final Label label = new Label();
        final HBox container = new HBox(imageView, label);

        {
            imageView.setFitWidth(ItemSlot.SLOT_SIZE);
            imageView.setFitHeight(ItemSlot.SLOT_SIZE);

            label.setTextFill(Color.BLACK);
            label.setMinHeight(40);
            label.setAlignment(Pos.CENTER_LEFT);
            label.setTextAlignment(TextAlignment.CENTER);

            container.setSpacing(8);
        }

        @Override
        protected void updateItem(ChoiceEntry item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setGraphic(null);
                setText(null);
            } else {
                imageView.setImage(item.image.get());
                label.setText(item.name.get());
                setGraphic(container);
            }
        }
    }

}
