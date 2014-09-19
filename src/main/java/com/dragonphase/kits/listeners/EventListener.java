package com.dragonphase.kits.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import com.dragonphase.kits.Kits;
import com.dragonphase.kits.api.Kit;
import com.dragonphase.kits.api.KitException;
import com.dragonphase.kits.permissions.Permissions;
import com.dragonphase.kits.util.Message;
import com.dragonphase.kits.util.Utils;
import com.dragonphase.kits.util.Message.MessageType;

public class EventListener implements Listener {
    public Kits plugin;

    public EventListener(Kits instance) {
        plugin = instance;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getName().toLowerCase().startsWith("new kit: "))
            CreateKit((Player) event.getPlayer(), event.getInventory());

        if (event.getInventory().getName().toLowerCase().startsWith("edit kit: "))
            EditKit((Player) event.getPlayer(), event.getInventory());
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (event.isBlockInHand()) return;
        if (event.getClickedBlock() == null || (event.getClickedBlock().getType() != Material.SIGN_POST && event.getClickedBlock().getType() != Material.WALL_SIGN))
            return;

        Sign sign = (Sign) event.getClickedBlock().getState();

        if (sign.getLines().length < 2) return;
        if (!sign.getLine(0).equalsIgnoreCase("[kit]")) return;
        if (!Permissions.checkPermission(event.getPlayer(), Permissions.KITS_SIGN)) return;

        List<String> lines = new ArrayList<String>(Arrays.asList(StringUtils.join(sign.getLines(), " ").split(" ")));
        lines.removeAll(Arrays.asList("", null));

        String[] arrayLines = Utils.trim(lines.toArray(new String[lines.size()]));

        if (!plugin.getKitManager().kitExists(arrayLines[0])) {
            event.getPlayer().sendMessage(Message.show("", "The kit " + arrayLines[0] + " does not exist.", MessageType.WARNING));
            return;
        }

        if (plugin.getCollectionManager().getDelayedPlayer(event.getPlayer()).playerDelayed(plugin.getKitManager().getKit(arrayLines[0])) && !StringUtils.join(arrayLines).toLowerCase().contains("-delay")) {
            String message = "You are currently delayed for kit " + arrayLines[0] + ". Remaining time:\n "
                    + plugin.getCollectionManager().getDelayedPlayer(event.getPlayer()).getRemainingTime(plugin.getKitManager().getKit(arrayLines[0]));
            event.getPlayer().sendMessage(Message.show("", message, MessageType.WARNING));
            return;
        }

        try {
            plugin.getKitManager().spawnKit(event.getPlayer(), arrayLines[0], Utils.trim(arrayLines));
        } catch (KitException e) {
            plugin.getLogger().warning("The sign at " + Utils.getLocationationAsString(event.getClickedBlock().getLocation()) + " threw an exception: " + e.getMessage());
        }

        event.getPlayer().updateInventory();
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (event.getLine(0).equalsIgnoreCase("[kit]") && !Permissions.checkPermission(event.getPlayer(), Permissions.KITS_ADMIN))
            event.setCancelled(true);
    }

    //Helper methods

    public void CreateKit(Player player, Inventory inventory) {
        String inventoryName = inventory.getName().toLowerCase().replace("new kit: ", "");

        Kit kit = plugin.getKitManager().createKit(inventoryName, inventory.getContents());
        plugin.getCollectionManager().save();
        player.sendMessage(Message.show("Kit " + kit.getName() + " created.", MessageType.INFO));
    }

    public void EditKit(Player player, Inventory inventory) {
        String inventoryName = inventory.getName().toLowerCase().replace("edit kit: ", "");

        String name = Utils.capitalize(inventoryName);

        plugin.getCollectionManager().getKit(name).setItems(inventory.getContents());

        player.sendMessage(Message.show("Kit " + name + " edited.", MessageType.INFO));

    }
}
