package com.ibexmc.domain.domainblock.open;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.api.events.domain.DomainResizeEvent;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.domainblock.Visualize;
import com.ibexmc.domain.helper.DomainFunctions;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import com.ibexmc.internal.functions.StringFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import com.ibexmc.internal.utility.IbexPersistentData;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


import java.util.HashMap;
import java.util.List;

public class Resize implements IOpenDomainBlock {

    //region Objects
    private int placedId;
    private boolean ignoreY = false;
    private Location corner1;
    private Location corner2;
    private final Domain instance;
    private DomainBlockManager domainBlockManager;
    private Visualize visualize;
    //endregion
    //region Constructor
    public Resize(int placedId) {
        this.placedId = placedId;
        this.instance = Domain.getInstance();
        domainBlockManager = new DomainBlockManager(instance);
        visualize = new Visualize(instance);
        DomainBlock domainBlock = domainBlockManager.getDomainBlockById(this.placedId);
        if (domainBlock != null) {
            this.ignoreY = domainBlock.getConfig().isIgnoreYAxis();
            this.corner1 = domainBlock.getCorner1().clone();
            this.corner2 = domainBlock.getCorner2().clone();
        }
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
     * Gets the altered corner 1
     * @return Corner 1
     */
    public Location corner1() {
        return this.corner1;
    }

    /**
     * Sets the altered corner 1
     * If ignore y axis is on for this block type, sets to y=0
     * @param location Corner 1
     */
    public void corner1(Location location) {
        if (ignoreY) {
            this.corner1 = new Location(
                    location.getWorld(),
                    location.getBlockX(),
                    0,
                    location.getBlockZ()
            );
        } else {
            this.corner1 = location;
        }
    }

    /**
     * Gets the altered corner 2
     * @return Corner 2
     */
    public Location corner2() {
        return this.corner2;
    }

    /**
     * Sets the altered corner 2
     * If ignore y axis on for this block type, sets to y = max height
     * @param location
     */
    public void corner2(Location location) {
        if (ignoreY) {
            this.corner2 = new Location(
                    location.getWorld(),
                    location.getBlockX(),
                    location.getWorld().getMaxHeight(),
                    location.getBlockZ()
            );
        } else {
            this.corner2 = location;
        }
    }

    /**
     * Displays the current resized field
     * @param player Player to display for
     */
    public void display(Player player) {
        DomainBlock domainBlock = domainBlockManager.getDomainBlockById(this.placedId);
        if (domainBlock != null) {
            boolean valid = valid(player);
            visualize.displayField(
                    player,
                    domainBlock,
                    0,
                    true,
                    true,
                    valid,
                    this.corner1,
                    this.corner2
            );
        }
    }

    /**
     * Checks if the resize is valid
     * @param player Player to message with error messages
     * @return If true, field is valid
     */
    public boolean valid(Player player) {
        boolean hasBypass = PlayerFunctions.hasBypass(player);

        DomainBlock domainBlock = domainBlockManager.getDomainBlockById(this.placedId);
        if (domainBlock == null) {
            return false;
        }
        if (this.corner1 == null || this.corner2 == null) {
            instance.getMessage().send(
                    player,
                    "valid_resize_corners",
                    "&4Field invalid.  You must set both corners",
                    null, true
            );
            return false;
        }
        // Check if the field is within limits
        int blocksInField = DomainFunctions.blockCount(this.corner1, this.corner2);
        if (blocksInField > domainBlock.getMaxVolume()) {
            HashMap<String, String> placeHolder = new HashMap<>();
            placeHolder.put("<%used%>", Integer.toString(blocksInField));
            placeHolder.put("<%maxvolume%>", "" + domainBlock.getMaxVolume());
            if (hasBypass) {
                // Field is too large
                instance.getMessage().send(
                        player,
                        "valid_resize_size_bypass",
                        "&2Oversized field allowed due to Domain Bypass.  Field size: &2<%used%> of <%maxvolume%>",
                        null, true
                );
                return true;
            } else {
                // Field is too large
                instance.getMessage().send(
                        player,
                        "valid_resize_size",
                        "&4Field invalid.  Field size: &2<%used%> of <%maxvolume%>",
                        null, true
                );
                return false;
            }
        }

        // Check if Domain block is in the field (if applicable)
        if (domainBlock.getConfig().isBlockInField()) {
            if (!WorldFunctions.insideCuboid(domainBlock.getBlockLocation(), this.corner1, this.corner2)) {
                // block must be inside field
                instance.getMessage().send(player,"valid_resize_in_field", "&4Field invalid.  Domain block must be within the field", null, true);
                return false;
            }
        }

        // Check if fields overlap if the block is rentable
        DomainBlockManager domainBlockManager = new DomainBlockManager(Domain.getInstance());
        if (domainBlock.getRentCost() > 0 && domainBlock.getRentDays() > 0) {
            List<Location> lstCorners = DomainFunctions.getCorners(this.corner1, this.corner2);
            for (Location l : lstCorners) {
                List<DomainBlock> pbAtLoc = domainBlockManager.playerDomainBlocks(l);
                if (pbAtLoc != null) {
                    if (pbAtLoc.size() > 0)	{
                        for (DomainBlock pbLoc : pbAtLoc) {
                            if (pbLoc.getPlacedId() != domainBlock.getPlacedId()) {
                                if (pbLoc.getConfig().getBlockKey().equals(domainBlock.getConfig().getBlockKey())) {
                                    instance.getMessage().send(
                                            player,
                                            "valid_resize_rent_overlap",
                                            "&4Field invalid.  Rentable fields cannot overlap",
                                            null, true
                                    );
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }

        // Check overlap
        DomainBlock testDomainBlock;
        testDomainBlock = domainBlock;
        testDomainBlock.setCorner1(corner1);
        testDomainBlock.setCorner2(corner2);
        boolean cancelOverlap = domainBlockManager.hasOverlap(player, testDomainBlock);
        if (cancelOverlap) {
            if (!hasBypass) {
                instance.getMessage().send(
                        player,
                        "valid_resize_overlap",
                        "&4Field invalid.  Fields belonging to different players cannot overlap",
                        null, true
                );
                return false;
            }
        }
        return true;
    }

    /**
     * Completes the resize
     * @param player Player completing the resize
     */
    public void finish(Player player) {
        DomainBlock domainBlock = domainBlockManager.getDomainBlockById(this.placedId);
        if (domainBlock == null) {
            return;
        }
        DomainResizeEvent domainResizeEvent = new DomainResizeEvent(
                player,
                domainBlock
        );
        Bukkit.getServer().getPluginManager().callEvent(domainResizeEvent);
        if (domainResizeEvent.isCancelled()) {
            instance.getDebug().log("Resize", "finish", "Event was cancelled");
        } else {
            instance.getDebug().log("Resize", "finish", "Event was not cancelled");
            domainBlock.setCorner1(this.corner1);
            domainBlock.setCorner2(this.corner2);
            domainBlock.setDirty(true);
            domainBlockManager.domainBlockSave(domainBlock);
            player.getWorld().playEffect(player.getLocation(), Effect.ENDERDRAGON_SHOOT, 10);
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
            Domain.getInstance().getData().openBlocks.remove(player.getUniqueId());
            instance.getMessage().send(
                    player,
                    "resize_success",
                    "&2SUCCESS - Your field has been resized",
                    null, true
            );
        }
    }

    /**
     * Gets the resize wand
     * @return Resize Wand ItemStack
     */
    public static ItemStack wand() {

        Domain instance = Domain.getInstance();
        IbexPersistentData persistentData = new IbexPersistentData(instance);

        ItemStack wand = new ItemStack(Material.STICK, 1);
        //wand = NBTEditor.set(wand, "resize", "domain", "wand");
        wand.addUnsafeEnchantment(Enchantment.LUCK, 1);
        ItemMeta wandMeta = wand.getItemMeta();
        persistentData.setString(wandMeta, "domain_wand", "resize");
        wandMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        wandMeta.setDisplayName(
                instance.getLocale().localeText(
                  "resize_wand_name",
                  "&bDomain Resize Wand",
                  null
                )
        );
        List<String> wandLore = StringFunctions.stringToList(
                instance.getLocale().localeText(
                        "resize_wand_lore",
                        "Instructions|Left click corner 1 of desired field|Right click corner 2 of desired field|Right click domain block with wand to save",
                        null
                ),
          "\\|"
        );
        wandMeta.setLore(wandLore);
        wand.setItemMeta(wandMeta);
        return wand;
    }

    /**
     * Removes any resize wands from a player
     * @param player Player to remove wand from
     */
    public static void removeWand(Player player) {

        Domain instance = Domain.getInstance();
        IbexPersistentData persistentData = new IbexPersistentData(instance);

        if (player != null) {
            int i = 0;
            for (ItemStack itemStack : player.getInventory()) {
                if (itemStack != null) {
                    if (itemStack.getType().equals(Material.STICK)) {
                        if (persistentData.hasString(itemStack.getItemMeta(), "domain_wand")) {
                            if (persistentData.getString(itemStack, "domain_wand").equals("resize")) {
                                itemStack = new ItemStack(Material.AIR, 1);
                                player.getInventory().setItem(i, itemStack);
                            }
                        }
                    }
                }
                i++;
            }
        }
    }

    //endregion
}
