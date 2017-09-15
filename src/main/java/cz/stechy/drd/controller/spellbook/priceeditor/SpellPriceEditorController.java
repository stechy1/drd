package cz.stechy.drd.controller.spellbook.priceeditor;

import cz.stechy.drd.R;
import cz.stechy.drd.util.Translator;
import cz.stechy.screens.BaseController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
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

    private final Translator translator;
    private DraggableSpellNode rootNode;

    // endregion

    // region Constructors

    public SpellPriceEditorController(Translator translator) {
        this.translator = translator;
    }

    // endregion

    // region Private methods

    private void initDragHandlers(Label label) {
        label.setOnDragDetected(onDragDetected);
    }

    private void addNode(int type, double screenX, double screenY) {
        DraggableSpellNode node;
        switch (type) {
            case PROPERTY_PRICE_TYPE_CONSTANT:
                node = new ConstantDraggableSpellNode(translator, screenX, screenY);
                break;
            case PROPERTY_PRICE_TYPE_VARIABLE:
                node = new VariableDraggableSpellNode(translator, screenX, screenY);
                break;
            case PROPERTY_PRICE_TYPE_MODIFIER:
                node = new ModifierDraggableSpellNode(translator, screenX, screenY);
                break;
            default:
                return;
        }
        if (this.rootNode == null) {
            this.rootNode = node;
        }

        componentPlayground.getChildren().add(node);
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (int i = 0; i < LABEL_CAPTIONS.length; i++) {
            final String key = LABEL_CAPTIONS[i];
            final Label label = new Label(resources.getString(key));
            label.getProperties().put(PROPERTY_PRICE_TYPE, i);
            initDragHandlers(label);
            componentSourceContainer.getChildren().add(label);
        }

        componentPlayground.setOnDragDropped(onDragDropped);
        componentPlayground.setOnDragOver(onDragOver);
    }

    private final EventHandler<? super MouseEvent> onDragDetected = event -> {
        final Label source = (Label) event.getSource();
        final Dragboard db = source.startDragAndDrop(TransferMode.ANY);
        final ClipboardContent content = new ClipboardContent();
        final int propertyType = (int) source.getProperties().get(PROPERTY_PRICE_TYPE);
        content.putString(String.valueOf(propertyType));
        db.setContent(content);

        event.consume();
    };

    private final EventHandler<? super DragEvent> onDragOver = event -> {
        if (event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.ANY);
        }

        event.consume();
    };
    private final EventHandler<? super DragEvent> onDragDropped = event -> {
        Dragboard db = event.getDragboard();
        if (db.hasString()) {
            event.setDropCompleted(true);
            final String type = db.getString();
            addNode(Integer.parseInt(type), event.getScreenX(), event.getScreenY());
        }

        event.consume();
    };
}
