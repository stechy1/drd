package cz.stechy.drd.controller.shop;

import com.jfoenix.controls.JFXToggleButton;
import cz.stechy.drd.R;
import cz.stechy.drd.model.db.base.Firebase.OnDeleteItem;
import cz.stechy.drd.model.db.base.Firebase.OnDownloadItem;
import cz.stechy.drd.model.db.base.Firebase.OnUploadItem;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.persistent.HeroService;
import cz.stechy.drd.model.persistent.UserService;
import cz.stechy.drd.model.shop.ShoppingCart;
import cz.stechy.drd.model.shop.entry.ShopEntry;
import cz.stechy.drd.model.user.User;
import cz.stechy.drd.util.HashGenerator;
import cz.stechy.drd.util.Translator;
import cz.stechy.drd.util.Translator.Key;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;

/**
 * Kontroler pro obchod s předměty + jejich správu
 */
public class ShopController1 extends BaseController implements Initializable {

    // region Constants

    private static final int NO_SELECTED_INDEX = -1;
    private static final int ACTION_ADD_ITEM = 1;
    private static final int ACTION_UPDATE_ITEM = 2;

    // endregion

    // region Variables

    // region FXML

    @FXML
    private Accordion accodionShopContainer;

    @FXML
    private ShopWeaponMeleController tableMeleWeaponController;
    @FXML
    private ShopWeaponRangedController tableRangedWeaponController;
    @FXML
    private ShopArmorController tableArmorController;
    @FXML
    private ShopGeneralController tableGeneralController;
    @FXML
    private ShopBackpackController tableBackpackController;

    @FXML
    private TableView tableMeleWeapon;
    @FXML
    private TableView tableRangedWeapon;
    @FXML
    private TableView tableArmor;
    @FXML
    private TableView tableGeneral;
    @FXML
    private TableView tableBackpack;

    @FXML
    private Button btnAddItem;
    @FXML
    private Button btnRemoveItem;
    @FXML
    private Button btnEditItem;
    @FXML
    private Button btnUploadItem;
    @FXML
    private Button btnDownloadItem;
    @FXML
    private Button btnRemoveOnlineItem;
    @FXML
    private ToggleButton btnToggleOnline;
    @FXML
    private Label lblTotalPrice;
    @FXML
    private Button btnContinueShopping;
    @FXML
    private JFXToggleButton btnToggleEditMode;

    // endregion

    private final Translator translator;
    private final ShoppingCart shoppingCart;
    private final List<String> translatedItemType = new ArrayList<>();
    private final IntegerProperty selectedAccordionPaneIndex = new SimpleIntegerProperty(
        NO_SELECTED_INDEX);
    private final IntegerProperty selectedRowIndex = new SimpleIntegerProperty(NO_SELECTED_INDEX);
    private final BooleanProperty showOnlineDatabase = new SimpleBooleanProperty(false);
    // Indikuje, zda-li se nacházím v edit modu
    private final BooleanProperty editMode = new SimpleBooleanProperty(this, "editMode", false);
    private final BooleanProperty disableDownloadBtn = new SimpleBooleanProperty(this,
        "disableDownloadBtn", true);
    private final BooleanProperty disableUploadBtn = new SimpleBooleanProperty(this,
        "disableUploadBtn", true);
    private final BooleanProperty disableRemoveOnlineBtn = new SimpleBooleanProperty(this,
        "disableRemoveOnlineBtn", true);

    private final User user;
    private final Hero hero;

    private ShopItemController[] controllers;
    private String title;

    // endregion

    // region Constructors

    public ShopController1(UserService userService, HeroService heroService, Translator translator, ShoppingCart shoppingCart) {
        this.hero = heroService.getHero();
        this.translator = translator;
        this.shoppingCart = shoppingCart;
        this.user = userService.getUser();

        this.translatedItemType.addAll(translator.getTranslationFor(Key.SHOP_ITEMS));
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = resources.getString(R.Translate.SHOP_TITLE);

        controllers = new ShopItemController[]{
            tableMeleWeaponController,
            tableRangedWeaponController,
            tableArmorController,
            tableGeneralController,
            tableBackpackController
        };

        accodionShopContainer.expandedPaneProperty()
            .addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    selectedAccordionPaneIndex.setValue(NO_SELECTED_INDEX);
                    selectedRowIndex.setValue(NO_SELECTED_INDEX);
                    return;
                }

