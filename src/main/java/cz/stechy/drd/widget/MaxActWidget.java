package cz.stechy.drd.widget;

import cz.stechy.drd.model.MaxActValue;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;

/**
 * Obecná vizualizace {@link cz.stechy.drd.model.MaxActValue}
 */
public class MaxActWidget extends Label {

    public MaxActWidget() {}

    public final void forMaxActValue(MaxActValue maxActValue) {
        textProperty().bind(Bindings.concat(
            maxActValue.actValueProperty().asString(),
            " / ",
            maxActValue.maxValueProperty().asString()
        ));

    }

}
