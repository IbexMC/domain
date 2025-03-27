package com.ibexmc.domain.scheduled;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.dependencies.papi.PAPIValues;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class UpdatePAPIValues {

    Domain instance;

    public UpdatePAPIValues(Domain instance) {
        this.instance = instance;
    }

    /**
     * Called by the update papi scheduler - Used to update the PAPI values for each player
     */
    public void Run() {

        PAPIValues papiValues = new PAPIValues(instance, new HashMap<>());

        for (Player player : Bukkit.getOnlinePlayers()) {
            papiValues.update(player);
        }
    }
}
