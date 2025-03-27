package com.ibexmc.domain.api.events.domain;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public final class DomainMergeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private String cancelReason;
    private boolean cancelled;
    private Player player;
    private DomainBlock domainBlock;

    public DomainMergeEvent(Player player, DomainBlock domainBlock) {

        Domain instance = Domain.getInstance();

        if (domainBlock != null) {
            instance.getDebug().log("DomainMergeEvent", "Constructor", "Domain Block ID: " + domainBlock.getPlacedId());
        } else {
            this.cancelReason = "No Domain Block found";
            this.cancelled = true;
        }

        this.player = player;
        this.domainBlock = domainBlock;

    }

    public Player getPlayer() {
        return player;
    }
    public DomainBlock getDomainBlock() {
        return domainBlock;
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