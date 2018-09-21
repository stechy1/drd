package cz.stechy.drd.model.entity;

import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;

/**
 * Třída představující jednu vlastnost entity a její opravu
 */
public abstract class EntityProperty {

    // region Constants

    private static final Object DEFAULT_BEAN = null;
    private static final String DEFAULT_NAME = "";

    // endregion

    // region Variables

    // Samotná hodnota vlaastnosti
    protected final IntegerProperty value = new SimpleIntegerProperty(this, "value", 0);
    // Oprava hodnoty vlastnosti
    protected final IntegerProperty repair = new SimpleIntegerProperty(this, "value", 0);
    // Spodní hranice intervalu hodnoty vlastnosti
    protected int minValue;
    // Horní hranice intervalu hodnoty valstnosti
    protected int maxValue;

    // Objekt vlastnící tuto vlastnost
    private final Object bean;
    // Název této vlastnosti
    private final String name;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou vlastnost entity
     *
     * @param minValue Spodní hranice intervalu hodnoty vlastnosti
     * @param maxValue Horní hranice intervalu hodnoty valstnosti
     */
    protected EntityProperty(int minValue, int maxValue) {
        this(DEFAULT_BEAN, DEFAULT_NAME, minValue, maxValue);
    }

    /**
     * Vytvoří novou vlastnost entity
     *
     * @param bean Objekt vlastnící tuto vlastnost
     * @param name Název vlastnosti
     * @param minValue Spodní hranice intervalu hodnoty vlastnosti
     * @param maxValue Horní hranice intervalu hodnoty valstnosti
     */
    protected EntityProperty(Object bean, String name, int minValue, int maxValue) {
        this.bean = bean;
        this.name = name;
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
     * @param binding {@link ObservableValue}
     */
    public void bindTo(ObservableValue<Number> binding) {
        this.value.bind(binding);
    }

    // endregion

    // region Getters & Setters

    public Object getBean() {
        return bean;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value.get();
    }

    public ReadOnlyIntegerProperty valueProperty() {
        return value;
    }

    public void setValue(Number value) {
        this.value.setValue(value);
    }

    public int getRepair() {
        return repair.get();
    }

    public ReadOnlyIntegerProperty repairProperty() {
        return repair;
    }

    public void setRepair(int repair) {
        this.repair.set(repair);
    }

    // endregion

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityProperty that = (EntityProperty) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    @Override
    public String toString() {
        return Integer.toString(getValue());
    }
}