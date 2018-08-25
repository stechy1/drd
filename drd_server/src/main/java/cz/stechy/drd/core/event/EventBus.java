package cz.stechy.drd.core.event;

import com.google.inject.Singleton;
import cz.stechy.drd.core.IEventHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class EventBus implements IEventBus {

    private final Map<String, List<IEventHandler>> listenerMap = new HashMap<>();

    @Override
    public void registerEventHandler(String messageType, IEventHandler listener) {
        List<IEventHandler> listeners = listenerMap
            .computeIfAbsent(messageType, k -> new ArrayList<>());

        listeners.add(listener);
    }

    @Override
    public void unregisterEventHandler(String messageType,
        IEventHandler listener) {
        final List<IEventHandler> listeners = listenerMap
            .getOrDefault(messageType, Collections.emptyList());

        listeners.remove(listener);
    }

    @Override
    public void publishEvent(IEvent event) {
        final List<IEventHandler> handlers = listenerMap
            .getOrDefault(event.getEventName(), Collections.emptyList());

        handlers.forEach(handler -> handler.handleEvent(event));
    }
}
