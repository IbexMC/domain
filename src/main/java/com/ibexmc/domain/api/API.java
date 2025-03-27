package com.ibexmc.domain.api;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.helper.DomainFunctions;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.domainblock.Visualize;
import com.ibexmc.domain.helper.BlockCommands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class API {
    private Domain instance;
    DomainBlockManager domainBlockManager;
    public API(Domain instance) {
        this.instance = instance;
        domainBlockManager = new DomainBlockManager(instance);
    }

    //region Field

    /**
     * Updates the corners for a field - forces update without checks
     * @param domainBlockID Domain block identifier to update
     * @param corner1 Corner1 location to set
     * @param corner2 Corner2 location to set
     * @param playerUUID Player unique identifier to alert if they are online, they will also see an updated visualization of the field.  If null, no messages or visualization will be shown to the player.
     * @return If true, field was resized correctly
     */
    public boolean resizeField(int domainBlockID, Location corner1, Location corner2, UUID playerUUID) {
        DomainBlock domainBlock = domainBlockManager.getDomainBlockByID(domainBlockID);
        if (domainBlock == null) {
            instance.getDebug().log(
                    "DomainAPI",
                    "resizeField",
                    instance.getLocale().localeText("api_invalid_domainblock", "Invalid Domain Block", null)
            );
            return false;
        }
        if (corner1 == null) {
            instance.getDebug().log(
                    "DomainAPI",
                    "resizeField",
                    instance.getLocale().localeText("api_invalid_corner1", "Invalid Location for Corner 1", null)
            );
            return false;
        } else {
            instance.getDebug().log("DomainAPI", "resizeField", "New Corner 1: " + WorldFunctions.simpleReadable(corner1));
            domainBlock.setCorner1(corner1);
        }
        if (corner2 == null) {
            instance.getDebug().log(
                    "DomainAPI",
                    "resizeField",
                    instance.getLocale().localeText("api_invalid_corner2", "Invalid Location for Corner 2", null)
            );
            return false;
        } else {
            instance.getDebug().log("DomainAPI", "resizeField", "New Corner 2: " + WorldFunctions.simpleReadable(corner2));
            domainBlock.setCorner2(corner2);
        }
        instance.getDebug().log("DomainAPI", "resizeField", "Saving domain block");
        domainBlockManager.domainBlockSave(domainBlock);

        Player player = Bukkit.getPlayer(playerUUID);
        BlockCommands blockCommands = new BlockCommands(instance);
        blockCommands.updateField(player, domainBlock);
        if (player != null) {
            Visualize visualize = new Visualize(instance);
            visualize.displayField(
                    player,
                    domainBlock,
                    0,
                    true,
                    true,
                    true
            );

            player.getWorld().playEffect(domainBlock.getBlockLocation(), Effect.ENDERDRAGON_SHOOT, 10);
            instance.getMessage().send(player,"api_resize_success", "&cYour field has been automatically resized, please contact a member of staff for questions", null, true);
        }

        instance.getDebug().log(
                "DomainAPI",
                "resizeField",
                instance.getLocale().localeText("api_resize_success", "Resize has been completed", null)
        );
        return true;
    }

    /**
     * Returns a List<Block> of all blocks in a field.  Returns null if Domain block does not exist
     * @param domainBlockId Domain block identifier
     * @return List of blocks in the field
     */
    public List<Block> getAllBlocksInField(int domainBlockId) {
        DomainBlock domainBlock = domainBlockManager.getDomainBlockByID(domainBlockId);
        List<Block> blocks = new ArrayList<>();
        if (domainBlock != null) {
            blocks = DomainFunctions.getBlocksInRegion(domainBlock.getCorner1(), domainBlock.getCorner2());
            if (blocks != null) {
                instance.getDebug().log(
                        "FieldFunctions",
                        "getAllBlocksInField",
                        instance.getLocale().localeText("api_getAllBlocksInField_success", "Blocks found", null)
                );
                return blocks;
            } else {
                instance.getDebug().log(
                        "FieldFunctions",
                        "getAllBlocksInField",
                        instance.getLocale().localeText("api_getAllBlocksInField_failblocks", "No blocks found", null)
                );
                return blocks;
            }
        } else {
            instance.getDebug().log(
                    "FieldFunctions",
                    "getAllBlocksInField",
                    instance.getLocale().localeText("api_getAllBlocksInField_fail", "Invalid Placed Domain Block", null)
            );
            return blocks;
        }
    }

    /**
     * Checks if the location provided is inside a Domain field
     * @param location Location to check
     * @return If true, location is inside a Domain field
     */
    public boolean locationInField(Location location) {

        if (location != null) {
            if (domainBlockManager.inField(location)) {
                instance.getDebug().log(
                        "FieldFunctions",
                        "getAllBlocksInField",
                        instance.getLocale().localeText("api_locationInField_yes", "Location is in a field", null)
                );
                return true;
            } else {
                instance.getDebug().log(
                        "FieldFunctions",
                        "getAllBlocksInField",
                        instance.getLocale().localeText("api_locationInField_yes", "Location is NOT in a field", null)
                );
                return false;
            }
        } else {
            instance.getDebug().log(
                    "FieldFunctions",
                    "getAllBlocksInField",
                    instance.getLocale().localeText("api_invalid_location", "Invalid Location", null)
            );
            return false;
        }
    }

    /**
     * Gets the Domain Block for the given identifier
     * @param domainBlockId Domain Block Identifier
     * @return Domain Block, returns null if does not exist
     */
    public DomainBlock getPlacedDomainBlockByID(int domainBlockId) {
        DomainBlock domainBlock = domainBlockManager.getDomainBlockByID(domainBlockId);
        if (domainBlock != null) {
            instance.getDebug().log(
                    "FieldFunctions",
                    "getAllBlocksInField",
                    instance.getLocale().localeText("api_getPlacedDomainBlockByID_success", "Domain block found", null)
            );
            return domainBlock;
        } else {
            instance.getDebug().log(
                    "FieldFunctions",
                    "getAllBlocksInField",
                    instance.getLocale().localeText("api_getPlacedDomainBlockByID_fail", "Invalid Placed Domain Block", null)
            );
            return null;
        }
    }

    /**
     * Searches for placed Domain Blocks by owner name, block key, or world name.  Search terms can be combined. Send "" for unused search criteria
     * @param owner Player name
     * @param blockKey Block key
     * @param worldName World Name
     * @return List of DomainBlocks found
     */
    public List<DomainBlock> getPlacedDomainBlocksSearch(String owner, String blockKey, String worldName) {

        List<DomainBlock> lstDomainBlocks = domainBlockManager.searchBlocks(owner, blockKey, worldName);
        if (lstDomainBlocks != null) {
            if (lstDomainBlocks.size() > 0) {
                instance.getDebug().log(
                        "FieldFunctions",
                        "getAllBlocksInField",
                        instance.getLocale().localeText("api_getDomainBlockItemStack_success", "Domain blocks found", null)
                );
                return lstDomainBlocks;
            } else {
                instance.getDebug().log(
                        "FieldFunctions",
                        "getAllBlocksInField",
                        instance.getLocale().localeText("api_getDomainBlockItemStack_fail", "No Domain blocks found", null)
                );
                return new ArrayList<>();
            }
        } else {
            instance.getDebug().log(
                    "FieldFunctions",
                    "getAllBlocksInField",
                    instance.getLocale().localeText("api_getDomainBlockItemStack_fail", "No Domain blocks found", null)
            );
            return new ArrayList<>();
        }
    }
    //endregion
    //region Flag

    /**
     * Checks if a player is allowed to perform the action prohibited by the flag provided
     * @param player Player
     * @param location Location to check
     * @param flag Flag to check
     * @return If false, the action is not allowed. If true, either allowed, or flag does not exist here
     */
    public boolean flagAllowed(Player player, Location location, String flag) {
        if (PlayerFunctions.hasBypass(PlayerFunctions.playerFromEntity(player))) {
            return true;
        }

        Flag.Type domainFlag = flagFromName(flag);
        if (domainFlag != null) {
            if (domainBlockManager.hasFlag(location, domainFlag)) {
                // Flag exists at this location
                if (domainBlockManager.isAllowed(player, location)) {
                    // Flag exists, but player is allowed here
                    return true;
                } else {
                    // Flag exists and player is not allowed here
                    return false;
                }
            } else {
                // Flag does not exist at this location, action is allowed
                return true;
            }
        } else {
            // No such flag, action is allowed
            return true;
        }
    }

    /**
     * Checks if the flag is active at the location provided
     * @param flag Flag
     * @param location Location
     * @return If true, flag is active at this location
     */
    public boolean flagAtLocation(Flag.Type flag, Location location) {
        if (flag == null) {
            instance.getDebug().log(
                    "FlagFunctions",
                    "flagAtLocation",
                    instance.getLocale().localeText("api_invalid_flag", "Invalid Flag", null)
            );
            return false;
        }
        if (location == null) {
            instance.getDebug().log(
                    "FlagFunctions",
                    "flagAtLocation",
                    instance.getLocale().localeText("api_invalid_location", "Invalid Location", null)
            );
            return false;
        }
        if (domainBlockManager.hasFlag(location, flag)) {
            instance.getDebug().log(
                    "FlagFunctions",
                    "flagAtLocation",
                    instance.getLocale().localeText("api_flatAtLocation_yes", "This flag exists and is active at this location", null)
            );
            return true;
        } else {
            instance.getDebug().log(
                    "FlagFunctions",
                    "flagAtLocation",
                    instance.getLocale().localeText("api_flatAtLocation_yes", "This flag either does not exist or is not active at this location", null)
            );
            return false;
        }
    }

    /**
     * Gets a Flag.Type object from a flag name
     * @param flag Flag name
     * @return Flag.Type, if not found, returns null
     */
    public Flag.Type flagFromName(String flag) {
        Flag.Type flagType = Flag.Type.valueOf(flag);
        if (flagType != null) {
            instance.getDebug().log(
                    "FlagFunctions",
                    "flagAtLocation",
                    instance.getLocale().localeText("api_flagFromName_success", "Flag found", null)
            );
            return flagType;
        } else {
            instance.getDebug().log(
                    "FlagFunctions",
                    "flagAtLocation",
                    instance.getLocale().localeText("api_flagFromName_fail", "Flag not found", null)
            );
            return null;
        }
    }

    /**
     * Gets a list of all Flag.Type objects available
     * @return List of Flag types
     */
    public List<Flag.Type> getFlags() {
        List<Flag.Type> flags = new ArrayList<>();
        for(Flag.Type flag : Flag.Type.values()) {
            flags.add(flag);
        }
        return flags;
    }
    //endregion
    //region Item

    /**
     * Returns an ItemStack with 1 Domain Block for the block key provided.
     * @param blockKey Block Key
     * @return Domain Block ItemStack, returns an AIR block if invalid Block type
     */
    public ItemStack getDomainBlockItemStack(String blockKey) {

        ItemStack itemStack = Domain.getInstance().getData().getAvailableBlocksItemStack(blockKey);
        if (!itemStack.getType().equals(Material.AIR))
        {
            itemStack.setAmount(1);
            instance.getDebug().log(
                    "ItemFunctions",
                    "getDomainBlockItemStack",
                    instance.getLocale().localeText("api_getDomainBlockItemStack_success", "Domain block found", null)
            );
            return itemStack;
        } else {
            instance.getDebug().log(
                    "ItemFunctions",
                    "getDomainBlockItemStack",
                    instance.getLocale().localeText("api_getDomainBlockItemStack_invalid_blockKey", "Invalid Block Key", null)
            );
            return new ItemStack(Material.AIR, 1);
        }
    }
    //endregion
    //region Misc
    /**
     * Gets the version of Domain
     * @return Version
     */
    public String getVersion() {
        return Domain.getInstance().config().getDisplayName() + " - " + Domain.getInstance().getDescription().getVersion();
    }
    //endregion
    //region Player
    /**
     * Returns a list of all the player owned fields
     * @param playerUUID Player Unique Identifier to lookup
     * @return List of DomainBlock objects owned by specified player
     */
    public List<DomainBlock> getPlayerDomainFields(UUID playerUUID) {
        Set<DomainBlock> fields = domainBlockManager.getOwnedDomainBlocks(playerUUID, false);
        if (fields == null) {
            return new ArrayList<>();
        }
        if (fields.size() == 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(fields);
    }

    /**
     * Returns a Player object from a string Player unique identifier. Added for Skript which treats UUID as String.  Returns null if invalid
     * @param playerUUID Player unique identifier in string format
     * @return Player object
     */
    public Player playerFromUUIDString(String playerUUID) {
        return Bukkit.getPlayer(UUID.fromString(playerUUID));
    }

    /**
     * Checks if a player is allowed at the location provided
     * @param location Location
     * @param player Player
     * @return If true, player is allowed
     */
    public boolean playerAllowedAtLocation(Location location, Player player) {
        // If player is null, exit out false
        if (player == null) {
            return false;
        }
        // If location is not null
        if (location != null) {
            // Check if allowed at location
            if (domainBlockManager.isAllowed(player, location)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    //endregion
}
