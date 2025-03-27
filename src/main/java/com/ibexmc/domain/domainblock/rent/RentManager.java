package com.ibexmc.domain.domainblock.rent;

import com.ibexmc.internal.functions.StringFunctions;
import com.ibexmc.internal.functions.TimeFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import com.ibexmc.internal.utility.IbexPersistentData;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.helper.VaultFunctions;

import java.text.SimpleDateFormat;
import java.util.*;

public class RentManager {

    Domain instance;
    DomainBlockManager domainBlockManager;
    public RentManager(Domain instance) {
        this.instance = instance;
        domainBlockManager = new DomainBlockManager(instance);
    }

    /**
     * Returns true if the field is rentable
     * @param placedId Domain Block Identifier
     * @return If true, field is rentable
     */
    public boolean rentable(int placedId) {
        DomainBlock domainBlock = domainBlockManager.getDomainBlockById(placedId);
        if (domainBlock != null) {
            if (domainBlock.isActive() && !domainBlock.isExpired()) {
                return domainBlock.getRentDays() > 0 && domainBlock.getRentCost() > 0;
            }
        }
        return false;
    }

    /**
     * Loops through any rent signs and updates them
     * @param placedId Domain Block Identifier
     */
    public void updateSigns(int placedId) {
        DomainBlock domainBlock = domainBlockManager.getDomainBlockById(placedId);
        if (domainBlock == null) {
            return;
        }
        if (domainBlock.getRentSigns() != null) {
            for (Location location : domainBlock.getRentSigns()) {
                if (
                        location.getBlock().getBlockData() instanceof org.bukkit.block.data.type.Sign ||
                        location.getBlock().getBlockData() instanceof WallSign
                ) {
                    if (location.getBlock().getState() instanceof Sign) {
                        Sign sign = (Sign) location.getBlock().getState();
                        if (!rentable(domainBlock.getPlacedId())) {
                            updateSign(sign, "", "", "");
                        }
                        if (domainBlock.getRenter() == null) {
                            // Currently Available
                            clearSign(sign, domainBlock);
                        } else {
                            // Currently Rented
                            OfflinePlayer offlineRenter = Bukkit.getOfflinePlayer(domainBlock.getRenter());
                            rentSign(sign, offlineRenter, domainBlock.getRentUntilLongTimestamp());
                        }
                    }
                }
            }
        }
    }

    /**
     * Loops through any rent signs and clears them
     * @param domainBlock DomainBlock to get signs from
     */
    public void clearSigns(DomainBlock domainBlock) {
        if (domainBlock == null) {
            return;
        }
        if (domainBlock.getRentSigns() != null) {
            for (Location location : domainBlock.getRentSigns()) {
                if (
                        location.getBlock().getBlockData() instanceof org.bukkit.block.data.type.Sign ||
                                location.getBlock().getBlockData() instanceof WallSign
                ) {
                    if (location.getBlock().getState() instanceof Sign) {
                        Sign sign = (Sign) location.getBlock().getState();
                        updateSign(sign, "", "", "");
                    }
                }
            }
        }
    }

    /**
     * Updates a sign with renter information
     * @param sign Sign to update
     * @param renter Renter
     * @param rentUntil Timestamp the rent expires
     */
    public void rentSign(Sign sign, OfflinePlayer renter, long rentUntil) {
        String rentedText = instance.getLocale().localeText(
                "sign_rented",
                "&0[&4Rented&0]",
                null
        );
        Date date = new Date();
        date.setTime(rentUntil);
        String formattedDate = new SimpleDateFormat("MM-dd-yyyy").format(date);
        updateSign(
                sign,
                rentedText,
                renter.getName(),
                formattedDate
        );
    }

