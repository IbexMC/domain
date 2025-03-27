package com.ibexmc.domain.api.events.player;

import com.ibexmc.domain.util.functions.PlayerFunctions;
import org.bukkit.entity.Player;

import com.ibexmc.domain.Domain;

public final class PlayerQuit extends BasePlayer {

    /**
     * PlayerQuit
     * This event is called when a player quit the game
     *
     * @param player The player that left the server
     *
     */
    public PlayerQuit(Player player) {

        super(player);

        Domain instance = super.getInstance();

        instance.getDebug().log("Domain_PlayerQuitEvent", "PlayerQuit", "PlayerQuit called");
        instance.getData().openDomainBlockRemove(player.getUniqueId());
        if (instance.config().isRemoveBypassJoinQuit())
        {
            if (PlayerFunctions.hasBypass(player))
            {
                instance.getDebug().log("PlayerQuit", "onPlayerQuit", "Player has quit - removing them from bypass mode");
                instance.getData().removeBypassPlayer(player.getUniqueId());
            }
        }

    }

}
