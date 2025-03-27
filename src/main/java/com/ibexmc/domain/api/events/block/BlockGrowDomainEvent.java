package com.ibexmc.domain.api.events.block;

import java.util.Map;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.util.functions.GameFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;

public final class BlockGrowDomainEvent extends BaseBlock implements Cancellable {

    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    /**
     * BlockGrowDomainEvent
     * This event is called when a block grows in a Domain field
     *
     * @param block The block that is growing
     *
     */
    public BlockGrowDomainEvent(Block block) {

        super(block);

        Domain instance = super.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        instance.getDebug().log("Domain_BlockGrowEvent", "BlockGrowDomainEvent", "BlockGrowDomainEvent called");

        Map<Flag.Type, Boolean> hmFlags = domainBlockManager.getFlagsAtLocation(block.getLocation());

        boolean preventAll = domainBlockManager.hasFlag(Flag.Type.PREVENT_GROW_ALL, hmFlags);
        boolean cancelGrow = false;

        if (preventAll) {
            this.cancelled = true;
            this.cancelReason = "Block grow at " +
                    WorldFunctions.simpleReadable(block.getLocation()) +
                    " for " + block.getType().name() + " is cancelled.";
            hmFlags = null; // cleanup
            return;
        }
        switch (block.getType()) {
            case AIR:
                break;
            case VINE:
                if (domainBlockManager.hasFlag(Flag.Type.PREVENT_GROW_VINE, hmFlags))
                {
                    cancelGrow = true;
                    break;
                }
                break;
            case WHEAT:
                if (domainBlockManager.hasFlag(Flag.Type.PREVENT_GROW_WHEAT, hmFlags))
                {
                    cancelGrow = true;
                    break;
                }
                break;
            case SUGAR_CANE:
                if (domainBlockManager.hasFlag(Flag.Type.PREVENT_GROW_SUGAR_CANE, hmFlags))
                {
                    cancelGrow = true;
                    break;
                }
                break;
            case CACTUS:
                if (domainBlockManager.hasFlag(Flag.Type.PREVENT_GROW_CACTUS, hmFlags))
                {
                    cancelGrow = true;
                    break;
                }
                break;
            case MELON:
                if (domainBlockManager.hasFlag(Flag.Type.PREVENT_GROW_MELON, hmFlags))
                {
                    cancelGrow = true;
                    break;
                }
                break;
            case PUMPKIN:
                if (domainBlockManager.hasFlag(Flag.Type.PREVENT_GROW_PUMPKIN, hmFlags))
                {
                    cancelGrow = true;
                    break;
                }
                break;
            case TURTLE_EGG:
                if (domainBlockManager.hasFlag(Flag.Type.PREVENT_GROW_TURTLE_EGG, hmFlags))
                {
                    cancelGrow = true;
                    break;
                }
            case COCOA:
                if (domainBlockManager.hasFlag(Flag.Type.PREVENT_GROW_COCOA, hmFlags))
                {
                    cancelGrow = true;
                    break;
                }
                break;
            case CARROTS:
                if (domainBlockManager.hasFlag(Flag.Type.PREVENT_GROW_CARROTS, hmFlags))
                {
                    cancelGrow = true;
                    break;
                }
                break;
            case POTATOES:
                if (domainBlockManager.hasFlag(Flag.Type.PREVENT_GROW_POTATOES, hmFlags))
                {
                    cancelGrow = true;
                    break;
                }
                break;
            case BEETROOTS:
                if (domainBlockManager.hasFlag(Flag.Type.PREVENT_GROW_BEETROOTS, hmFlags))
                {
                    cancelGrow = true;
                    break;
                }
                break;
            case NETHER_WART:
                if (domainBlockManager.hasFlag(Flag.Type.PREVENT_GROW_NETHER_WART, hmFlags))
                {
                    cancelGrow = true;
                    break;
                }
                break;
            case CHORUS_FLOWER:
                if (domainBlockManager.hasFlag(Flag.Type.PREVENT_GROW_CHORUS_FLOWER, hmFlags))
                {
                    cancelGrow = true;
                    break;
                }
                break;
            default:
                if (GameFunctions.is114() || GameFunctions.is115() || GameFunctions.is116()) {
                    if (block.getType().equals(Material.SWEET_BERRY_BUSH)) {
                        if (domainBlockManager.hasFlag(Flag.Type.PREVENT_GROW_SWEET_BERRY_BUSH, hmFlags))
                        {
                            cancelGrow = true;
                            break;
                        }
                    }
                }
                break;
        }

        if (cancelGrow) {
            this.cancelled = true;
            this.cancelReason = "Block grow at " +
                    WorldFunctions.simpleReadable(block.getLocation()) +
                    " for " + block.getType().name() + " is cancelled.";
            hmFlags = null; // cleanup
            return;
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
