package com.ibexmc.domain.api.events.domain;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.dependencies.worldguard.WorldGuard;
import com.ibexmc.domain.domainblock.config.IFieldConfig;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public final class DomainFieldCreateEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private String cancelReason;
    private boolean cancelled;

    public DomainFieldCreateEvent(Player player, Block block, String domainBlockKey) {
        Domain instance = Domain.getInstance();
        IFieldConfig fieldConfig = instance.getData().getDomainFieldConfig(domainBlockKey);
        if (fieldConfig == null) {
            this.cancelReason = instance.getLocale().localeText("field_create_invalid_key", "Invalid Domain Block Key", null);
            this.cancelled = true;
        }
        WorldGuard worldGuard = new WorldGuard(Domain.getInstance());
        if (!worldGuard.canBuild(player, block.getLocation())) {
            instance.getDebug().log("DomainFieldCreateEvent", "Event", "WorldGuard will not allow fields to be created here");
            this.cancelReason = instance.getLocale().localeText("field_create_invalid_wg", "WorldGuard will not allow fields to be created here", null);
            this.cancelled = true;
        }
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}