package cz.stechy.drd.db;

import java.io.IOException;

/**
 * Třída představující vyjímku při nenalezení záznamu v databázi
 */
public class DatabaseException extends IOException {

    /**
     * {@inheritDoc}
     */
    public DatabaseException() {
    }

    /**
     * {@inheritDoc}
     */
    public DatabaseException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public DatabaseException(Throwable cause) {
        super(cause);
    }
}
