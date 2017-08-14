package cz.stechy.drd.model.inventory;

import cz.stechy.drd.model.inventory.ItemSlot.DragDropHandlers;
import cz.stechy.drd.model.item.Armor;
import cz.stechy.drd.model.item.Armor.ArmorType;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.ItemType;
import java.util.function.Predicate;

/**
 * Pomocná třída pro generování specializovaných slotů v inventáři
 */
public final class ItemSlotHelper {

    // region Constants

    // Filtr pro přilbu
    private static final Predicate<ItemBase> HELM_FILTER = itemBase -> {
        return itemBase.getItemType() == ItemType.ARMOR &&
            ((Armor) itemBase).getType() == ArmorType.HELM;
    };

    // Filtr pro hrudní brnění
    private static final Predicate<ItemBase> BODY_FILTER = itemBase ->
        itemBase.getItemType() == ItemType.ARMOR &&
            ((Armor) itemBase).getType() == ArmorType.BODY;

    // Filtr pro kalhoty
    private static final Predicate<ItemBase> LEGS_FILTER = itemBase ->
        itemBase.getItemType() == ItemType.ARMOR &&
            ((Armor) itemBase).getType() == ArmorType.LEGS;

    // Filtr pro boty
    private static final Predicate<ItemBase> BOTS_FILTER = itemBase ->
        itemBase.getItemType() == ItemType.ARMOR &&
            ((Armor) itemBase).getType() == ArmorType.BOTS;

    // Filtr pro rukavice
    private static final Predicate<ItemBase> GLOVES_FILTER = itemBase ->
        itemBase.getItemType() == ItemType.ARMOR &&
            ((Armor) itemBase).getType() == ArmorType.GLOVES;

    // Filtr pro zbraně
    private static final Predicate<ItemBase> WEAPON_FILTER = itemBase ->
        ItemType.isSword(itemBase.getItemType());

    // Filtr pro štíty
    private static final Predicate<ItemBase> SHIELD_FILTER = itemBase -> false;

    // endregion

    // region Constructors

    private ItemSlotHelper() {}

    // endregion

    // region Public static methods

    /**
     * Vytvoří slot se zadaným filtrem.
     *
     * @param id Id slotu
     * @param dragDropHandlers {@link DragDropHandlers}
     * @param filter Filter, který určí, jaký typ předmětu lze do slotu umístit
     * @return
     */
    private static ItemSlot createSlot(int id, DragDropHandlers dragDropHandlers, Predicate<ItemBase> filter) {
        final ItemSlot itemSlot = new ItemSlot(id, dragDropHandlers);
        itemSlot.setFilter(filter);
        return itemSlot;
    }

    /**
     * Vytvoří {@link ItemSlot}, s filtrem na přilby.
     *
     * @param id Id slotu
     * @param dragDropHandlers {@link DragDropHandlers}
     * @return
     */
    public static ItemSlot forArmorHelm(int id, DragDropHandlers dragDropHandlers) {
        return createSlot(id, dragDropHandlers, HELM_FILTER);
    }

    /**
     * Vytvoří {@link ItemSlot}, s filtrem na hrudní brnění.
     *
     * @param id Id slotu
     * @param dragDropHandlers {@link DragDropHandlers}
     * @return
     */
    public static ItemSlot forArmorBody(int id, DragDropHandlers dragDropHandlers) {
        return createSlot(id, dragDropHandlers, BODY_FILTER);
    }

    /**
     * Vytvoří {@link ItemSlot}, s filtrem na kalhoty.
     *
     * @param id Id slotu
     * @param dragDropHandlers {@link DragDropHandlers}
     * @return
     */
    public static ItemSlot forArmorLegs(int id, DragDropHandlers dragDropHandlers) {
        return createSlot(id, dragDropHandlers, LEGS_FILTER);
    }

    /**
     * Vytvoří {@link ItemSlot}, s filtrem na boty.
     *
     * @param id Id slotu
     * @param dragDropHandlers {@link DragDropHandlers}
     * @return
     */
    public static ItemSlot forArmorBots(int id, DragDropHandlers dragDropHandlers) {
        return createSlot(id, dragDropHandlers, BOTS_FILTER);
    }

    /**
     * Vytvoří {@link ItemSlot}, s filtrem na rukavice.
     *
     * @param id Id slotu
     * @param dragDropHandlers {@link DragDropHandlers}
     * @return
     */
    public static ItemSlot forArmorGloves(int id, DragDropHandlers dragDropHandlers) {
        return createSlot(id, dragDropHandlers, GLOVES_FILTER);
    }

    /**
     * Vytvoří {@link ItemSlot}, s filtrem na zbraně.
     *
     * @param id Id slotu
     * @param dragDropHandlers {@link DragDropHandlers}
     * @return
     */
    public static ItemSlot forWeapon(int id, DragDropHandlers dragDropHandlers) {
        return createSlot(id, dragDropHandlers, WEAPON_FILTER);
    }

    /**
     * Vytvoří {@link ItemSlot}, s filtrem na štít.
     *
     * @param id Id slotu
     * @param dragDropHandlers {@link DragDropHandlers}
     * @return
     */
    public static ItemSlot forShield(int id, DragDropHandlers dragDropHandlers) {
        return createSlot(id, dragDropHandlers, SHIELD_FILTER);
    }

    // endregion

}
