package cz.stechy.drd.model.db.base;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference.CompletionListener;
import java.util.Map;

/**
 * Rozhraní definující metody pro komunikaci s firebase
 */
public interface Firebase<T> {

    /**
     * Konvertuje {@link DataSnapshot} na instanci třídy {@link T}
     *
     * @param snapshot Snapshot itemu
     * @return Instanci třídy {@link T}
     */
    T parseDataSnapshot(DataSnapshot snapshot);

    /**
     * Namapuje vybraný item do mapy
     *
     * @param item Item, který se má převést do mapy
     * @return Mapu, kde klíč je název sloupce a hodnota je hodnota sloupce
     */
    Map<String, Object> toFirebaseMap(T item);

    /**
     * Nahraje item do sdílené databáze
     *
     * @param item Item, který se má nahrát
     * @param listener {@link CompletionListener}
     */
    void uploadAsync(T item, CompletionListener listener);

    /**
     * Odstraní vzdálený item buď z lokální databáze, nebo ze vzdálené databáze
     *
     * @param item Item, který se má odstranit
     * @param remote True, pokud se má odstranit lokální item ze vzdálené databáze, jinak se
     * odstraní vzdálen item z lokální databáze
     * @param listener {@link CompletionListener}
     */
    void deleteRemoteAsync(T item, boolean remote, CompletionListener listener);
}
