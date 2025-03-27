package com.ibexmc.domain.events.bukkit;


import com.ibexmc.domain.Domain;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.hanging.HangingPlaceEvent;


import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.flag.entity.living.Animal;
import com.ibexmc.domain.util.functions.PlayerFunctions;


public class HangingEvents implements Listener {
    @EventHandler
    public void onHangingPlace(HangingPlaceEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        if (!domainBlockManager.inField(evt.getEntity().getLocation())) {
            return;
        }

        instance.getDebug().log("HangingEvents", "onHangingPlace", "Attempting to hang item.  Type: " + evt.getEntity().getType().name());

        if (evt.getPlayer() != null) {
            boolean isAllowed = domainBlockManager.isAllowed(evt.getPlayer(), evt.getBlock().getLocation());
            boolean hasBypass = PlayerFunctions.hasBypass(PlayerFunctions.playerFromEntity(evt.getPlayer()));

            if (evt.getEntity().getType().equals(EntityType.PAINTING)) {
                if (domainBlockManager.hasFlag(evt.getBlock().getLocation(), Flag.Type.PREVENT_PLACE_PAINTING_BY_NONALLOWED))
                {
                    if (!isAllowed && !hasBypass) {
                        instance.getMessage().send(evt.getPlayer(), "hanging_painting", "You cannot place paintings here" , null, true);
                        evt.setCancelled(true);
                        return;
                    }
                }
            }
            if (evt.getEntity().getType().equals(EntityType.ITEM_FRAME)) {
                if (domainBlockManager.hasFlag(evt.getBlock().getLocation(), Flag.Type.PREVENT_ITEM_FRAME_ACCESS_BY_NONALLOWED))
                {
                    if (!isAllowed && !hasBypass) {
                        instance.getMessage().send(evt.getPlayer(), "hanging_itemframe", "You cannot place item frames here" , null, true);
                        evt.setCancelled(true);
                        return;
                    }
                }
            }
            if (evt.getEntity().getType().equals(EntityType.LEASH_HITCH)) {
                if (Animal.Lead.preventNonAllowed(evt.getPlayer(), evt.getBlock().getLocation())) {
                    evt.setCancelled(true);
                    return;
                }
            }
        }
    }
    @EventHandler
    public void onHangingBreak(HangingBreakEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        instance.getDebug().log("HangingEvents", "onHangingBreakByEntity()", "Event fired");
        if (!domainBlockManager.inField(evt.getEntity().getLocation())) {
            return;
        }
        //instance.getDebug().log("HangingEvents", "onHangingBreak", "Hanging item break event.  Type: " + evt.getEntity().getType().name());
        if (evt.getCause().equals(RemoveCause.EXPLOSION)) {
            if (domainBlockManager.hasFlag(evt.getEntity().getLocation(), Flag.Type.PREVENT_EXPLOSION_DAMAGE))
            {
                instance.getDebug().log("HangingEvents", "onHangingBreak()", "Explosion damage cancelled for " + evt.getEntity().getType().name());
                evt.setCancelled(true);
                return;
            }
        }

        instance.getDebug().log("HangingEvents", "onHangingBreak", "Hanging item break event.  Type: " + evt.getEntity().getType().name());
        if (evt.getEntity().getType().equals(EntityType.LEASH_HITCH)) {
            if (evt.getCause().equals(RemoveCause.ENTITY)) {
                if (domainBlockManager.hasFlag(evt.getEntity().getLocation(), Flag.Type.PREVENT_LEAD_BY_NONALLOWED))
                {
                    instance.getDebug().log("HangingEvents", "onHangingBreak()", "Explosion damage cancelled for " + evt.getEntity().getType().name());
                    evt.setCancelled(true);
                    return;
                }
            }
        }

        instance.getDebug().log("HangingEvents", "onHangingBreak", "Hanging item break event.  Type: " + evt.getEntity().getType().name());
        if (evt.getEntity().getType().equals(EntityType.ITEM_FRAME)) {
            if (evt.getCause().equals(RemoveCause.ENTITY)) {
                if (domainBlockManager.hasFlag(evt.getEntity().getLocation(), Flag.Type.PREVENT_BREAK_ITEM_FRAME_BY_NONALLOWED))
                {
                    instance.getDebug().log("HangingEvents", "onHangingBreak()", "Item frame break cancelled for " + evt.getEntity().getType().name());
                    evt.setCancelled(true);
                    return;
                }
            }
        }
    }
    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        instance.getDebug().log("HangingEvents", "onHangingBreakByEntity()", "Event fired - Remover: " + evt.getRemover().getType().name());

        if(evt.getRemover() instanceof Player) {
            Player player = (Player) evt.getRemover();
            boolean isAllowed = domainBlockManager.isAllowed(player, evt.getEntity().getLocation());
            boolean hasBypass = PlayerFunctions.hasBypass(PlayerFunctions.playerFromEntity(player));
            if (evt.getEntity().getType() == EntityType.PAINTING) {
                if (!isAllowed && !hasBypass) {
                    if (domainBlockManager.hasFlag(evt.getEntity().getLocation(), Flag.Type.PREVENT_BREAK_PAINTING_BY_NONALLOWED))
                    {
                        instance.getDebug().log("HangingEvents", "onHangingBreakByEntity()", "Painting cannot be removed by " + player.getName());
                        instance.getMessage().send(player, "break_painting", "&cYou cannot break paintings here" , null, true);
                        evt.setCancelled(true);
                        return;
                    }
                }
            }
        }

        if(evt.getRemover() instanceof Player) {
            Player player = (Player) evt.getRemover();
            boolean isAllowed = domainBlockManager.isAllowed(player, evt.getEntity().getLocation());
            boolean hasBypass = PlayerFunctions.hasBypass(PlayerFunctions.playerFromEntity(player));
            if (evt.getEntity().getType() == EntityType.ITEM_FRAME) {
                if (!isAllowed && !hasBypass) {
                    if (domainBlockManager.hasFlag(evt.getEntity().getLocation(), Flag.Type.PREVENT_BREAK_ITEM_FRAME_BY_NONALLOWED))
                    {
                        instance.getDebug().log("HangingEvents", "onHangingBreakByEntity()", "Item Frame cannot be removed by " + player.getName());
                        instance.getMessage().send(player, "break_item_frame", "&cYou cannot break that here" , null, true);
                        evt.setCancelled(true);
                        return;
                    }
                }
            }
        }

        if (evt.getRemover() instanceof Projectile) {
            if (evt.getEntity().getType() == EntityType.PAINTING) {
                if (domainBlockManager.hasFlag(evt.getEntity().getLocation(), Flag.Type.PREVENT_PROJECTILE_PAINTING))
                {
                    instance.getDebug().log("ProjectileRedstone", "onProjectileHit.Entity", "Removing entity");
                    evt.setCancelled(true);
                }
            }
        }

    }
}
