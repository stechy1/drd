package cz.stechy.drd.model.inventory;

/**
 * Třída představující vyjímku, která může být vyvolána při práci s inventářem
 */
public class InventoryException extends Exception {

    private static final long serialVersionUID = -3718290047346851959L;

    public InventoryException() {
    }

    public InventoryException(String message) {
        super(message);
    }

    public InventoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public InventoryException(Throwable cause) {
        super(cause);
    }
}
