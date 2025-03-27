package com.ibexmc.domain.api.events.block;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;

public final class SpongeAbsorbDomainEvent extends BaseBlock implements Cancellable {

    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    /**
     * SpongeAbsorbDomainEvent
     * This event is called when a sponge absorbs water in a Domain field
     *
     * @param block The sponge block that is absorbing the water
     *
     */
    public SpongeAbsorbDomainEvent(Block block) {

        super(block);

        Domain instance = super.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        instance.getDebug().log("Domain_SpongeAbsorbEvent", "SpongeAbsorbDomainEvent", "SpongeAbsorbDomainEvent called");

        if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_SPONGE_ABSORB)) {
            this.cancelled = true;
            this.cancelReason = "Sponge absorb at " +
                    WorldFunctions.simpleReadable(block.getLocation()) +
                    " is cancelled.";
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
