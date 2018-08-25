package cz.stechy.drd.core.connection;

/**
 * Rozhraní továrny pro správce spojení
 */
public interface IConnectionManagerFactory {

    /**
     * Vytvoří nového správce spojení
     *
     * @param maxClients Maximální počet aktivně komunikujících klientů
     * @param waitingQueueSize Velikost čekací fronty
     * @return {@link IConnectionManager}
     */
    IConnectionManager getConnectionManager(int maxClients, int waitingQueueSize);

}
