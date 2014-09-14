package com.dragonphase.kits.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.dragonphase.kits.Kits;
import com.dragonphase.kits.api.events.KitSpawnEvent;
import com.dragonphase.kits.permissions.Permissions;
import com.dragonphase.kits.util.Message;
import com.dragonphase.kits.util.Utils;
import com.dragonphase.kits.util.Message.MessageType;

public class KitManager {

    private Kits plugin;

    public KitManager(Kits instance) {
        plugin = instance;
    }

    /**
     * Creates a new kit using default values -
     * delay = 0;
     * overwrite = true;
     * announce = true.
     *
     * @param kitName The name of the kit to create.
     * @param contents The contents of the kit.
     * @return The created Kit.
     */
    public Kit createKit(String kitName, ItemStack[] contents) {
        return createKit(kitName, contents, 0, true, true, true);
    }

    /**
     * Creates a new Kit.
     *
     * @param kitName The name of the Kit to create.
     * @param contents The contents of the Kit.
     * @param delay The default delay value of the Kit.
     * @param overwrite The default overwrite value of the Kit.
     * @param announce The default announce value of the Kit.
     * @return The created Kit.
     */
    public Kit createKit(String kitName, ItemStack[] contents, long delay, boolean clear, boolean overwrite, boolean announce) {
        String name = Utils.capitalize(kitName);
        Kit kit = new Kit(name, contents, delay, clear, overwrite, announce);
        plugin.getCollectionManager().addKit(kit);
        return kit;
    }

    /**
     * Edits the Kit with the specified name.
     *
     * @param kitName The name of the Kit to edit.
     * @param contents The contents of the kit to set.
     * @param delay The delay to edit.
     * @param clear The clear to edit.
     * @param overwrite The overwrite to edit.
     * @param announce The announce to edit.
     * @throws KitException If the kit does not exist.
     */
    public void editKit(String kitName, ItemStack[] contents, long delay, boolean clear, boolean overwrite, boolean announce) throws KitException {
        if (!kitExists(kitName)) throw new KitException("The kit " + kitName + " does not exist.");

        editKit(getKit(kitName), contents, delay, clear, overwrite, announce);
    }

    /**
     * Edits the specified Kit.
     *
     * @param kit The Kit to edit.
     * @param contents The contents of the kit to set.
     * @param delay The delay to edit.
     * @param clear The clear to edit.
     * @param overwrite The overwrite to edit.
     * @param announce The announce to edit.
     */
    public void editKit(Kit kit, ItemStack[] contents, long delay, boolean clear, boolean overwrite, boolean announce) {
        kit.setItems(contents);
        editKitDelay(kit, delay);
        editKitClear(kit, clear);
        editKitOverwrite(kit, overwrite);
        editKitAnnounce(kit, announce);
    }
    
    /**
     * Edits the specified Kit's clear value
     * 
     * @param kit The Kit to edit.
     * @param clear The clear to edit.
     */
    public void editKitClear(Kit kit, boolean clear){
        kit.setClear(clear);
    }
    
    /**
     * Edits the specified Kit's overwrite value
     * 
     * @param kit The Kit to edit.
     * @param overwrite The overwrite to edit.
     */
    public void editKitOverwrite(Kit kit, boolean overwrite){
        kit.setOverwrite(overwrite);
    }
    
    /**
     * Edits the specified Kit's announce value
     * 
     * @param kit The Kit to edit.
     * @param announce The announce to edit.
     */
    public void editKitAnnounce(Kit kit, boolean announce){
        kit.setAnnounce(announce);
    }
    
    /**
     * Edits the specified Kit's delay value
     * 
     * @param kit The Kit to edit.
     * @param delay The delay to edit.
     */
    public void editKitDelay(Kit kit, long delay){
        plugin.getCollectionManager().sortDelayedPlayers();
        kit.setDelay(delay);
    }

    /**
     * Gets the Kit with the specified name.
     *
     * @param kitName The name of the Kit to retrieve.
     * @return The Kit value or null if the Kit does not exist.
     */
    public Kit getKit(String kitName) {
        return plugin.getCollectionManager().getKit(Utils.capitalize(kitName.toLowerCase()));
    }

