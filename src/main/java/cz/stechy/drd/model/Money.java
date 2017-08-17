package cz.stechy.drd.model;

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

    public static final int MAX_GOLD = Integer.MAX_VALUE;
    public static final int MAX_SILVER = 99;
    public static final int MAX_COPPER = 99;

    // endregion

    // region Variables

    private final Object lock = new Object();

    // Interní reprezentace peněz
    private final IntegerProperty raw = new SimpleIntegerProperty(0);
    public final ObjectProperty<Number> gold = new SimpleObjectProperty<>(0);
    public final ObjectProperty<Number> silver = new SimpleObjectProperty<>(0);
    public final ObjectProperty<Number> copper = new SimpleObjectProperty<>(0);

    public final StringProperty text = new SimpleStringProperty(toString());

    private boolean changing = false;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou instanci peněz s nulovým obnosem
     */
    public Money() {
        this(0);
    }

    /**
     * Kopy konstruktor
     *
     * @param money {@link Money}
     */
    public Money(Money money) {
        this(money.raw.intValue());
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
            synchronized (lock) {
                if (changing) {
                    return;
                }
            }
            changing = true;
            int value = newValue.intValue();
            gold.setValue((value & MASK_GOLD) >> MULTIPLIER_GOLD);
            silver.setValue((value & MASK_SILVER) >> MULTIPLIER_SILVER);
            copper.setValue(value & MASK_COPPER);
            text.setValue(toString());
            changing = false;
        });
        gold.addListener((observable, oldValue, newValue) -> {
            synchronized (lock) {
                if (changing) {
                    return;
                }
            }
            changing = true;
            subtractGold(oldValue.intValue());
            addGold(newValue.intValue());
            changing = false;
        });
        silver.addListener((observable, oldValue, newValue) -> {
            synchronized (lock) {
                if (changing) {
                    return;
                }
            }
            changing = true;
            subtractSilver(oldValue.intValue());
            addSilver(newValue.intValue());
            changing = false;
        });
        copper.addListener((observable, oldValue, newValue) -> {
            synchronized (lock) {
                if (changing) {
                    return;
                }
            }
            changing = true;
            subtractCopper(oldValue.intValue());
            addCopper(newValue.intValue());
            changing = false;
        });
    }

    // endregion

    // region Public methods

    /**
     * Přičte peníze z jiné instance
     *
     * @param other Druhá instance peněz, která se přičte k aktuální hodnotě
     * @return {@link Money}
     */
    public Money add(Money other) {
        this.addGold(other.getGold());
        this.addSilver(other.getSilver());
        this.addCopper(other.getCopper());

        return this;
    }

    /**
     * Odečte peníze z jiné instance
     *
     * @param other Druhá instance peněz, která se bude odčítat
     * @return {@link Money}
     */
    public Money subtract(Money other) {
        this.subtractCopper(other.getCopper());
        this.subtractSilver(other.getSilver());
        this.subtractGold(other.getGold());

        return this;
    }

    /**
     * Vynásobí každou část peněz konstantou
     *
     * @param constant Konstanta, kterou se bude násobit
     * @return {@link Money}
     */
    public Money multiply(int constant) {
        setGold(getGold() * constant);
        setSilver(getSilver() * constant);
        setCopper(getCopper() * constant);

        return this;
    }

    /**
     * Nastaví počet zlaťáků
     *
     * @param gold Počet zlaťáků
     * @return {@link Money}
     */
    public Money setGold(int gold) {
        if (gold < 0) {
            return this;
        }

        final int rawValue = getRaw();
        raw.setValue(rawValue | (gold << MULTIPLIER_GOLD));

        return this;
    }

    /**
     * Přidá zlaťáky
     *
     * @param gold Počet zlaťáků
     * @return {@link Money}
     */
    public Money addGold(int gold) {
        if (gold < 0) {
            return this;
        }

        final int rawValue = getRaw();
        raw.setValue(rawValue + (gold << MULTIPLIER_GOLD));

        return this;
    }

    /**
     * Odebere zlaťáky
     *
     * @param gold Počet zlaťáků
     * @return {@link Money}
     */
    public Money subtractGold(int gold) {
        if (gold < 0) {
            return this;
        }

        int rawValue = getRaw();
        raw.setValue(rawValue - (gold << MULTIPLIER_GOLD));

        return this;
    }

    /**
     * Nastaví počet stříbrňáků
     *
     * @param silver Počet stříbrňáků
     * @return {@link Money}
     */
    public Money setSilver(int silver) {
        if ((silver / 100) != 0) {
            setGold(silver / 100);
        }

        final int rawValue = getRaw();
        raw.setValue(rawValue | ((silver % 100) << MULTIPLIER_SILVER));

        return this;
    }

    /**
     * Přidá stříbrňáky
     *
     * @param silver Počet stříbrňáků
     * @return {@link Money}
     */
    public Money addSilver(int silver) {
        if (silver <= 0) {
            return this;
        }

        silver = getSilver() + silver;
        addGold(silver / 100);
        final int rawValue = getRaw();
        raw.setValue(rawValue + (((silver % 100) - getSilver()) << MULTIPLIER_SILVER));

        return this;
    }

    /**
     * Odebere stříbrňáky
     *
     * @param silver Počet stříbrňáků
     * @return {@link Money}
     */
    public Money subtractSilver(int silver) {
        if (silver <= 0) {
            return this;
        }

        subtractGold(silver / 100);
        silver = getSilver() - (silver % 100);
        if (silver < 0) {
            silver = 100 + silver;
            subtractGold(1);
        }

        final int rawValue = getRaw();
        raw.setValue(rawValue - (getSilver() - (silver % 100) << MULTIPLIER_SILVER));

        return this;
    }

    /**
     * Nastaví počet měďáků
     *
     * @param copper Počet měďáků
     * @return {@link Money}
     */
    public Money setCopper(int copper) {
        if ((copper / 100) != 0) {
            setSilver(copper / 100);
        }

        final int rawValue = getRaw();
        raw.setValue(rawValue | (copper % 100));

        return this;
    }

    /**
     * Přidá měďáky
     *
     * @param copper Počet měďáků
     * @return {@link Money}
     */
    public Money addCopper(int copper) {
        if (copper <= 0) {
            return this;
        }

        copper = getCopper() + copper;
        addSilver(copper / 100);
        final int rawValue = getRaw();
        raw.setValue(rawValue + (copper % 100) - getCopper());

        return this;
    }

    /**
     * Odebere měďáky
     *
     * @param copper Počet měďáků
     * @return {@link Money}
     */
    public Money subtractCopper(int copper) {
        if (copper <= 0) {
            return this;
        }

        subtractSilver(copper / 100);
        copper = getCopper() - (copper % 100);
        if (copper < 0) {
            copper = 100 + copper;
            subtractSilver(1);
        }

        final int rawValue = getRaw();
        raw.setValue(rawValue - (getCopper() - (copper % 100)));

        return this;
    }

    // endregion

    // region Getters & Setters

    /**
     * Nastaví peníze z interně uloženého stavu
     *
     * @param value Interní reprezentace peněz
     */
    public void setRaw(int value) {
        this.raw.setValue(value);
    }

    /**
     * Vrátí interní reprezentaci peněz
     *
     * @return Interní reprezentaci penět
     */
    public int getRaw() {
        return this.raw.intValue();
    }

    /**
     * Vrátí počet zlaťáků
     *
     * @return Počet zlaťáků
     */
    public int getGold() {
        return this.gold.getValue().intValue();
    }

    /**
     * Vrátí počet stříbrňáků
     *
     * @return Počet stříbrňáků
     */
    public int getSilver() {
        return this.silver.getValue().intValue();
    }

    /**
     * Vrátí počet měďáků
     *
     * @return Počet měďáků
     */
    public int getCopper() {
        return this.copper.getValue().intValue();
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

        Money money = (Money) o;

        return raw.get() == money.raw.get();
    }

    @Override
    public int hashCode() {
        return 31 * raw.get();
    }

    @Override
    public String toString() {
        return String
            .format("%dzl %dst %dmd", gold.getValue().intValue(), silver.getValue().intValue(),
                copper.getValue().intValue());
    }

}
