package de.lx.entitytags.api;

import org.bukkit.entity.Player;

public abstract class EntityTag {
    
    public abstract boolean isVisible(Player player);
    public abstract String getText(Player player);

    public final void update(){
        
    }
}