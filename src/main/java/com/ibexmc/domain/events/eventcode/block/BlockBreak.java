package com.ibexmc.domain.events.eventcode.block;

import java.util.HashMap;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.helper.DomainFunctions;
import com.ibexmc.domain.helper.MaterialGroups;
import com.ibexmc.internal.functions.NumberFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import org.bukkit.block.data.Ageable;
import com.ibexmc.domain.util.functions.PlayerFunctions;

public class BlockBreak {

    private boolean cancelled = false;

    public void onBlockBreak(DomainBlock domainBlock, Block block, Player player) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        instance.getDebug().log("Domain_BlockBreakEvent", "BlockBreak", "BlockBreak called");

        Location domainBlockLocation = WorldFunctions.simplify(domainBlock.getBlockLocation());
        Location blockLocation = WorldFunctions.simplify(block.getLocation());

        if (domainBlockLocation.equals(blockLocation)) {
            // Always stop a Domain block from being broken
            this.cancelled = true;
            HashMap<String, String> placeHolder = new HashMap<String, String>();
            placeHolder.put("<%location%>", WorldFunctions.simpleFancyReadable(block.getLocation()));
            if (player != null) {
                instance.getMessage().send(player, "block_break_domainblock", "&4You cannot break this domain block - To remove, the owner can open the field and select Take", null, true);
            }
            return; // return out so we stop processing the block break
        }

        if (block.getBlockData() instanceof Ageable) {
            if (domainBlockManager.hasFlag(blockLocation, Flag.Type.PREVENT_CROP_HARVEST_BY_NONALLOWED)) {
                if (player != null) {
                    if (PlayerFunctions.hasBypass(player)) {
                        // Player has bypass so can break crops here
                        DomainFunctions.bypassLog(instance, player, "Crop harvested " + block.getType().name() + " at " + WorldFunctions.simpleReadable(block.getLocation()));
                        this.cancelled = false;
                    } else {
                        if (domainBlockManager.isOwner(player, domainBlock) || domainBlockManager.isAllowedToField(player, domainBlock) || domainBlockManager.isClanAllowed(domainBlock, player)) {
                            // Player has bypass so can break crops here
                            this.cancelled = false;
                        } else {
                            // If we got this far, we should stop the ageable block from breaking
                            instance.getMessage().send(player, "crop_break_refused", "&4You cannot harvest crops in this field", null, true);
                            this.cancelled = true;
                            return;
                        }
                    }
                }
            }
        }
        if (domainBlockManager.hasFlag(blockLocation, Flag.Type.PREVENT_BREAK) && !(block.getBlockData() instanceof Ageable)) {
            domainBlockLocation = null; // We don't need these now
            blockLocation = null;
            if (player != null) {
                if (PlayerFunctions.hasBypass(player)) {
                    // Player has bypass so can break blocks here
                    DomainFunctions.bypassLog(instance, player, "Block broken " + block.getType().name() + " at " + WorldFunctions.simpleReadable(block.getLocation()));
                    this.cancelled = false;
                } else {
                    if (domainBlockManager.isOwner(player, domainBlock) || domainBlockManager.isAllowedToField(player, domainBlock) || domainBlockManager.isClanAllowed(domainBlock, player)) {
                        // Player has bypass so can break blocks here
                        this.cancelled = false;
                    } else {
                        // If we got this far, we should stop the block from breaking
                        instance.getMessage().send(player, "block_break_refused", "&4You cannot break blocks in this field" , null, true);
                        this.cancelled = true;
                        return;
                    }
                }

                // If a sign is broken, check if it is a rent sign
                // If it IS a rent sign, the sign location must
                // be removed from the Domain block
                if (MaterialGroups.isSign(block.getType().name()))
                {
                    instance.getDebug().log("BlockEvents", "onBlockBreak", "Sign broken");
                    if (block.hasMetadata("pid") && !block.getMetadata("pid").isEmpty()) {
                        // It's a rent sign - remove it from the list

                        int placed_id = NumberFunctions.stringToInt(block.getMetadata("pid").get(0).asString());
                        DomainBlock pb = domainBlockManager.getDomainBlockByID(placed_id);

                        if (pb != null)
                        {
                            pb.removeRentSign(block.getLocation());
                            instance.getDebug().log("BlockEvents", "onBlockBreak", "Sign removed");
                        }
                    }
                }

                // All checks complete, return out of the event
                return;


            }
            else {
                // Block was broken but not by a player
                this.cancelled = true;
                return;
            }
        } else {
            // If this isn't a domain block or is within a PREVENT_BREAK
            // area, exit out without canceling the event.
            domainBlockLocation = null; // We don't need these now
            blockLocation = null;
            this.cancelled = false;
            return;
        }

    }
    public boolean isCancelled() {
        return cancelled;
    }
}
