package cz.stechy.drd.controller.bestiary.edit;

import cz.stechy.drd.R;
import cz.stechy.drd.controller.bestiary.BestiaryHelper;
import cz.stechy.drd.util.DialogUtils;
import cz.stechy.screens.Bundle;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;

/**
 * Kontroler pro editaci obrázku nestvůry
 */
public class BestiaryEditImageController implements IEditController, Initializable {

    // region Variables

    // region FXML

    public StackPane container;

    @FXML
    private Label lblSelectImage;

    // endregion

    private final BooleanProperty valid = new SimpleBooleanProperty(this, "valid");
    private final ObjectProperty<byte[]> imageRaw = new SimpleObjectProperty<>(this, "rawImage");

    private String imageChooserTitle;

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.imageChooserTitle = resources.getString(R.Translate.IMAGE_CHOOSE_DIALOG);
        imageRaw.addListener((observable, oldValue, newValue) -> {
            valid.set(!(newValue == null || Arrays.equals(newValue, new byte[0])));

            final ByteArrayInputStream inputStream = new ByteArrayInputStream(newValue);
            container.setBackground(new Background(new BackgroundImage(
                new Image(inputStream),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT)));
            lblSelectImage.setVisible(newValue == null);
        });
    }

    @Override
    public void loadMobPropertiesFromBundle(Bundle bundle) {
        imageRaw.setValue(bundle.get(BestiaryHelper.IMAGE));
    }

    @Override
    public void saveMobPropertiesToBundle(Bundle bundle) {
        bundle.put(BestiaryHelper.IMAGE, imageRaw.getValue());
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid;
    }

    @FXML
    private void handleSelectImage(MouseEvent mouseEvent) {
        try {
            final byte[] image = DialogUtils
                .openImageForItemEditor(((Node) mouseEvent.getSource()).getScene().getWindow(),
                    imageChooserTitle);
            this.imageRaw.setValue(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
