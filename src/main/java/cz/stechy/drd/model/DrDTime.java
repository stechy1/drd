package cz.stechy.drd.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Třída reprezentující čas ve světě Dračího doupěte
 *
 * Máme jednotky:
 * <ul>
 *     <li>Rok</li> year
 *     <li>Měsíc</li> month
 *     <li>Den</li> day
 *     <li>Směna</li> inning
 *     <li>Kolo</li> cycle
 * </ul>
 *
 * Směna = 10 kol = 4bity =  0b000000000000001111
 * Den = 24 směn = 5bitů =   0b000000000111110000
 * Měsíc = 30 dní = 5bitů =  0b000011111000000000
 * Rok = 12 měsíců = 4bity = 0b111100000000000000
 *
 */
public final class DrDTime implements Comparable<Money> {

    // region Constants

    private static final int MULTIPLIER_CYCLE = 0;
    private static final int MULTIPLIER_INNING = 4;
    private static final int MULTIPLIER_DAY = 9;
    private static final int MULTIPLIER_MONTH = 14;
    private static final int MULTIPLIER_YEAR = 18;

    private static final int MASK_CYCLE =  0b000000000000001111;
    private static final int MASK_INNING = 0b000000000111110000;
    private static final int MASK_DAY =    0b000011111000000000;
    private static final int MASK_MONTH =  0b111100000000000000;
    private static final int MASK_YEAR =  ~0b111111111111111111;

    private static final int CYCLE_IN_INNING = 10;
    private static final int MONTH_IN_YEAR = 12;
    private static final int INNING_IN_DAY = 24;
    private static final int DAY_IN_MONTH = 30;
    private static final int YEAR_IN_WORLD = Integer.MAX_VALUE;

    public static final int DEFAULT_VALUE = 0;

    // endregion

    // region Variables

    private final Object lock = new Object();

    private final IntegerProperty raw = new SimpleIntegerProperty();
    public final ObjectProperty<Number> year = new SimpleObjectProperty<>();
    public final ObjectProperty<Number> month = new SimpleObjectProperty<>();
    public final ObjectProperty<Number> day = new SimpleObjectProperty<>();
    public final ObjectProperty<Number> inning = new SimpleObjectProperty<>();
    public final ObjectProperty<Number> cycle = new SimpleObjectProperty<>();

    private boolean changing = false;
    // endregion

    // region Constructors

    /**
     * Vytvoří novou dobu trvání s výchozí hodnotou
     */
    public DrDTime() {
        this(DEFAULT_VALUE);
    }

    /**
     * Kopy konstruktor
     *
     * @param time {@link DrDTime}
     */
    public DrDTime(DrDTime time) {
        this(time.getRaw());
    }

    /**
     * Vytvoří novou dobu trvání s délkou zadanou v parametru
     *
     * @param time Doba trvání
     */
    public DrDTime(int time) {
        this.raw.setValue(time);
        init();
    }

    /**
     * Vytvoří novou dobu trvání s nastavením všech parametrů
     *
     * @param year Počet let
     * @param month Počet měsíců
     * @param day Počet dnů
     * @param inning Počet směn
     * @param cycle Počet kol
     */
    public DrDTime(int year, int month, int day, int inning, int cycle) {
        setCycle(cycle);
        setInning(inning);
        setDay(day);
        setMonth(month);
        setYear(year);
        init();
    }

    // endregion

    // region Private methods

