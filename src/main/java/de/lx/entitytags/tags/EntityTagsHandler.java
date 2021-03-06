package de.lx.entitytags.tags;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import de.lx.entitytags.api.EntityTag;
import de.lx.entitytags.api.EntityTags;
import de.lx.entitytags.services.DataWatcherService;
import de.lx.entitytags.services.EntityIdRepository;
import de.lx.entitytags.services.EntityService;
import de.lx.entitytags.services.PacketService;
import de.lx.entitytags.util.events.EntityTagUpdateEventArgs;
import de.lx.entitytags.util.events.EventHandler;

public class EntityTagsHandler extends PacketAdapter
        implements EntityTags, Listener, EventHandler<EntityTagUpdateEventArgs>, Closeable {

    private final static double TAG_SPACING = 0.3;
    private final static double INITIAL_SPAWN_DISTANCE = 64;

    private final PacketService packetService;
    private final EntityService entityService;
    private final EntityIdRepository entityIdRepository;
    private final DataWatcherService dataWatcherService;

    private final Entity entity;
    private final List<EntityTagInstance> tags = new ArrayList<>();

    public EntityTagsHandler(Plugin plugin, Entity entity, PacketService packetService, EntityService entityService,
            EntityIdRepository entityIdRepository, DataWatcherService dataWatcherService) {
        super(plugin, ListenerPriority.MONITOR, packetService.getEntityPacketTypes());
        this.entity = entity;
        this.entityIdRepository = entityIdRepository;
        this.dataWatcherService = dataWatcherService;
        this.packetService = packetService;
        this.entityService = entityService;
    }

    @Override
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public void addTag(EntityTag tag) {
        if (containsEntityTag(tag))
            return;

        EntityTagInstance instance = createInstance(tag);
        this.tags.add(instance);
        handleAddInstance(instance);
    }

    @Override
    public void addTag(EntityTag tag, int position) {
        if (containsEntityTag(tag))
            return;

        EntityTagInstance instance = createInstance(tag);
        this.tags.add(instance);
        handleAddInstance(instance);
    }

    @Override
    public void removeTag(EntityTag tag) {
        Optional<EntityTagInstance> instance = this.tags.stream().filter(t -> t.getEntityTag() == tag).findFirst();

        if (!instance.isPresent())
            return;

        destroyInstance(instance.get());
        this.tags.remove(instance.get());
    }

    @Override
    public List<EntityTag> getTags() {
        return Collections.unmodifiableList(this.tags.stream().map(t -> t.getEntityTag()).collect(Collectors.toList()));
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.isCancelled() || !this.packetService.containsEntity(event, this.entity))
            return;

        handlePacket(event);
    }

    @Override
    public void handleEvent(Object sender, EntityTagUpdateEventArgs args) {
        handleUpdateAllForAllPlayers();
    }

    @Override
    public void close() throws IOException {
        this.tags.stream().map(t -> t.getEntityTag()).collect(Collectors.toList()).forEach(this::removeTag);
    }

    private void handlePacket(PacketEvent event) {

        if (this.packetService.isMovePacket(event.getPacketType())) {
            handleMoveAllForPlayer(event.getPlayer());
        } else if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY_LIVING) {
            handleSpawnAllForPlayer(event.getPlayer());
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_DESTROY) {
            handleDestroyAllForPlayer(event.getPlayer());
        }

    }

    private void handleUpdateAllForAllPlayers() {
        for (Player player : this.entityService.getNearbyPlayers(this.entity, INITIAL_SPAWN_DISTANCE)) {
            handleUpdateAllForPlayer(player);
        }
    }

    private void handleUpdateAllForPlayer(Player player) {
        for (EntityTagInstance entityTagInstance : tags) {
            this.packetService.sendMetadata(entityTagInstance.getEntityId(),
                    createDataWatcher(entityTagInstance, player), player);
        }
    }

    private void handleMoveAllForPlayer(Player player) {
        Location location = this.entityService.getTagLocation(this.entity);

        for (EntityTagInstance entityTagInstance : tags) {
            this.packetService.sendMove(entityTagInstance.getEntityId(), location, player);
            location = location.add(0, TAG_SPACING, 0);
        }
    }

    private void handleSpawnAllForPlayer(Player player) {
        Location location = this.entityService.getTagLocation(this.entity);

        for (EntityTagInstance entityTagInstance : tags) {
            if (!entityTagInstance.getEntityTag().isVisible(player))
                continue;

            this.packetService.sendSpawn(entityTagInstance.getEntityId(), EntityType.ARMOR_STAND,
                    createDataWatcher(entityTagInstance, player), location, player);
            location = location.add(0, TAG_SPACING, 0);
        }
    }

    private void handleSpawnSingleForPlayer(EntityTagInstance entityTagInstance, Player player) {
        if (!entityTagInstance.getEntityTag().isVisible(player))
            return;

        this.packetService.sendSpawn(entityTagInstance.getEntityId(), EntityType.ARMOR_STAND,
                createDataWatcher(entityTagInstance, player), this.entityService.getTagLocation(entity), player);
    }

    private void handleDestroyAllForPlayer(Player player) {
        for (EntityTagInstance entityTagInstance : tags) {
            this.packetService.sendDestroy(entityTagInstance.getEntityId(), player);
        }
    }

    private void handleDestroySingleForPlayer(EntityTagInstance entityTagInstance, Player player) {
        this.packetService.sendDestroy(entityTagInstance.getEntityId(), player);
    }

    private boolean containsEntityTag(EntityTag entityTag) {
        return this.tags.stream().anyMatch(t -> t.getEntityTag() == entityTag);
    }

    private EntityTagInstance createInstance(EntityTag entityTag) {
        entityTag.registerEventHandler(this);
        return new EntityTagInstance(entityTag, this.entityIdRepository.reserve());
    }

    private void destroyInstance(EntityTagInstance instance) {
        instance.getEntityTag().unregisterEventHandler(this);
        handleRemoveInstance(instance);
        this.entityIdRepository.free(instance.getEntityId());
    }

    private WrappedDataWatcher createDataWatcher(EntityTagInstance entityTagInstance, Player player) {
        WrappedDataWatcher dataWatcher = this.dataWatcherService.getByEntityType(EntityType.ARMOR_STAND);
        this.dataWatcherService.setCustomNameVisible(dataWatcher, true);
        this.dataWatcherService.setInvisible(dataWatcher, true);
        this.dataWatcherService.setMarker(dataWatcher, true);
        this.dataWatcherService.setNoGravity(dataWatcher, true);
        this.dataWatcherService.setCustomName(dataWatcher, entityTagInstance.getEntityTag().getText(player));
        return dataWatcher;
    }

    private void handleAddInstance(EntityTagInstance instance) {
        for (Player player : this.entityService.getNearbyPlayers(this.entity, INITIAL_SPAWN_DISTANCE)) {
            handleSpawnSingleForPlayer(instance, player);
            handleMoveAllForPlayer(player);
        }
    }

    private void handleRemoveInstance(EntityTagInstance instance) {
        for (Player player : this.entityService.getNearbyPlayers(this.entity, INITIAL_SPAWN_DISTANCE)) {
            handleDestroySingleForPlayer(instance, player);
        }
    }
}