    /**
     * Gets whether the Kit with the specified name exists.
     *
     * @param kitName The Kit to find.
     * @return true if the Kit exists, otherwise false.
     */
    public boolean kitExists(String kitName) {
        return getKit(kitName) != null;
    }

    /**
     * Removes the Kit with the specified name.
     *
     * @param kitName The name of the Kit to remove.
     * @throws KitException If the kit does not exist.
     */
    public void removeKit(String kitName) throws KitException {
        if (kitExists(kitName)) plugin.getCollectionManager().removeKit(getKit(kitName));
        else throw new KitException("The kit " + kitName + " does not exist.");
    }

    /**
     * Spawns the Kit with the specified name for the specified player.
     *
     * @param player The player to spawn the kit for.
     * @param kitName The name of the Kit to spawn.
     * @throws KitException If the kit does not exist.
     */
    public void spawnKit(Player player, String kitName) throws KitException {
        spawnKit(player, kitName, "");
    }

    /**
     * Spawns the Kit with the specified name for the specified player with the specified flags.
     *
     * @param player The player to spawn the kit for.
     * @param kitName The name of the Kit to spawn.
     * @param flags The flags to spawn the Kit with.
     * @throws KitException If the kit does not exist.
     */
    public void spawnKit(Player player, String kitName, String flags) throws KitException {
        if (kitExists(kitName)) spawnKit(player, getKit(kitName), flags);
        else throw new KitException("The kit " + kitName + " does not exist.");
    }

    /**
     * Spawns the Kit with the specified name for the specified player with the specified flags.
     *
     * @param player The player to spawn the kit for.
     * @param kitName The name of the Kit to spawn.
     * @param flags The flags to spawn the Kit with.
     * @throws KitException If the kit does not exist.
     */
    public void spawnKit(Player player, String kitName, String... flags) throws KitException {
        if (kitExists(kitName)) spawnKit(player, getKit(kitName), flags);
        else throw new KitException("The kit " + kitName + " does not exist.");
    }

    /**
     * Spawns the Kit with the specified name for the specified player with the specified flags.
     *
     * @param player The player to spawn the kit for.
     * @param kitName The name of the Kit to spawn.
     * @param flags The flags to spawn the Kit with.
     * @throws KitException If the kit does not exist.
     */
    public void spawnKit(Player player, String kitName, HashMap<String, Boolean> flags) throws KitException {
        if (kitExists(kitName)) spawnKit(player, getKit(kitName), flags);
        else throw new KitException("The kit " + kitName + " does not exist.");
    }

    /**
     * Spawns the Kit with the specified name for the specified player with the specified flags.
     *
     * @param player The player to spawn the kit for.
     * @param kit The Kit to spawn.
     * @param flags The flags to spawn the Kit with.
     */
    public void spawnKit(Player player, Kit kit, String flags) {
        spawnKit(player, kit, flags.split(" "));
    }

    /**
     * Spawns the Kit with the specified name for the specified player with the specified flags.
     *
     * @param player The player to spawn the kit for.
     * @param kit The Kit to spawn.
     * @param flags The flags to spawn the Kit with.
     */
    public void spawnKit(Player player, Kit kit, String... flags) {
        List<String> flagList = Arrays.asList(flags);
        HashMap<String, Boolean> flagMap = new HashMap<>();

        for (String flag : flagList) {
            if (flag.isEmpty() || flag.length() < 2) continue;
            flagMap.put(flag.replace("+", "").replace("-", ""), !flag.startsWith("-"));
        }

        spawnKit(player, kit, flagMap);
    }

