package cz.stechy.drd.widget;

import cz.stechy.drd.model.entity.EntityProperty;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Kontrolka pro zobrazení jedné vlastnosti a její opravy
 */
public class LabeledHeroProperty extends TextFlow {

    private final Text value = new Text("0");
    private final Text separator = new Text(" / ");
    private final Text repair = new Text("0");

    public void setHeroProperty(EntityProperty entityProperty) {
        entityProperty.repairProperty().addListener((observable, oldValue, newValue) ->
            setRepairTextFill(newValue.intValue()));

        value.textProperty().bind(entityProperty.valueProperty().asString());
        repair.textProperty().bind(entityProperty.repairProperty().asString());
        setRepairTextFill(entityProperty.getRepair());

        getChildren().setAll(value, separator, repair);
    }

    private void setRepairTextFill(int value) {
        repair.setFill(value > 0
            ? Color.GREEN
            : value < 0
                ? Color.RED
                : Color.BLACK);
    }


}
