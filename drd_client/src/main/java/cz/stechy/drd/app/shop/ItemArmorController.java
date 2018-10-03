package cz.stechy.drd.app.shop;

import cz.stechy.drd.R;
import cz.stechy.drd.app.MoneyController;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.ValidatedModel;
import cz.stechy.drd.model.item.Armor;
import cz.stechy.drd.model.item.Armor.ArmorType;
import cz.stechy.drd.util.DialogUtils;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.drd.service.translator.TranslatorService;
import cz.stechy.drd.service.translator.TranslatorService.Key;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
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
 * Kontroler pro vytvoření nového brnění
 */
public class ItemArmorController extends BaseController implements Initializable {

    // region Constants

    private static final int ACTION_MONEY_A = 1;
    private static final int ACTION_MONEY_B = 2;
    private static final int ACTION_MONEY_C = 3;

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String DEFENCE = "defence";
    private static final String MINIMUM_STRENGTH = "minimum_strength";
    private static final String TYPE = "type";
    private static final String WEIGHT_A = "weight_a";
    private static final String WEIGHT_B = "weight_b";
    private static final String WEIGHT_C = "weight_c";
    private static final String PRICE_A = "price_a";
    private static final String PRICE_B = "price_b";
    private static final String PRICE_C = "price_c";
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
    private TextField txtDefenceNumber;
    @FXML
    private TextField txtMiniumStrength;
    @FXML
    private ComboBox<ArmorType> cmbType;
    @FXML
    private TextField txtWeightA;
    @FXML
    private TextField txtWeightB;
    @FXML
    private TextField txtWeightC;
    @FXML
    private Hyperlink lblPriceA;
    @FXML
    private Hyperlink lblPriceB;
    @FXML
    private Hyperlink lblPriceC;
    @FXML
    private TextField txtStackSize;
    @FXML
    private ImageView imageView;
    @FXML
    private Label lblSelectImage;
    @FXML
    private Button btnFinish;

    // endregion

    private final ArmorModel model = new ArmorModel();
    private final TranslatorService translator;
    private String titleNew;
    private String titleUpdate;
    private int action;
    private String imageChooserTitle;

    // endregion

    // region Constructors

    public ItemArmorController(TranslatorService translator) {
        this.translator = translator;
    }

    // endregion

    // region Public static methods

    public static Armor fromBundle(Bundle bundle) {
        return new Armor.Builder()
            .id(bundle.getString(ID))
            .name(bundle.getString(NAME))
            .description(bundle.getString(DESCRIPTION))
            .defenceNumber(bundle.getInt(DEFENCE))
            .minimumStrength(bundle.getInt(MINIMUM_STRENGTH))
            .type(bundle.getInt(TYPE))
            .weightA(bundle.getInt(WEIGHT_A))
            .weightB(bundle.getInt(WEIGHT_B))
            .weightC(bundle.getInt(WEIGHT_C))
            .priceA(bundle.getInt(PRICE_A))
            .priceB(bundle.getInt(PRICE_B))
            .priceC(bundle.getInt(PRICE_C))
            .author(bundle.getString(AUTHOR))
            .image(bundle.getByteArray(IMAGE))
            .stackSize(bundle.getInt(STACK_SIZE))
            .uploaded(bundle.getBoolean(UPLOADED))
            .downloaded(bundle.getBoolean(DOWNLOADED))
            .build();
    }

