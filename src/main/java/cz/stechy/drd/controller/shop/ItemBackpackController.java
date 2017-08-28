package cz.stechy.drd.controller.shop;

import cz.stechy.drd.R;
import cz.stechy.drd.controller.MoneyController;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.item.Backpack;
import cz.stechy.drd.model.item.Backpack.Size;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.drd.util.ImageUtils;
import cz.stechy.drd.util.StringConvertors;
import cz.stechy.drd.util.Translator;
import cz.stechy.drd.widget.EnumComboBox;
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
import javafx.beans.binding.Bindings;
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
 * Kontroler pro vytvoření nového standartního předmětu
 */
public class ItemBackpackController extends BaseController implements Initializable {

    // region Constants

    private static final int ACTION_MONEY = 1;

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String PRICE = "price";
    private static final String WEIGHT = "weight";
    private static final String MAX_LOAD = "max_load";
    private static final String SIZE = "size";
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
    private TextField txtWeight;
    @FXML
    private TextField txtMaxLoad;
    @FXML
    private EnumComboBox<Size> cmbSize;
    @FXML
    private Hyperlink lblPrice;
    @FXML
    private TextField txtStackSize;
    @FXML
    private Button btnFinish;
    @FXML
    private Label lblSelectImage;
    @FXML
    private ImageView imageView;

    // endregion

    private final Translator translator;
    private final ItemModel model = new ItemModel();
    private String titleNew;
    private String titleUpdate;
    private String imageChooserTitle;
    private int action;

    // endregion

    // region Constructors

    public ItemBackpackController(Translator translator) {
        this.translator = translator;
    }

    // endregion

    // region Public static methods

    public static Backpack fromBundle(Bundle bundle) {
        return new Backpack.Builder()
            .id(bundle.getString(ID))
            .name(bundle.getString(NAME))
            .description(bundle.getString(DESCRIPTION))
            .weight(bundle.getInt(WEIGHT))
            .price(bundle.getInt(PRICE))
            .maxLoad(bundle.getInt(MAX_LOAD))
            .size(bundle.getInt(SIZE))
            .author(bundle.getString(AUTHOR))
            .image(bundle.getByteArray(IMAGE))
            .stackSize(bundle.getInt(STACK_SIZE))
            .uploaded(bundle.getBoolean(UPLOADED))
            .downloaded(bundle.getBoolean(DOWNLOADED))
            .build();
    }

    public static void toBundle(Bundle bundle, Backpack backpack) {
        bundle.putString(ID, backpack.getId());
        bundle.putString(NAME, backpack.getName());
        bundle.putString(DESCRIPTION, backpack.getDescription());
        bundle.putInt(WEIGHT, backpack.getWeight());
        bundle.putInt(PRICE, backpack.getPrice().getRaw());
        bundle.putInt(MAX_LOAD, backpack.getMaxLoad());
        bundle.putInt(SIZE, backpack.getSize().ordinal());
        bundle.putString(AUTHOR, backpack.getAuthor());
        bundle.putByteArray(IMAGE, backpack.getImage());
        bundle.putInt(STACK_SIZE, backpack.getStackSize());
        bundle.putBoolean(UPLOADED, backpack.isUploaded());
        bundle.putBoolean(DOWNLOADED, backpack.isDownloaded());
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.titleNew = resources.getString(R.Translate.ITEM_NEW);
        this.titleUpdate = resources.getString(R.Translate.ITEM_UPDATE);
        this.imageChooserTitle = resources.getString(R.Translate.IMAGE_CHOOSE_DIALOG);

        txtName.textProperty().bindBidirectional(model.name);
        txtDescription.textProperty().bindBidirectional(model.description);
        FormUtils.initTextFormater(txtWeight, model.weight);
        FormUtils.initTextFormater(txtMaxLoad, model.maxLoad);

        cmbSize.converterProperty().setValue(StringConvertors.forBackpackSize(translator));
        cmbSize.valueProperty().bindBidirectional(model.size);

        lblPrice.textProperty().bind(model.price.text);

        FormUtils.initTextFormater(txtStackSize, model.stackSize);

        imageView.imageProperty().bindBidirectional(model.image);
        model.imageRaw.addListener((observable, oldValue, newValue) ->
            lblSelectImage.setVisible(Arrays.equals(newValue, new byte[0])));

        btnFinish.disableProperty()
            .bind(Bindings.or(model.name.isEmpty(), model.imageRaw.isNull()));
    }

    @Override
    protected void onCreate(Bundle bundle) {
        action = bundle.getInt(ShopHelper.ITEM_ACTION);
        lblTitle.setText(action == ShopHelper.ITEM_ACTION_ADD ? titleNew : titleUpdate);

        model.id.setValue(bundle.getString(ID));
        model.name.setValue(bundle.getString(NAME));
        model.description.setValue(bundle.getString(DESCRIPTION));
        model.price.setRaw(bundle.getInt(PRICE));
        model.weight.setActValue(bundle.getInt(WEIGHT));
        model.maxLoad.setActValue(bundle.getInt(MAX_LOAD));
        model.size.setValue(Size.values()[bundle.getInt(SIZE)]);
        model.author.setValue(bundle.getString(AUTHOR));
        model.imageRaw.setValue(bundle.getByteArray(IMAGE));
        model.stackSize.setActValue(bundle.getInt(STACK_SIZE));
        model.uploaded.setValue(bundle.getBoolean(UPLOADED));
        model.downloaded.setValue(bundle.getBoolean(DOWNLOADED));
    }

    @Override
    protected void onResume() {
        setTitle(action == ShopHelper.ITEM_ACTION_ADD ? titleNew : titleUpdate);
        setScreenSize(570, 450);
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

    // region Button handlers

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
        bundle.putInt(MAX_LOAD, model.maxLoad.getActValue().intValue());
        bundle.putInt(SIZE, model.size.getValue().ordinal());
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

    private static class ItemModel {

        final StringProperty id = new SimpleStringProperty();
        final StringProperty name = new SimpleStringProperty();
        final StringProperty description = new SimpleStringProperty();
        final Money price = new Money();
        final MaxActValue weight = new MaxActValue(Integer.MAX_VALUE);
        final MaxActValue maxLoad = new MaxActValue(Integer.MAX_VALUE);
        final StringProperty author = new SimpleStringProperty();
        final ObjectProperty<byte[]> imageRaw = new SimpleObjectProperty<>();
        final ObjectProperty<Image> image = new SimpleObjectProperty<>();
        final MaxActValue stackSize = new MaxActValue(1, Integer.MAX_VALUE, 1);
        final BooleanProperty uploaded = new SimpleBooleanProperty();
        final BooleanProperty downloaded = new SimpleBooleanProperty();
        final ObjectProperty<Size> size = new SimpleObjectProperty<>();

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
