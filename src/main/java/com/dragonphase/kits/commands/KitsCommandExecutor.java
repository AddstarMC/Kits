package com.dragonphase.kits.commands;

import com.dragonphase.kits.Kits;
import com.dragonphase.kits.api.Kit;
import com.dragonphase.kits.permissions.Permissions;
import com.dragonphase.kits.util.CommandDescription;
import com.dragonphase.kits.util.Message;
import com.dragonphase.kits.util.Message.MessageType;
import com.dragonphase.kits.util.Time;
import com.dragonphase.kits.util.Utils;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitsCommandExecutor implements CommandExecutor {
    private final Kits plugin;

    public KitsCommandExecutor(Kits instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {

        if (args.length < 1) {
            handleBaseCommand(sender);
            return false;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            handleReload(sender);
        }

        return false;
    }

    private void handleBaseCommand(CommandSender sender) {
        if (sender instanceof Player && !Permissions.checkPermission((Player) sender, Permissions.KITS_LIST)) {
            return;
        }

        if (plugin.getCollectionManager().getKits().size() < 1) {
            Message.showMessage(sender,Message.show("There are no available kits.", MessageType.WARNING));
            return;
        }

        if (!(sender instanceof Player)) {
            TextComponent.Builder kitName = TextComponent.builder().content("Available Kits: ");
            int i = 0;
            int total = plugin.getCollectionManager().getKits().size();
            for (Kit kit : plugin.getCollectionManager().getKits()) {
                i++;
                kitName = kitName.append(TextComponent.of(kit.getName()).color(NamedTextColor.DARK_AQUA));
                if(i < total) {
                    kitName = kitName.append(TextComponent.of(", "));
                }
            }
            Message.showMessage(sender,kitName.build());
            return;
        }
        
        Player player = (Player) sender;

        List<CommandDescription> commands = new ArrayList<>();

        for (Kit kit : plugin.getCollectionManager().getKits()) {
            if (!Permissions.hasPermission(player, Permissions.KITS_SPAWN, kit.getName())) continue;
            
            boolean delayed = plugin.getCollectionManager().getDelayedPlayer(player).playerDelayed(kit);
            
            List<TextComponent> items = new ArrayList<>();
            
            for (ItemStack item : kit.getItems()) {
                if (item == null) continue;
                items.add(TextComponent.of(item.getAmount() + " x " + (
                      (item.hasItemMeta() && item.getItemMeta() != null) ?
                            item.getItemMeta().getDisplayName() :
                            Utils.capitalize(item.getType().name()))));
            }
            TextComponent.Builder displayTextBuilder = TextComponent.builder()
                  .append(TextComponent.of("Number of items: ").color(NamedTextColor.GOLD)).append(TextComponent.of(items.size())).append(TextComponent.newline())
                  .append(TextComponent.of("Delay: ").color(NamedTextColor.GOLD)).append(TextComponent.of(kit.getDelay() <= 0 ? "no delay" : Time.fromMilliseconds(kit.getDelay()).toReadableFormat(false))).append(TextComponent.newline())
                  .append(TextComponent.of("Clear: ").color(NamedTextColor.GOLD)).append(TextComponent.of(kit.getClear())).append(TextComponent.newline())
                  .append(TextComponent.of("Overwrite: ").color(NamedTextColor.GOLD)).append(TextComponent.of(kit.getOverwrite())).append(TextComponent.newline())
                  .append(TextComponent.of("Announce: ").color(NamedTextColor.GOLD)).append(TextComponent.of(kit.getAnnounce())).append(TextComponent.newline());
            if (delayed) {
                displayTextBuilder
                      .append(TextComponent.of("Remaining time: ").color(NamedTextColor.GOLD))
                      .append(TextComponent.of(plugin.getCollectionManager().getDelayedPlayer(player).getRemainingTime(kit))
                            .color(NamedTextColor.RED))
                      .append(TextComponent.newline());
            }
            TextComponent displayText = displayTextBuilder.build();
            TextComponent.Builder titleBuilder = TextComponent.builder();
            titleBuilder.content(kit.getName());
            String command;
            if (delayed) {
                titleBuilder.color(NamedTextColor.RED).decoration(TextDecoration.STRIKETHROUGH, TextDecoration.State.TRUE);
                command = "";
            } else {
                titleBuilder.color(NamedTextColor.GOLD);
                command = "/kit "+kit.getName();
            }
            commands.add(new CommandDescription(titleBuilder.build(),command, displayText));
        }
        Message.showMessage(player,TextComponent.of("Kits available to you:").color(NamedTextColor.GOLD));
        Message.showCommand(player, " ", commands.toArray(new CommandDescription[0]));
    }

    private void handleReload(CommandSender sender) {
        if (sender instanceof Player && !Permissions.checkPermission((Player) sender, Permissions.KITS_ADMIN)) {
            return;
        }
        
        plugin.reload();
        Message.showMessage(sender,Message.show("Reloaded configurations.", MessageType.INFO));
    }
}
