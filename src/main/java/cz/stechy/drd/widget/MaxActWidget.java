package cz.stechy.drd.widget;

import cz.stechy.drd.model.MaxActValue;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;

/**
 * Obecná vizualizace {@link cz.stechy.drd.model.MaxActValue}
 */
public final class MaxActWidget extends Label {

    // region Constants

    private static final String DEFAULT_TEXT = "0 / 0";

    // endregion

    // region Constructors

    public MaxActWidget() {
        reset();
    }

    // endregion

    // region Private methods

    /**
     * Vyresetuje text na výchozí hodnotu
     */
    private void reset() {
        if (!textProperty().isBound()) {
            setText(DEFAULT_TEXT);
        }
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
        textProperty().bind(Bindings.concat(
            maxActValue.actValueProperty().asString(),
            " / ",
            maxActValue.maxValueProperty().asString()
        ));
    }

    /**
     * Přestane pozorovat dříve přiřazený model.
     */
    public void unbind() {
        textProperty().unbind();

        reset();
    }

    // endregion

}
