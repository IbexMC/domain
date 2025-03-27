package com.ibexmc.domain.api.events.player;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.util.MaterialFunctions;

public final class BucketEmpty extends BasePlayer implements Cancellable {

    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    private Block block;
    private Material material;
    /**
     * BucketEmpty
     * This event is called when a bucket is emptied in a Domain field
     *
     * @param player The player that emptied the bucket
     * @param block The block that was clicked
     * @param material The material of the bucket
     *
     */
    public BucketEmpty(Player player, Block block, Material material) {

        super(player, block, material);

        Domain instance = super.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        this.block = block;
        this.material = material;

        instance.getDebug().log("Domain_BucketEmptyEvent", "BucketEmpty", "BucketEmpty called");

        boolean isAllowed = domainBlockManager.isAllowed(player, block.getLocation());
        boolean hasBypass = PlayerFunctions.hasBypass(PlayerFunctions.playerFromEntity(player));

        if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_WATER_PLACE) && MaterialFunctions.waterBuckets().contains(material)) {
            if (!isAllowed && !hasBypass) {
                this.cancelled = true;
                this.cancelReason = "Water Bucket Empty at " +
                        WorldFunctions.simpleReadable(block.getLocation()) +
                        " is cancelled.";
                instance.getMessage().send(player, "bucket_empty_water", "&4You cannot empty a water bucket here", null, true);
                return;
            } else {
                if (hasBypass) {
                    instance.getDebug().log(
                            "PlayerEvents.BucketEmpty",
                            "BucketEmpty",
                            player.getName() + " emptied a Water bucket at " + WorldFunctions.simpleReadable(block.getLocation())
                    );
                }
            }
        }

        if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_LAVA_PLACE) && material == Material.LAVA_BUCKET) {
            if (!isAllowed && !hasBypass) {
                this.cancelled = true;
                this.cancelReason = "Lava Bucket Empty at " +
                        WorldFunctions.simpleReadable(block.getLocation()) +
                        " is cancelled.";
                instance.getMessage().send(player,"bucket_empty_lava", "&4You cannot empty a lava bucket here", null, true);
                return;
            } else {
                if (hasBypass) {
                    instance.getDebug().log(
                            "PlayerEvents.BucketEmpty",
                            "BucketEmpty",
                            player.getName() + " emptied a Lava bucket at " + WorldFunctions.simpleReadable(block.getLocation())
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
