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

    private static final int FONT_SIZE = 12;
    private static final Font TEXT_FONT = Font.font("Roboto", FONT_SIZE);

    private final Text value = new Text("0");
    private final Text separator = new Text(" / ");
    private final Text repair = new Text("0");

    {
        value.setFont(TEXT_FONT);
        separator.setFont(TEXT_FONT);
        repair.setFont(TEXT_FONT);

        final IntegerBinding translateBinding = Bindings.createIntegerBinding(() ->
            ((int) getHeight() / 2 - FONT_SIZE / 2), heightProperty());

        value.translateYProperty().bind(translateBinding);
        separator.translateYProperty().bind(translateBinding);
        repair.translateYProperty().bind(translateBinding);
    }

    public void setHeroProperty(EntityProperty entityProperty) {
        repair.fillProperty().bind(Bindings
            .when(entityProperty.repairProperty().isEqualTo(0))
            .then(Color.BLACK)
            .otherwise(Bindings
                .when(entityProperty.repairProperty().greaterThan(0))
                .then(Color.GREEN)
                .otherwise((Color.RED))));

        value.textProperty().bind(entityProperty.valueProperty().asString());
        repair.textProperty().bind(entityProperty.repairProperty().asString());
        setTextAlignment(TextAlignment.CENTER);

        getChildren().setAll(value, separator, repair);
    }
}
