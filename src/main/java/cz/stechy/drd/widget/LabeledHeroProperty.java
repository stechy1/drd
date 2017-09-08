package cz.stechy.drd.widget;

import cz.stechy.drd.model.entity.EntityProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 * Kontrolka pro zobrazení jedné vlastnosti a její opravy
 */
public class LabeledHeroProperty extends TextFlow {

    // region Constants

    private static final int FONT_SIZE = 12;
    private static final Font TEXT_FONT = Font.font("Roboto", FONT_SIZE);

    private static final Color COLOR_NEUTRAL = Color.BLACK;
    private static final Color COLOR_POSITIVE = Color.GREEN;
    private static final Color COLOR_NEGATIVE = Color.RED;
    private static final String DEFAULT_VALUE = "0";

    // endregion

    // region Variables

    private final Text value = new Text();
    private final Text separator = new Text(" / ");
    private final Text repair = new Text();

    // endregion

    {
        value.setFont(TEXT_FONT);
        separator.setFont(TEXT_FONT);
        repair.setFont(TEXT_FONT);

        final IntegerBinding translateBinding = Bindings.createIntegerBinding(() ->
            ((int) getHeight() / 2 - FONT_SIZE / 2), heightProperty());

        value.translateYProperty().bind(translateBinding);
        separator.translateYProperty().bind(translateBinding);
        repair.translateYProperty().bind(translateBinding);

        setTextAlignment(TextAlignment.CENTER);

        getChildren().setAll(value, separator, repair);
        reset();
    }

    // region Private methods

    /**
     * Všude kde to bude možné se nastaví výchozí hodnoty
     */
    private void reset() {
        if (!value.textProperty().isBound()) {
            value.setText(DEFAULT_VALUE);
        }
        if (!repair.textProperty().isBound()) {
            repair.setText(DEFAULT_VALUE);
        }
        if (!repair.fillProperty().isBound()) {
            repair.setFill(COLOR_NEUTRAL);
        }
    }

    // endregion

    // region Public methods

    /**
     * Začne pozorovat vybraný model.
     * Pokud se změní v modelu vybrané hdnoty, jsou tyto změny propagovány do widgetu.
     *
     * @param entityProperty {@link EntityProperty}
     */
    public void bind(EntityProperty entityProperty) {
        repair.fillProperty().bind(Bindings
            .when(entityProperty.repairProperty().isEqualTo(0))
            .then(COLOR_NEUTRAL)
            .otherwise(Bindings
                .when(entityProperty.repairProperty().greaterThan(0))
                .then(COLOR_POSITIVE)
                .otherwise((COLOR_NEGATIVE))));

        value.textProperty().bind(entityProperty.valueProperty().asString());
        repair.textProperty().bind(entityProperty.repairProperty().asString());
    }

    /**
     * Přestane pozorovat dříve přiřazený model.
     */
    public void unbind() {
        repair.fillProperty().unbind();
        value.textProperty().unbind();
        repair.textProperty().unbind();

        reset();
    }

    // endregion
}
