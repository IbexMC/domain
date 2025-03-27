package com.ibexmc.domain.api.events.block;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;

public final class BlockToDomainEvent extends BaseBlock implements Cancellable {

    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    /**
     * BlockToDomainEvent
     * This event is called when water or lava flows to a Domain field
     *
     * @param sourceBlock The source block (from)
     * @param block The destination block (to)
     *
     */
    public BlockToDomainEvent(Block sourceBlock, Block block) {

        super(sourceBlock, block);

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        instance.getDebug().log(
                "Domain_BlockToEvent",
                "BlockToDomainEvent",
                "BlockToDomainEvent called:" +
                        "\n&9Source: " + sourceBlock.getType().name() +
                        "\n&eDestination: " + block.getType().name()
        );

        if (sourceBlock.getType() == Material.LAVA) {
            instance.getDebug().log("Domain_BlockToEvent", "BlockToDomainEvent", "Is Lava");
            if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_LAVA_FLOW_TO)) {
                instance.getDebug().log("Domain_BlockToEvent", "BlockToDomainEvent", "PREVENT_LAVA_FLOW_TO found - cancelling");
                this.cancelled = true;
                this.cancelReason = "Block flow to " +
                        WorldFunctions.simpleReadable(block.getLocation()) +
                        " by " + block.getType().name() + " is cancelled.";
                return;
            }
        }
        if (sourceBlock.getType() == Material.WATER) {
            instance.getDebug().log("Domain_BlockToEvent", "BlockToDomainEvent", "Is Water");
            if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_WATER_FLOW_TO)) {
                instance.getDebug().log("Domain_BlockToEvent", "BlockToDomainEvent", "PREVENT_WATER_FLOW_TO found - cancelling");
                this.cancelled = true;
                this.cancelReason = "Block flow to " +
                        WorldFunctions.simpleReadable(block.getLocation()) +
                        " by " + block.getType().name() + " is cancelled.";
                return;
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
