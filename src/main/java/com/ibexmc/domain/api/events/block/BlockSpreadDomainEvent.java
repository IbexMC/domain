package com.ibexmc.domain.api.events.block;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.util.functions.GameFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;

public final class BlockSpreadDomainEvent extends BaseBlock implements Cancellable {

    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    /**
     * BlockSpreadDomainEvent
     * This event is called when a block spreads
     *
     * @param sourceBlock The source block that is being spread
     * @param block The block that is spreading
     *
     */
    public BlockSpreadDomainEvent(Block sourceBlock, Block block) {

        super(sourceBlock, block);

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        instance.getDebug().log("Domain_BlockSpreadEvent", "BlockSpreadDomainEvent", "BlockSpreadDomainEvent called");

        if (block.getType() == Material.FIRE) {
            if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_FIRE_SPREAD)) {
                this.cancelled = true;
                this.cancelReason = "Block spread at " +
                        WorldFunctions.simpleReadable(block.getLocation()) +
                        " by " + block.getType().name() + " is cancelled.";
            }
        }
        if (sourceBlock.getType() == Material.CHORUS_FLOWER || sourceBlock.getType() == Material.CHORUS_PLANT) {
            if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_GROW_CHORUS_PLANT)) {
                this.cancelled = true;
                this.cancelReason = "Block spread at " +
                        WorldFunctions.simpleReadable(block.getLocation()) +
                        " by " + block.getType().name() + " is cancelled.";
            }
        }
        // Kelp was released in 1.13
        if (sourceBlock.getType() == Material.KELP) {
            if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_GROW_KELP)) {
                this.cancelled = true;
                this.cancelReason = "Block spread at " +
                        WorldFunctions.simpleReadable(block.getLocation()) +
                        " by " + block.getType().name() + " is cancelled.";
            }
        }
        if (GameFunctions.is114() || GameFunctions.is115() || GameFunctions.is116()) {
            // Bamboo was released in 1.14
            if (sourceBlock.getType() == Material.BAMBOO) {
                if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_GROW_BAMBOO)) {
                    this.cancelled = true;
                    this.cancelReason = "Block spread at " +
                            WorldFunctions.simpleReadable(block.getLocation()) +
                            " by " + block.getType().name() + " is cancelled.";
                }
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
