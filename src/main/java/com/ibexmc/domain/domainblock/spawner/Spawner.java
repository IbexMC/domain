package com.ibexmc.domain.domainblock.spawner;

import org.bukkit.entity.EntityType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Spawner {

    private EntityType entityType;
    private int amount = 0;
    private int multiAmountMax = 0;
    private int chance = 0;
    private int multiChance = 0;
    private Set<UUID> worlds = new HashSet<>();
    private int minLight = 0;
    private int maxLight = 0;
    private boolean requireSlimeChunk = false;
    private boolean requireWater = false;
    private boolean requireGrass = false;
    private int maxDistance = 16;
    private int maxEntities = 20;
    private int baby = 0;

    public Spawner(EntityType entityType, int amount, int multiAmountMax, int chance, int multiChance, Set<UUID> worlds, int minLight, int maxLight, boolean requireSlimeChunk, boolean requireWater, boolean requireGrass, int maxDistance, int maxEntities, int baby) {
        this.entityType = entityType;
        this.amount = amount;
        this.multiAmountMax = multiAmountMax;
        this.chance = chance;
        this.multiChance = multiChance;
        this.worlds = worlds;
        this.minLight = minLight;
        this.maxLight = maxLight;
        this.requireSlimeChunk = requireSlimeChunk;
        this.requireWater = requireWater;
        this.requireGrass = requireGrass;
        this.maxDistance = maxDistance;
        this.maxEntities = maxEntities;
        this.baby = baby;
        if (this.amount > 10) {
            this.amount = 10;
        }
        if (this.multiAmountMax > 10) {
            this.multiAmountMax = 10;
        }
    }

    /**
     * Gets the EntityType assigned to this Spawner
     * @return Entity Type
     */
    public EntityType entityType() {
        return entityType;
    }

    /**
     * Gets the amount of the entity to spawn each cycle
     * @return Entity Spawn Amount
     */
    public int amount() {
        return amount;
    }

    /**
     * Gets the maximum amount of the entity to spawn when multi-entity conditions are met
     * The actual number spawned will be a number between amount and multiAmountMax
     * @return
     */
    public int multiAmountMax() { return multiAmountMax; }

    /**
     * Chance 0-100 of the mob spawning in
     * @return Spawn Chance
     */
    public int chance() {
        return chance;
    }

    /**
     * Chance 0-100 of multiple mobs spawning in
     * @return Multiple Spawn Chance
     */
    public int multiChance() { return multiChance; }

    /**
     * HashSet of World Unique Identifiers the entities are allowed to spawn in
     * @return HashSet of World Unique Identifiers
     */
    public Set<UUID> worlds() {
        return worlds;
    }

    /**
     * Minimum light level allowed to spawn in
     * @return Minimum light level
     */
    public int minLight() {
        return minLight;
    }

    /**
     * Maximum light level allowed to spawn in
     * @return Maximum light level
     */
    public int maxLight() {
        return maxLight;
    }

    /**
     * If true, location must be in a spawn chunk to allow spawning
     * @return Requires Slime Chunk
     */
    public boolean requireSlimeChunk() {
        return requireSlimeChunk;
    }

    /**
     * If true, location must be in or above water to allow spawning
     * @return Requires in or above water
     */
    public boolean requireWater() {
        return requireWater;
    }

    /**
     * If true, location must be above grass to allow spawning
     * @return Requires above grass
     */
    public boolean requireGrass() { return requireGrass; }

    /**
     * Distance in blocks to check for maximum entity count
     * @return Maximum Entity Count Distance
     */
    public int maxDistance() {
        return maxDistance;
    }

    /**
     * Maximum number of this entity type that is allow to exist within the set distance
     * @return Maximum Entity Count for this type
     */
    public int maxEntities() {
        return maxEntities;
    }

    /**
     * Chance out of 100 that the mob will spawn as a baby if it is ageable
     * @return Baby Chance
     */
    public int baby() { return baby; }

}
