package cz.stechy.drd.model.inventory.container;

import cz.stechy.drd.model.inventory.ItemContainer;
import cz.stechy.drd.model.inventory.ItemSlot;
import cz.stechy.drd.model.inventory.TooltipTranslator;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 * Základní inventář obshující mřížku s itemy
 */
public class GridItemContainer extends ItemContainer {

    // region Variables

    // Node, který reprezentuje jeden inventář
    private final GridPane container = new GridPane();
    private final int cols;
    private final int rows;

    // endregion

    // region Constructors

    /**
     * Inicializuje inventář
     *
     * @param tooltipTranslator {@link TooltipTranslator}
     * @param capacity Počet slotů v inventáři
     * @param cols Počet sloupečků
     * @param rows Počet řádků
     */
    public GridItemContainer(TooltipTranslator tooltipTranslator, int capacity, int cols, int rows) {
        super(tooltipTranslator, capacity);

        this.cols = cols;
        this.rows = rows;

        container.setHgap(SLOT_SPACING);
        container.setVgap(SLOT_SPACING);

        init();
    }

    // endregion

    // region Private methods

    private void init() {
        int remaining = capacity;
        int index = 0;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (remaining == 0) {
                    break;
                }
                ItemSlot slot = new ItemSlot(index++, dragDropHandlers);
                addItemSlot(slot);
                container.add(slot.getContainer(), x, y);

                remaining--;
            }

            if (remaining == 0) {
                break;
            }
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
