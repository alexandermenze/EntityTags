package de.lx.entitytags.services;

import java.util.UUID;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class EventInterceptor extends PacketAdapter implements Listener {

    private final EntityTypeService entityTypeService;
    private final EntityIdRepository entityIdRepository;

    private Player player;
    private int entityId;

    public EventInterceptor(Plugin plugin, EntityTypeService entityTypeService, EntityIdRepository entityIdRepository) {
        super(plugin, ListenerPriority.HIGH, PacketType.Play.Server.ENTITY_TELEPORT,
                PacketType.Play.Server.REL_ENTITY_MOVE, PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
        this.entityTypeService = entityTypeService;
        this.entityIdRepository = entityIdRepository;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event){
        this.player = event.getPlayer();
        spawnArmorStand(player);
    }

    private void updateArmorStand(){

        

    }

    private void spawnArmorStand(Player player){
        Location spawnLocation = player.getLocation();

        this.entityId = entityIdRepository.reserve();

        WrapperPlayServerSpawnEntityLiving packetWrapper = new WrapperPlayServerSpawnEntityLiving();
        packetWrapper.setEntityID(entityId);
        packetWrapper.setType(this.entityTypeService.getEntityTypeId(EntityType.ARMOR_STAND));
        packetWrapper.setUniqueId(UUID.randomUUID());
        packetWrapper.setX(spawnLocation.getX());
        packetWrapper.setY(spawnLocation.getY());
        packetWrapper.setZ(spawnLocation.getZ());

        packetWrapper.sendPacket(player);
    }

}