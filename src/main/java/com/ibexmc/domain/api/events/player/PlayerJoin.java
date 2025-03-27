package com.ibexmc.domain.api.events.player;

import com.ibexmc.domain.util.functions.PlayerFunctions;
import org.bukkit.entity.Player;

import com.ibexmc.domain.Domain;

public final class PlayerJoin extends BasePlayer {

    /**
     * PlayerJoin
     * This event is called when a player joins the game
     *
     * @param player The player that joined the server
     *
     */
    public PlayerJoin(Player player) {

        super(player);

        Domain instance = super.getInstance();

        instance.getDebug().log("Domain_PlayerJoinEvent", "PlayerJoin", "PlayerJoin called");
        if (Domain.getInstance().config().isRemoveBypassJoinQuit())
        {
            if (PlayerFunctions.hasBypass(player))
            {
                instance.getDebug().log("PlayerJoin", "onPlayerJoin", "Player joining already in Bypass mode - removing them from bypass mode");
                Domain.getInstance().getData().removeBypassPlayer(player.getUniqueId());
            }
        }

    }

}
