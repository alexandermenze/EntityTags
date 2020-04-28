package de.lx.entitytags.nms;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import org.bukkit.craftbukkit.v1_15_R1.util.CraftChatMessage;
import org.bukkit.entity.EntityType;

import de.lx.entitytags.services.DataWatcherService;
import de.lx.entitytags.services.EntityDataWatcherProducer;
import de.lx.entitytags.util.BitFlags;

public class NMSDataWatcherService implements DataWatcherService {

    private final EntityDataWatcherProducer dataWatcherProducer;
    private final Map<EntityType, WrappedDataWatcher> cachedDataWatchers = new HashMap<>();

    public NMSDataWatcherService(EntityDataWatcherProducer dataWatcherProducer) {
        this.dataWatcherProducer = dataWatcherProducer;
    }

    @Override
    public WrappedDataWatcher getByEntityType(EntityType entityType) {
        return this.cachedDataWatchers.computeIfAbsent(entityType, dataWatcherProducer::produce).deepClone();
    }

    @Override
    public void setCustomName(WrappedDataWatcher dataWatcher, String customName) {
        dataWatcher.setObject(2, Optional.of(CraftChatMessage.fromStringOrNull(customName)));
    }

    @Override
    public void setCustomNameVisible(WrappedDataWatcher dataWatcher, boolean visible) {
        dataWatcher.setObject(3, visible);
    }

    @Override
    public void setNoGravity(WrappedDataWatcher dataWatcher, boolean noGravity) {
        dataWatcher.setObject(5, noGravity);
    }

    @Override
    public void setMarker(WrappedDataWatcher dataWatcher, boolean marker) {
        dataWatcher.setObject(14, BitFlags.set(dataWatcher.getByte(14), (byte)0x10, marker));
    }

    @Override
    public void setInvisible(WrappedDataWatcher dataWatcher, boolean invisible) {
        dataWatcher.setObject(0, BitFlags.set(dataWatcher.getByte(0), (byte)0x20, invisible));
    }
    
}