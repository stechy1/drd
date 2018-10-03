package cz.stechy.drd.db.base;

import cz.stechy.drd.model.DiffEntry;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Třída představující jednu operaci transakce
 */
public abstract class TransactionOperation<T extends Row> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionOperation.class);

    // endregion

    // region Variables

    final T before;
    final T after;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou transakční operaci
     *
     * @param before {@link T} Stav předmětu před operací
     * @param after Stav předmětu po operaci
     */
    TransactionOperation(T before, T after) {
        this.before = before;
        this.after = after;
    }

    // endregion

    // region Public methods

    /**
     * Provede operaci na kolekci předmětů
     *
     * @param items {@link ObservableList<T>}
     */
    public abstract void commit(final ObservableList<T> items);

    // endregion

    public static final class InsertOperation<T extends Row> extends TransactionOperation<T> {

        public InsertOperation(T model) {
            super(null, model);
        }

        @Override
        public void commit(ObservableList<T> items) {
            LOGGER.trace("Commit INSERT of {}.", after.toString());
            items.add(after);
        }
    }

    public static final class UpdateOperation<T extends Row> extends TransactionOperation<T> {

        public UpdateOperation(T before, T after) {
            super(before, after);
        }

        @Override
        public void commit(ObservableList<T> items) {
            final DiffEntry<T> diffEntry = new DiffEntry<>(before, after);
            LOGGER.trace("Commit UPDATE of: {} diff: {}.", after.toString(), diffEntry.getDiffMap());
            before.update(after);
        }
    }

    public static final class DeleteOperation<T extends Row> extends TransactionOperation<T> {

        public DeleteOperation(T model) {
            super(model, null);
        }

        @Override
        public void commit(ObservableList<T> items) {
            LOGGER.trace("Commit DELETE of {}.", before.toString());
            items.remove(before);
        }
    }

}
