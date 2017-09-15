package cz.stechy.drd.model;

import cz.stechy.drd.util.BitUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Pomocná třída obsahující validační prvky
 */
public abstract class ValidatedModel {

    protected final IntegerProperty validityFlag = new SimpleIntegerProperty();
    protected final BooleanProperty valid = new SimpleBooleanProperty();
    protected final BooleanProperty changed = new SimpleBooleanProperty();

    // region Public methods

    /**
     * Nastaví validitu zadanému příznaku
     *
     * @param flag Příznak
     * @param value True, pokud je příznak validní, jinak false
     */
    public void setValidityFlag(int flag, boolean value) {
        int oldFlagValue = validityFlag.get();
        int newFlag = BitUtils.setBit(oldFlagValue, flag, value);

        if (newFlag == oldFlagValue) {
            return;
        }

        setChanged(true);

        validityFlag.set(newFlag);
        if (newFlag == 0) {
            setValid(true);
        }
    }

    // endregion

    // region Getters & Setters

    public void setValid(boolean valid) {
        this.valid.set(valid);
    }

    public BooleanProperty validProperty() {
        return valid;
    }

    public void setChanged(boolean changed) {
        this.changed.set(changed);
    }

    public BooleanProperty changedProperty() {
        return changed;
    }

    // endregion

}
