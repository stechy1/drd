package cz.stechy.drd.model.inventory;

/**
 * Rozhraní pro metody reagující na kliknutí na item v inventáři
 */
public interface ItemClickListener {

    /**
     * Metoda se zavolá při kliknutí na slot s předmětem
     *
     * @param itemSlot {@link ItemSlot} na který se kliklo
     */
    void onClick(ItemSlot itemSlot);

}