    /**
     * Inicializace listeneru
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
            year.setValue((value & MASK_YEAR) >> MULTIPLIER_YEAR);
            month.setValue((value & MASK_MONTH) >> MULTIPLIER_MONTH);
            day.setValue((value & MASK_DAY) >> MULTIPLIER_DAY);
            inning.setValue((value & MASK_INNING) >> MULTIPLIER_INNING);
            cycle.setValue((value & MASK_CYCLE) >> MULTIPLIER_CYCLE);
            changing = false;
        });

        year.addListener((observable, oldValue, newValue) ->
            changeInternal(oldValue, newValue, this::subtractYear, this::addYear));
        month.addListener((observable, oldValue, newValue) ->
            changeInternal(oldValue, newValue, this::subtractMonth, this::addMonth));
        day.addListener((observable, oldValue, newValue) ->
            changeInternal(oldValue, newValue, this::subtractDay, this::addDay));
        inning.addListener((observable, oldValue, newValue) ->
            changeInternal(oldValue, newValue, this::subtractInning, this::addInning));
        cycle.addListener((observable, oldValue, newValue) ->
            changeInternal(oldValue, newValue, this::subtractCycle, this::addCycle));
    }

    /**
     * Pomocná interní metoda pro nastavení hodnoty
     * Metoda je volaná z listeneru
     *
     * @param oldValue Stará hodnota
     * @param newValue Nová hodnota
     * @param subtracter "Ukazatel" na metodu pro odečtení staré hodnoty
     * @param adder "Ukazatel" na metodu pro přičtení nové hodnoty
     */
    private void changeInternal(Number oldValue, Number newValue, Callback subtracter, Callback adder) {
        synchronized (lock) {
            if (changing) {
                return;
            }
        }

        changing = true;
        subtracter.callParent(oldValue.intValue());
        adder.callParent(newValue.intValue());
        changing = false;
    }

    /**
     * Pomocná interní metoda pro nastavení hodnoty
     *
     * @param value Nová hodnota
     * @param modifier Modifikátor, kterým se testuje, zda-li se bude volat callback
     * @param multiplier Multiplikátor, který posune bitově hodnotu na správnou pozici
     * @param callback Callback, který upraví hodnotu vyššího řádu
     * @return {@link DrDTime}
     */
    private DrDTime setInternal(int value, int modifier, int multiplier, Callback callback) {
        if (value < 0) {
            return this;
        }

        if ((value / modifier) != 0) {
            callback.callParent(value / modifier);
        }

        final int rawValue = getRaw();
        setRaw(rawValue | ((value % modifier) << multiplier));

        return this;
    }

    /**
     * Pomocná interní metoda pro přičtení hodnoty
     *
     * @param value Hodnota, která se má přičíst
     * @param modifier Modifikátor, kterým se upraví hodnota
     * @param multiplier Multiplikátor, který posune bitově hodnotu na správnou pozici
     * @param callback Callback, který přičte hodnotu vyššího řádu
     * @param getter Getter pro ziskání aktuální hodnoty
     * @return {@link DrDTime}
     */
    private DrDTime addInternal(int value, int modifier, int multiplier, Callback callback, Getter getter) {
        if (value < 0) {
            return this;
        }

        value = getter.get() + value;
        callback.callParent(value / modifier);

        final int rawValue = getRaw();
        raw.setValue(rawValue + (((value % modifier) - getter.get()) << multiplier));

        return this;
    }

    /**
     * Pomocná interní metoda pro odečtení hodnoty
     *
     * @param value Hodnota, která se má přičíst
     * @param modifier Modifikátor, kterým se upraví hodnota
     * @param multiplier Multiplikátor, který posune bitově hodnotu na správnou pozici
     * @param callback Callback, který odečte hodnotu vyššího řádu
     * @param getter Getter pro ziskání aktuální hodnoty
     * @return {@link DrDTime}
     */
    private DrDTime subtractInternal(int value, int modifier, int multiplier, Callback callback, Getter getter) {
        if (value < 0) {
            return this;
        }

        callback.callParent(value / modifier);
        value = getter.get() - (value % modifier);
        if (value < 0) {
            value = modifier + value;
            callback.callParent(1);
        }

        final int rawValue = getRaw();
        setRaw(rawValue - (getter.get() - (value % modifier) << multiplier));

        return this;
    }

    // endregion

    // region Public methods

    public DrDTime add(DrDTime other) {
        if (other == null) {
            return this;
        }

        addYear(other.getYear());
        addMonth(other.getMonth());
        addDay(other.getDay());
        addInning(other.getInning());
        addCycle(other.getCycle());

        return this;
    }

    public DrDTime subtract(DrDTime other) {
        if (other == null) {
            return this;
        }

        subtractCycle(other.getCycle());
        subtractInning(other.getInning());
        subtractDay(other.getDay());
        subtractMonth(other.getMonth());
        subtractYear(other.getYear());

        return this;
    }

