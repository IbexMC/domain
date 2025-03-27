package com.ibexmc.domain.api.events.block;

import java.util.HashMap;
import java.util.List;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Cancellable;

public final class PistonExtendDomainEvent extends BaseBlock implements Cancellable {

    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    /**
     * PistonExtendDomainEvent
     * This event is called when a piston extends
     *
     * @param blockFace The block face that fired the event
     * @param blocksMoved The blocks being moved
     *
     */
    public PistonExtendDomainEvent(BlockFace blockFace, List<Block> blocksMoved) {

        super(blockFace, blocksMoved);

        Domain instance = super.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        instance.getDebug().log("Domain_PistonExtendEvent", "PistonExtendDomainEvent", "PistonExtendDomainEvent called");

        for (Block block : blocksMoved) {
            if (domainBlockManager.isDomainBlock(block)) {
                HashMap<String, String> placeHolder = new HashMap<String, String>();
                placeHolder.put("<%location%>", WorldFunctions.simpleFancyReadable(block.getLocation()));
                this.cancelled = true;
                this.cancelReason = instance.getLocale().localeText("pistonextend_domainblock", "Block at <%location%> is a Domain block and cannot be moved", placeHolder);
                return; // return out so we stop processing blocks
            }

            // Get Domain Block at source
            DomainBlock domainBlockFrom = domainBlockManager.getCurrentField(block.getLocation());
            DomainBlock domainBlockTo = domainBlockManager.getCurrentField(
                    block.getLocation().add(
                            blockFace.getModX(),
                            blockFace.getModY(),
                            blockFace.getModZ()
                    )
            );

            if (domainBlockFrom == null && domainBlockTo == null) {
                // Exit out of this block, we are not interested in this even if
                // neither source or destination are not Domain block field locations
                break;
            }
            if (domainBlockFrom == domainBlockTo) {
                // Exit out of this block - we are not interested in piston block
                // movement that stays in the field
                break;
            }

            // To get this far, either source or destination location is a Domain block location
            // and it is not within the same field



            if (domainBlockFrom != null) {
                if (domainBlockManager.hasFlag(Flag.Type.PREVENT_PISTON_MOVE_OUT_FIELD, domainBlockManager.getFlagsAtLocation(block.getLocation()))) {
                    this.cancelled = true;
                    this.cancelReason = instance.getLocale().localeText("pistonextend_moveout_stop", "Unable to move block out of the Domain Block field", null);
                    return; // return out so we stop processing blocks
                }
            }

            if (domainBlockTo != null) {
                if (domainBlockManager.hasFlag(Flag.Type.PREVENT_PISTON_MOVE_INTO_FIELD, domainBlockManager.getFlagsAtLocation(
                        block.getLocation().add(
                                blockFace.getModX(),
                                blockFace.getModY(),
                                blockFace.getModZ()
                        )
                        )
                )
                ) {
                    this.cancelled = true;
                    this.cancelReason = instance.getLocale().localeText("pistonextend_movein_stop", "Unable to move block into the Domain Block field", null);
                    return; // return out so we stop processing blocks
                }
            }
        }

        return;
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
