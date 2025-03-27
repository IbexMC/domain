package com.ibexmc.domain.domainblock.open;

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
import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Teleport implements IOpenDomainBlock {

    //region Objects
    private final int placedId;
    private Location location;
    private final Domain instance;
    private DomainBlockManager domainBlockManager;
    private IbexPersistentData persistentData;
    //endregion
    //region Constructor
    public Teleport(int placedId) {
        this.placedId = placedId;
        this.instance = Domain.getInstance();
        domainBlockManager = new DomainBlockManager(instance);
        persistentData = new IbexPersistentData(instance);
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
     * Gets the teleport location
     * @return Teleport Location
     */
    public Location location() {
        return this.location;
    }

    /**
     * Sets the teleport location
     * @param location Teleport Location
     */
    public void location(Location location) {
        this.location = location;
    }

    /**
     * Sets the teleport location to the block provided
     * Location will be ABOVE the block, so if you click a block at y=64
     * you will teleport in with your feet on top of that block.  Uses
     * the players yaw with the destination block location
     * @param player Player to set for
     * @param block Block to set to
     */
    public boolean set(Player player, Block block) {
        instance.getDebug().log("Teleport", "set", "Starting Teleport Set");
        if (player == null) {
            instance.getDebug().log(
                    "Teleport",
                    "set",
                    "Player provided is null"
            );
            return false;
        }
        ItemStack inHand = player.getInventory().getItemInMainHand().clone();
        if (block == null) {
            instance.getDebug().log(
                    "Teleport",
                    "set",
                    "Block provided is null"
            );
            return false;
        }
        DomainBlock domainBlock = domainBlockManager.getDomainBlockById(this.placedId);
        if (domainBlock == null) {
            instance.getDebug().log(
                    "Teleport",
                    "set",
                    "Invalid Domain Block"
            );
            return false;
        }
        if (persistentData.hasString(inHand, "domain_wand")) {
            instance.getDebug().log("Teleport", "set", "It was a wand");
            // Item in hand is a wand
            String wandKey = persistentData.getString(inHand, "domain_wand");
            if (wandKey == null) {
                instance.getDebug().log("Teleport", "set", "Wand key is null");
                return false;
            }
            if (wandKey.equalsIgnoreCase("teleport")) {
                instance.getDebug().log("Teleport", "set", "Wand is a teleport wand");
                // Check if clicked block = domain block location
                if (WorldFunctions.simplify(block.getLocation().clone()).equals(
                        WorldFunctions.simplify(domainBlock.getBlockLocation().clone())
                    )
                ) {
                    // Save & Close the teleport
                    if (this.location != null) {
                        domainBlock.setTeleportToLocation(this.location.clone());
                        domainBlock.setDirty(true);
                        domainBlockManager.domainBlockSave(domainBlock);
                        Map<String, String> placeHolder = new HashMap<>();
                        placeHolder.put("<%location%>", WorldFunctions.simpleFancyReadable(this.location.clone()));
                        instance.getMessage().send(
                                player,
                                "saved_tpto_domainblock",
                                "&aSUCCESS &f- Your teleport location has been set to <%location%>",
                                placeHolder, true
                        );
                        Teleport.takeTeleportWands(player);
                        Domain.getInstance().getData().openBlocks.remove(player.getUniqueId());
                        return true;
                    } else {
                        // Location not set
                        instance.getMessage().send(
                                player,
                                "teleport_set_notset",
                                "&4Teleport location has not been set.  To cancel, right click the Domain Block with an open hand",
                                null, true
                        );
                    }
                } else {
                    // Set the teleport location
                    if (WorldFunctions.safeTeleport(block.getLocation().clone().add(0.5, 1, 0.5))) {
                        this.location = new Location(
                                block.getLocation().getWorld(),
                                block.getLocation().getBlockX(),
                                block.getLocation().getBlockY() + 1,
                                block.getLocation().getBlockZ(),
                                player.getLocation().clone().getYaw(),
                                0
                        );
                        instance.getDebug().log("Teleport", "set", "Teleport Location set to " + WorldFunctions.fancyReadable(this.location));
                        instance.getMessage().send(
                                player,
                                "teleport_set_ok",
                                "&4Teleport location set.  Left click the Domain Block with the wand to save",
                                null, true
                        );
                        return true;
                    } else {
                        // Unsafe
                        instance.getMessage().send(
                                player,
                                "teleport_set_unsafe",
                                "&4Unable to set this location, it is unsafe for teleporting to",
                                null, true
                        );
                    }
                }
            } 
        }
        return false;
    }

    /**
     * Cancels the setting of a teleport location
     * @param player Player to cancel for
     */
    public boolean cancel(Player player, Block block) {
        instance.getDebug().log("Teleport", "set", "Starting Teleport Set");
        if (player == null) {
            instance.getDebug().log(
                    "Teleport",
                    "set",
                    "Player provided is null"
            );
            return false;
        }
        DomainBlock domainBlock = domainBlockManager.getDomainBlockById(this.placedId);
        if (domainBlock == null) {
            instance.getDebug().log(
                    "Teleport",
                    "set",
                    "Invalid Domain Block"
            );
            return false;
        }
        if (block == null) {
            instance.getDebug().log(
                    "Teleport",
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
                        "teleport_set_cancel",
                        "&4Teleport location setting has been cancelled",
                        null, true
                );
                Domain.getInstance().getData().openBlocks.remove(player.getUniqueId());
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the teleport wand
     * @return Teleport Wand ItemStack
     */
    public static ItemStack wand() {

        Domain instance = Domain.getInstance();
        IbexPersistentData persistentData = new IbexPersistentData(instance);
        
        ItemStack wand = new ItemStack(Material.STICK, 1);
        //wand = NBTEditor.set(wand, "teleport", "domain", "wand");
        wand.addUnsafeEnchantment(Enchantment.LUCK, 1);
        ItemMeta wandMeta = wand.getItemMeta();
        persistentData.setString(wandMeta, "domain_wand", "teleport");
        wandMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        wandMeta.setDisplayName(
                instance.getLocale().localeText(
                        "teleport_wand_name",
                        "&bDomain Teleport Wand",
                        null
                )
        );
        List<String> wandLore = StringFunctions.stringToList(
                instance.getLocale().localeText(
                        "teleport_wand_lore",
                        "&f&lInstructions|&fLeft click the location you wish to teleport to|&fthen left click the Domain Block to save|&fRight click the Domain Block with an empty hand to cancel",
                        null
                ),
                "\\|"
        );
        wandMeta.setLore(wandLore);
        wand.setItemMeta(wandMeta);
        return wand;
    }

    /**
     * Takes any teleport wands away from the player specified
     * @param player Player to take wands from
     */
    public static void takeTeleportWands(Player player) {

        Domain instance = Domain.getInstance();
        IbexPersistentData persistentData = new IbexPersistentData(instance);
        
        Material wandMaterial = Teleport.wand().getType();
        int i = 0;
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item != null) {
                if (item.getType() != Material.AIR) {
                    if (item.getType() == wandMaterial) {
                        if (persistentData.hasString(item, "domain_wand")) {
                            if (persistentData.getString(item, "domain_wand").equalsIgnoreCase("teleport")) {
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
