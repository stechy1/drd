package cz.stechy.drd.core.dispatcher;

/**
 * Rozhraní továrny pro výrobu {@link IClientDispatcher}
 */
public interface IClientDispatcherFactory {

    /**
     * Vytvoří novou instanci třídy {@link IClientDispatcher}
     *
     * @param waitingQueueSize Velikost čekací fronty
     * @return {@link IClientDispatcher}
     */
    IClientDispatcher getClientDispatcher(int waitingQueueSize);

}
