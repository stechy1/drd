package cz.stechy.drd.controller.shop;

import cz.stechy.drd.Money;
import cz.stechy.drd.R;
import cz.stechy.drd.controller.MoneyController;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.item.GeneralItem;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.drd.util.ImageUtils;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Kontroler pro vytvoření nového standartního předmětu
 */
public class ItemGeneralController extends BaseController implements Initializable {

    // region Constants

    private static final int ACTION_MONEY = 1;

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String PRICE = "price";
    private static final String WEIGHT = "weight";
    private static final String AUTHOR = "author";
    private static final String IMAGE = "image";
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
    private TextField txtWeight;
    @FXML
    private Hyperlink lblPrice;
    @FXML
    private Button btnFinish;
    @FXML
    private ImageView imageView;

    // endregion

    private final ItemModel model = new ItemModel();
    private String title;
    private String imageChooserTitle;
    private int action;

    // endregion

    // region Public static methods

    public static GeneralItem fromBundle(Bundle bundle) {
        return new GeneralItem.Builder()
            .id(bundle.getString(ID))
            .name(bundle.getString(NAME))
            .description(bundle.getString(DESCRIPTION))
            .weight(bundle.getInt(WEIGHT))
            .price(bundle.getInt(PRICE))
            .author(bundle.getString(AUTHOR))
            .image(bundle.getByteArray(IMAGE))
            .uploaded(bundle.getBoolean(UPLOADED))
            .downloaded(bundle.getBoolean(DOWNLOADED))
            .build();
    }

    public static void toBundle(Bundle bundle, GeneralItem weapon) {
        bundle.putString(ID, weapon.getId());
        bundle.putString(NAME, weapon.getName());
        bundle.putString(DESCRIPTION, weapon.getDescription());
        bundle.putInt(WEIGHT, weapon.getWeight());
        bundle.putInt(PRICE, weapon.getPrice().getRaw());
        bundle.putString(AUTHOR, weapon.getAuthor());
        bundle.putByteArray(IMAGE, weapon.getImage());
        bundle.putBoolean(UPLOADED, weapon.isUploaded());
        bundle.putBoolean(DOWNLOADED, weapon.isDownloaded());
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title = resources.getString(R.Translate.ITEM_TYPE_GENERAL);
        this.imageChooserTitle = resources.getString(R.Translate.ITEM_IMAGE_CHOOSE_DIALOG);

        txtName.textProperty().bindBidirectional(model.name);
        txtDescription.textProperty().bindBidirectional(model.description);
        FormUtils.initTextFormater(txtWeight, model.weight);

        lblPrice.textProperty().bind(model.price.text);
        imageView.imageProperty().bindBidirectional(model.image);

        btnFinish.disableProperty()
            .bind(Bindings.or(model.name.isEmpty(), model.imageRaw.isNull()));
    }

    @Override
    protected void onCreate(Bundle bundle) {
        model.id.setValue(bundle.getString(ID));
        model.name.setValue(bundle.getString(NAME));
        model.description.setValue(bundle.getString(DESCRIPTION));
        model.price.setRaw(bundle.getInt(PRICE));
        model.weight.setActValue(bundle.getInt(WEIGHT));
        model.author.setValue(bundle.getString(AUTHOR));
        model.imageRaw.setValue(bundle.getByteArray(IMAGE));
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
        bundle.putString(AUTHOR, model.author.getValue());
        bundle.putByteArray(IMAGE, model.imageRaw.getValue());
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

    private static class ItemModel {

        final StringProperty id = new SimpleStringProperty();
        final StringProperty name = new SimpleStringProperty();
        final StringProperty description = new SimpleStringProperty();
        final Money price = new Money();
        final MaxActValue weight = new MaxActValue(Integer.MAX_VALUE);
        final StringProperty author = new SimpleStringProperty();
        final ObjectProperty<byte[]> imageRaw = new SimpleObjectProperty<>();
        final ObjectProperty<Image> image = new SimpleObjectProperty<>();
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
