package com.dragonphase.kits;

import net.gravitydevelopment.updater.Updater;
import net.gravitydevelopment.updater.Updater.UpdateType;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Plotter;

import com.dragonphase.kits.api.Kit;
import com.dragonphase.kits.api.KitManager;
import com.dragonphase.kits.autosave.AutoSave;
import com.dragonphase.kits.commands.KitCommandExecutor;
import com.dragonphase.kits.commands.KitsCommandExecutor;
import com.dragonphase.kits.configuration.CollectionManager;
import com.dragonphase.kits.configuration.Config;
import com.dragonphase.kits.listeners.EventListener;
import com.dragonphase.kits.util.DelayedPlayer;
import com.dragonphase.kits.util.Time;

public class Kits extends JavaPlugin {

    private static Kits instance;
    
    private Config config;

    private KitManager kitManager;
    private CollectionManager collectionManager;

    @Override
    public void onDisable() {
        //getCollectionManager().save();   // Dont save on unload, doesnt play nice with multi-server 

        // Nullify static references to the plugin

        instance = null;
    }

    @Override
    public void onEnable() {
        registerConfig();
        registerManagers();
        registerEvents();
        registerCommands();
        registerConfigurationSerializables();
        registerMetrics();
        registerAutoSave();
        
        checkForUpdates();
        
        reload();
        
        instance = this;
    }
    
    private void registerConfig() {
        config = new Config(this, "config");
    }
    
    private void registerManagers() {
        kitManager = new KitManager(this);
        collectionManager = new CollectionManager(this);
    }
    
    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }
    
    private void registerCommands() {
        getCommand("kits").setExecutor(new KitsCommandExecutor(this));
        getCommand("kit").setExecutor(new KitCommandExecutor(this));
    }
    
    private void registerConfigurationSerializables() {
        ConfigurationSerialization.registerClass(Kit.class);
        ConfigurationSerialization.registerClass(DelayedPlayer.class);
    }
    
    private void registerMetrics() {
        try {
            Metrics metrics = new Metrics(this);
            
            metrics.createGraph("Kits").addPlotter(new Plotter("Number of kits created") {
                public int getValue() {
                    return getCollectionManager().getKits().size();
                }
            });
            
            metrics.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void registerAutoSave() {
        if (!config.getBoolean("autosave.enabled")) {
            return;
        }
        
        long interval = new Time(config.getString("autosave.interval")).getMilliseconds();
        
        new AutoSave(this).runTaskTimer(this, (interval*20)/1000, (interval*20)/1000);
    }
    
    private void checkForUpdates() {
        if (!config.getBoolean("updater.enabled")) {
            return;
        }
        
        UpdateType type = config.getString("updater.type").equalsIgnoreCase("default")
                ? UpdateType.DEFAULT
                : config.getString("updater.type").equalsIgnoreCase("force")
                ? UpdateType.NO_VERSION_CHECK
                : UpdateType.NO_DOWNLOAD;
        
        boolean silent = !config.getBoolean("updater.silent");
        
        new Updater(this, 51690, this.getFile(), type, silent);
    }

    public void reload() {
        registerConfig();
        getCollectionManager().reload();
    }

    public String getPluginDetails() {
        return getPluginName() + " " + getPluginVersion();
    }

    public String getPluginName() {
        return getDescription().getName();
    }

    public String getPluginVersion() {
        return getDescription().getVersion();
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    public static Kits getInstance() {
        return instance;
    }
}
