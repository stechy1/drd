package cz.stechy.drd.net;

/**
 * Rozhraní definující metodu, která se zavolá,
 * pokud se nečekaně ztratí spojení mezi klientem a serverem.
 */
@FunctionalInterface
public interface LostConnectionHandler {

    /**
     * Pokud se ztratí spojení
     */
    void onLostConnection();
}
