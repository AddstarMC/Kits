package com.dragonphase.kits.configuration;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.org.apache.commons.lang3.ArrayUtils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.dragonphase.kits.Kits;
import com.dragonphase.kits.api.Kit;
import com.dragonphase.kits.util.DelayedPlayer;

public class CollectionManager {
    
	public Config kitConfig;
	public List<Kit> kitList;
	
	public Config playerConfig;
	public List<DelayedPlayer> playerList;
	
	public CollectionManager(){
	    kitList = new ArrayList<Kit>();
	    playerList = new ArrayList<DelayedPlayer>();
	}
	
	public void save(){
		kitConfig.set("kits", kitList);
		kitConfig.save();
		
		for (DelayedPlayer player : playerList)
			player.sortKits(this);
		
		playerConfig.set("players", playerList);
		playerConfig.save();
	}
	
	@SuppressWarnings("unchecked")
	public void load(Kits plugin){
		kitConfig = new Config(plugin, "kits");
		migrateOldKitsFile();
		kitList = kitConfig.get("kits") == null ? new ArrayList<Kit>() : (List<Kit>) kitConfig.get("kits");
		
		playerConfig = new Config(plugin, "players");
		playerList = playerConfig.get("players") == null ? new ArrayList<DelayedPlayer>() : (List<DelayedPlayer>) playerConfig.getList("players");
	}
	
	@SuppressWarnings("unchecked")
    private void migrateOldKitsFile(){
	    if (kitConfig.contains("kits")) return;
	    
	    List<Kit> newKits = new ArrayList<Kit>();
	    
	    for (String key : kitConfig.getKeys(false)){
	        ItemStack[] items = ((ArrayList<ItemStack>)kitConfig.get(key + ".kit")).toArray(new ItemStack[((ArrayList<ItemStack>)kitConfig.get(key + ".kit")).size()]);
	        ArrayUtils.reverse(items);
	        newKits.add(new Kit(key, items, kitConfig.getLong(key + ".delay"), kitConfig.getBoolean(key + ".overwrite"), true));
	    }
	    
	    for (Kit kit : newKits)
	        kitConfig.set(kit.getName(), null);
	    
	    kitConfig.set("kits", newKits);
	}
	
	public void reload(Kits plugin){
		if (kitConfig != null && playerConfig != null) save();
		load(plugin);
	}
	
	public Kit getKit(String name){
		for (Kit kit : kitList){
			if (kit.getName().equalsIgnoreCase(name)) return kit;
		}
		return null;
	}
	
	public void addKit(Kit kit){
	    kitList.add(kit);
	}
	
	public void removeKit(Kit kit){
	    kitList.remove(kit);
	}
	
	public DelayedPlayer getDelayedPlayer(Player player){
		for (DelayedPlayer delayedPlayer : playerList){
		    try{
	            if (delayedPlayer.getPlayer().getUniqueId().equals(player.getUniqueId())) return delayedPlayer;
		    }catch (Exception ex){ }
		}
		DelayedPlayer delayedPlayer = new DelayedPlayer(player);
		playerList.add(delayedPlayer);
		return delayedPlayer;
	}
	
	public List<Kit> getKitList(){
	    return kitList;
	}
	
	public List<DelayedPlayer> getDelayedPlayers(){
	    return playerList;
	}
}
