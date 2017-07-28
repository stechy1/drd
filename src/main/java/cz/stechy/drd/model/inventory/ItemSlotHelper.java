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
    private static final Predicate<ItemBase> helmFilter = itemBase ->
        itemBase.getItemType() == ItemType.ARMOR &&
            ((Armor) itemBase).getType() == ArmorType.HELM;

    // Filtr pro hrudní brnění
    private static final Predicate<ItemBase> bodyFilter = itemBase ->
        itemBase.getItemType() == ItemType.ARMOR &&
            ((Armor) itemBase).getType() == ArmorType.BODY;

    // Filtr pro kalhoty
    private static final Predicate<ItemBase> legsFilter = itemBase ->
        itemBase.getItemType() == ItemType.ARMOR &&
            ((Armor) itemBase).getType() == ArmorType.LEGS;

    // Filtr pro boty
    private static final Predicate<ItemBase> botsFilter = itemBase ->
        itemBase.getItemType() == ItemType.ARMOR &&
            ((Armor) itemBase).getType() == ArmorType.BOTS;

    // Filtr pro rukavice
    private static final Predicate<ItemBase> glovesFilter = itemBase ->
        itemBase.getItemType() == ItemType.ARMOR &&
            ((Armor) itemBase).getType() == ArmorType.GLOVES;

    // Filtr pro zbraně
    private static final Predicate<ItemBase> weaponFilter = itemBase ->
        ItemType.isSword(itemBase.getItemType());

    // Filtr pro štíty
    private static final Predicate<ItemBase> shieldFilter = itemBase -> true;

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
        return createSlot(id, dragDropHandlers, helmFilter);
    }

    /**
     * Vytvoří {@link ItemSlot}, s filtrem na hrudní brnění.
     *
     * @param id Id slotu
     * @param dragDropHandlers {@link DragDropHandlers}
     * @return
     */
    public static ItemSlot forArmorBody(int id, DragDropHandlers dragDropHandlers) {
        return createSlot(id, dragDropHandlers, bodyFilter);
    }

    /**
     * Vytvoří {@link ItemSlot}, s filtrem na kalhoty.
     *
     * @param id Id slotu
     * @param dragDropHandlers {@link DragDropHandlers}
     * @return
     */
    public static ItemSlot forArmorLegs(int id, DragDropHandlers dragDropHandlers) {
        return createSlot(id, dragDropHandlers, legsFilter);
    }

    /**
     * Vytvoří {@link ItemSlot}, s filtrem na boty.
     *
     * @param id Id slotu
     * @param dragDropHandlers {@link DragDropHandlers}
     * @return
     */
    public static ItemSlot forArmorBots(int id, DragDropHandlers dragDropHandlers) {
        return createSlot(id, dragDropHandlers, botsFilter);
    }

    /**
     * Vytvoří {@link ItemSlot}, s filtrem na rukavice.
     *
     * @param id Id slotu
     * @param dragDropHandlers {@link DragDropHandlers}
     * @return
     */
    public static ItemSlot forArmorGloves(int id, DragDropHandlers dragDropHandlers) {
        return createSlot(id, dragDropHandlers, glovesFilter);
    }

    /**
     * Vytvoří {@link ItemSlot}, s filtrem na zbraně.
     *
     * @param id Id slotu
     * @param dragDropHandlers {@link DragDropHandlers}
     * @return
     */
    public static ItemSlot forWeapon(int id, DragDropHandlers dragDropHandlers) {
        return createSlot(id, dragDropHandlers, weaponFilter);
    }

    /**
     * Vytvoří {@link ItemSlot}, s filtrem na štít.
     *
     * @param id Id slotu
     * @param dragDropHandlers {@link DragDropHandlers}
     * @return
     */
    public static ItemSlot forShield(int id, DragDropHandlers dragDropHandlers) {
        return createSlot(id, dragDropHandlers, shieldFilter);
    }

    // endregion

}
