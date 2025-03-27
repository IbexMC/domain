package com.ibexmc.domain.helper;

import java.util.*;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.domainblock.Visualize;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


public class BlockCommands {

    private final Domain instance;

    public BlockCommands(Domain instance) {
        this.instance = instance;
    }

    public void getBlockInfo(Player player, Block block) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        if (domainBlockManager.isDomainBlock(block)) {
            boolean isOwner = false;
            boolean hasBypass = PlayerFunctions.hasBypass(player);
            DomainBlock pb = domainBlockManager.getDomainBlockFromBlock(block);
            if (domainBlockManager.isOwner(player, pb)) {
                isOwner = true;
            }
            if (isOwner || hasBypass) {
                if (!pb.isActive()) {
                    instance.getMessage().send(player, "block_info_disabled", "&4FIELD IS DISABLED", null, true);
                }
                Map<String, String> placeHolder = new HashMap<>();
                placeHolder.put("<%placed_id%>", Integer.toString(pb.getPlacedId()));
                placeHolder.put("<%blocktype%>", pb.getConfig().getDisplayName());
                placeHolder.put("<%blockname%>", pb.getFieldName());
                if (pb.getOwner() != null) {
                    placeHolder.put("<%owner%>", Bukkit.getOfflinePlayer(pb.getOwner()).getName());
                } else {
                    if (pb.isAdminBlock()) {
                        placeHolder.put("<%owner%>", instance.getLocale().localeText("admin_block", "&6Admin Block", null));
                    }
                }
                placeHolder.put("<%usedblocks%>",
                       "" + pb.getUsedVolume() + " of " + pb.getMaxVolume());

                instance.getMessage().send(player, "block_info_placedid", "Block ID: <%placed_id%>", placeHolder, true);
                instance.getMessage().send(player, "block_info_blocktype", "Block Type: <%blocktype%>", placeHolder, true);
                instance.getMessage().send(player, "block_info_blockname", "Block Name: <%blockname%>", placeHolder, true);
                instance.getMessage().send(player, "block_info_owner", "Block Owner: <%owner%>", placeHolder, true);
                instance.getMessage().send(player, "block_info_usedblocks", "Used blocks: <%usedblocks%>", placeHolder, true);
            }
        }
    }

    public void changeOwner(UUID newOwnerUUID, DomainBlock domainBlock) {

        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        Map<String, String> placeHolder = new HashMap<>();
        placeHolder.put("<%blocktype%>", domainBlock.getConfig().getDisplayName());
        placeHolder.put("<%blockname%>", domainBlock.getFieldName());
        if (domainBlock.isAdminBlock()) {
            instance.getDebug().log("BlockCommands", "changeOwner", "Domain Block is an Admin Block, removing Admin Block flag");
            domainBlock.setAdminBlock(false);
        } else {
            Player old_owner = Bukkit.getPlayer(domainBlock.getOwner());
            if (old_owner != null) {
                instance.getMessage().send(old_owner, "change_owner_old_player",
                        "&4You are no longer the owner of <%blocktype%> field - <%blockname%>",
                        placeHolder, true);
                Visualize visualize = new Visualize(instance);
                visualize.clearVisualization(old_owner);
            }
        }
        instance.getDebug().log("BlockCommands", "changeOwner", "Setting owner to " + newOwnerUUID.toString());
        domainBlock.setOwner(newOwnerUUID);
        Player new_owner = Bukkit.getPlayer(newOwnerUUID);
        if (new_owner != null) {
            instance.getMessage().send(new_owner,"change_owner_new_player", "&2You are now the owner of <%blocktype%> field - <%blockname%>",
                   placeHolder, true);
        }
        instance.getDebug().log("BlockCommands", "changeOwner", "Saving Domain Block");
        domainBlock.setDirty(true);
        domainBlockManager.domainBlockSave(domainBlock);
    }

    public void updateField(Player player, DomainBlock domainBlock) {
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        if (player != null) {
            instance.getDebug().log("BlockCommands", "updateField",
                    "Updating block #" + Integer.toString(domainBlock.getPlacedId()) + " for " + player.getName());
        } else {
            instance.getDebug().log("BlockCommands", "updateField",
                    "Updating block #" + Integer.toString(domainBlock.getPlacedId()) + ".");
        }
        domainBlock.setDirty(true);
        domainBlockManager.domainBlockSave(domainBlock);
    }

    /**
     * Removes a player from the allowed list
     * @param player Player calling the function
     * @param removeUUID Player unique identifier to remove
     * @param placedId Identifier for the field to remove from
     */
    public void removeOwnerAllowed(Player player, UUID removeUUID, int placedId) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        DomainBlock domainBlock = domainBlockManager.getDomainBlockByID(placedId);
        if (domainBlock != null) {
            OfflinePlayer removeOfflinePlayer = Bukkit.getOfflinePlayer(removeUUID);
            if (domainBlock.getAllowedPlayers().contains(removeUUID)) {
                domainBlock.removeAllowedPlayer(removeUUID);
                domainBlock.setDirty(true);
                domainBlockManager.domainBlockSave(domainBlock);
                Map<String, String> placeHolder = new HashMap<>();
                placeHolder.put("<%player%>", removeOfflinePlayer.getName());
                instance.getMessage().send(player, "remove_player_success_owner",
                        "&2SUCCESS - <%player%> has been removed from access to this field", placeHolder, true);

                if (removeOfflinePlayer.getPlayer() != null) {
                    Map<String, String> placeHolder1 = new HashMap<>();
                    placeHolder1.put("<%blocktype%>", domainBlock.getConfig().getDisplayName());
                    placeHolder1.put("<%player%>", player.getDisplayName());
                    instance.getMessage().send(removeOfflinePlayer.getPlayer(), "remove_player_success_player",
                            "You have been removed from access to the <%blocktype%> field by <%player%>",
                            placeHolder1, true);
                }
            } else {
                instance.getMessage().send(player, "remove_player_fail_not_allowed",
                        "Unable to remove player, they are not allowed to the field", null, true);

            }
        }
    }

}
