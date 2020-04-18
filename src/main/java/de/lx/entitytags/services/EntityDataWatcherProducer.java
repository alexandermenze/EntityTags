package de.lx.entitytags.services;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import org.bukkit.entity.EntityType;

public interface EntityDataWatcherProducer {
    WrappedDataWatcher produce(EntityType entityType);
}