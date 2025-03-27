package com.ibexmc.domain.api.events.block;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;


public final class SignChangeDomainEvent extends BaseBlock implements Cancellable {

    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    /**
     * SignChangeDomainEvent
     * This event is called when a Domain sign changes
     *
     * @param block The sign block
     * @param player The player placing the sign
     *
     */
    public SignChangeDomainEvent(Block block, Player player) {

        super(block);

        Domain instance = super.getInstance();

        instance.getDebug().log("Domain_SignChangeEvent", "SignChangeDomainEvent", "SignChangeDomainEvent called");

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
