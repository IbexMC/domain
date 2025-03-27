package com.ibexmc.domain.events.bukkit;

import java.util.Map;

import com.ibexmc.domain.helper.MaterialGroups;
import org.bukkit.Chunk;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;

public class CreatureSpawnEvents implements Listener {
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        if (!Domain.getInstance().getData().spawnEventStatus) {
            return;
        }

        Chunk chunk = evt.getLocation().getChunk();

        if (!chunk.isLoaded()) {
            //instance.getDebug().log("CreatureSpawnEvents", "onCreatureSpawn", "Chunk is not yet loaded");
            return;
        }

        if (!domainBlockManager.inField(evt.getLocation())) {
            return;
        }
        EntityType entityType = evt.getEntityType();

        instance.getDebug().log("CreatureSpawnEvents", "onCreatureSpawn", "&eEntity Type: " + entityType.name() + " Reason: " + evt.getSpawnReason());

        boolean cancelEvent = false;

        // Get the flags at this location - that way we can use the domainBlockManager.hasFlag that has the flags
        // already defined, and we can avoid looking them up again and again.
        Map<Flag.Type, Boolean> hmFlags = domainBlockManager.getFlagsAtLocation(evt.getLocation());

        if (MaterialGroups.hostileMobs().contains(entityType)) {
            //instance.getDebug().log("CreatureSpawnEvents", "onCreatureSpawn", "&eEntity is a hostile mob");
            if (domainBlockManager.hasFlag(Flag.Type.PREVENT_SPAWN_HOSTILE, hmFlags)) {
                cancelEvent = true;
            }
        } else {
            //instance.getDebug().log("CreatureSpawnEvents", "onCreatureSpawn", "&eEntity is not a hostile mob");
            if (domainBlockManager.hasFlag(Flag.Type.PREVENT_SPAWN_NONHOSTILE, hmFlags)) {
                cancelEvent = true;
            }
        }

        if (!cancelEvent) {
            // If we haven't already cancelled the event, lets check per mob type
            switch (evt.getEntityType()) {
                case PHANTOM:
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_SPAWN_PHANTOM, hmFlags)) {
                        cancelEvent = true;
                        break;
                    }
                    break;
                case PILLAGER:
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_SPAWN_PILLAGER, hmFlags)) {
                        cancelEvent = true;
                        break;
                    }
                    break;
                case RAVAGER:
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_SPAWN_RAVAGER, hmFlags)) {
                        cancelEvent = true;
                        break;
                    }
                    break;
                case EVOKER:
                case EVOKER_FANGS:
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_SPAWN_EVOKER, hmFlags)) {
                        cancelEvent = true;
                        break;
                    }
                    break;
                case ILLUSIONER:
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_SPAWN_ILLUSIONER, hmFlags)) {
                        cancelEvent = true;
                        break;
                    }
                    break;
                case VEX:
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_SPAWN_VEX, hmFlags)) {
                        cancelEvent = true;
                        break;
                    }
                    break;
                case VINDICATOR:
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_SPAWN_VINDICATOR, hmFlags)) {
                        cancelEvent = true;
                        break;
                    }
                    break;
                case TRADER_LLAMA:
                case WANDERING_TRADER:
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_SPAWN_WANDERING_TRADER, hmFlags)) {
                        cancelEvent = true;
                        break;
                    }
                    break;
                case WARDEN:
                    if (domainBlockManager.hasFlag(Flag.Type.PREVENT_SPAWN_WARDEN, hmFlags)) {
                        cancelEvent = true;
                        break;
                    }
                    break;
                default:
                    break;
            }
        }

        if (!cancelEvent) {
            // If we haven't already cancelled the event, lets check per spawn reason
            switch (evt.getSpawnReason())
            {
//				case DEFAULT:
//					if (evt.getEntityType().equals(EntityType.ARMOR_STAND))
//					{
//						instance.getDebug().log("CreatureSpawnEvents", "onCreatureSpawn", "Armor stand spawned - not cancelled");
//						if (domainBlockManager.hasFlag(Flag.Type.PREVENT_BUILD, hmFlags)) {
//							cancelEvent = true;
//						}
//					}
//				break;
//				case SPAWNER_EGG:
//					if (domainBlockManager.hasFlag(evt.getLocation(), Flag.Type.PREVENT_SPAWNER_EGG))
//					{
//						cancelEvent = true;
//						break;
//					}
//					break;
                case BREEDING:
                    if (domainBlockManager.hasFlag(evt.getLocation(), Flag.Type.PREVENT_BREEDING))
                    {
                        cancelEvent = true;
                        break;
                    }
                    break;
                case BUILD_IRONGOLEM:
                    if (domainBlockManager.hasFlag(evt.getLocation(), Flag.Type.PREVENT_BUILD_IRON_GOLEM))
                    {
                        cancelEvent = true;
                        break;
                    }
                    break;
                case BUILD_SNOWMAN:
                    if (domainBlockManager.hasFlag(evt.getLocation(), Flag.Type.PREVENT_BUILD_SNOWMAN))
                    {
                        cancelEvent = true;
                        break;
                    }
                    break;
                default:
                    if (domainBlockManager.hasFlag(evt.getLocation(), Flag.Type.PREVENT_SPAWN))
                    {
                        cancelEvent = true;
                        break;
                    }
                    break;
            }
        }

        // Clear the flags at this location
        hmFlags = null;

        if (cancelEvent) {
            evt.setCancelled(true);
            return;
        }


    }
}