    public DrDTime multiply(int constant) {
        setYear(getYear() * constant);
        setMonth(getMonth() * constant);
        setDay(getDay() * constant);
        setInning(getInning() * constant);
        setCycle(getCycle() * constant);

        return this;
    }

    public DrDTime addYear(int year) {
        return addInternal(year, YEAR_IN_WORLD, MULTIPLIER_YEAR, value -> {}, this::getYear);
    }
    public DrDTime subtractYear(int year) {
        return subtractInternal(year, YEAR_IN_WORLD, MULTIPLIER_YEAR, value -> {}, this::getYear);
    }

    public DrDTime addMonth(int month) {
        return addInternal(month, MONTH_IN_YEAR, MULTIPLIER_MONTH, this::addYear, this::getMonth);
    }
    public DrDTime subtractMonth(int month) {
        return subtractInternal(month, MONTH_IN_YEAR, MULTIPLIER_MONTH, this::subtractYear, this::getMonth);
    }

    public DrDTime addDay(int day) {
        return addInternal(day, DAY_IN_MONTH, MULTIPLIER_DAY, this::addMonth, this::getDay);
    }
    public DrDTime subtractDay(int day) {
        return subtractInternal(day, DAY_IN_MONTH, MULTIPLIER_DAY, this::subtractMonth, this::getDay);
    }

    public DrDTime addInning(int inning) {
        return addInternal(inning, INNING_IN_DAY, MULTIPLIER_INNING, this::addDay, this::getInning);
    }
    public DrDTime subtractInning(int inning) {
        return subtractInternal(inning, INNING_IN_DAY, MULTIPLIER_INNING, this::subtractDay, this::getInning);
    }

    public DrDTime addCycle(int cycle) {
        return addInternal(cycle, CYCLE_IN_INNING, MULTIPLIER_CYCLE, this::addInning, this::getCycle);
    }
    public DrDTime subtractCycle(int cycle) {
        return subtractInternal(cycle, CYCLE_IN_INNING, MULTIPLIER_CYCLE, this::subtractInning, this::getCycle);
    }

    // endregion

    // region Getters & Setters

    public int getRaw() {
        return raw.get();
    }

    public void setRaw(int raw) {
        this.raw.set(raw);
    }

    public int getCycle() {
        return getRaw() & MASK_CYCLE;
    }

    public DrDTime setCycle(int cycle) {
        return setInternal(cycle, CYCLE_IN_INNING, MULTIPLIER_CYCLE, this::setInning);
    }

    public int getInning() {
        return (getRaw() & MASK_INNING) >> MULTIPLIER_INNING;
    }

    public DrDTime setInning(int inning) {
        return setInternal(inning, INNING_IN_DAY, MULTIPLIER_INNING, this::setDay);
    }

    public int getDay() {
        return (getRaw() & MASK_DAY) >> MULTIPLIER_DAY;
    }

    public DrDTime setDay(int day) {
        return setInternal(day, DAY_IN_MONTH, MULTIPLIER_DAY, this::setMonth);
    }

    public int getMonth() {
        return (getRaw() & MASK_MONTH) >> MULTIPLIER_MONTH;
    }

    public DrDTime setMonth(int month) {
        return setInternal(month, MONTH_IN_YEAR, MULTIPLIER_MONTH, this::setYear);
    }

    public int getYear() {
        return (getRaw() & MASK_YEAR) >> MULTIPLIER_YEAR;
    }

    public DrDTime setYear(int year) {
        if (year < 0) {
            return this;
        }

        final int rawValue = getRaw();
        setRaw(rawValue | (year << MULTIPLIER_YEAR));

        return this;
    }

    // endregion

    @Override
    public int compareTo(Money o) {
        return Integer.compare(this.getRaw(), o.getRaw());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DrDTime drDTime = (DrDTime) o;

        return this.getRaw() == drDTime.getRaw();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getRaw());
    }

    @Override
    public String toString() {
        return String
            .format("%drok %dměsíc %dden %dsměna %dkolo",
                getYear(), getMonth(), getDay(), getInning(), getCycle());
    }

    @FunctionalInterface
    private interface Callback {
        void callParent(int value);
    }

    @FunctionalInterface
    private interface Getter {
        int get();
    }
}
