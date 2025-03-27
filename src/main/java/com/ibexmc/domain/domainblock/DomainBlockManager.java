package com.ibexmc.domain.domainblock;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.config.IFieldConfig;
import com.ibexmc.domain.domainblock.rent.RentManager;
import com.ibexmc.domain.domainblock.storage.StorageManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.gui.MainGUI;
import com.ibexmc.domain.helper.DomainFunctions;
import com.ibexmc.domain.util.*;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import com.ibexmc.internal.functions.*;
import com.ibexmc.internal.utility.IbexPersistentData;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.ibexmc.domain.api.events.domain.DomainFieldCreateEvent;
import com.ibexmc.myriad.Myriad;

import java.io.File;
import java.util.*;

public class DomainBlockManager {

    Domain instance;

    public DomainBlockManager(Domain instance) {
        this.instance = instance;
    }

    /**
     * Creates a new Domain field
     * @param player Player creating the field
     * @param fieldConfig Block configuration to use
     * @param location Location of the domain block
     * @param parentPlacedId The field parent if there is one
     * @return Domain Block, null if the event gets cancelled
     */
    public DomainBlock create(Player player, IFieldConfig fieldConfig, Location location, int parentPlacedId) {
        // Call the DomainCreateEvent
        DomainFieldCreateEvent createEvent = new DomainFieldCreateEvent(player, location.getBlock(), fieldConfig.getBlockKey());
        Bukkit.getServer().getPluginManager().callEvent(createEvent);
        if (!createEvent.isCancelled()) {
            DomainBlock domainBlock = new DomainBlock(fieldConfig.getBlockKey());
            String defaultName = fieldConfig.getDefaultNewName();
            defaultName = defaultName.replace("<%player%>", player.getDisplayName());
            defaultName = defaultName.replace("<%type%>", fieldConfig.getDisplayName());

            domainBlock.setFieldName(defaultName);
            domainBlock.setWelcomeMessage(
                    Domain.getInstance().config().getDefaultWelcome().replace(
                            "<%type%>", fieldConfig.getDisplayName()
                    ).replace(
                            "<%player%>", player.getName()
                    )
            );
            domainBlock.setFarewellMessage(
                    Domain.getInstance().config().getDefaultFarewell().replace(
                            "<%type%>", fieldConfig.getDisplayName()
                    ).replace(
                            "<%player%>", player.getName()
                    )
            );
            domainBlock.setOwner(player.getUniqueId());
            domainBlock.addAllowedPlayer(player.getUniqueId());
            domainBlock.setBlockLocation(location);
            if (domainBlock.getConfig().isSnapToChunk()) {
                Location[] corners = DomainFunctions.chunkCorners(location);
                if (corners[0] != null && corners[1] != null) {
                    domainBlock.setCorner1(corners[0]);
                    domainBlock.setCorner2(corners[1]);
                }
            } else {
                if (fieldConfig.isIgnoreYAxis()) {
                    domainBlock.setCorner1(
                            new Location(
                                    location.getWorld(),
                                    location.getBlockX() - fieldConfig.getDefaultRadius(),
                                    location.getWorld().getMinHeight(),
                                    location.getBlockZ() - fieldConfig.getDefaultRadius()
                            )
                    );
                } else {
                    domainBlock.setCorner1(
                            new Location(
                                    location.getWorld(),
                                    location.getBlockX() - fieldConfig.getDefaultRadius(),
                                    location.getBlockY() - fieldConfig.getDefaultHeightDown(),
                                    location.getBlockZ() - fieldConfig.getDefaultRadius()
                            )
                    );
                }
                if (fieldConfig.isIgnoreYAxis()) {
                    domainBlock.setCorner2(
                            new Location(
                                    location.getWorld(),
                                    location.getBlockX() + fieldConfig.getDefaultRadius(),
                                    location.getWorld().getMaxHeight(),
                                    location.getBlockZ() + fieldConfig.getDefaultRadius()
                            )
                    );
                } else {
                    domainBlock.setCorner2(
                            new Location(
                                    location.getWorld(),
                                    location.getBlockX() + fieldConfig.getDefaultRadius(),
                                    location.getBlockY() + fieldConfig.getDefaultHeightUp(),
                                    location.getBlockZ() + fieldConfig.getDefaultRadius()
                            )
                    );
                }
            }
            domainBlock.setPlacedLongTimestamp(TimeFunctions.now().getTime());
            domainBlock.setPlacedBy(player.getUniqueId());
            if (parentPlacedId > 0) {
                domainBlock.setParentPlacedId(parentPlacedId);
            }
            if (fieldConfig.getRentCost() > 0) {
                domainBlock.setRentCost(fieldConfig.getRentCost());
            }
            if (fieldConfig.getRentDays() > 0) {
                domainBlock.setRentDays(fieldConfig.getRentDays());
            }

            if (!isNewFieldValid(fieldConfig, player, location, domainBlock.getCorner1(), domainBlock.getCorner2())) {
                return null;
            }

            domainBlockSave(domainBlock);

            Visualize visualize = new Visualize(instance);
            visualize.clearVisualization(player); // Clear existing visualizations (including the Domain block mask)
            visualize.maskBlock(domainBlock.getPlacedId(), player, Material.BEACON);

            if (fieldConfig.getFieldDisplaySecs() > 0) {
                visualize.displayField(
                        player,
                        domainBlock,
            0,
                        true,
                        false,
                        true
                );
            }

            Map<String, String> placeHolders = new HashMap<>();
            placeHolders.put("<%blocktype%>", "&r" + fieldConfig.getDisplayName() + "&r");

            Domain instance = Domain.getInstance();

            instance.getMessage().send(player, "block_place_domainblock_success", "&2SUCCESS &r- Your <%blocktype%> field has been placed", placeHolders, true);
            if (!domainBlock.getConfig().isPreventResize()) {
                instance.getMessage().send(player, "block_place_domainblock_success_resize", "To resize this field, right click the Domain block without anything in your main hand, then select Resize", null, true);
            }

            return domainBlock;
        } else {
            // Create event cancelled
            return null;
        }
    }

