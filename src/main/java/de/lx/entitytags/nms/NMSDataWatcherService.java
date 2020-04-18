package de.lx.entitytags.nms;

import java.util.HashMap;
import java.util.Map;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import org.bukkit.craftbukkit.v1_15_R1.util.CraftChatMessage;
import org.bukkit.entity.EntityType;

import de.lx.entitytags.services.DataWatcherService;
import de.lx.entitytags.services.EntityDataWatcherProducer;

public class NMSDataWatcherService implements DataWatcherService {

    private final EntityDataWatcherProducer dataWatcherProducer;
    private final Map<EntityType, WrappedDataWatcher> cachedDataWatchers = new HashMap<>();

    public NMSDataWatcherService(EntityDataWatcherProducer dataWatcherProducer) {
        this.dataWatcherProducer = dataWatcherProducer;
    }

    @Override
    public WrappedDataWatcher getByEntityType(EntityType entityType) {
        return this.cachedDataWatchers.computeIfAbsent(entityType, dataWatcherProducer::produce);
    }

    @Override
    public void setCustomName(WrappedDataWatcher dataWatcher, String customName) {
        dataWatcher.setObject(2, CraftChatMessage.fromStringOrNull(customName));
    }
    
}