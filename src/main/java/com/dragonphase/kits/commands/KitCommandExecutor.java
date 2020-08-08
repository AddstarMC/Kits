package com.dragonphase.kits.commands;

import com.dragonphase.kits.Kits;
import com.dragonphase.kits.api.Kit;
import com.dragonphase.kits.api.KitException;
import com.dragonphase.kits.permissions.Permissions;
import com.dragonphase.kits.util.FlagType;
import com.dragonphase.kits.util.Message;
import com.dragonphase.kits.util.Message.MessageType;
import com.dragonphase.kits.util.Time;
import com.dragonphase.kits.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitCommandExecutor implements CommandExecutor {
    private final Kits plugin;

    public KitCommandExecutor(Kits instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {

        if (args.length < 1) {
            handleBaseCommand(sender);
            return false;
        }

        if (args[0].equalsIgnoreCase("create")) {
            createKit(sender, Utils.trim(args));
        } else if (args[0].equalsIgnoreCase("edit")) {
            editKit(sender, Utils.trim(args));
        } else if (args[0].equalsIgnoreCase("remove")) {
            removeKit(sender, Utils.trim(args));
        } else {
            spawnKit(sender, args);
        }

        return false;
    }

    private void handleBaseCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Message.sendMessage(sender, Message.show("", "Available commands:", MessageType.MESSAGE));
            Message.showMessage(sender, Message.show("", "  /kits", MessageType.INFO), "List the kits available to you.");
            Message.showMessage(sender, Message.show("", "  /kit " + ChatColor.ITALIC + "kitname", MessageType.INFO), "Spawn the kit with the specified name.");
            if (sender.hasPermission(Permissions.KITS_ADMIN)) {
                Message.showMessage(sender, Message.show("", "  /kit " + ChatColor.ITALIC + "kitname [flags]", MessageType.INFO), "Spawn the kit with the specified name.", "With the specified flags. Example:", "/kit basic -announce");
                Message.showMessage( sender, Message.show("", "  /kit " + ChatColor.ITALIC + "kitname playername", MessageType.INFO), "Spawn the kit with the specified name.", "For the player with the specified name.");
                Message.showMessage(sender, Message.show("", "  /kit " + ChatColor.ITALIC + "kitname playername [flags]", MessageType.INFO), "Spawn the kit with the specified name.", "For the player with the specified name.", "With the specified flags. Example:", "/kit basic bob -announce");
            	Message.showMessage(sender, Message.show("", "  /kit create " + ChatColor.ITALIC + "kitname", MessageType.INFO), "Create a new kit with the specific name.");
            	Message.showMessage(sender, Message.show("", "  /kit edit " + ChatColor.ITALIC + "kitname", MessageType.INFO), "Edit an existing kit with the specific name.");
            	Message.showMessage(sender, Message.show("", "  /kit edit " + ChatColor.ITALIC + "kitname [flagname] <flagvalue>", MessageType.INFO), "Edit an existing kit's flags with the specific names.");
            	Message.showMessage(sender, Message.show("", "  /kit remove " + ChatColor.ITALIC + "kitname", MessageType.INFO), "Remove an existing kit with the specific name.");
            }
        } else {
            Message.sendMessage(sender,Message.show("", "Available commands:", MessageType.MESSAGE));
            Message.sendMessage(sender,Message.show("", "/kits", MessageType.INFO));
            Message.sendMessage(sender,Message.show("", "/kit create " + ChatColor.ITALIC + "kitname", MessageType.INFO));
            Message.sendMessage(sender,Message.show("", "/kit " + ChatColor.ITALIC + "kitname", MessageType.INFO));
            Message.sendMessage(sender,Message.show("", "/kit " + ChatColor.ITALIC + "kitname [flags]", MessageType.INFO));
            Message.sendMessage(sender,Message.show("", "/kit " + ChatColor.ITALIC + "kitname playername", MessageType.INFO));
            Message.sendMessage(sender,Message.show("", "/kit " + ChatColor.ITALIC + "kitname playername [flags]", MessageType.INFO));
            Message.sendMessage(sender,Message.show("", "/kit create " + ChatColor.ITALIC + "kitname", MessageType.INFO));
            Message.sendMessage(sender,Message.show("", "/kit edit " + ChatColor.ITALIC + "kitname", MessageType.INFO));
            Message.sendMessage(sender,Message.show("", "/kit edit " + ChatColor.ITALIC + "kitname [flagname] <flagvalue>", MessageType.INFO));
            Message.sendMessage(sender,Message.show("", "/kit remove " + ChatColor.ITALIC + "kitname", MessageType.INFO));
        }
    }

    // Create Kit

    private void createKit(CommandSender sender, String[] args) {
        if (notPlayer(sender)) return;

        Player player = (Player) sender;

        if (!Permissions.checkPermission(player, Permissions.KITS_ADMIN)) return;

        if (args.length < 1) {
            Message.showMessage(player, Message.show("Usage", "/kit create " + ChatColor.ITALIC + "kitname", MessageType.INFO), "Create a new kit with the specific name.");
            return;
        }

        if (args.length > 1) {
            Message.sendMessage(sender,Message.show("", "Kit names cannot contain spaces.", MessageType.WARNING));
            return;
        }

        if (plugin.getKitManager().kitExists(args[0])) {
            Message.sendMessage(sender,Message.show("", "Kit " + args[0] + " already exists.", MessageType.WARNING));
            return;
        }

        if (args[0].length() > 22) {
            Message.sendMessage(sender,Message.show("", "Kit name cannot exceed 22 characters.", MessageType.WARNING));
            return;
        }

        Inventory inventory = Bukkit.createInventory(player, 45, "New kit: " + args[0]);
        player.openInventory(inventory);
    }

    // Edit Kit

    private void editKit(CommandSender sender, String[] args) {
        if (notPlayer(sender)) return;

        Player player = (Player) sender;

        if (!Permissions.checkPermission(player, Permissions.KITS_ADMIN)) return;

        if (args.length < 1) {
            Message.showMessage(player, Message.show("Usage", "/kit edit " + ChatColor.ITALIC + "kitname", MessageType.INFO), "Edit an existing kit with the specific name.");
            Message.showMessage(player, Message.show("Usage", "/kit edit " + ChatColor.ITALIC + "kitname flagname", MessageType.INFO), "Edit an existing kit's flags with the specific names.");
            return;
        }

        if (!plugin.getKitManager().kitExists(args[0])) {
            Message.showMessage(player,Message.show("", "Kit " + args[0] + " does not exist.", MessageType.WARNING));
            return;
        }

        if (args.length > 1) {
            editKitFlags(player, plugin.getKitManager().getKit(args[0]), Utils.trim(args));
            return;
        }

        Inventory inventory = Bukkit.createInventory(player, 45, "Edit kit: " + args[0]);

        inventory.setContents(plugin.getKitManager().getKit(args[0]).getItems());

        player.openInventory(inventory);
    }

    public void editKitFlags(Player player, Kit kit, String[] args) {
        if (args[0].equalsIgnoreCase("overwrite")) {
            editKitOverwrite(player, kit, Utils.trim(args));
            return;
        }

        if (args[0].equalsIgnoreCase("announce")) {
            editKitAnnounce(player, kit, Utils.trim(args));
            return;
        }

        if (args[0].equalsIgnoreCase("clear")) {
            editKitClear(player, kit, Utils.trim(args));
        }

        if (args[0].equalsIgnoreCase("delay")) {
            editKitDelay(player, kit, Utils.trim(args));
        }
    }

    public void editKitOverwrite(Player player, Kit kit, String[] args) {
        if (args.length < 1) {
            Message.showMessage(player, Message.show("Usage", "/kit edit " + ChatColor.ITALIC + kit.getName() + " overwrite [true|false]", MessageType.INFO), "Change the overwrite flag of " + kit.getName() + " to true or false.", "Current overwrite value: " + kit.getOverwrite() + ".");
            return;
        }

        boolean value = args[0].equalsIgnoreCase("true");
        plugin.getKitManager().editKit(kit, kit.getItems(), kit.getDelay(), kit.getClear(), value, kit.getAnnounce());
        Message.showMessage(player,Message.show("", "Overwrite for kit " + kit.getName() + " set to " + value, MessageType.INFO));
    }

    public void editKitAnnounce(Player player, Kit kit, String[] args) {
        if (args.length < 1) {
            Message.showMessage(player, Message.show("Usage", "/kit edit " + ChatColor.ITALIC + kit.getName() + " announce [true|false]", MessageType.INFO), "Change the announce flag of " + kit.getName() + " to true or false.", "Current announce value: " + kit.getAnnounce() + ".");
            return;
        }

        boolean value = args[0].equalsIgnoreCase("true");
        plugin.getKitManager().editKit(kit, kit.getItems(), kit.getDelay(), kit.getClear(), kit.getOverwrite(), value);
        Message.showMessage(player,Message.show("", "Announce for kit " + kit.getName() + " set to " + value, MessageType.INFO));
    }

    public void editKitClear(Player player, Kit kit, String[] args) {
        if (args.length < 1) {
            Message.showMessage(player, Message.show("Usage", "/kit edit " + ChatColor.ITALIC + kit.getName() + " clear [true|false]", MessageType.INFO), "Change the clear flag of " + kit.getName() + " to true or false.", "Current clear value: " + kit.getClear() + ".");
            return;
        }

        boolean value = args[0].equalsIgnoreCase("true");
        plugin.getKitManager().editKit(kit, kit.getItems(), kit.getDelay(), value, kit.getOverwrite(), kit.getAnnounce());
        Message.showMessage(player, Message.show("", "Clear for kit " + kit.getName() + " set to " + value, MessageType.INFO));
    }

    public void editKitDelay(Player player, Kit kit, String[] args) {
        if (args.length < 1) {
            Message.showMessage(player, Message.show("Usage", "/kit edit " + ChatColor.ITALIC + kit.getName() + " delay [delay]", MessageType.INFO), "Change the delay flag of " + kit.getName() + ".", "Example: 1h30m for 1 hour 30 minute delay.");
            return;
        }

        try {
            Time value = Time.fromExpression(StringUtils.join(args, " "));
            plugin.getKitManager().editKit(kit, kit.getItems(), value.getTotalMilliseconds(), kit.getClear(), kit.getOverwrite(), kit.getAnnounce());
            Message.sendMessage(player,Message.show("", "Delay for kit " + kit.getName() + " set to " + StringUtils.join(args, " "), MessageType.INFO));
        } catch (Exception ex) {
            Message.sendMessage(player,Message.show("", "Incorrect delay format. Example: 1h30m for 1 hour 30 minute delay.", MessageType.WARNING));
        }
    }

    // Remove Kit

    private void removeKit(CommandSender sender, String[] args) {
        if (notPlayer(sender)) return;

        Player player = (Player) sender;

        if (!Permissions.checkPermission(player, Permissions.KITS_ADMIN)) return;

        if (args.length < 1) {
            Message.showMessage(player, Message.show("Usage", "/kit remove " + ChatColor.ITALIC + "kitname", MessageType.INFO), "Remove an existing kit with the specific name.");
            return;
        }

        try {
            plugin.getKitManager().removeKit(args[0]);
            Message.sendMessage(player,Message.show("", "Kit " + args[0] + " removed.", MessageType.INFO));
        } catch (KitException e) {
            Message.sendMessage(player,Message.show("", "Kit " + args[0] + " does not exist.", MessageType.WARNING));
        }
    }

    // Spawn Kit

    public void spawnKit(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            spawnKit((Player) sender, args);
            return;
        }

        if (args.length < 2) {
            Message.sendMessage(sender,Message.show("Usage", "kit " + ChatColor.ITALIC + "kitname playername", MessageType.WARNING));
            return;
        }

        if (spawnKit(sender, args[0], args[1], StringUtils.join(Utils.trim(Utils.trim(args)), " "))) {
            Message.showMessage(sender,Message.show("", "Kit " + args[0] + " spawned for " + args[1] + ".", MessageType.INFO));
        }
    }

    private void spawnKit(Player player, String[] args) {
        if (args.length == 0) {
            Message.showMessage(player, Message.show("Usage", "/kit " + ChatColor.ITALIC + "kitname", MessageType.WARNING), "Spawn the specified kit.");
            Message.showMessage(player, Message.show("Usage", "/kit " + ChatColor.ITALIC + "kitname playername", MessageType.WARNING), "Spawn the specified kit for the specified player.");
            return;
        }

        if (!Permissions.checkPermission(player, Permissions.KITS_SPAWN + "." + args[0].toLowerCase())) return;

        if (args.length > 1 && (!args[1].startsWith("+") && !args[1].startsWith("-"))) {
            if (!Permissions.checkPermission(player, Permissions.KITS_SPAWN_OTHERS + "." + args[0].toLowerCase())) return;

            if (spawnKit(player, args[0], args[1], StringUtils.join(Utils.trim(Utils.trim(args)), " "))) {
                Message.sendMessage(player,Message.show("", "Kit " + args[0] + " spawned for " + args[1] + ".", MessageType.INFO));
            }
            return;
        }

        spawnKit(player, args[0], player.getName(), StringUtils.join(Utils.trim(args), " "));
    }

    private boolean spawnKit(CommandSender sender, String kitName, String playerName, String flags) {
        Player player = getPlayer(playerName);

        if (player == null) {
            Message.sendMessage(sender,Message.show("", playerName + " is not online. Make sure the name is typed correctly.", MessageType.WARNING));
            return false;
        }

        if (!plugin.getKitManager().kitExists(kitName)) {
            Message.sendMessage(sender,Message.show("", "Kit " + kitName + " does not exist. Make sure the name is typed correctly.", MessageType.WARNING));
            return false;
        }

        return spawnKit(sender, player, plugin.getKitManager().getKit(kitName), flags);
    }

    private boolean spawnKit(CommandSender sender, Player player, Kit kit, String flags) {
        List<String> flagList = StringUtils.isEmpty(flags) ? new ArrayList<>() : Arrays.asList(flags.split(" "));
        Map<String, Boolean> Flags = new HashMap<>();

        for (String flag : flagList) {
            if (flag.isEmpty() || flag.length() < 2) continue;
            if (sender instanceof Player && !Permissions.hasPermission((Player) sender, Permissions.KITS_FLAGS, flag.replace("+", "").replace("-", ""))) {
                Message.sendMessage(sender,Message.show("", "You do not have permission to use the " + flag.replace("+", "").replace("-", "") + " flag.", MessageType.WARNING));
                continue;
            }
            Flags.put(flag.replace("+", "").replace("-", ""), !flag.startsWith("-"));
        }

        return spawnKit(sender, player, kit, Flags);
    }

    private boolean spawnKit(CommandSender sender, Player player, Kit kit, Map<String, Boolean> flags) {
        long delay = Permissions.hasPermission(player, Permissions.KITS_NODELAY, kit.getName()) ? 0 : kit.getDelay();
        boolean clear = kit.getClear();
        boolean overwrite = kit.getOverwrite();
        boolean announce = kit.getAnnounce();

        for (String flag : flags.keySet()) {
            switch (FlagType.match(flag)) {
                case OVERWRITE:
                    overwrite = flags.get(flag);
                    break;
                case ANNOUNCE:
                    announce = flags.get(flag);
                    break;
                case DELAY:
                    delay = flags.get(flag) ? delay : 0;
                    break;
                case CLEAR:
                    clear = flags.get(flag);
            }
        }

        return spawnKit(sender, player, kit, delay, clear, overwrite, announce);
    }

    private boolean spawnKit(CommandSender sender, Player player, Kit kit, long delay, boolean clear, boolean overwrite, boolean announce) {

        if (plugin.getCollectionManager().getDelayedPlayer(player).playerDelayed(kit) && kit.getDelay() == delay && delay > 0) {
            if (announce) {
                String message = (sender instanceof Player && sender.getName().equalsIgnoreCase(player.getName()) ? "You are " : player.getName() + " is ") + "currently delayed for kit " + kit.getName() + ". Remaining time:\n " + plugin.getCollectionManager().getDelayedPlayer(player).getRemainingTime(kit);
                Message.sendMessage(sender,Message.show("", message, MessageType.WARNING));
            }
            return false;
        }

        plugin.getKitManager().spawnKit(player, kit, delay, clear, overwrite, announce);

        return true;
    }

    // Helper methods

    private boolean notPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return false;
        }
        Message.sendMessage(sender,Message.show("", "Command must be issued ingame.", MessageType.WARNING));
        return true;
    }

    private Player getPlayer(String name) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) return player.getPlayer();
        }
        return null;
    }
}
