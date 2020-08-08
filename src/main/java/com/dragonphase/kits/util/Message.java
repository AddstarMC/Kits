package com.dragonphase.kits.util;

import com.dragonphase.kits.Kits;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static void showMessage(Player player, String title, String... args) {
        if (args.length < 1) {
            player.sendMessage(title);
            return;
        }
        TextComponent.Builder builder = TextComponent.builder();
        for (String arg : args) {
            builder.content(arg).append(TextComponent.newline());
        }
        TextComponent showText = builder.build();
        TextComponent component = TextComponent.builder()
              .content(title)
              .hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, showText))
              .build();
        Kits.audiences.audience(player).sendMessage(component);
    }

    public static TextComponent showCommand(Player player, CommandDescription command) {
        if (command.getArgs().length < 1) {
            return LegacyComponentSerializer.legacySection().deserialize(command.getTitle());
        }
        TextComponent.Builder builder = TextComponent.builder();
        for (String arg : command.getArgs()) {
            builder
                  .append(LegacyComponentSerializer.legacy(LegacyComponentSerializer.SECTION_CHAR).deserialize(arg))
                  .append(TextComponent.newline());
        }
        TextComponent showText = builder.build();
        return TextComponent.builder()
              .content(command.getTitle())
              .hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, showText))
              .clickEvent(ClickEvent.runCommand(command.getCommand()))
              .build();
    }

    public static void showCommand(Player player, String prefix, CommandDescription... commands) {
        TextComponent component = LegacyComponentSerializer.legacySection().deserialize(prefix);
        List<CommandDescription> commandList = new ArrayList<>(Arrays.asList(commands));
        int total = commandList.size();
        int i = 0;
        for (CommandDescription command : commandList) {
            i++;
            component.append(showCommand(player, command));
            if (i < total) {
                component.append(TextComponent.of(", "));
            }

        }
        sendMessage(player, component);
    }

    public static void sendMessage(Player player, TextComponent component) {
        Kits.audiences.audience(player).sendMessage(component);
    }
    //public static void sendJSONMessage(Player player, FancyMessage message) {
    // message.send(player);
    // }
}
