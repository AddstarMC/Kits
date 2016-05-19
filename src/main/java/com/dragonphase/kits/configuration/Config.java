package com.dragonphase.kits.configuration;

import java.io.File;
import java.util.Map.Entry;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config extends YamlConfiguration {

    private final JavaPlugin plugin;
    private final String fileName;

    public Config(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName + (fileName.endsWith(".yml") ? "" : ".yml");

        createFiles();
    }

    public void createFiles() {
        try {
            File file = new File(plugin.getDataFolder(), fileName);
            if (!file.exists()) {
                if (plugin.getResource(fileName) != null) {
                    plugin.saveResource(fileName, false);
                } else {
                    save(file);
                }
            }
            load(file);

            try {
                save(file);
            } catch (Exception ignored) {
            }
        } catch (Exception ignored) {
        }
    }

    @SuppressWarnings("deprecation")
    public void copyDefaults() {
        if (plugin.getResource(fileName) != null) {
            YamlConfiguration temp = new YamlConfiguration();
            try {
                temp.load(plugin.getResource(fileName));
            } catch (Exception ignored) {
            }

            for (final Entry<String, Object> entry : temp.getValues(true).entrySet()) {
                if (!contains(entry.getKey())) {
                    set(entry.getKey(), entry.getValue());
                }
            }

            File file = new File(plugin.getDataFolder(), fileName);

            try {
                save(file);
                load(file);
            } catch (Exception ignored) {
            }
        }
    }

    public void save() {
        try {
            save(new File(plugin.getDataFolder(), fileName));
        } catch (Exception ignored) {
        }
    }
}
