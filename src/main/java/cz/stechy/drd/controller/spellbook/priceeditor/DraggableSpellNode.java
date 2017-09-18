package cz.stechy.drd.controller.spellbook.priceeditor;

import cz.stechy.drd.model.DragContainer;
import cz.stechy.drd.util.Translator;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

abstract class DraggableSpellNode extends Group implements Initializable {

    // region Constants

    private static final String RESOURCE_PATH = "/fxml/spellbook/price_editor/draggable_spell_node.fxml";
    private static final Cursor DEFAULT_CURSOR = Cursor.OPEN_HAND;
    private static final Cursor MOVE_CURSOR = Cursor.CLOSED_HAND;

    private static final String NODE_ID = "node_id";

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
    private final BooleanProperty circlesVisible = new SimpleBooleanProperty(this, "circlesVisible", false);
    private final INodeManipulator nodeManipulator;
    private final ILinkListener linkListener;
    protected final Translator translator;

    private DraggableSpellNode leftNode;
    private DraggableSpellNode rightNode;
    private DraggableSpellNode bottomNode;

    private Point2D mouse;
    private NodeLink dragLink;


    // endregion

    // region Constructors

    DraggableSpellNode(INodeManipulator nodeManipulator, ILinkListener linkListener,
        Translator translator) {
        this.nodeManipulator = nodeManipulator;
        this.linkListener = linkListener;
        this.translator = translator;
        setId(UUID.randomUUID().toString());
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
        circleLeftLink.visibleProperty().bind(circlesVisible);
        circleRightLink.visibleProperty().bind(circlesVisible);

        moveContainer.setOnMousePressed(this::onNodeMousePressed);
        moveContainer.setOnMouseDragged(this::onNodeMouseDragged);
        setOnMouseReleased(event -> moveCursor.setValue(DEFAULT_CURSOR));

        circleBottomLink.setOnDragDetected(this::onLinkDragDetect);
        circleBottomLink.setOnDragDone(this::onLinkDragDone);

        circlesVisible.addListener((observable, oldValue, newValue) -> {
            if (newValue == null || !newValue) { // Kolečka nejsou viditelné - odeberu listenery
                circleLeftLink.setOnDragOver(null);
                circleLeftLink.setOnDragDropped(null);
                circleRightLink.setOnDragOver(null);
                circleRightLink.setOnDragDropped(null);
            } else { // Kolečka jsou viditelná, nastavím listenerry
                circleLeftLink.setOnDragOver(this::onLinkDragOver);
                circleLeftLink.setOnDragDropped(this::onLinkDragDropped);
                circleRightLink.setOnDragOver(this::onLinkDragOver);
                circleRightLink.setOnDragDropped(this::onLinkDragDropped);
            }
        });
    }

    // region Private methods

    /**
     * Zobrazí levé a pravé kolečko
     */
    protected void showLeftRightCircles() {
        circlesVisible.set(true);
    }

    // region Node drag&drop

    private void onNodeMousePressed(MouseEvent event) {
        mouse = new Point2D(event.getSceneX(), event.getSceneY());
        mouse = sceneToLocal(mouse);
        moveCursor.setValue(MOVE_CURSOR);

        event.consume();
    }

    private void onNodeMouseDragged(MouseEvent event) {
        Point2D local = getParent().sceneToLocal(new Point2D(event.getSceneX(), event.getSceneY()));
        relocate(local.getX() - mouse.getX(), local.getY() - mouse.getY());

        event.consume();
    }

    // endregion

    // region Link drag&drop

    private void onLinkDragDetect(MouseEvent event) {
        nodeManipulator.setOnDragOverHandler(this::onParentDragOver);

        final Circle source = (Circle) event.getSource();
        // Instanci ukládám pouze, abych mohl lehce měnit koncové souřadnice čáry podle myši
        linkListener.saveSourceNode(this);
        dragLink = linkListener.createNodeLink(localToParent(new Point2D(source.getLayoutX(), source.getLayoutY())));

        final DragContainer container = new DragContainer();
        container.addData(NODE_ID, getId());

        final ClipboardContent content = new ClipboardContent();
        content.put(DragContainer.PRICE_NODE_LINK_ADD, container);

        source.startDragAndDrop(TransferMode.ANY).setContent(content);
        event.consume();
    }

    private void onLinkDragOver(DragEvent event) {
        final DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.PRICE_NODE_LINK_ADD);
        if (container == null) {
            return;
        }

        container.getValue(NODE_ID).ifPresent(o -> {
            final String otherId = (String) o;
            if (!getId().equals(otherId)) {
                System.out.println("Accepting");
                event.acceptTransferModes(TransferMode.ANY);
                event.consume();
            }
        });
    }

    private void onLinkDragDropped(DragEvent event) {
        final Circle source = (Circle) event.getSource();

        linkListener.connectLineEndWithNode(getId(), source == circleLeftLink ? LinkPosition.LEFT : LinkPosition.RIGHT);

        event.setDropCompleted(true);
    }

    private void onLinkDragDone(DragEvent event) {
        nodeManipulator.setOnDragOverHandler(null);
        dragLink = null;
    }

    private void onParentDragOver(DragEvent event) {
        if (dragLink == null) {
            return;
        }

        dragLink.setEnd(getParent().sceneToLocal(new Point2D(event.getSceneX(), event.getSceneY())));
    }

    // endregion

    // endregion

    // region Getters & Setters

    // endregion

}
