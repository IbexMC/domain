package com.ibexmc.domain.events.bukkit;

import com.ibexmc.domain.Domain;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;

import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;

public class PotionSplashEvents implements Listener {
    @EventHandler
    public void onPotionSplash(PotionSplashEvent evt) {

        DomainBlockManager domainBlockManager = new DomainBlockManager(Domain.getInstance());
        Location loc = null;
        if (!evt.isCancelled()) {
            if (evt != null) {
                if (evt.getHitBlock() != null)
                {
                    loc = evt.getHitBlock().getLocation();
                }
                if (evt.getHitEntity() != null)
                {
                    loc = evt.getHitEntity().getLocation();
                }
                if (loc != null) {
                    if (domainBlockManager.hasFlag(loc, Flag.Type.PREVENT_SPLASHPOTION_EFFECT))
                    {
                        evt.getAffectedEntities().clear();
                        evt.setCancelled(true);
                    }
                }
            }
        }
    }
    @EventHandler
    public void onLingeringPotionSplash(LingeringPotionSplashEvent evt) {

        DomainBlockManager domainBlockManager = new DomainBlockManager(Domain.getInstance());

        Location loc = null;
        if (evt.getHitBlock() != null)
        {
            loc = evt.getHitBlock().getLocation();
        }
        if (evt.getHitEntity() != null)
        {
            loc = evt.getHitEntity().getLocation();
        }
        if (loc != null) {
            if (domainBlockManager.hasFlag(loc, Flag.Type.PREVENT_LINGERINGPOTION_EFFECT))
            {
                evt.setCancelled(true);
            }
        }
    }
}
