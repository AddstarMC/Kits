package com.dragonphase.kits.util;

import net.kyori.adventure.text.TextComponent;

public class CommandDescription {
    private TextComponent title;
    private String command;
    private TextComponent[] args;

    public CommandDescription(TextComponent title, String command, TextComponent... args) {
        setTitle(title);
        setCommand(command);
        setArgs(args);
    }

    public TextComponent[] getArgs() {
        return args;
    }

    public void setArgs(TextComponent[] args) {
        this.args = args;
    }

    public TextComponent getTitle() {
        return title;
    }

    public void setTitle(TextComponent title) {
        this.title = title;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
