package cz.stechy.drd.model.inventory;

import cz.stechy.drd.model.inventory.InventoryRecord.Metadata;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.ItemRegistry.ItemException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Třída představuje stack jednoho předmětu, který se může vkládat do jednoho slotu v inventáři
 */
public class ItemStack {

    // region Constants

    private static final int MIN_AMMOUNT = 1;

    // endregion

    // region Variables

    private final ItemBase item;
    private final IntegerProperty ammount = new SimpleIntegerProperty();
    private final Metadata metadata;

    // endregion

    // region Constructors

    /**
     * Kopy konstruktor
     *
     * @param itemStack {@link ItemStack} Kopírovaný stack
     */
    public ItemStack(ItemStack itemStack) {
        this(itemStack.getItem(), itemStack.getAmmount(), itemStack.getMetadata());
    }

    /**
     * Vytvoří nový {@link ItemStack} s definovaným počtem itemů
     *  @param item {@link ItemBase}
     * @param ammount Počet itemů na stacku
     * @param metadata
     */
    public ItemStack(ItemBase item, int ammount, Metadata metadata) {
        this.item = item;
        this.ammount.set(ammount);
        this.metadata = metadata;
    }

    // endregion

    // region Public methods

    /**
     * Zvýší počet položek v tomto stacku
     *
     * @param ammount Přidávaný počet položek
     */
    public void addAmmount(int ammount) {
        //if (this.ammount.get() + ammount > )
        // TODO implementovat stack size
        this.ammount.set(this.ammount.get() + ammount);
    }

    /**
     * Odebere požadovaný počet itemů že stacku
     *
     * @param ammount Počet itemů, který se má odebrat
     */
    public void subtractAmmount(int ammount) throws ItemException {
        final int tmpAmmount = this.ammount.get();
        final int resAmmount = ammount - tmpAmmount;
        if (resAmmount < 0) {
            throw new ItemException("Not enought items to remove");
        }
        this.ammount.set(resAmmount);
    }

    /**
     * Zjistí, zda-li obsahuje item požadovaného typu
     *
     * @param other Item, který se testuje, jestli stack obsahuje
     * @return True, pokud jsou itemy shodné, jinak false
     */
    public boolean containsItemType(ItemBase other) {
        if (this.item == null) {
            return false;
        }

        return this.item.getItemType() == other.getItemType();
    }

    // endregion

    // region Getters & Setters

    public ItemBase getItem() {
        return item;
    }

    public int getAmmount() {
        return ammount.get();
    }

    public IntegerProperty ammountProperty() {
        return ammount;
    }

    public void setAmmount(int ammount) {
        this.ammount.set(ammount);
    }

    public Metadata getMetadata() {
        return metadata;
    }

    // endregion
}
