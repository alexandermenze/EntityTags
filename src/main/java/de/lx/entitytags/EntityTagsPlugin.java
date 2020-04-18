package de.lx.entitytags;

import java.util.ArrayList;
import java.util.List;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketListener;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import de.lx.entitytags.nms.NMSDataWatcherService;
import de.lx.entitytags.nms.NMSEntityDataWatcherProducer;
import de.lx.entitytags.nms.NMSEntityIdRepository;
import de.lx.entitytags.nms.NMSEntityTypeService;
import de.lx.entitytags.services.EventInterceptor;

public class EntityTagsPlugin extends JavaPlugin {

    private final List<PacketListener> _packetListeners = new ArrayList<PacketListener>();
    private final List<Listener> _listeners = new ArrayList<Listener>();

    @Override
    public void onEnable() {
        CreatePacketListeners();

        final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        _packetListeners.stream().forEach(protocolManager::addPacketListener);

        _listeners.stream().forEach(
            (l) -> getServer().getPluginManager().registerEvents(l, this));
    }

    @Override
    public void onDisable() {
        final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        _packetListeners.stream().forEach(protocolManager::removePacketListener);

        HandlerList.unregisterAll(this);
    }

    private void CreatePacketListeners(){

        EventInterceptor eventInterceptor = 
            new EventInterceptor(this, 
            new NMSEntityTypeService(), 
            new NMSEntityIdRepository(), 
            new NMSDataWatcherService(new NMSEntityDataWatcherProducer(getServer(), new NMSEntityTypeService())));

        _packetListeners.add(eventInterceptor);
        _listeners.add(eventInterceptor);

    }
}