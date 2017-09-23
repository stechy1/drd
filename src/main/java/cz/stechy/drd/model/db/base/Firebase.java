package cz.stechy.drd.model.db.base;

import com.google.firebase.database.DataSnapshot;
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
     */
    void upload(T item);

    /**
     * Odstraní vzdálený item buď z lokální databáze, nebo ze vzdálené databáze
     *
     * @param item Item, který se má odstranit
     * @param remote True, pokud se má odstranit lokální item ze vzdálené databáze, jinak se
     * odstraní vzdálen item z lokální databáze
     */
    void deleteRemote(T item, boolean remote);

    @FunctionalInterface
    interface OnUploadItem<T> {
        void onUploadRequest(T item);
    }

    @FunctionalInterface
    interface OnDownloadItem<T> {
        void onDownloadRequest(T item);
    }

    @FunctionalInterface
    interface OnDeleteItem<T> {
        void onDeleteRequest(T item, boolean remote);
    }
}
