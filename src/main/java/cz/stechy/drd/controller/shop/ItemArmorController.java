package cz.stechy.drd.controller.shop;

import cz.stechy.drd.R;
import cz.stechy.drd.controller.MoneyController;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.item.Armor;
import cz.stechy.drd.model.item.Armor.ArmorType;
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

    // endregion

    private final ArmorModel model = new ArmorModel();
    private final Translator translator;
    private String titleNew;
    private String titleUpdate;
    private int action;
    private String imageChooserTitle;

    // endregion

    // region Constructors

    public ItemArmorController(Translator translator) {
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

        cmbType.converterProperty().setValue(StringConvertors.forArmorType(translator));

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
    }

    @Override
    protected void onCreate(Bundle bundle) {
        action = bundle.getInt(ShopHelper.ITEM_ACTION);
        lblTitle.setText(action == ShopHelper.ITEM_ACTION_ADD ? titleNew : titleUpdate);

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

    @Override
    protected void onResume() {
        setTitle(action == ShopHelper.ITEM_ACTION_ADD ? titleNew : titleUpdate);
        setScreenSize(570, 550);
    }

    @Override
    protected void onScreenResult(int statusCode, int actionId, Bundle bundle) {
        switch (actionId) {
            case ACTION_MONEY_A:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }
                model.priceA.setRaw(bundle.getInt(MoneyController.MONEY));

                break;
            case ACTION_MONEY_B:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }
                model.priceB.setRaw(bundle.getInt(MoneyController.MONEY));

                break;
            case ACTION_MONEY_C:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }
                model.priceC.setRaw(bundle.getInt(MoneyController.MONEY));

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
        startNewPopupWindowForResult("money", ACTION_MONEY_A, bundle, (Node) actionEvent.getSource());
    }

    @FXML
    private void handleShowMoneyBPopup(ActionEvent actionEvent) {
        Bundle bundle = new Bundle().put(MoneyController.MONEY, model.priceB.getRaw());
        startNewPopupWindowForResult("money", ACTION_MONEY_B, bundle, (Node) actionEvent.getSource());
    }

    @FXML
    private void handleShowMoneyCPopup(ActionEvent actionEvent) {
        Bundle bundle = new Bundle().put(MoneyController.MONEY, model.priceC.getRaw());
        startNewPopupWindowForResult("money", ACTION_MONEY_C,  bundle, (Node) actionEvent.getSource());
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

    private static class ArmorModel {

        final StringProperty id = new SimpleStringProperty();
        final StringProperty name = new SimpleStringProperty();
        final StringProperty description = new SimpleStringProperty();
        final MaxActValue defence = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue minimumStrength = new MaxActValue(Integer.MAX_VALUE);
        final ObjectProperty<ArmorType> type = new SimpleObjectProperty<>();
        final MaxActValue weightA = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue weightB = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue weightC = new MaxActValue(Integer.MAX_VALUE);
        final Money priceA = new Money();
        final Money priceB = new Money();
        final Money priceC = new Money();
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
