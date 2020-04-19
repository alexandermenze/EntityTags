package de.lx.entitytags.nms;

import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerRelEntityMove;
import com.comphenix.packetwrapper.WrapperPlayServerRelEntityMoveLook;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import org.bukkit.entity.Entity;

import de.lx.entitytags.exceptions.PacketServiceException;
import de.lx.entitytags.services.PacketService;

public class NMSPacketService implements PacketService {

    private static final PacketType[] MOVE_PACKET_TYPES = new PacketType[] {  
        PacketType.Play.Server.REL_ENTITY_MOVE,
        PacketType.Play.Server.REL_ENTITY_MOVE_LOOK,
        PacketType.Play.Server.ENTITY_TELEPORT
    };

    @Override
    public Entity getEntity(PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.REL_ENTITY_MOVE) {
            return new WrapperPlayServerRelEntityMove(event.getPacket()).getEntity(event);
        } else if (event.getPacketType() == PacketType.Play.Server.REL_ENTITY_MOVE_LOOK) {
            return new WrapperPlayServerRelEntityMoveLook(event.getPacket()).getEntity(event);
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
            return new WrapperPlayServerEntityTeleport(event.getPacket()).getEntity(event);
        } else {
            throw new PacketServiceException("Unsupported packet for entity retrieval!");
        }
    }

    @Override
    public PacketType[] getEntityMovePacketTypes() {
        return MOVE_PACKET_TYPES;
    }

}