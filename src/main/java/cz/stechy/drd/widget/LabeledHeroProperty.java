package cz.stechy.drd.widget;

import cz.stechy.drd.model.entity.EntityProperty;
import javafx.beans.binding.Bindings;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 * Kontrolka pro zobrazení jedné vlastnosti a její opravy
 */
public class LabeledHeroProperty extends TextFlow {

    private final Text value = new Text("0");
    private final Text separator = new Text(" / ");
    private final Text repair = new Text("0");

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
