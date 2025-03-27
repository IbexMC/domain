package com.ibexmc.domain.api.events.block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.config.IFieldConfig;
import com.ibexmc.domain.domainblock.open.IOpenDomainBlock;
import com.ibexmc.domain.domainblock.open.Merge;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.helper.DomainFunctions;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import com.ibexmc.internal.functions.NumberFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.permissions.PermissionAttachmentInfo;

import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.helper.VaultFunctions;

public final class BlockPlaceDomainEvent extends BaseBlock implements Cancellable {

    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    /**
     * BlockPlaceDomainEvent
     * This event is called when a block is placed
     *
     * @param block The block that is being placed
     * @param player The player placing the block
     *
     */
    public BlockPlaceDomainEvent(Block block, Player player) {

        super(block);

        Domain instance = super.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        instance.getDebug().log("Domain_BlockPlaceEvent", "BlockPlaceDomainEvent", "BlockPlaceDomainEvent called");

        if (instance.getData().openBlocks.containsKey(player.getUniqueId())) {
            IOpenDomainBlock openDomainBlock = instance.getData().openBlocks.get(player.getUniqueId());
            if (openDomainBlock instanceof Merge) {
                instance.getDebug().log(
                        "Domain_BlockPlaceEvent",
                        "BlockPlaceDomainEvent",
                        "Player is currently merging, unable to place block"
                );
                this.cancelled = true;
                this.cancelReason = "Cannot place blocks while merging";
                return; // Block place is cancelled, exit out of code
            }
        }

        boolean inField = domainBlockManager.inField(block.getLocation());
        boolean hasBypass = PlayerFunctions.hasBypass(player);
        boolean isAllowed = domainBlockManager.isAllowed(player, block.getLocation());

        boolean preventBuild = false;
        boolean allowDomainBlock = true;
        boolean isDomainBlock = false;
        if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_BUILD)) {
            // If we have PREVENT_BUILD, prevent build and do not allow domain blocks to be placed
            preventBuild = true;
            allowDomainBlock = false;
        }
        if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_BUILD_EXCEPT_DOMAINBLOCK)) {
            // If we have PREVENT_BUILD_EXCEPT_DOMAINBLOCK, prevent build, but do allow domain
            // blocks to be placed.  This overrides the PREVENT_BUILD flag
            preventBuild = true;
            allowDomainBlock = true;
        }

        if (player.getInventory().getItemInMainHand() != null) {
            if (domainBlockManager.isItemIsDomainBlock(player.getInventory().getItemInMainHand())) {
                instance.getDebug().log("Domain_BlockPlaceEvent", "BlockPlaceDomainEvent", "Item in main hand is a Domain block");
                isDomainBlock = true;
            } else {
                instance.getDebug().log("Domain_BlockPlaceEvent", "BlockPlaceDomainEvent", "Item not returning as a Domain block");
                if (block.getBlockData() instanceof Ageable) {
                    // Item is ageable (crop)
                    if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_CROP_PLANTING_BY_NONALLOWED)) {
                        // prevent player
                        if (isAllowed || hasBypass) {
                            // Allowed
                            if (hasBypass) {
                                // log
                                DomainFunctions.bypassLog(
                                        instance,
                                        player,
                                        "Crop placed at " + WorldFunctions.simpleReadable(block.getLocation())
                                );
                            }
                            instance.getDebug().log(
                                    "Domain_BlockPlaceEvent",
                                    "BlockPlaceDomainEvent", "Crop placed " + WorldFunctions.simpleReadable(block.getLocation())
                            );
                            return;
                        } else {
                            // Prevent

                            instance.getMessage().send(player, "crop_planting_refused", "&4You cannot plant crops here", null, true);
                            this.cancelled = true;
                            this.cancelReason = "Crop (" + block.getType().name()  + ") planting at " +
                                    WorldFunctions.simpleReadable(block.getLocation()) +
                                    " of " + block.getType().name() + " is cancelled.";
                            return; // Block place is cancelled, exit out of code
                        }
                    } else {
                        instance.getDebug().log(
                                "Domain_BlockPlaceEvent",
                                "BlockPlaceDomainEvent", "Crop placed " + WorldFunctions.simpleReadable(block.getLocation()) + " no prevent planting flag active here"
                        );
                        return; // It is ageable, but no flag to prevent placing it, so exit out
                    }
                }
            }
        } else {
            instance.getDebug().log("Domain_BlockPlaceEvent", "BlockPlaceDomainEvent", "Item in main hand is null");
        }


        if (preventBuild) {
            if (!inField) {
                isAllowed = true; // Automatically allowed if not in a Domain field
            }

            if (isAllowed || hasBypass) {
                if (inField && hasBypass) {
                    // If in a Domain field and bypass is on, log the block type and location
                    DomainFunctions.bypassLog(
                            instance,
                            player,
                            block.getType().name() + " placed at " + WorldFunctions.simpleReadable(block.getLocation())
                    );
                }
                // Build allowed
                preventBuild = false;
            } else {

                if (isDomainBlock && allowDomainBlock) {
                    // if it is a domain block and we allow domain blocks to be placed despite preventing build
                    // then simply log the fact and move on, do not exit out
                    instance.getDebug().log("Domain_BlockPlaceEvent", "BlockPlaceDomainEvent", "Item is a domain block, and domain blocks are allowed");
                } else {
                    instance.getMessage().send(player, "block_place_refused", "&4You cannot place blocks here", null, true);
                    this.cancelled = true;
                    this.cancelReason = "Block place at " +
                            WorldFunctions.simpleReadable(block.getLocation()) +
                            " of " + block.getType().name() + " is cancelled.";
                    return; // Block place is cancelled, exit out of code
                }
            }
        } else {
            // Build allowed
        }



        if (isDomainBlock && (allowDomainBlock || !preventBuild)) {

            instance.getDebug().log("Domain_BlockPlaceEvent", "BlockPlaceDomainEvent", "Placing a domain block");

            // Item is a domain block
            IFieldConfig blueprint = domainBlockManager.getDomainConfigFromItemStack(
                    player.getInventory().getItemInMainHand()
            ); // Get the blueprint for the Domain block

            if (blueprint != null) {
                String lockedTo = "";
                if (blueprint.getLockedTo() != null)
                {
                    lockedTo = blueprint.getLockedTo();
                }
                DomainBlock currentField = domainBlockManager.getCurrentField(block.getLocation());
                boolean canPlaceDomainBlock = true;
                if (currentField != null) {
                    if (!lockedTo.equals("") && !lockedTo.equals(currentField.getConfig().getBlockKey())) {
                        // Send error - incompatible lockedTo
                        instance.getDebug().log("Domain_BlockPlaceEvent", "BlockPlaceDomainEvent", "Invalid field type in locked_to");
                        canPlaceDomainBlock = false;
                    }
                } else {
                    if (!lockedTo.equals("")) {
                        instance.getDebug().log("Domain_BlockPlaceEvent", "BlockPlaceDomainEvent", "Domain block has a locked_to, but not currently in a field");

                        HashMap<String, String> lockedToParam = new HashMap<String, String>();
                        lockedToParam.put("<%lockedto%>", lockedTo);

                        instance.getMessage().send(player, "block_place_locked", "You cannot place that field here - it can only be placed in a <%lockedto%> field", lockedToParam, true);
                        this.cancelled = true;
                        this.cancelReason = "Block place at " +
                                WorldFunctions.simpleReadable(block.getLocation()) +
                                " of " + block.getType().name() + " is cancelled - Block has a locked_to, but not currently in a field.";
                        return; // Block place is cancelled, exit out of code
                    }
                }
                if (canPlaceDomainBlock) {
                    // Check if it will cause overlapping rentable fields
                    if (blueprint.getRentCost() > 0 && blueprint.getRentDays() > 0)
                    {
                        List<DomainBlock> pbAtLoc = domainBlockManager.playerDomainBlocks(block.getLocation());
                        if (pbAtLoc != null)
                        {
                            if (pbAtLoc.size() > 0)
                            {
                                for (DomainBlock pbLoc : pbAtLoc)
                                {
                                    if (pbLoc.getConfig().getBlockKey().equals(blueprint.getBlockKey()))
                                    {
                                        if (!hasBypass) {
                                            instance.getDebug().log("Domain_BlockPlaceEvent", "BlockPlaceDomainEvent", player.getName() + " tried to overlap rentable fields");
                                            instance.getMessage().send(player, "block_place_rent_overlap", "&4Rentable fields cannot overlap", null, true);
                                            this.cancelled = true;
                                            this.cancelReason = "Block place at " +
                                                    WorldFunctions.simpleReadable(block.getLocation()) +
                                                    " of " + block.getType().name() + " is cancelled - Rentable fields cannot overlap.";
                                            return; // Block place is cancelled, exit out of code
                                        } else {
                                            // Log - placed in overlapping rentable field
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // Check if it is an invalid world for this blueprint
                    if (blueprint.getInvalidWorlds() != null) {
                        for (String worldName : blueprint.getInvalidWorlds()) {
                            if (worldName.equalsIgnoreCase(block.getLocation().getWorld().getName())) {
                                if (!hasBypass) {
                                    Map<String, String> worldParameters = new HashMap<String, String>();
                                    worldParameters.put("<%blocktype%>", blueprint.getDisplayName());
                                    instance.getMessage().send(player, "block_place_invalid_world", "&4You cannot place a <%blocktype%> block in this world" , worldParameters, true);
                                    this.cancelled = true;
                                    this.cancelReason = "Block place at " +
                                            WorldFunctions.simpleReadable(block.getLocation()) +
                                            " of " + block.getType().name() + " is cancelled - Invalid world.";
                                    return; // Block place is cancelled, exit out of code
                                } else {
                                    // Log - placed in invalid world
                                }
                            }
                        }
                    }
                    // Check if Vault is installed and charge the player as needed
                    if (instance.getDependencies().hasVault()) {
                        if (blueprint.getCostToPlace() > 0)
                        {
                            if (hasBypass) {
                                // Log - no payment
                            } else {
                                VaultFunctions vaultFunctions = new VaultFunctions(instance);
                                VaultFunctions.VaultReturn playerBalance = vaultFunctions.withdraw(
                                        PlayerFunctions.offlinePlayerFromUUID(
                                                player.getUniqueId()
                                        ), new Double(blueprint.getCostToPlace())
                                );
                                if (playerBalance.isSuccess()) {
                                    // Message player - Funds withdrawn
                                } else {
                                    // Error - unable to withdraw funds
                                    this.cancelled = true;
                                    this.cancelReason = "Block place at " +
                                            WorldFunctions.simpleReadable(block.getLocation()) +
                                            " of " + block.getType().name() + " is cancelled - Unable to withdraw funds." +
                                            playerBalance.getMessage();
                                    return; // Block place is cancelled, exit out of code
                                }
                            }
                        }
                    }
                    // Check for maximum number of blocks
                    int permMax = 0;
                    int permUse = 0;
                    boolean permLimitByBlock = instance.config().isPermLimitByBlock();
                    instance.getDebug().log("BlockPlaceDomainEvent", "Placing block", "Perm limit by block = " + Boolean.toString(permLimitByBlock));
                    for (PermissionAttachmentInfo perms : player.getEffectivePermissions()) {
                        if (permLimitByBlock) {
                            // Permission limit is by block
                            if (perms.getPermission().startsWith("domain." + blueprint.getBlockKey() + ".limit.")) {
                                instance.getDebug().log("BlockPlaceDomainEvent", "Placing block", "Permission starts with " + "domain." + blueprint.getBlockKey() + ".limit.");
                                int permNum = NumberFunctions.stringToInt((perms.getPermission().split("domain." + blueprint.getBlockKey() + ".limit.")[1]));
                                instance.getDebug().log("BlockPlaceDomainEvent", "Placing block", "Perm limit number = " + Integer.toString(permNum));
                                if (permNum >= 0) {

                                    permUse = Math.min(999, Integer.valueOf(perms.getPermission().split("domain." + blueprint.getBlockKey() + ".limit.")[1]));

                                    if (permUse > permMax) {
                                        instance.getDebug().log("BlockPlaceDomainEvent", "Placing Block",
                                                "Using this perm amount as it is more than our current max.  This perm amount = " + Integer.toString(permUse) + " current max = " + Integer.toString(permMax)
                                        );
                                        permMax = permUse;
                                    } else {
                                        instance.getDebug().log("BlockPlaceDomainEvent", "Placing Block",
                                                "Not using this perm amount as it is NOT more than our current max.  This perm amount = " + Integer.toString(permUse) + " current max = " + Integer.toString(permMax)
                                        );
                                    }
                                }
                            }
                        } else {
                            // Permission limit is not by block
                            if (perms.getPermission().startsWith("domain.limit.")) {
                                int permNum = NumberFunctions.stringToInt((perms.getPermission().split("domain.limit.")[1]));
                                if (permNum >= 0) {
                                    permUse = Math.min(999, Integer.valueOf(perms.getPermission().split("domain.limit.")[1]));
                                }
                                if (permUse > permMax) {
                                    instance.getDebug().log("BlockPlaceDomainEvent", "Placing Block",
                                            "Using this perm amount as it is more than our current max.  This perm amount = " + Integer.toString(permUse) + " current max = " + Integer.toString(permMax)
                                    );
                                    permMax = permUse;
                                } else {
                                    instance.getDebug().log("BlockPlaceDomainEvent", "Placing Block",
                                            "Not using this perm amount as it is NOT more than our current max.  This perm amount = " + Integer.toString(permUse) + " current max = " + Integer.toString(permMax)
                                    );
                                }
                            }
                        }
                    }
                    int maxBlocks = 0;
                    instance.getDebug().log("BlockPlaceDomainEvent", "Block Check", "IFieldConfig Max: " + Integer.toString(blueprint.getMaxBlocks()) + " Perm Max: " + Integer.toString(permMax));
                    maxBlocks = Math.max(blueprint.getMaxBlocks(), permMax);
                    int blockCount = 0;
                    instance.getDebug().log("BlockPlaceDomainEvent", "Block Check", "Chosen max amount: " + Integer.toString(maxBlocks));
                    Set<DomainBlock> lstPB = domainBlockManager.getOwnedDomainBlocks(player.getUniqueId(), false);
                    if (lstPB != null) {
                        for (DomainBlock chkPB : lstPB) {
                            if (ChatColor.stripColor(blueprint.getBlockKey()).equalsIgnoreCase(ChatColor.stripColor(chkPB.getConfig().getBlockKey()))) {
                                blockCount++;
                            }
                        }
                    }
                    instance.getDebug().log("BlockPlaceDomainEvent", "Block Check", "Block count: " + Integer.toString(blockCount));
                    lstPB.clear();
                    if (blockCount >= maxBlocks) {
                        if (!hasBypass) {
                            Map<String, String> placeLimitParameters = new HashMap<String, String>();
                            placeLimitParameters.put("<%blocktype%>", blueprint.getDisplayName());
                            instance.getMessage().send(player, "block_place_limit_reached", "&4Sorry, you have reached your limit for <%blocktype%> Domain fields" , placeLimitParameters, true);
                            this.cancelled = true;
                            this.cancelReason = "Block place at " +
                                    WorldFunctions.simpleReadable(block.getLocation()) +
                                    " of " + block.getType().name() + " is cancelled - Maximum number of Domain blocks reached.";
                            return; // Block place is cancelled, exit out of code
                        } else {
                            instance.getDebug().log("BlockEvents", "onBlockPlace", "Player has bypass - limit check ignored");
                        }
                    }

                    DomainBlock returnedBlock = null;
                    if (currentField != null) {
                        returnedBlock = domainBlockManager.create(player, blueprint, block.getLocation(), currentField.getPlacedId());
                    } else {
                        returnedBlock = domainBlockManager.create(player, blueprint, block.getLocation(), -1);
                    }

                    if (returnedBlock != null) {
                        instance.getDebug().log("BlockEvents", "onBlockPlace", blueprint.getDisplayName() + " placed by " + player.getName() + " at X: " + block.getX() + " Y: " + block.getY() + " Z: " + block.getZ());
                        instance.getDebug().log("BlockEvents", "onBlockPlace", "&2Running NewDomainBlock");
                    } else {
                        this.cancelled = true;
                        this.cancelReason = "Block place at " +
                                WorldFunctions.simpleReadable(block.getLocation()) +
                                " of " + block.getType().name() + " is cancelled - Unexpected error creating Domain field";
                        return; // Block place is cancelled, exit out of code
                    }
                    currentField = null;
                    returnedBlock = null;
                }
            } else {
                // Unexpected error
                this.cancelled = true;
                this.cancelReason = "Unexpected error placing Domain Block at " +
                        WorldFunctions.simpleReadable(block.getLocation());
                return; // Block place is cancelled, exit out of code
            }

            blueprint = null;
        } else {
            instance.getDebug().log("Domain_BlockPlaceEvent", "BlockPlaceDomainEvent", "Item in main hand is NOT a Domain block");
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
