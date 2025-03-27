package com.ibexmc.domain.gui;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.internal.functions.SoundFunctions;
import com.ibexmc.internal.utility.IbexPersistentData;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.flag.FlagManager;
import com.ibexmc.domain.util.Permission;

import java.util.TreeMap;

public class FlagsGUI implements IInventoryGUI {

    //region Objects
    private DomainBlock domainBlock;
    private Player player;
    TreeMap<String, Flag.Type> flags;
    private int pageNumber = 0;
    private int totalPages = 0;
    //endregion
    //region Constructors
    public FlagsGUI(DomainBlock domainBlock, Player player) {
        this.domainBlock = domainBlock;
        this.player = player;
        this.pageNumber = 1;
        FlagManager flagManager = new FlagManager(Domain.getInstance());
        this.flags = flagManager.fieldFlagsByName(this.domainBlock);
        if ((flags.size() % 45) > 0) {
            this.totalPages = (this.flags.size() / 45) + 1;
        } else {
            this.totalPages = (this.flags.size() / 45);
        }
    }
    public FlagsGUI(DomainBlock domainBlock, Player player, int pageNumber) {
        this.domainBlock = domainBlock;
        this.player = player;
        this.pageNumber = pageNumber;
        FlagManager flagManager = new FlagManager(Domain.getInstance());
        this.flags = flagManager.fieldFlagsByName(this.domainBlock);
        if ((flags.size() % 45) > 0) {
            this.totalPages = (this.flags.size() / 45) + 1;
        } else {
            this.totalPages = (this.flags.size() / 45);
        }
    }
    //endregion
    //region Events
    @Override
    /**
     * Gets the inventory being used in this GUI
     */
    public Inventory getInventory() {

        Domain instance = Domain.getInstance();
        FlagManager flagManager = new FlagManager(instance);

        Inventory inventory = Bukkit.createInventory(
                this,
                54,
                instance.getLocale().localeText("gui_flags_title", "Flags", null) + " " + pageNumber + "/" + totalPages
        );

        for(int i = 0; i < 54; ++i) {
            switch (i) {
                case 45:
                    inventory.setItem(i, GUIHelper.get("back"));
                    break;
                case 48:
                    if (Permission.hasPermission(player, "domain.flag.add") ) {
                        inventory.setItem(i, GUIHelper.get("flag_add"));
                    } else {
                        inventory.setItem(i, GUIHelper.get("na"));
                    }
                    break;
                case 51:
                    if (totalPages > 1 && pageNumber > 1) {
                        inventory.setItem(i, GUIHelper.get("info_flags_previous"));
                    } else {
                        inventory.setItem(i, GUIHelper.get("info_flags_previous_disabled"));
                    }
                    break;
                case 53:
                    if (totalPages > 1 && pageNumber != totalPages) {
                        inventory.setItem(i, GUIHelper.get("info_flags_next"));
                    } else {
                        inventory.setItem(i, GUIHelper.get("info_flags_next_disabled"));
                    }
                    break;
                default:
                    inventory.setItem(i, GUIHelper.get("na"));
                    break;

            }
        }
        int i = 0;
        int j = 0;
        for(String key : flags.keySet()) {
            if (i >= ((pageNumber - 1) * 45) && i < ((pageNumber - 1) * 45) + 45) {
                Flag.Type flagType = flags.get(key);
                if (flagManager.fieldFlags(this.domainBlock).containsKey(flagType)) {
                    instance.getDebug().log(
                            "FlagsGUI",
                            "onInventory",
                            "Field Flags: " + flagType.name()
                    );
                    inventory.setItem(
                            j,
                            flagManager.item(
                                    flagType,
                                    flagManager.fieldFlags(
                                            this.domainBlock
                                    ).get(flagType)
                            )
                    );
                    j++;
                }
            }
            i++;
        }
        return inventory;
    }

