package de.lx.entitytags.api;

import java.util.List;

import org.bukkit.entity.Entity;

public interface EntityTags {
    Entity getEntity();
    void addTag(EntityTag tag);
    void addTag(EntityTag tag, int position);
    void removeTag(EntityTag tag);
    List<EntityTag> getTags();
}