    public static void toBundle(Bundle bundle, Armor armor) {
        bundle.putString(ID, armor.getId());
        bundle.putString(NAME, armor.getName());
        bundle.putString(DESCRIPTION, armor.getDescription());
        bundle.putString(AUTHOR, armor.getAuthor());
        bundle.putInt(DEFENCE, armor.getDefenceNumber());
        bundle.putInt(MINIMUM_STRENGTH, armor.getMinimumStrength());
        bundle.putInt(TYPE, armor.getType().ordinal());
        bundle.putInt(WEIGHT_A, armor.getWeightA());
        bundle.putInt(WEIGHT_B, armor.getWeightB());
        bundle.putInt(WEIGHT_C, armor.getWeightC());
        bundle.putInt(PRICE_A, armor.getPriceA().getRaw());
        bundle.putInt(PRICE_B, armor.getPriceB().getRaw());
        bundle.putInt(PRICE_C, armor.getPriceC().getRaw());
        bundle.putByteArray(IMAGE, armor.getImage());
        bundle.putInt(STACK_SIZE, armor.getStackSize());
        bundle.putBoolean(UPLOADED, armor.isUploaded());
        bundle.putBoolean(DOWNLOADED, armor.isDownloaded());
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.titleNew = resources.getString(R.Translate.ITEM_NEW);
        this.titleUpdate = resources.getString(R.Translate.ITEM_UPDATE);
        this.imageChooserTitle = resources.getString(R.Translate.IMAGE_CHOOSE_DIALOG);

        cmbType.converterProperty().setValue(translator.getConvertor(Key.ARMOR_TYPES));

        txtName.textProperty().bindBidirectional(model.name);
        txtDescription.textProperty().bindBidirectional(model.description);

        FormUtils.initTextFormater(txtDefenceNumber, model.defence);
        FormUtils.initTextFormater(txtMiniumStrength, model.minimumStrength);
        FormUtils.initTextFormater(txtWeightA, model.weightA);
        FormUtils.initTextFormater(txtWeightB, model.weightB);
        FormUtils.initTextFormater(txtWeightC, model.weightC);

        cmbType.valueProperty().bindBidirectional(model.type);

        lblPriceA.textProperty().bind(model.priceA.text);
        lblPriceB.textProperty().bind(model.priceB.text);
        lblPriceC.textProperty().bind(model.priceC.text);

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
            model.defence.setActValue(bundle.getInt(DEFENCE));
            model.minimumStrength.setActValue(bundle.getInt(MINIMUM_STRENGTH));
            model.type.setValue(ArmorType.values()[bundle.getInt(TYPE)]);
            model.weightA.setActValue(bundle.getInt(WEIGHT_A));
            model.weightB.setActValue(bundle.getInt(WEIGHT_B));
            model.weightC.setActValue(bundle.getInt(WEIGHT_C));
            model.priceA.setRaw(bundle.getInt(PRICE_A));
            model.priceB.setRaw(bundle.getInt(PRICE_B));
            model.priceC.setRaw(bundle.getInt(PRICE_C));
            model.author.setValue(bundle.getString(AUTHOR));
            model.imageRaw.setValue(bundle.getByteArray(IMAGE));
            model.stackSize.setActValue(bundle.getInt(STACK_SIZE));
            model.uploaded.setValue(bundle.getBoolean(UPLOADED));
            model.downloaded.setValue(bundle.getBoolean(DOWNLOADED));
        }
    }

    @Override
    protected void onResume() {
        setTitle(action == ShopHelper.ITEM_ACTION_ADD ? titleNew : titleUpdate);
        setScreenSize(570, 550);
    }

    @Override
    protected void onScreenResult(int statusCode, int actionId, Bundle bundle) {
        ShopHelper.setItemPrice(statusCode, actionId, ACTION_MONEY_A, bundle, model.priceA);
        ShopHelper.setItemPrice(statusCode, actionId, ACTION_MONEY_B, bundle, model.priceB);
        ShopHelper.setItemPrice(statusCode, actionId, ACTION_MONEY_C, bundle, model.priceC);
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
        bundle.putInt(DEFENCE, model.defence.getActValue().intValue());
        bundle.putInt(MINIMUM_STRENGTH, model.minimumStrength.getActValue().intValue());
        bundle.putInt(TYPE, model.type.getValue().ordinal());
        bundle.putInt(WEIGHT_A, model.weightA.getActValue().intValue());
        bundle.putInt(WEIGHT_B, model.weightB.getActValue().intValue());
        bundle.putInt(WEIGHT_C, model.weightC.getActValue().intValue());
        bundle.putInt(PRICE_A, model.priceA.getRaw());
        bundle.putInt(PRICE_B, model.priceB.getRaw());
        bundle.putInt(PRICE_C, model.priceC.getRaw());
        bundle.putString(AUTHOR, model.author.getValue());
        bundle.putByteArray(IMAGE, model.imageRaw.getValue());
        bundle.putInt(STACK_SIZE, model.stackSize.getActValue().intValue());
        bundle.putBoolean(UPLOADED, model.uploaded.getValue());
        bundle.putBoolean(DOWNLOADED, model.downloaded.getValue());
        finish(bundle);
    }

    @FXML
    private void handleShowMoneyAPopup(ActionEvent actionEvent) {
        Bundle bundle = new Bundle().put(MoneyController.MONEY, model.priceA.getRaw());
        startNewPopupWindowForResult("money", ACTION_MONEY_A, bundle,
            (Node) actionEvent.getSource());
    }

    @FXML
    private void handleShowMoneyBPopup(ActionEvent actionEvent) {
        Bundle bundle = new Bundle().put(MoneyController.MONEY, model.priceB.getRaw());
        startNewPopupWindowForResult("money", ACTION_MONEY_B, bundle,
            (Node) actionEvent.getSource());
    }

    @FXML
    private void handleShowMoneyCPopup(ActionEvent actionEvent) {
        Bundle bundle = new Bundle().put(MoneyController.MONEY, model.priceC.getRaw());
        startNewPopupWindowForResult("money", ACTION_MONEY_C, bundle,
            (Node) actionEvent.getSource());
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

    private static class ArmorModel extends ValidatedModel {

        private static final int FLAG_NAME = 1 << 0;
        private static final int FLAG_DESCRIPTION = 1 << 1;
        private static final int FLAG_DEFENCE = 1 << 2;
        private static final int FLAG_MINIMUM_STRENGTH = 1 << 2;
        private static final int FLAG_TYPE = 1 << 4;
        private static final int FLAG_WEIGHT_A = 1 << 5;
        private static final int FLAG_WEIGHT_B = 1 << 6;
        private static final int FLAG_WEIGHT_C = 1 << 7;
        private static final int FLAG_IMAGE = 1 << 8;
        private static final int FLAG_STACK_SIZE = 1 << 9;

        final StringProperty id = new SimpleStringProperty(this, "id", null);
        final StringProperty name = new SimpleStringProperty(this, "name", null);
        final StringProperty description = new SimpleStringProperty(this, "description", null);
        final MaxActValue defence = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue minimumStrength = new MaxActValue(Integer.MAX_VALUE);
        final ObjectProperty<ArmorType> type = new SimpleObjectProperty<>(this, "type", null);
        final MaxActValue weightA = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue weightB = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue weightC = new MaxActValue(Integer.MAX_VALUE);
        final Money priceA = new Money();
        final Money priceB = new Money();
        final Money priceC = new Money();
        final StringProperty author = new SimpleStringProperty(this, "author", null);
        final ObjectProperty<byte[]> imageRaw = new SimpleObjectProperty<>(this, "imageRaw");
        final ObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");
        final MaxActValue stackSize = new MaxActValue(1, Integer.MAX_VALUE, 1);
        final BooleanProperty uploaded = new SimpleBooleanProperty(this, "uploaded");
        final BooleanProperty downloaded = new SimpleBooleanProperty(this, "downloaded");

        private final AtomicBoolean b = new AtomicBoolean(false);

        {
            imageRaw.addListener(FormUtils.notEmptyImageRawCondition(this, FLAG_IMAGE, image, b));
            image.addListener(FormUtils.notEmptyImageSetter(imageRaw, b));

            name.addListener(FormUtils.notEmptyCondition(this, FLAG_NAME));
            description.addListener(FormUtils.notEmptyCondition(this, FLAG_DESCRIPTION));
            defence.actValueProperty().addListener(FormUtils.notEmptyCondition(this, FLAG_DEFENCE));
            minimumStrength.actValueProperty()
                .addListener(FormUtils.notEmptyCondition(this, FLAG_MINIMUM_STRENGTH));
            type.addListener(FormUtils.notEmptyCondition(this, FLAG_TYPE));
            weightA.actValueProperty()
                .addListener(FormUtils.notEmptyCondition(this, FLAG_WEIGHT_A));
            weightB.actValueProperty()
                .addListener(FormUtils.notEmptyCondition(this, FLAG_WEIGHT_B));
            weightC.actValueProperty()
                .addListener(FormUtils.notEmptyCondition(this, FLAG_WEIGHT_C));
            stackSize.actValueProperty()
                .addListener(FormUtils.notEmptyCondition(this, FLAG_STACK_SIZE));

            validityFlag.set(FLAG_NAME + FLAG_TYPE + FLAG_IMAGE);
        }
    }
}
