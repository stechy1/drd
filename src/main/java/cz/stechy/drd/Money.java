package cz.stechy.drd;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Třída reprezentující měnu světa
 */
public final class Money {

    // region Constants

    private static final int MULTIPLIER_GOLD = 16;
    private static final int MULTIPLIER_SILVER = 8;

    private static final int MASK_GOLD = ~0xFFFF;
    private static final int MASK_SILVER = 0xFF00;
    private static final int MASK_COPPER = 0xFF;

    // endregion

    // region Variables

    // Interní reprezentace peněz
    private final IntegerProperty raw = new SimpleIntegerProperty(0);
    public final ObjectProperty<Integer> gold = new SimpleObjectProperty<>(0);
    public final ObjectProperty<Integer> silver = new SimpleObjectProperty<>(0);
    public final ObjectProperty<Integer> copper = new SimpleObjectProperty<>(0);

    public final StringProperty text = new SimpleStringProperty(toString());

    // endregion

    // region Constructors

    /**
     * Vytvoří novou instanci peněz s nulovým obnosem
     */
    public Money() {
        this(0);
    }

    /**
     * Vytvoří novou instanci peněz
     *
     * @param rawValue Inicializační hodnota
     */
    public Money(int rawValue) {
        init();
        this.raw.setValue(rawValue);
    }

    /**
     * Vytvoří novou instanci peněz
     *
     * @param gold Počet zlaťáků
     * @param silver Počet stříbrňáků
     * @param copper Počet měďáků
     */
    public Money(int gold, int silver, int copper) {
        init();
        addGold(gold);
        addSilver(silver);
        addCopper(copper);

    }

    // endregion

    // region Private methods

    /**
     * Nastaví obousměrný binding hodnot
     */
    private void init() {
        raw.addListener((observable, oldValue, newValue) -> {
            int value = newValue.intValue();
            gold.setValue((value & ~0xFFFF) >> MULTIPLIER_GOLD);
            silver.setValue((value & 0xFF00) >> MULTIPLIER_SILVER);
            copper.setValue(value & ((1 << 8) - 1));
            text.setValue(toString());
        });
    }

    // endregion

    // region Public methods

    /**
     * Přičte peníze z jiné instance
     *
     * @param other Druhá instance peněz, která se přičte k aktuální hodnotě
     */
    public void add(Money other) {
        this.raw.add(other.raw.intValue());
    }

    /**
     * Nastaví počet zlaťáků
     *
     * @param gold Počet zlaťáků
     */
    public void setGold(int gold) {
        int rawValue = raw.intValue();
        rawValue = (rawValue & (~MASK_GOLD)) | (gold << MULTIPLIER_GOLD);
        raw.setValue(rawValue);
    }

    /**
     * Přidá zlaťáky
     *
     * @param gold Počet zlaťáků
     */
    public void addGold(int gold) {
        if (gold <= 0) {
            return;
        }

        raw.add(gold << MULTIPLIER_GOLD);
    }

    /**
     * Nastaí počet stříbrňáků
     *
     * @param silver Počet stříbrňáků
     */
    public void setSilver(int silver) {
        if ((silver / 100) != 0) {
            setGold(silver / 100);
        }
        int rawValue = raw.intValue();
        rawValue = (rawValue & (~MASK_SILVER)) | ((silver % 100) << MULTIPLIER_SILVER);
        raw.setValue(rawValue);
    }

    /**
     * Přidá stříbrňáky
     *
     * @param silver Počet stříbrňáků
     */
    public void addSilver(int silver) {
        if (silver <= 0) {
            return;
        }

        addGold(silver / 100);
        raw.add((silver % 100) << MULTIPLIER_SILVER);
    }

    /**
     * Nastaví počet měďáků
     *
     * @param copper Počet měďáků
     */
    public void setCopper(int copper) {
        if ((copper / 100) != 0) {
            setSilver(copper / 100);
        }
        int rawValue = raw.intValue();
        rawValue = (rawValue & (~MASK_COPPER)) | (copper % 100);
        raw.setValue(rawValue);
    }

    /**
     * Přidá měďáky
     *
     * @param copper Počet měďáků
     */
    public void addCopper(int copper) {
        if (copper <= 0) {
            return;
        }

        addSilver(copper / 100);
        raw.add(copper % 100);
    }

    /**
     * Odečte peníze z jiné instance
     *
     * @param other Druhá instance peněz, která se bude odčítat
     */
    public void subtract(Money other) {
        this.raw.subtract(other.raw.intValue());
    }

    /**
     * Odebere zlaťáky
     *
     * @param gold Počet zlaťáků
     */
    public void subtractGold(int gold) {
        if (gold <= 0) {
            return;
        }

        raw.subtract(gold);
    }

    /**
     * Odebere stříbrňáky
     *
     * @param silver Počet stříbrňáků
     */
    public void subtractSilver(int silver) {
        if (silver <= 0) {
            return;
        }

        subtractGold(silver / 100);
        raw.subtract(silver % 100);
    }

    /**
     * Odebere měďáky
     *
     * @param copper Počet měďáků
     */
    public void subtractCopper(int copper) {
        if (copper <= 0) {
            return;
        }

        subtractSilver(copper % 100);
        raw.subtract(copper);
    }

    // endregion

    // region Getters & Setters

    public void setRaw(int value) {
        this.raw.setValue(value);
    }

    public int getRaw() {
        return this.raw.intValue();
    }

    // endregion

    @Override
    public String toString() {
        return String
            .format("%dzl %dst %dmd", gold.getValue().intValue(), silver.getValue().intValue(),
                copper.getValue().intValue());
    }
}
