package cz.stechy.drd.controller.spellbook.priceeditor;

import cz.stechy.drd.util.Translator;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

abstract class DraggableSpellNode extends Group implements Initializable {

    // region Constants

    private static final String RESOURCE_PATH = "/fxml/spellbook/price_editor/draggable_spell_node.fxml";
    private static final Cursor DEFAULT_CURSOR = Cursor.OPEN_HAND;
    private static final Cursor MOVE_CURSOR = Cursor.CLOSED_HAND;

    // endregion

    // region Variables

    // region FXML

    @FXML
    protected AnchorPane anchorRoot;
    @FXML
    private HBox moveContainer;
    @FXML
    protected Label lblTitle;
    @FXML
    protected Button btnCloseNode;
    @FXML
    protected VBox container;
    @FXML
    protected Circle circleLeftLink;
    @FXML
    protected Circle circleRightLink;
    @FXML
    protected Circle circleBottomLink;

    // endregion

    private final ObjectProperty<Cursor> moveCursor = new SimpleObjectProperty<>(this, "moveCursor", Cursor.OPEN_HAND);

    private DraggableSpellNode leftNode;
    private DraggableSpellNode rightNode;
    private DraggableSpellNode bottomNode;

    private Point2D mouse;

    protected final Translator translator;

    // endregion

    // region Constructors

    DraggableSpellNode(Translator translator) {
        this.translator = translator;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(RESOURCE_PATH));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        moveContainer.cursorProperty().bind(moveCursor);

        moveContainer.setOnMousePressed(this::onNodeMousePressed);
        moveContainer.setOnMouseDragged(this::onNodeMouseDragged);
        setOnMouseReleased(event -> moveCursor.setValue(DEFAULT_CURSOR));
    }

    // region Private methods

    private void onNodeMousePressed(MouseEvent event) {
        mouse = new Point2D(event.getSceneX(), event.getSceneY());
        mouse = sceneToLocal(mouse);
        moveCursor.setValue(MOVE_CURSOR);
        toFront();

        event.consume();
    }

    private void onNodeMouseDragged(MouseEvent event) {
        Point2D local = getParent().sceneToLocal(new Point2D(event.getSceneX(), event.getSceneY()));
        relocate(local.getX() - mouse.getX(), local.getY() - mouse.getY());

        event.consume();
    }

    // endregion

}
