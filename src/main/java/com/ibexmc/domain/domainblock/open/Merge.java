package com.ibexmc.domain.domainblock.open;

import com.ibexmc.domain.domainblock.config.IFieldConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.helper.VaultFunctions;
import com.ibexmc.domain.util.functions.PlayerFunctions;

import java.util.HashMap;
import java.util.Map;

public class Merge implements IOpenDomainBlock {

    //region Objects
    private int placedId;
    private final Domain instance;
    private DomainBlockManager domainBlockManager;
    private VaultFunctions vaultFunctions;
    //endregion
    //region Constructor
    public Merge(int placedId) {
        this.placedId = placedId;
        this.instance = Domain.getInstance();
        domainBlockManager = new DomainBlockManager(instance);
        this.vaultFunctions = new VaultFunctions(instance);
    }
    //endregion
    //region Methods

    /**
     * Gets the Domain Block Identifier
     * @return Domain Block Identifier
     */
    public int placedId() {
        return this.placedId;
    }

    /**
     * Merges this Domain Block with the one in the players hand
     * @param player Player to merge for
     */
    public void merge(Player player) {

        instance.getDebug().log("Merge", "merge", "Starting Merge");
        ItemStack inHand = player.getInventory().getItemInMainHand().clone();
        if (inHand.getType() == Material.AIR) {
            instance.getMessage().send(
                    player,
                    "merge_block_cancel",
                    "Domain block merging cancelled",
                    null, true
            );
            Domain.getInstance().getData().openBlocks.remove(player.getUniqueId());
            return;
        }
        double balance = 0;
        DomainBlock domainBlock = domainBlockManager.getDomainBlockById(this.placedId);
        if (domainBlock != null) {
            instance.getDebug().log("Merge", "merge", "Domain Block found");
            if (domainBlockManager.hasBlockTag(inHand)) {
                instance.getDebug().log("Merge", "merge", "Item in hand has a block tag");
                IFieldConfig config = domainBlockManager.getConfigFromItem(inHand);
                if (config != null) {
                    if (config.getBlockKey().equalsIgnoreCase(domainBlock.getConfig().getBlockKey())) {
                        instance.getDebug().log("Merge", "merge", "Config found");
                        if (domainBlock.getMergedCount() >= domainBlock.getConfig().getMaxMerge()) {
                            if (!PlayerFunctions.hasBypass(player)) {
                                // max reached
                                instance.getMessage().send(
                                        player,
                                        "merge_maximum_reached",
                                        "Sorry, you have reached your maximum number of field merges",
                                        null, true
                                );
                                return;
                            }
                        }
                        if (domainBlock.getConfig().getCostToPlace() > 0) {
                            if (!PlayerFunctions.hasBypass(player)) {
                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
                                VaultFunctions.VaultReturn vrBalance = vaultFunctions.getBalance(offlinePlayer);
                                if (vrBalance.isSuccess()) {
                                    if (vrBalance.getBalance() >= domainBlock.getConfig().getCostToPlace()) {
                                        VaultFunctions.VaultReturn vrDebit = vaultFunctions.withdraw(
                                                player, domainBlock.getConfig().getCostToBuy()
                                        );
                                        if (vrDebit.isSuccess()) {
                                            balance = vrBalance.getBalance() - domainBlock.getConfig().getCostToPlace();
                                        } else {
                                            // error
                                            instance.getMessage().send(
                                                    player,
                                                    "merge_withdraw_error",
                                                    "Unable to withdraw funds for the domain block.  Unable to merge field",
                                                    null, true
                                            );
                                            return;
                                        }
                                    } else {
                                        // insufficient funds
                                        instance.getMessage().send(
                                                player,
                                                "merge_insufficient_funds",
                                                "You cannot afford to merge this domain block",
                                                null, true
                                        );
                                        return;
                                    }
                                } else {
                                    // error
                                    instance.getMessage().send(
                                            player,
                                            "merge_withdraw_error",
                                            "Unable to withdraw funds for the domain block.  Unable to merge field",
                                            null, true
                                    );
                                    return;
                                }
                            } else {
                                instance.getMessage().send(
                                        player,
                                        "merge_bypass_no_charge",
                                        "You have not been charged for this domain block field as you have Bypass enabled",
                                        null, true
                                );
                            }
                        }
                        // Merge
                        if (inHand.getAmount() > 1) {
                            instance.getDebug().log("Merge", "merge", "Block count in hand > 1, decrementing");
                            inHand.setAmount(inHand.getAmount() - 1);
                            player.getInventory().setItemInMainHand(inHand);
                        } else {
                            instance.getDebug().log("Merge", "merge", "Block count in hand not > 1, setting to air");
                            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
                        }
                        instance.getDebug().log("Merge", "merge", "Adding to merge count");
                        domainBlock.setMergedCount(domainBlock.getMergedCount() + 1);
                        domainBlock.setMaxVolume(domainBlock.getConfig().getVolume() * (domainBlock.getMergedCount() + 1));
                        domainBlock.setDirty(true);
                        domainBlockManager.domainBlockSave(domainBlock);
                        Domain.getInstance().getData().openBlocks.remove(player.getUniqueId());
                        Map<String, String> placeHolder = new HashMap<>();
                        placeHolder.put("<%maxvolume%>", "" + domainBlock.getMaxVolume());
                        if (domainBlock.getConfig().getCostToPlace() > 0 && !PlayerFunctions.hasBypass(player)) {
                            placeHolder.put("<%newbalance%>", "" + balance);
                            instance.getMessage().send(
                                    player,
                                    "merge_must_hold_block",
                                    "You must be holding a <%blocktype%> domain block in your main hand when merging this field",
                                    null, true
                            );
                        }
                        instance.getMessage().send(
                                player,
                                "merge_complete_new_size",
                                "Fields have been merged, your new max volume is: &b<%maxvolume%>",
                                null, true
                        );
                        instance.getDebug().log("Merge", "merge", "Merge complete");
                    } else {
                        // Incorrect block type
                        Map<String, String> placeHolder = new HashMap<>();
                        placeHolder.put("<%blocktype%>", domainBlock.getConfig().getDisplayName());
                        instance.getMessage().send(
                                player,
                                "merge_must_hold_block",
                                "You must be holding a <%blocktype%> domain block in your main hand when merging this field",
                                null, true
                        );
                    }
                } else {
                    // Missing config, invalid block
                    Map<String, String> placeHolder = new HashMap<>();
                    placeHolder.put("<%blocktype%>", domainBlock.getConfig().getDisplayName());
                    instance.getMessage().send(
                            player,
                            "merge_must_hold_block",
                            "You must be holding a <%blocktype%> domain block in your main hand when merging this field",
                            null, true
                    );
                }
            } else {
                // Item does not have a block tag
                Map<String, String> placeHolder = new HashMap<>();
                placeHolder.put("<%blocktype%>", domainBlock.getConfig().getDisplayName());
                instance.getMessage().send(
                        player,
                        "merge_must_hold_block",
                        "You must be holding a <%blocktype%> domain block in your main hand when merging this field",
                        null, true
                );
            }
        } else {
            // invalid domain block?
            instance.getMessage().send(
                    player,
                    "merge_error",
                    "&4Unexpected error - Unable to merge field",
                    null, true
            );
        }
    }
    //endregion
}