                selectedAccordionPaneIndex.setValue(translatedItemType.indexOf(newValue.getText()));
            });

        editMode.bindBidirectional(btnToggleEditMode.selectedProperty());

        final BooleanBinding selectedRowBinding = selectedRowIndex.isEqualTo(NO_SELECTED_INDEX);
        final BooleanBinding selectedAccordionPane = selectedAccordionPaneIndex
            .isNotEqualTo(NO_SELECTED_INDEX).not();
        btnAddItem.disableProperty().bind(Bindings.or(
            editMode.not(),
            Bindings.or(
                selectedAccordionPane,
                showOnlineDatabase)));
        btnRemoveItem.disableProperty().bind(Bindings.or(
            editMode.not(),
            Bindings.or(
                selectedRowBinding,
                Bindings.or(
                    selectedAccordionPane,
                    showOnlineDatabase))));
        btnEditItem.disableProperty().bind(Bindings.or(
            editMode.not(),
            Bindings.or(
                selectedRowBinding,
                Bindings.or(
                    selectedAccordionPane,
                    showOnlineDatabase))));
        btnDownloadItem.disableProperty().bind(
            user.loggedProperty().not().or(
                editMode.not().or(
                    disableDownloadBtn.or(
                        showOnlineDatabase.not()))));
        btnUploadItem.disableProperty().bind(
            user.loggedProperty().not().or(
                editMode.not().or(
                    disableUploadBtn.or(
                        showOnlineDatabase))));
        btnRemoveOnlineItem.disableProperty().bind(
            user.loggedProperty().not().or(
                editMode.not().or(
                    disableRemoveOnlineBtn.or(
                        showOnlineDatabase.not()))));

        btnContinueShopping.disableProperty().bind(Bindings.or(
            editMode,
            Bindings.or(
                Bindings.equal(
                    "", hero.nameProperty()),
                shoppingCart.enoughtMoneyProperty().not())));
        selectedAccordionPaneIndex.addListener((observable, oldValue, newValue) -> {
            int index = newValue.intValue();
            if (index < 0) {
                return;
            }

            controllers[index].clearSelectedRow();
        });
        selectedRowIndex.addListener((observable, oldValue, newValue) -> {
            if (newValue == null || selectedAccordionPaneIndex.get() < 0) {
                return;
            }

            assert selectedAccordionPaneIndex.getValue() != null;

            final ShopItemController controller = controllers[selectedAccordionPaneIndex.get()];
            final Optional<ShopEntry> entryOptional = controller.getSelectedItem();
            if (entryOptional.isPresent()) {
                final ShopEntry entry = entryOptional.get();
                disableDownloadBtn.bind(entry.downloadedProperty());
                disableUploadBtn.bind(entry.uploadedProperty().or(entry.authorProperty().isNotEqualTo(user.nameProperty())));
                disableRemoveOnlineBtn.bind(entry.authorProperty().isNotEqualTo(user.nameProperty()));
            } else {
                disableDownloadBtn.unbind();
                disableUploadBtn.unbind();
                disableRemoveOnlineBtn.unbind();

                disableDownloadBtn.set(true);
                disableUploadBtn.set(true);
                disableRemoveOnlineBtn.set(true);
            }
        });
        lblTotalPrice.textProperty().bind(shoppingCart.totalPrice.text);
        lblTotalPrice.textFillProperty().bind(Bindings
            .when(shoppingCart.enoughtMoneyProperty())
            .then(Color.GREEN)
            .otherwise(Color.RED));
        showOnlineDatabase.bindBidirectional(btnToggleOnline.selectedProperty());

        for (ShopItemController controller : controllers) {
            controller.setShoppingCart(shoppingCart);
            controller.setRowSelectedIndexProperty(selectedRowIndex);
            controller.setShowOnlineDatabase(showOnlineDatabase);
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        tableArmorController.setHeroHeight(hero.getHeight());
    }

    @Override
    protected void onResume() {
        setScreenSize(1280, 600);
        setTitle(title);
    }

    @Override
    protected void onClose() {
        showOnlineDatabase.setValue(false);
    }

    @Override
    protected void onScreenResult(int statusCode, int actionId, Bundle bundle) {
        final ShopItemController controller = controllers[selectedAccordionPaneIndex.get()];
        ItemBase item;
        switch (actionId) {
            case ACTION_ADD_ITEM:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }
                item = controller.fromBundle(bundle);
                item.setAuthor(user.getName());
                item.setId(HashGenerator.createHash());
                item.setDownloaded(true);
                controller.onAddItem(item, false);
                break;
            case ACTION_UPDATE_ITEM:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }
                item = controller.fromBundle(bundle);
                controller.onUpdateItem(item);
                break;
        }
    }

    // region Button handlers

    @FXML
    private void handleAddItem(ActionEvent actionEvent) {
        Bundle bundle = new Bundle();
        bundle.putInt(ShopHelper.ITEM_ACTION, ShopHelper.ITEM_ACTION_ADD);
        startNewDialogForResult(controllers[selectedAccordionPaneIndex.get()].getEditScreenName(),
            ACTION_ADD_ITEM, bundle);
    }

    @FXML
    private void handleRemoveItem(ActionEvent actionEvent) {
        controllers[selectedAccordionPaneIndex.get()].requestRemoveItem(selectedRowIndex.get());
    }

    @FXML
    private void handleEditItem(ActionEvent actionEvent) {
        Bundle bundle = new Bundle();
        ShopItemController controller = controllers[selectedAccordionPaneIndex.get()];
        bundle.putInt(ShopHelper.ITEM_ACTION, ShopHelper.ITEM_ACTION_UPDATE);
        controller.insertItemToBundle(bundle, selectedRowIndex.get());
        startNewDialogForResult(controller.getEditScreenName(),
            ACTION_UPDATE_ITEM, bundle);
    }

    @FXML
    private void handleUploadItem(ActionEvent actionEvent) {
        ShopItemController<ShopEntry> controller = controllers[selectedAccordionPaneIndex.get()];
        controller.getSelectedItem().ifPresent(entry -> controller.uploadRequest(entry.getItemBase()));
    }

    @FXML
    private void handleDownloadItem(ActionEvent actionEvent) {
        ShopItemController<ShopEntry> controller = controllers[selectedAccordionPaneIndex.get()];
        controller.getSelectedItem().ifPresent(entry -> controller.onAddItem(entry.getItemBase(), true));
    }

    @FXML
    private void handleRemoveOnlineItem(ActionEvent actionEvent) {
        ShopItemController<ShopEntry> controller = controllers[selectedAccordionPaneIndex.get()];
        controller.getSelectedItem().ifPresent(entry -> controller.requestRemoveItem(entry, true));
    }

    @FXML
    private void handleContinueShopping(ActionEvent actionEvent) {
        Bundle bundle = new Bundle();
        bundle.put(ShopController2.HERO_ID, hero.getId());
        bundle.put(ShopController2.SHOPPING_CART, shoppingCart);
        startScreen(R.FXML.SHOP2, bundle);
    }

    @FXML
    private void handleSynchronize(ActionEvent actionEvent) {
        Arrays.stream(controllers).forEach(ShopItemController::synchronizeItems);
    }

    // endregion

    private final OnUploadItem<ShopEntry> uploadHandler = item -> {
        ShopItemController controller = controllers[selectedAccordionPaneIndex.get()];
        controller.uploadRequest(item.getItemBase());
    };
    private final OnDownloadItem<ShopEntry> downloadHandler = item -> {
        ShopItemController controller = controllers[selectedAccordionPaneIndex.get()];
        controller.onAddItem(item.getItemBase(), true);
    };
    private final OnDeleteItem<ShopEntry> deleteHandler = (item, remote) -> {
        ShopItemController controller = controllers[selectedAccordionPaneIndex.get()];
        controller.requestRemoveItem(item, remote);
    };
}
