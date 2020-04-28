package de.lx.entitytags.tags;

import org.bukkit.entity.Player;

import de.lx.entitytags.api.EntityTag;

public class EntityTagPlayerInstance {

    private final EntityTag entityTag;
    private final Player player;

    public EntityTagPlayerInstance(EntityTag entityTag, Player player) {
        this.entityTag = entityTag;
        this.player = player;
    }

    public EntityTag getEntityTag(){
        return this.entityTag;
    }

}