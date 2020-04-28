package de.lx.entitytags.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerRelEntityMove;
import com.comphenix.packetwrapper.WrapperPlayServerRelEntityMoveLook;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
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

import de.lx.entitytags.api.EntityTag;
import de.lx.entitytags.tags.EntityTagsHandler;
import net.md_5.bungee.api.ChatColor;

public class EventInterceptor extends PacketAdapter implements Listener {

    private class TestTag extends EntityTag {

        @Override
        public boolean isVisible(Player player) {
            return true;
        }

        @Override
        public String getText(Player player) {
            return "Test" + ChatColor.RED + "Tag" + ChatColor.AQUA + "!";
        }

    }

    private final EntityTypeService entityTypeService;
    private final EntityIdRepository entityIdRepository;
    private final DataWatcherService dataWatcherService;
    private final PacketService packetService;
    private final EntityService entityService;

    private Entity entity;
    private int armorStandEntityId;

    private final List<EntityTagsHandler> handlers = new ArrayList<>();

    public EventInterceptor(Plugin plugin, EntityTypeService entityTypeService, EntityIdRepository entityIdRepository,
            DataWatcherService dataWatcherService, PacketService packetService, EntityService entityService) {
        super(plugin, ListenerPriority.HIGH, PacketType.Play.Server.ENTITY_TELEPORT,
                PacketType.Play.Server.REL_ENTITY_MOVE, PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
        this.entityTypeService = entityTypeService;
        this.entityIdRepository = entityIdRepository;
        this.dataWatcherService = dataWatcherService;
        this.packetService = packetService;
        this.entityService = entityService;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        // if(this.entity == null)
        //     return;
        
        // Entity entity = getEntityFromPacket(event);

        // if (entity == null || entity.getEntityId() != this.entity.getEntityId())
        //     return;

        // WrapperPlayServerEntityTeleport updateArmorStandWrapper = new WrapperPlayServerEntityTeleport();
        // updateArmorStandWrapper.setEntityID(this.armorStandEntityId);
        // updateArmorStandWrapper.setX(entity.getLocation().getX());
        // updateArmorStandWrapper.setY(entity.getLocation().getY() + getTagOffset(entity));
        // updateArmorStandWrapper.setZ(entity.getLocation().getZ());
        // updateArmorStandWrapper.setYaw(entity.getLocation().getYaw());
        // updateArmorStandWrapper.setPitch(entity.getLocation().getPitch());

        // updateArmorStandWrapper.sendPacket(event.getPlayer());
    }

    @EventHandler
    private void onPlayerRightClickEntity(PlayerInteractEntityEvent event) {
        this.entity = event.getRightClicked();
        
        Optional<EntityTagsHandler> existingHandler = handlers.stream().filter(h -> h.getEntity().getEntityId() == this.entity.getEntityId()).findFirst();

        if(existingHandler.isPresent()){
            existingHandler.get().addTag(new TestTag());
            return;
        }

        EntityTagsHandler handler = new EntityTagsHandler(plugin, this.entity, this.packetService, this.entityService, this.entityIdRepository, this.dataWatcherService);

        this.plugin.getServer().getPluginManager().registerEvents(handler, this.plugin);
        ProtocolLibrary.getProtocolManager().addPacketListener(handler);

        handlers.add(handler);

        handler.addTag(new EntityTag(){
        
            @Override
            public boolean isVisible(Player player) {
                return true;
            }
        
            @Override
            public String getText(Player player) {
                return "testText!";
            }
        });
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
        packetWrapper.setY(spawnLocation.getY() + getTagOffset(entity));
        packetWrapper.setZ(spawnLocation.getZ());

        WrapperPlayServerEntityMetadata packetMetadata = new WrapperPlayServerEntityMetadata();

        packetMetadata.setEntityID(this.armorStandEntityId);
        WrappedDataWatcher dataWatcher = this.dataWatcherService.getByEntityType(EntityType.ARMOR_STAND);
        this.dataWatcherService.setCustomName(dataWatcher, "Tests 1234");
        this.dataWatcherService.setCustomNameVisible(dataWatcher, true);
        this.dataWatcherService.setNoGravity(dataWatcher, true);
        this.dataWatcherService.setMarker(dataWatcher, true);
        this.dataWatcherService.setInvisible(dataWatcher, true);
        packetMetadata.setMetadata(dataWatcher.getWatchableObjects());

        packetWrapper.sendPacket(p);
        packetMetadata.sendPacket(p);
    }

    private static Entity getEntityFromPacket(PacketEvent event) {
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

    private static double getTagOffset(Entity entity){
        return entity.isCustomNameVisible() ? entity.getHeight() + 0.3 : entity.getHeight();
    }

}