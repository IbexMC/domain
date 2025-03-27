package com.ibexmc.domain.domainblock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.helper.DomainFunctions;
import com.ibexmc.internal.functions.TimeFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class OpenDomainBlock {
    private int placedID;
    private OpenDomainBlockType openDomainBlockType;
    private Location corner1;
    private Location corner2;
    private UUID newOwnerUUID;
    private String newOwnerName;
    private Location tpTo;
    private long opened;
    private Domain instance;
    private Visualize visualize;

    public OpenDomainBlock(int placedID, OpenDomainBlockType openDomainBlockType) {
        this.placedID = placedID;
        this.openDomainBlockType = openDomainBlockType;
        this.opened = TimeFunctions.now().getTime();
        this.instance = Domain.getInstance();
        visualize = new Visualize(instance);
    }
    public Integer getPlacedID() {
        return this.placedID;
    }
    public OpenDomainBlockType getOpenDomainBlockType() {
        return this.openDomainBlockType;
    }
    public Location getCorner1() {
        return this.corner1;
    }
    public Location getCorner2() {
        return this.corner2;
    }
    public void setCorner1(Location corner1) {
        this.corner1 = corner1;
    }
    public void setCorner2(Location corner2) {
        this.corner2 = corner2;
    }
    public UUID getNewOwnerUUID() {
        return this.newOwnerUUID;
    }
    public void setNewOwnerUUID(UUID uuid) {
        this.newOwnerUUID = uuid;
    }
    public String getNewOwnerName() {
        return this.newOwnerName;
    }
    public void setNewOwnerName(String name) {
        this.newOwnerName = name;
    }
    public void openEffects(Player player, DomainBlock domainBlock) {
        player.getWorld().playEffect(player.getLocation(), Effect.CLICK1, 10);
        visualize.clearVisualization(player);
        visualize.maskBlock(
                domainBlock.getPlacedId(),
                player,
                Material.BEACON
        );
        new BukkitRunnable() {
            @Override
            public void run() {
                visualize.displayField(
                        player,
                        domainBlock,
                        -1,
                        true,
                        true,
                        true
                );
            }
        }.runTaskAsynchronously(Domain.getInstance());
    }
    public void openEffectsWithoutVisualization(Player player, DomainBlock domainBlock) {
        player.getWorld().playEffect(player.getLocation(), Effect.CLICK1, 10);
        visualize.clearVisualization(player);
        visualize.maskBlock(
                domainBlock.getPlacedId(),
                player,
                Material.BEACON
        );
    }
    public void closeEffects(Player player, DomainBlock domainBlock) {
        player.getWorld().playEffect(player.getLocation(), Effect.CLICK2, 10);
        visualize.clearVisualization(player);
    }
    public void setCorner(Player player, DomainBlock domainBlock, Boolean hasBypass) {
        if (this.corner1 != null && this.corner2 != null) {
            boolean validField = this.validResize(player, domainBlock, hasBypass);
            new BukkitRunnable() {
                @Override
                public void run() {
                    visualize.displayField(
                            player,
                            domainBlock,
                            -1,
                            true,
                            true,
                            validField
                    );
                }
            }.runTaskAsynchronously(Domain.getInstance());
            player.getWorld().playEffect(player.getLocation(), Effect.IRON_TRAPDOOR_CLOSE, 10);
        }
    }
    public boolean validResize(Player player, DomainBlock domainBlock, boolean hasBypass) {
        boolean returnValue = true;


        // Check both corners are set
        if (this.corner1 == null || this.corner2 == null) {
            // Both corners must be set
            instance.getMessage().send(player, "valid_resize_corners", "&4Field invalid.  You must set both corners", null, true);
            return false;
        }

        // Check if the field is within limits
        int blocksInField = DomainFunctions.blockCount(this.corner1, this.corner2); // calculate the number of blocks in the field
        if (blocksInField > domainBlock.getMaxVolume())
        {
            if (hasBypass) {
                // Field is too large
                Map<String, String> resizecountparameters = new HashMap<>();
                resizecountparameters.put("<%used%>", Integer.toString(blocksInField));
                resizecountparameters.put("<%maxvolume%>", "" + domainBlock.getMaxVolume());
                instance.getMessage().send(player, "valid_resize_size_bypass", "&2Oversized field allowed due to Domain Bypass.  Field size: &2<%used%> of <%maxvolume%>", resizecountparameters, true);
                return true;
            } else {
                // Field is too large
                Map<String, String> resizeCountPlaceholder = new HashMap<>();
                resizeCountPlaceholder.put("<%used%>", Integer.toString(blocksInField));
                resizeCountPlaceholder.put("<%maxvolume%>", "" + domainBlock.getMaxVolume());
                instance.getMessage().send(player, "valid_resize_size", "&4Field invalid.  Field size: &2<%used%> of <%maxvolume%>", resizeCountPlaceholder, true);
                return false;
            }
        }

        // Check if Domain block is in the field (if applicable)
        if (domainBlock.getConfig().isBlockInField()) {

            if (!WorldFunctions.insideCuboid(domainBlock.getBlockLocation(), this.corner1, this.corner2))
            {
                // block must be inside field
                instance.getMessage().send(player, "valid_resize_in_field", "&4Field invalid.  Domain block must be within the field", null, true);
                return false;
            }
        }

        // Check if fields overlap if the block is rentable
        DomainBlockManager domainBlockManager = new DomainBlockManager(Domain.getInstance());
        if (domainBlock.getConfig().getRentCost() > 0 && domainBlock.getConfig().getRentDays() > 0) {
            List<Location> lstCorners = DomainFunctions.getCorners(this.corner1, this.corner2);
            if (lstCorners != null) {
                if (lstCorners.size() > 0) {
                    for (Location l : lstCorners) {
                        List<DomainBlock> pbAtLoc = domainBlockManager.playerDomainBlocks(l);
                        if (pbAtLoc != null) {
                            if (pbAtLoc.size() > 0)	{
                                for (DomainBlock pbLoc : pbAtLoc) {
                                    if (pbLoc.getPlacedId() != domainBlock.getPlacedId()) {
                                        if (pbLoc.getConfig().getBlockKey().equals(domainBlock.getConfig().getBlockKey())) {
                                            instance.getMessage().send(player, "valid_resize_rent_overlap", "&4Field invalid.  Rentable fields cannot overlap", null, true);
                                            return false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        DomainBlock testDomainBlock = domainBlock;
        testDomainBlock.setCorner1(corner1);
        testDomainBlock.setCorner2(corner2);
        boolean cancelOverlap = domainBlockManager.hasOverlap(player, testDomainBlock);
        if (cancelOverlap) {
            if (!hasBypass) {
                //instance.getMessage().send("valid_resize_overlap", "&4Field invalid.  Fields belonging to different players cannot overlap", player, true, null);
                return false;
            }
        }

        return returnValue;
    }
    public void setTPTo(Location tpTo) {
        this.tpTo = tpTo;
    }
    public Location getTpTo() {
        return this.tpTo;
    }
}
