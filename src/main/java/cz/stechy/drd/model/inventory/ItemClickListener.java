package cz.stechy.drd.model.inventory;

/**
 * Rozhraní pro metody reagující na kliknutí na item v inventáři
 */
public interface ItemClickListener {

    void onClick(ItemSlot itemSlot);

}
