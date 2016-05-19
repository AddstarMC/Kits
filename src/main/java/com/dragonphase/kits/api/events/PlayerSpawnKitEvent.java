package com.dragonphase.kits.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.dragonphase.kits.api.Kit;

public class PlayerSpawnKitEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Kit kit;
    private boolean clear, overwrite, announce;
    private long delay;
    private boolean cancelled;

    public PlayerSpawnKitEvent(Kit kit, Player player, boolean clear, boolean overwrite, boolean announce, long delay) {
        super(player);
        this.kit = kit;
        setClear(clear);
        setOverwrite(overwrite);
        setAnnounce(announce);
        setDelay(delay);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    // Kit methods

    public Kit getKit() {
        return kit;
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

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
}
