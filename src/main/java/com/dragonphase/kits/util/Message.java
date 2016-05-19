package com.dragonphase.kits.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mkremins.fanciful.FancyMessage;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Message {

    public enum MessageType {
        MESSAGE,
        INFO,
        WARNING
    }

    public static String show(String message, MessageType type) {
        return show("", message, type);
    }

    public static String show(String prefix, String message, MessageType type) {
        return ChatColor.YELLOW + (prefix.isEmpty() ? "" : prefix + ": ") + (type == MessageType.MESSAGE ? ChatColor.YELLOW : type == MessageType.INFO ? ChatColor.GOLD : ChatColor.RED) + message;
    }

    // FancyMessage commands

    public static void showMessage(Player player, String title, String... args) {
        if (args.length < 1) {
            player.sendMessage(title);
            return;
        }

        FancyMessage message = new FancyMessage("").then(title).itemTooltip(getMessage(args));

        sendJSONMessage(player, message);
    }

    public static void showCommand(Player player, CommandDescription command) {
        if (command.getArgs().length < 1) {
            player.sendMessage(command.getTitle());
            return;
        }

        FancyMessage message = new FancyMessage("").then(command.getTitle()).itemTooltip(getMessage(command.getArgs())).command(command.getCommand());

        sendJSONMessage(player, message);
    }

    public static void showCommand(Player player, String prefix, CommandDescription... commands) {
        FancyMessage message = new FancyMessage(prefix).color(ChatColor.GOLD);

        List<CommandDescription> commandList = new ArrayList<>(Arrays.asList(commands));

        for (CommandDescription command : commandList) {
            if (command.getArgs().length < 1) {
                player.sendMessage(command.getTitle());
                return;
            }

            message = message.then(command.getTitle()).itemTooltip(getMessage(command.getArgs()));

            if (!command.getCommand().isEmpty()) message = message.command(command.getCommand());

            if (commandList.get(commandList.size() - 1) != command) message = message.then(", ").color(ChatColor.GRAY);

        }

        sendJSONMessage(player, message);
    }

    public static ItemStack getMessage(String... args) {
        ItemStack item = new ItemStack(Material.STONE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + args[0]);

        List<String> lore = new ArrayList<>(Arrays.asList(Utils.trim(args)));
        for (String line : lore)
            lore.set(lore.indexOf(line), ChatColor.RESET + line);

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public static void sendJSONMessage(Player player, FancyMessage message) {
        message.send(player);
    }
}
