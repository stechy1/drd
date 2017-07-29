package cz.stechy.drd.model.shop.entry;

import cz.stechy.drd.model.entity.Height;
import cz.stechy.drd.model.item.Armor;
import cz.stechy.drd.model.item.Armor.ArmorType;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Položka v obchodě představující brnění
 */
public class ArmorEntry extends ShopEntry {

    private final IntegerProperty defenceNumber = new SimpleIntegerProperty();
    private final IntegerProperty minimumStrength = new SimpleIntegerProperty();
    private final ObjectProperty<ArmorType> armorType = new SimpleObjectProperty<>();

    // endregion

    // region Constructors

    /**
     * Vytvoří novou nákupní položku obsahující zbroj
     *
     * @param armor Zbroj
     * @param height Velikost bytosti, která zbroj kupuje
     */
    public ArmorEntry(Armor armor, ObjectProperty<Height> height) {
        super(armor);

        this.defenceNumber.bind(armor.defenceNumberProperty());
        this.minimumStrength.bind(armor.minimumStrengthProperty());
        this.armorType.bind(armor.typeProperty());

        height.addListener((observable, oldValue, newValue) -> armor.forHeight(newValue));
    }

    // endregion

    // region Getters & Setters

    public int getDefenceNumber() {
        return defenceNumber.get();
    }

    public IntegerProperty defenceNumberProperty() {
        return defenceNumber;
    }

    public void setDefenceNumber(int defenceNumber) {
        this.defenceNumber.set(defenceNumber);
    }

    public int getMinimumStrength() {
        return minimumStrength.get();
    }

    public IntegerProperty minimumStrengthProperty() {
        return minimumStrength;
    }

    public void setMinimumStrength(int minimumStrength) {
        this.minimumStrength.set(minimumStrength);
    }

    public ArmorType getArmorType() {
        return armorType.get();
    }

    public ObjectProperty<ArmorType> armorTypeProperty() {
        return armorType;
    }

    public void setArmorType(ArmorType armorType) {
        this.armorType.set(armorType);
    }

    // endregion
}
