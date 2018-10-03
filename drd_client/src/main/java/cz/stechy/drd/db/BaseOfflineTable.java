package cz.stechy.drd.db;

import cz.stechy.drd.R;
import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.db.base.BaseTableDefinitions;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.db.base.OfflineTable;
import cz.stechy.drd.db.base.Row;
import cz.stechy.drd.db.base.TransactionHandler;
import cz.stechy.drd.db.base.TransactionOperation;
import cz.stechy.drd.db.base.TransactionOperation.DeleteOperation;
import cz.stechy.drd.db.base.TransactionOperation.InsertOperation;
import cz.stechy.drd.db.base.TransactionOperation.UpdateOperation;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Základní implementace správce
 */
public abstract class BaseOfflineTable<T extends Row> implements OfflineTable<T> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseOfflineTable.class);

//    /**
//     * Vygeneruje řetěžec obsahující názvy sloupců oddělené čárkou
//     *
//     * col1,col2,col3
//     *
//     * @param columns Názvy sloupců
//     * @return Názvy sloupců oddělené čárkou
//     */
//    protected static String GENERATE_COLUMN_KEYS(String[] columns) {
//        return String.join(",", columns);
//    }
//
//    /**
//     * Vygeneruje řetězec obsahující otazníky zastupující názvy sloupců oddělené čárkou
//     *
//     * ?,?,?
//     *
//     * @param columns Názvy sloupců
//     * @return Otazníky zastupující názvy sloupců oddělené čárkou
//     */
//    protected static String GENERATE_COLUMNS_VALUES(String[] columns) {
//        return Arrays.stream(columns).map(s -> "?").collect(Collectors.joining(","));
//    }
//
//    /**
//     * Vygeneruje řetězec obsahující názvy sloupců ke kterým bude přiřazena hodnota představující
//     * otazník a budou oddělené čárkou
//     *
//     * col1=?,col2=?,col3=?
//     *
//     * @param columns Názvy sloupců
//     * @return Názvy sloupců s otazníky: col1=?,col2=?,col3=?
//     */
//    protected static String GENERATE_COLUMNS_UPDATE(String[] columns) {
//        return Arrays.stream(columns).map(s -> s + "=?").collect(Collectors.joining(","));
//    }

    // endregion

    // region Variables

    // Operace transakce
    private final List<TransactionOperation<T>> operations = new ArrayList<>();
    // Databáze
    protected final Database db;
    protected final BaseTableDefinitions<T> tableDefinitions;
    final ObservableList<T> records = FXCollections.observableArrayList();
    private boolean selectAllCalled = false;

    // endregion

    // region Constructors

    /**
     * Vytvoří nového správce
     *
     * @param tableDefinitions {@link BaseTableDefinitions} Definice tabulky
     * @param db {@link Database}
     */
    protected BaseOfflineTable(BaseTableDefinitions<T> tableDefinitions, Database db) {
        this.tableDefinitions = tableDefinitions;
        this.db = db;
        TransactionHandler transactionHandler = new TransactionHandler() {
            @Override
            public void onCommit() {
                operations.forEach(operation -> operation.commit(records));
                operations.clear();
            }

            @Override
            public void onRollback() {
                operations.clear();
            }
        };
        db.addCommitHandler(transactionHandler);
    }

    // endregion

    // region Public static methods

    public static Predicate<? super Row> ID_FILTER(String id) {
        return t -> t.getId().equals(id);
    }

