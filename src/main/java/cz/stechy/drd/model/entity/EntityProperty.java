package cz.stechy.drd.model.entity;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.value.ObservableValue;

/**
 * Třída představující jednu vlastnost entity a její opravu
 */
public abstract class EntityProperty {

    // region Variables

    // Samotná hodnota vlaastnosti
    protected final ReadOnlyIntegerWrapper value = new ReadOnlyIntegerWrapper(0);
    // Oprava hodnoty vlastnosti
    protected final ReadOnlyIntegerWrapper repair = new ReadOnlyIntegerWrapper(0);
    // Spodní hranice intervalu hodnoty vlastnosti
    protected int minValue;
    // Horní hranice intervalu hodnoty valstnosti
    protected int maxValue;

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

    // region Public methods

    /**
     * Aktualizuje všechny hodnoty
     *
     * @param entityProperty {@link EntityProperty}
     */
    public void update(EntityProperty entityProperty) {
        this.minValue = entityProperty.minValue;
        this.maxValue = entityProperty.maxValue;
        setValue(entityProperty.getValue());
    }

    /**
     * Nabinduje hodnotu
     *
     * @param binding
     */
    public void bindTo(ObservableValue<Number> binding) {
        this.value.bind(binding);
    }

    // endregion

    // region Getters & Setters

    public int getValue() {
        return value.get();
    }

    public ReadOnlyIntegerProperty valueProperty() {
        return value.getReadOnlyProperty();
    }

    public void setValue(Number value) {
        this.value.setValue(value);
    }

    public int getRepair() {
        return repair.get();
    }

    public ReadOnlyIntegerProperty repairProperty() {
        return repair.getReadOnlyProperty();
    }

    public void setRepair(int repair) {
        this.repair.set(repair);
    }

    // endregion
}