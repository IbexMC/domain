package com.ibexmc.domain.api.events.domain;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;


public final class DomainResizeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private String cancelReason; // The cancellation reason
    private boolean cancelled = false; // A flag to indicate if the event was cancelled
    private Player internalPlayer;
    private DomainBlock internalDomainBlock;
    private DomainBlockManager domainBlockManager;

    /**
     * DomainResizeEvent(Player, DomainBlock, Location, Location)
     * This event is called when a player resizes a field
     *
     * @param player The player that is entering the field
     * @param domainBlock The pre-changed domain field the player is entering
     */
    public DomainResizeEvent(Player player, DomainBlock domainBlock) {

        Domain instance = Domain.getInstance();
        domainBlockManager = new DomainBlockManager(instance);

        // Check to see if the player is null
        if (player != null) {
            if (domainBlock != null) {
                internalPlayer = player;
                internalDomainBlock = domainBlock;
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
    public Location getCorner1() {
        return internalDomainBlock.getCorner1();
    }
    public Location getCorner2() {
        return internalDomainBlock.getCorner2();
    }
    public boolean isAllowedInField() {
        return domainBlockManager.isAllowedToField(internalPlayer, internalDomainBlock);
    }
}