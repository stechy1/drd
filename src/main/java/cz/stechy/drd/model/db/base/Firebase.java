package cz.stechy.drd.model.db.base;

/**
 * Rozhraní definující metody pro komunikaci s firebase
 */
public interface Firebase<T> {

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
