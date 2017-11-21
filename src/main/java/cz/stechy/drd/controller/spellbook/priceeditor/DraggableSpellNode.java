package cz.stechy.drd.controller.spellbook.priceeditor;

import cz.stechy.drd.controller.spellbook.ISpellGraphNode;
import cz.stechy.drd.model.DragContainer;
import cz.stechy.drd.util.Translator;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
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
import javafx.util.Pair;

abstract class DraggableSpellNode extends Group implements Initializable, ISpellGraphNode,
    ISpellPriceCreator {

    // region Constants

    private static final String RESOURCE_PATH = "/fxml/spellbook/price_editor/draggable_spell_node.fxml";
    private static final Cursor DEFAULT_CURSOR = Cursor.OPEN_HAND;
    private static final Cursor MOVE_CURSOR = Cursor.CLOSED_HAND;

    private static final String NODE_ID = "node_id";
    private static final String LINK_ADD_STATUS = "link_add_status";
    private static final String LINK_ID = "link_id";

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

    protected DraggableSpellNode leftNode;
    protected DraggableSpellNode rightNode;
    protected DraggableSpellNode bottomNode;
    protected NodeLink leftLink;
    protected NodeLink rightLink;
    protected NodeLink bottomLink;

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
                circleLeftLink.setOnDragDetected(null);
                circleLeftLink.setOnDragOver(null);
                circleLeftLink.setOnDragDropped(null);
                circleRightLink.setOnDragDetected(null);
                circleRightLink.setOnDragOver(null);
                circleRightLink.setOnDragDropped(null);
            } else { // Kolečka jsou viditelná, nastavím listenerry
                circleLeftLink.setOnDragDetected(this::onLinkDragDetect);
                circleLeftLink.setOnDragOver(this::onLinkDragOver);
                circleLeftLink.setOnDragDropped(this::onLinkDragDropped);
                circleRightLink.setOnDragDetected(this::onLinkDragDetect);
                circleRightLink.setOnDragOver(this::onLinkDragOver);
                circleRightLink.setOnDragDropped(this::onLinkDragDropped);
            }
        });
    }

    @Override
    public Pair<ISpellGraphNode, ISpellGraphNode> getParentNodes() {
        return new Pair<>(leftNode, rightNode);
    }

    @Override
    public ISpellGraphNode getChildNode() {
        return bottomNode;
    }

    // region Private methods

    /**
     * Zobrazí levé a pravé kolečko
     */
    protected void showLeftRightCircles() {
        circlesVisible.set(true);
    }

    private LinkPosition getPosition(Circle circle) {
        if (this.circleBottomLink == circle) {
            return LinkPosition.BOTTOM;
        }
        if (this.circleLeftLink == circle) {
            return LinkPosition.LEFT;
        } else {
            return LinkPosition.RIGHT;
        }
    }

    private NodeLink getLink(LinkPosition position) {
        switch (position) {
            case BOTTOM:
                return bottomLink;
            case LEFT:
                return leftLink;
            case RIGHT:
                return rightLink;
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * Odstraní fyzické spojení mezi this.bottomNode a nodem s ním spojeným
     */
    private boolean disconectParent() {
        if (bottomNode == null) {
            return false;
        }

        if (bottomNode.leftNode == this) {
            bottomNode.leftNode = null;
            bottomNode.leftLink = null;
        } else if (bottomNode.rightNode == this) {
            bottomNode.rightNode = null;
            bottomNode.rightLink = null;
        }

        return true;
    }

    private boolean disconnectChild(DraggableSpellNode node) {
        if (node == null) {
            return false;
        }

        node.bottomNode = null;
        node.bottomLink = null;

        return true;
    }

    private void disconnectAll() {
        if (disconectParent()) {
            linkListener.deleteNodeLink(bottomLink);
            bottomNode = null;
            bottomLink = null;
        }
        if (disconnectChild(leftNode)) {
            linkListener.deleteNodeLink(leftLink);
            leftNode = null;
            leftLink = null;
        }
        if (disconnectChild(rightNode)) {
            linkListener.deleteNodeLink(rightLink);
            rightNode = null;
            rightLink = null;
        }
    }

    private boolean hasConnection(LinkPosition position) {
        switch (position) {
            case BOTTOM:
                return bottomLink != null;
            case LEFT:
                return leftLink != null;
            case RIGHT:
                return rightLink != null;
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * Přesune node "dopředu"
     */
    private void goFront() {
        toFront();
        linkToFront(leftLink);
        linkToFront(rightLink);
        linkToFront(bottomLink);
    }

    /**
     * Přesune link "dopředu" pokud není null
     *
     * @param link {@link NodeLink}
     */
    private void linkToFront(NodeLink link) {
        if (link != null) {
            link.toFront();
        }
    }

    // region Node drag&drop

    private void onNodeMousePressed(MouseEvent event) {
        mouse = new Point2D(event.getSceneX(), event.getSceneY());
        mouse = sceneToLocal(mouse);
        moveCursor.setValue(MOVE_CURSOR);
        goFront();

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
        final Circle source = (Circle) event.getSource();
        final LinkPosition position = getPosition(source);
        final boolean connected = hasConnection(position);
        if ((position == LinkPosition.LEFT || position == LinkPosition.RIGHT)) {
            event.consume();
            return;
        }

        nodeManipulator.setOnDragOverHandler(this::onParentDragOver);

        // Instanci ukládám pouze, abych mohl lehce měnit koncové souřadnice čáry podle myši
        linkListener.saveSourceNode(this);
        if (connected) {
            dragLink = getLink(position);
            dragLink.setEnd(localToParent(new Point2D(source.getLayoutX(), source.getLayoutY())));
            // opositeNode = muj bottom node
            // jehož left | right node jsem ja
            disconectParent();

            linkListener.saveNodeLink(dragLink);
        } else {
            dragLink = linkListener.createNodeLink(
                localToParent(new Point2D(source.getLayoutX(), source.getLayoutY())));
        }

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
                event.acceptTransferModes(TransferMode.ANY);
                event.consume();
            }
        });
    }

    private void onLinkDragDropped(DragEvent event) {
        final Circle source = (Circle) event.getSource();
        final DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.PRICE_NODE_LINK_ADD);
        if (container == null) {
            return;
        }

        linkListener.connectLineEndWithNode(getId(), source == circleLeftLink ? LinkPosition.LEFT : LinkPosition.RIGHT);
        container.addData(LINK_ADD_STATUS, true);

        final ClipboardContent content = new ClipboardContent();
        content.put(DragContainer.PRICE_NODE_LINK_ADD, container);
        event.getDragboard().setContent(content);

        event.setDropCompleted(true);
        event.consume();
    }

    private void onLinkDragDone(DragEvent event) {
        final DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.PRICE_NODE_LINK_ADD);
        if (container == null) {
            return;
        }

        final Optional<Object> optional = container.getValue(LINK_ADD_STATUS);
        if (!optional.isPresent()) {
            bottomLink = null;
            bottomNode = null;
            linkListener.deleteNodeLink(dragLink);
        }
        nodeManipulator.setOnDragOverHandler(null);
        dragLink = null;
    }

    // region Parent drag&drop

    private void onParentDragOver(DragEvent event) {
        if (dragLink == null) {
            return;
        }

        dragLink.setEnd(getParent().sceneToLocal(new Point2D(event.getSceneX(), event.getSceneY())));
    }

    // endregion

    // endregion

    // region Button handlers

    @FXML
    private void handleCloseNode(ActionEvent actionEvent) {
        disconnectAll();
        nodeManipulator.removeNode(this);
    }

    // endregion

    // endregion

    // region Getters & Setters

    // endregion

    @Override
    public String toString() {
        return lblTitle.getText() + " - " + super.toString();
    }
}
