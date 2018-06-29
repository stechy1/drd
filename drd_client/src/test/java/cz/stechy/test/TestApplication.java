package cz.stechy.test;

import cz.stechy.drd.db.MiniConnectionPoolManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

public class TestApplication extends Application {

    private static final String PATH = "C:\\Users\\Petr\\AppData\\Local\\stechy1\\drd_helper\\1.0\\database.sqlite";
    private static Executor JAVA_FX_EXECUTOR = Platform::runLater;

    private Database db;
    private ListView<String> listView;

    @Override
    public void init() throws Exception {
        super.init();
        db = new Database(PATH);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final Scene scene = new Scene(buildScene());
        primaryStage.setScene(scene);
        primaryStage.setWidth(600);
        primaryStage.setHeight(400);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        db.shutdown();
        super.stop();
    }

    private Parent buildScene() {
        listView = new ListView<>();
        final Button btnLoad = new Button("load");
        btnLoad.setOnAction(this::load);
        final Button btnAdd = new Button("add");
        btnAdd.setOnAction(this::insert);
        final Button btnRemove = new Button("remove");
        final ProgressBar pbar = new ProgressBar();

        final VBox buttonBox = new VBox(btnLoad, pbar, btnAdd, btnRemove);

        final HBox layout = new HBox(listView, buttonBox);
        return layout;
    }

    private void load(ActionEvent actionEvent) {
        try {
            db.selectAsync(
                resultSet -> resultSet.getString("name"),
                "SELECT name FROM test")
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    throw new RuntimeException(throwable);
                })
                .thenAccept(strings -> {
                    System.out.println("Select all with: " + strings.size() + " items.");
                    listView.getItems().setAll(strings);
                });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static final String[] NAMES = {
        "petr",
        "michal",
        "jirka",
        "vaclav"
    };

    private void insert(ActionEvent actionEvent) {
        final String name = NAMES[(int) Math.round(Math.random() * 3)];
        System.out.println("Insert init: " + Thread.currentThread());
        final CompletableFuture<Long> future = db.query("INSERT INTO test (name) VALUES (?)", name);
        System.out.println("Between: " + Thread.currentThread());
        future
            .thenAcceptAsync(aLong -> {
                System.out.println("Insert done: " + Thread.currentThread());
                System.out.println("Byl vlozen zaznam s cislem: " + aLong);
                listView.getItems().add(name);
            }, JAVA_FX_EXECUTOR)
            .exceptionally(throwable -> {
                throwable.printStackTrace();
                throw new RuntimeException(throwable);
            });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

@SuppressWarnings("all")
class Database {

    private static final String CONNECTION_PREFIX = "jdbc:sqlite:";
    private static final int MAX_CONNECTIONS = 10;

    private final SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();
    private final MiniConnectionPoolManager pool;

    public Database(String path) {
        dataSource.setUrl(CONNECTION_PREFIX + path);
        pool = new MiniConnectionPoolManager(dataSource, MAX_CONNECTIONS);
    }

    public void shutdown() throws SQLException {
        pool.dispose();
    }

    public CompletableFuture<List<String>> selectAsync(MyOnRowHandler handler, String query,
        Object... params) throws SQLException {
        CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
            final List<String> resultList = new ArrayList<>();
            try (
                final Connection connection = pool.getConnection();
                final PreparedStatement statement = connection.prepareStatement(query)) {
                for (int i = 0; i < params.length; i++) {
                    // Indexy v databázi jsou od 1, proto i+1
                    statement.setObject(i + 1, params[i]);
                }

                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    resultList.add(handler.onRow(result));
                    Thread.sleep(50);
                }

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            return resultList;
        });

        return future;
    }

    public CompletableFuture<Long> query(String query, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            long result = -1;
            try (
                Connection connection = pool.getConnection();
                PreparedStatement statement = connection
                    .prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                System.out.println("Initial query: " + Thread.currentThread());
                for (int i = 0; i < params.length; i++) {
                    // Indexy v databázi jsou od 1, proto i+1
                    statement.setObject(i + 1, params[i]);
                }
                statement.executeUpdate();
                Thread.sleep(1000);

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        result = generatedKeys.getLong(1);
                    }
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            /**/
            return result;
        });
    }
}

@FunctionalInterface
interface MyOnRowHandler {

    String onRow(ResultSet resultSet) throws Exception;
}