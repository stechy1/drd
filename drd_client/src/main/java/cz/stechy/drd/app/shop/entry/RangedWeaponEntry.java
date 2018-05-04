package cz.stechy.drd.app.shop.entry;

import cz.stechy.drd.model.item.RangedWeapon;
import cz.stechy.drd.model.item.RangedWeapon.RangedWeaponType;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Položka v obchodě představující zbraň na dálku
 */
public final class RangedWeaponEntry extends ShopEntry {

    // region Variables

    private final IntegerProperty strength = new SimpleIntegerProperty(this, "strength");
    private final IntegerProperty rampancy = new SimpleIntegerProperty(this, "rampancy");
    private final ObjectProperty<RangedWeaponType> weaponType = new SimpleObjectProperty<>(this,
        "weaponType",
        RangedWeaponType.FIRE);
    private final IntegerProperty rangeLow = new SimpleIntegerProperty(this, "rangeLow");
    private final IntegerProperty rangeMedium = new SimpleIntegerProperty(this, "rangeMedium");
    private final IntegerProperty rangeLong = new SimpleIntegerProperty(this, "rangeLong");

    // endregion

    // region Constructors

    /**
     * Vytvoří novou nákupní položku obsahující zbraň na dálku
     *
     * @param weapon Zbraň na dálku
     */
    public RangedWeaponEntry(RangedWeapon weapon) {
        super(weapon);

        strength.bind(weapon.strengthProperty());
        rampancy.bind(weapon.rampancyProperty());
        weaponType.bind(weapon.weaponTypeProperty());
        rangeLow.bind(weapon.rangeLowProperty());
        rangeMedium.bind(weapon.rangeMediumProperty());
        rangeLong.bind(weapon.rangeLongProperty());
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

    public RangedWeaponType getWeaponType() {
        return weaponType.get();
    }

    public ObjectProperty<RangedWeaponType> weaponTypeProperty() {
        return weaponType;
    }

    public void setWeaponType(RangedWeaponType weaponType) {
        this.weaponType.set(weaponType);
    }

    public int getRangeLow() {
        return rangeLow.get();
    }

    public IntegerProperty rangeLowProperty() {
        return rangeLow;
    }

    public void setRangeLow(int rangeLow) {
        this.rangeLow.set(rangeLow);
    }

    public int getRangeMedium() {
        return rangeMedium.get();
    }

    public IntegerProperty rangeMediumProperty() {
        return rangeMedium;
    }

    public void setRangeMedium(int rangeMedium) {
        this.rangeMedium.set(rangeMedium);
    }

    public int getRangeLong() {
        return rangeLong.get();
    }

    public IntegerProperty rangeLongProperty() {
        return rangeLong;
    }

    public void setRangeLong(int rangeLong) {
        this.rangeLong.set(rangeLong);
    }

    // endregion
}
