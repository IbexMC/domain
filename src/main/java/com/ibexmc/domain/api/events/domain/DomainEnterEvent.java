package com.ibexmc.domain.api.events.domain;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;


public final class DomainEnterEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    private Player internalPlayer;
    private DomainBlock internalDomainBlock;
    private DomainBlockManager domainBlockManager;

    /**
     * DomainBypassEnterEvent(Player)
     * This event is called when a player enters a field
     *
     * @param player The player that is entering the field
     * @param DomainBlock The domain field the player is entering
     */
    public DomainEnterEvent(Player player, DomainBlock DomainBlock) {

        Domain instance = Domain.getInstance();
        domainBlockManager = new DomainBlockManager(instance);

        // Check to see if the player is null
        if (player != null) {
            if (DomainBlock != null) {
                this.cancelled = false;
                internalPlayer = player;
                internalDomainBlock = DomainBlock;
            } else {
                // A null DomainBlock object was sent - cancel the event
                this.cancelReason = instance.getLocale().localeText("player_enter_null_field", "Field is null", null);
                this.cancelled = true;
            }
        } else {
            // A null player object was sent - cancel the event
            this.cancelReason = instance.getLocale().localeText("player_enter_null_player", "Player is null", null);
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

    public Player getPlayer() {
        return internalPlayer;
    }
    public DomainBlock getDomainBlock() {
        return internalDomainBlock;
    }
    public boolean isAllowedInField() {
        return domainBlockManager.isAllowedToField(internalPlayer, internalDomainBlock);
    }
}