    /**
     * Resets the sign provided to the unrented default
     * @param sign Sign to update
     * @param domainBlock Domain Block to get cost/days from
     */
    public void clearSign(Sign sign, DomainBlock domainBlock) {

        Map<String, String> placeHolder = new HashMap<>();
        String line1 = instance.getLocale().localeText(
                "sign_rent",
                "&0&l[&d&lRent&0&l]",
                null
        );
        String line2;
        String line3;

        if (rentable(domainBlock.getPlacedId())) {
            placeHolder.put("<%cost%>", "" + domainBlock.getRentCost());
            placeHolder.put("<%days%>", "" + domainBlock.getRentDays());
            line2 = instance.getLocale().localeText(
                    "sign_cost",
                    "&lCost: &b&l$<%cost%>",
                    placeHolder
            );
            line3 = instance.getLocale().localeText(
                    "sign_days",
                    "&lDays: &b&l<%days%>",
                    placeHolder
            );
        } else {
            line2 = instance.getLocale().localeText(
                    "rent_na",
                    "&4N/A",
                    null
            );
            line3 = "";
        }

        updateSign(
                sign,
                line1,
                line2,
                line3
        );
    }

    /**
     * Updates a sign
     * @param sign Sign to update
     * @param line0 Top line
     * @param line1 Middle line
     * @param line2 Bottom line
     */
    public void updateSign(Sign sign, String line0, String line1, String line2) {
        if (sign == null) {
            return;
        }
        sign.setLine(0, line0);
        sign.setLine(1, line1);
        sign.setLine(2, line2);
        Bukkit.getScheduler().runTaskLater(instance, () -> sign.update(true), 20);
    }

    /**
     * Resets the rent signs for the given Domain Block
     * @param domainBlock Domain Block to reset signs for
     */
    public void resetSigns(DomainBlock domainBlock) {
        if (domainBlock == null) {
            return;
        }
        if (domainBlock.getRentSigns() != null) {
            for (Location location : domainBlock.getRentSigns()) {
                if (location.getBlock() instanceof Sign) {
                    Sign sign = (Sign) location.getBlock().getState();
                    clearSign(sign, domainBlock);
                }
            }
        }
    }

    /**
     * Clears the current renter from the field and clears the signs
     * @param placedId Domain Block Identifier
     */
    public void evict(int placedId) {
        DomainBlock domainBlock = domainBlockManager.getDomainBlockById(placedId);
        if (domainBlock != null) {
            if (domainBlock.getRenter() != null) {
                Player player = Bukkit.getPlayer(domainBlock.getRenter());
                if (player != null) {
                    Map<String, String> placeHolder = new HashMap<>();
                    placeHolder.put("<%placed_id%>", "" + domainBlock.getPlacedId());
                    placeHolder.put("<%location%>", WorldFunctions.simpleReadable(domainBlock.getBlockLocation()));
                    instance.getMessage().send(
                            player,
                            "rent_evict",
                            "&4You are no longer renting field <%placed_id%> at <%location%>",
                            placeHolder, true
                    );
                }
                domainBlock.setRenter(null);
                domainBlock.setRentUntilLongTimestamp(0);
                updateSigns(domainBlock.getPlacedId());
            }
        }
    }

    /**
     * Sends a confirmation message to the player about the terms of the rent
     * @param player Player to send the message to
     * @param placedId Domain Block Identifier
     */
    public void rentConfirm(Player player, int placedId) {
        if (player == null) {
            instance.getDebug().log("RentManager", "rentConfirm", "No player provided");
            return;
        }
        instance.getDebug().log("RentManager", "rentConfirm", "Confirming " + placedId + " for " + player.getName());
        DomainBlock domainBlock = domainBlockManager.getDomainBlockById(placedId);
        if (domainBlock.getOwner() != null) {
            if (player.getUniqueId().equals(domainBlock.getOwner())) {
                instance.getMessage().send(
                        player,
                        "rent_own",
                        "&4You cannot rent your own field",
                        null, true
                );
                return;
            }
        }
        if (domainBlock.getRenter() != null) {
            instance.getMessage().send(
                    player,
                    "rent_setrented_already_rented",
                    "Unable to rent field - This field is already rented by another player",
                    null, true
            );
            return;
        }
        if (rentable(placedId)) {
            Map<String, String> placeHolder = new HashMap<>();
            placeHolder.put("<%cost%>", "" + domainBlock.getRentCost());
            placeHolder.put("<%days%>", "" + domainBlock.getRentDays());
            TextComponent msg = new TextComponent(
                    instance.getLocale().localeText(
                            "rent_confirm",
                            "Are you sure you want to rent this field? It costs &b$<%cost%> &rfor &b<%days%>&r days",
                            placeHolder
                    )
            );
            TextComponent rentSure = new TextComponent(instance.getLocale().localeText("rent_confirm_yes", " &f[&2YES - Rent the field&f]", null));
            rentSure.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(instance.getLocale().localeText("rent_confirm_info", "&4Clicking YES will rent the field and debit your funds", null)).create()));
            rentSure.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/domain rentconfirm " + player.getUniqueId().toString() + " " + placedId));
            msg.addExtra(rentSure);

