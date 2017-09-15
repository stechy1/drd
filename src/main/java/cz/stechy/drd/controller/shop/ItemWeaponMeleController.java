package cz.stechy.drd.controller.shop;

import cz.stechy.drd.R;
import cz.stechy.drd.controller.MoneyController;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.ValidatedModel;
import cz.stechy.drd.model.item.MeleWeapon;
import cz.stechy.drd.model.item.MeleWeapon.MeleWeaponClass;
import cz.stechy.drd.model.item.MeleWeapon.MeleWeaponType;
import cz.stechy.drd.util.DialogUtils;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.drd.util.ImageUtils;
import cz.stechy.drd.util.Translator;
import cz.stechy.drd.util.Translator.Key;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * Kontroler pro vytvoření nové zbraně na blízko
 */
public class ItemWeaponMeleController extends BaseController implements Initializable {

    // region Constants

    private static final int ACTION_MONEY = 1;

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String PRICE = "price";
    private static final String WEIGHT = "weight";
    private static final String STRENGTH = "strength";
    private static final String RAMPANCY = "rampancy";
    private static final String DEFENCE = "defence";
    private static final String WEAPON_CLASS = "weapon_class";
    private static final String WEAPON_TYPE = "weapon_type";
    private static final String AUTHOR = "author";
    private static final String RENOWN = "renown";
    private static final String IMAGE = "image";
    private static final String STACK_SIZE = "stack_size";
    private static final String UPLOADED = "uploaded";
    private static final String DOWNLOADED = "downloaded";

    // endregion

    // region Variables

    // region FXML

    @FXML
    private Label lblTitle;
    @FXML
    private TextField txtName;
    @FXML
    private TextArea txtDescription;
    @FXML
    private TextField txtStrength;
    @FXML
    private TextField txtRampancy;
    @FXML
    private TextField txtDefenceNumber;
    @FXML
    private ComboBox<MeleWeaponClass> cmbWeaponClass;
    @FXML
    private ComboBox<MeleWeapon.MeleWeaponType> cmbWeaponType;
    @FXML
    private TextField txtRenown;
    @FXML
    private TextField txtWeight;
    @FXML
    private Hyperlink lblPrice;
    @FXML
    private TextField txtStackSize;
    @FXML
    private Label lblSelectImage;
    @FXML
    private ImageView imageView;
    @FXML
    private Button btnFinish;

    // endregion

    private final WeaponMeleModel model = new WeaponMeleModel();
    private final Translator translator;
    private String titleNew;
    private String titleUpdate;
    private String imageChooserTitle;
    private int action;

    // endregion

    // region Constructors

    public ItemWeaponMeleController(Translator translator) {
        this.translator = translator;
    }

    // endregion

    // region Public static methods

    public static MeleWeapon fromBundle(Bundle bundle) {
        return new MeleWeapon.Builder()
            .id(bundle.getString(ID))
            .name(bundle.getString(NAME))
            .description(bundle.getString(DESCRIPTION))
            .weight(bundle.getInt(WEIGHT))
            .price(bundle.getInt(PRICE))
            .strength(bundle.getInt(STRENGTH))
            .rampancy(bundle.getInt(RAMPANCY))
            .defence(bundle.getInt(DEFENCE))
            .weaponClass(bundle.getInt(WEAPON_CLASS))
            .weaponType(bundle.getInt(WEAPON_TYPE))
            .author(bundle.getString(AUTHOR))
            .renown(bundle.getInt(RENOWN))
            .image(bundle.getByteArray(IMAGE))
            .stackSize(bundle.getInt(STACK_SIZE))
            .uploaded(bundle.getBoolean(UPLOADED))
            .downloaded(bundle.getBoolean(DOWNLOADED))
            .build();
    }

