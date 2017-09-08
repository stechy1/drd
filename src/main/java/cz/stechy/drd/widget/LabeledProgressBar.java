package cz.stechy.drd.widget;

import cz.stechy.drd.model.MaxActValue;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Kontrolka pro grafické zobrazení životů a magů
 */
public class LabeledProgressBar extends VBox {

    // region Constants

    private static final int DEFAULT_LABEL_PADDING = 5;
    private static final int DEFAULT_PROGRESS = 0;
    private static final int DEFAULT_MAX_PROGRESS = 1;

    private static final String[] COLORS = new String[]{
        "-fx-accent: red", "-fx-accent: blue", "-fx-accent: #FEFF8A"
    };

    // endregion

    // region Variables

    private final Label label = new Label();
    private final ProgressBar progressBar = new ProgressBar();
    private final Label progressLabel = new Label();
    private final StackPane container = new StackPane(progressBar, progressLabel);
    private DisplayMode displayMode;
    private int actValue = 1;
    private int maxValue = 1;

    // endregion

    // region Constructors

    public LabeledProgressBar() {
        setDisplayMode(DisplayMode.LIVE);

        progressLabel.setStyle("-fx-text-fill: black; -fx-background-color: rgba(0, 0, 0, 0.1); -fx-font-size: 12;");
        label.setFont(Font.font(10));

        progressBar.setPrefWidth(Double.MAX_VALUE);
        getChildren().setAll(label, container);

        sync(DEFAULT_PROGRESS, DEFAULT_MAX_PROGRESS);
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
        progressBar.setProgress(actValue / (double) maxValue);
        progressLabel.setText(String.format("%d / %d", actValue, maxValue));

        progressBar.setMinHeight(
            progressLabel.getBoundsInLocal().getHeight() + DEFAULT_LABEL_PADDING * 2);
        progressBar.setMinWidth(
            progressLabel.getBoundsInLocal().getWidth() + DEFAULT_LABEL_PADDING * 2);

        // Uložení nových hodnot do pomocných proměnných pro pozdější využití
        this.actValue = actValue;
        this.maxValue = maxValue;
    }

    // endregion

    // region Public methods

    /**
     *
     *
     * @param maxActValue
     */
    public void bind(final MaxActValue maxActValue) {
        maxActValue.actValueProperty().addListener(actValueListener);
        maxActValue.maxValueProperty().addListener(maxValueListener);

        sync(maxActValue.getActValue().intValue(), maxActValue.getMaxValue().intValue());
    }

    /**
     * Přestane pozorovat vybraný model.
     *
     * @param maxActValue
     */
    public void unbind(MaxActValue maxActValue) {
        assert maxActValue != null;
        maxActValue.actValueProperty().removeListener(actValueListener);
        maxActValue.maxValueProperty().removeListener(maxValueListener);

        sync(DEFAULT_PROGRESS, DEFAULT_MAX_PROGRESS);
    }

    // endregion

    // region Getters & Setters

    public DisplayMode getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(DisplayMode displayMode) {
        this.displayMode = displayMode;
        progressBar.setStyle(COLORS[displayMode.ordinal()]);
    }

    public StringProperty labelProperty() {
        return label.textProperty();
    }

    public String getLabel() {
        return label.getText();
    }

    public void setLabel(String label) {
        this.label.setText(label);
    }

    // endregion

    private final ChangeListener<Number> actValueListener = (observable, oldValue, newValue) ->
        sync(newValue.intValue(), maxValue);
    private final ChangeListener<Number> maxValueListener = (observable, oldValue, newValue) ->
        sync(this.actValue, newValue.intValue());

    public enum DisplayMode {
        LIVE, MAG, EXPERIENCE
    }
}
