package cz.stechy.drd.core.dispatcher;

import cz.stechy.drd.core.connection.Client;
import cz.stechy.drd.core.IThreadControl;

public interface IClientDispatcher extends IThreadControl {

    /**
     * Zjistí, zda-li se ve frontě nacházejí nějací klienti
     *
     * @return True, pokud jsou ve frontě klienti, jinak False
     */
    boolean hasClientInQueue();

    /**
     * Získá a odebere klienta z fronty, pokud nějakého obsahuje, jinak null
     *
     * @return {@link Client}
     */
    Client getClientFromQueue();

    /**
     * Přidá klienta do čekací fronty
     *
     * @param client {@link Client}
     * @return True, pokud se podařilo klienta přidat do čekací fronty. False, pokud je fronta plná
     */
    boolean addClientToQueue(Client client);

}
