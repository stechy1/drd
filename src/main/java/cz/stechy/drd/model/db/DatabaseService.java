package cz.stechy.drd.model.db;

import java.util.function.Predicate;
import javafx.collections.ObservableList;

/**
 * Rozhraní pro všechny správce komunikující s databází
 */
public interface DatabaseService<T> {

    /**
     * Metoda pro vytvoření nové tabulky v databázi
     *
     * @throws DatabaseException Pokud se vytvoření tabulky nezdaří
     */
    void createTable() throws DatabaseException;

    /**
     * Provede výběr konkrétního předmětu podle zadaného filtru
     *
     * @param filter Filtr pro výběr konkrétního předmětu
     * @return {@link T}
     * @throws DatabaseException Pokud předmět není nalezen
     */
    T select(Predicate<? super T> filter) throws DatabaseException;

    /**
     * Provede výběr všech předmětů a vrátí ho jako pozorovatelnou kolekci
     *
     * @return Pozorovatelnou kolekci všech předmětů
     */
    ObservableList<T> selectAll();

    /**
     * Vloží předmět do databáze
     *
     * @param item {@link T}
     * @throws DatabaseException Pokud se vložení nezdaří
     */
    void insert(T item) throws DatabaseException;

    /**
     * Aktualizuje předmět v databázi
     *
     * @param item {@Łink T}
     * @throws DatabaseException Pokud se aktualizace nezdaří
     */
    void update(T item) throws DatabaseException;

    /**
     * Smaže předmět z databáze podle ID předmětu
     *
     * @param id ID předmětu, který má být smazán
     * @throws DatabaseException Pokud se smazání nezdaří
     */
    void delete(String id) throws DatabaseException;

    /**
     * Započne novou transakci, během které lze provést více operací
     *
     * @throws DatabaseException Pokud se nepodaří začít novou transakci
     */
    void beginTransaction() throws DatabaseException;

    /**
     * Potvrdí změny v transakci
     *
     * @throws DatabaseException Pokud se nezdaří potvrdit transakci
     */
    void commit() throws DatabaseException;

    /**
     * Zruší změny, které byly provedeny během transakce
     *
     * @throws DatabaseException Pokud se nezdaří zrušit změny
     */
    void rollback() throws DatabaseException;

    /**
     * Metoda se zavolá, pokud je potřeba upgradovat databázi
     *
     * @param newVersion Nová verze databáze
     * @throws DatabaseException Pokud se upgrade nezdaří
     */
    void onUpgrade(int newVersion) throws DatabaseException;
}
