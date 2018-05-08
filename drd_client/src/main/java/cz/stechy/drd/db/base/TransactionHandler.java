package cz.stechy.drd.db.base;

/**
 * Rozhraní pro zachycení potvrzení transakce
 */
public interface TransactionHandler {

    /**
     * Akce, která se provede po potvrzení transakce.
     */
    void onCommit();

    /**
     * Akce, která se provede při zrušení transakce
     */
    void onRollback();
}
