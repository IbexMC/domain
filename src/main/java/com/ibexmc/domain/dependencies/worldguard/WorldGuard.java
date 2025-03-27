package com.ibexmc.domain.dependencies.worldguard;

import com.ibexmc.internal.messaging.Error;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.ibexmc.domain.Domain;

import java.util.Objects;

public class WorldGuard {

    Domain instance;

    public WorldGuard(Domain instance) {
        this.instance = instance;
    }

    /**
     * Checks if WorldGuard will allow a player can build in the location
     * @param player Player to check
     * @param location Location to check
     * @param flag Flag to check
     * @return If true, flag is allowed by player at the location
     */
    public boolean flagAllowed(Player player, Location location, StateFlag flag) {
        if (player == null) {
            instance.getError().save("001", "WorldGuard", "flagAllowed", "Invalid parameters", "Player is null", Error.Severity.WARN, null);
            return true;
        }
        if (location == null) {
            instance.getError().save("002", "WorldGuard", "flagAllowed", "Invalid parameters", "Location is null", Error.Severity.WARN, null);
            return true;
        }
        if (flag == null) {
            instance.getError().save("003", "WorldGuard", "flagAllowed", "Invalid parameters", "Flag is null", Error.Severity.WARN, null);
            return true;
        }
        try {
            if(Domain.getInstance().getDependencies().hasWorldGuard()){
                LocalPlayer wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
                com.sk89q.worldedit.world.World wgWorld = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()));
                com.sk89q.worldedit.util.Location wgLocation = BukkitAdapter.adapt(location);
                com.sk89q.worldguard.WorldGuard worldGuard = Domain.getInstance().getDependencies().getWorldGuard();
                RegionQuery query = worldGuard.getPlatform().getRegionContainer().createQuery();
                if (!worldGuard.getPlatform().getSessionManager().hasBypass(wgPlayer, wgWorld)) {
                    return query.testState(wgLocation, wgPlayer, flag);
                }
            }
        } catch (Exception ex) {
            instance.getError().save("004", "WorldGuard", "flagAllowed", "Unexpected Error", ex.getMessage(), Error.Severity.WARN, ex.getStackTrace());
            return true;
        }
        return true; // If we reach this point, something didn't go well with WorldGuard, so return true
    }

    /**
     * Checks if WorldGuard will allow a player can build in the location
     * @param player Player to check
     * @param location Location to check
     * @return If true, player can build at the location
     */
    public boolean canBuild(Player player, Location location) {
        if (player == null) {
            instance.getError().save("001", "WorldGuard", "flagAllowed", "Invalid parameters", "Player is null", Error.Severity.WARN, null);
            return true;
        }
        if (location == null) {
            instance.getError().save("002", "WorldGuard", "flagAllowed", "Invalid parameters", "Location is null", Error.Severity.WARN, null);
            return true;
        }
        try {
            if(Domain.getInstance().getDependencies().hasWorldGuard()){
                LocalPlayer wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
                com.sk89q.worldedit.world.World wgWorld = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()));
                com.sk89q.worldedit.util.Location wgLocation = BukkitAdapter.adapt(location);
                com.sk89q.worldguard.WorldGuard worldGuard = Domain.getInstance().getDependencies().getWorldGuard();
                RegionQuery query = worldGuard.getPlatform().getRegionContainer().createQuery();
                if (!worldGuard.getPlatform().getSessionManager().hasBypass(wgPlayer, wgWorld)) {
                    return query.testState(wgLocation, wgPlayer, Flags.BUILD);
                }
            }
        } catch (Exception ex) {
            instance.getError().save("003", "WorldGuard", "canBuild", "Unexpected Error", ex.getMessage(), Error.Severity.WARN, ex.getStackTrace());
            return true;
        }
        return true; // If we reach this point, something didn't go well with WorldGuard, so return true (can build)
    }

    /**
     * Checks if WorldGuard will allow various types of damage
     * PVP is assumed true, Mob damage is assumed true, explosion damage is assumed true, fall damage is assumed true
     * @param player Player to check
     * @param location Location to check
     * @return If true, player can receive damage
     */
    public boolean canReceiveDamage(Player player, Location location) {
        if (player == null) {
            instance.getError().save("001", "WorldGuard", "canReceiveDamage", "Invalid parameters", "Player is null", Error.Severity.WARN, null);
            return true;
        }
        if (location == null) {
            instance.getError().save("002", "WorldGuard", "canReceiveDamage", "Invalid parameters", "Location is null", Error.Severity.WARN, null);
            return true;
        }
        return canReceiveDamage(player, location, false, false, false, false);
    }

    /**
     * Checks if WorldGuard will allow various types of damage
     * Mob damage is assumed true, explosion damage is assumed true, fall damage is assumed true
     * @param player Player to check
     * @param location Location to check
     * @param pvp Check for PVP
     * @return If true, player can receive damage
     */
    public boolean canReceiveDamage(Player player, Location location, boolean pvp) {
        if (player == null) {
            instance.getError().save("001", "WorldGuard", "canReceiveDamage", "Invalid parameters", "Player is null", Error.Severity.WARN, null);
            return true;
        }
        if (location == null) {
            instance.getError().save("002", "WorldGuard", "canReceiveDamage", "Invalid parameters", "Location is null", Error.Severity.WARN, null);
            return true;
        }
        return canReceiveDamage(player, location, pvp, false, false, false);
    }

    /**
     * Checks if WorldGuard will allow various types of damage
     * Explosion damage is assumed true, fall damage is assumed true
     * @param player Player to check
     * @param location Location to check
     * @param pvp Check for PVP
     * @param mobDamage Check for mob damage
     * @return If true, player can receive damage
     */
    public boolean canReceiveDamage(Player player, Location location, boolean pvp, boolean mobDamage) {
        if (player == null) {
            instance.getError().save("001", "WorldGuard", "canReceiveDamage", "Invalid parameters", "Player is null", Error.Severity.WARN, null);
            return true;
        }
        if (location == null) {
            instance.getError().save("002", "WorldGuard", "canReceiveDamage", "Invalid parameters", "Location is null", Error.Severity.WARN, null);
            return true;
        }
        return canReceiveDamage(player, location, pvp, mobDamage, false, false);
    }

    /**
     * Checks if WorldGuard will allow various types of damage
     * Fall damage is assumed true
     * @param player Player to check
     * @param location Location to check
     * @param pvp Check for PVP
     * @param mobDamage Check for mob damage
     * @param explosionDamage Check for explosion damage
     * @return If true, player can receive damage
     */
    public boolean canReceiveDamage(Player player, Location location, boolean pvp, boolean mobDamage, boolean explosionDamage) {
        if (player == null) {
            instance.getError().save("001", "WorldGuard", "canReceiveDamage", "Invalid parameters", "Player is null", Error.Severity.WARN, null);
            return true;
        }
        if (location == null) {
            instance.getError().save("002", "WorldGuard", "canReceiveDamage", "Invalid parameters", "Location is null", Error.Severity.WARN, null);
            return true;
        }
        return canReceiveDamage(player, location, pvp, mobDamage, explosionDamage, false);
    }

    /**
     * Checks if WorldGuard will allow various types of damage
     * @param player Player to check
     * @param location Location to check
     * @param pvp Check for PVP
     * @param mobDamage Check for mob damage
     * @param explosionDamage Check for explosion damage
     * @param fallDamage Check for fall damage
     * @return If true, player can receive damage
     */
    public boolean canReceiveDamage(Player player, Location location, boolean pvp, boolean mobDamage, boolean explosionDamage, boolean fallDamage) {
        if (player == null) {
            instance.getError().save("001", "WorldGuard", "canReceiveDamage", "Invalid parameters", "Player is null", Error.Severity.WARN, null);
            return true;
        }
        if (location == null) {
            instance.getError().save("002", "WorldGuard", "canReceiveDamage", "Invalid parameters", "Location is null", Error.Severity.WARN, null);
            return true;
        }
        try {
            if(Domain.getInstance().getDependencies().hasWorldGuard()){
                WorldGuardPlugin worldGuardPlugin = Domain.getInstance().getDependencies().getWorldGuardPlugin();
                LocalPlayer wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
                com.sk89q.worldedit.world.World wgWorld = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()));
                com.sk89q.worldedit.util.Location wgLocation = BukkitAdapter.adapt(location);
                com.sk89q.worldguard.WorldGuard worldGuard = Domain.getInstance().getDependencies().getWorldGuard();
                RegionQuery query = worldGuard.getPlatform().getRegionContainer().createQuery();
                if (!worldGuard.getPlatform().getSessionManager().hasBypass(wgPlayer, wgWorld)) {
                    boolean result = true;
                    if (pvp && !query.testState(wgLocation, wgPlayer, Flags.PVP)) {
                        result = false;
                    }
                    if (
                            result && mobDamage && (
                                    !query.testState(wgLocation, wgPlayer, Flags.MOB_DAMAGE) ||
                                    !query.testState(wgLocation, wgPlayer, Flags.WITHER_DAMAGE) ||
                                    !query.testState(wgLocation, wgPlayer, Flags.GHAST_FIREBALL)
                            )
                    ) {
                        result = false;
                    }
                    if (
                            result && explosionDamage && (
                                    !query.testState(wgLocation, wgPlayer, Flags.CREEPER_EXPLOSION) ||
                                    !query.testState(wgLocation, wgPlayer, Flags.OTHER_EXPLOSION)
                            )
                    ) {
                        result = false;
                    }
                    if (result && fallDamage && !query.testState(wgLocation, wgPlayer, Flags.FALL_DAMAGE)) {
                        result = false;
                    }
                    return result;
                }
            }
        } catch (Exception ex) {
            instance.getError().save("001", "WorldGuard", "canReceiveDamage", "Unexpected Error", ex.getMessage(), Error.Severity.WARN, ex.getStackTrace());
            return true;
        }
        return true; // If we reach this point, something didn't go well with WorldGuard, so return true (can receive damage)
    }
}
