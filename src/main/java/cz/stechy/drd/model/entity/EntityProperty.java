package cz.stechy.drd.model.entity;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Třída představující jednu vlastnost entity a její opravu
 */
public abstract class EntityProperty {


    // region Variables

    // Samotná hodnota vlaastnosti
    protected final IntegerProperty value = new SimpleIntegerProperty(0);
    // Oprava hodnoty vlastnosti
    protected final IntegerProperty repair = new SimpleIntegerProperty(0);
    // Spodní hranice intervalu hodnoty vlastnosti
    protected final int minValue;
    // Horní hranice intervalu hodnoty valstnosti
    protected final int maxValue;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou vlastnost entity
     *
     * @param minValue Spodní hranice intervalu hodnoty vlastnosti
     * @param maxValue Horní hranice intervalu hodnoty valstnosti
     */
    protected EntityProperty(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    // endregion

    // region Private methods

    /**
     * Omezi hodnotu zhora i zdola
     *
     * @param value Hodnota, která má být omezena
     * @return Ohraničenou hodnotu v nastaveném intervalu
     */
    protected Number limit(Number value) {
        return Math.min(Math.max(value.doubleValue(), minValue), maxValue);
    }

    // endregion

    // region Getters & Setters

    public int getValue() {
        return value.get();
    }

    public IntegerProperty valueProperty() {
        return value;
    }

    public void setValue(Number value) {
        this.value.setValue(value);
    }

    public int getRepair() {
        return repair.get();
    }

    public IntegerProperty repairProperty() {
        return repair;
    }

    public void setRepair(int repair) {
        this.repair.set(repair);
    }

    // endregion
}