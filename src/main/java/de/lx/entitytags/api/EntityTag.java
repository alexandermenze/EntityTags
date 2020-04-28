package de.lx.entitytags.api;

import org.bukkit.entity.Player;

import de.lx.entitytags.util.events.EntityTagUpdateEventArgs;
import de.lx.entitytags.util.events.EventSource;

public abstract class EntityTag extends EventSource<EntityTagUpdateEventArgs> {
    
    public abstract boolean isVisible(Player player);
    public abstract String getText(Player player);

    protected void Update(){
        raiseEvent(new EntityTagUpdateEventArgs(this));
    }
}