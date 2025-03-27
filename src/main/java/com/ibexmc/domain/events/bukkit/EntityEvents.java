package com.ibexmc.domain.events.bukkit;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibexmc.domain.helper.MaterialGroups;
import com.ibexmc.internal.functions.StringFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.spigotmc.event.entity.EntityMountEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.ibexmc.domain.Domain;

import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.flag.entity.living.Animal;
import com.ibexmc.domain.util.functions.PlayerFunctions;

public class EntityEvents implements Listener {
    private String getEntityTypeName(Entity entity) {
        if (entity == null) {
            return "null";
        } else {
            return entity.getType().name();
        }
    }
    @EventHandler
    public void onEntityTransform(EntityTransformEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        instance.getDebug().log("EntityEvents", "onEntityTransform", evt.getTransformReason().name());
        if (evt.getTransformReason() == EntityTransformEvent.TransformReason.CURED) {
            if (evt.getEntity() != null) {
                if (evt.getEntity() instanceof ZombieVillager) {
                    ZombieVillager zombieVillager = (ZombieVillager) evt.getEntity();
                    if (Domain.getInstance().getData().isConvertingZombieVillager(
                            evt.getEntity().getEntityId())
                    ) {
                        instance.getDebug().log(
                                "EntityEvents",
                                "EntityTransformEvent",
                                "Preventing Zombie Villager Cure for Entity " + evt.getEntity().getEntityId()
                        );
                        evt.setCancelled(true);
                        Domain.getInstance().getData().removeConvertingZombieVillager(
                                evt.getEntity().getEntityId()
                        );
                    }
                }
            }
        }
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        if (evt.getEntity() != null) {
            if (evt.getEntityType() != null) {
                instance.getDebug().log("EntityEvents", "onEntityDeath", "Type: " + StringFunctions.isNull(evt.getEntityType().name(), ""));
            }
        }
        //instance.getDebug().log("EntityEvents", "onEntityDeath", "Last damage cause: " + evt.getEntity().getLastDamageCause().getCause().name());
    }
    @EventHandler
    public void onEntityDamage(EntityDamageEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        boolean isAllowed = false;
        boolean hasBypass = false;
        boolean isPlayer = PlayerFunctions.isEntityPlayer(evt.getEntity());
        if (isPlayer)
        {
            isAllowed = domainBlockManager.isAllowed((Player) evt.getEntity());
            hasBypass = PlayerFunctions.hasBypass((Player) evt.getEntity());
        }

        // Get the flags at this location - that way we can use the domainBlockManager.hasFlag that has the flags
        // already defined, and we can avoid looking them up again and again.
        Map<Flag.Type, Boolean> hmFlagsEntity = domainBlockManager.getFlagsAtLocation(evt.getEntity().getLocation());

        instance.getDebug().log("EntityEvents", "onEntityDamage", "Cause: " + evt.getCause().name());
        instance.getDebug().log("EntityEvents", "onEntityDamage", "Entity Type: " + evt.getEntityType().name());

        if (evt.getEntity() instanceof Creature) {
            //instance.getDebug().log("EntityEvents", "onEntityDamage", "Damaged entity is a creature");
            if (domainBlockManager.hasFlag(evt.getEntity().getLocation(), Flag.Type.PREVENT_ANIMAL_DAMAGE_BY_ALL))
            {
                instance.getDebug().log("EntityEvents", "onEntityDamage", "Removing damage to " + evt.getEntityType().name());
                evt.setCancelled(true);
                hmFlagsEntity = null;
                return;
            }
        }
        // Magic damage
        if (evt.getCause() == DamageCause.MAGIC)
        {
            if (!evt.isCancelled())
            {
                if (domainBlockManager.hasFlag(Flag.Type.PREVENT_MAGIC, hmFlagsEntity))
                {
                    evt.getEntity().setFireTicks(0);
                    evt.setCancelled(true);
                    hmFlagsEntity = null;
                    return;
                }
            }
        }
        // Villager fire damage
        if (evt.getEntityType() == EntityType.VILLAGER)
        {
            if (evt.getCause() == DamageCause.FIRE || evt.getCause() == DamageCause.FIRE_TICK)
            {
                if (!evt.isCancelled())
                {
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_VILLAGER_FIRE, hmFlagsEntity))
                    {
                        evt.getEntity().setFireTicks(0);
                        evt.setCancelled(true);
                        hmFlagsEntity = null;
                        return;
                    }
                }
            }
        }
        // Contact damage
        if (evt.getCause() == DamageCause.CONTACT)
        {
            if (!evt.isCancelled())
            {
                if (isPlayer)
                {
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_PLAYER_CONTACT_DAMAGE, hmFlagsEntity))
                    {
                        evt.setCancelled(true);
                        hmFlagsEntity = null;
                        return;
                    }
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_ALLOWED_PLAYER_CONTACT_DAMAGE, hmFlagsEntity))
                    {
                        if (hasBypass || isAllowed)
                        {
                            evt.setCancelled(true);
                            hmFlagsEntity = null;
                            return;
                        }
                    }
                } else {
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_MOB_CONTACT_DAMAGE, hmFlagsEntity))
                    {
                        evt.setCancelled(true);
                        hmFlagsEntity = null;
                        return;
                    }
                }
            }
        }
        // Wither damage
        if (evt.getCause() == DamageCause.WITHER)
        {
            if (!evt.isCancelled())
            {
                if (isPlayer)
                {
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_PLAYER_WITHER_DAMAGE, hmFlagsEntity))
                    {
                        evt.setCancelled(true);
                        hmFlagsEntity = null;
                        return;
                    }
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_ALLOWED_PLAYER_WITHER_DAMAGE, hmFlagsEntity))
                    {
                        if (hasBypass || isAllowed)
                        {
                            evt.setCancelled(true);
                            hmFlagsEntity = null;
                            return;
                        }
                    }
                } else {
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_MOB_WITHER_DAMAGE, hmFlagsEntity))
                    {
                        evt.setCancelled(true);
                        hmFlagsEntity = null;
                        return;
                    }
                }
            }
        }
        // Drowning damage
        if (evt.getCause() == DamageCause.DROWNING)
        {
            if (!evt.isCancelled())
            {
                if (isPlayer)
                {
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_PLAYER_DROWN, hmFlagsEntity))
                    {
                        evt.setCancelled(true);
                        hmFlagsEntity = null;
                        return;
                    }
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_ALLOWED_PLAYER_DROWN, hmFlagsEntity))
                    {
                        if (hasBypass || isAllowed)
                        {
                            evt.setCancelled(true);
                            hmFlagsEntity = null;
                            return;
                        }
                    }
                } else {
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_MOB_DROWN, hmFlagsEntity))
                    {
                        evt.setCancelled(true);
                        hmFlagsEntity = null;
                        return;
                    }
                }
            }
        }
        // Explosion damage
        if (evt.getCause() == DamageCause.BLOCK_EXPLOSION || evt.getCause() == DamageCause.ENTITY_EXPLOSION)
        {
            if (!evt.isCancelled())
            {
                if (isPlayer)
                {
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_PLAYER_EXPLOSION_DAMAGE, hmFlagsEntity))
                    {
                        evt.setCancelled(true);
                        hmFlagsEntity = null;
                        return;
                    }
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_ALLOWED_PLAYER_EXPLOSION_DAMAGE, hmFlagsEntity))
                    {
                        if (hasBypass || isAllowed)
                        {
                            evt.setCancelled(true);
                            hmFlagsEntity = null;
                            return;
                        }
                    }
                } else {
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_MOB_EXPLOSION_DAMAGE, hmFlagsEntity))
                    {
                        evt.setCancelled(true);
                        hmFlagsEntity = null;
                        return;
                    }
                }
            }
        }
        // Fall damage
        if (evt.getCause() == DamageCause.FALL)
        {
            if (!evt.isCancelled())
            {
                if (isPlayer)
                {
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_PLAYER_FALL_DAMAGE, hmFlagsEntity))
                    {
                        evt.setCancelled(true);
                        hmFlagsEntity = null;
                        return;
                    }
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_ALLOWED_PLAYER_FALL_DAMAGE, hmFlagsEntity))
                    {
                        if (hasBypass || isAllowed)
                        {
                            evt.setCancelled(true);
                            hmFlagsEntity = null;
                            return;
                        }
                    }
                } else {
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_MOB_FALL_DAMAGE, hmFlagsEntity))
                    {
                        evt.setCancelled(true);
                        hmFlagsEntity = null;
                        return;
                    }
                }
            }
        }
        // Fire damage
        if
        (
                evt.getCause() == DamageCause.FIRE ||
                        evt.getCause() == DamageCause.FIRE_TICK ||
                        evt.getCause() == DamageCause.LAVA ||
                        evt.getCause() == DamageCause.HOT_FLOOR ||
                        evt.getCause() == DamageCause.LIGHTNING
        )
        {
            if (!evt.isCancelled())
            {
                if (isPlayer)
                {
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_PLAYER_FIRE_DAMAGE, hmFlagsEntity))
                    {
                        evt.setCancelled(true);
                        evt.getEntity().setFireTicks(0);
                        hmFlagsEntity = null;
                        return;
                    }
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_ALLOWED_PLAYER_FIRE_DAMAGE, hmFlagsEntity))
                    {
                        if (hasBypass || isAllowed)
                        {
                            evt.setCancelled(true);
                            evt.getEntity().setFireTicks(0);
                            hmFlagsEntity = null;
                            return;
                        }
                    }
                } else {
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_MOB_FIRE_DAMAGE, hmFlagsEntity))
                    {
                        evt.setCancelled(true);
                        evt.getEntity().setFireTicks(0);
                        hmFlagsEntity = null;
                        return;
                    }
                }
            }
        }
    }
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        // Get the damager and victim
        Entity eVictim = evt.getEntity();
        if (!domainBlockManager.inField(eVictim.getLocation())) {
            return;
        }
        Entity eDamager = evt.getDamager();

        // Get the flags at this location - that way we can use the domainBlockManager.hasFlag that has the flags
        // already defined, and we can avoid looking them up again and again.

        instance.getDebug().log("EntityEvents", "onEntityDamageByEntity", "Victim: " + getEntityTypeName(evt.getEntity()));
        instance.getDebug().log("EntityEvents", "onEntityDamageByEntity", "Victim Location: " + WorldFunctions.simpleReadable(evt.getEntity().getLocation()));
        instance.getDebug().log("EntityEvents", "onEntityDamageByEntity", "Damager: " +  getEntityTypeName(evt.getDamager()));
        instance.getDebug().log("EntityEvents", "onEntityDamageByEntity", "Damager Location: " + WorldFunctions.simpleReadable(evt.getDamager().getLocation()));



        Map<Flag.Type, Boolean> hmFlagsVictim = domainBlockManager.getFlagsAtLocation(evt.getEntity().getLocation());

        // Check if projectile, if so, get the shooter as the damager
        if (eDamager instanceof Projectile) {
            Projectile proj = (Projectile) evt.getDamager();
            if (proj.getShooter() != null)
            {
                if (proj.getShooter() instanceof Player)
                {
                    eDamager = (Entity) proj.getShooter();
                }
            }
        }

        boolean damagerHasBypass = false;
        boolean damagerIsAllowed = false;

        //instance.getDebug().log("EntityEvents", "onEntityDamageByEntity()", "Victim: " + eVictim.getName() + " at " + WorldFunctions.simpleReadable(eVictim.getLocation()));

        if (eVictim.getName().equals("Item Frame")) {
            if (domainBlockManager.hasFlag(Flag.Type.PREVENT_PROJECTILE_ITEM_FRAME, hmFlagsVictim))
            {
                if (evt.getDamager() instanceof Projectile) {
                    evt.setCancelled(true);
                }
            }
        }

        if (eVictim.getType() == EntityType.PAINTING) {
            if (domainBlockManager.hasFlag(Flag.Type.PREVENT_PROJECTILE_PAINTING, hmFlagsVictim))
            {
                if (evt.getDamager() instanceof Projectile) {
                    evt.setCancelled(true);
                }
            }
        }

        // Damage by player
        if (eDamager instanceof Player)
        {
            // Damage by player on player
            if (eVictim instanceof Player)
            {
                damagerHasBypass = PlayerFunctions.hasBypass((Player) eDamager);
                damagerIsAllowed = domainBlockManager.isAllowed((Player) eDamager, eVictim.getLocation());


                // Glow player
                if (domainBlockManager.hasFlag(Flag.Type.GLOW_NONALLOWED_PLAYER_ON_PVP, hmFlagsVictim))
                {
                    if (!damagerHasBypass && !damagerIsAllowed)
                    {
                        Domain.getInstance().getData().putPVPList(PlayerFunctions.playerFromEntity(evt.getEntity()), new Timestamp(System.currentTimeMillis()));
                        instance.getDebug().log("EntityEvents", "onEntityDamageByEntity", "PVP by non-allowed player detected - setting player to glowing");
                        Map<String, String> placeHolder = new HashMap<>();
                        placeHolder.put("<%pvpseconds%>", Integer.toString(Domain.getInstance().config().getPvpGlowTime()));
                        instance.getMessage().send((Player) eDamager, "pvp_glow_time", "PVP detected in a PVP warning area - You will glow for <%pvpseconds%> seconds" ,placeHolder, true);

                        evt.getDamager().setGlowing(true);
                    }
                }

                // Prevent PVP
                if (domainBlockManager.hasFlag(Flag.Type.PREVENT_PVP, hmFlagsVictim))
                {
                    if (!damagerHasBypass)
                    {
                        Domain.getInstance().getData().putPVPList(PlayerFunctions.playerFromEntity(evt.getEntity()), new Timestamp(System.currentTimeMillis()));
                        instance.getDebug().log("EntityEvents", "onEntityDamageByEntity", "PVP by non-allowed player detected - removing PVP damage");
                        instance.getMessage().send((Player) eDamager, "pvp_not_allowed", "PVP is not allowed in this area", null, true);


                        evt.setCancelled(true);
                        hmFlagsVictim = null;
                        return;
                    }
                }
                // Prevent PVP
                if (domainBlockManager.hasFlag(Flag.Type.PREVENT_PVP_BY_NONALLOWED, hmFlagsVictim))
                {
                    if (!damagerHasBypass && !damagerIsAllowed)
                    {
                        Domain.getInstance().getData().putPVPList(PlayerFunctions.playerFromEntity(evt.getEntity()), new Timestamp(System.currentTimeMillis()));
                        instance.getDebug().log("EntityEvents", "onEntityDamageByEntity", "PVP by non-allowed player detected - removing PVP damage");
                        instance.getMessage().send(
                                (Player) eDamager,
                                "pvp_not_allowed_can_engage", 
                                "PVP is not allowed in this area by people not allowed into the field. &4CAUTION: Players allowed into this field can engage you" , 
                                null,
                                true
                        );


                        evt.setCancelled(true);
                        hmFlagsVictim = null;
                        return;
                    }
                }
            } else {
                // Damage by player on non-player

                // Item Frame Access
                instance.getDebug().log("EntityEvents", "onEntityDamageByEntity", "Damage by Player On non-player");
                if (eVictim.getType() == EntityType.ITEM_FRAME)
                {
                    instance.getDebug().log("EntityEvents", "onEntityDamageByEntity", "Victim is an item frame");
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_ITEM_FRAME_ACCESS_BY_NONALLOWED, hmFlagsVictim))
                    {
                        instance.getDebug().log("EntityEvents", "onEntityDamageByEntity", "Field has PREVENT_ITEM_FRAME_ACCESS_BY_NONALLOWED");
                        damagerHasBypass = PlayerFunctions.hasBypass((Player) eDamager);
                        damagerIsAllowed = domainBlockManager.isAllowed((Player) eDamager, eVictim.getLocation());
                        instance.getDebug().log("EntityEvents", "onEntityDamageByEntity", "damagerHasBypass: " + Boolean.toString(damagerHasBypass) + " - damagerIsAllowed: " + Boolean.toString(damagerIsAllowed));
                        if (!damagerHasBypass && !damagerIsAllowed)
                        {
                            instance.getMessage().send((Player) eDamager, "item_frame_deny_access", "You cannot access items from item frames in this area" , null, true);
                            evt.setCancelled(true);
                            hmFlagsVictim = null;
                            return;
                        }
                    }

                }

                if (eVictim.getType() == EntityType.ARMOR_STAND) {
                    if (com.ibexmc.domain.flag.entity.nonliving.ArmorStand.Damage.preventNonAllowed((Player) eDamager, (ArmorStand) eVictim)) {
                        instance.getDebug().log("EntityEvents", "onEntityDamageByEntity", "Armor stand entity damage cancelled");
                        evt.setCancelled(true);
                        hmFlagsVictim = null;
                        return;
                    }
                }

            }
        }
        else
        {
            // Player Damage by creature
            if (eVictim instanceof Player)
            {
                if (domainBlockManager.hasFlag(Flag.Type.PREVENT_PLAYER_ATTACK_DAMAGE_BY_CREATURE, hmFlagsVictim))
                {
                    instance.getDebug().log("EntityEvents", "onEntityDamageByEntity", "Damage from non-player " + evt.getEntity().getName() + " removed for player " + eDamager.getName());
                    evt.setCancelled(true);
                    hmFlagsVictim = null;
                    return;
                }
            }

            if (eVictim.getType() == EntityType.ARMOR_STAND) {
                instance.getDebug().log("EntityEvents", "onEntityDamageByEntity", "Non-player damager and and non-player victim damage to Armor stand");
                if (evt.getCause() == DamageCause.ENTITY_EXPLOSION) {
                    if (com.ibexmc.domain.flag.entity.nonliving.ArmorStand.Damage.preventExplosion((ArmorStand) eVictim)) {
                        instance.getDebug().log("EntityEvents", "onEntityDamageByEntity", "Armor stand entity damage cancelled");
                        evt.setCancelled(true);
                        hmFlagsVictim = null;
                        return;
                    }
                }
            }
        }
        if (eVictim instanceof Creature)
        {
            if (evt.getEntity().getType() == EntityType.VILLAGER) {
                boolean removeDmg = false;
                if (evt.getCause() == DamageCause.FIRE || evt.getCause() == DamageCause.FIRE_TICK)
                {
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_VILLAGER_FIRE, hmFlagsVictim))
                    {
                        evt.getEntity().setFireTicks(0);
                        removeDmg = true;
                    }
                }
                if (eDamager instanceof Player)
                {
                    damagerHasBypass = PlayerFunctions.hasBypass((Player) eDamager);
                    damagerIsAllowed = domainBlockManager.isAllowed((Player) eDamager, eVictim.getLocation());

                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_VILLAGER_DAMAGE_BY_ALL, hmFlagsVictim))
                    {
                        if (!damagerHasBypass)
                        {
                            removeDmg = true;
                        }
                    }
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_VILLAGER_DAMAGE_BY_ALLOWED, hmFlagsVictim))
                    {
                        if (!damagerHasBypass)
                        {
                            removeDmg = true;
                        }
                    }
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_VILLAGER_DAMAGE_BY_NONALLOWED, hmFlagsVictim))
                    {
                        if (!damagerHasBypass && !damagerIsAllowed)
                        {
                            removeDmg = true;
                        }
                    }
                    if (removeDmg)
                    {
                        instance.getMessage().send((Player) eDamager,"villager_damage_nonallowed", "You cannot harm villagers here" , null, true);


                    }
                } else {
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_VILLAGER_DAMAGE_BY_NONPLAYER, hmFlagsVictim))
                    {
                        removeDmg = true;
                    }
                }
                if (removeDmg)
                {
                    instance.getDebug().log("EntityEvents", "onEntityDamageByEntity", "Villager damage removed");
                    evt.setCancelled(true);
                    hmFlagsVictim = null;
                    return;
                }
            } else {
                // Non-Villager Creature
                if (evt.getEntity() instanceof Monster) {
                    // Monsters
                    boolean removeDmg = false;
                    if (eDamager instanceof Player)
                    {
                        damagerHasBypass = PlayerFunctions.hasBypass((Player) eDamager);
                        damagerIsAllowed = domainBlockManager.isAllowed((Player) eDamager, eVictim.getLocation());
                        if (domainBlockManager.hasFlag(Flag.Type.PREVENT_MONSTER_DAMAGE_BY_ALL, hmFlagsVictim))
                        {
                            if (!damagerHasBypass)
                            {
                                removeDmg = true;
                            }
                        }
                        if (domainBlockManager.hasFlag(Flag.Type.PREVENT_MONSTER_DAMAGE_BY_ALLOWED, hmFlagsVictim))
                        {
                            if (!damagerHasBypass)
                            {
                                removeDmg = true;
                            }
                        }
                        if (domainBlockManager.hasFlag(Flag.Type.PREVENT_MONSTER_DAMAGE_BY_NONALLOWED, hmFlagsVictim))
                        {
                            if (!damagerHasBypass && !damagerIsAllowed)
                            {
                                removeDmg = true;
                            }
                        }
                        if (removeDmg)
                        {
                            instance.getMessage().send((Player) eDamager, "monster_damage_nonallowed", "You cannot harm monsters here", null, true);
                        }
                    } else {
                        if (domainBlockManager.hasFlag(Flag.Type.PREVENT_MONSTER_DAMAGE_BY_NONPLAYER, hmFlagsVictim))
                        {
                            removeDmg = true;
                        }
                    }
                    if (removeDmg)
                    {
                        instance.getDebug().log("EntityEvents", "onEntityDamageByEntity", "Monster damage removed - Victim: " + eVictim.getType() + " Attacker: " + eDamager.getType());
                        evt.setCancelled(true);
                        hmFlagsVictim = null;
                        return;
                    }
                } else {
                    // Non-Monster Creatures
                    if (eVictim.getType() == EntityType.VILLAGER || eVictim.getType() == EntityType.PILLAGER || eVictim.getType() == EntityType.ZOMBIE_VILLAGER) {
                        return;
                    }
                    boolean removeDmg = false;
                    if (eDamager instanceof Player)
                    {
                        damagerHasBypass = PlayerFunctions.hasBypass((Player) eDamager);
                        damagerIsAllowed = domainBlockManager.isAllowed((Player) eDamager, eVictim.getLocation());
                        if (domainBlockManager.hasFlag(Flag.Type.PREVENT_ANIMAL_DAMAGE_BY_ALL, hmFlagsVictim))
                        {
                            if (!damagerHasBypass)
                            {
                                removeDmg = true;
                            }
                        }
                        if (domainBlockManager.hasFlag(Flag.Type.PREVENT_ANIMAL_DAMAGE_BY_ALLOWED, hmFlagsVictim))
                        {
                            if (!damagerHasBypass)
                            {
                                removeDmg = true;
                            }
                        }
                        if (domainBlockManager.hasFlag(Flag.Type.PREVENT_ANIMAL_DAMAGE_BY_NONALLOWED, hmFlagsVictim))
                        {
                            if (!damagerHasBypass && !damagerIsAllowed)
                            {
                                removeDmg = true;
                            }
                        }
                        if (removeDmg)
                        {
                            instance.getMessage().send((Player) eDamager,"creature_damage_nonallowed", "You cannot harm creatures here" , null, true);
                        }
                    } else {
                        if (domainBlockManager.hasFlag(Flag.Type.PREVENT_ANIMAL_DAMAGE_BY_NONPLAYER, hmFlagsVictim))
                        {
                            removeDmg = true;
                        }
                    }
                    if (removeDmg)
                    {
                        instance.getDebug().log("EntityEvents", "onEntityDamageByEntity", "Non-Monster creature damage removed - Victim: " + eVictim.getType() + " Attacker: " + eDamager.getType());
                        evt.setCancelled(true);
                        hmFlagsVictim = null;
                        return;
                    }
                }
            }
        }
    }
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        if(evt.isCancelled()) {
            instance.getDebug().log("EntityEvents", "onEntityExplode", "Event was cancelled");
            return;
        }
        List<Block> blockListCopy = new ArrayList<Block>();
        blockListCopy.addAll(evt.blockList());
        for (Block block : blockListCopy) {
            if (domainBlockManager.isDomainBlock(block)) {
                instance.getDebug().log("EntityEvents", "onEntityExplode", "BlockEvents damage for Domain block at " + block.getLocation().toString() + " cancelled");
                evt.blockList().remove(block);
            }
            if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_EXPLOSION_DAMAGE))
            {
                instance.getDebug().log("EntityEvents", "onEntityExplode", "BlockEvents damage for block at " + block.getLocation().toString() + " cancelled");
                evt.blockList().remove(block);
            }
        }
    }
    @EventHandler
    public void onEntityInteract(EntityInteractEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        if(evt.isCancelled()) {
            instance.getDebug().log("EntityEvents", "onEntityInteract", "Event was cancelled");
            return;
        }

        if (evt.getBlock().getType() == Material.FARMLAND && evt.getEntity() instanceof LivingEntity)
        {
            if (!PlayerFunctions.isEntityPlayer(evt.getEntity()))
            {
                if (!evt.isCancelled())
                {
                    if (domainBlockManager.hasFlag(evt.getEntity().getLocation(), Flag.Type.PREVENT_CROP_DAMAGE_BY_NONPLAYER))
                    {
                        instance.getDebug().log("EntityEvents", "onEntityInteract", "Cancelling crop damage");
                        evt.setCancelled(true);
                        return;
                    }
                }
            }
        }

        if (evt.getEntity() instanceof Projectile) {
            Block b = evt.getBlock();
            List<Material> lstButtons = MaterialGroups.redstoneInteractions();
            if (lstButtons != null)
            {
                if (lstButtons.size() > 0)
                {
                    for (Material m : lstButtons)
                    {
                        if (b.getType() == m || b.getType() == Material.TNT)
                        {
                            if (domainBlockManager.hasFlag(evt.getEntity().getLocation(), Flag.Type.PREVENT_ARROW_INTERACT))
                            {
                                instance.getDebug().log("EntityEvents", "onEntityInteract", "Projectiles can't be used on buttons");
                                evt.setCancelled(true);
                            }
                        }

                    }
                }
            }
        }
    }
    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        //instance.getDebug().log("EntityEvents", "onEntityPickupItem", "onEntityPickupItem called");
        if(evt.isCancelled()) {
            //instance.getDebug().log("EntityEvents", "onEntityPickupItem", "Event was cancelled");
            return;
        }
        if (PlayerFunctions.isEntityPlayer(evt.getEntity()))
        {
            if (domainBlockManager.hasFlag(evt.getEntity().getLocation(), Flag.Type.PREVENT_ITEM_PICKUP_BY_NONALLOWED))
            {
                if (!domainBlockManager.isAllowed((Player) evt.getEntity(), evt.getEntity().getLocation())) {
                    instance.getDebug().log("EntityEvents", "onEntityPickupItem", "Item pickup by player cancelled");
                    evt.setCancelled(true);
                    return;
                }
            }
        }
        if (evt.getEntity().getType() == EntityType.VILLAGER)
        {
            if (domainBlockManager.hasFlag(evt.getEntity().getLocation(), Flag.Type.PREVENT_ITEM_PICKUP_BY_VILLAGER))
            {
                instance.getDebug().log("EntityEvents", "onEntityPickupItem", "Item pickup by villager cancelled");
                evt.setCancelled(true);
                return;
            }
        }
    }
    @EventHandler
    public void onEntityTame(EntityTameEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        if (evt.getOwner() instanceof Player) {
            if (domainBlockManager.hasFlag(evt.getEntity().getLocation(), Flag.Type.PREVENT_TAME_BY_NONALLOWED)) {
                Player player = (Player) evt.getOwner();
                if (!domainBlockManager.isAllowed(player, evt.getEntity().getLocation())) {
                    if (!PlayerFunctions.hasBypass(player)) {
                        instance.getMessage().send(player, "tame_deny", "&4You cannot tame animals here", null, true);
                        evt.setCancelled(true);
                    } else {
                        instance.getMessage().send(player, "tame_deny_bypass", "&4You cannot tame animals here, but have bypass mode enabled", null, true);
                    }
                }
            }
        }
    }
    @EventHandler
    public void onEntityBreed(EntityBreedEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        if (domainBlockManager.hasFlag(evt.getEntity().getLocation(), Flag.Type.PREVENT_BREEDING)) {
            evt.setCancelled(true);
        }
    }
    @EventHandler
    public void onSheepDye(SheepDyeWoolEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        if (domainBlockManager.hasFlag(evt.getEntity().getLocation(), Flag.Type.PREVENT_SHEEP_DYE)) {
            evt.setCancelled(true);
        }
    }
    @EventHandler
    public void onSheepRegrow(SheepRegrowWoolEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        if (domainBlockManager.hasFlag(evt.getEntity().getLocation(), Flag.Type.PREVENT_SHEEP_REGROW_WOOL)) {
            evt.setCancelled(true);
        }
    }
    @EventHandler
    public void onEntityMount(EntityMountEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        instance.getDebug().log("EntityEvents", "onEntityMount()", "Event fired. Entity: " + evt.getEntity().getType().name() + " Mount: " + evt.getMount().getType().name());
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            if (player != null) {
                if (evt.getMount() instanceof LivingEntity) {
                    instance.getDebug().log("EntityEvents", "onEntityMount()", "Player: " + player.getName() + " attempted to mount a " + evt.getMount().getName());
                    if (Animal.Mount.preventNonAllowed(player, evt.getMount().getLocation())) {
                        evt.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
    @EventHandler
    public void onPlayerLeashEntity(PlayerLeashEntityEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        instance.getDebug().log("EntityEvents", "onPlayerLeashEntity()", "Event fired. Entity: " + evt.getEntity().getType().name() + evt.getPlayer().getName());
        if (Animal.Lead.preventNonAllowed(evt.getPlayer(), evt.getEntity().getLocation())) {
            evt.setCancelled(true);
            return;
        }
    }
    public void onEntitySpawnEvent(EntitySpawnEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        instance.getDebug().log("EntityEvents", "onEntitySpawn", "Entity Spawn");
        if (evt.getEntityType() == EntityType.PRIMED_TNT) {
            instance.getDebug().log("EntityEvents", "onEntitySpawn", "Primed TNT");
            evt.setCancelled(true);
        }
    }
    @EventHandler
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        if (!event.isCancelled()) {
            if (event.getEntityType().equals(EntityType.ENDERMAN)) {
                if (domainBlockManager.hasFlag(event.getEntity().getLocation(), Flag.Type.PREVENT_ENDERMAN_GRIEF)) {
                    instance.getDebug().log("EntityEvents", "onEntityChangeBlockEvent", "Cancelling enderman grief");
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        if(event.getEntity() instanceof Enderman && event.getTarget() instanceof Player) {
            if(event.getReason() == EntityTargetEvent.TargetReason.CLOSEST_PLAYER) {
                if (domainBlockManager.hasFlag(event.getTarget().getLocation(), Flag.Type.PREVENT_ENDERMAN_AGGRESSION)) {
                    instance.getDebug().log(
                            "EntityEvents",
                            "onEntityChangeBlockEvent",
                            "Preventing Enderman aggression towards " + ((Player) event.getTarget()).getDisplayName()
                    );
                    event.setCancelled(true);
                }
            }
        }
    }
}
