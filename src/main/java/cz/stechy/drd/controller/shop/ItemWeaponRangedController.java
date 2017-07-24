package cz.stechy.drd.controller.shop;

import cz.stechy.drd.Money;
import cz.stechy.drd.R;
import cz.stechy.drd.controller.MoneyController;
import cz.stechy.drd.model.Context;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.item.RangedWeapon;
import cz.stechy.drd.model.item.RangedWeapon.RangedWeaponType;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.drd.util.ImageUtils;
import cz.stechy.drd.util.StringConvertors;
import cz.stechy.drd.util.Translator;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.imageio.ImageIO;

/**
 * Kontroler pro vytvoření nové zbraně na dálku
 */
public class ItemWeaponRangedController extends BaseController implements Initializable {

    // region Constants

    private static final int ACTION_MONEY = 1;

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String WEIGHT = "weight";
    private static final String PRICE = "price";
    private static final String STRENGTH = "strength";
    private static final String RAMPANCY = "rampancy";
    private static final String WEAPON_TYPE = "type";
    private static final String RANGE_LOW = "range_low";
    private static final String RANGE_MEDIUM = "range_medium";
    private static final String RANGE_LONG = "range_long";
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
    private TextField txtRangeLow;
    @FXML
    private TextField txtRangeMedium;
    @FXML
    private TextField txtRangeLong;
    @FXML
    private ComboBox<RangedWeaponType> cmbWeaponType;
    @FXML
    private TextField txtWeight;
    @FXML
    private Hyperlink lblPrice;
    @FXML
    private TextField txtStackSize;
    @FXML
    private ImageView imageView;
    // endregion

    private final WeaponRangedModel model = new WeaponRangedModel();
    private final Translator translator;

    private String title;
    private int action;
    private String imageChooserTitle;

    // endregion

    // region Public static methods

    public static RangedWeapon fromBundle(Bundle bundle) {
        return new RangedWeapon.Builder()
            .id(bundle.getString(ID))
            .name(bundle.getString(NAME))
            .description(bundle.getString(DESCRIPTION))
            .weight(bundle.getInt(WEIGHT))
            .price(bundle.getInt(PRICE))
            .strength(bundle.getInt(STRENGTH))
            .rampancy(bundle.getInt(RAMPANCY))
            .weaponType(bundle.getInt(WEAPON_TYPE))
            .rangeLow(bundle.getInt(RANGE_LOW))
            .rangeMedium(bundle.getInt(RANGE_MEDIUM))
            .rangeLong(bundle.getInt(RANGE_LONG))
            .author(bundle.getString(AUTHOR))
            .image(bundle.getByteArray(IMAGE))
            .stackSize(bundle.getInt(STACK_SIZE))
            .uploaded(bundle.getBoolean(UPLOADED))
            .downloaded(bundle.getBoolean(DOWNLOADED))
            .build();
    }

    public static void toBundle(Bundle bundle, RangedWeapon weapon) {
        bundle.putString(ID, weapon.getId());
        bundle.putString(NAME, weapon.getName());
        bundle.putString(DESCRIPTION, weapon.getDescription());
        bundle.putInt(WEIGHT, weapon.getWeight());
        bundle.putInt(PRICE, weapon.getPrice().getRaw());
        bundle.putInt(STRENGTH, weapon.getStrength());
        bundle.putInt(RAMPANCY, weapon.getRampancy());
        bundle.putInt(RANGE_LOW, weapon.getRangeLow());
        bundle.putInt(RANGE_MEDIUM, weapon.getRangeMedium());
        bundle.putInt(RANGE_LONG, weapon.getRangeLong());
        bundle.putInt(WEAPON_TYPE, weapon.getWeaponType().ordinal());
        bundle.putString(AUTHOR, weapon.getAuthor());
        bundle.putByteArray(IMAGE, weapon.getImage());
        bundle.putInt(STACK_SIZE, weapon.getStackSize());
        bundle.putBoolean(UPLOADED, weapon.isUploaded());
        bundle.putBoolean(DOWNLOADED, weapon.isDownloaded());
    }

    // endregion

    // region Constructors

