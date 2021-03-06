package cz.stechy.drd.controller.shop;

import cz.stechy.drd.R;
import cz.stechy.drd.model.Context;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.persistent.HeroManager;
import cz.stechy.drd.model.shop.OnDeleteItem;
import cz.stechy.drd.model.shop.OnDownloadItem;
import cz.stechy.drd.model.shop.OnUploadItem;
import cz.stechy.drd.model.shop.ShoppingCart;
import cz.stechy.drd.model.user.User;
import cz.stechy.drd.util.HashGenerator;
import cz.stechy.drd.util.Translator;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;

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
    private ToggleButton btnToggleOnline;
    @FXML
    private Button btnContinueShopping;

    // endregion

    private final Translator translator;
    private final ShoppingCart shoppingCart = new ShoppingCart();
    private final List<String> translatedItemType = new ArrayList<>();
    private final IntegerProperty selectedAccordionPaneIndex = new SimpleIntegerProperty(
        NO_SELECTED_INDEX);
    private final IntegerProperty selectedRowIndex = new SimpleIntegerProperty(NO_SELECTED_INDEX);
    private final BooleanProperty showOnlineDatabase = new SimpleBooleanProperty(false);
    private final ObjectProperty<User> user;
    private final ObjectProperty<Hero> hero;

    private ShopItemController[] controllers;
    private String title;
    //private Hero hero;

    // endregion

    // region Constructors

    public ShopController1(Context context) {
        this.translator = context.getTranslator();
        this.translatedItemType.addAll(translator.getShopTypeList());
        this.user = context.getUserManager().getUser();
        this.hero = ((HeroManager) context.getManager(Context.MANAGER_HERO)).getHero();
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

        final BooleanBinding selectedRowBinding = selectedRowIndex.isNotEqualTo(NO_SELECTED_INDEX);
        btnAddItem.disableProperty().bind(Bindings.or(
            selectedAccordionPaneIndex.isNotEqualTo(NO_SELECTED_INDEX).not(),
            showOnlineDatabase));
        btnRemoveItem.disableProperty().bind(Bindings.or(
            selectedRowBinding.not(),
            Bindings.or(
                selectedAccordionPaneIndex.isNotEqualTo(NO_SELECTED_INDEX).not(),
                showOnlineDatabase)));
        btnEditItem.disableProperty().bind(Bindings.or(
            selectedRowBinding.not(),
            Bindings.or(
                selectedAccordionPaneIndex.isNotEqualTo(NO_SELECTED_INDEX).not(),
                showOnlineDatabase)));
        selectedAccordionPaneIndex.addListener((observable, oldValue, newValue) -> {
            int index = newValue.intValue();
            if (index < 0) {
                return;
            }

            controllers[index].clearSelectedRow();
        });

        showOnlineDatabase.bindBidirectional(btnToggleOnline.selectedProperty());

        for (ShopItemController controller : controllers) {
            controller.setShoppingCart(shoppingCart, uploadHandler, downloadHandler, deleteHandler);
            controller.setRowSelectedIndexProperty(selectedRowIndex);
            controller.setShowOnlineDatabase(showOnlineDatabase);
        }

        btnContinueShopping.setDisable(hero.get().getName().isEmpty());
        hero.addListener((observable, oldValue, newValue) ->
            btnContinueShopping.setDisable(newValue.getName().isEmpty()));
    }

    @Override
    protected void onCreate(Bundle bundle) {
        tableArmorController.setHeroHeight(hero.get().getHeight());
    }

    @Override
    protected void onResume() {
        setScreenSize(1000, 600);
        setTitle(title);
    }

    @Override
    protected void onClose() {
        for (ShopItemController controller : controllers) {
            controller.onClose();
        }
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
                item.setAuthor(user.get().getName());
                item.setId(HashGenerator.createHash());
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

    public void handleAddItem(ActionEvent actionEvent) {
        Bundle bundle = new Bundle();
        bundle.putInt(ShopHelper.ITEM_ACTION, ShopHelper.ITEM_ACTION_ADD);
        startNewDialogForResult(controllers[selectedAccordionPaneIndex.get()].getEditScreenName(),
            ACTION_ADD_ITEM, bundle);
    }

    public void handleRemoveItem(ActionEvent actionEvent) {
        controllers[selectedAccordionPaneIndex.get()].requestRemoveItem(selectedRowIndex.get());
    }

    public void handleEditItem(ActionEvent actionEvent) {
        Bundle bundle = new Bundle();
        ShopItemController controller = controllers[selectedAccordionPaneIndex.get()];
        bundle.putInt(ShopHelper.ITEM_ACTION, ShopHelper.ITEM_ACTION_UPDATE);
        controller.insertItemToBundle(bundle, selectedRowIndex.get());
        startNewDialogForResult(controller.getEditScreenName(),
            ACTION_UPDATE_ITEM, bundle);
    }

    public void handleToggleOnline(ActionEvent actionEvent) {

    }

    public void handleContinueShopping(ActionEvent actionEvent) {
        Bundle bundle = new Bundle();
        bundle.put(ShopController2.HERO_ID, hero.get().getId());
        bundle.put(ShopController2.SHOPPING_CART, shoppingCart);
        startScreen(R.FXML.SHOP2, bundle);
    }

    // endregion

    private final OnUploadItem uploadHandler = item -> {
        ShopItemController controller = controllers[selectedAccordionPaneIndex.get()];
        controller.uploadRequest(item.getItemBase());
    };
    private final OnDownloadItem downloadHandler = item -> {
        ShopItemController controller = controllers[selectedAccordionPaneIndex.get()];
        controller.onAddItem(item.getItemBase(), true);
    };
    private final OnDeleteItem deleteHandler = (item, remote) -> {
        ShopItemController controller = controllers[selectedAccordionPaneIndex.get()];
        controller.requestRemoveItem(item, remote);
    };

}
