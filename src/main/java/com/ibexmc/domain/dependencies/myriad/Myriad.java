package com.ibexmc.domain.dependencies.myriad;

import com.ibexmc.domain.Domain;

import java.util.UUID;

public class Myriad {

    private final Domain instance;

    public Myriad(Domain instance) {
        this.instance = instance;
    }

    public boolean validClan(String clan) {
        com.ibexmc.myriad.Myriad mc = Domain.getInstance().getDependencies().getMyriadClans();
        if (mc != null) {
            return mc.getAPI().clanExists(clan);
        } else {
            return false;
        }
    }

    public String playerClanTag(UUID uuid) {
        com.ibexmc.myriad.Myriad mc = Domain.getInstance().getDependencies().getMyriadClans();
        if (mc != null) {
            return mc.getAPI().getPlayerClanTag(uuid);
        } else {
            return "";
        }
    }
}
