package cz.stechy.drd.controller.shop;

import cz.stechy.drd.Money;
import cz.stechy.drd.R;
import cz.stechy.drd.controller.MoneyController;
import cz.stechy.drd.model.Context;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.item.MeleWeapon;
import cz.stechy.drd.model.item.MeleWeapon.MeleWeaponClass;
import cz.stechy.drd.model.item.MeleWeapon.MeleWeaponType;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.drd.util.ImageUtils;
import cz.stechy.drd.util.StringConvertors;
import cz.stechy.drd.util.Translator;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

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
    private static final String IMAGE = "image";
    private static final String STACK_SIZE = "stack_size";
    private static final String UPLOADED = "uploaded";
    private static final String DOWNLOADED = "downloaded";

    // endregion

    // region Variables

    // region FXML

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
    private TextField txtWeight;
    @FXML
    private Hyperlink lblPrice;
    @FXML
    private ImageView imageView;
    @FXML
    private Button btnFinish;

    // endregion

    private final WeaponMeleModel model = new WeaponMeleModel();
    private final Translator translator;
    private String title;
    private String imageChooserTitle;
    private int action;

    // endregion

    // region Constructors

    public ItemWeaponMeleController(Context context) {
        this.translator = context.getTranslator();
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
        bundle.putByteArray(IMAGE, weapon.getImage());
        bundle.putInt(STACK_SIZE, weapon.getStackSize());
        bundle.putBoolean(UPLOADED, weapon.isUploaded());
        bundle.putBoolean(DOWNLOADED, weapon.isDownloaded());
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title = resources.getString(R.Translate.ITEM_TYPE_WEAPON_MELE);
        this.imageChooserTitle = resources.getString(R.Translate.ITEM_IMAGE_CHOOSE_DIALOG);

        txtName.textProperty().bindBidirectional(model.name);
        txtDescription.textProperty().bindBidirectional(model.description);

        cmbWeaponType.valueProperty().bindBidirectional(model.weaponType);
        cmbWeaponType.converterProperty().setValue(StringConvertors.forMeleWeaponType(translator));
        cmbWeaponClass.valueProperty().bindBidirectional(model.weaponClass);
        cmbWeaponClass.converterProperty().setValue(
            StringConvertors.forMeleWeaponClass(translator));

        FormUtils.initTextFormater(txtStrength, model.strength);
        FormUtils.initTextFormater(txtRampancy, model.rampancy);
        FormUtils.initTextFormater(txtDefenceNumber, model.defence);
        FormUtils.initTextFormater(txtWeight, model.weight);

        lblPrice.textProperty().bind(model.price.text);
        imageView.imageProperty().bindBidirectional(model.image);

        btnFinish.disableProperty().bind(
            Bindings.or(
                model.name.isEmpty(),
                model.imageRaw.isNull()));
    }

    @Override
    protected void onCreate(Bundle bundle) {
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
        model.imageRaw.setValue(bundle.getByteArray(IMAGE));
        model.stackSize.setValue(bundle.getInt(STACK_SIZE));
        model.uploaded.setValue(bundle.getBoolean(UPLOADED));
        model.downloaded.setValue(bundle.getBoolean(DOWNLOADED));
        action = bundle.getInt(ShopHelper.ITEM_ACTION);
    }

    @Override
    protected void onResume() {
        setTitle(title);
        setScreenSize(570, 350);
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
        bundle.putString(AUTHOR, model.author.getValue());
        bundle.putByteArray(IMAGE, model.imageRaw.getValue());
        bundle.putInt(STACK_SIZE, model.stackSize.getValue());
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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(imageChooserTitle);
        fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG", "*.png"));
        final File file = fileChooser
            .showOpenDialog(((Node) mouseEvent.getSource()).getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            final byte[] image = ImageUtils.readImage(file);
            model.imageRaw.set(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // endregion

    private static class WeaponMeleModel {

        final StringProperty id = new SimpleStringProperty();
        final StringProperty name = new SimpleStringProperty();
        final StringProperty description = new SimpleStringProperty();
        final Money price = new Money();
        final MaxActValue weight = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue strength = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue rampancy = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue defence = new MaxActValue(Integer.MAX_VALUE);
        final ObjectProperty<MeleWeaponClass> weaponClass = new SimpleObjectProperty<>(
            MeleWeaponClass.LIGHT);
        final ObjectProperty<MeleWeaponType> weaponType = new SimpleObjectProperty<>(
            MeleWeaponType.ONE_HAND);
        final StringProperty author = new SimpleStringProperty();
        final ObjectProperty<byte[]> imageRaw = new SimpleObjectProperty<>();
        final ObjectProperty<Image> image = new SimpleObjectProperty<>();
        final IntegerProperty stackSize = new SimpleIntegerProperty();
        final BooleanProperty uploaded = new SimpleBooleanProperty();
        final BooleanProperty downloaded = new SimpleBooleanProperty();

        {
            imageRaw.addListener((observable, oldValue, newValue) -> {
                final ByteArrayInputStream inputStream = new ByteArrayInputStream(newValue);
                image.set(new Image(inputStream));
            });
        }
    }


}
