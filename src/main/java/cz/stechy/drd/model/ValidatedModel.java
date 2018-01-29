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

    // region Constants

    public static final int VALID_FLAG_VALUE = 0;

    // endregion

    // region Variables

    protected final IntegerProperty validityFlag = new SimpleIntegerProperty(this, "validityFlag", VALID_FLAG_VALUE);
    protected final BooleanProperty valid = new SimpleBooleanProperty(this, "valid", true);
    private final BooleanProperty changed = new SimpleBooleanProperty(this, "changed", false);

    // endregion

    {
        valid.bind(validityFlag.isEqualTo(VALID_FLAG_VALUE));
    }

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

        this.changed.setValue(true);

        validityFlag.set(newFlag);
    }

    // endregion

    // region Getters & Setters

    public BooleanProperty validProperty() {
        return valid;
    }

    public BooleanProperty changedProperty() {
        return changed;
    }

    // endregion

}
