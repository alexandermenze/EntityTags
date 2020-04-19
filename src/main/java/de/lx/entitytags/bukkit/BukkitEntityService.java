package de.lx.entitytags.bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.lx.entitytags.services.EntityService;

public class BukkitEntityService implements EntityService {

    @Override
    public Player[] getNearbyPlayers(Entity entity, double distance) {
        return entity
            .getNearbyEntities(distance, distance, distance)
            .stream()
            .filter(e -> e instanceof Player)
            .map(e -> (Player)e)
            .toArray(Player[]::new);
    }

}