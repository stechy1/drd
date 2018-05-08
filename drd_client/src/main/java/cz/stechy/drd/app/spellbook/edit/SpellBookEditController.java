package cz.stechy.drd.app.spellbook.edit;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import cz.stechy.drd.R;
import cz.stechy.drd.app.DrDTimeController;
import cz.stechy.drd.app.bestiary.BestiaryHelper;
import cz.stechy.drd.app.spellbook.SpellBookHelper;
import cz.stechy.drd.model.DrDTime;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.SpellParser;
import cz.stechy.drd.model.ValidatedModel;
import cz.stechy.drd.model.spell.Spell.SpellProfessionType;
import cz.stechy.drd.model.spell.Spell.SpellTarget;
import cz.stechy.drd.model.spell.price.BasicSpellPrice;
import cz.stechy.drd.model.spell.price.ISpellPrice;
import cz.stechy.drd.util.DialogUtils;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.drd.util.Translator;
import cz.stechy.drd.util.Translator.Key;
import cz.stechy.drd.widget.DrDTimeWidget;
import cz.stechy.drd.widget.EnumComboBox;
import cz.stechy.drd.widget.SpellPriceWidget;
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
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class SpellBookEditController extends BaseController implements Initializable {

    // region Constants

    private static final int ACTION_DURATION = 5;
    private static final int ACTION_CAST_TIME = 6;

    // endregion

    // region Variables

    // region FXML

    @FXML
    private Label lblTitle;

    @FXML
    private JFXTextField txtName;
    @FXML
    private JFXTextField txtMagicName;
    @FXML
    private EnumComboBox<SpellProfessionType> cmbType;
    @FXML
    private SpellPriceWidget widgetSpellPrice;
    @FXML
    private EnumComboBox<SpellTarget> cmbTarget;
    @FXML
    private JFXTextField txtRadius;
    @FXML
    private JFXTextField txtRange;
    @FXML
    private DrDTimeWidget widgetCastTime;
    @FXML
    private DrDTimeWidget widgetDuration;
    @FXML
    private ImageView imageView;
    @FXML
    private Label lblSelectImage;
    @FXML
    private JFXTextArea txtDescription;

    @FXML
    private Button btnFinish;

    // endregion

    private final SpellModel model = new SpellModel();
    private final Translator translator;

    private String titleNew;
    private String titleUpdate;

    private int action;
    private String imageChooserTitle;

    // endregion

    // region Constructors

    public SpellBookEditController(Translator translator) {
        this.translator = translator;
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleNew = resources.getString(R.Translate.SPELL_BOOK_EDIT_NEW_TITLE);
        titleUpdate = resources.getString(R.Translate.SPELL_BOOK_EDIT_UPDATE_TITLE);
        this.imageChooserTitle = resources.getString(R.Translate.IMAGE_CHOOSE_DIALOG);

        cmbType.converterProperty().setValue(translator.getConvertor(Key.SPELL_PROFESSION_TYPES));
        cmbTarget.converterProperty().setValue(translator.getConvertor(Key.SPELL_TARGET_TYPES));

        txtName.textProperty().bindBidirectional(model.name);
        txtMagicName.textProperty().bindBidirectional(model.magicName);
        txtDescription.textProperty().bindBidirectional(model.description);

        FormUtils.initTextFormater(txtRadius, model.radius);
        FormUtils.initTextFormater(txtRange, model.range);
        //FormUtils.initTextFormater(txtCastTime, model.castTime);

        cmbType.valueProperty().bindBidirectional(model.type);
        cmbTarget.valueProperty().bindBidirectional(model.target);

        widgetSpellPrice.bind(model.price, translator);
        widgetSpellPrice.setOnMouseClicked(this::handlePrice);

        imageView.imageProperty().bindBidirectional(model.image);
        model.imageRaw.addListener((observable, oldValue, newValue) ->
            lblSelectImage.setVisible(Arrays.equals(newValue, new byte[0])));

        widgetCastTime.bind(model.castTime.get(), translator);
        widgetCastTime.setOnMouseClicked(this::handleCastTime);

        widgetDuration.bind(model.duration.get(), translator);
        widgetDuration.setOnMouseClicked(this::handleDuration);

        btnFinish.disableProperty().bind(model.validProperty().not());
    }

    @Override
    protected void onCreate(Bundle bundle) {
        action = bundle.getInt(SpellBookHelper.SPELL_ACTION);
        if (action == BestiaryHelper.MOB_ACTION_UPDATE) {
            model.id.setValue(bundle.getString(SpellBookHelper.ID));
            model.author.setValue(bundle.getString(SpellBookHelper.AUTHOR));
            model.name.setValue(bundle.getString(SpellBookHelper.NAME));
            model.magicName.setValue(bundle.getString(SpellBookHelper.MAGIC_NAME));
            model.description.setValue(bundle.getString(SpellBookHelper.DESCRIPTION));
            model.type.setValue(
                SpellProfessionType.values()[bundle.getInt(SpellBookHelper.PROFESSION_TYPE)]);
            model.price.setValue(new SpellParser(bundle.getString(SpellBookHelper.PRICE)).parse());
            model.radius.setActValue(bundle.getInt(SpellBookHelper.RADIUS));
            model.range.setActValue(bundle.getInt(SpellBookHelper.RANGE));
            model.target.setValue(SpellTarget.values()[bundle.getInt(SpellBookHelper.TARGET)]);
            model.castTime.get().setRaw(bundle.getInt(SpellBookHelper.CAST_TIME));
            model.duration.get().setRaw(bundle.getInt(SpellBookHelper.DURATION));
            model.imageRaw.setValue(bundle.get(SpellBookHelper.IMAGE));
            model.uploaded.setValue(bundle.getBoolean(SpellBookHelper.UPLOADED));
            model.downloaded.setValue(bundle.getBoolean(SpellBookHelper.DOWNLOADED));
        }
    }

    @Override
    protected void onResume() {
        final String title = action == SpellBookHelper.SPELL_ACTION_ADD ? titleNew : titleUpdate;
        setTitle(title);
        lblTitle.setText(title);
        setScreenSize(550, 500);
    }

    @Override
    protected void onScreenResult(int statusCode, int actionId, Bundle bundle) {
        ISpellPrice spellPrice;
        switch (actionId) {
            case SpellBookHelper.SPELL_PRICE_ACTION_UPDATE:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }

                spellPrice = new SpellParser(bundle.getString(SpellBookHelper.PRICE))
                    .parse();

                this.model.price.set(spellPrice);
                break;
            case ACTION_DURATION:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }

                this.model.duration.get().setRaw(bundle.getInt(DrDTimeController.TIME));
                break;
            case ACTION_CAST_TIME:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }

                this.model.castTime.get().setRaw(bundle.getInt(DrDTimeController.TIME));
                break;
        }
    }

    // region Button handlers

    @FXML
    private void handleFinish(ActionEvent actionEvent) {
        setResult(RESULT_SUCCESS);
        final Bundle bundle = new Bundle();
        bundle.putInt(SpellBookHelper.SPELL_ACTION, action);
        bundle.putString(SpellBookHelper.ID, model.id.getValue());
        bundle.putString(SpellBookHelper.AUTHOR, model.author.getValue());
        bundle.putString(SpellBookHelper.NAME, model.name.getValue());
        bundle.putString(SpellBookHelper.MAGIC_NAME, model.magicName.getValue());
        bundle.putString(SpellBookHelper.DESCRIPTION, model.description.getValue());
        bundle.putInt(SpellBookHelper.PROFESSION_TYPE, model.type.getValue().ordinal());
        bundle.putString(SpellBookHelper.PRICE, model.price.get().pack());
        bundle.putInt(SpellBookHelper.RADIUS, model.radius.getActValue().intValue());
        bundle.putInt(SpellBookHelper.RANGE, model.range.getActValue().intValue());
        bundle.putInt(SpellBookHelper.TARGET, model.target.getValue().ordinal());
        bundle.putInt(SpellBookHelper.CAST_TIME, model.castTime.get().getRaw());
        bundle.putInt(SpellBookHelper.DURATION, model.duration.get().getRaw());
        bundle.put(SpellBookHelper.IMAGE, model.imageRaw.getValue());
        bundle.putBoolean(SpellBookHelper.UPLOADED, model.uploaded.getValue());
        bundle.putBoolean(SpellBookHelper.DOWNLOADED, model.downloaded.getValue());

        finish(bundle);
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

    @FXML
    private void handlePrice(MouseEvent mouseEvent) {
        Bundle bundle = new Bundle();
        String price = "";
        if (model.price.get() != null) {
            price = model.price.get().pack();
        }
        bundle.putString(SpellBookHelper.PRICE, price);
        startNewDialogForResult(R.FXML.SPELL_PRICE_EDITOR,
            SpellBookHelper.SPELL_PRICE_ACTION_UPDATE, bundle);
    }

    @FXML
    private void handleCastTime(MouseEvent mouseEvent) {
        Bundle bundle = new Bundle();
        bundle.putInt(DrDTimeController.TIME, model.castTime.get().getRaw());
        startNewPopupWindowForResult(R.FXML.TIME, ACTION_CAST_TIME, bundle,
            (Node) mouseEvent.getSource());
    }

    @FXML
    private void handleDuration(MouseEvent mouseEvent) {
        Bundle bundle = new Bundle();
        bundle.putInt(DrDTimeController.TIME, model.duration.get().getRaw());
        startNewPopupWindowForResult(R.FXML.TIME, ACTION_DURATION, bundle,
            (Node) mouseEvent.getSource());
    }

    // endregion

    private static final class SpellModel extends ValidatedModel {

        private static final int FLAG_IMAGE = 1 << 1;

        final StringProperty id = new SimpleStringProperty(this, "id", null);
        final StringProperty author = new SimpleStringProperty(this, "author", null);
        final StringProperty name = new SimpleStringProperty(this, "name", null);
        final StringProperty magicName = new SimpleStringProperty(this, "magicName", null);
        final StringProperty description = new SimpleStringProperty(this, "description", null);
        final ObjectProperty<SpellProfessionType> type = new SimpleObjectProperty<>(this, "type",
            null);
        final ObjectProperty<ISpellPrice> price = new SimpleObjectProperty<>(this, "price",
            new BasicSpellPrice(1));
        final MaxActValue radius = new MaxActValue(-1, Integer.MAX_VALUE, 0);
        final MaxActValue range = new MaxActValue(Integer.MAX_VALUE);
        final ObjectProperty<SpellTarget> target = new SimpleObjectProperty<>(this, "target", null);
        final ObjectProperty<DrDTime> castTime = new SimpleObjectProperty<>(this, "castTime",
            new DrDTime());
        final ObjectProperty<DrDTime> duration = new SimpleObjectProperty<>(this, "duration",
            new DrDTime());
        final ObjectProperty<byte[]> imageRaw = new SimpleObjectProperty<>(this, "imageRaw");
        final ObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");
        final BooleanProperty uploaded = new SimpleBooleanProperty(this, "uploaded");
        final BooleanProperty downloaded = new SimpleBooleanProperty(this, "downloaded");

        private final AtomicBoolean b = new AtomicBoolean(false);

        {
            imageRaw.addListener(FormUtils.notEmptyImageRawCondition(this, FLAG_IMAGE, image, b));
            image.addListener(FormUtils.notEmptyImageSetter(imageRaw, b));

            validityFlag.set(ValidatedModel.VALID_FLAG_VALUE);
        }
    }
}