    /**
     * Spawns the Kit with the specified name for the specified player with the specified flags.
     *
     * @param player The player to spawn the kit for.
     * @param kit The Kit to spawn.
     * @param flags The flags to spawn the Kit with.
     */
    public void spawnKit(Player player, Kit kit, HashMap<String, Boolean> flags) {
        long delay = Permissions.hasPermission(player, Permissions.KITS_NODELAY, kit.getName()) ? 0 : kit.getDelay();
        boolean clear = kit.getClear();
        boolean overwrite = kit.getOverwrite();
        boolean announce = kit.getAnnounce();

        for (String flag : flags.keySet()) {
            switch (flag) {
                case "overwrite":
                    overwrite = flags.get(flag);
                    break;
                case "announce":
                    announce = flags.get(flag);
                    break;
                case "delay":
                    delay = flags.get(flag) ? delay : 0;
                    break;
                case "clear":
                    clear = flags.get(flag);
                    break;
            }
        }

        spawnKit(player, kit, delay, clear, overwrite, announce);
    }

    /**
     * Spawns the Kit with the specified name for the specified player with the specified flags.
     *
     * @param player The player to spawn the kit for.
     * @param kitName The name of the Kit to spawn.
     * @param delay The delay flag to spawn the Kit with.
     * @param overwrite The overwrite flag to spawn the Kit with.
     * @param announce The announce flag to spawn the Kit with.
     * @throws KitException If the kit does not exist.
     */
    public void spawnKit(Player player, String kitName, long delay, boolean clear, boolean overwrite, boolean announce) throws KitException {
        if (kitExists(kitName)) spawnKit(player, getKit(kitName), delay, clear, overwrite, announce);
        else throw new KitException("The kit " + kitName + " does not exist.");
    }

    /**
     * Spawns the Kit for the specified player with the specified flags.
     *
     * @param player The player to spawn the kit for.
     * @param kit The Kit to spawn.
     * @param delay The delay flag to spawn the Kit with.
     * @param overwrite The overwrite flag to spawn the Kit with.
     * @param announce The announce flag to spawn the Kit with.
     */
    public void spawnKit(Player player, Kit kit, long delay, boolean clear, boolean overwrite, boolean announce) {
        
        KitSpawnEvent event = new KitSpawnEvent(kit, player, clear, overwrite, announce, delay);
        
        Bukkit.getServer().getPluginManager().callEvent(event);
        
        if (event.isCancelled()) {
            return;
        }
        
        List<ItemStack> items = new ArrayList<ItemStack>(Arrays.asList(event.getKit().getItems()));
        java.util.Collections.replaceAll(items, null, new ItemStack(Material.AIR));

        ItemStack[] armor = new ItemStack[]{items.remove(0), items.remove(0), items.remove(0), items.remove(0)};
        ArrayUtils.reverse(armor);

        for (int i = 0; i < 5; i++)
            items.remove(0);

        if (event.getOverwrite()) {
            if (event.getClear()) {
                player.getInventory().clear();
                player.getInventory().setArmorContents(armor);
            } else {
                for (int i = 0; i < armor.length; i ++) {
                    if (armor[i] == null || armor[i].getType() == Material.AIR) continue;
                    
                    player.getInventory().setItem(player.getInventory().getSize() + i, armor[i]);
                }
            }
            

            for (int i = 0; i < items.size(); i++) {
                if (player.getInventory().getItem(i + 9 < 36 ? i + 9 : i - 27) != null
                        && items.get(i).getType() == Material.AIR) continue;
                player.getInventory().setItem(i + 9 < 36 ? i + 9 : i - 27, items.get(i));
            }
        } else {
            player.getInventory().addItem(items.toArray(new ItemStack[items.size()]));
            
            for (int i = 0; i < armor.length; i ++) {
                if (player.getInventory().getArmorContents()[i] == null || player.getInventory().getArmorContents()[i].getType() == Material.AIR)
                    player.getInventory().setItem(player.getInventory().getSize() + i, armor[i]);
            }
        }

        if (event.getDelay() > 0)
            plugin.getCollectionManager().getDelayedPlayer(player).addKit(kit, event.getDelay() - kit.getDelay());

        if (event.getAnnounce()) {
            player.sendMessage(Message.show("Kit " + kit.getName() + " spawned.", MessageType.INFO));
        }

    }
}
