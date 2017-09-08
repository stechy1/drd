package cz.stechy.drd.widget;

import cz.stechy.drd.model.MaxActValue;
import java.io.IOException;
import javafx.beans.NamedArg;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Rozšířená grafická vizualizace pro {@link cz.stechy.drd.model.MaxActValue}
 */
public class LabeledMaxActValue extends Group {

    // region Constants

    private static final int DEFAULT_PROGRESS = 0;

    // endregion

    // region Variables

    // region FXML

    @FXML
    private ImageView imageView;
    @FXML
    private Label lblCaption;
    @FXML
    private MaxActWidget lblValue;
    @FXML
    private ProgressBar bar;
    @FXML
    private Label lblDescription;

    // endregion

    private int actValue = 1;
    private int maxValue = 1;

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
            if (maxActValue == null) {
                reset();
            } else {
                bind(maxActValue);
            }
            prefWidth(270);
            prefHeight(140);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // endregion

    // region Private methods

    /**
     * Nastaví progress baru správnou hodnotu
     *
     * @param actValue Aktuální stav postupu
     * @param maxValue Maximální hodnota postupu
     */
    private void sync(int actValue, int maxValue) {
        bar.setProgress(actValue / (double) maxValue);

        // Uložení nových hodnot do pomocných proměnných pro pozdější využití
        this.actValue = actValue;
        this.maxValue = maxValue;
    }

    /**
     * Vyresetuje progress bar a nastaví mu výchozí hodnotu
     */
    private void reset() {
        bar.setProgress(DEFAULT_PROGRESS);
    }

    // endregion

    // region Public methods

    /**
     * Začne pozorovat vybraný model.
     * Pokud se změní v modelu vybrané hdnoty, jsou tyto změny propagovány do widgetu.
     *
     * @param maxActValue {@link MaxActValue}
     */
    public void bind(MaxActValue maxActValue) {
        lblValue.bind(maxActValue);

        maxActValue.actValueProperty().addListener(actValueListener);
        maxActValue.maxValueProperty().addListener(maxValueListener);

        sync(maxActValue.getActValue().intValue(), maxActValue.getMaxValue().intValue());
    }

    /**
     * Přestane pozorovat vybraný model.
     *
     * @param maxActValue {@link MaxActValue}
     */
    public void unbind(MaxActValue maxActValue) {
        assert maxActValue != null;
        maxActValue.actValueProperty().removeListener(actValueListener);
        maxActValue.maxValueProperty().removeListener(maxValueListener);
        lblValue.unbind();

        reset();
    }

    public void setImage(Image image) {
        imageView.setImage(image);
    }

    // endregion

    private final ChangeListener<Number> actValueListener = (observable, oldValue, newValue) ->
        sync(newValue.intValue(), maxValue);
    private final ChangeListener<Number> maxValueListener = (observable, oldValue, newValue) ->
        sync(this.actValue, newValue.intValue());
}
