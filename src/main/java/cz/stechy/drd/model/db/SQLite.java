package cz.stechy.drd.model.db;

import cz.stechy.drd.model.db.base.TransactionHandler;
import cz.stechy.drd.model.db.base.Database;
import cz.stechy.drd.model.db.base.OnRowHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

/**
 * Implementace databáze pro SQLite
 */
public class SQLite implements Database {

    // region Constants

    private static final String CONNECTION_PREFIX = "jdbc:sqlite:";
    private static final int MAX_CONNECTIONS = 10;

    // endregion

    // region Variables

    private final SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();
    private final MiniConnectionPoolManager pool;
    private final List<TransactionHandler> transactionHandlers = new ArrayList<>();
    // Lokální verze databáze
    private final int localVersion;

    private Connection transactionalConnection = null;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou instanci databází pro SQLite
     *  @param path Cesta kde se má nacházet soubor s databází
     * @param localVersion Aktuální verze databáze
     */
    public SQLite(String path, int localVersion) {
        dataSource.setUrl(CONNECTION_PREFIX + path);
        pool = new MiniConnectionPoolManager(dataSource, MAX_CONNECTIONS);
        this.localVersion = localVersion;
    }

    // endregion

    // region Private methods

    private long queryTransactional(String query, Object... params) throws SQLException {
        try (Connection connection = pool.getConnection()) {
            return query(connection, query, params);
        }
    }

    private long query(Connection connection, String query, Object... params) throws SQLException {
        long result = -1;
        try (PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.length; i++) {
                // Indexy v databázi jsou od 1, proto i+1
                statement.setObject(i + 1, params[i]);
            }
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    result = generatedKeys.getLong(1);
                }
            }
        }

        return result;
    }

    // endregion

    @Override
    public synchronized long query(String query, Object... params) throws SQLException {
        if (isTransactional()) {
            return query(transactionalConnection, query, params);
        } else {
            return queryTransactional(query, params);
        }
    }

    @Override
    public synchronized void select(OnRowHandler handler, String query, Object... params) throws SQLException {
        try (
            final Connection connection = pool.getConnection();
            final PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                // Indexy v databázi jsou od 1, proto i+1
                statement.setObject(i + 1, params[i]);
            }

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                handler.onRow(result);
            }
        }
    }

    @Override
    public void beginTransaction() throws SQLException {
        if (transactionalConnection != null) {
            return;
        }

        transactionalConnection = pool.getConnection();
        transactionalConnection.setAutoCommit(false);
    }

    @Override
    public void commit() throws SQLException {
        if (transactionalConnection == null) {
            return;
        }

        transactionalConnection.commit();
        transactionalConnection = null;
        transactionHandlers.forEach(TransactionHandler::onCommit);
    }

    @Override
    public void rollback() throws SQLException {
        if (transactionalConnection == null) {
            return;
        }

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
