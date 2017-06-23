package cz.stechy.drd.model.db.base;

import cz.stechy.drd.model.db.BaseDatabaseService.UpdateListener;
import javafx.collections.ObservableList;

/**
 * Třída představující jednu operaci transakce
 */
public abstract class TransactionOperation<T extends DatabaseItem> {

    final T before;
    final T after;

    TransactionOperation(T before, T after) {
        this.before = before;
        this.after = after;
    }

    public abstract void commit(final ObservableList<T> items);

    public static final class InsertOperation<T extends DatabaseItem> extends TransactionOperation<T> {

        public InsertOperation(T model) {
            super(null, model);
        }

        @Override
        public void commit(ObservableList<T> items) {
            items.add(after);
        }
    }

    public static final class UpdateOperation<T extends DatabaseItem> extends TransactionOperation<T> {

        private final UpdateListener<T> updater;

        public UpdateOperation(T before, T after, UpdateListener<T> updater) {
            super(before, after);

            this.updater = updater;
        }

        @Override
        public void commit(ObservableList<T> items) {
            before.update(after);
            if (this.updater != null) {
                this.updater.onUpdate(before);
            }
        }
    }

    public static final class DeleteOperation<T extends DatabaseItem> extends TransactionOperation<T> {

        public DeleteOperation(T model) {
            super(model, null);
        }

        @Override
        public void commit(ObservableList<T> items) {
            items.remove(before);
        }
    }

}
