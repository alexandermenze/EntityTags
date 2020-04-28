package de.lx.entitytags.nms;

import java.util.Arrays;

import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerRelEntityMove;
import com.comphenix.packetwrapper.WrapperPlayServerRelEntityMoveLook;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import de.lx.entitytags.exceptions.PacketServiceException;
import de.lx.entitytags.services.EntityTypeService;
import de.lx.entitytags.services.PacketService;

public class NMSPacketService implements PacketService {

    private static final PacketType[] MOVE_PACKET_TYPES = new PacketType[] { PacketType.Play.Server.REL_ENTITY_MOVE,
            PacketType.Play.Server.REL_ENTITY_MOVE_LOOK, PacketType.Play.Server.ENTITY_TELEPORT };

    private static final PacketType[] ENTITY_PACKET_TYPES = new PacketType[] { PacketType.Play.Server.REL_ENTITY_MOVE,
            PacketType.Play.Server.REL_ENTITY_MOVE_LOOK, PacketType.Play.Server.ENTITY_TELEPORT,
            PacketType.Play.Server.ENTITY_DESTROY, PacketType.Play.Server.SPAWN_ENTITY_LIVING };

    private final EntityTypeService entityTypeService;

    public NMSPacketService(EntityTypeService entityTypeService) {
        this.entityTypeService = entityTypeService;
    }

    @Override
    public boolean containsEntity(PacketEvent event, Entity entity) {
        if (event.getPacketType() == PacketType.Play.Server.REL_ENTITY_MOVE) {
            return compare(new WrapperPlayServerRelEntityMove(event.getPacket()).getEntity(event), entity);
        } else if (event.getPacketType() == PacketType.Play.Server.REL_ENTITY_MOVE_LOOK) {
            return compare(new WrapperPlayServerRelEntityMoveLook(event.getPacket()).getEntity(event), entity);
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
            return compare(new WrapperPlayServerEntityTeleport(event.getPacket()).getEntity(event), entity);
        } else if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY_LIVING) {
            return compare(new WrapperPlayServerSpawnEntityLiving(event.getPacket()).getEntity(event), entity);
        }else if(event.getPacketType() == PacketType.Play.Server.ENTITY_DESTROY){
            int[] ids = new WrapperPlayServerEntityDestroy(event.getPacket()).getEntityIDs();
            return Arrays.stream(ids).anyMatch(id -> id == entity.getEntityId());
        }else{
            throw new PacketServiceException("Unsupported packet for entity retrieval!");
        }
    }

    @Override
    public PacketType[] getEntityPacketTypes() {
        return ENTITY_PACKET_TYPES;
    }

    @Override
    public PacketType[] getMovePacketTypes() {
        return MOVE_PACKET_TYPES;
    }

    @Override
    public boolean isMovePacket(PacketType type) {
        return Arrays.stream(MOVE_PACKET_TYPES).anyMatch(t -> type == t);
    }

    @Override
    public void sendMove(int entityId, Location location, Player targetPlayer) {
        WrapperPlayServerEntityTeleport wrapper = new WrapperPlayServerEntityTeleport();
        wrapper.setEntityID(entityId);
        wrapper.setX(location.getX());
        wrapper.setY(location.getY());
        wrapper.setZ(location.getZ());
        wrapper.sendPacket(targetPlayer);
    }

    @Override
    public void sendSpawn(int entityId, EntityType type, WrappedDataWatcher dataWatcher, Location location,
            Player targetPlayer) {
        
        WrapperPlayServerSpawnEntityLiving wrapperSpawn = new WrapperPlayServerSpawnEntityLiving();
        wrapperSpawn.setEntityID(entityId);
        wrapperSpawn.setType(this.entityTypeService.getEntityTypeId(type));
        wrapperSpawn.setX(location.getX());
        wrapperSpawn.setY(location.getY());
        wrapperSpawn.setZ(location.getZ());

        WrapperPlayServerEntityMetadata wrapperMetadata = new WrapperPlayServerEntityMetadata();
        wrapperMetadata.setEntityID(entityId);
        wrapperMetadata.setMetadata(dataWatcher.getWatchableObjects());

        wrapperSpawn.sendPacket(targetPlayer);
        wrapperMetadata.sendPacket(targetPlayer);
    }

    @Override
    public void sendDestroy(int entityId, Player targetPlayer) {
        WrapperPlayServerEntityDestroy wrapper = new WrapperPlayServerEntityDestroy();
        wrapper.setEntityIds(new int[]{ entityId });
        wrapper.sendPacket(targetPlayer);
    }

    @Override
    public void sendMetadata(int entityId, WrappedDataWatcher dataWatcher, Player targetPlayer) {
        WrapperPlayServerEntityMetadata wrapperMetadata = new WrapperPlayServerEntityMetadata();
        wrapperMetadata.setEntityID(entityId);
        wrapperMetadata.setMetadata(dataWatcher.getWatchableObjects());
        wrapperMetadata.sendPacket(targetPlayer);
    }

    private boolean compare(Entity a, Entity b){
        if(a == null || b == null)
            return false;
            
        return a.getEntityId() == b.getEntityId();
    }

}