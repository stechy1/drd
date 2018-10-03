package cz.stechy.drd.db.base;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javafx.collections.ObservableList;

/**
 * Rozhraní definující metody pro komunikaci s databází na serveru
 */
public interface OnlineTable<T> {

    /**
     * Vrátí název tabulky, se kterou se bude pracovat
     *
     * @return Název tabulky
     */
    String getFirebaseChildName();

    /**
     * Vybere záznam podle zadaného filtru
     *
     * @param filter Filter záznamů
     * @return {@link Optional<T>} Optional s nalezeným záznamem, nebo prázdnou hodnotu
     */
    Optional<T> selectOnline(Predicate<? super T> filter);

    /**
     * Vrátí pozorovatelnou kolekci všech online záznamů
     *
     * @return {@link ObservableList<T>} Kolekci všech záznamů
     */
    ObservableList<T> selectAllOnline();

    /**
     * Nahraje item na server
     *
     * @param item {@link T} Item, který se má nahrát
     * @return {@link CompletableFuture<Void>}
     */
    CompletableFuture<Void> uploadAsync(T item);

    /**
     * Aktualizuje item na serveru
     *
     * @param item {@link T} Item, který se má aktualizovat
     * @return {@link CompletableFuture<Void>}
     */
    CompletableFuture<Void> updateOnlineAsync(T item);

    /**
     * Odstraní item ze serveru
     *
     * @param item {@link T} Item, který se má odstranit
     * @return {@link CompletableFuture<Void>}
     */
    CompletableFuture<Void> deleteRemoteAsync(T item);

    /**
     * Konvertuje {@link Map} na instanci třídy {@link T}
     *
     * @param map Kolekce obsahující data o třídě {@link T}
     * @return Instanci třídy {@link T}
     */
    T fromStringMap(Map<String, Object> map);

    /**
     * Konvertuje instanci třídy {@link T} na kolekci
     *
     * @param item Instance třídy {@link T}
     * @return Kolekci obsahující data o třídě {@link T}
     */
    Map<String, Object> toStringItemMap(T item);
}
