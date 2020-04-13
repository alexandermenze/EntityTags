package de.lx.EntityTags;

import java.util.UUID;
import java.util.stream.Collectors;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class PlayerMovePacketListener extends PacketAdapter implements Listener {

    private static final EntityType ARMOR_STAND_ENTITY_TYPE = EntityType.DROPPED_ITEM;
    private static final int STATIC_ENTITY_ID = 32999;

    public PlayerMovePacketListener(Plugin plugin) {
        super(plugin, ListenerPriority.NORMAL, 
            PacketType.Play.Server.SPAWN_ENTITY_LIVING,
            PacketType.Play.Server.ENTITY_METADATA,
            PacketType.Play.Server.ENTITY_DESTROY,
            PacketType.Play.Server.ENTITY_EQUIPMENT,
            PacketType.Play.Server.NAMED_ENTITY_SPAWN);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if(event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY_LIVING){
            WrapperPlayServerSpawnEntityLiving packetWrapper = new WrapperPlayServerSpawnEntityLiving(event.getPacket());
            System.out.println("Entity living spawned: " + packetWrapper.getEntityID() + ", EntityType: " + packetWrapper.getType());
        }else if(event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA){
            WrapperPlayServerEntityMetadata packetWrapper = new WrapperPlayServerEntityMetadata(event.getPacket());

            String metadata = packetWrapper.getMetadata()
                .stream()
                .map(m -> "(" + m.getIndex() + ", " + m.getValue() + ")")
                .collect(Collectors.joining(", "));

            System.out.println("Entity metadata updated: " + packetWrapper.getEntityID() + ", Metadata: " + metadata);
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event){
        Location playerLocation = event.getPlayer().getLocation();
        
        WrapperPlayServerSpawnEntityLiving packetWrapper = new WrapperPlayServerSpawnEntityLiving();
        packetWrapper.setEntityID(STATIC_ENTITY_ID);
        packetWrapper.setType(ARMOR_STAND_ENTITY_TYPE);
        packetWrapper.setUniqueId(UUID.randomUUID());
        packetWrapper.setX(playerLocation.getX());
        packetWrapper.setY(playerLocation.getY());
        packetWrapper.setZ(playerLocation.getZ());

        packetWrapper.sendPacket(event.getPlayer());
    }

}