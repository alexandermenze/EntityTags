package de.lx.entitytags.util.events;

import java.util.HashSet;
import java.util.Set;

public class EventSource<T> {

    private final Set<EventHandler<T>> eventHandlers = new HashSet<>();

    protected void raiseEvent(T args){
        eventHandlers.stream().forEach(h -> h.handleEvent(this, args));
    }

    public final void registerEventHandler(EventHandler<T> handler){
        this.eventHandlers.add(handler);
    }

    public final void unregisterEventHandler(EventHandler<T> handler){
        this.eventHandlers.remove(handler);
    }
}