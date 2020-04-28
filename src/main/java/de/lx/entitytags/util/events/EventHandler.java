package de.lx.entitytags.util.events;

public interface EventHandler<T> {

    void handleEvent(Object sender, T args);
}