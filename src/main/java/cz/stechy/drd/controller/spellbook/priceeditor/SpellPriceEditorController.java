package cz.stechy.drd.controller.spellbook.priceeditor;

import cz.stechy.drd.R;
import cz.stechy.drd.model.DragContainer;
import cz.stechy.drd.util.Translator;
import cz.stechy.screens.BaseController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
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
public class SpellPriceEditorController extends BaseController implements Initializable, INodeManipulator {

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

    private final Translator translator;

    private String title;
    private DraggableSpellNode rootNode;

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
                node = new ConstantDraggableSpellNode(this, translator);
                break;
            case PROPERTY_PRICE_TYPE_VARIABLE:
                node = new VariableDraggableSpellNode(this, translator);
                break;
            case PROPERTY_PRICE_TYPE_MODIFIER:
                node = new ModifierDraggableSpellNode(this, translator);
                break;
            default:
                return;
        }
        if (this.rootNode == null) {
            this.rootNode = node;
        }

        componentPlayground.getChildren().add(node);
        Point2D point = node.getParent().sceneToLocal(new Point2D(screenX, screenY));
        System.out.println(point);
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

    @Override
    public void addNode(Node node) {
        componentPlayground.getChildren().add(node);
    }

    @Override
    public void removeNode(Node node) {
        componentPlayground.getChildren().remove(node);
    }

    @Override
    public void setOnDragOverHandler(EventHandler<? super DragEvent> event) {
        if (event == null) {
            componentPlayground.setOnDragOver(this::onDragOver);
        } else {
            componentPlayground.setOnDragOver(event);
        }
    }
}
