package com.ibexmc.domain.domainblock.open;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.internal.functions.StringFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import com.ibexmc.internal.utility.IbexPersistentData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class Magnet implements IOpenDomainBlock {

    //region Objects
    private final int placedId;
    private Location location;
    private final Domain instance;
    private IbexPersistentData persistentData;
    private DomainBlockManager domainBlockManager;
    //endregion
    //region Constructor
    public Magnet(int placedId) {
        this.placedId = placedId;
        this.instance = Domain.getInstance();
        persistentData = new IbexPersistentData(instance);
        domainBlockManager = new DomainBlockManager(instance);
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
     * Gets the magnet location
     * @return Magnet Location
     */
    public Location location() {
        return this.location;
    }

    /**
     * Sets the magnet location
     * @param location Magnet Location
     */
    public void location(Location location) {
        this.location = location;
    }

    /**
     * Sets the magnet location to the block provided
     * Location will be ABOVE the block, so if you click a block at y=64
     * the magnet will putll to the top of that block.  Uses
     * the entities yaw with the destination block location
     * @param player Player to set for
     * @param block Block to set to
     */
    public boolean set(Player player, Block block) {
        instance.getDebug().log("Magnet", "set", "Starting Magnet Set");
        if (player == null) {
            instance.getDebug().log(
                    "Magnet",
                    "set",
                    "Player provided is null"
            );
            return false;
        }
        ItemStack inHand = player.getInventory().getItemInMainHand().clone();
        if (block == null) {
            instance.getDebug().log(
                    "Magnet",
                    "set",
                    "Block provided is null"
            );
            return false;
        }
        DomainBlock domainBlock = domainBlockManager.getDomainBlockById(this.placedId);
        if (domainBlock == null) {
            instance.getDebug().log(
                    "Magnet",
                    "set",
                    "Invalid Domain Block"
            );
            return false;
        }
        if (persistentData.hasString(inHand, "domain_wand")) {
            instance.getDebug().log("Magnet", "set", "It was a wand");
            // Item in hand is a wand
            String wandKey = persistentData.getString(inHand, "domain_wand");
            if (wandKey == null) {
                instance.getDebug().log("Magnet", "set", "Wand key is null");
                return false;
            }
            if (wandKey.equalsIgnoreCase("magnet")) {
                instance.getDebug().log("Magnet", "set", "Wand is a magnet wand");
                // Check if clicked block = domain block location
                if (WorldFunctions.simplify(block.getLocation().clone()).equals(
                        WorldFunctions.simplify(domainBlock.getBlockLocation().clone())
                    )
                ) {
                    // Save & Close the magnet
                    if (this.location != null) {
                        domainBlock.setMagnetLocation(this.location.clone());
                        domainBlock.setDirty(true);
                        domainBlockManager.domainBlockSave(domainBlock);
                        HashMap<String, String> placeHolder = new HashMap<>();
                        placeHolder.put("<%location%>", WorldFunctions.simpleFancyReadable(this.location.clone()));
                        instance.getMessage().send(
                                player,
                                "saved_magnet_domainblock",
                                "&aSUCCESS &f- Your magnet location has been set to <%location%>",
                                placeHolder,
                                true
                        );
                        Magnet.takeTeleportWands(player);
                        Domain.getInstance().getData().openBlocks.remove(player.getUniqueId());
                        return true;
                    } else {
                        // Location not set
                        instance.getMessage().send(
                                player,
                                "magnet_set_notset",
                                "&4Magnet location has not been set.  To cancel, right click the Domain Block with an open hand",
                                null, true
                        );
                    }
                } else {
                    this.location = new Location(
                            block.getLocation().getWorld(),
                            block.getLocation().getBlockX(),
                            block.getLocation().getBlockY() + 1,
                            block.getLocation().getBlockZ(),
                            player.getLocation().clone().getYaw(),
                            0
                    );
                    instance.getDebug().log("Magnet", "set", "Magnet Location set to " + WorldFunctions.fancyReadable(this.location));
                    instance.getMessage().send(
                            player,
                            "magnet_set_ok",
                            "&4Magnet location set.  Left click the Domain Block with the wand to save",
                            null, true
                    );
                    return true;
                }
            } 
        }
        return false;
    }

    /**
     * Cancels the setting of a magnet location
     * @param player Player to cancel for
     */
    public boolean cancel(Player player, Block block) {
        instance.getDebug().log("Magnet", "set", "Starting Magnet Set");
        if (player == null) {
            instance.getDebug().log(
                    "Magnet",
                    "set",
                    "Player provided is null"
            );
            return false;
        }
        DomainBlock domainBlock = domainBlockManager.getDomainBlockById(this.placedId);
        if (domainBlock == null) {
            instance.getDebug().log(
                    "Magnet",
                    "set",
                    "Invalid Domain Block"
            );
            return false;
        }
        if (block == null) {
            instance.getDebug().log(
                    "Magnet",
                    "set",
                    "Block provided is null"
            );
            return false;
        }

        if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            if (WorldFunctions.simplify(block.getLocation().clone()).equals(
                    WorldFunctions.simplify(domainBlock.getBlockLocation().clone())
            )
            ) {
                instance.getMessage().send(
                        player,
                        "magnet_set_cancel",
                        "&4Magnet location setting has been cancelled",
                        null, true
                );
                Domain.getInstance().getData().openBlocks.remove(player.getUniqueId());
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the magnet wand
     * @return Magnet Wand ItemStack
     */
    public static ItemStack wand() {

        Domain instance = Domain.getInstance();
        IbexPersistentData persistentData = new IbexPersistentData(instance);

        ItemStack wand = new ItemStack(Material.STICK, 1);
        //wand = NBTEditor.set(wand, "teleport", "domain", "wand");
        wand.addUnsafeEnchantment(Enchantment.LUCK, 1);
        ItemMeta wandMeta = wand.getItemMeta();
        persistentData.setString(wandMeta, "domain_wand", "magnet");
        wandMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        wandMeta.setDisplayName(
                instance.getLocale().localeText(
                        "magnet_wand_name",
                        "&bDomain Magnet Wand",
                        null
                )
        );
        List<String> wandLore = StringFunctions.stringToList(
                instance.getLocale().localeText(
                        "magnet_wand_lore",
                        "&f&lInstructions|&fLeft click the location you wish to set as magnet|&fthen left click the Domain Block to save|&fRight click the Domain Block with an empty hand to cancel",
                        null
                ),
                "\\|"
        );
        wandMeta.setLore(wandLore);
        wand.setItemMeta(wandMeta);
        return wand;
    }

    /**
     * Takes any magnet wands away from the player specified
     * @param player Player to take wands from
     */
    public static void takeTeleportWands(Player player) {

        Domain instance = Domain.getInstance();
        IbexPersistentData persistentData = new IbexPersistentData(instance);

        Material wandMaterial = Magnet.wand().getType();
        int i = 0;
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item != null) {
                if (item.getType() != Material.AIR) {
                    if (item.getType() == wandMaterial) {
                        if (persistentData.hasString(item, "domain_wand")) {
                            if (persistentData.getString(item, "domain_wand").equalsIgnoreCase("magnet")) {
                                player.getInventory().setItem(i, new ItemStack(Material.AIR, 1));
                            }
                        }
                    }
                }
            }
            i++;
        }
    }
    //endregion
}