    /**
     * Gets the file for the specified placedId
     * @param placedId Domain Block Identifier to get file for
     * @return File
     */
    public File getFile(int placedId) {
        return new File(
                Domain.getInstance().getDataFolder() +
                        File.separator + "Data" +  File.separator + placedId + ".yml"
        );
    }

    /**
     * Saves the Domain field
     * @param domainBlock Domain field to save
     */
    public void domainBlockSave(DomainBlock domainBlock) {
        StorageManager storageManager = new StorageManager(instance);
        storageManager.save(domainBlock);
        instance.getData().placedFields.put(domainBlock.getPlacedId(), domainBlock);
    }

    /**
     * Gets a Domain block from the list by id
     * @param placedId Identifier to lookup
     * @return Domain Block, if not found, returns null
     */
    public DomainBlock getDomainBlockById(int placedId) {
        if (Domain.getInstance().getData().placedFields.containsKey(placedId)) {
            return Domain.getInstance().getData().placedFields.get(placedId);
        }
        return null;
    }

    /**
     * Gets all Domain blocks currently loaded
     * @return HashSet of placed Domain Blocks
     */
    public Set<DomainBlock> getAllDomainBlocks() {
        Set<DomainBlock> domainBlocks = new HashSet<>();
        for (Map.Entry<Integer, DomainBlock> entry : Domain.getInstance().getData().placedFields.entrySet()) {
            domainBlocks.add(entry.getValue());
        }
        return domainBlocks;
    }

    /**
     * Gets a hashset of owned Domain Blocks
     * @param uuid Player Unique Identifier to lookup
     * @param includeRented If true, include rented as owned
     * @return HashSet of Domain Blocks
     */
    public Set<DomainBlock> getOwnedDomainBlocks(UUID uuid, boolean includeRented) {
        Set<DomainBlock> returnSet = new HashSet<>();
        Set<DomainBlock> domainBlocks = this.getAllDomainBlocks();
        for (DomainBlock domainBlock : domainBlocks) {
            if (!domainBlock.isAdminBlock()) {
                if (domainBlock.getOwner() != null) {
                    if (domainBlock.getOwner().equals(uuid)) {
                        returnSet.add(domainBlock);
                    }
                }
                if (includeRented) {
                    if (domainBlock.getRenter() != null) {
                        if (domainBlock.getRenter().equals(uuid)) {
                            returnSet.add(domainBlock);
                        }
                    }
                }
            }
        }
        return returnSet;
    }

    /**
     * Gets the visualization type for a Domain Block
     * @param domainBlock Domain Block to check
     * @return Visualization Type
     */
    public Visualize.VisualizationType getVisualizationTypeFromConfig(DomainBlock domainBlock) {
        return domainBlock.getConfig().getVisualizationType();
    }

    /**
     * Gets the Domain Block at the block provided, if not a Domain Block, returns null
     * @param block Block to check
     * @return Domain Block
     */
    public DomainBlock getFromBlock(Block block) {
        Domain instance = Domain.getInstance();
        for (DomainBlock domainBlock : getAllDomainBlocks()) {
            instance.getDebug().log(
                    "DomainBlockManager",
                    "getFromBlock",
                    "Checking " + domainBlock.getPlacedId() + "\n" +
                            "Domain block: " + WorldFunctions.simpleReadable(domainBlock.getBlockLocation().clone()) + "\n" +
                            "Clicked block: " + WorldFunctions.simpleReadable(block.getLocation().clone())
                    );
            if (WorldFunctions.blockLocationMatch(domainBlock.getBlockLocation().getBlock(), block)) {
                return domainBlock;
            }
        }
        return null;
    }

    /**
     * Checks if the player has access to this field
     * @param player Plaayer to check
     * @param domainBlock Domain Block to check
     * @return If true, player has access
     */
    public boolean playerHasAccessToField(Player player, DomainBlock domainBlock) {
        return (
                PlayerFunctions.hasBypass(player) ||
                        isOwner(player, domainBlock) ||
                        isRenter(player, domainBlock)
        );
    }

