package com.dragonphase.kits.util;

import com.dragonphase.kits.Kits;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
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

    public static TextComponent show(String message, MessageType type) {
        return show("", message, type);
    }

    public static TextComponent show(String prefix, String message, MessageType type) {
        NamedTextColor color;
        switch (type){
            case INFO:
                color = NamedTextColor.GOLD;
                break;
            case MESSAGE:
            default:
                color = NamedTextColor.YELLOW;
                break;
            case WARNING:
                color = NamedTextColor.RED;
                      break;
        }
        return TextComponent.builder()
              .content(prefix)
              .color(color)
              .append(LegacyComponentSerializer.legacySection().deserialize(message))
              .build();
    }
    public static void showMessage(CommandSender player, TextComponent title, String... args) {
        if (args.length < 1) {
            sendMessage(player,title);
            return;
        }
        TextComponent.Builder builder = TextComponent.builder();
        for (String arg : args) {
            builder = builder.content(arg).append(TextComponent.newline());
        }
        TextComponent showText = builder.build();
        TextComponent component = title
              .hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, showText));
        Kits.audiences.audience(player).sendMessage(component);
    }
    public static void showMessage(CommandSender player, String title, String... args) {
        if (args.length < 1) {
            player.sendMessage(title);
            return;
        }
        showMessage(player,TextComponent.of(title),args);
    }

    public static TextComponent showCommand(CommandDescription command) {
        if (command.getArgs().length < 1) {
            return command.getTitle();
        }
        TextComponent.Builder builder = TextComponent.builder();
        for (TextComponent arg : command.getArgs()) {
           builder = builder
                  .append(arg)
                  .append(TextComponent.newline());
        }
        TextComponent showText = builder.build();
        return command.getTitle()
              .hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, showText))
              .clickEvent(ClickEvent.runCommand(command.getCommand()));
    }

    public static void showCommand(Player player, String prefix, CommandDescription... commands) {
        TextComponent component = LegacyComponentSerializer.legacySection().deserialize(prefix);
        List<CommandDescription> commandList = new ArrayList<>(Arrays.asList(commands));
        int total = commandList.size();
        int i = 0;
        for (CommandDescription command : commandList) {
            i++;
            component = component.append(showCommand(command));
            if (i < total) {
                component = component.append(TextComponent.of(", "));
            }

        }
        sendMessage(player, component);
    }

    public static void sendMessage(CommandSender player, TextComponent component) {
        Kits.audiences.audience(player).sendMessage(component);
    }
    //public static void sendJSONMessage(Player player, FancyMessage message) {
    // message.send(player);
    // }
}
