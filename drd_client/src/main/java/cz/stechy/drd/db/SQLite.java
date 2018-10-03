package cz.stechy.drd.db;

import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.db.base.RowTransformHandler;
import cz.stechy.drd.db.base.TransactionHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

/**
 * Implementace databáze pro SQLite
 */
public class SQLite implements Database {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLite.class);

    private static final String CONNECTION_PREFIX = "jdbc:sqlite:";
    private static final int MAX_CONNECTIONS = 10;

    // endregion

    // region Variables

    private final MiniConnectionPoolManager pool;
    private final List<TransactionHandler> transactionHandlers = new ArrayList<>();
    // Lokální verze databáze
    private final int localVersion;

    private Connection transactionalConnection = null;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou instanci databází pro SQLite
     *
     * @param path Cesta kde se má nacházet soubor s databází
     * @param localVersion Aktuální verze databáze
     */
    public SQLite(String path, int localVersion) {
        SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();
        dataSource.setUrl(CONNECTION_PREFIX + path);
        pool = new MiniConnectionPoolManager(dataSource, MAX_CONNECTIONS);
        this.localVersion = localVersion;
    }

    // endregion

    // region Private methods

    private CompletableFuture<Long> queryTransactionalAsync(String query, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return pool.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, ThreadPool.DB_EXECUTOR)
            .thenCompose(connection ->
                queryAsync(connection, query, params)
                    .thenApply(value -> {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        return value;
                    }));
    }

    private CompletableFuture<Long> queryAsync(Connection connection, String query, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            long result;
            try (PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    // Indexy v databázi jsou od 1, proto i+1
                    statement.setObject(i + 1, params[i]);
                }
                LOGGER.debug(query);
                result = statement.executeUpdate();

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            return result;
        }, ThreadPool.DB_EXECUTOR);
    }

    // endregion

    @Override
    public CompletableFuture<Long> queryAsync(String query, Object... params) {
        if (isTransactional()) {
            return queryAsync(transactionalConnection, query, params);
        } else {
            return queryTransactionalAsync(query, params);
        }
    }

    @Override
    public <T> CompletableFuture<List<T>> selectAsync(RowTransformHandler<T> handler, String query, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            final List<T> resultList = new ArrayList<>();
            try (
                final Connection connection = pool.getConnection();
                final PreparedStatement statement = connection.prepareStatement(query)) {
                for (int i = 0; i < params.length; i++) {
                    // Indexy v databázi jsou od 1, proto i+1
                    statement.setObject(i + 1, params[i]);
                }

                LOGGER.debug(query);
                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    resultList.add(handler.transofrm(result));
                }

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            return resultList;
        }, ThreadPool.DB_EXECUTOR);
    }

    @Override
    public void beginTransaction() throws SQLException {
        if (transactionalConnection != null) {
            return;
        }

        LOGGER.debug("Begin transaction");
        transactionalConnection = pool.getConnection();
        transactionalConnection.setAutoCommit(false);
    }

    @Override
    public void commit() throws SQLException {
        if (transactionalConnection == null) {
            return;
        }

        LOGGER.debug("Commit transaction");
        transactionalConnection.commit();
        transactionalConnection = null;
        transactionHandlers.forEach(TransactionHandler::onCommit);
    }

    @Override
    public void rollback() throws SQLException {
        if (transactionalConnection == null) {
            return;
        }

        LOGGER.debug("Rollback transaction");
        transactionalConnection.rollback();
        transactionalConnection = null;
        transactionHandlers.forEach(TransactionHandler::onRollback);
    }

    @Override
    public boolean isTransactional() {
        return transactionalConnection != null;
    }

    @Override
    public void addCommitHandler(TransactionHandler handler) {
        transactionHandlers.add(handler);
    }

    @Override
    public int getVersion() {
        return localVersion;
    }
}
