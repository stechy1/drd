package cz.stechy.drd.app.shop.entry;

import cz.stechy.drd.model.item.MeleWeapon;
import cz.stechy.drd.model.item.MeleWeapon.MeleWeaponClass;
import cz.stechy.drd.model.item.MeleWeapon.MeleWeaponType;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Položka v obchodě představující zbraň na blízko
 */
public final class MeleWeaponEntry extends ShopEntry {

    // region Variables

    private final IntegerProperty strength = new SimpleIntegerProperty(this, "strength");
    private final IntegerProperty rampancy = new SimpleIntegerProperty(this, "rampancy");
    private final IntegerProperty defence = new SimpleIntegerProperty(this, "defence");
    private final ObjectProperty<MeleWeaponClass> weaponClass = new SimpleObjectProperty<>(this, "weaponClass",
        MeleWeaponClass.LIGHT);
    private final ObjectProperty<MeleWeaponType> weaponType = new SimpleObjectProperty<>(this, "weaponType",
        MeleWeaponType.ONE_HAND);

    // endregion

    // region Constructors

    /**
     * Vytvoří novou nákupní položku obsahující zbraň na blíko
     *
     * @param weapon {@link MeleWeapon}
     */
    public MeleWeaponEntry(MeleWeapon weapon) {
        super(weapon);

        strength.bind(weapon.strengthProperty());
        rampancy.bind(weapon.rampancyProperty());
        defence.bind(weapon.defenceProperty());
        weaponClass.bind(weapon.weaponClassProperty());
        weaponType.bind(weapon.weaponTypeProperty());
    }

    // endregion

    // region Getters & Setters

    public int getStrength() {
        return strength.get();
    }

    public IntegerProperty strengthProperty() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength.set(strength);
    }

    public int getRampancy() {
        return rampancy.get();
    }

    public IntegerProperty rampancyProperty() {
        return rampancy;
    }

    public void setRampancy(int rampancy) {
        this.rampancy.set(rampancy);
    }

    public int getDefence() {
        return defence.get();
    }

    public IntegerProperty defenceProperty() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence.set(defence);
    }

    public MeleWeaponClass getWeaponClass() {
        return weaponClass.get();
    }

    public ObjectProperty<MeleWeaponClass> weaponClassProperty() {
        return weaponClass;
    }

    public void setWeaponClass(MeleWeaponClass weaponClass) {
        this.weaponClass.set(weaponClass);
    }

    public MeleWeaponType getWeaponType() {
        return weaponType.get();
    }

    public ObjectProperty<MeleWeaponType> weaponTypeProperty() {
        return weaponType;
    }

    public void setWeaponType(MeleWeaponType weaponType) {
        this.weaponType.set(weaponType);
    }

    // endregion

}
