package cz.stechy.drd.old.module;

import cz.stechy.drd.old.Client;
import cz.stechy.drd.net.message.IMessage;

/**
 * Rozhraní pro třídy, které zpracovávají zprávy
 */
public interface IModule {

    /**
     * Inicializuje daný modul
     */
    default void init() {}

    /**
     * Zpracuje přijatou zprávu
     *
     * @param message {@link IMessage} Přijatá zpráva
     * @param client {@link Client} Klient, od kterého zpráva přišla
     */
    void handleMessage(IMessage message, Client client);

    /**
     * Metoda se zavolá, když se klient odpojí od serveru
     *
     * @param client {@link Client} Odpojený klient
     */
    default void onClientDisconnect(Client client) {}

}
