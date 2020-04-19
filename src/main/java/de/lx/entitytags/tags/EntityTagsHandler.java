package de.lx.entitytags.tags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Entity;

import de.lx.entitytags.api.EntityTag;
import de.lx.entitytags.api.EntityTags;

public class EntityTagsHandler implements EntityTags {

    private final Entity entity;
    private final List<EntityTag> tags = new ArrayList<>();

    public EntityTagsHandler(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public void addTag(EntityTag tag) {
        this.tags.add(tag);
    }

    @Override
    public void addTag(EntityTag tag, int position) {
        this.tags.add(position, tag);
    }

    @Override
    public void removeTag(EntityTag tag) {
        this.tags.remove(tag);
    }

    @Override
    public List<EntityTag> getTags() {
        return Collections.unmodifiableList(this.tags);
    }

}