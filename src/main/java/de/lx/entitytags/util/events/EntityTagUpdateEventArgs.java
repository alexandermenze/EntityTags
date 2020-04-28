package de.lx.entitytags.util.events;

import de.lx.entitytags.api.EntityTag;

public class EntityTagUpdateEventArgs {

    private final EntityTag entityTag;

    public EntityTagUpdateEventArgs(EntityTag entityTag) {
        this.entityTag = entityTag;
    }

    public EntityTag getEntityTag(){
        return this.entityTag;
    }

}