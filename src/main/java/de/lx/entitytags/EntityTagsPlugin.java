package de.lx.entitytags;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import de.lx.entitytags.api.EntityTags;
import de.lx.entitytags.bukkit.BukkitEntityService;
import de.lx.entitytags.nms.NMSDataWatcherService;
import de.lx.entitytags.nms.NMSEntityDataWatcherProducer;
import de.lx.entitytags.nms.NMSEntityIdRepository;
import de.lx.entitytags.nms.NMSEntityTypeService;
import de.lx.entitytags.nms.NMSPacketService;
import de.lx.entitytags.services.DataWatcherService;
import de.lx.entitytags.services.EntityIdRepository;
import de.lx.entitytags.services.EntityService;
import de.lx.entitytags.services.PacketService;
import de.lx.entitytags.tags.EntityTagsHandler;

public class EntityTagsPlugin extends JavaPlugin {

    private final Set<EntityTagsHandler> entityTags = new HashSet<>();
    private PacketService packetService;
    private EntityService entityService;
    private EntityIdRepository entityIdRepository;
    private DataWatcherService dataWatcherService;
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        this.packetService = new NMSPacketService(new NMSEntityTypeService());
        this.entityService = new BukkitEntityService();
        this.entityIdRepository = new NMSEntityIdRepository();
        this.dataWatcherService = new NMSDataWatcherService(
            new NMSEntityDataWatcherProducer(this.getServer(), new NMSEntityTypeService()));
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onDisable() {
        this.entityTags.stream().forEach(protocolManager::removePacketListener);
        this.entityTags.clear();
        HandlerList.unregisterAll(this);
    }

    public EntityTags ofEntity(Entity entity){
        return findOrCreate(entity);
    }

    public boolean hasTags(Entity entity){
        return find(entity).isPresent();
    }

    private Optional<EntityTagsHandler> find(Entity entity){
        return this.entityTags
            .stream()
            .filter(t -> t.getEntity().getEntityId() == entity.getEntityId())
            .findFirst();
    }

    private EntityTagsHandler findOrCreate(Entity entity){
        Optional<EntityTagsHandler> existingHandler = find(entity);
        return existingHandler.isPresent() ? existingHandler.get() : create(entity);
    }

    private EntityTagsHandler create(Entity entity){
        EntityTagsHandler handler = new EntityTagsHandler(this, entity, 
            this.packetService, this.entityService, this.entityIdRepository, this.dataWatcherService);

        this.getServer().getPluginManager().registerEvents(handler, this);
        this.protocolManager.addPacketListener(handler);

        return handler;
    }
}