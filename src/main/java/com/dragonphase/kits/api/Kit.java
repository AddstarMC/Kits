package com.dragonphase.kits.api;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Kit implements ConfigurationSerializable {

    private String name;
    private long delay;
    private boolean clear, overwrite, announce;
    private ItemStack[] items;

    public Kit(String name, ItemStack[] items, long delay, boolean clear, boolean overwrite, boolean announce) {
        setName(name);
        setDelay(delay);
        setClear(clear);
        setOverwrite(overwrite);
        setAnnounce(announce);
        setItems(items);
    }

    @SuppressWarnings("unchecked")
    public Kit(Map<String, Object> args) {
        setName((String) args.get("name"));
        setItems(((List<ItemStack>) args.get("items")).toArray(new ItemStack[((List<ItemStack>) args.get("items")).size()]));
        setClear((Boolean) (args.containsKey("clear") ? args.get("clear") : true));
        setOverwrite((Boolean) (args.containsKey("overwrite") ? args.get("overwrite") : true));
        setAnnounce((Boolean) (args.containsKey("announce") ? args.get("announce") : true));

        try {
            setDelay((Long) (args.containsKey("delay") ? args.get("delay") : 0));
        } catch (Exception ex) {
            setDelay((Integer) (args.containsKey("delay") ? args.get("delay") : 0));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public boolean getClear() {
        return clear;
    }

    public void setClear(boolean clear) {
        this.clear = clear;
    }

    public boolean getOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public boolean getAnnounce() {
        return announce;
    }

    public void setAnnounce(boolean announce) {
        this.announce = announce;
    }

    public ItemStack[] getItems() {
        return items;
    }

    public void setItems(ItemStack[] items) {
        this.items = items;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();

        result.put("name", getName());
        result.put("delay", getDelay());
        result.put("clear", getClear());
        result.put("overwrite", getOverwrite());
        result.put("announce", getAnnounce());
        result.put("items", getItems());

        return result;
    }
}
