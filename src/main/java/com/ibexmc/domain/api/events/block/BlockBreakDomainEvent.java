package com.ibexmc.domain.api.events.block;

import com.ibexmc.domain.domainblock.DomainBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;


public final class BlockBreakDomainEvent extends BaseBlock implements Cancellable {

    private String cancelReason = ""; // The cancellation reason
    private boolean cancelled = false; // A flag to indicate if the event was cancelled
    private DomainBlock domainBlock;
    private Player player;

    /**
     * BlockBreakDomainEvent
     * This event is called when a block is broken inside a Domain field
     *
     * @param domainBlock The domain block where the block is being broken
     * @param block The block that is being broken
     * @param player The player that broke the block (null if not a player)
     *
     */
    public BlockBreakDomainEvent(DomainBlock domainBlock, Block block, Player player) {

        super(domainBlock, block, player);

        this.domainBlock = domainBlock;
        this.player = player;

    }

    public DomainBlock getDomainBlock() {
        return this.domainBlock;
    }

    public Player getPlayer() {
        return this.player;
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