    /**
     * Checks if the specified player is the owner of this field
     * @param player Player to check
     * @param domainBlock Domain Block to check
     * @return If true, player is hte owner
     */
    public boolean isOwner(Player player, DomainBlock domainBlock) {
        if (domainBlock.isAdminBlock()) {
            if (Permission.hasPermission(player, "domain.admin")) {
                return true;
            }
        } else {
            if (player.getUniqueId().equals(domainBlock.getOwner())) {
                return true;
            }
        }
        if (PlayerFunctions.hasBypass(player)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the specified player is the renter of this field
     * @param player Player to check
     * @param domainBlock Domain Block to check
     * @return If true, player is the renter
     */
    public boolean isRenter(Player player, DomainBlock domainBlock) {
        if (domainBlock.getRenter() != null) {
            if (player.getUniqueId().equals(domainBlock.getRenter())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Opens the Main GUI for the Domain Block
     * @param player Player to open GUI for
     * @param domainBlock Domain Block to open
     * @return If true, GUI opened successfully
     */
    public boolean openBlock(Player player, DomainBlock domainBlock) {
        Domain instance = Domain.getInstance();
        if (playerHasAccessToField(player, domainBlock)) {
            instance.getDebug().log("DomainBlockManager", "openBlock", "&aOpening block");
            MainGUI gui = new MainGUI(
                    instance,
                    domainBlock,
                    player
            );
            player.openInventory(gui.getInventory());
            SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
            return true;
        } else {
            instance.getDebug().log("DomainBlockManager", "openBlock", "&cNOT Opening block");
            player.getWorld().playEffect(player.getLocation(), Effect.EXTINGUISH, 10);
            instance.getMessage().send(player, "cannot_open_for_commands", "&4You cannot open this Domain block", null, true);

            DomainParticles.denyBlock(domainBlock.getBlockLocation().clone());
            return false;
        }
    }

    /**
     * Checks if the ItemStack has the domain\wand NBT tag
     * @param itemStack ItemStack to check
     * @return If true, ItemStack has the domain\wand NBT tag
     */
    public boolean hasBlockTag(ItemStack itemStack) {
        IbexPersistentData ibexPersistentData = new IbexPersistentData(instance);
        return ibexPersistentData.hasString(itemStack.getItemMeta(), "domain_block");
    }

    /**
     * Gets the wand tag from ItemStack - Gets the domain\wand NBT tag
     * @param itemStack ItemStack to check
     * @return Wand Tag key from NBT tag
     */
    public IFieldConfig getConfigFromItem(ItemStack itemStack) {
        IbexPersistentData persistentData = new IbexPersistentData(instance);
        if (persistentData.hasString(itemStack.getItemMeta(), "domain_block")) {
            String key = persistentData.getString(itemStack.getItemMeta(), "domain_block");
            IFieldConfig config = Domain.getInstance().getData().getDomainFieldConfig(key);
            if (config != null) {
                return config;
            } else {
                // error
                return null;
            }
        }
        return null;
    }

    /**
     * Checks if a new field is valid
     * @param config Domain Block Configuration
     * @param player Player placing the field
     * @param location Block Location
     * @param corner1 Corner 1
     * @param corner2 Corner 2
     * @return If true, field is valid
     */
    public boolean isNewFieldValid(IFieldConfig config, Player player, Location location, Location corner1, Location corner2) {
        // Check overlap
        DomainBlock testDomainBlock = new DomainBlock(config.getBlockKey());
        testDomainBlock.setBlockLocation(location);
        testDomainBlock.setCorner1(corner1);
        testDomainBlock.setCorner2(corner2);
        boolean cancelOverlap = this.hasOverlap(player, testDomainBlock);
        if (cancelOverlap) {
            if (!PlayerFunctions.hasBypass(player)) {
                instance.getMessage().send(
                        player,
                        "valid_resize_overlap",
                        "&4Field invalid.  Fields belonging to different players cannot overlap",
                        null,
                        true
                );
                return false;
            }
        }
        // Rentable field overlap
        List<Location> lstCorners = DomainFunctions.getCorners(corner1, corner2);
        for (Location l : lstCorners) {
            List<DomainBlock> domainBlocks = this.playerDomainBlocks(l);
            if (domainBlocks != null) {
                for (DomainBlock domainBlock : domainBlocks) {
                    if (config.getBlockKey().equals(domainBlock.getConfig().getBlockKey())) {
                        if (config.getRentCost() > 0 && config.getRentDays() > 0) {
                            instance.getMessage().send(
                                    player,
                                    "valid_resize_rent_overlap",
                                    "&4Field invalid.  Rentable fields cannot overlap",
                                    null,
                                    true
                            );
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * If true, the ItemStack provided will create a domain field when placed
     * @param itemStack ItemStack to check
     * @returnIf true, ItemStack is a Domain Block
     */
    public boolean isItemIsDomainBlock(ItemStack itemStack) {
        if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                for (IFieldConfig c : Domain.getInstance().getData().getDomainFieldConfigs()) {
                    if (ChatColor.stripColor(itemMeta.getDisplayName()).equals(ChatColor.stripColor(ColorFunctions.colorToString(c.getDisplayName())))) {
                        if (itemStack.getType().equals(c.getBaseMaterial())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * If true, the Block provided is a Domain Block
     * @param block Block to check
     * @return If true, Block is a Domain Block
     */
    public boolean isDomainBlock(Block block) {
        boolean retVal = false;
        if (block != null) {
            //instance.getDebug().log("Check", "isDomainBlock", "Checking if block is a domain block");
            Set<DomainBlock> lstPB = this.getAllDomainBlocks();
            if (lstPB != null)
            {
                if (lstPB.size() > 0)
                {
                    for (DomainBlock pb : lstPB)
                    {
                        if (WorldFunctions.blockLocationMatch(pb.getBlockLocation().getBlock(), block)) {
                            instance.getDebug().log("Check", "isDomainBlock", "Block Matched");
                            retVal = true;
                        }
                    }
                }
            }
        } else {
            instance.getDebug().log("Check", "isDomainBlock", "Block is null");
        }
        return retVal;
    }

    /**
     * Checks if there the domain field provided has the flag provided and active
     * @param domainBlock Domain Block to check
     * @param flag Flag to look for
     * @return If true, flag exists and is active
     */
    public boolean hasActiveFlag(DomainBlock domainBlock, Flag.Type flag) {
        if (domainBlock.getConfig().getFlags() != null) {
            for (Flag fbp : domainBlock.getConfig().getFlags()) {
                if (fbp.getFlagType().equals(flag) && fbp.isActive()) {
                    return true;
                }
            }
        }
        if (domainBlock.getFlags() != null) {
            for (Flag f : domainBlock.getFlags()) {
                if (f.getFlagType().equals(flag) && f.isActive()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasOverlap(Player player, DomainBlock domainBlock) {
        boolean returnVal = false;
        int sourceMinX = Math.min(domainBlock.getCorner1().getBlockX(), domainBlock.getCorner2().getBlockX());
        int sourceMaxX = Math.max(domainBlock.getCorner1().getBlockX(), domainBlock.getCorner2().getBlockX());
        int sourceMinY = Math.min(domainBlock.getCorner1().getBlockY(), domainBlock.getCorner2().getBlockY());
        int sourceMaxY = Math.max(domainBlock.getCorner1().getBlockY(), domainBlock.getCorner2().getBlockY());
        int sourceMinZ = Math.min(domainBlock.getCorner1().getBlockZ(), domainBlock.getCorner2().getBlockZ());
        int sourceMaxZ = Math.max(domainBlock.getCorner1().getBlockZ(), domainBlock.getCorner2().getBlockZ());
        Set<DomainBlock> domainBlocks = this.getAllDomainBlocks();
        if (domainBlocks != null) {

            instance.getDebug().log("Check", "overlap", "Domain blocks found = " + domainBlocks.size());

            for (DomainBlock targetBlock : domainBlocks) {
                instance.getDebug().log("Check", "overlap", "Checking: " + targetBlock.getFieldName());

                if (!this.isOwner(player, targetBlock) || targetBlock.isAdminBlock()) {
                    if (domainBlock.getPlacedId() == targetBlock.getPlacedId()) {
                        break;
                    }
                    if (targetBlock.getBlockLocation().getWorld().getName().equalsIgnoreCase(domainBlock.getBlockLocation().getWorld().getName())) {
                        int targetMinX = Math.min(targetBlock.getCorner1().getBlockX(), targetBlock.getCorner2().getBlockX());
                        int targetMaxX = Math.max(targetBlock.getCorner1().getBlockX(), targetBlock.getCorner2().getBlockX());
                        int targetMinY = Math.min(targetBlock.getCorner1().getBlockY(), targetBlock.getCorner2().getBlockY());
                        int targetMaxY = Math.max(targetBlock.getCorner1().getBlockY(), targetBlock.getCorner2().getBlockY());
                        int targetMinZ = Math.min(targetBlock.getCorner1().getBlockZ(), targetBlock.getCorner2().getBlockZ());
                        int targetMaxZ = Math.max(targetBlock.getCorner1().getBlockZ(), targetBlock.getCorner2().getBlockZ());
                        instance.getDebug().log("Check", "overlap", "Checking X: " + sourceMinX + " - " + sourceMaxX);
                        instance.getDebug().log("Check", "overlap", "Against X: " + targetMinX + " - " + targetMaxX);
                        if (DomainFunctions.testOverlap(sourceMinX, sourceMaxX, targetMinX, targetMaxX)) {
                            instance.getDebug().log("Check", "overlap", "X Ranges overlapped");
                            instance.getDebug().log("Check", "overlap", "Checking Y: " + sourceMinY + " - " + sourceMaxY);
                            instance.getDebug().log("Check", "overlap", "Against Y: " + targetMinY + " - " + targetMaxY);
                            if (DomainFunctions.testOverlap(sourceMinY, sourceMaxY, targetMinY, targetMaxY)) {
                                instance.getDebug().log("Check", "overlap", "Y Ranges overlapped");
                                instance.getDebug().log("Check", "overlap", "Checking Z: " + sourceMinZ + " - " + sourceMaxZ);
                                instance.getDebug().log("Check", "overlap", "Against Z: " + targetMinZ + " - " + targetMaxZ);
                                if (DomainFunctions.testOverlap(sourceMinZ, sourceMaxZ, targetMinZ, targetMaxZ)) {
                                    instance.getDebug().log("Check", "overlap", "Z Rangeds overlapped");
                                    HashMap<String, String> hmPlaceHolder = new HashMap<>();
                                    hmPlaceHolder.put("%fieldname%", targetBlock.getFieldName());


                                    instance.getDebug().log("Check", "overlap", "&bOVERLAP FOUND");

                                    boolean lockedToOverride = false;

                                    if (targetBlock.isAdminBlock()) {
                                        instance.getDebug().log("Check", "overlap", "Target block (" + targetBlock.getFieldName() + ") is an admin block");

                                        instance.getDebug().log("Check", "overlap", "Domain block locked to: " + domainBlock.getConfig().getLockedTo());


                                        if (domainBlock.getConfig().getLockedTo().equalsIgnoreCase(targetBlock.getConfig().getBlockKey())) {
                                            instance.getDebug().log("Check", "overlap", "No overlap as Domain block is locked to the target block and it is an admin field");
                                            lockedToOverride = true;
                                            returnVal = false;
                                        }
                                    }

                                    if (!lockedToOverride) {
                                        if (!PlayerFunctions.hasBypass(player)) {
                                            instance.getMessage().send(player, "block_create_overlap", "&4ERROR - &rUnable to create field here - field would overlap %fieldname%", hmPlaceHolder, true);
                                            instance.getLog().log(
                                                    "DomainBlockManager",
                                                    ColorFunctions.colorToString("&4Domain Overlap Check"),
                                                    player.getName() + " tried to place a field that would overlap another players field at " + WorldFunctions.simpleFancyReadable(this.getLocationFromDomainBlock(domainBlock)));
                                            returnVal = true;
                                        } else {
                                            instance.getMessage().send(
                                                    player,
                                                    "valid_resize_overlap_bypass",
                                                    "&4BYPASS WARNING - Fields belonging to different players should not overlap, but can be set as you are in bypass mode",
                                                    null,
                                                    true
                                            );
                                            instance.getLog().log("DomainBlockManager", ColorFunctions.colorToString("&4Domain Overlap Check"), player.getName() + " was allowed to place a field that would overlap another players field at " + WorldFunctions.simpleFancyReadable(this.getLocationFromDomainBlock(domainBlock)) + ColorFunctions.colorToString("&r due to bypass mode being enabled"));
                                            return false;
                                        }
                                    }
                                } else {
                                    instance.getDebug().log("Check", "overlap", "Z Ranges did not overlap");
                                }
                            } else {
                                instance.getDebug().log("Check", "overlap", "Y Ranges did not overlap");
                            }
                        } else {
                            instance.getDebug().log("Check", "overlap", "X Ranges did not overlap");
                        }
                    }
                }
            }
        }
        return returnVal;
    }

    public boolean hasFlag(Flag.Type flag, Map<Flag.Type, Boolean> flagsAtLocation) {
        boolean returnVal = false;
        if (flagsAtLocation != null)
        {
            if (flagsAtLocation.isEmpty()) {
                return false;
            }
            if (flagsAtLocation.get(flag) != null)
            {
                //instance.getDebug().log("Check", "HasFlag", flag.name() + " | " + Boolean.toString(hmFlags.get(flag)));
                if (flagsAtLocation.get(flag))
                {
                    returnVal = true;
                }
            }
        }
        //instance.getDebug().log("Check", "HasFlag", "Flag: " + flag.toString() + " = " + Boolean.toString(returnVal));
        return returnVal;
    }
    public boolean hasFlag(Location loc, Flag.Type flag) {
        boolean returnVal = false;
        if (loc != null) {
            Map<Flag.Type, Boolean> hmFlags = this.getFlagsAtLocation(loc, flag);
            if (hmFlags != null)
            {
                if (hmFlags.get(flag) != null)
                {
                    //instance.getDebug().log("Check", "HasFlag", flag.name() + " | " + Boolean.toString(hmFlags.get(flag)));
                    if (hmFlags.get(flag))
                    {
                        returnVal = true;
                    }
                }
            }
        }
        //instance.getDebug().log("Check", "HasFlag", "Flag: " + flag.toString() + " = " + Boolean.toString(returnVal));
        return returnVal;
    }

    public boolean isClanAllowed(DomainBlock pb, Player p) {
        boolean ret = false;
        if (Domain.getInstance().getDependencies().hasMyriadClans())
        {
            if (pb.getAllowedClans() == null)
            {
                return false;
            }
            Myriad mc = Domain.getInstance().getDependencies().getMyriadClans();
            if (mc != null) {
                try {
                    String tag = mc.getAPI().getPlayerClanTag(p.getUniqueId());

                    if (tag.equals("")) {
                        // player is not in a clan
                        return false;
                    }
                    for (String ac : pb.getAllowedClans()) {
                        if (ac.equalsIgnoreCase(tag)) {
                            instance.getDebug().log("Check", "isClanAllowed(DomainBlock, Player)", "Clan found");
                            ret = true;
                            break;
                        }
                    }
                } catch (Exception ex) {
                    instance.getDebug().log("Check", "isClanAllowed(DomainBlock, Player)", "Myriad Clans returned null, but Domain said it was loaded");
                }
            } else {
                instance.getDebug().log("Check", "isClanAllowed(DomainBlock, Player)", "Myriad Clans returned null, but Domain said it was loaded");
            }
        }
        return ret;
    }

    public boolean inField(Location loc) {
        boolean returnVal = false;
        if (loc != null)
        {
            List<DomainBlock> retSet = this.playerDomainBlocks(loc);
            if (retSet != null)
            {
                if (retSet.size() > 0)
                {
                    returnVal = true;
                }
            }
        } else {
            instance.getDebug().log("Check", "InField(Location)", "No valid location supplied");
        }
        return returnVal;
    }
    public boolean inField(Location loc, DomainBlock pb) {
        boolean returnVal = false;
        if (loc != null)
        {
            List<DomainBlock> retSet = this.playerDomainBlocks(loc);
            if (retSet != null)
            {
                if (retSet.size() > 0)
                {
                    for (DomainBlock pblock : retSet)
                    {
                        if (pblock.getPlacedId() == pb.getPlacedId())
                        {
                            returnVal = true;
                        }
                    }
                }
            }
        } else {
            instance.getDebug().log("Check", "InField(Location, DomainBlock)", "No valid location supplied");
        }
        return returnVal;
    }

    public boolean isAllowed(Entity e) {
        //instance.getDebug().log("this.isAllowed(Entity)", "Check called");
        boolean returnVal = false;
        if (e instanceof Player) {
            Player player = (Player) e;
            List<DomainBlock> retSet = this.playerDomainBlocks(e.getLocation());
            if (retSet != null) {
                if (retSet.size() > 0) {
                    DomainBlock pb = retSet.get(retSet.size() - 1);
                    //instance.getDebug().log("Check", "IsAllowed", "DomainBlock ID: " + Integer.toString(pb.getPlacedID()));
                    Set<UUID> lstAllowed = pb.getAllowedPlayers();
                    if (lstAllowed != null) {
                        for (IFieldConfig c : Domain.getInstance().getData().getDomainFieldConfigs()) {
                            if (ChatColor.stripColor(pb.getConfig().getBlockKey()).equals(ChatColor.stripColor(c.getBlockKey()))) {
                                //instance.getDebug().log("Check", "IsAllowed(Player, Location)", "Config Found");
                                if (c.isInheritPermsFromParent()) {
                                    if (pb.getParentPlacedId() > 0) {
                                        //instance.getDebug().log("Check", "IsAllowed(Player, Location)", "Inheriting from parent DomainBlock: " + Integer.toString(pb.getLockedToPlacedID()));
                                        DomainBlock pbparent = this.getDomainBlockByID(pb.getParentPlacedId());
                                        if (pbparent != null) {
                                            //instance.getDebug().log("Check", "IsAllowed(Player, Location)", "Parent Found");
                                            Set<UUID> lstParentAllowed = pbparent.getAllowedPlayers();
                                            if (lstParentAllowed != null) {
                                                for (UUID aparent : lstParentAllowed) {
                                                    //instance.getDebug().log("Check", "IsAllowed(Player, Location)", "Adding: " + aparent.UUID + " from parent");
                                                    if (aparent.equals(player.getUniqueId()))
                                                    {
                                                        //instance.getDebug().log("Check", "IsAllowed", "Player is allowed into field via inheritence");
                                                        returnVal = true;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (this.isClanAllowed(pbparent, player))
                                            {
                                                returnVal = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (player.getUniqueId().toString().equals(pb.getOwner())) {
                            //instance.getDebug().log("Check", "IsAllowed(Entity)", "Player is owner of the field");
                            returnVal = true;
                        }
                        for (UUID a : lstAllowed) {
                            if (a.equals(player.getUniqueId()))
                            {
                                //instance.getDebug().log("this.isAllowed", "Player is allowed into field");
                                returnVal = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return returnVal;
    }
    public boolean isAllowed(Player p, Location loc) {
        boolean returnVal = false;
        try {

            if (Domain.getInstance().getGameData().enderPearlThrown().has(p.getUniqueId())) {
                // If now is not 3 seconds after the last enderpearl was thrown, return false
                if (!TimeFunctions.after(TimeFunctions.now(), TimeFunctions.addSeconds(Domain.getInstance().getGameData().enderPearlThrown().get(p.getUniqueId()), 3))) {
                    instance.getDebug().log("DomainBlockManager", "isAllowed", "Preventing access due to enderpearl being thrown less than 3 seconds ago");
                    return false;
                }
            }



            //instance.getDebug().log("Check", "IsAllowed(Player, Location)", "Check called");
            //if (p != null) {
            //instance.getDebug().log("Check", "IsAllowed(Player, Location)", p.getDisplayName());
            //}
            if (loc != null) {
                //instance.getDebug().log("Check", "IsAllowed(Player, Location)", WorldFunctions.simpleReadable(loc));
                if (loc.getBlock() != null) {
                    if (loc.getBlock().getType() != null) {
                        //instance.getDebug().log("Check", "IsAllowed(Player, Location)", "Block at location: " + loc.getBlock().getType());
                    }
                }
            }

            List<DomainBlock> retSet = this.playerDomainBlocks(loc);
            if (retSet != null) {
                if (retSet.size() > 0)
                {
                    DomainBlock pb = retSet.get(retSet.size() - 1);

                    //instance.getDebug().log("Check", "IsAllowed(Player, Location)", "DomainBlock ID: " + Integer.toString(pb.getPlacedID()));
                    Set<UUID> lstAllowed = pb.getAllowedPlayers();
                    if (lstAllowed != null)
                    {
                        for (IFieldConfig c : Domain.getInstance().getData().getDomainFieldConfigs()) {
                            if (ChatColor.stripColor(pb.getConfig().getBlockKey()).equals(ChatColor.stripColor(c.getBlockKey()))) {
                                //instance.getDebug().log("Check", "IsAllowed(Player, Location)", "Config Found");
                                if (c.isInheritPermsFromParent()) {
                                    if (pb.getParentPlacedId() >= 0) {
                                        //instance.getDebug().log("Check", "IsAllowed(Player, Location)", "Inheriting from parent DomainBlock: " + Integer.toString(pb.getLockedToPlacedID()));
                                        DomainBlock pbparent = this.getDomainBlockByID(pb.getParentPlacedId());
                                        if (pbparent != null) {
                                            //instance.getDebug().log("Check", "IsAllowed(Player, Location)", "Parent Found");
                                            Set<UUID> lstParentAllowed = pbparent.getAllowedPlayers();
                                            if (lstParentAllowed != null) {
                                                for (UUID aparent : lstParentAllowed) {
                                                    //instance.getDebug().log("Check", "IsAllowed(Player, Location)", "Adding: " + aparent.UUID + " from parent");
                                                    if (aparent.equals(p.getUniqueId()))
                                                    {
                                                        //instance.getDebug().log("Check", "IsAllowed(Player, Location)", "Player is allowed into field via inheritence");
                                                        returnVal = true;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (this.isClanAllowed(pbparent, p))
                                            {
                                                returnVal = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //instance.getDebug().log("Check", "IsAllowed(Player, Location)", "Player UUID: " + p.getUniqueId().toString());
                        //instance.getDebug().log("Check", "IsAllowed(Player, Location)", "Owner UUID: " + pb.getOwner());
                        if (p.getUniqueId().toString().equals(pb.getOwner())) {
                            //instance.getDebug().log("Check", "IsAllowed(Player, Location)", "Player is owner of the field");
                            returnVal = true;
                        }
                        for (UUID a : lstAllowed) {
                            if (a.equals(p.getUniqueId()))
                            {
                                //instance.getDebug().log("Check", "IsAllowed(Player, Location)", "Player is allowed into field - DomainBlock ID: " + Integer.toString(pb.getPlacedID()));
                                returnVal = true;
                                break;
                            }
                        }
                    }
                    if (this.isClanAllowed(pb, p))
                    {
                        //instance.getDebug().log("Check", "IsAllowed(Player, Location)", "Allowed due to clan");
                        returnVal = true;
                    }
                } else {
                    //instance.getDebug().log("Check", "IsAllowed(Player, Location)", "No fields found");
                    // No field here - return true
                    return true;
                }
            } else {
                // No field here - return true
                return true;
            }
            //instance.getDebug().log("Check", "IsAllowed(Player, Location)", "Return Value: " + Boolean.toString(returnVal));
        } catch (Exception ex) {
            instance.getLog().log("DomainBlockManager", "isAllowed()", "Unexpected error checking if player is allowed: " + ex.getMessage());
            return false;
        }
        return returnVal;
    }

    public boolean isAllowedToField(Player player, DomainBlock pb){
        boolean returnVal = false;

        if (Domain.getInstance().getGameData().enderPearlThrown().has(player.getUniqueId())) {
            // If now is not 3 seconds after the last enderpearl was thrown, return false
            if (!TimeFunctions.after(TimeFunctions.now(), TimeFunctions.addSeconds(Domain.getInstance().getGameData().enderPearlThrown().get(player.getUniqueId()), 3))) {
                instance.getDebug().log("DomainBlockManager", "isAllowedToField", "Preventing access due to enderpearl being thrown less than 3 seconds ago");
                return false;
            }
        }

        instance.getDebug().log("Check", "IsAllowedToField", "Block owner: " + pb.getOwner() + " | Player uuid: " + player.getUniqueId().toString());
        Set<UUID> lstA = pb.getAllowedPlayers();
        if (lstA != null)
        {
            if (lstA.size() > 0)
            {
                for (UUID a : lstA) {
                    if (a.equals(player.getUniqueId()))
                    {
                        instance.getDebug().log("Check", "IsAllowedToField", "Player is allowed to the field");
                        returnVal = true;
                        break;
                    }
                }
            }
        }
        if (pb.getOwner() != null) { // May be null if admin block
            if (pb.getOwner().equals(player.getUniqueId().toString())) // Owner is always allowed
            {
                instance.getDebug().log("Check", "IsAllowedToField", "Player is owner of this block");
                returnVal = true;
            }
        }
        return returnVal;
    }

    public DomainBlock getCurrentField(Entity e) {
        List<DomainBlock> domainBlockList = this.playerDomainBlocks(e.getLocation());
        int placed_id = -1;
        int maxPriority = 999999;
        if (domainBlockList != null) {
            for (DomainBlock domainField : domainBlockList) {
                if (placed_id == -1) {
                    placed_id = domainField.getPlacedId();
                    maxPriority = domainField.getConfig().getFieldOrder();
                }
                if (domainField.getConfig().getFieldOrder() > maxPriority) {
                    placed_id = domainField.getPlacedId();
                    maxPriority = domainField.getConfig().getFieldOrder();
                }
            }

            for (DomainBlock p : domainBlockList) {
                if (p.getPlacedId() == placed_id) {
                    return p;
                }
            }
        }
        return null;
    }
    public DomainBlock getCurrentField(Player player) {
        List<DomainBlock> domainBlockList = this.playerDomainBlocks(player.getLocation());
        int placed_id = -1;
        int maxPriority = 999999;
        if (domainBlockList != null) {
            for (DomainBlock p : domainBlockList) {
                if (placed_id == -1) {
                    placed_id = p.getPlacedId();
                    maxPriority = p.getConfig().getFieldOrder();
                }
                if (p.getConfig().getFieldOrder() > maxPriority) {
                    placed_id = p.getPlacedId();
                    maxPriority = p.getConfig().getFieldOrder();
                }
            }

            for (DomainBlock p : domainBlockList) {
                if (p.getPlacedId() == placed_id) {
                    return p;
                }
            }
        }
        return null;
    }
    public DomainBlock getCurrentField(Location loc) {
        List<DomainBlock> domainBlockList = this.playerDomainBlocks(loc);
        int highestFieldOrder = -1;
        DomainBlock currentPlacedBlock = null;
        if (domainBlockList != null) {
            for (DomainBlock p : domainBlockList) {
                if (p.getConfig().getFieldOrder() >= highestFieldOrder) {
                    //instance.getDebug().log("Provide", "currentField", "Has higher or equal field order - using this one");
                    currentPlacedBlock = p;
                }
            }
        }
        return currentPlacedBlock;
    }

    public DomainBlock getDomainBlockByID(int placed_id) {
        DomainBlock pblock = null;
        Set<DomainBlock> lstPB = this.getAllDomainBlocks();
        if (lstPB != null)
        {
            for (DomainBlock pb : lstPB)
            {
                if (pb.getPlacedId() == placed_id)
                {
                    pblock = pb;
                }
            }
        }
        return pblock;
    }

    /**
     * Gets the DomainBlock from the block provided
     * Returns null if block is not a Domain Block
     * @param block Block to get DomainBlock from
     * @return DomainBlock, null if not found
     */
    public DomainBlock getDomainBlockFromBlock(Block block) {
        for (DomainBlock domainBlock : getAllDomainBlocks()) {
            if (WorldFunctions.blockLocationMatch(domainBlock.getBlockLocation().getBlock(), block)) {
                return domainBlock;
            }
        }
        return null;
    }

    public Location getLocationFromDomainBlock(DomainBlock domainBlock) {
        return domainBlock.getBlockLocation();
    }
    public Map<Flag.Type, Boolean> getFlagsAtLocation(Location playerLocation) {
        Map<Flag.Type, Boolean> flagMap = new HashMap<>();
        List<DomainBlock> domainBlockList = this.playerDomainBlocks(playerLocation);
        if (domainBlockList != null) {
            Collections.sort(domainBlockList);
            for (DomainBlock domainBlock : domainBlockList)  {
                if (domainBlock.isActive()) {
                    if (domainBlock.getConfig().getFlags() != null) {
                        for (Flag fbp : domainBlock.getConfig().getFlags()) {
                            if (fbp.isActive()) {
                                flagMap.put(fbp.getFlagType(), fbp.isActive());
                            } else {
                                flagMap.put(fbp.getFlagType(), false);
                            }
                        }
                    }
                    if (domainBlock.getFlags() != null) {
                        for (Flag f : domainBlock.getFlags()) {
                            if (f.isActive()) {
                                flagMap.put(f.getFlagType(), f.isActive());
                            } else {
                                flagMap.put(f.getFlagType(), false);
                            }
                        }
                    }
                }
            }
        }
        return flagMap;
    }
    public Map<Flag.Type, Boolean> getFlagsAtLocation(Location playerLocation, Flag.Type flag) {
        Map<Flag.Type, Boolean> returnValue = new HashMap<>();
        Map<Flag.Type, Boolean> flagMap = getFlagsAtLocation(playerLocation);
        if (flagMap.containsKey(flag)) {
            returnValue.put(flag, flagMap.get(flag));
        }
        return returnValue;
    }
    public List<DomainBlock> playerDomainBlocks(Location playerLocation) {
        List<DomainBlock> retSet = new ArrayList<>();
        Set<DomainBlock> lstPB = this.getAllDomainBlocks();
        if (lstPB != null)
        {
            for (DomainBlock domainBlock : lstPB)
            {
                if (WorldFunctions.insideCuboid(playerLocation, domainBlock.getCorner1().clone(), domainBlock.getCorner2().clone()))
                {
                    retSet.add(domainBlock);
                }
            }
            if (retSet != null)
            {
                Collections.sort(retSet, new Comparator<DomainBlock>() {
                    public int compare(DomainBlock left, DomainBlock right)  {
                        return left.getConfig().getFieldOrder() - right.getConfig().getFieldOrder();
                    }
                });
            }
        } else {
            return null;
        }
        return retSet;
    }

    public List<DomainBlock> searchBlocks(String owner, String blockType, String worldName) {
        instance.getDebug().log("Provide", "searchBlocks", "&fOwner: &b" + owner + " &fType: &b" + blockType + " &fWorld: &b" + worldName);

        Set<DomainBlock> allBlocks = this.getAllDomainBlocks();
        List<DomainBlock> domainBlocks = new ArrayList<>();
        if (allBlocks != null) {

            instance.getDebug().log("Provide", "searchBlocks", "allBlocks Size: " + allBlocks.size());

            for (DomainBlock domainBlock : allBlocks) {

                instance.getDebug().log("Provide", "searchBlocks", "&bPlaced Block ID: " + domainBlock.getPlacedId());

                instance.getDebug().log("Provide", "searchBlocks", "&6Checking Placed Block");
                boolean include = false;
                if (owner != "") {
                    instance.getDebug().log("Provide", "searchBlocks", "Checking Owner");
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(domainBlock.getOwner());
                    if (offlinePlayer != null) {
                        if (offlinePlayer.getName() != null) {
                            instance.getDebug().log("Provide", "searchBlocks", "OfflinePlayer is not null.  Name: " + offlinePlayer.getName());
                            if (offlinePlayer.getName().equalsIgnoreCase(owner) ) {
                                instance.getDebug().log("Provide", "searchBlocks", "Owner matched");
                                include = true;
                            } else {
                                instance.getDebug().log("Provide", "searchBlocks", "Owner did not match");
                                include = false;
                            }
                        } else {
                            instance.getDebug().log("Provide", "searchBlocks", "No valid owner");
                            include = false;
                        }
                    }
                    offlinePlayer = null;
                }
                if (blockType != "") {
                    instance.getDebug().log("Provide", "searchBlocks", "Checking Block Type against " + domainBlock.getConfig().getBlockKey());
                    if (blockType.equalsIgnoreCase(domainBlock.getConfig().getBlockKey())) {
                        instance.getDebug().log("Provide", "searchBlocks", "Block type matched");
                        include = true;
                    } else {
                        instance.getDebug().log("Provide", "searchBlocks", "Block type does not match");
                        include = false;
                    }
                }
                if (worldName != "") {
                    instance.getDebug().log("Provide", "searchBlocks", "Checking World Name against " + domainBlock.getBlockLocation().getWorld().getName());
                    if (worldName.equalsIgnoreCase(domainBlock.getBlockLocation().getWorld().getName())) {
                        instance.getDebug().log("Provide", "searchBlocks", "World Name Matched");
                        include = true;
                    } else {
                        instance.getDebug().log("Provide", "searchBlocks", "World Name does not match");
                        include = false;
                    }
                }
                if (include) {
                    instance.getDebug().log("Provide", "searchBlocks", "Including block in search results");
                    domainBlocks.add(domainBlock);
                } else {
                    instance.getDebug().log("Provide", "searchBlocks", "Not including block in search results");
                }
            }
        }
        return domainBlocks;
    }
    public IFieldConfig getDomainConfigFromItemStack(ItemStack itemStack) {
        IFieldConfig blueprint = null;
        if (itemStack != null) {
            blueprint = this.getConfigFromItem(itemStack);
        }
        return blueprint;
    }




    /**
     * Will attempt to kick the specified player from the field
     * Will first take a ring around the block and get the closet corner, and build a 20x20 block at that corner
     * then loops through and finds the first safe teleport location that is NOT inside the field
     * If none is found, will check the players last bed location, if that is not safe, then it will default
     * to spawn.
     * @param player Player to kick out the field
     * @param domainBlock DomainBlock to kick from
     * @return If true, player was kicked out the field
     */
    public boolean kickFromField(Player player, DomainBlock domainBlock) {
        List<Location> corners = DomainFunctions.getRing(domainBlock.getCorner1(), domainBlock.getCorner2(), player.getLocation().getBlockY());
        Location nearestCorner = null;
        Location playerLocation = player.getLocation().clone();
        Location teleportLocation = null;

        if (!Domain.getInstance().config().isIdleFieldDirectToSpawn()) {
            if (corners != null) {
                for (Location location : corners) {
                    if (nearestCorner == null) {
                        nearestCorner = location.clone();
                    }
                    if (playerLocation.distance(location) < playerLocation.distance(nearestCorner)) {
                        nearestCorner = location.clone();
                    }
                }
            }
            if (nearestCorner != null) {
                Location newCorner1 = nearestCorner.add(-10, -10, -10);
                Location newCorner2 = nearestCorner.add(10, 10, 10);
                List<Block> blocks = DomainFunctions.getBlocksInRegion(newCorner1, newCorner2);

                if (blocks != null) {
                    for (Block block : blocks) {
                        if (WorldFunctions.safeTeleport(block.getLocation()) && !WorldFunctions.insideCuboid(block.getLocation(), domainBlock.getCorner1(), domainBlock.getCorner2())) {
                            instance.getDebug().log("DomainBlock", "kickFromField", "Found a safe location to teleport to: " + WorldFunctions.simpleReadable(block.getLocation()));
                            teleportLocation = block.getLocation().clone();
                            break;
                        }
                    }
                }
            }
            if (teleportLocation == null) {
                if (player.getBedSpawnLocation() != null) {
                    if (WorldFunctions.safeTeleport(player.getBedSpawnLocation())) {
                        instance.getDebug().log("DomainBlock", "kickFromField", "Unable to get safe teleport location nearby - teleporting to bed location: " + WorldFunctions.simpleReadable(player.getBedSpawnLocation()));
                        teleportLocation = player.getBedSpawnLocation();
                    }
                }
            }
        }
        if (teleportLocation == null) {
            instance.getDebug().log("DomainBlock", "kickFromField", "Unable to get safe teleport location nearby and bed location is not safe - teleporting to spawn");
            teleportLocation = player.getLocation().getWorld().getSpawnLocation();
        }

        if (this.inField(player.getLocation(), domainBlock)) {
            player.teleport(teleportLocation);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes a Domain field
     * @param placedId Domain field identifier to remove
     */
    public void removeField(int placedId) {
        DomainBlock domainBlock = getDomainBlockById(placedId);
        if (domainBlock != null) {
            RentManager rentManager = new RentManager(instance);
            rentManager.clearSigns(domainBlock);
            instance.getData().placedFields.remove(placedId);
            StorageManager storageManager = new StorageManager(instance);
            storageManager.remove(domainBlock);
            if (instance.getDependencies().hasDynmap()) {
                instance.getDependencies().removeFieldDynmap(domainBlock);
            }
        }
    }

    /**
     * Gets the highest current placedId and adds 1, this is used to number any new fields
     * @return Next placed id
     */
    public int getNextID() {
        int highestKey = 0;
        for (int key : instance.getData().placedFields.keySet()) {
            if (key > highestKey) {
                highestKey = key;
            }
        }
        return highestKey+ 1;
    }

}
