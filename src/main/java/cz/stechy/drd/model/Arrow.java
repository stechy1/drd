package cz.stechy.drd.model;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Group;
import javafx.scene.shape.Line;

/**
 * Třída reprezentující šipku
 */
public class Arrow extends Group {

    // region Constants

    private static final double ARROW_LENGTH = 20;
    private static final double ARROW_WIDTH = 7;

    // endregion

    // region Variables

    // Hlavní středová čára
    private final Line line;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou šipku
     */
    public Arrow() {
        this(new Line(), new Line(), new Line());
    }

    /**
     * Vytvoří novou šipku
     *
     * @param line Středová čára
     * @param arrow1 První kus šipky
     * @param arrow2 Druhý kus šipky
     */
    private Arrow(Line line, Line arrow1, Line arrow2) {
        super(line, arrow1, arrow2);
        this.line = line;
        final InvalidationListener updater = o -> {
            double ex = getEndX();
            double ey = getEndY();
            double sx = getStartX();
            double sy = getStartY();

            arrow1.setEndX(ex);
            arrow1.setEndY(ey);
            arrow2.setEndX(ex);
            arrow2.setEndY(ey);

            if (ex == sx && ey == sy) {
                arrow1.setStartX(ex);
                arrow1.setStartY(ey);
                arrow2.setStartX(ex);
                arrow2.setStartY(ey);
            } else {
                double factor = ARROW_LENGTH / Math.hypot(sx-ex, sy-ey);
                double factorO = ARROW_WIDTH / Math.hypot(sx-ex, sy-ey);

                double dx = (sx - ex) * factor;
                double dy = (sy - ey) * factor;

                double ox = (sx - ex) * factorO;
                double oy = (sy - ey) * factorO;

                arrow1.setStartX(ex + dx - oy);
                arrow1.setStartY(ey + dy + ox);
                arrow2.setStartX(ex + dx + oy);
                arrow2.setStartY(ey + dy - ox);
            }
        };

        startXProperty().addListener(updater);
        startYProperty().addListener(updater);
        endXProperty().addListener(updater);
        endYProperty().addListener(updater);
        updater.invalidated(null);
    }

    // endregion

    // region Getters & Setters

    public final void setStartX(double value) {
        line.setStartX(value);
    }

    public final double getStartX() {
        return line.getStartX();
    }

    public final ReadOnlyDoubleProperty startXProperty() {
        return line.startXProperty();
    }

    public final void setStartY(double value) {
        line.setStartY(value);
    }

    public final double getStartY() {
        return line.getStartY();
    }

    public final ReadOnlyDoubleProperty startYProperty() {
        return line.startYProperty();
    }

    public final void setEndX(double value) {
        line.setEndX(value);
    }

    public final double getEndX() {
        return line.getEndX();
    }

    public final ReadOnlyDoubleProperty endXProperty() {
        return line.endXProperty();
    }

    public final void setEndY(double value) {
        line.setEndY(value);
    }

    public final double getEndY() {
        return line.getEndY();
    }

    public final ReadOnlyDoubleProperty endYProperty() {
        return line.endYProperty();
    }

    // endregion

}