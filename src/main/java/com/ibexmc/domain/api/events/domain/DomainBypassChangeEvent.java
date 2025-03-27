package com.ibexmc.domain.api.events.domain;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.util.Permission;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;


public final class DomainBypassChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled

    /**
     * DomainBypassChangeEvent(Player)
     * This event is called when a player enters or leaves Domain Bypass mode
     *
     * @param player The player that is changing Domain Bypass mode
     */
    public DomainBypassChangeEvent(Player player) {

        Domain instance = Domain.getInstance();

        // Check to see if the player is null
        if (player != null) {
            BossBar bar = Domain.getInstance().getBypassBar(); // Get the bypass BossBar
            if (Permission.hasPermission(player, "domain.bypass")) { // Check if the player has bypass permission
                if (PlayerFunctions.hasBypass(player)) { // Check if the player is in bypass mode

                    Domain.getInstance().getData().removeBypassPlayer(player.getUniqueId());

                    instance.getMessage().send(player, "command_bypass_inactive", "Domain Bypass - &4Inactive", null, true); // Send a message to the player
                    for (Player barPlayer : bar.getPlayers()) { // Loop through the bypass players
                        if (barPlayer.getUniqueId().equals(player.getUniqueId())) { // If the player matches, remove them
                            bar.removePlayer(player);
                            instance.setBypassBar(bar); // Update the bypass bar list
                        }
                    }
                } else {
                    //Domain.instance().AddBypassPlayer(player.getUniqueId().toString()); // Add the player to the bypass list
                    instance.getData().addBypassPlayer(player.getUniqueId());
                    instance.getMessage().send(player, "command_bypass_active", "Domain Bypass - &2Active", null, true); // Message the player
                    bar.setProgress(1); // Set the progress of the bypass BossBar
                    boolean hasPlayer = false;
                    if (bar.getPlayers() != null) {
                        for (Player barPlayer : bar.getPlayers()) {
                            if (barPlayer.getUniqueId().equals(player.getUniqueId())) {
                                hasPlayer = true;
                                break;
                            }
                        }
                        if (!hasPlayer) {
                            bar.addPlayer(player);
                            bar.setVisible(true);
                            Domain.getInstance().setBypassBar(bar);
                        }
                    }
                }
            } else {
                // The player does not have the domain.bypass permission - send a message to the player and cancel the event
                instance.getMessage().send(player, "command_no_perm", "&4You do not have permission to use this command", null, true);
                this.cancelReason = instance.getLocale().localeText("command_no_perm", "&4You do not have permission to use this command", null);
                this.cancelled = true;
            }
        } else {
            // A null player object was sent - cancel the event
            this.cancelReason = instance.getLocale().localeText("bypass_change_null_player", "Player is null", null);
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