    public ItemWeaponRangedController(Context context) {
        this.translator = context.getTranslator();
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title = resources.getString(R.Translate.ITEM_TYPE_WEAPON_RANGED);
        this.imageChooserTitle = resources.getString(R.Translate.ITEM_IMAGE_CHOOSE_DIALOG);

        txtName.textProperty().bindBidirectional(model.name);
        txtDescription.textProperty().bindBidirectional(model.description);

        cmbWeaponType.valueProperty().bindBidirectional(model.weaponType);
        cmbWeaponType.converterProperty()
            .setValue(StringConvertors.forRangedWeaponType(translator));

        FormUtils.initTextFormater(txtStrength, model.strength);
        FormUtils.initTextFormater(txtRampancy, model.rampancy);
        FormUtils.initTextFormater(txtRangeLow, model.rangeLow);
        FormUtils.initTextFormater(txtRangeMedium, model.rangeMedium);
        FormUtils.initTextFormater(txtRangeLong, model.rangeLong);
        FormUtils.initTextFormater(txtWeight, model.weight);
        lblPrice.textProperty().bind(model.price.text);
        FormUtils.initTextFormater(txtStackSize, model.stackSize);
        imageView.imageProperty().bindBidirectional(model.image);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        model.id.setValue(bundle.getString(ID));
        model.name.setValue(bundle.getString(NAME));
        model.description.setValue(bundle.getString(DESCRIPTION));
        model.weight.setActValue(bundle.getInt(WEIGHT));
        model.price.setRaw(bundle.getInt(PRICE));
        model.strength.setActValue(bundle.getInt(STRENGTH));
        model.rampancy.setActValue(bundle.getInt(RAMPANCY));
        model.weaponType.setValue(RangedWeaponType.valueOf(bundle.getInt(WEAPON_TYPE)));
        model.rangeLow.setActValue(bundle.getInt(RANGE_LOW));
        model.rangeMedium.setActValue(bundle.getInt(RANGE_MEDIUM));
        model.rangeLong.setActValue(bundle.getInt(RANGE_LONG));
        model.author.setValue(bundle.getString(AUTHOR));
        model.imageRaw.setValue(bundle.getByteArray(IMAGE));
        model.stackSize.setActValue(bundle.getInt(STACK_SIZE));
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

    // region Button handles

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
        bundle.putInt(RANGE_LOW, model.rangeLow.getActValue().intValue());
        bundle.putInt(RANGE_MEDIUM, model.rangeMedium.getActValue().intValue());
        bundle.putInt(RANGE_LONG, model.rangeLong.getActValue().intValue());
        bundle.putInt(WEAPON_TYPE, model.weaponType.getValue().ordinal());
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
            final byte[] resizedImage = ImageUtils.resizeImageRaw(image, 150, 150);
            model.imageRaw.set(resizedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // endregion

    private static class WeaponRangedModel {

        final StringProperty id = new SimpleStringProperty();
        final StringProperty name = new SimpleStringProperty();
        final StringProperty description = new SimpleStringProperty();
        final Money price = new Money();
        final MaxActValue weight = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue strength = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue rampancy = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue rangeLow = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue rangeMedium = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue rangeLong = new MaxActValue(Integer.MAX_VALUE);
        final ObjectProperty<RangedWeaponType> weaponType = new SimpleObjectProperty<>(
            RangedWeaponType.FIRE);
        final StringProperty author = new SimpleStringProperty();
        final ObjectProperty<byte[]> imageRaw = new SimpleObjectProperty<>();
        final ObjectProperty<Image> image = new SimpleObjectProperty<>();
        final MaxActValue stackSize = new MaxActValue(1, Integer.MAX_VALUE, 1);
        final BooleanProperty uploaded = new SimpleBooleanProperty();
        final BooleanProperty downloaded = new SimpleBooleanProperty();

        private boolean block = false;

        {
            imageRaw.addListener((observable, oldValue, newValue) -> {
                if (block) {
                    return;
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
                    BufferedImage bImage = SwingFXUtils.fromFXImage(newValue, null);
                    ByteArrayOutputStream s = new ByteArrayOutputStream();
                    ImageIO.write(bImage, "png", s);
                    byte[] res = s.toByteArray();
                    s.close(); //especially if you are using a different output stream.
                    imageRaw.setValue(res);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    block = false;
                }
            });
        }

    }
}
