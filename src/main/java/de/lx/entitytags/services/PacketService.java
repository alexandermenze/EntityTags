package de.lx.entitytags.services;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import org.bukkit.entity.Entity;

public interface PacketService {
    PacketType[] getEntityMovePacketTypes();
    Entity getEntity(PacketEvent event);
}