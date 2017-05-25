package cz.stechy.drd.model.inventory;

import cz.stechy.drd.model.item.ItemType;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * Inventář obsahující vybavení postavy
 * Vybavení zahrnuje zbroj, zbraň a případně i štít
 */
public class EquipItemContainer extends ItemContainer {

    public static final int CAPACITY = 3;

    private HBox container;
    private int idCounter = 0;

    /**
     * Inicializuje kontainer pro výbavu postavy
     */
    public EquipItemContainer() {
        super(CAPACITY);

        init();
    }

    private void init() {
        container = new HBox();
        container.setSpacing(1);
        BorderPane.setAlignment(container, Pos.CENTER);
        container.prefHeight(100);

        ItemSlot slotSword = new ItemSlot(idCounter++, dragDropHandlers);
        slotSword.setFilter(itemBase -> ItemType.isSword(itemBase.getItemType()));
        ItemSlot slotArmor = new ItemSlot(idCounter++, dragDropHandlers);
        slotArmor.setFilter(itemBase -> itemBase.getItemType() == ItemType.ARMOR);
        ItemSlot slotShield = new ItemSlot(idCounter++, dragDropHandlers);

        itemSlots.addAll(slotSword, slotArmor, slotShield);
        container.getChildren().setAll(
            slotSword.getContainer(),
            slotArmor.getContainer(),
            slotShield.getContainer());
    }

    @Override
    public Node getGraphics() {
        return container;
    }
}
