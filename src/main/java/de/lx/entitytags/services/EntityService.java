package de.lx.entitytags.services;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface EntityService {
    Player[] getNearbyPlayers(Entity entity, double distance);
    Location getTagLocation(Entity entity);
}