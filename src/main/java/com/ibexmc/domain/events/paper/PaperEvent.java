package com.ibexmc.domain.events.paper;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.ibexmc.domain.Domain;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.ibexmc.domain.flag.redstone.TNT;

/*
    This event is ONLY for PaperMC servers
*/

public class PaperEvent implements Listener {
    @EventHandler()
    public void onTNTPrimed(TNTPrimeEvent event) {

        Domain instance = Domain.getInstance();
        instance.getDebug().log("PaperEvent", "onTNTPrimed", "TNT Prime Event Started");
        instance.getDebug().log(
                "PaperEvent",
                "onTNTPrimed",
                "Block Location: " + WorldFunctions.simpleFancyReadable(event.getBlock().getLocation())
        );
        if (TNT.Priming.prevent(event.getBlock())) {
            instance.getDebug().log("PaperEvent", "onTNTPrimed", "TNT Prime Event Cancelled");
            event.setCancelled(true);
            return;
        }
    }
}