//    /**
//     * Přešte blob dat ze zadaného sloupečku
//     *
//     * @param resultSet {@link ResultSet}
//     * @param column Sloupeček, který obsahuje blob
//     * @return Pole bytu
//     */
//    protected static byte[] readBlob(ResultSet resultSet, String column) {
//        final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
//        try {
//            final InputStream binaryStream = resultSet.getBinaryStream(column);
//            final byte[] buffer = new byte[1024];
//            while (binaryStream.read(buffer) > 0) {
//                arrayOutputStream.write(buffer);
//            }
//        } catch (SQLException | IOException sqlException) {
//            sqlException.printStackTrace();
//        }
//
//        return arrayOutputStream.toByteArray();
//    }
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
    protected abstract List<Object> toParamList(T item);

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
    public CompletableFuture<Void> createTableAsync() {
        final String query = getInitializationQuery();
        return db.queryAsync(query)
            .thenCompose(ignore -> {
                final int dbVersion = db.getVersion();
                final int requiredVersion = R.DATABASE_VERSION;
                if (requiredVersion > dbVersion) {
                    return onUpgradeAsync(requiredVersion);
                } else {
                    return CompletableFuture.completedFuture(null);
                }
            })
            .thenAccept(ignore ->
                db.selectAsync((resultSet -> {
                    final int count = resultSet.getInt(1);
                    // TODO nějak zobrazit počet načtených hodnot
                    return null;
                }), String.format("SELECT COUNT(%s) FROM %s", getColumnWithId(), getTable())));
    }

    @Override
    public Optional<T> selectAsync(Predicate<? super T> filter) {
        LOGGER.trace("Provádím select dotaz v tabulce: {}.", getTable());
        return records.stream().filter(filter).findFirst();
    }

    @Override
    public CompletableFuture<ObservableList<T>> selectAllAsync() {
        return selectAllAsync(getParamsForSelectAll());
    }

    @Override
    public CompletableFuture<ObservableList<T>> selectAllAsync(Object... params) {
        LOGGER.trace("Provádím dotaz selectAll nad tabulkou: {}", getTable());
        if (records.isEmpty() && !selectAllCalled) {
            return db
                .selectAsync(this::parseResultSet, getQuerySelectAll(), params)
                .thenApplyAsync(resultItems -> {
                    records.setAll(resultItems);
                    selectAllCalled = true;
                    return FXCollections.unmodifiableObservableList(records);
                }, ThreadPool.JAVAFX_EXECUTOR);
        }

        return CompletableFuture.completedFuture(FXCollections.unmodifiableObservableList(records));
    }

    @Override
    public CompletableFuture<T> insertAsync(T item) {
        final String query = String.format("INSERT INTO %s (%s) VALUES (%s)", getTable(), getColumnsKeys(), getColumnValues());
        LOGGER.trace("Vkládám položku {} do databáze.", item.toString());
        return db.queryAsync(query, toParamList(item).toArray())
            .thenApplyAsync(value -> {
                final TransactionOperation<T> operation = new InsertOperation<>(item);
                if (db.isTransactional()) {
                    operations.add(operation);
                } else {
                    operation.commit(records);
                }

                return item;
            }, ThreadPool.JAVAFX_EXECUTOR);
    }

    @Override
    public CompletableFuture<T> updateAsync(T item) {
        final String query = String.format("UPDATE %s SET %s WHERE %s = ?", getTable(), getColumnsUpdate(), getColumnWithId());
        final List<Object> params = toParamList(item);
        params.add(item.getId());
        LOGGER.trace("Aktualizuji položku {} v databázi.", item.toString());

        return db.queryAsync(query, params.toArray())
            .thenApplyAsync(value -> {
                final Optional<T> result = records.stream()
                    .filter(t -> item.getId().equals(t.getId()))
                    .findFirst();
                assert result.isPresent();
                final TransactionOperation<T> operation = new UpdateOperation<>(result.get(), item);
                if (db.isTransactional()) {
                    operations.add(operation);
                } else {
                    operation.commit(records);
                }

                return item;
            }, ThreadPool.JAVAFX_EXECUTOR);
    }

    @Override
    public CompletableFuture<T> deleteAsync(T item) {
        final String query = String.format("DELETE FROM %s WHERE %s = ?", getTable(), getColumnWithId());
        LOGGER.trace("Mažu položku {} z databáze.", item.toString());

        return db.queryAsync(query, item.getId())
            .thenApplyAsync(value -> {
                final TransactionOperation<T> operation = new DeleteOperation<>(item);
                if (db.isTransactional()) {
                    operations.add(operation);
                } else {
                    operation.commit(records);
                }

                return item;
            }, ThreadPool.JAVAFX_EXECUTOR);
    }

    @Override
    public void onUpgrade(int newVersion) {
        LOGGER.info("Aktualizuji tabulku: {} na verzi: {}", getTable(), newVersion);
    }

    @Override
    public CompletableFuture<Void> onUpgradeAsync(int newVersion) {
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.info("Aktualizuji tabulku: {} na verzi: {}", getTable(), newVersion);
            return null;
        });
    }

    @Override
    public void clearCache() {
        LOGGER.info("Čistím cache tabulky: {}.", getTable());
        records.clear();
        selectAllCalled = false;
    }

    // endregion

}
