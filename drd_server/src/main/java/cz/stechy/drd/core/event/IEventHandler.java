package cz.stechy.drd.core.event;

@FunctionalInterface
public interface IEventHandler {

    /**
     * Handler určité události
     *
     * @param event {@link IEvent}
     */
    void handleEvent(IEvent event);

}