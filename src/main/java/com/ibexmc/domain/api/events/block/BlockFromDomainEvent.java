package com.ibexmc.domain.api.events.block;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;

public final class BlockFromDomainEvent extends BaseBlock implements Cancellable {

    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    /**
     * BlockFromDomainEvent
     * This event is called when water or lava flows from a Domain field
     *
     * @param sourceBlock The source block (from)
     * @param block The destination block (to)
     *
     */
    public BlockFromDomainEvent(Block sourceBlock, Block block) {

        super(sourceBlock, block);

        Domain instance = super.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        instance.getDebug().log("Domain_BlockFromEvent", "BlockFromDomainEvent", "BlockFromDomainEvent called");

        if (sourceBlock.getType() == Material.LAVA) {
            if (domainBlockManager.hasFlag(sourceBlock.getLocation(), Flag.Type.PREVENT_LAVA_FLOW)) {
                this.cancelled = true;
                this.cancelReason = "Block flow from " +
                        WorldFunctions.simpleReadable(sourceBlock.getLocation()) +
                        " by " + sourceBlock.getType().name() + " is cancelled.";
            }
        }
        if (sourceBlock.getType() == Material.WATER) {
            if (domainBlockManager.hasFlag(sourceBlock.getLocation(), Flag.Type.PREVENT_WATER_FLOW)) {
                this.cancelled = true;
                this.cancelReason = "Block flow from " +
                        WorldFunctions.simpleReadable(sourceBlock.getLocation()) +
                        " by " + sourceBlock.getType().name() + " is cancelled.";
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
