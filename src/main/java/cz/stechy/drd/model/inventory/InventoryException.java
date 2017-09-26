package cz.stechy.drd.model.inventory;

/**
 * Třída představující vyjímku, která může být vyvolána při práci s inventářem
 */
public class InventoryException extends Exception {

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
