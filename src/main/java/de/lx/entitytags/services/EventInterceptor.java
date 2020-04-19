package de.lx.entitytags.services;

import java.util.UUID;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerRelEntityMove;
import com.comphenix.packetwrapper.WrapperPlayServerRelEntityMoveLook;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.Plugin;

public class EventInterceptor extends PacketAdapter implements Listener {

    private final EntityTypeService entityTypeService;
    private final EntityIdRepository entityIdRepository;
    private final DataWatcherService dataWatcherService;

    private Entity entity;
    private int armorStandEntityId;

    public EventInterceptor(Plugin plugin, EntityTypeService entityTypeService, EntityIdRepository entityIdRepository,
            DataWatcherService dataWatcherService) {
        super(plugin, ListenerPriority.HIGH, PacketType.Play.Server.ENTITY_TELEPORT,
                PacketType.Play.Server.REL_ENTITY_MOVE, PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
        this.entityTypeService = entityTypeService;
        this.entityIdRepository = entityIdRepository;
        this.dataWatcherService = dataWatcherService;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if(this.entity == null)
            return;
        
        Entity entity = getEntityFromPacket(event);

        if (entity == null || entity.getEntityId() != this.entity.getEntityId())
            return;

        WrapperPlayServerEntityTeleport updateArmorStandWrapper = new WrapperPlayServerEntityTeleport();
        updateArmorStandWrapper.setEntityID(this.armorStandEntityId);
        updateArmorStandWrapper.setX(entity.getLocation().getX());
        updateArmorStandWrapper.setY(entity.getLocation().getY());
        updateArmorStandWrapper.setZ(entity.getLocation().getZ());
        updateArmorStandWrapper.setYaw(entity.getLocation().getYaw());
        updateArmorStandWrapper.setPitch(entity.getLocation().getPitch());

        updateArmorStandWrapper.sendPacket(event.getPlayer());
    }

    @EventHandler
    private void onPlayerJoin(PlayerInteractEntityEvent event) {
        this.entity = event.getRightClicked();
        spawnArmorStand(event.getRightClicked(), event.getPlayer());
    }

    private void updateArmorStand() {

    }

    private void spawnArmorStand(Entity entity, Player p) {
        Location spawnLocation = entity.getLocation();

        this.armorStandEntityId = entityIdRepository.reserve();

        WrapperPlayServerSpawnEntityLiving packetWrapper = new WrapperPlayServerSpawnEntityLiving();
        packetWrapper.setEntityID(this.armorStandEntityId);
        packetWrapper.setType(this.entityTypeService.getEntityTypeId(EntityType.ARMOR_STAND));
        packetWrapper.setUniqueId(UUID.randomUUID());
        packetWrapper.setX(spawnLocation.getX());
        packetWrapper.setY(spawnLocation.getY());
        packetWrapper.setZ(spawnLocation.getZ());

        packetWrapper.sendPacket(p);

        WrapperPlayServerEntityMetadata packetMetadata = new WrapperPlayServerEntityMetadata();

        packetMetadata.setEntityID(this.armorStandEntityId);
        WrappedDataWatcher dataWatcher = this.dataWatcherService.getByEntityType(EntityType.ARMOR_STAND);
        this.dataWatcherService.setCustomName(dataWatcher, "Tests 1234");
        this.dataWatcherService.setCustomNameVisible(dataWatcher, true);
        this.dataWatcherService.setNoGravity(dataWatcher, true);
        this.dataWatcherService.setMarker(dataWatcher, true);
        packetMetadata.setMetadata(dataWatcher.getWatchableObjects());

        packetMetadata.sendPacket(p);
    }

    private Entity getEntityFromPacket(PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.REL_ENTITY_MOVE) {
            return new WrapperPlayServerRelEntityMove(event.getPacket()).getEntity(event);
        } else if (event.getPacketType() == PacketType.Play.Server.REL_ENTITY_MOVE_LOOK) {
            return new WrapperPlayServerRelEntityMoveLook(event.getPacket()).getEntity(event);
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
            return new WrapperPlayServerEntityTeleport(event.getPacket()).getEntity(event);
        } else {
            return null;
        }
    }

}