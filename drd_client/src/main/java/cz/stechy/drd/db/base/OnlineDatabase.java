package cz.stechy.drd.db.base;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Rozhraní definující metody pro komunikaci s databází na serveru
 */
public interface OnlineDatabase<T> {

    /**
     * Vrátí název tabulky, se kterou se bude pracovat
     *
     * @return Název tabulky
     */
    String getFirebaseChildName();

    /**
     * Konvertuje {@link Map} na instanci třídy {@link T}
     *
     * @param map Kolekce obsahující data o třídě {@link T}
     * @return Instanci třídy {@link T}
     */
    T fromStringItemMap(Map<String, Object> map);

    /**
     * Konvertuje instanci třídy {@link T} na kolekci
     *
     * @param item Instance třídy {@link T}
     * @return Kolekci obsahující data o třídě {@link T}
     */
    Map<String, Object> toStringItemMap(T item);

    /**
     * Nahraje item na server
     *
     * @param item {@link T} Item, který se má nahrát
     * @return {@link CompletableFuture<Void}
     */
    CompletableFuture<Void> uploadAsync(T item);

    /**
     * Odstraní item ze serveru
     *
     * @param item {@link T} Item, který se má odstranit
     * @return {@link CompletableFuture<Void>}
     */
    CompletableFuture<Void> deleteRemoteAsync(T item);
}
