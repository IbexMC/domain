package com.ibexmc.domain.api.events.player;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;

public final class PlayerShear extends BasePlayer implements Cancellable {

    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    private Entity entity;

    /**
     * PlayerShear
     * This event is called when a player shears an entity in a Domain field
     *
     * @param player The player that ran the command
     * @param entity The entity being sheared
     *
     */
    public PlayerShear(Player player, Entity entity) {

        super(player, entity);

        Domain instance = super.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        this.entity = entity;

        instance.getDebug().log("Domain_PlayerShearEvent", "PlayerShear", "PlayerShear called");

        boolean hasBypass = PlayerFunctions.hasBypass(PlayerFunctions.playerFromEntity(player));
        boolean isAllowed = domainBlockManager.isAllowed(player, player.getLocation());

        if (domainBlockManager.hasFlag(entity.getLocation(), Flag.Type.PREVENT_SHEAR_BY_NONALLOWED)) {
            if (!isAllowed) {
                if (!hasBypass) {
                    this.cancelled = true;
                    this.cancelReason = "Player is not allowed to shear entities here.  Event has been cancelled";
                    instance.getMessage().send(player, "shear_deny", "&4You cannot shear animals here", null, true);
                    return;
                } else {
                    instance.getMessage().send(player, "shear_deny_bypass", "&4You cannot shear animals here, but have bypass mode enabled", null, true);
                    instance.getDebug().log(
                            "PlayerShear",
                            "PlayerShear(Player, Entity)",
                            player.getName() +
                                    " sheared an " +
                                    entity.getType().name() +
                                    " at " +
                                    WorldFunctions.simpleReadable(entity.getLocation())
                    );
                }
            }
        }
    }



    public Entity getShearedEntity() {
        return this.entity;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

}