            player.spigot().sendMessage(msg);
        } else {
            // not rentable
            instance.getDebug().log("RentManager", "rentConfirm", "Field is not rentable");
        }
    }

    /**
     * Attempts to rent the field.  Intended post-confirmation
     * @param placedId Domain Block Identifier
     * @param renterUUID Renters Unique Identifier
     * @return If true, field was rented
     */
    public boolean rent(int placedId, UUID renterUUID) {
        if (renterUUID != null) {
            Player renter = Bukkit.getPlayer(renterUUID);
            OfflinePlayer offlineRenter = Bukkit.getOfflinePlayer(renterUUID);
            String renterName = "Unknown";
            if (offlineRenter.getName() != null) {
                renterName = offlineRenter.getName();
            }
            Map<String, String> placeHolder = new HashMap<>();
            placeHolder.put("<%renter%>", renterName);
            placeHolder.put("<%placed_id%>", "" + placedId);

            DomainBlock domainBlock = domainBlockManager.getDomainBlockById(placedId);
            if (domainBlock != null) {
                placeHolder.put("<%blocktype%>", domainBlock.getConfig().getDisplayName());
                placeHolder.put("<%cost%>", "" + domainBlock.getRentCost());
                placeHolder.put("<%days%>", "" + domainBlock.getRentDays());
                if (domainBlock.getRentCost() > 0 && domainBlock.getRentDays() > 0) {
                    long rentUntil = TimeFunctions.addDays(
                            TimeFunctions.now(),
                            domainBlock.getRentDays()
                    ).getTime();
                    // for testing.. just add it in minutes instead of days
/*                    rentUntil = TimeFunctions.addMinutes(
                            TimeFunctions.now(),
                            domainBlock.rentDays()
                    ).getTime();*/
                    // Check if already rented
                    if (domainBlock.getRenter() != null) {
                        // If it is already rented, check if it is by the player trying to rent
                        if (!domainBlock.getRenter().equals(renterUUID)) {
                            instance.getMessage().send(
                                    renter,
                                    "rent_setrented_already_rented",
                                    "Unable to rent field - This field is already rented by another player",
                                    placeHolder, true
                            );
                            OfflinePlayer currentRenter = Bukkit.getOfflinePlayer(domainBlock.getRenter());
                            instance.getDebug().log(
                                    "RentManager",
                                    "rent",
                                    "This field is already rented by another player. Renter: " +
                                            domainBlock.getRenter() + " (" +
                                            StringFunctions.isNull(currentRenter.getName(), "Unknown") + ")"
                            );
                            return false;
                        }
                        // Renter is not null, but not another player, so must be this renter.  Check if rent ends after now
                        // If so, we'll extend on to current end date, if not, we'll stick with the original end date
                        if (TimeFunctions.after(
                                TimeFunctions.timestampFromLong(domainBlock.getRentUntilLongTimestamp()),
                                TimeFunctions.now())
                        ) {

                            instance.getDebug().log("RentManager", "rent", "Extending current lease");
                            rentUntil = TimeFunctions.addDays(
                                    TimeFunctions.timestampFromLong(domainBlock.getRentUntilLongTimestamp()),
                                    domainBlock.getRentDays()
                            ).getTime();
                        }
                    }
                    // Check player can afford the rent
                    VaultFunctions vaultFunctions = new VaultFunctions(instance);
                    VaultFunctions.VaultReturn vrBalance = vaultFunctions.getBalance(offlineRenter);
                    if (vrBalance.isSuccess()) {
                        if (vrBalance.getBalance() >= domainBlock.getRentCost()) {
                            // Withdraw funds
                            VaultFunctions.VaultReturn vrWithdraw =vaultFunctions.withdraw(
                                    offlineRenter, domainBlock.getRentCost()
                            );
                            if (vrWithdraw.isSuccess()) {
                                placeHolder.put("<%newbalance%>", "" + (vrBalance.getBalance() - domainBlock.getRentCost()));
                                if (!domainBlock.isAdminBlock() && domainBlock.getOwner() != null) {
                                    OfflinePlayer offlineOwner = Bukkit.getOfflinePlayer(domainBlock.getOwner());
                                    VaultFunctions.VaultReturn vrDeposit = vaultFunctions.deposit(
                                            offlineOwner, domainBlock.getRentCost()
                                    );
                                    if (!vrDeposit.isSuccess()) {
                                        instance.getLog().log(
                                                "RentManager",
                                                "&4ACTION_REQUIRED",
                                                "&4PAYMENT ERROR\n" +
                                                        "&fAn error occurred while paying a player for their Domain field being rented\n" +
                                                        "Player: " + offlineOwner.getUniqueId() + " (" + StringFunctions.isNull(offlineOwner.getName(), "Unknown") + ")\n" +
                                                        "Domain Field: " + placedId + "\n" +
                                                        "Renter: " + offlineRenter.getUniqueId() + " (" + StringFunctions.isNull(offlineRenter.getName(), "Unknown") + ")\n" +
                                                        "Action: The owner of the field should be paid " + domainBlock.getRentCost()
                                        );
                                    }
                                }
                                // Update all the rent signs for this domain block
                                updateSigns(domainBlock.getPlacedId());

                                instance.getMessage().send(
                                        renter,
                                        "rent_setrented_renter_success",
                                        "You have been charged $<%cost%> - new balance: &2$<%newbalance%>",
                                        placeHolder, true
                                );

                                if (!domainBlock.isAdminBlock() && domainBlock.getOwner() != null) {
                                    Player owner = Bukkit.getPlayer(domainBlock.getOwner());
                                    if (owner != null) {
                                        instance.getMessage().send(
                                                owner,
                                                "rent_setrented_owner_success",
                                                "<%renter%> has rented your <%blocktype%> field.  You have been paid $<%cost%> - new balance: &2$<%newbalance%>",
                                                placeHolder, true
                                        );
                                    }
                                }
                                domainBlock.setRenter(renterUUID);
                                domainBlock.setRentUntilLongTimestamp(rentUntil);
                                domainBlock.setFieldName(renter.getName() + "s " + domainBlock.getConfig().getDisplayName() + " field");
                                domainBlock.setWelcomeMessage(
                                        Domain.getInstance().config().getDefaultWelcome().replace(
                                                "<%type%>", domainBlock.getConfig().getDisplayName()
                                        ).replace(
                                                "<%player%>", renter.getName()
                                        )
                                );
                                domainBlock.setFarewellMessage(
                                        Domain.getInstance().config().getDefaultFarewell().replace(
                                                "<%type%>", domainBlock.getConfig().getDisplayName()
                                        ).replace(
                                                "<%player%>", renter.getName()
                                        )
                                );
                                domainBlock.addAllowedPlayer(renter.getUniqueId());
                                domainBlock.setDirty(true);
                                domainBlockManager.domainBlockSave(domainBlock);

                                updateSigns(domainBlock.getPlacedId());
                                return true;
                            } else {
                                instance.getMessage().send(
                                        renter,
                                        "rent_unexpected_error",
                                        "Unable to rent field - Unexpected Error, please contact a member of staff",
                                        placeHolder, true
                                );
                                instance.getLog().log(
                                        "RentManager",
                                        "RentManager.rent",
                                        "&4Unexpected Error withdrawing from player balance\n" +
                                                "&4Error: " + vrWithdraw.getMessage() + "\n" +
                                                "&4Player UUID: " + renterUUID
                                );
                            }
                        } else {
                            instance.getMessage().send(
                                    renter,
                                    "rent_setrented_insufficient_funds",
                                    "&4You cannot afford to rent this field.",
                                    placeHolder, true
                            );
                            return false;
                        }
                    } else {
                        instance.getMessage().send(
                                renter,
                                "rent_unexpected_error",
                                "Unable to rent field - Unexpected Error, please contact a member of staff",
                                placeHolder, true
                        );
                        instance.getLog().log(
                                "RentManager",
                                "RentManager.rent",
                                "&4Unexpected Error getting player balance\n" +
                                        "&4Error: " + vrBalance.getMessage() + "\n" +
                                        "&4Player UUID: " + renterUUID
                        );
                    }
                } else {
                    instance.getMessage().send(
                            renter,
                            "rent_setrented_not_available",
                            "&4This field is not currently available to rent",
                            placeHolder, true
                    );
                    instance.getDebug().log(
                            "RentManager",
                            "rent",
                            "Field is not rentable - Cost: " + domainBlock.getRentCost() +
                                    " Days: " + domainBlock.getRentDays()
                    );
                    return false;
                }
            } else {
                instance.getMessage().send(
                        renter,
                        "rent_setrented_not_available",
                        "&4This field is not currently available to rent",
                        placeHolder, true
                );
                instance.getDebug().log("RentManager", "rent", "Invalid Domain Block - " + placedId);
                return false;
            }
        } else {
            instance.getDebug().log("RentManager", "rent", "renterUUID provided is null");
            return false;
        }
        return false;
    }

    /**
     * Returns a rent sign using the sign ItemStack provided as a base
     * @param item Sign item stack to replace
     * @param placedId Domain Block Identifier
     * @return ItemStack of signs provided with a rent NBT tag, name and lore, if not a sign, returns null
     */
    public ItemStack rentSign(ItemStack item, int placedId) {
        if (item.getType().isBlock()) {
            BlockData blockData = item.getType().createBlockData();
            if (blockData instanceof org.bukkit.block.data.type.Sign || blockData instanceof WallSign) {
                ItemStack signItem = item.clone();
                //signItem = NBTEditor.set(signItem, "" + placedId, "domain", "rent");
                ItemMeta rentMeta = signItem.getItemMeta();
                IbexPersistentData persistentData = instance.getPersistentData();
                persistentData.setInt(rentMeta, "domain_rent_sign", placedId);
                Map<String, String> placeHolder = new HashMap<>();
                placeHolder.put("<%placed_id%>", "" + placedId);
                rentMeta.setDisplayName(
                        instance.getLocale().localeText(
                                "rent_sign_name",
                                "&bRent Sign",
                                null
                        )
                );
                rentMeta.setLore(
                        StringFunctions.stringToList(
                                instance.getLocale().localeText(
                                        "rent_sign_lore",
                                        "&fAutomated rent sign|Domain Field <%placed_id%>",
                                        placeHolder
                                ), "\\|")
                );
                signItem.setItemMeta(rentMeta);
                return signItem;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Creates a new sign at the block position provided, used to create a new
     * pre-populated rent sign.  Block data is the block data of the originally
     * placed blank sign that this replaced.
     * @param block Block to create the sign at
     * @param blockData Block Data of the originally placed sign
     * @param placedId Domain Block Identifier
     * @return If true, sign was placed successfully
     */
    public boolean newSign(Block block, BlockData blockData, int placedId) {
        if (block != null && blockData != null) {
            instance.getDebug().log("BlockEvents", "onBlockPlace", "Sign Detected");

            instance.getDebug().log("BlockEvents", "onBlockPlace", "Getting Domain Block for placedId = " + placedId);
            DomainBlock domainBlock = domainBlockManager.getDomainBlockById(placedId);
            if (domainBlock != null) {
                instance.getDebug().log("BlockEvents", "onBlockPlace", "Domain Block Found");
                if (blockData instanceof org.bukkit.block.data.type.Sign) {
                    instance.getDebug().log("BlockEvents", "onBlockPlace", "Block Data is a Sign");
                    org.bukkit.block.data.type.Sign signBlockData = (org.bukkit.block.data.type.Sign) blockData;
                    instance.getDebug().log("BlockEvents", "onBlockPlace", WorldFunctions.simpleReadable(block.getLocation()));
                    instance.getDebug().log("BlockEvents", "onBlockPlace", "Rotation: " + signBlockData.getRotation().name());
                    instance.getDebug().log("BlockEvents", "onBlockPlace", "Material: " + signBlockData.getMaterial().name());
                    instance.getDebug().log("BlockEvents", "onBlockPlace", "Cancelling the event");
                    Bukkit.getScheduler().runTaskLater(Domain.getInstance(), () -> {
                        instance.getDebug().log("BlockEvents", "onBlockPlace", "Placing pre-populated sign");
                        BlockState bs1 = block.getState();
                        bs1.setType(signBlockData.getMaterial());
                        org.bukkit.block.data.type.Sign newBlockData = (org.bukkit.block.data.type.Sign) signBlockData.getMaterial().createBlockData();
                        newBlockData.setRotation(signBlockData.getRotation());
                        bs1.setBlockData(newBlockData);
                        bs1.update(true);
                        org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();
                        if (domainBlock.getRenter() == null) {
                            clearSign(sign, domainBlock);
                        } else {
                            OfflinePlayer offlineRenter = Bukkit.getOfflinePlayer(domainBlock.getRenter());
                            rentSign(sign, offlineRenter, domainBlock.getRentUntilLongTimestamp());
                        }
                        instance.getDebug().log("BlockEvents", "onBlockPlace", "Sign place finished");
                    }, 2);
                }
                if (blockData instanceof WallSign) {
                    instance.getDebug().log("BlockEvents", "onBlockPlace", "Block Data is a Wall Sign");
                    WallSign wallSignBlockData = (WallSign) blockData;
                    instance.getDebug().log("BlockEvents", "onBlockPlace", WorldFunctions.simpleReadable(block.getLocation()));
                    instance.getDebug().log("BlockEvents", "onBlockPlace", "Face: " + wallSignBlockData.getFacing().name());
                    instance.getDebug().log("BlockEvents", "onBlockPlace", "Material: " + wallSignBlockData.getMaterial().name());
                    Bukkit.getScheduler().runTaskLater(Domain.getInstance(), () -> {
                        instance.getDebug().log("BlockEvents", "onBlockPlace", "Timed!");
                        BlockState bs1 = block.getState();
                        bs1.setType(wallSignBlockData.getMaterial());
                        WallSign newBlockData = (WallSign) wallSignBlockData.getMaterial().createBlockData();
                        newBlockData.setFacing(wallSignBlockData.getFacing());
                        bs1.setBlockData(newBlockData);
                        bs1.update(true);
                        org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();
                        if (domainBlock.getRenter() == null) {
                            clearSign(sign, domainBlock);
                        } else {
                            OfflinePlayer offlineRenter = Bukkit.getOfflinePlayer(domainBlock.getRenter());
                            rentSign(sign, offlineRenter, domainBlock.getRentUntilLongTimestamp());
                        }
                        instance.getDebug().log("BlockEvents", "onBlockPlace", "Wall Sign place finished");
                    }, 2);
                }
                domainBlock.addRentSign(block.getLocation());
                domainBlock.setDirty(true);
                domainBlockManager.domainBlockSave(domainBlock);
                return true;
            } else {
                // No Domain Block Found
                instance.getDebug().log("BlockEvents", "onBlockPlace", "No Domain Block Found");
                return false;
            }
        } else {
            instance.getDebug().log("BlockEvents", "onBlockPlace", "Block or lock Data is null");
            return false;
        }
    }

    /**
     * Gets a domain block from a sign IF it is a valid rent sign
     * @param block Block with sign to check
     * @return DomainBlock assigned to this sign, if none, returns null
     */
    public DomainBlock domainBlockFromSign(Block block) {
        for (DomainBlock domainBlock : domainBlockManager.getAllDomainBlocks()) {
            if (domainBlock.getRentSigns() != null) {
                for (Location rentLocation : domainBlock.getRentSigns()) {
                    if (WorldFunctions.blockLocationMatch(block, rentLocation.getBlock())) {
                        return domainBlock;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Removes a rent sign from the domain block
     * @param domainBlock Domain Block to remove from
     * @param block Block in world that should be removed
     */
    public void removeRentSign(DomainBlock domainBlock, Block block) {
        if (domainBlock == null) {
            instance.getDebug().log("RentManager", "removeRentSign", "Domain Block is null");
            return;
        }
        if (block == null) {
            instance.getDebug().log("RentManager", "removeRentSign", "Block is null");
            return;
        }
        if (domainBlock.getRentSigns() != null) {
            for (Location location : domainBlock.getRentSigns()) {
                if (WorldFunctions.match(location, block.getLocation(), true)) {
                    instance.getDebug().log(
                            "RentManager",
                            "removeRentSign",
                            "Removing " + WorldFunctions.simpleReadable(location)
                    );
                    domainBlock.removeRentSign(location);
                    domainBlock.setDirty(true);
                    domainBlockManager.domainBlockSave(domainBlock);
                }
            }
        }
    }

    /**
     * Loops through all Domain fields and evicts any players where the expiration
     * timestamp is after the current timestamp
     */
    public void expireCheck() {
        instance.getDebug().log(
                "RentManager",
                "expireCheck",
                "Checking rent expirations"
        );
        for (DomainBlock domainBlock : domainBlockManager.getAllDomainBlocks()) {
            instance.getDebug().log(
                    "RentManager",
                    "expireCheck",
                    "Checking placedId: " + domainBlock.getPlacedId()
            );
            if (domainBlock.getRenter() != null) {
                instance.getDebug().log(
                        "RentManager",
                        "expireCheck",
                        "Field has a renter, checking times\n" +
                                "now: " + TimeFunctions.now() + "\n" +
                                "until: " + TimeFunctions.timestampFromLong(domainBlock.getRentUntilLongTimestamp())
                );
                if (TimeFunctions.after(
                        TimeFunctions.now(),
                        TimeFunctions.timestampFromLong(domainBlock.getRentUntilLongTimestamp())
                )) {
                    instance.getDebug().log(
                            "RentManager",
                            "expireCheck",
                            "Rent expired for placedId = " + domainBlock.getPlacedId()
                    );
                    evict(domainBlock.getPlacedId());
                } else {
                    instance.getDebug().log(
                            "RentManager",
                            "expireCheck",
                            "Not expired"
                    );
                }
            }
        }
    }

    /**
     * Displays some basic information about the rent status/cost etc.
     * @param player Player to display information for
     * @param domainBlock Domain Block to get information about
     */
    public void rentInfo(Player player, DomainBlock domainBlock) {
        Map<String, String> placeHolder = new HashMap<>();

        placeHolder.put("<%cost%>", "" + domainBlock.getRentCost());
        placeHolder.put("<%days%>", "" + domainBlock.getRentDays());
        if (domainBlock.getRenter() != null) {
            OfflinePlayer offlineRenter = Bukkit.getOfflinePlayer(domainBlock.getRenter());
            placeHolder.put("<%renter%>", offlineRenter.getName());
            Date date = new Date();
            date.setTime(domainBlock.getRentUntilLongTimestamp());
            String formattedDate = new SimpleDateFormat("MM-dd-yyyy").format(date);
            placeHolder.put("<%expires%>", "" + formattedDate);
        }

        String rentStatus = instance.getLocale().localeText(
                "rent_info_rented",
                "&fCurrently rented to <%renter%> until <%expires%>",
                placeHolder
        );
        if (domainBlock.getRenter() == null) {
            rentStatus = instance.getLocale().localeText(
                    "rent_info_available",
                    "&fNot currently rented out",
                    null
            );
        }
        String rentCost = instance.getLocale().localeText(
                "rent_info_cost",
                "&fCost $<%cost%> for <%days%> days",
                placeHolder
        );
        if (!rentable(domainBlock.getPlacedId())) {
            rentCost = instance.getLocale().localeText(
                    "rent_info_na",
                    "&4Field is not currently set as rentable",
                    placeHolder
            );
        }
        instance.getMessage().send(
                player,
                "rent_info_title",
                "\n&b************************************************" +
                        "\n&fRent Info" +
                        "\n&b************************************************" +
                        "\n" + rentStatus +
                        "\n" + rentCost +
                        "\n&b************************************************"                ,
                placeHolder, false
        );
    }
}
