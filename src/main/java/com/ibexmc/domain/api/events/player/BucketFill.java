package com.ibexmc.domain.api.events.player;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.ibexmc.domain.domainblock.DomainBlockManager;

public final class BucketFill extends BasePlayer implements Cancellable {

    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    private Block block;
    private Material material;
    /**
     * BucketFill
     * This event is called when a bucket is filled in a Domain field
     * and the block clicked is LAVA or WATER
     *
     * @param player The player that filled the bucket
     * @param block The block that was clicked
     * @param material The material of the bucket
     *
     */
    public BucketFill(Player player, Block block, Material material) {

        super(player, block, material);

        Domain instance = super.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        this.block = block;
        this.material = material;

        instance.getDebug().log("Domain_BucketEmptyEvent", "BucketEmpty", "BucketEmpty called");

        boolean isAllowed = domainBlockManager.isAllowed(player, block.getLocation());
        boolean hasBypass = PlayerFunctions.hasBypass(PlayerFunctions.playerFromEntity(player));

        if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_BUCKET_FILL_BY_NONALLOWED)) {
            if (!isAllowed && !hasBypass) {
                this.cancelled = true;
                this.cancelReason = "Bucket Fill at " +
                        WorldFunctions.simpleReadable(block.getLocation()) +
                        " is cancelled.";
                instance.getMessage().send(player, "bucket_fill_deny", "&4You cannot fill buckets here", null, true);
                return;
            } else {
                if (hasBypass) {
                    instance.getMessage().send(player, "bucket_fill_deny_bypass", "&4You cannot fill buckets here, but have bypass mode enabled", null, true);
                    instance.getDebug().log(
                            "PlayerEvents.BucketEmpty",
                            "BucketEmpty",
                            player.getName() + " emptied a Water bucket at " + WorldFunctions.simpleReadable(block.getLocation())
                    );
                }
            }
        }
    }

    public Block getClickedBlock() {
        return this.block;
    }

    public Material getBucketType() {
        return this.material;
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
