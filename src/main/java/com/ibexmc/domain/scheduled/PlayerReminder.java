package com.ibexmc.domain.scheduled;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.util.Permission;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerReminder {

    private final Domain instance;

    public PlayerReminder(Domain instance) {
        this.instance = instance;
    }
    public void Run() {
        if (Bukkit.getOnlinePlayers() != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (PlayerFunctions.hasBypass(player)) {
                    instance.getMessage().send(player, "bypass_reminder", "&6Remember that you have Domain bypass mode enabled", null, true);
                }
                if (Domain.getInstance().config().isDisplayBypassSnitch()) {
                    boolean displaySnitch = false;
                    if (Domain.getInstance().getData().getBypassPlayers() != null) {
                        if (Permission.hasPermission(player, "domain.bypass.snitch")) {
                            for(UUID bypassPlayer : Domain.getInstance().getData().getBypassPlayers()) {
                                if (bypassPlayer.equals(player.getUniqueId())) {
                                    displaySnitch = true;
                                    break;
                                }
                            }
                            if (displaySnitch) {
                                String playerList = "";
                                for(UUID bypassPlayerCheck : Domain.getInstance().getData().getBypassPlayers()) {
                                    Player bypassPlayer = Bukkit.getPlayer(bypassPlayerCheck);
                                    if (bypassPlayer != null) {
                                        playerList = playerList + bypassPlayer.getName() + ", ";
                                    } else {
                                        Domain.getInstance().getData().removeBypassPlayer(bypassPlayerCheck);
                                    }
                                }
                                playerList = playerList + "#";
                                playerList = playerList.replace(", #", "");

                                Map<String, String> placeHolder = new HashMap<>();
                                placeHolder.put("<%players%>", playerList);
                                instance.getMessage().send(player, "bypass_snitch_count", "The following players are in Domain Bypass Mode: &d<%players%>", placeHolder, true);
                            }
                        }
                    }
                }
            }
        }
    }
}
