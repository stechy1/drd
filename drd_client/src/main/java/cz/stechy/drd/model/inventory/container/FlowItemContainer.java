package cz.stechy.drd.model.inventory.container;

import cz.stechy.drd.model.inventory.ItemContainer;
import cz.stechy.drd.model.inventory.ItemSlot;
import cz.stechy.drd.model.inventory.TooltipTranslator;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;

/**
 * Inventář založený na {@link javafx.scene.layout.FlowPane} kontejneru
 */
public class FlowItemContainer extends ItemContainer {

    // region Variables

    private final FlowPane container = new FlowPane();

    // endregion

    // region Constructors

    public FlowItemContainer(TooltipTranslator tooltipTranslator, int capacity) {
        super(tooltipTranslator, capacity);

        container.setHgap(SLOT_SPACING);
        container.setVgap(SLOT_SPACING);
        init();
    }

    // endregion

    // region Private methods

    private void init() {
        final ObservableList<Node> children = container.getChildren();
        for (int i = 0; i < capacity; i++) {
            final ItemSlot slot = new ItemSlot(i, dragDropHandlers);
            addItemSlot(slot);
            children.add(slot.getContainer());
        }
    }

    // endregion

    // region Public methods

    // endregion

    @Override
    public Node getGraphics() {
        return container;
    }
}
