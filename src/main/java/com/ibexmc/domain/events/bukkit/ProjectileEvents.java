package com.ibexmc.domain.events.bukkit;

import java.sql.Timestamp;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.data.type.TNT;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import com.ibexmc.domain.Domain;

import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.util.functions.PlayerFunctions;

public class ProjectileEvents implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onProjectileHit(ProjectileHitEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        if (evt.getEntity().getType().equals(EntityType.LLAMA_SPIT)) {
            return;
        }

        instance.getDebug().log("ProjectileRedstone", "onProjectileHit", evt.getEntityType().toString());

        if (evt.getHitEntity() != null)
        {

            instance.getDebug().log("ProjectileRedstone", "onProjectileHit.Entity", evt.getHitEntity().toString());
            instance.getDebug().log("ProjectileRedstone", "onProjectileHit.Entity - Name", evt.getHitEntity().getName());


            if (evt.getHitEntity().getName().equals("ITEM FRAME")) {
                if (domainBlockManager.hasFlag(evt.getHitBlock().getLocation(), Flag.Type.PREVENT_PROJECTILE_ITEM_FRAME))
                {
                    instance.getDebug().log("ProjectileRedstone", "onProjectileHit.Entity", "Removing entity");
                    evt.getEntity().remove();
                }
            }
            if (evt.getHitEntity().getType() == EntityType.PAINTING) {
                if (domainBlockManager.hasFlag(evt.getHitEntity().getLocation(), Flag.Type.PREVENT_PROJECTILE_PAINTING))
                {
                    instance.getDebug().log("ProjectileRedstone", "onProjectileHit.Entity", "Removing entity");
                    evt.getEntity().remove();
                }
            }
        }

        if (evt.getHitBlock() != null)
        {
            if (!domainBlockManager.inField(evt.getHitBlock().getLocation())) {
                return;
            }

            // Get the flags at this location - that way we can use the DomainBlockManager.hasFlag that has the flags
            // already defined, and we can avoid looking them up again and again.
            Map<Flag.Type, Boolean> hmFlagsHitBlock = domainBlockManager.getFlagsAtLocation(evt.getHitBlock().getLocation());

            instance.getDebug().log("ProjectileRedstone", "onProjectileHit.Block", evt.getHitBlock().getType().name());

            if (domainBlockManager.hasFlag(Flag.Type.PREVENT_ARROW_INTERACT, hmFlagsHitBlock))
            {
                Domain.getInstance().getData().putArrowBlock(evt.getHitBlock(), new Timestamp(System.currentTimeMillis()));
                if (evt.getHitBlock().getType() == Material.TNT) {
                    instance.getDebug().log("ProjectileRedstone", "onProjectileHit.Block", "TNT hit but arrow interaction not allowed");
                    if (evt.getHitBlock().getBlockData() instanceof TNT) {
                        instance.getDebug().log("ProjectileRedstone", "onProjectileHit.Block", "Disabling TNT unstable");
                        evt.getHitBlock().getLocation().getBlock().setType(Material.AIR);
                        evt.getHitBlock().getLocation().getBlock().setType(Material.TNT);
                    }
                }
            }
            boolean arrowRemoved = false;
            boolean isAllowed = false;
            boolean hasBypass = false;
            Entity e = evt.getEntity();
            if (evt.getEntity() instanceof Projectile) {
                Projectile proj = (Projectile) evt.getEntity();
                if (proj.getShooter() != null)
                {
                    if (proj.getShooter() instanceof Player)
                    {
                        e = (Entity) proj.getShooter();
                        isAllowed = domainBlockManager.isAllowed((Player) e, evt.getHitBlock().getLocation());
                        hasBypass = PlayerFunctions.hasBypass((Player) e);
                    }
                }
            }
            if (domainBlockManager.hasFlag(Flag.Type.REMOVE_FIRED_ARROWS_BY_NONALLOWED, hmFlagsHitBlock))
            {
                if (!hasBypass || !isAllowed)
                {
                    if (evt.getEntity() instanceof Trident) {
                        // do nothing
                    } else {
                        if (evt.getEntity() instanceof Arrow) {
                            Arrow a = (Arrow) evt.getEntity();
                            a.remove();
                            arrowRemoved = true;
                        }
                    }
                }
            }
            if (!arrowRemoved)
            {
                if (domainBlockManager.hasFlag(Flag.Type.REMOVE_FIRED_FLAME_ARROWS_BY_NONALLOWED, hmFlagsHitBlock))
                {
                    if (!hasBypass || !isAllowed)
                    {
                        if (evt.getEntity() instanceof Trident) {
                            // do nothing
                        } else {
                            if (evt.getEntity() instanceof Arrow) {
                                Arrow a = (Arrow) evt.getEntity();
                                if (a.getFireTicks() > 0)
                                {
                                    a.remove();
                                    arrowRemoved = true;
                                }
                            }
                        }
                    }
                }
            }

            hmFlagsHitBlock = null; // cleanup
        }
    }
}
