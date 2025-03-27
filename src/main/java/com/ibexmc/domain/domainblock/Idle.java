package com.ibexmc.domain.domainblock;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.internal.functions.TimeFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.Map;

public class Idle {

    private Player player = null;
    private Location location = null;
    int unmoved = 0;
    private Location lastKickedFrom = null;
    public enum BackResult {
        SUCCESS,
        NO_PRIOR_LOCATION,
        TOO_FAR_FROM_KICKED_TO,
        LOCATION_NOT_SAFE
    }

    public Idle(Player player) {
        this.player = player;
        this.location = player.getLocation();
        this.unmoved = 0;
    }

    public void updateLocation() {
        Domain instance = Domain.getInstance();

        if (WorldFunctions.match(this.player.getLocation().clone(), this.location.clone(), true)) {
            // Player is unmoved
            instance.getDebug().log("Idle", "updateLocation", "Player is unmoved! - Location updated\nPlayer:" + this.player.getName() + "\nLocation: " + WorldFunctions.simpleReadable(player.getLocation()) + "\nUnmoved: " + unmoved);
            unmoved++;
        } else if (this.player.isInsideVehicle() && this.player.getLocation().distance(this.location) < 5) {
            // If player is inside a vehicle, consider unmoved if within 5 blocks
            // set unmoved = 0 if enter/exit vehicle
            unmoved++;
        } else {
            this.location = player.getLocation().clone();
            unmoved = 0;
        }


        instance.getDebug().log("Idle", "updateLocation", "Location updated\nPlayer:" + this.player.getName() + "\nLocation: " + WorldFunctions.simpleReadable(player.getLocation()) + "\nUnmoved: " + unmoved);
    }

    public int getUnmoved() {
        return this.unmoved;
    }

    public boolean inIdleField() {
        DomainBlockManager domainBlockManager = new DomainBlockManager(Domain.getInstance());
        Map<Flag.Type, Boolean> hmFlags = domainBlockManager.getFlagsAtLocation(this.player.getLocation());
        if (hmFlags != null) {
            if (hmFlags.containsKey(Flag.Type.KICK_OUT_IDLE_NONALLOWED)) {
                return hmFlags.get(Flag.Type.KICK_OUT_IDLE_NONALLOWED);
            }
        }
        return false;
    }

    public boolean fieldOldEnough() {
        DomainBlockManager domainBlockManager = new DomainBlockManager(Domain.getInstance());
        DomainBlock domainBlock = domainBlockManager.getCurrentField(this.player);
        if (domainBlock != null) {
            Timestamp placed = TimeFunctions.timestampFromLong(domainBlock.getPlacedLongTimestamp());
            Timestamp placed20 = TimeFunctions.addMinutes(
                    placed,
                    Domain.getInstance().config().getNewFieldKickCooldown()
            );
            if (TimeFunctions.after(TimeFunctions.now(), placed20)) {
                return true;
            }
        }
        return false;
    }

    public void warnPlayer() {
        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(Domain.getInstance());
        DomainBlock domainBlock = domainBlockManager.getCurrentField(this.player);
        if (domainBlock == null) {
            return;
        }
        if (!domainBlock.hasAccess(this.player)) {
            instance.getMessage().send(
                    player,
                    "idle_kick_warning",
                    "&4You have been idle in this Domain field for too long and will be kicked out of it shortly.  To avoid this, move away from your current location",
                    null,
                    true
            );
        }
    }
    public void kickPlayer(boolean allowBack) {
        // TODO: Try to kick outside the field
        // TODO: If that fails, then kick to spawn
        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        DomainBlock domainBlock = domainBlockManager.getCurrentField(this.player);
        if (domainBlock == null) {
            return;
        }
        if (!domainBlock.hasAccess(this.player)) {
            if (allowBack) {
                this.lastKickedFrom = player.getLocation().clone();
            }
            if (domainBlockManager.kickFromField(this.player, domainBlock)) {
                instance.getMessage().send(
                        player,
                        "idle_kick",
                        "&4You have been kicked out of the Domain field for idling",
                        null,
                        true
                );
            }
        }
    }


    public BackResult back() {
        if (this.lastKickedFrom != null) {
            if (WorldFunctions.safeTeleport(this.lastKickedFrom)) {
                if (player.getLocation().getWorld().getSpawnLocation().distance(this.player.getLocation()) < 5) {
                    player.teleport(this.lastKickedFrom);
                    this.lastKickedFrom = null;
                    // Teleporting back
                    return BackResult.SUCCESS;
                } else {
                    // Unable to go back as you have gone too far from where you were kicked to
                    return BackResult.TOO_FAR_FROM_KICKED_TO;
                }
            } else {
                return BackResult.LOCATION_NOT_SAFE;
            }
        }
        return BackResult.NO_PRIOR_LOCATION;
    }
}
