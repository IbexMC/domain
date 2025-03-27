package com.ibexmc.domain.api.events.block;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;

public final class BlockBurnDomainEvent extends BaseBlock implements Cancellable {

    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    /**
     * BlockBurnDomainEvent
     * This event is called when a block burns in a Domain field
     *
     * @param block The block that is burning
     *
     */
    public BlockBurnDomainEvent(Block block) {

        super(block);

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
