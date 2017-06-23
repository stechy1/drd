package cz.stechy.drd.model.item;

import cz.stechy.drd.model.db.base.DatabaseItem;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Základní třída pro všechny zbraně
 */
public abstract class WeaponBase extends ItemBase {

    // region Variables

    // Síla zbraně
    protected final IntegerProperty strength = new SimpleIntegerProperty(this, "strength");
    // Útočnost zbraně
    protected final IntegerProperty rampancy = new SimpleIntegerProperty(this, "rampancy");

    // endregion

    // region Constructors

    /**
     * Konstruktor pro všechny zbraně
     *  @param id Id zbraně
     * @param name Název zbraně
     * @param description Popis zbraně
     * @param weight Váha zbraně
     * @param price Cena zbraně
     * @param strength Síla zbraně
     * @param rampancy Útočnost zbraně
     * @param author Autor zbraně
     * @param image Obrázek zbraně
     * @param stackSize Maximální počet zbraní, který může být v jednom stacku ve slotu inventáře
     * @param downloaded Příznak určující, zda-li je položka uložena v offline databázi, či nikoliv
     * @param uploaded Příznak určující, zda-li je položka nahrána v online databázi, či nikoliv
     */
    protected WeaponBase(String id, String name, String description, int weight, int price,
        int strength, int rampancy, String author, byte[] image, int stackSize, boolean downloaded,
        boolean uploaded) {
        super(id, author, name, description, weight, price, image, stackSize, downloaded, uploaded);

        setStrength(strength);
        setRampancy(rampancy);
    }

    // endregion

    // region Public methods

    @Override
    public void update(DatabaseItem other) {
        super.update(other);

        WeaponBase weapon = (WeaponBase) other;
        setStrength(weapon.getStrength());
        setRampancy(weapon.getRampancy());
    }

    // endregion

    // region Getters & Setters

    public final int getStrength() {
        return strength.get();
    }

    public final ReadOnlyIntegerProperty strengthProperty() {
        return strength;
    }

    private void setStrength(int strength) {
        this.strength.set(strength);
    }

    public final int getRampancy() {
        return rampancy.get();
    }

    public final ReadOnlyIntegerProperty rampancyProperty() {
        return rampancy;
    }

    private void setRampancy(int rampancy) {
        this.rampancy.set(rampancy);
    }

    // endregion
}
