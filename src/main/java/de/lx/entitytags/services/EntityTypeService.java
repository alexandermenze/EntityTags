package de.lx.entitytags.services;

import org.bukkit.entity.EntityType;

import net.minecraft.server.v1_15_R1.EntityTypes;

public interface EntityTypeService {
    int getEntityTypeId(EntityType entityType);
    int getEntityTypeId(String minecraftName);
    EntityTypes<?> getEntityType(EntityType entityType);
}