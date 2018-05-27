package cz.stechy.drd.app.hero.creator;

import cz.stechy.drd.R;
import cz.stechy.drd.app.hero.HeroHelper;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.util.CellUtils;
import cz.stechy.drd.util.DialogUtils;
import cz.stechy.drd.util.DialogUtils.ChoiceEntry;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;

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

        //itemRegistry.setAll(ItemRegistry.getINSTANCE().getChoices());

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
        final Optional<ChoiceEntry> result = DialogUtils.selectItem(itemRegistry);
        result.ifPresent(choiceEntry -> {
            final Optional<ItemEntry> entry = items.stream()
                .filter(itemEntry -> itemEntry.getId().equals(choiceEntry.getId()))
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

}
