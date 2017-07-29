package cz.stechy.drd.widget;

import cz.stechy.drd.model.MaxActValue;
import javafx.beans.property.StringProperty;
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

    private static final String[] COLORS = new String[]{
        "-fx-accent: red", "-fx-accent: blue", "-fx-accent: #FEFF8A"
    };

    // endregion

    // region Variables

    private final Label label = new Label();
    private final ProgressBar progressBar = new ProgressBar(0);
    private final Label progressLabel = new Label("0 / 0");
    private final StackPane container = new StackPane(progressBar, progressLabel);
    private DisplayMode displayMode;

    // endregion

    // region Constructors

    public LabeledProgressBar() {
        setDisplayMode(DisplayMode.LIVE);

        progressLabel.setStyle("-fx-text-fill: black; -fx-background-color: rgba(0, 0, 0, 0.1); -fx-font-size: 12;");
        label.setFont(Font.font(10));

        progressBar.setPrefWidth(Double.MAX_VALUE);
        getChildren().setAll(label, container);

        sync(0, 100);
    }

    // endregion

    // region Private methods

    private void sync(int actValue, int maxValue) {
        progressBar.setProgress(actValue / (double) maxValue);
        progressLabel.setText(String.format("%d / %d", actValue, maxValue));

        progressBar.setMinHeight(
            progressLabel.getBoundsInLocal().getHeight() + DEFAULT_LABEL_PADDING * 2);
        progressBar.setMinWidth(
            progressLabel.getBoundsInLocal().getWidth() + DEFAULT_LABEL_PADDING * 2);
    }

    // endregion

    // region Public methods

    public void setMaxActValue(final MaxActValue maxActValue) {
        final int maxValue = maxActValue.getMaxValue().intValue();
        maxActValue.actValueProperty().addListener((observable, oldValue, newValue) ->
            sync(newValue.intValue(), maxValue));

        sync(maxActValue.getActValue().intValue(), maxActValue.getMaxValue().intValue());
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

    public enum DisplayMode {
        LIVE, MAG, EXPERIENCE
    }
}