    public static void toBundle(Bundle bundle, MeleWeapon weapon) {
        bundle.putString(ID, weapon.getId());
        bundle.putString(NAME, weapon.getName());
        bundle.putString(DESCRIPTION, weapon.getDescription());
        bundle.putInt(WEIGHT, weapon.getWeight());
        bundle.putInt(PRICE, weapon.getPrice().getRaw());
        bundle.putInt(STRENGTH, weapon.getStrength());
        bundle.putInt(RAMPANCY, weapon.getRampancy());
        bundle.putInt(DEFENCE, weapon.getDefence());
        bundle.putInt(WEAPON_CLASS, weapon.getWeaponClass().ordinal());
        bundle.putInt(WEAPON_TYPE, weapon.getWeaponType().ordinal());
        bundle.putString(AUTHOR, weapon.getAuthor());
        bundle.putInt(RENOWN, weapon.getRenown());
        bundle.putByteArray(IMAGE, weapon.getImage());
        bundle.putInt(STACK_SIZE, weapon.getStackSize());
        bundle.putBoolean(UPLOADED, weapon.isUploaded());
        bundle.putBoolean(DOWNLOADED, weapon.isDownloaded());
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.titleNew = resources.getString(R.Translate.ITEM_NEW);
        this.titleUpdate = resources.getString(R.Translate.ITEM_UPDATE);
        this.imageChooserTitle = resources.getString(R.Translate.IMAGE_CHOOSE_DIALOG);

        txtName.textProperty().bindBidirectional(model.name);
        txtDescription.textProperty().bindBidirectional(model.description);

        cmbWeaponType.valueProperty().bindBidirectional(model.weaponType);
        cmbWeaponType.converterProperty().setValue(translator.getConvertor(Key.WEAPON_MELE_TYPES));
        cmbWeaponClass.valueProperty().bindBidirectional(model.weaponClass);
        cmbWeaponClass.converterProperty().setValue(translator.getConvertor(Key.WEAPON_MELE_CLASSES));

        FormUtils.initTextFormater(txtStrength, model.strength);
        FormUtils.initTextFormater(txtRampancy, model.rampancy);
        FormUtils.initTextFormater(txtDefenceNumber, model.defence);
        FormUtils.initTextFormater(txtRenown, model.renown);
        FormUtils.initTextFormater(txtWeight, model.weight);
        lblPrice.textProperty().bind(model.price.text);
        FormUtils.initTextFormater(txtStackSize, model.stackSize);
        imageView.imageProperty().bindBidirectional(model.image);
        model.imageRaw.addListener((observable, oldValue, newValue) ->
            lblSelectImage.setVisible(Arrays.equals(newValue, new byte[0])));

        btnFinish.disableProperty().bind(model.validProperty().not());
    }

    @Override
    protected void onCreate(Bundle bundle) {
        action = bundle.getInt(ShopHelper.ITEM_ACTION);
        lblTitle.setText(action == ShopHelper.ITEM_ACTION_ADD ? titleNew : titleUpdate);

        if (action == ShopHelper.ITEM_ACTION_UPDATE) {
            model.id.setValue(bundle.getString(ID));
            model.name.setValue(bundle.getString(NAME));
            model.description.setValue(bundle.getString(DESCRIPTION));
            model.price.setRaw(bundle.getInt(PRICE));
            model.weight.setActValue(bundle.getInt(WEIGHT));
            model.strength.setActValue(bundle.getInt(STRENGTH));
            model.rampancy.setActValue(bundle.getInt(RAMPANCY));
            model.defence.setActValue(bundle.getInt(DEFENCE));
            model.weaponClass.setValue(MeleWeaponClass.valueOf(bundle.getInt(WEAPON_CLASS)));
            model.weaponType.setValue(MeleWeaponType.valueOf(bundle.getInt(WEAPON_TYPE)));
            model.author.setValue(bundle.getString(AUTHOR));
            model.renown.setActValue(bundle.getInt(RENOWN));
            model.imageRaw.setValue(bundle.getByteArray(IMAGE));
            model.stackSize.setActValue(bundle.getInt(STACK_SIZE));
            model.uploaded.setValue(bundle.getBoolean(UPLOADED));
            model.downloaded.setValue(bundle.getBoolean(DOWNLOADED));
        }
    }

    @Override
    protected void onResume() {
        setTitle(action == ShopHelper.ITEM_ACTION_ADD ? titleNew : titleUpdate);
        setScreenSize(570, 500);
    }

