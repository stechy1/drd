package cz.stechy.drd.controller.shop;

import cz.stechy.drd.R;
import cz.stechy.drd.controller.MoneyController;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.ValidatedModel;
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
import java.util.Arrays;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
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
    private static final String RENOWN = "renown";
    private static final String AUTHOR = "author";
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
    private TextField txtRangeLow;
    @FXML
    private TextField txtRangeMedium;
    @FXML
    private TextField txtRangeLong;
    @FXML
    private ComboBox<RangedWeaponType> cmbWeaponType;
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

    private final WeaponRangedModel model = new WeaponRangedModel();
    private final Translator translator;

    private String titleNew;
    private String titleUpdate;
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
            .renown(bundle.getInt(RENOWN))
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
        bundle.putInt(RENOWN, weapon.getRenown());
        bundle.putByteArray(IMAGE, weapon.getImage());
        bundle.putInt(STACK_SIZE, weapon.getStackSize());
        bundle.putBoolean(UPLOADED, weapon.isUploaded());
        bundle.putBoolean(DOWNLOADED, weapon.isDownloaded());
    }

    // endregion

    // region Constructors

    public ItemWeaponRangedController(Translator translator) {
        this.translator = translator;
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
        cmbWeaponType.converterProperty()
            .setValue(StringConvertors.forRangedWeaponType(translator));

        FormUtils.initTextFormater(txtStrength, model.strength);
        FormUtils.initTextFormater(txtRampancy, model.rampancy);
        FormUtils.initTextFormater(txtRangeLow, model.rangeLow);
        FormUtils.initTextFormater(txtRangeMedium, model.rangeMedium);
        FormUtils.initTextFormater(txtRangeLong, model.rangeLong);
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
            model.weight.setActValue(bundle.getInt(WEIGHT));
            model.price.setRaw(bundle.getInt(PRICE));
            model.strength.setActValue(bundle.getInt(STRENGTH));
            model.rampancy.setActValue(bundle.getInt(RAMPANCY));
            model.weaponType.setValue(RangedWeaponType.valueOf(bundle.getInt(WEAPON_TYPE)));
            model.rangeLow.setActValue(bundle.getInt(RANGE_LOW));
            model.rangeMedium.setActValue(bundle.getInt(RANGE_MEDIUM));
            model.rangeLong.setActValue(bundle.getInt(RANGE_LONG));
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
        bundle.putInt(RENOWN, model.renown.getActValue().intValue());
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

    private static class WeaponRangedModel extends ValidatedModel {

        private static final int FLAG_NAME = 1 << 0;
        private static final int FLAG_WEIGHT = 1 << 1;
        private static final int FLAG_STRENGTH = 1 << 2;
        private static final int FLAG_RAMPANCY = 1 << 3;
        private static final int FLAG_RANGE_LOW = 1 << 4;
        private static final int FLAG_RANGE_MEDIUM = 1 << 5;
        private static final int FLAG_RANGE_LONG = 1 << 6;
        private static final int FLAG_WEAPON_TYPE = 1 << 7;
        private static final int FLAG_RENOWN = 1 << 8;
        private static final int FLAG_IMAGE = 1 << 9;
        private static final int FLAG_STACK_SIZE = 1 << 10;

        final StringProperty id = new SimpleStringProperty(this, "id", null);
        final StringProperty name = new SimpleStringProperty(this, "name", null);
        final StringProperty description = new SimpleStringProperty(this, "description", null);
        final Money price = new Money();
        final MaxActValue weight = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue strength = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue rampancy = new MaxActValue(Integer.MIN_VALUE ,Integer.MAX_VALUE, 0);
        final MaxActValue rangeLow = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue rangeMedium = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue rangeLong = new MaxActValue(Integer.MAX_VALUE);
        final ObjectProperty<RangedWeaponType> weaponType = new SimpleObjectProperty<>(this, "weaponType", null);
        final StringProperty author = new SimpleStringProperty(this, "author", null);
        final MaxActValue renown = new MaxActValue(Integer.MAX_VALUE);
        final ObjectProperty<byte[]> imageRaw = new SimpleObjectProperty<>(this, "imageRaw");
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
            rangeLow.actValueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_RANGE_LOW, true);
                } else {
                    setValidityFlag(FLAG_RANGE_LOW, false);
                }
            });
            rangeMedium.actValueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_RANGE_MEDIUM, true);
                } else {
                    setValidityFlag(FLAG_RANGE_MEDIUM, false);
                }
            });
            rangeLong.actValueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    setValid(false);
                    setValidityFlag(FLAG_RANGE_LONG, true);
                } else {
                    setValidityFlag(FLAG_RANGE_LONG, false);
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

            validityFlag.set(FLAG_NAME + FLAG_WEAPON_TYPE + FLAG_IMAGE);
            setValid(false);
        }

    }
}
