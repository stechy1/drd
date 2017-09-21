package cz.stechy.drd.controller.spellbook.priceeditor;

import cz.stechy.drd.R;
import cz.stechy.drd.model.DragContainer;
import cz.stechy.drd.util.Translator;
import cz.stechy.screens.BaseController;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Kontroler pro editaci ceny kouzla
 */
public class SpellPriceEditorController extends BaseController implements Initializable {

    // region Constants

    private static final String[] LABEL_CAPTIONS = new String[]{
        R.Translate.SPELL_PRICE_TYPE_CONSTANT,
        R.Translate.SPELL_PRICE_TYPE_VARIABLE,
        R.Translate.SPELL_PRICE_TYPE_MODIFIER
    };

    private static final String PROPERTY_PRICE_TYPE = "price_type";
    private static final int PROPERTY_PRICE_TYPE_CONSTANT = 0;
    private static final int PROPERTY_PRICE_TYPE_VARIABLE = 1;
    private static final int PROPERTY_PRICE_TYPE_MODIFIER = 2;

    // endregion

    // region Variables

    // region FXML

    @FXML
    private VBox componentSourceContainer;
    @FXML
    private Pane componentPlayground;

    // endregion

    private final List<DraggableSpellNode> nodes = new ArrayList<>();
    private final List<NodeLink> links = new ArrayList<>();
    private final Translator translator;

    private String title;
    private DraggableSpellNode rootNode;

    private DraggableSpellNode dragSourceNode;
    private NodeLink dragLink;

    // endregion

    // region Constructors

    public SpellPriceEditorController(Translator translator) {
        this.translator = translator;
    }

    // endregion

    // region Private methods

    /**
     * Přidá node určitého typu na scénu
     *
     * @param type Typ nodu
     * @param screenX X-ová souřadnice středu nodu v X-ové ose
     * @param screenY Y-ová souřadnice reprezentující horní část nodu
     */
    private void addNode(int type, double screenX, double screenY) {
        DraggableSpellNode node;
        switch (type) {
            case PROPERTY_PRICE_TYPE_CONSTANT:
                node = new ConstantDraggableSpellNode(manipulator, linkListener, translator);
                break;
            case PROPERTY_PRICE_TYPE_VARIABLE:
                node = new VariableDraggableSpellNode(manipulator, linkListener, translator);
                break;
            case PROPERTY_PRICE_TYPE_MODIFIER:
                node = new ModifierDraggableSpellNode(manipulator, linkListener, translator);
                break;
            default:
                return;
        }
        if (this.rootNode == null) {
            this.rootNode = node;
        }

        componentPlayground.getChildren().add(node);
        nodes.add(node);
        Point2D point = node.getParent().sceneToLocal(new Point2D(screenX, screenY));
        // 100 = polovina šířky draggable nodu
        // 15 = polovina výšky záhlaví draggable nodu
        node.relocate(point.getX() - 100, point.getY() - 15);
    }

    private void onDragDetected(MouseEvent event) {
        final Label source = (Label) event.getSource();
        final int propertyType = (int) source.getProperties().get(PROPERTY_PRICE_TYPE);

        final DragContainer container = new DragContainer();
        container.addData(PROPERTY_PRICE_TYPE, propertyType);

        final ClipboardContent content = new ClipboardContent();
        content.put(DragContainer.PRICE_NODE_ADD, container);

        source.startDragAndDrop(TransferMode.ANY).setContent(content);

        event.consume();
    }

    private void onDragDropped(DragEvent event) {
        final DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.PRICE_NODE_ADD);
        container.getValue(PROPERTY_PRICE_TYPE).ifPresent(o -> {
            event.setDropCompleted(true);
            final int type = (int) o;
            addNode(type, event.getSceneX(), event.getSceneY());
        });

        event.consume();
    }

    private void onDragOver(DragEvent event) {
        final DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.PRICE_NODE_ADD);
        if (container == null) {
            return;
        }

        container.getValue(PROPERTY_PRICE_TYPE).ifPresent(o ->
            event.acceptTransferModes(TransferMode.ANY));

        event.consume();
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title = resources.getString(R.Translate.SPELL_PRICE_EDITOR_TITLE);
        for (int i = 0; i < LABEL_CAPTIONS.length; i++) {
            final String key = LABEL_CAPTIONS[i];
            final Label label = new Label(resources.getString(key));
            label.getProperties().put(PROPERTY_PRICE_TYPE, i);
            label.setOnDragDetected(this::onDragDetected);
            componentSourceContainer.getChildren().add(label);
        }

        componentPlayground.setOnDragDropped(this::onDragDropped);
        componentPlayground.setOnDragOver(this::onDragOver);
    }

    @Override
    protected void onResume() {
        setTitle(title);
        setScreenSize(800, 600);
    }

    // Implementace node manipulatoru
    private final INodeManipulator manipulator = new INodeManipulator() {

        @Override
        public void setOnDragOverHandler(EventHandler<? super DragEvent> event) {
            if (event == null) {
                componentPlayground.setOnDragOver(SpellPriceEditorController.this::onDragOver);
            } else {
                componentPlayground.setOnDragOver(event);
            }
        }

    };

    private final ILinkListener linkListener = new ILinkListener() {
        @Override
        public void saveSourceNode(DraggableSpellNode node) {
            SpellPriceEditorController.this.dragSourceNode = node;
        }

        @Override
        public NodeLink createNodeLink(Point2D start) {
            dragLink = new NodeLink();
            dragLink.setStart(start);
            componentPlayground.getChildren().add(dragLink);
            links.add(dragLink);
            return dragLink;
        }

        @Override
        public void saveNodeLink(NodeLink nodeLink) {
            SpellPriceEditorController.this.dragLink = nodeLink;
        }

        @Override
        public void deleteNodeLink(NodeLink nodeLink) {
            componentPlayground.getChildren().remove(nodeLink);
            links.remove(nodeLink);
            nodeLink.unbind();
        }

        @Override
        public void connectLineEndWithNode(String id, LinkPosition position) {
            nodes.stream()
                .filter(draggableSpellNode -> draggableSpellNode.getId().equals(id))
                .findFirst()
                .ifPresent(dragDestinationNode ->
                    dragLink.bindEnds(dragSourceNode, dragDestinationNode, position));
        }
    };
}
