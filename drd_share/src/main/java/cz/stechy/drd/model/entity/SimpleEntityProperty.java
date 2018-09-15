package cz.stechy.drd.model.entity;

/**
 * Základní implementace vlastnosti entity
 */
public final class SimpleEntityProperty extends EntityProperty {

    // region Constants

    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE;
    private static final int[] REPAIRS = new int[]{
        -5, -4, -4, -3, -3, -2, -2, -1, -1, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7,
        8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13, 14, 14, 15, 15, 16, 16, 17, 17, 18, 18, 19,
        19, 20, 20, 21, 21, 22, 22, 23, 23, 24, 24, 25, 25
    };

    // endregion

    static {
        MAX_VALUE = REPAIRS.length;
    }

    // region Constructors

    /**
     * Vytvoří novou vlastnost
     */
    public SimpleEntityProperty() {
        super(MIN_VALUE, MAX_VALUE);

        init();
    }

    /**
     * Vytvoří novou vlastnost
     *
     * @param bean Objekt obsahující tuto vlastnost
     * @param name Název vlastnosti
     */
    public SimpleEntityProperty(Object bean, String name) {
        super(bean, name, MIN_VALUE, MAX_VALUE);

        init();
    }

    // endregion

    private void init() {
        value.addListener((observable, oldValue, newValue) -> {
            int index = limit(newValue).intValue();
            int repairValue = REPAIRS[index - 1];
            repair.setValue(repairValue);
        });
    }
}
