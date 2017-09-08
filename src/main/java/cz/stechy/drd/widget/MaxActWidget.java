package cz.stechy.drd.widget;

import cz.stechy.drd.model.MaxActValue;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;

/**
 * Obecná vizualizace {@link cz.stechy.drd.model.MaxActValue}
 */
public final class MaxActWidget extends Label {

    // region Constructors

    public MaxActWidget() {
        reset();
    }

    // endregion

    // region Private methods

    private void reset() {
        if (!textProperty().isBound()) {
            setText("0/0");
        }
    }

    // endregion

    // region Public methods

    public void bind(MaxActValue maxActValue) {
        textProperty().bind(Bindings.concat(
            maxActValue.actValueProperty().asString(),
            " / ",
            maxActValue.maxValueProperty().asString()
        ));
    }

    public void unbind() {
        textProperty().unbind();

        reset();
    }

    // endregion

}
