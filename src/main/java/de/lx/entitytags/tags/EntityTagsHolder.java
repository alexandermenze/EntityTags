package de.lx.entitytags.tags;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.comphenix.protocol.ProtocolManager;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;

import de.lx.entitytags.api.EntityTags;
import de.lx.entitytags.services.DataWatcherService;
import de.lx.entitytags.services.EntityIdRepository;
import de.lx.entitytags.services.EntityService;
import de.lx.entitytags.services.PacketService;

public class EntityTagsHolder implements Listener, Closeable {

    private final Set<EntityTagsHandler> entityTags = new HashSet<>();
    private final Plugin plugin;
    private final PacketService packetService;
    private final EntityService entityService;
    private final EntityIdRepository entityIdRepository;
    private final DataWatcherService dataWatcherService;
    private final ProtocolManager protocolManager;

    public EntityTagsHolder(Plugin plugin, PacketService packetService, EntityService entityService,
            EntityIdRepository entityIdRepository, DataWatcherService dataWatcherService,
            ProtocolManager protocolManager) {
        this.plugin = plugin;
        this.packetService = packetService;
        this.entityService = entityService;
        this.entityIdRepository = entityIdRepository;
        this.dataWatcherService = dataWatcherService;
        this.protocolManager = protocolManager;
    }

    public EntityTags ofEntity(Entity entity) {
        return findOrCreate(entity);
    }

    public boolean hasTags(Entity entity) {
        return find(entity).isPresent();
    }

    @Override
    public void close() throws IOException {
        this.entityTags.stream().collect(Collectors.toList()).forEach(this::remove);
    }

    @EventHandler
    private void onEntityDeath(EntityDeathEvent event) {
        Optional<EntityTagsHandler> entityTagsHandler = find(event.getEntity());

        if (!entityTagsHandler.isPresent())
            return;

        remove(entityTagsHandler.get());
    }

    private void remove(EntityTagsHandler entityTagsHandler) {
        tryClose(entityTagsHandler);
        HandlerList.unregisterAll(entityTagsHandler);
        protocolManager.removePacketListener(entityTagsHandler);
        this.entityTags.remove(entityTagsHandler);
    }

    private Optional<EntityTagsHandler> find(Entity entity) {
        return this.entityTags.stream().filter(t -> t.getEntity().getEntityId() == entity.getEntityId()).findFirst();
    }

    private EntityTagsHandler findOrCreate(Entity entity) {
        Optional<EntityTagsHandler> existingHandler = find(entity);
        return existingHandler.isPresent() ? existingHandler.get() : create(entity);
    }

    private EntityTagsHandler create(Entity entity) {
        EntityTagsHandler handler = new EntityTagsHandler(this.plugin, entity, this.packetService, this.entityService,
                this.entityIdRepository, this.dataWatcherService);

        this.plugin.getServer().getPluginManager().registerEvents(handler, this.plugin);
        this.protocolManager.addPacketListener(handler);

        return handler;
    }

    private static void tryClose(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception ex) {
            System.out.println("Error closing object: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}