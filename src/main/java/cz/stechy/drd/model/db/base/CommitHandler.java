package cz.stechy.drd.model.db.base;

/**
 * Rozhraní pro zachycení potvrzení transakce
 */
public interface CommitHandler {
    void onCommit();
}
