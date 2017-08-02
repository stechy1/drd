package cz.stechy.drd.model;

import cz.stechy.drd.model.entity.EntityProperty;
import java.util.Arrays;

/**
 * Třída reprezentující past ve smyslu hodu na úspěch proti nějaké vlastnosti
 */
public final class Trap {

    // region Constants

    // Kostka, se kterou se bude házet proti pasti
    private static final Dice dice = Dice.K10;

    // endregion

    // region Variables

    private int propertyRepair = 0;
    private int danger;
    private Range strengthIfSuccess;
    private Range strengthIfNotSuccess;

    private int result;
    // endregion

    // region Constructors

    /**
     * Vytvoří novou past připravenou ke konfiguraci
     */
    public Trap() {}

    /**
     * Vytvoří novou past
     *
     * @param property {@link EntityProperty} Vlastnost, která se bude testovat
     * @param danger Nebezpečnost pasti
     */
    public Trap(EntityProperty property, int danger) {
        this(property, danger, null, null);
    }

    /**
     * Vytvoří novou past
     *
     * @param property {@link EntityProperty} Vlastnost, která se bude testovat
     * @param danger Nebezpečnost pasti
     * @param strengthIfSuccess Rozsah síly pasti pokud je hod proti pasti úspěšný
     * @param strengthIfNotSuccess Rozsah síly pasti pokud je hod proti pasti neúspěšný
     */
    public Trap(EntityProperty property, int danger, Range strengthIfSuccess, Range strengthIfNotSuccess) {
        this.propertyRepair = property.getRepair();
        this.danger = danger;
        this.strengthIfSuccess = strengthIfSuccess;
        this.strengthIfNotSuccess = strengthIfNotSuccess;
    }

    // endregion

    // region Public methods

    /**
     * Nastaví požadované vlastnosti
     *
     * @param properties {@link EntityProperty} Vlastnosti, které se budou testovat
     * @return {@link Trap}
     */
    public Trap property(EntityProperty ...properties) {
        this.propertyRepair += Arrays.stream(properties).mapToInt(value -> value.getRepair()).sum();
        return this;
    }

    /**
     * Přidá do výpočtu konstantu
     *
     * @param value Konstanta, která se má připočítat do výpočtu
     * @return {@link Trap}
     */
    public Trap property(int value) {
        this.propertyRepair += value;
        return this;
    }

    /**
     * Započítá do výpočtu pasti životaschopnost cíle
     *
     * @param live Životaschopnost cíle pasti
     * @return {@link Trap}
     */
    public Trap propertyFromLive(int live) {
        this.propertyRepair += (int) Math.round(live / 5.0);
        return this;
    }

    /**
     * Nastaví požadované vlastnosti se záporným znaménkem
     *
     * @param properties {@link EntityProperty} Vlastnosti, které se budou testovatt
     * @return
     */
    public Trap subtractProperty(EntityProperty ...properties) {
        this.propertyRepair -= Arrays.stream(properties).mapToInt(value -> value.getRepair()).sum();
        return this;
    }

    /**
     * Přidá do výpočtu konstantu se záporným znaménkem - odečte ji od interní hodnoty
     *
     * @param value Konstante, která se má připočítat do výpočtu
     * @return {@link Trap}
     */
    public Trap subtractProperty(int value) {
        this.propertyRepair -= value;
        return this;
    }

    /**
     * Nastaví nebezpečnost pasti
     *
     * @param strengthIfSuccess Rozsah síly pasti pokud je hod proti pasti úspěšný
     * @param strengthIfNotSuccess Rozsah síly pasti pokud je hod proti pasti neúspěšný
     * @return {@link Trap}
     */
    public Trap strength(Range strengthIfSuccess, Range strengthIfNotSuccess) {
        this.strengthIfSuccess = strengthIfSuccess;
        this.strengthIfNotSuccess = strengthIfNotSuccess;
        return this;
    }

    /**
     * Nebezpečnost pasti = kolik musí postava hodit, aby byla úspěšná
     *
     * @param danger Nebezpečnost pasti
     * @return {@link Trap}
     */
    public Trap danger(int danger) {
        this.danger = danger;
        return this;
    }

    /**
     * Provede se samotný hod proti pasti
     *
     * @return {@link Trap}
     */
    public Trap roll() {
        result = dice.roll() + propertyRepair;

        return this;
    }

    /**
     * Zjstí, zda-li je hod proti pasti úspěšný, či nikoliv
     *
     * @return True, pokud je hod proti pasti úspěšní, jinak False
     */
    public boolean isSuccess() {
        return result >= danger;
    }

    /**
     * Vrátí sílu pasti v závislosti na úspěchu při hodu proti pasti
     *
     * @return {@link Range} Rozsah síly pasti v závislosti na úspěchu při hodu proti pasit
     */
    public Range getStrength() {
        return isSuccess() ? strengthIfSuccess : strengthIfNotSuccess;
    }

    // endregion

}
