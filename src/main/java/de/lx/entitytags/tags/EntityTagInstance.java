package de.lx.entitytags.tags;

import de.lx.entitytags.api.EntityTag;

public class EntityTagInstance {

    private final EntityTag entityTag;
    private final int entityId;

    public EntityTagInstance(EntityTag entityTag, int entityId) {
        this.entityTag = entityTag;
        this.entityId = entityId;
    }

    public EntityTag getEntityTag(){
        return this.entityTag;
    }

    public int getEntityId(){
        return this.entityId;
    }

}