    /**
     * Called from InventoryClick for any clicks in an inventory window of this type
     * @param event InventoryClickEvent to process
     */
    @Override
    public void onClick(InventoryClickEvent event) {

        Domain instance = Domain.getInstance();
        IbexPersistentData persistentData = new IbexPersistentData(instance);
        event.setCancelled(true);
        switch (event.getRawSlot()) {
            //region Back
            case 45:
                // Back
                InfoGUI infoGUI = new InfoGUI(instance, domainBlock, player);
                SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                player.openInventory(infoGUI.getInventory());
                break;
                //endregion
            //region Add Flags
            case 48:
                if (Permission.hasPermission(player, "domain.flag.add")) {
                    BlockUnusedFlagsGUI addGUI = new BlockUnusedFlagsGUI(instance);
                    addGUI.setPlacedID(this.domainBlock.getPlacedId());
                    addGUI.setPlayer(player);
                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                    player.openInventory(addGUI.getInventory());
                } else {
                    GUIHelper.soundError(player);
                }
                break;
                //endregion
            //region Previous
            case 51:
                if (totalPages > 1 && pageNumber > 1) {
                    GUIHelper.soundClick(player);
                    FlagsGUI guiPage = new FlagsGUI(this.domainBlock, this.player, this.pageNumber - 1);
                    player.openInventory(guiPage.getInventory());
                } else {
                    GUIHelper.soundError(player);
                }
                break;
                //endregion
            //region Next
            case 53:
                // Next
                if (player == null) {
                    instance.getDebug().log(
                            "FlagsGUI",
                            "onClick",
                            "Player is null"
                    );
                }
                if (totalPages > 1 && pageNumber != totalPages) {
                    GUIHelper.soundClick(player);
                    FlagsGUI guiPage = new FlagsGUI(this.domainBlock, this.player, this.pageNumber + 1);
                    player.openInventory(guiPage.getInventory());
                } else {
                    GUIHelper.soundError(player);
                }
                break;
                //endregion
            default:
                //region Flags
                if (event.getRawSlot() >= 0 && event.getRawSlot() < 45) {
                    ItemStack clickedItem = event.getInventory().getItem(event.getRawSlot());
                    if (
                            persistentData.hasString(clickedItem, "domain_flag") &&
                                    persistentData.hasInt(clickedItem, "domain_flag_status")
                    ) {
                        String flagName = persistentData.getString(clickedItem, "domain_flag");
                        int flagStatus = persistentData.getInt(clickedItem, "domain_flag_status");
                        // flag
                        instance.getDebug().log(
                                "FlagsGUI",
                                "onClick",
                                "Clicked " + flagName +
                                        " Status " + flagStatus
                        );
                        Flag.Type flagType = Flag.flagFromString(flagName);
                        if (flagType != null) {
                            boolean newStatus = false;
                            if (flagStatus == 1) {
                                // on - change to off
                                instance.getDebug().log(
                                        "FlagsGUI",
                                        "onClick",
                                        "Changing " + flagName + " to &4DISABLED"
                                );
                                newStatus = false;
                            } else if (flagStatus == 0) {
                                // off - change to on
                                instance.getDebug().log(
                                        "FlagsGUI",
                                        "onClick",
                                        "Changing " + flagName + " to &2ENABLED"
                                );
                                newStatus = true;
                            }
                            FlagManager flagManager = new FlagManager(instance);
                            event.getInventory().setItem(event.getRawSlot(), flagManager.item(flagType, newStatus));
                            Flag flag = new Flag(
                                    domainBlock.getPlacedId(),
                                    flagType,
                                    newStatus
                            );
                            domainBlock.removeFlag(flagType);
                            domainBlock.addFlag(flag);
                            domainBlock.setDirty(true);
                            GUIHelper.soundClick(player);
                        }

                    } else {
                        instance.getDebug().log(
                                "FlagsGUI",
                                "onClick",
                                "Clicked N/A"
                        );
                        GUIHelper.soundError(player);
                    }
                }
                //endregion
                break;
        }

    }

    /**
     * Used to handle inventory closing
     * @param event InventoryCloseEvent to process
     */
    @Override
    public void onClose(InventoryCloseEvent event) {
        DomainBlockManager domainBlockManager = new DomainBlockManager(Domain.getInstance());
        Domain.getInstance().getDebug().log(
                "FlagsGUI",
                "onClose",
                "Saving Changes"
        );
        domainBlockManager.domainBlockSave(domainBlock);
    }

    /**
     * Returns the DomainBlock being used in this menu
     * @return DomainBlock
     */
    public DomainBlock domainBlock() {
        return this.domainBlock;
    }


    //endregion
}
