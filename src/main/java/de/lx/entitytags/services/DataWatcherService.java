package de.lx.entitytags.services;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import org.bukkit.entity.EntityType;

public interface DataWatcherService {
    WrappedDataWatcher getByEntityType(EntityType entityType);
    void setCustomName(WrappedDataWatcher dataWatcher, String customName);
}