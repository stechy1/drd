package cz.stechy.drd.core;


import cz.stechy.drd.core.event.IEvent;

public interface IEventHandler {

    void handleEvent(IEvent event);

}
