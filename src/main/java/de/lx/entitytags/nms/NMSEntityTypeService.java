package de.lx.entitytags.nms;

import org.bukkit.entity.EntityType;

import de.lx.entitytags.services.EntityTypeService;
import net.minecraft.server.v1_15_R1.IRegistry;
import net.minecraft.server.v1_15_R1.MinecraftKey;

public class NMSEntityTypeService implements EntityTypeService {

    @Override
    public int getEntityTypeId(EntityType entityType) {
        return getEntityTypeId(entityType.toString());
    }

    @Override
    public int getEntityTypeId(String minecraftName) {
        return IRegistry.ENTITY_TYPE.a(IRegistry.ENTITY_TYPE.get(MinecraftKey.a(minecraftName)));
    }

}