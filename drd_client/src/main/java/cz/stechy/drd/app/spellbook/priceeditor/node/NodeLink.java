package cz.stechy.drd.app.spellbook.priceeditor.node;

import cz.stechy.drd.app.spellbook.priceeditor.LinkPosition;
import java.util.UUID;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;

/**
 * Třída reprezentující spojení mezi dvěma nody
 */
public class NodeLink extends CubicCurve {

    // region Variables

    // endregion

    // region Constructors

    /**
     * Vytvoří nový spoj mezi budoucími dvěma body
     */
    public NodeLink() {
        DoubleProperty mControlOffsetX = new SimpleDoubleProperty();
        mControlOffsetX.set(100.0);
        DoubleProperty mControlOffsetY = new SimpleDoubleProperty();
        mControlOffsetY.set(50.0);
        setStroke(Color.BLACK);
        setFill(null);
        setId(UUID.randomUUID().toString());

        DoubleProperty mControlDirectionX1 = new SimpleDoubleProperty();
        mControlDirectionX1.bind(Bindings
            .when(startXProperty().greaterThan(endXProperty()))
            .then(-1.0)
            .otherwise(1.0));

        DoubleProperty mControlDirectionX2 = new SimpleDoubleProperty();
        mControlDirectionX2.bind(Bindings
            .when(startXProperty().greaterThan(endXProperty()))
            .then(1.0)
            .otherwise(-1.0));

        controlX1Property().bind(startXProperty()
            .add(mControlOffsetX.multiply(mControlDirectionX1)));

        controlX2Property().bind(endXProperty()
            .add(mControlOffsetX.multiply(mControlDirectionX2)));

        DoubleProperty mControlDirectionY1 = new SimpleDoubleProperty();
        controlY1Property().bind(startYProperty()
            .add(mControlOffsetY.multiply(mControlDirectionY1)));

        DoubleProperty mControlDirectionY2 = new SimpleDoubleProperty();
        controlY2Property().bind(endYProperty()
            .add(mControlOffsetY.multiply(mControlDirectionY2)));
    }

    // endregion

    // region Public methods

    /**
     * Nastaví startovní pozici linku
     *
     * @param startPoint {@link Point2D}
     */
    public void setStart(Point2D startPoint) {
        unbindStart();
        setStartX(startPoint.getX());
        setStartY(startPoint.getY());
    }

    /**
     * Nastaví koncovou pozici linku
     *
     * @param endPoint {@link Point2D}
     */
    public void setEnd(Point2D endPoint) {
        unbindEnd();
        setEndX(endPoint.getX());
        setEndY(endPoint.getY());
    }

    /**
     * Propojí graficky link se dvěma nody
     *
     * @param source Zdrojový node
     * @param target Cílový node
     */
    public void bindEnds(DraggableSpellNode source, DraggableSpellNode target,
        LinkPosition position) {
        startXProperty().bind(source.layoutXProperty().add(source.circleBottomLink.getLayoutX()));
        startYProperty().bind(source.layoutYProperty().add(source.circleBottomLink.getLayoutY()));
        source.bottomLink = this;
        source.bottomNode = target;

        Circle targetCircle = null;
        switch (position) {
            case LEFT:
                targetCircle = target.circleLeftLink;
                target.leftLink = this;
                target.leftNode = source;
                break;
            case RIGHT:
                targetCircle = target.circleRightLink;
                target.rightLink = this;
                target.rightNode = source;
                break;
        }
        endXProperty().bind(target.layoutXProperty().add(targetCircle.getLayoutX()));
        endYProperty().bind(target.layoutYProperty().add(targetCircle.getLayoutY()));
    }

    /**
     * Zruší pozorování startovního bodu
     */
    public void unbindStart() {
        startXProperty().unbind();
        startYProperty().unbind();
    }

    /**
     * Zruší pozorování koncového bodu
     */
    public void unbindEnd() {
        endXProperty().unbind();
        endYProperty().unbind();
    }

    /**
     * Zruší pozorování vlastností
     */
    public void unbind() {
        unbindStart();
        unbindEnd();
    }

    // endregion

}
