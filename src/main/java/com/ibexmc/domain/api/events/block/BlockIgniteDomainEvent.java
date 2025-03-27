package com.ibexmc.domain.api.events.block;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.helper.DomainFunctions;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;

public final class BlockIgniteDomainEvent extends BaseBlock implements Cancellable {

    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    /**
     * BlockIgniteDomainEvent
     * This event is called when a block is broken
     *
     * @param domainBlock The domain block where the block is being broken
     * @param block The block that is being broken
     * @param igniteCause Cause of the fire ignition
     * @param player The player that ignited the fire (if applicable)
     *
     */
    public BlockIgniteDomainEvent(DomainBlock domainBlock, Block block, IgniteCause igniteCause, Player player) {

        super(domainBlock, block, igniteCause, player);

        Domain instance = super.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        instance.getDebug().log("Domain_BlockIgniteEvent", "BlockIgniteDomainEvent", "BlockIgniteDomainEvent called");


        if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_FIRE_IGNITE)) {
            switch (igniteCause) {
                case FLINT_AND_STEEL:
                    if (player != null) {
                        if (PlayerFunctions.hasBypass(player)) {
                            // Player has bypass so can start fires here
                            DomainFunctions.bypassLog(
                                    instance,
                                    player,
                                    "Block ignited " + block.getType().name() + " at " + WorldFunctions.simpleReadable(block.getLocation())
                            );
                            this.cancelled = false;
                            break;
                        }
                        if (!domainBlockManager.isAllowedToField(player, domainBlock)) {
                            this.cancelled = true;
                            this.cancelReason = "Block ignite from player " +
                                    player.getName() + " at " +
                                    WorldFunctions.simpleReadable(block.getLocation()) +
                                    " caused by " + igniteCause.name() + " is cancelled.";
                            break;
                        }
                    } else {
                        this.cancelled = true;
                        this.cancelReason = "Block ignite at " +
                                WorldFunctions.simpleReadable(block.getLocation()) +
                                " caused by " + igniteCause.name() + " is cancelled.";
                    }
                    break;
                case ARROW:
                case ENDER_CRYSTAL:
                case EXPLOSION:
                case FIREBALL:
                case LAVA:
                case LIGHTNING:
                case SPREAD:
                    // Allowing for multiple causes here in case individual flags are added later
                    this.cancelled = true;
                    this.cancelReason = "Block ignite at " +
                            WorldFunctions.simpleReadable(block.getLocation()) +
                            " caused by " + igniteCause.name() + " is cancelled.";
                    break;
                default:
                    break;
            }
        }
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
