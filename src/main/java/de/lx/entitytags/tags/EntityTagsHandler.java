package de.lx.entitytags.tags;

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

public class EntityTagsHandler extends PacketAdapter implements EntityTags, Listener {

    private final PacketService packetService;
    private final EntityService entityService;
    private final EntityIdRepository entityIdRepository;
    private final DataWatcherService dataWatcherService;

    private final Entity entity;
    private final List<EntityTagInstance> tags = new ArrayList<>();

    public EntityTagsHandler(Plugin plugin, Entity entity, PacketService packetService,
            EntityService entityService, EntityIdRepository entityIdRepository, DataWatcherService dataWatcherService) {
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
        if(containsEntityTag(tag))
            return;

        this.tags.add(createInstance(tag));
    }

    @Override
    public void addTag(EntityTag tag, int position) {
        if(containsEntityTag(tag))
            return;

        this.tags.add(position, createInstance(tag));
    }

    @Override
    public void removeTag(EntityTag tag) {
        Optional<EntityTagInstance> instance = this.tags.stream().filter(t -> t.getEntityTag() == tag).findFirst();

        if(!instance.isPresent())
            return;

        this.tags.remove(instance.get());
    }

    @Override
    public List<EntityTag> getTags() {
        return Collections.unmodifiableList(
            this.tags.stream().map(t -> t.getEntityTag()).collect(Collectors.toList()));
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if(event.isCancelled() || !this.packetService.containsEntity(event, this.entity))
            return;

        handlePacket(event);
    }

    private void handlePacket(PacketEvent event){

        if(this.packetService.isMovePacket(event.getPacketType())){
            handleMove(event);
        }else if(event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY_LIVING){
            handleSpawn(event);
        }else if(event.getPacketType() == PacketType.Play.Server.ENTITY_DESTROY){
            handleDestroy(event);
        }

    }

    private void handleMove(PacketEvent event){
        Location location = this.entityService.getTagLocation(this.entity);

        for (EntityTagInstance entityTagInstance : tags) {
            this.packetService.sendMove(entityTagInstance.getEntityId(), location, event.getPlayer());
            location = location.add(0, 0.25, 0);
        }
    }

    private void handleSpawn(PacketEvent event){
        Location location = this.entityService.getTagLocation(this.entity); 

        for (EntityTagInstance entityTagInstance : tags) {
            this.packetService.sendSpawn(entityTagInstance.getEntityId(), EntityType.ARMOR_STAND, 
                createDataWatcher(entityTagInstance, event.getPlayer()), location, event.getPlayer());
            location = location.add(0, 0.25, 0);
        }
    }

    private void handleDestroy(PacketEvent event) {
        for (EntityTagInstance entityTagInstance : tags) {
            this.packetService.sendDestroy(entityTagInstance.getEntityId(), event.getPlayer());
        }
    }

    private boolean containsEntityTag(EntityTag entityTag){
        return this.tags.stream().anyMatch(t -> t.getEntityTag() == entityTag);
    }

    private EntityTagInstance createInstance(EntityTag entityTag){
        return new EntityTagInstance(entityTag, this.entityIdRepository.reserve());
    }

    private WrappedDataWatcher createDataWatcher(EntityTagInstance entityTagInstance, Player player){
        WrappedDataWatcher dataWatcher = this.dataWatcherService.getByEntityType(EntityType.ARMOR_STAND);
        this.dataWatcherService.setCustomNameVisible(dataWatcher, true);
        this.dataWatcherService.setInvisible(dataWatcher, true);
        this.dataWatcherService.setMarker(dataWatcher, true);
        this.dataWatcherService.setNoGravity(dataWatcher, true);
        this.dataWatcherService.setCustomName(dataWatcher, entityTagInstance.getEntityTag().getText(player));
        return dataWatcher;
    }
}