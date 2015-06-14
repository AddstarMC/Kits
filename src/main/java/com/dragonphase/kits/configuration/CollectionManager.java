package com.dragonphase.kits.configuration;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.dragonphase.kits.Kits;
import com.dragonphase.kits.api.Kit;
import com.dragonphase.kits.util.DelayedPlayer;

public class CollectionManager {
    
    private Kits plugin;

    public Config kitConfig;
    public List<Kit> kits;

    public Config playerConfig;
    public List<DelayedPlayer> delayedPlayers;

    public CollectionManager(Kits instance) {
        plugin = instance;
        
        kits = new ArrayList<Kit>();
        delayedPlayers = new ArrayList<DelayedPlayer>();
    }

    public void save() {
        kitConfig.set("kits", kits);
        kitConfig.save();
        
        sortDelayedPlayers();

        playerConfig.set("players", delayedPlayers);
        playerConfig.save();
    }

    @SuppressWarnings("unchecked")
    public void load() {
        kitConfig = new Config(plugin, "kits");
        migrateOldKitsFile();
        kits = kitConfig.get("kits") == null ? new ArrayList<Kit>() : (List<Kit>) kitConfig.get("kits");

        playerConfig = new Config(plugin, "players");
        delayedPlayers = playerConfig.get("players") == null ? new ArrayList<DelayedPlayer>() : (List<DelayedPlayer>) playerConfig.getList("players");
    }

    public void reload() {
        //if (kitConfig != null && playerConfig != null) save();  // Dont save on unload, doesnt play nice with multi-server
        load();
    }

    public Kit getKit(String name) {
        for (Kit kit : kits) {
            if (kit.getName().equalsIgnoreCase(name)) return kit;
        }
        return null;
    }

    public void addKit(Kit kit) {
        kits.add(kit);
        save();
    }

    public void removeKit(Kit kit) {
        kits.remove(kit);
    }

    public DelayedPlayer getDelayedPlayer(Player player) {
        for (DelayedPlayer delayedPlayer : delayedPlayers) {
            try {
                if (delayedPlayer.getUniqueId().equals(player.getUniqueId())) return delayedPlayer;
            } catch (Exception ignored) {
            }
        }
        DelayedPlayer delayedPlayer = new DelayedPlayer(player);
        delayedPlayers.add(delayedPlayer);
        return delayedPlayer;
    }

    public List<Kit> getKits() {
        return kits;
    }

    public List<DelayedPlayer> getDelayedPlayers() {
        return delayedPlayers;
    }
    
    public void sortDelayedPlayers() {
        for (DelayedPlayer player : delayedPlayers)
            player.sortKits(this);
    }
    
    //Migrate kits from pre-Kits 1.7. Resulting inventories are somewhat buggy but can easily be edited ingame.

    @SuppressWarnings("unchecked")
    private void migrateOldKitsFile() {
        if (kitConfig.contains("kits")) return;

        List<Kit> newKits = new ArrayList<Kit>();

        for (String key : kitConfig.getKeys(false)) {
            ItemStack[] items = ((ArrayList<ItemStack>) kitConfig.get(key + ".kit")).toArray(new ItemStack[((ArrayList<ItemStack>) kitConfig.get(key + ".kit")).size()]);
            ArrayUtils.reverse(items);
            newKits.add(new Kit(key, items, kitConfig.getLong(key + ".delay"), true, kitConfig.getBoolean(key + ".overwrite"), true));
        }

        for (Kit kit : newKits)
            kitConfig.set(kit.getName(), null);

        kitConfig.set("kits", newKits);
    }
}
