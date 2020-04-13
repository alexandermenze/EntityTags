package de.lx.EntityTags;

import java.util.UUID;

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

    private static final int STATIC_ENTITY_ID = 32999;

    public PlayerMovePacketListener(Plugin plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.REL_ENTITY_MOVE);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        //System.out.println("Received relative move!");
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event){
        Location playerLocation = event.getPlayer().getLocation();

        WrapperPlayServerSpawnEntityLiving packetWrapper = new WrapperPlayServerSpawnEntityLiving();
        packetWrapper.setEntityID(STATIC_ENTITY_ID);
        packetWrapper.setType(EntityType.ARMOR_STAND);
        packetWrapper.setUniqueId(UUID.randomUUID());
        packetWrapper.setX(playerLocation.getX());
        packetWrapper.setY(playerLocation.getY());
        packetWrapper.setZ(playerLocation.getZ());

        packetWrapper.sendPacket(event.getPlayer());
    }

}