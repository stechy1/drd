package cz.stechy.drd.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

/**
 * Třída obsahující tři hodnoty
 * Maximální, minimální a aktuální hodnotu
 * Aktuální hodnota nemůže překročit hranice minimální a maximální hodnoty
 */
public final class MaxActValue {

    // region Variables

    private final ObjectProperty<Number> minValue = new SimpleObjectProperty<>(0);
    private final ObjectProperty<Number> maxValue = new SimpleObjectProperty<>(0);
    private final ObjectProperty<Number> actValue = new SimpleObjectProperty<>(0);
    // Možnost přetečení hodnoty - bude se ignorovat horní interval
    private final BooleanProperty overflow = new SimpleBooleanProperty(this, "overflow", false);
    // Možnost podtečení hodnoty - bude se ignorovat dolní interval
    private final BooleanProperty underflow = new SimpleBooleanProperty(this, "underflow",false);

    // Pomocná proměnná pro zabránění nekonečného nastavování hodnoty
    private boolean locked = false;

    // endregion

    // region Constructors

    public MaxActValue() {
        this(0, 100, 0);
    }

    public MaxActValue(Number maxValue) {
        this(0, maxValue, 0);
    }

    /**
     * Vytvoří novou instanci třídy představující intervalovou hodnotu
     *
     * @param minValue Minimální hodnota
     * @param maxValue Maximální hodnota
     * @param actValue Aktuální hodnota
     */
    public MaxActValue(Number minValue, Number maxValue, Number actValue) {
        this.actValue.addListener(valueListener);
        setMinValue(minValue);
        setMaxValue(maxValue);
        setActValue(actValue);
    }

    // endregion

    // region Public methods

    /**
     * Přidá zadaný počet
     *
     * @param ammount Počet, který se má přičíst k aktuální hodnotě
     */
    public void add(int ammount) {
        int act = actValue.get().intValue();
        actValue.set(act + ammount);
    }

    /**
     * Odebere zadaný počet
     *
     * @param ammount Počet, který se má odečíst od aktuální hodnoty
     */
    public void subtract(int ammount) {
        int act = actValue.get().intValue();
        actValue.set(act - ammount);
    }

    /**
     * Aktualizuje hodnoty v aktuální instanci
     *
     * @param other Instance, ze které se mají aktualizovat hodnoty
     */
    public void update(MaxActValue other) {
        this.setMaxValue(other.getMaxValue());
        this.setMinValue(other.getMinValue());
        this.setActValue(other.getActValue());
        this.setOverflow(other.canOverflow());
        this.setUnderflow(other.canUnderflow());
    }

    // endregion

    // region Getters & Setters

    public Number getMinValue() {
        return minValue.get();
    }

    public ObjectProperty<Number> minValueProperty() {
        return minValue;
    }

    public void setMinValue(Number minValue) {
        this.minValue.set(minValue);
    }

    public Number getMaxValue() {
        return maxValue.get();
    }

    public ObjectProperty<Number> maxValueProperty() {
        return maxValue;
    }

    public void setMaxValue(Number maxValue) {
        this.maxValue.set(maxValue);
    }

    public Number getActValue() {
        return actValue.get();
    }

    public ObjectProperty<Number> actValueProperty() {
        return actValue;
    }

    public void setActValue(Number actValue) {
        this.actValue.set(actValue);
    }

    public ChangeListener<Number> getValueListener() {
        return valueListener;
    }

    public final boolean canOverflow() {
        return overflow.get();
    }

    public final BooleanProperty overflowProperty() {
        return overflow;
    }

    public final void setOverflow(boolean overflow) {
        this.overflow.set(overflow);
    }

    public final boolean canUnderflow() {
        return underflow.get();
    }

    public final BooleanProperty underflowProperty() {
        return underflow;
    }

    public final void setUnderflow(boolean underflow) {
        this.underflow.set(underflow);
    }

    // endregion

    private final ChangeListener<Number> valueListener = (observable, oldValue, newValue) -> {
        if (newValue == null) {
            return;
        }

        if (locked) {
            return;
        }

        locked = true;

        int value = newValue.intValue();
        if (value > getMaxValue().doubleValue() && !canOverflow()) {
            setActValue(getMaxValue());
        } else if (value < getMinValue().doubleValue() && !canUnderflow()) {
            setActValue(getMinValue());
        }

        locked = false;
    };
}
