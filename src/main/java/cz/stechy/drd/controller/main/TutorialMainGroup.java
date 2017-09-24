package cz.stechy.drd.controller.main;

import com.jfoenix.controls.JFXButton;
import cz.stechy.drd.model.Arrow;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class TutorialMainGroup extends Group {

    // region Variables

    private final Rectangle screenRectangle = new Rectangle(880, 700);
    private final JFXButton nextButton = new JFXButton("Next");
    private final Arrow arrow = new Arrow();

    // endregion

    // region Constructors

    /**
     * Vytvoří nového průvodce hlavní obrazovkou
     */
    public TutorialMainGroup() {
        nextButton.getStyleClass().addAll("btn", "btn-primary", "btn-raised");
        showToolbarHelp();
    }

    // endregion

    // region Private methods

    /**
     * Nastaví správný výřez obrazovky
     *
     * @param other Tvar výřezu v obrazovce
     */
    private void setResult(Shape other) {
        final Shape subtract = Shape.subtract(screenRectangle, other);
        subtract.setFill(Color.web("#122930", 0.5));
        getChildren().setAll(subtract, arrow, nextButton);
    }

    private void setButtonPosition(double x, double y) {
        nextButton.setLayoutX(x);
        nextButton.setLayoutY(y);
    }

    /**
     * Nastaví novou pozici šipky
     *
     * @param sx X-ová souřadnice počátku šipky
     * @param sy Y-ová souřadnice počátku šipky
     * @param ex X-ová souřadnice konce šipky
     * @param ey Y-ová souřadnice konce šipky
     */
    private void setArrowPosition(double sx, double sy, double ex, double ey) {
        arrow.setStartX(sx);
        arrow.setStartY(sy);
        arrow.setEndX(ex);
        arrow.setEndY(ey);
    }

    private void showToolbarHelp() {
        final Rectangle rect = new Rectangle(0, 50, screenRectangle.getWidth(), 48);
        rect.setArcHeight(30);
        rect.setArcWidth(30);
        setButtonPosition(400, 300);
        //setArrowPosition();
        setResult(rect);
    }

    // endregion
}
