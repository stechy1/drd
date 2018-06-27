package cz.stechy.drd.model.inventory.container;

import cz.stechy.drd.R;
import cz.stechy.drd.R.Images.Icon;
import cz.stechy.drd.model.inventory.InventoryRecord;
import cz.stechy.drd.model.inventory.ItemContainer;
import cz.stechy.drd.model.inventory.ItemSlot;
import cz.stechy.drd.model.inventory.ItemSlotHelper;
import cz.stechy.drd.model.inventory.TooltipTranslator;
import cz.stechy.drd.service.ItemRegistry;
import java.util.function.Predicate;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

/**
 * Inventář obsahující vybavení postavy
 * Vybavení zahrnuje zbroj, zbraň a případně i štít
 */
public class EquipItemContainer extends ItemContainer {

    // region Constants

    private static final Image SLOT_BACKGROUND_SWORD = new Image(
        R.class.getResourceAsStream(Icon.CLOSE));
    private static final Image SLOT_BACKGROUND_SHIELD = new Image(
        R.class.getResourceAsStream(Icon.CLOSE));
    private static final Image SLOT_BACKGROUND_HELM = new Image(
        R.class.getResourceAsStream(Icon.CLOSE));
    private static final Image SLOT_BACKGROUND_BODY = new Image(
        R.class.getResourceAsStream(Icon.CLOSE));
    private static final Image SLOT_BACKGROUND_LEGS = new Image(
        R.class.getResourceAsStream(Icon.CLOSE));
    private static final Image SLOT_BACKGROUND_BOTS = new Image(
        R.class.getResourceAsStream(Icon.CLOSE));
    private static final Image SLOT_BACKGROUND_GLOVES = new Image(
        R.class.getResourceAsStream(Icon.CLOSE));

    // Počet slotů v inventáři
    public static final int CAPACITY = 7;

    public static final Predicate<InventoryRecord> SLOT_SWORD = o -> o.getSlotId() == 0;
    public static final Predicate<InventoryRecord> SLOT_SHIELD = o -> o.getSlotId() == 1;
    public static final Predicate<InventoryRecord> SLOT_HELM = o -> o.getSlotId() == 2;
    public static final Predicate<InventoryRecord> SLOT_BODY = o -> o.getSlotId() == 3;
    public static final Predicate<InventoryRecord> SLOT_LEGS = o -> o.getSlotId() == 4;
    public static final Predicate<InventoryRecord> SLOT_BOTS = o -> o.getSlotId() == 5;
    public static final Predicate<InventoryRecord> SLOT_GLOVES = o -> o.getSlotId() == 6;

    // endregion

    // region Variables

    private final GridPane container = new GridPane();

    private int idCounter = 0;

    // endregion

    // region Constructors

    /**
     * Inicializuje kontainer pro výbavu postavy.
     *
     * @param itemRegistry
     * @param tooltipTranslator {@link TooltipTranslator}
     */
    public EquipItemContainer(ItemRegistry itemRegistry, TooltipTranslator tooltipTranslator) {
        super(itemRegistry, tooltipTranslator, CAPACITY);

        init();
    }

    // endregion

    // region Private methods

    private void init() {
        container.setHgap(1);
        container.setVgap(1);
        BorderPane.setAlignment(container, Pos.CENTER);
        container.prefHeight(100);
        container.setStyle("-fx-background-color: orange");

        final ItemSlot slotSword = ItemSlotHelper.forWeapon(idCounter++, dragDropHandlers);
        final ItemSlot slotShield = ItemSlotHelper.forShield(idCounter++, dragDropHandlers);
        final ItemSlot slotHelm = ItemSlotHelper.forArmorHelm(idCounter++, dragDropHandlers);
        final ItemSlot slotBody = ItemSlotHelper.forArmorBody(idCounter++, dragDropHandlers);
        final ItemSlot slotLegs = ItemSlotHelper.forArmorLegs(idCounter++, dragDropHandlers);
        final ItemSlot slotBots = ItemSlotHelper.forArmorBots(idCounter++, dragDropHandlers);
        final ItemSlot slotGloves = ItemSlotHelper.forArmorGloves(idCounter++, dragDropHandlers);

        slotSword.setBackgroundImage(SLOT_BACKGROUND_SWORD);
        slotShield.setBackgroundImage(SLOT_BACKGROUND_SHIELD);
        slotHelm.setBackgroundImage(SLOT_BACKGROUND_HELM);
        slotBody.setBackgroundImage(SLOT_BACKGROUND_BODY);
        slotLegs.setBackgroundImage(SLOT_BACKGROUND_LEGS);
        slotBots.setBackgroundImage(SLOT_BACKGROUND_BOTS);
        slotGloves.setBackgroundImage(SLOT_BACKGROUND_GLOVES);

        itemSlots.setAll(slotSword, slotShield, slotHelm, slotBody, slotLegs, slotBots, slotGloves);

        container.add(slotHelm.getContainer(), 1, 0);
        container.add(slotBody.getContainer(), 1, 1);
        container.add(slotLegs.getContainer(), 1, 2);
        container.add(slotBots.getContainer(), 1, 3);
        container.add(slotGloves.getContainer(), 0, 2);
        container.add(slotSword.getContainer(), 0, 1);
        container.add(slotShield.getContainer(), 2, 1);
    }

    // endregion

    @Override
    public Node getGraphics() {
        return container;
    }
}
