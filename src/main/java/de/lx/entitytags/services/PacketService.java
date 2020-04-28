package de.lx.entitytags.services;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public interface PacketService {
    PacketType[] getEntityPacketTypes();
    PacketType[] getMovePacketTypes();
    boolean containsEntity(PacketEvent event, Entity entity);
    boolean isMovePacket(PacketType type);
    void sendMove(int entityId, Location location, Player targetPlayer);
    void sendSpawn(int entityId, EntityType type, WrappedDataWatcher dataWatcher, Location location, Player targetPlayer);
    void sendDestroy(int entityId, Player targetPlayer);
}