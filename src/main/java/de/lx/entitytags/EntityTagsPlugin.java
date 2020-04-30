package de.lx.entitytags;

import com.comphenix.protocol.ProtocolLibrary;

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
import de.lx.entitytags.tags.EntityTagsHolder;

public class EntityTagsPlugin extends JavaPlugin {

    private EntityTagsHolder entityTagsHolder;

    @Override
    public void onEnable() {
        this.entityTagsHolder = new EntityTagsHolder(this, new NMSPacketService(new NMSEntityTypeService()),
                new BukkitEntityService(), new NMSEntityIdRepository(),
                new NMSDataWatcherService(
                        new NMSEntityDataWatcherProducer(this.getServer(), new NMSEntityTypeService())),
                ProtocolLibrary.getProtocolManager());
                
        this.getServer().getPluginManager().registerEvents(this.entityTagsHolder, this);
    }

    @Override
    public void onDisable() {
        try {
            this.entityTagsHolder.close();
        } catch (Exception ex) {
            System.out.println("Error disposing entity tags: " + ex.getMessage());
            ex.printStackTrace();
        }

        HandlerList.unregisterAll(this);
    }

    public EntityTags ofEntity(Entity entity) {
        return this.entityTagsHolder.ofEntity(entity);
    }

    public boolean hasTags(Entity entity) {
        return this.entityTagsHolder.hasTags(entity);
    }
}