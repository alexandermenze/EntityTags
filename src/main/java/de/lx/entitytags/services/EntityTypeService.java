package de.lx.entitytags.services;

import org.bukkit.entity.EntityType;

public interface EntityTypeService {
    int getEntityTypeId(EntityType entityType);
    int getEntityTypeId(String minecraftName);
}