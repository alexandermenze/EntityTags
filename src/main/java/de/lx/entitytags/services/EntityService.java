package de.lx.entitytags.services;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface EntityService {
    Player[] getNearbyPlayers(Entity entity, double distance);
}