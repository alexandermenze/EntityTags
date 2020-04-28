package de.lx.entitytags.bukkit;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.lx.entitytags.services.EntityService;

public class BukkitEntityService implements EntityService {

    private static final double NEARBY_ENTITY_HEIGHT = 256;
    private static final double TAG_SPACING = 0.25;

    @Override
    public Player[] getNearbyPlayers(Entity entity, double distance) {
        return entity.getNearbyEntities(distance, NEARBY_ENTITY_HEIGHT, distance).stream().filter(e -> e instanceof Player)
                .map(e -> (Player) e).toArray(Player[]::new);
    }

    @Override
    public Location getTagLocation(Entity entity) {
        return entity.getLocation().add(0, entity.getHeight() + TAG_SPACING, 0);
    }

}