    @Override
    protected void onScreenResult(int statusCode, int actionId, Bundle bundle) {
        switch (actionId) {
            case ACTION_MONEY:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }
                model.price.setRaw(bundle.getInt(MoneyController.MONEY));

                break;
        }
    }

    // region Buton handles

    @FXML
    private void handleFinish(ActionEvent actionEvent) {
        setResult(RESULT_SUCCESS);
        Bundle bundle = new Bundle();
        bundle.putInt(ShopHelper.ITEM_ACTION, action);
        bundle.putString(ID, model.id.getValue());
        bundle.putString(NAME, model.name.getValue());
        bundle.putString(DESCRIPTION, model.description.getValue());
        bundle.putInt(PRICE, model.price.getRaw());
        bundle.putInt(WEIGHT, model.weight.getActValue().intValue());
        bundle.putInt(STRENGTH, model.strength.getActValue().intValue());
        bundle.putInt(RAMPANCY, model.rampancy.getActValue().intValue());
        bundle.putInt(DEFENCE, model.defence.getActValue().intValue());
        bundle.putInt(WEAPON_CLASS, model.weaponClass.getValue().ordinal());
        bundle.putInt(WEAPON_TYPE, model.weaponType.getValue().ordinal());
        bundle.putInt(RENOWN, model.renown.getActValue().intValue());
        bundle.putString(AUTHOR, model.author.getValue());
        bundle.putByteArray(IMAGE, model.imageRaw.getValue());
        bundle.putInt(STACK_SIZE, model.stackSize.getActValue().intValue());
        bundle.putBoolean(UPLOADED, model.uploaded.getValue());
        bundle.putBoolean(DOWNLOADED, model.downloaded.getValue());
        finish(bundle);
    }

    @FXML
    private void handleShowMoneyPopup(ActionEvent actionEvent) {
        Bundle bundle = new Bundle().put(MoneyController.MONEY, model.price.getRaw());
        startNewPopupWindowForResult("money", ACTION_MONEY, bundle, (Node) actionEvent.getSource());
    }

    @FXML
    private void handleSelectImage(MouseEvent mouseEvent) {
        try {
            final byte[] image = DialogUtils
                .openImageForItemEditor(((Node) mouseEvent.getSource()).getScene().getWindow(),
                    imageChooserTitle);
            model.imageRaw.setValue(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // endregion

    private static class WeaponMeleModel extends ValidatedModel {

        private static final int FLAG_NAME = 1 << 0;
        private static final int FLAG_WEIGHT = 1 << 1;
        private static final int FLAG_STRENGTH = 1 << 2;
        private static final int FLAG_RAMPANCY = 1 << 3;
        private static final int FLAG_DEFENCE = 1 << 4;
        private static final int FLAG_WEAPON_CLASS = 1 << 5;
        private static final int FLAG_WEAPON_TYPE = 1 << 6;
        private static final int FLAG_IMAGE = 1 << 7;
        private static final int FLAG_RENOWN = 1 << 8;
        private static final int FLAG_STACK_SIZE = 1 << 9;

        final StringProperty id = new SimpleStringProperty(this, "id", null);
        final StringProperty name = new SimpleStringProperty(this, "name", null);
        final StringProperty description = new SimpleStringProperty(this, "description", null);
        final Money price = new Money();
        final MaxActValue weight = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue strength = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue rampancy = new MaxActValue(Integer.MIN_VALUE ,Integer.MAX_VALUE, 0);
        final MaxActValue defence = new MaxActValue(Integer.MIN_VALUE ,Integer.MAX_VALUE, 0);
        final ObjectProperty<MeleWeaponClass> weaponClass = new SimpleObjectProperty<>(this, "weaponClass", null);
        final ObjectProperty<MeleWeaponType> weaponType = new SimpleObjectProperty<>(this, "weaponType", null);
        final StringProperty author = new SimpleStringProperty(this, "author", null);
        final ObjectProperty<byte[]> imageRaw = new SimpleObjectProperty<>(this, "imageRaw");
        final MaxActValue renown = new MaxActValue(Integer.MAX_VALUE);
        final ObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");
        final MaxActValue stackSize = new MaxActValue(1, Integer.MAX_VALUE, 1);
        final BooleanProperty uploaded = new SimpleBooleanProperty(this, "uploaded");
        final BooleanProperty downloaded = new SimpleBooleanProperty(this, "downloaded");

        private boolean block = false;

        {
            imageRaw.addListener((observable, oldValue, newValue) -> {
                if (block) {
                    return;
                }

                if (newValue == null || Arrays.equals(newValue, new byte[0])) {
                    setValid(false);
                    setValidityFlag(FLAG_IMAGE, true);
                } else {
                    setValidityFlag(FLAG_IMAGE, false);
                }

                block = true;
                try {
                    final ByteArrayInputStream inputStream = new ByteArrayInputStream(newValue);
                    image.set(new Image(inputStream));
                } finally {
                    block = false;
                }
            });
            image.addListener((observable, oldValue, newValue) -> {
                if (block) {
                    return;
                }

                block = true;
                try {
                    imageRaw.setValue(ImageUtils.imageToRaw(newValue));
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    block = false;
                }
            });

            name.addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_NAME, true);
                } else {
                    setValidityFlag(FLAG_NAME, false);
                }
            });
            weight.actValueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_WEIGHT, true);
                } else {
                    setValidityFlag(FLAG_WEIGHT, false);
                }
            });
            strength.actValueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_STRENGTH, true);
                } else {
                    setValidityFlag(FLAG_STRENGTH, false);
                }
            });
            rampancy.actValueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_RAMPANCY, true);
                } else {
                    setValidityFlag(FLAG_RAMPANCY, false);
                }
            });
            defence.actValueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_DEFENCE, true);
                } else {
                    setValidityFlag(FLAG_DEFENCE, false);
                }
            });
            weaponClass.addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_WEAPON_CLASS, true);
                } else {
                    setValidityFlag(FLAG_WEAPON_CLASS, false);
                }
            });
            weaponType.addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_WEAPON_TYPE, true);
                } else {
                    setValidityFlag(FLAG_WEAPON_TYPE, false);
                }
            });
            renown.actValueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_RENOWN, true);
                } else {
                    setValidityFlag(FLAG_RENOWN, false);
                }
            });
            stackSize.actValueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_STACK_SIZE, true);
                } else {
                    setValidityFlag(FLAG_STACK_SIZE, false);
                }
            });

            validityFlag.set(FLAG_NAME + FLAG_WEAPON_CLASS + FLAG_WEAPON_TYPE + FLAG_IMAGE);
            setValid(false);
        }

    }


}
