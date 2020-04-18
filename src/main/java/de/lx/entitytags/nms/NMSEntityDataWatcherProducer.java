package de.lx.entitytags.nms;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.EntityType;

import de.lx.entitytags.exceptions.NMSException;
import de.lx.entitytags.services.EntityDataWatcherProducer;
import de.lx.entitytags.services.EntityTypeService;
import net.minecraft.server.v1_15_R1.DataWatcher;
import net.minecraft.server.v1_15_R1.Entity;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.World;

public class NMSEntityDataWatcherProducer implements EntityDataWatcherProducer {

    private final Server server;
    private final EntityTypeService entityTypeService;

    public NMSEntityDataWatcherProducer(Server server, EntityTypeService entityTypeService) {
        this.server = server;
        this.entityTypeService = entityTypeService;
    }

    @Override
    public WrappedDataWatcher Produce(EntityType entityType) {
        Optional<org.bukkit.World> bukkitWorld = this.server.getWorlds().stream().findFirst();

        if(!bukkitWorld.isPresent())
            throw new NMSException("Server does not contain any worlds!");

        World world = ((CraftWorld)bukkitWorld.get()).getHandle();

        EntityTypes<?> nmsEntityType = this.entityTypeService.getEntityType(entityType);

        Object entityObject = nmsEntityType.createCreature(world, null, null, null, null, null, false, false);

        if(!(entityObject instanceof Entity))
            throw new NMSException("Spawned object is not of type entity!");

        Optional<Field> dataWatcherField = Arrays.stream(Entity.class.getDeclaredFields()).filter(f -> f.getType() == DataWatcher.class).findFirst();

        if(!dataWatcherField.isPresent())
            throw new NMSException("No dataWatcher field found in entity class!");

        try {
            dataWatcherField.get().setAccessible(true);
            DataWatcher dataWatcher = (DataWatcher) dataWatcherField.get().get(entityObject);
            return new WrappedDataWatcher(dataWatcher);
        } catch (Exception ex) {
            throw new NMSException("Exception occured while retrieving entity dataWatcher!", ex);
        }
    }
}