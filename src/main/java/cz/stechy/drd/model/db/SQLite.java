package cz.stechy.drd.model.db;

import cz.stechy.drd.model.db.base.Database;
import cz.stechy.drd.model.db.base.OnRowHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

/**
 * Implementace databáze pro SQLite
 */
public class SQLite implements Database {

    private static final String CONNECTION_PREFIX = "jdbc:sqlite:";
    private static final int MAX_CONNECTIONS = 10;

    private final SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();
    private final MiniConnectionPoolManager pool;

    /**
     * Vytvoří novou instanci databází pro SQLite
     */
    public SQLite(String path) throws SQLException {
        dataSource.setUrl(CONNECTION_PREFIX + path);
        pool = new MiniConnectionPoolManager(dataSource, MAX_CONNECTIONS);
    }

    @Override
    public long query(String query, Object... params) throws SQLException {
        long result = -1;
        try (
            Connection connection = pool.getConnection();
            PreparedStatement statement = connection
                .prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
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

    @Override
    public void select(OnRowHandler handler, String query, Object... params) throws SQLException {
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
}
