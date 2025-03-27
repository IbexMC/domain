package com.ibexmc.domain.domainblock.spawner;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.util.DomainParticles;
import com.ibexmc.internal.functions.NumberFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import com.ibexmc.domain.domainblock.DomainBlock;

import java.util.Set;

public class SpawnerManager {

    /**
     * Gets the light level
     * @param location Location to check
     * @return Light Level
     */
    private static byte lightLevel(Location location) {
        return location.getBlock().getLightLevel();
    }

    /**
     * Checks if the location is in a slime chunk
     * @param location Location to check
     * @return If true, location is in a slime chunk
     */
    private static boolean slimeChunk(Location location) {
        return location.getBlock().getChunk().isSlimeChunk();
    }

    /**
     * Checks if the location is in or above water
     * @param location Location to check
     * @return If true, location is in or above water
     */
    private static boolean inOrAboveWater(Location location) {
        if (location.getBlock().getType() == Material.WATER) {
            return true;
        }
        for (int i =location.getBlockY();i>0;i--) {
            Location nextLocation = new Location(location.getWorld(), location.getBlockX(), i, location.getBlockZ());
            if (nextLocation.getBlock().getType() == Material.WATER) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the location is above a grass block
     * @param location Location to check
     * @return If true, location is above grass
     */
    private static boolean aboveGrass(Location location) {
        for (int i =location.getBlockY();i>0;i--) {
            Location nextLocation = new Location(location.getWorld(), location.getBlockX(), i, location.getBlockZ());
            if (nextLocation.getBlock().getType() == Material.GRASS_BLOCK) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the maximum number of entities is reached
     * @param location Location to check
     * @param spawner Spawner to get entity type from
     * @return If true, maximum number is reached
     */
    private static boolean maxEntityTypeReached(Location location, Spawner spawner) {
        int maxCount = spawner.maxEntities();
        int distance = spawner.maxDistance();
        int entityTypeCount = 0;
        Set<Entity> entities = WorldFunctions.getNearbyEntities(location, distance, distance, distance);
        if (entities != null) {
            for (Entity entity : entities) {
                if (entity.getType().equals(spawner.entityType())) {
                    entityTypeCount++;
                }
            }
        }
        if (entityTypeCount >= maxCount) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the player is in range of the spawner
     * Player must also be in survival mode
     * @param location Location to check
     * @param spawner Spawner to check
     * @return If true, player is in range
     */
    private static boolean playerInRange(Location location, Spawner spawner) {
        int distance = spawner.maxDistance();
        Set<Entity> entities = WorldFunctions.getNearbyEntities(location, distance, distance, distance);
        if (entities != null) {
            for (Entity entity : entities) {
                if (entity.getType().equals(EntityType.PLAYER)) {
                    Player player = (Player) entity;
                    if (player != null) {
                        if (player.getGameMode().equals(GameMode.SURVIVAL)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gets a random location within the cuboid
     * @param domainBlock DomainBlock to get cuboid from
     * @return Random Location
     */
    private static Location randomLocationInRegion(DomainBlock domainBlock) {
        return WorldFunctions.randomLocationInCuboid(domainBlock.getCorner1(), domainBlock.getCorner2());
    }

    /**
     * Compares the spawn chance against a random number
     * and returns true if it is less than the random number
     * @param chance Chance to compare against
     * @return If true, ok to spawn
     */
    private static boolean spawnChance(int chance) {
        int spawnChance = NumberFunctions.random(0, 100);
        return (spawnChance <= chance);
    }

    /**
     * Spawns an entity type at the specified location
     * @param location Location to spawn at
     * @param entityType Entity Type to spawn
     */
    private static void spawnEntity(Location location, EntityType entityType, boolean baby) {
        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(
               location, entityType
        );
        if (baby) {
            if (entity instanceof Ageable) {
                ((Ageable) entity).setBaby();
            }
        }
    }

    /**
     * Returns true if the baby chance if < random number
     * @param spawner Spawner to check
     * @return If true, spawn as baby
     */
    private static boolean babyChance(Spawner spawner) {
        int babyChance = NumberFunctions.random(0, 100);
        return (babyChance <= spawner.baby());
    }

    /**
     * Checks if the location is either air or liquid
     * If not, it would indicate that the entity will spawn inside a block and die
     * @param location Location to check
     * @return If true, location is inside a non-air/liquid block
     */
    private static boolean inBlock(Location location) {
        if (location.getBlock().isLiquid()) {
            return false;
        }
        if (location.getBlock().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Performs the spawn pre-requisite checks, and runs spawnEntity if appropriate
     * @param domainBlock DomainBlock to check for
     * @param spawner Spawner to check for
     */
    public static void spawn(DomainBlock domainBlock, Spawner spawner) {

        Domain instance = Domain.getInstance();
        int maxTries = 10;
        for(int i = 0; i < maxTries; ++i) {
            boolean okSpawn = true;
            Location spawnLocation = randomLocationInRegion(domainBlock);
            //if (Domain.instance().data().isPaper()) {
            //    if (!spawnLocation.isChunkLoaded()) {
            //        instance.getDebug().log("SpawnerManager", "spawn", "&4Not Spawning - Chunk not loaded for placedId = " + domainBlock.placedId());
            //        break;
            //    }
            //}
            int lightLevel = lightLevel(spawnLocation);
            String errs = "";
            if (spawnLocation == null) {
                errs = errs + "Spawn Location is null; ";
                okSpawn = false;
            } else {
                if (!playerInRange(spawnLocation, spawner)) {
                    instance.getDebug().log("SpawnerManager", "spawn", "&4Not Spawning - &fNo player in range");
                    break;
                }
                if (!spawnChance(spawner.chance())) {
                    instance.getDebug().log("SpawnerManager", "spawn", "&4Not Spawning - &fSpawn chance not in range");
                    break;
                }
                if (maxEntityTypeReached(spawnLocation, spawner)) {
                    instance.getDebug().log("SpawnerManager", "spawn", "&4Not Spawning - &fMaximum number of this entity type has been reached");
                    break;
                }
                if (spawner.requireWater()) {
                    if (!inOrAboveWater(spawnLocation)) {
                        errs = errs + "Spawn Location requires stationary water; ";
                        okSpawn = false;
                    }
                }
                if (spawner.requireSlimeChunk()) {
                    if (!slimeChunk(spawnLocation)) {
                        errs = errs + "Spawn Location requires a Slime chunk; ";
                        okSpawn = false;
                    }
                }
                if (spawner.requireGrass()) {
                    if (!aboveGrass(spawnLocation)) {
                        errs = errs + "Spawn location requires grass below it; ";
                        okSpawn = false;
                    }
                }
                if (lightLevel < spawner.minLight()) {
                    errs = errs + "Light level is " + lightLevel + " but must be >= than " + spawner.minLight() + ";";
                    okSpawn = false;
                }
                if (lightLevel >= spawner.maxLight()) {
                    errs = errs + "Light level is " + lightLevel + " but must be <= than " + spawner.maxLight() + ";";
                    okSpawn = false;
                }
                if (inBlock(spawnLocation)) {
                    errs = errs + "Spawn location is not air/liquid; ";
                    okSpawn = false;
                }
                if (okSpawn) {
                    instance.getDebug().log("SpawnerManager", "spawn", "&2Spawning " + spawner.entityType().name() + "for placedId = " + domainBlock.getPlacedId());
                    spawnEntity(spawnLocation, spawner.entityType(), babyChance(spawner));
                    DomainParticles.flame(domainBlock.getBlockLocation());
                    break;
                } else {
                    instance.getDebug().log("SpawnerManager", "spawn", "&4Not Spawning - &f" + errs);
                }
            }
        }
    }
}
