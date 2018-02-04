package cz.stechy.drd.model.db;

import cz.stechy.drd.PreloaderNotifier;
import cz.stechy.drd.R;
import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.di.Inject;
import cz.stechy.drd.model.db.base.Database;
import cz.stechy.drd.model.db.base.DatabaseItem;
import cz.stechy.drd.model.db.base.TransactionHandler;
import cz.stechy.drd.model.db.base.TransactionOperation;
import cz.stechy.drd.model.db.base.TransactionOperation.DeleteOperation;
import cz.stechy.drd.model.db.base.TransactionOperation.InsertOperation;
import cz.stechy.drd.model.db.base.TransactionOperation.UpdateOperation;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Základní implementace správce
 */
public abstract class BaseDatabaseService<T extends DatabaseItem> implements DatabaseService<T> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDatabaseService.class);

    /**
     * Vygeneruje řetěžec obsahující názvy sloupců oddělené čárkou
     *
     * col1,col2,col3
     *
     * @param columns Názvy sloupců
     * @return Názvy sloupců oddělené čárkou
     */
    protected static String GENERATE_COLUMN_KEYS(String[] columns) {
        return Arrays.stream(columns).collect(Collectors.joining(","));
    }

    /**
     * Vygeneruje řetězec obsahující otazníky zastupující názvy sloupců oddělené čárkou
     *
     * ?,?,?
     *
     * @param columns Názvy sloupců
     * @return Otazníky zastupující názvy sloupců oddělené čárkou
     */
    protected static String GENERATE_COLUMNS_VALUES(String[] columns) {
        return Arrays.stream(columns).map(s -> "?").collect(Collectors.joining(","));
    }

    /**
     * Vygeneruje řetězec obsahující názvy sloupců ke kterým bude přiřazena hodnota představující
     * otazník a budou oddělené čárkou
     *
     * col1=?,col2=?,col3=?
     *
     * @param columns Názvy sloupců
     * @return Názvy sloupců s otazníky: col1=?,col2=?,col3=?
     */
    protected static String GENERATE_COLUMNS_UPDATE(String[] columns) {
        return Arrays.stream(columns).map(s -> s + "=?").collect(Collectors.joining(","));
    }

    // endregion

    // region Variables

    private final List<TransactionOperation<T>> operations = new ArrayList<>();
    private boolean selectAllCalled = false;
    protected final ObservableList<T> items = FXCollections.observableArrayList();
    // Databáze
    protected final Database db;
    @Inject
    protected PreloaderNotifier notifier;
    // Operace transakce

    // endregion

    // region Constructors

    /**
     * Vytvoří nového správce
     *
     * @param db {@link Database}
     */
    protected BaseDatabaseService(Database db) {
        this.db = db;
        db.addCommitHandler(transactionHandler);
    }

    // endregion

    // region Public static methods

    public static Predicate<? super DatabaseItem> ID_FILTER(String id) {
        return t -> t.getId().equals(id);
    }

    /**
     * Přešte blob dat ze zadaného sloupečku
     *
     * @param resultSet {@link ResultSet}
     * @param column Sloupeček, který obsahuje blob
     * @return Pole bytu
     */
    protected static byte[] readBlob(ResultSet resultSet, String column) {
        final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            final InputStream binaryStream = resultSet.getBinaryStream(column);
            final byte[] buffer = new byte[1024];
            while (binaryStream.read(buffer) > 0) {
                arrayOutputStream.write(buffer);
            }
        } catch (SQLException | IOException sqlException) {
            sqlException.printStackTrace();
        }

        return arrayOutputStream.toByteArray();
    }
    // endregion

    // region Private methods

    /**
     * Zkonvertuje {@link ResultSet} na instanci třídy {@link T}
     *
     * @param resultSet Result set z databáze
     * @return Instanci třídy {@link T}
     * @throws SQLException Pokud se nepodaří {@link ResultSet} správně naparsovat
     */
    protected abstract T parseResultSet(ResultSet resultSet) throws SQLException;

    /**
     * Zkonvertuje instanci třídy {@link T} na objekt pro parametry
     *
     * @param item {@link T}
     * @return Objekt obsahující parametry
     */
    protected abstract List<Object> itemToParams(T item);

    /**
     * @return Vrátí název tabulky
     */
    protected abstract String getTable();

    /**
     * @return Vrátí název sloupečku, který představuje primární klíč tabulky
     */
    protected abstract String getColumnWithId();

    /**
     * @return Vrátí názvy sloupců oddělené čárkou
     */
    protected abstract String getColumnsKeys();

    /**
     * @return Vrátí sloupce zastoupené otazníkem
     */
    protected abstract String getColumnValues();

    /**
     * @return Vrátí sloupce připravené k dotazu pro aktualizaci údajů
     */
    protected abstract String getColumnsUpdate();

    /**
     * @return Vrátí SQL příkaz pro inicializaci tabulky
     */
    protected abstract String getInitializationQuery();

    /**
     * @return Vrátí dotaz pro výběr všech položek
     */
    protected String getQuerySelectAll() {
        return String.format("SELECT * FROM %s", getTable());
    }

    /**
     * @return Vrátí parametry pro dotaz k výběru všech položek
     */
    protected Object[] getParamsForSelectAll() {
        return new Object[0];
    }

    // endregion

    // region Public methods

    @Override
    public void createTable() throws DatabaseException {
        LOGGER.trace("Inicializuji tabulku {}", getTable());
        if (notifier != null) {
            notifier.increaseProgress(
                String.format("Inicializuji tabulku: %s", getTable()));
        }
        final String query = getInitializationQuery();
        try {
            db.query(query);

            final int dbVersion = db.getVersion();
            final int requiredVersion = R.DATABASE_VERSION;

            if (requiredVersion > dbVersion) {
                onUpgrade(requiredVersion);
            }

            db.select(resultSet -> {
                final int count = resultSet.getInt(1);
                if (notifier != null) {
                    notifier.increaseMaxProgress(count);
                }
            }, String.format("SELECT COUNT(%s) FROM %s", getColumnWithId(), getTable()));
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public T select(Predicate<? super T> filter) throws DatabaseException {
        Optional<T> item = items.stream()
            .filter(filter)
            .findFirst();
        if (item.isPresent()) {
            return item.get();
        }

        throw new DatabaseException("Item not found");
    }

    @Override
    public ObservableList<T> selectAll() {
        if (items.isEmpty() && !selectAllCalled) {
            try {
                db.select(resultSet -> {
                        final T item = parseResultSet(resultSet);
                        if (notifier != null) {
                            notifier.increaseProgress(
                                String.format("Načítám záznam: %s", item.toString()));
                        }
                        items.add(item);
                    },
                    getQuerySelectAll(), getParamsForSelectAll());
                selectAllCalled = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return items;
    }

    @Override
    public CompletableFuture<ObservableList<T>> selectAllAsync() {
        if (items.isEmpty() && !selectAllCalled) {
            return db
                .selectAsync(this::parseResultSet, getQuerySelectAll(), getParamsForSelectAll())
                .thenApplyAsync(resultItems -> {
                    items.setAll(resultItems);
                    selectAllCalled = true;
                    return items;
                }, ThreadPool.JAVAFX_EXECUTOR);
        }

        return CompletableFuture.completedFuture(items);
    }

    @Override
    public void insert(T item) throws DatabaseException {
        final String query = String
            .format("INSERT INTO %s (%s) VALUES (%s)", getTable(), getColumnsKeys(),
                getColumnValues());
        try {
            LOGGER.trace("Vkládám položku {} do databáze.", item.toString());
            db.query(query, itemToParams(item).toArray());
            final TransactionOperation<T> operation = new InsertOperation<>(item);
            if (db.isTransactional()) {
                operations.add(operation);
            } else {
                operation.commit(items);
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public CompletableFuture<T> insertAsync(T item) {
        final String query = String
            .format("INSERT INTO %s (%s) VALUES (%s)", getTable(), getColumnsKeys(),
                getColumnValues());
        LOGGER.trace("Vkládám položku {} do databáze.", item.toString());
        return db.queryAsync(query, itemToParams(item).toArray())
            .thenApply(value -> {
                final TransactionOperation<T> operation = new InsertOperation<>(item);
                if (db.isTransactional()) {
                    operations.add(operation);
                } else {
                    operation.commit(items);
                }

                return item;
            });
    }

    @Override
    public void update(T item) throws DatabaseException {
        final String query = String
            .format("UPDATE %s SET %s WHERE %s = ?", getTable(), getColumnsUpdate(),
                getColumnWithId());
        final List<Object> params = itemToParams(item);
        params.add(item.getId());
        try {
            LOGGER.trace("Aktualizuji položku {} v databázi", item.toString());
            db.query(query, params.toArray());
            final Optional<T> result = items.stream()
                .filter(t -> item.getId().equals(t.getId()))
                .findFirst();
            assert result.isPresent();
            final TransactionOperation<T> operation = new UpdateOperation<>(result.get(), item);
            if (db.isTransactional()) {
                operations.add(operation);
            } else {
                operation.commit(items);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public CompletableFuture<T> updateAsync(T item) {
        final String query = String
            .format("UPDATE %s SET %s WHERE %s = ?", getTable(), getColumnsUpdate(),
                getColumnWithId());
        final List<Object> params = itemToParams(item);
        params.add(item.getId());
        LOGGER.trace("Aktualizuji položku {} v databázi", item.toString());

        return db.queryAsync(query, params.toArray())
            .thenApply(value -> {
                final Optional<T> result = items.stream()
                    .filter(t -> item.getId().equals(t.getId()))
                    .findFirst();
                assert result.isPresent();
                final TransactionOperation<T> operation = new UpdateOperation<>(result.get(), item);
                if (db.isTransactional()) {
                    operations.add(operation);
                } else {
                    operation.commit(items);
                }

                return item;
            });
    }

    @Override
    public void delete(String id) throws DatabaseException {
        final String query = String
            .format("DELETE FROM %s WHERE %s = ?", getTable(), getColumnWithId());
        try {
            LOGGER.trace("Mažu položku {} z databáze.", id);
            db.query(query, id);
            final Optional<T> result = items.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst();
            assert result.isPresent();
            final TransactionOperation<T> operation = new DeleteOperation<>(result.get());
            if (db.isTransactional()) {
                operations.add(operation);
            } else {
                operation.commit(items);
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public CompletableFuture<T> deleteAsync(T item) {
        final String query = String
            .format("DELETE FROM %s WHERE %s = ?", getTable(), getColumnWithId());
        LOGGER.trace("Mažu položku {} z databáze.", item.getId());

        return db.queryAsync(query, item.getId())
            .thenApply(value -> {
                final TransactionOperation<T> operation = new DeleteOperation<>(item);
                if (db.isTransactional()) {
                    operations.add(operation);
                } else {
                    operation.commit(items);
                }

                return item;
            });
    }

    @Override
    public void beginTransaction() throws DatabaseException {
        try {
            db.beginTransaction();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void commit() throws DatabaseException {
        try {
            db.commit();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void rollback() throws DatabaseException {
        try {
            db.rollback();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void onUpgrade(int newVersion) throws DatabaseException {
        LOGGER.info("Aktualizuji tabulku: {} na verzi: {}", getTable(), newVersion);
    }

    // endregion

    private final TransactionHandler transactionHandler = new TransactionHandler() {
        @Override
        public void onCommit() {
            operations.forEach(operation -> operation.commit(items));
            operations.clear();
        }

        @Override
        public void onRollback() {
            operations.clear();
        }
    };

}
