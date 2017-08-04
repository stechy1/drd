package cz.stechy.drd.widget;

import com.jfoenix.controls.JFXProgressBar;
import cz.stechy.drd.model.MaxActValue;
import java.io.IOException;
import javafx.beans.NamedArg;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Rozšířená grafická vizualizace pro {@link cz.stechy.drd.model.MaxActValue}
 */
public class LabeledMaxActValue extends Group {

    // region Variables

    // region FXML

    @FXML
    private ImageView imageView;
    @FXML
    private Label lblCaption;
    @FXML
    private MaxActWidget lblValue;
    @FXML
    private JFXProgressBar bar;
    @FXML
    private Label lblDescription;

    private int actValue = 1;
    private int maxValue = 1;

    // endregion

    // endregion

    // region Constructors

    public LabeledMaxActValue(@NamedArg("caption") String caption, @NamedArg("description") String description, @NamedArg("maxActValue") MaxActValue maxActValue) {
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/fxml/labeled_max_act_value.fxml"));
            loader.setController(this);
            Pane pane = loader.load();
            getChildren().setAll(pane);
            lblCaption.setText(caption);
            lblDescription.setText(description);
            lblValue.forMaxActValue(maxActValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // endregion

    private void sync(int actValue, int maxValue) {
        bar.setProgress(actValue / (double) maxValue);

        // Uložení nových hodnot do pomocných proměnných pro pozdější využití
        this.actValue = actValue;
        this.maxValue = maxValue;
    }

    public void setMaxActValue(MaxActValue maxActValue) {
        lblValue.forMaxActValue(maxActValue);

        maxActValue.actValueProperty().addListener((observable, oldValue, newValue) -> {
            sync(newValue.intValue(), maxValue);
        });
        maxActValue.maxValueProperty().addListener((observable, oldValue, newValue) -> {
            sync(this.actValue, newValue.intValue());
        });

        sync(maxActValue.getActValue().intValue(), maxActValue.getMaxValue().intValue());
    }

    public void setImage(Image image) {
        imageView.setImage(image);
    }
}
