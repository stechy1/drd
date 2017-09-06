package cz.stechy.drd.model.db;

import java.util.function.Predicate;
import javafx.collections.ObservableList;

/**
 * Rozhraní pro všechny správce komunikující s databází
 */
public interface DatabaseService<T> {

    void createTable() throws DatabaseException;

    T select(Predicate<? super T> filter) throws DatabaseException;

    ObservableList<T> selectAll();

    void insert(T item) throws DatabaseException;

    void update(T item) throws DatabaseException;

    void delete(String id) throws DatabaseException;

    void beginTransaction() throws DatabaseException;

    void commit() throws DatabaseException;

    void rollback() throws DatabaseException;

    /**
     * Metoda se zavolá, pokud je potřeba upgradovat databázi
     *
     * @param newVersion Nová verze databáze
     * @throws DatabaseException Pokud se upgrade nezdaří
     */
    void onUpgrade(int newVersion) throws DatabaseException;
}
