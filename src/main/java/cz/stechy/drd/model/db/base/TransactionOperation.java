package cz.stechy.drd.model.db.base;

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


        public UpdateOperation(T before, T after) {
            super(before, after);
        }

        @Override
        public void commit(ObservableList<T> items) {
            before.update(after);
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
