package com.ibexmc.domain.gui;

import java.util.*;

import com.ibexmc.internal.functions.ColorFunctions;
import com.ibexmc.internal.functions.SoundFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.Domain.IGUI;

import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.flag.FlagMenuItem;
import com.ibexmc.domain.domainblock.DomainBlock;

public class BlockUnusedFlagsGUI implements IGUI{

    private Domain instance;
    private DomainBlockManager domainBlockManager;

    private boolean validGUI = false;
    private int pageNum = 1;
    //private TreeMap<String, Boolean> tmFlags = new TreeMap<String, Boolean>();
    private HashMap<String, FlagMenuItem> flagMenuItems = Domain.getInstance().getData().getFlagMenuItems();

    private HashMap<Integer, Flag.Type> flagPos = new HashMap<Integer, Flag.Type>();
    private TreeMap<Flag.Type, Integer> treeFlags = new TreeMap<Flag.Type, Integer>();

    public BlockUnusedFlagsGUI(Domain instance) {
        this.instance = instance;
        this.domainBlockManager = new DomainBlockManager(instance);
    }

    @Override
    public int getPageNum() {
        return this.pageNum;
    }
    @Override
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
    private String key = "";
    private int placedID = -1;

    private int slotCount = 54;
    private HashMap<String, GUIOption> guiOptions = new HashMap<>();

    private ItemStack flagOn(String flagName) {
        guiOptions.put("flag_allow", Domain.getInstance().getData().getGUIOption("flag_allow", Material.LIME_STAINED_GLASS_PANE, "&2Allow", "Allows the flag in the field", true));
        ItemStack itemStackOn = guiOptions.get("flag_allow").getItemStack();
        ItemMeta itemMetaOn = itemStackOn.getItemMeta();
        if (itemMetaOn != null) {
            itemMetaOn.setDisplayName(ColorFunctions.colorToString("&b" + flagName));
            itemMetaOn.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            List<String> newLore = new ArrayList<>();
            if (itemMetaOn.getLore() != null) {
                newLore = itemMetaOn.getLore();
            }
            newLore.add(instance.getLocale().localeText("missing_flag_config", "&7This flag is not in the config file. Please update it", null));
            itemMetaOn.setLore(newLore);
            itemStackOn.setItemMeta(itemMetaOn);
        }
        return itemStackOn;
    }
    private ItemStack flagOff(String flagName) {
        guiOptions.put("flag_deny", Domain.getInstance().getData().getGUIOption("flag_deny", Material.RED_STAINED_GLASS_PANE, "&4Deny", "Denies the flag in the field", true));
        ItemStack itemStackOff = guiOptions.get("flag_deny").getItemStack();
        ItemMeta itemMetaOff = itemStackOff.getItemMeta();
        if (itemMetaOff != null) {
            itemMetaOff.setDisplayName(ColorFunctions.colorToString("&b" + flagName));
            itemMetaOff.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            List<String> newLore = new ArrayList<String>();
            if (itemMetaOff.getLore() != null) {
                newLore = itemMetaOff.getLore();
            }
            newLore.add(instance.getLocale().localeText("missing_flag_config", "&7This flag is not in the config file. Please update it", null));
            itemMetaOff.setLore(newLore);
            itemStackOff.setItemMeta(itemMetaOff);
        }
        return itemStackOff;
    }

    @Override
    public boolean isValidGUI() {
        return validGUI;
    }
    @Override
    public int getSlotCount() {
        return slotCount;
    }
    @Override
    public String getName() {
        return "BlockGUI";
    }
    private Player guiPlayer;
    @Override
    public void setPlayer(Player player) {
        this.guiPlayer = player;
    }
    @Override
    public Inventory getInventory() {

        // Getting our GUI Options
        guiOptions.put("na", Domain.getInstance().getData().getGUIOption("na", Material.BLACK_STAINED_GLASS_PANE, "&0.", "", true));
        guiOptions.put("disabled", Domain.getInstance().getData().getGUIOption("disabled", Material.BLACK_STAINED_GLASS_PANE, "", "&fThis option is not available", true));
        guiOptions.put("back", Domain.getInstance().getData().getGUIOption("back", Material.NETHER_STAR, "&bBack", "&fReturn to previous page", true));
        guiOptions.put("error", Domain.getInstance().getData().getGUIOption("error", Material.RED_STAINED_GLASS_PANE, "", "", true));
        guiOptions.put("info_flags_previous", Domain.getInstance().getData().getGUIOption("info_flags_previous", Material.LIGHT_BLUE_CONCRETE, "&bPrevious", "&fGoes to the previous page", true));
        guiOptions.put("info_flags_previous_disabled", Domain.getInstance().getData().getGUIOption("info_flags_previous_disabled", Material.GRAY_CONCRETE, "&4Previous", "No previous pages found", true));
        guiOptions.put("info_flags_next", Domain.getInstance().getData().getGUIOption("info_flags_next", Material.ORANGE_CONCRETE, "&bNext", "&fGoes to the next page", true));
        guiOptions.put("info_flags_next_disabled", Domain.getInstance().getData().getGUIOption("info_flags_next_disabled", Material.GRAY_CONCRETE, "&4Next", "No more pages found", true));

        if (flagMenuItems != null) {
            instance.getDebug().log("BlockFlagsGUI", "getInventory()", "flagMenuItems is not null");
            instance.getDebug().log("BlockFlagsGUI", "getInventory()", "flagMenuItems size: " + Integer.toString(flagMenuItems.size()));
        } else {
            instance.getDebug().log("BlockFlagsGUI", "getInventory()", "flagMenuItems is null");
        }

        if (guiPlayer != null) {
            instance.getDebug().log("BlockFlagsGUI", "getInventory()", "Player = " + guiPlayer.getDisplayName());
        }
        if (placedID >= 0) {
            DomainBlock pb = domainBlockManager.getDomainBlockByID(placedID);
            if (pb != null) {

                // Flag count grabs a count of all flags that are type 1 or 3
                int flagCount = 0;

                // Looping through all the flags, setting them with a value of 1
                // in the treemap.  This indicates it is an unused flag, so should
                // be included in the gui
                for (Flag.Type f : Flag.Type.values())
                {
                    treeFlags.put(f, 1); // Add the flag with value = 1 (1 = Unused Flag entry)
                }
                // Now loop through the config flags, and set any of the flags
                // in the treemap that are in the config to 2.  This indicates a
                // config flag and should not be included in the gui
                Set<Flag> lstConfigFlags = pb.getConfig().getFlags();
                if (lstConfigFlags != null) {
                    for (Flag flag : lstConfigFlags) {
                        treeFlags.put(flag.getFlagType(), 2); // Add the flag with value = 2 (2 = Config entry)
                        flagCount++;
                    }
                }
                // Now loop through the block flags for any already added
                // and set them to 3.  They will appear in the GUI, but with
                // a green entry to indicate they're already added
                Set<Flag> lstFlags = pb.getFlags();
                if (lstFlags != null) {
                    for (Flag flag : lstFlags) {
                        //tmFlags.put(flag.FlagType.name(), flag.Active);

                        treeFlags.put(flag.getFlagType(), 3); // Add the flag with value = 3 (3 = Added entry)
                        flagCount++;
                    }
                }

                String pageCount = "";
                /*
                if (flagCount >= 45) {
                    if (flagCount >= 45 && flagCount < 90) {
                        pageCount = " " + this.pageNum + "/2";
                    }
                    if (flagCount >= 90 && flagCount < 135) {
                        pageCount = " " + this.pageNum + "/3";
                    }
                    if (flagCount >= 135 && flagCount < 180) {
                        pageCount = " " + this.pageNum + "/4";
                    }
                    if (flagCount >= 180 && flagCount < 225) {
                        pageCount = " " + this.pageNum + "/5";
                    }
                    if (flagCount >= 225 && flagCount < 270) {
                        pageCount = " " + this.pageNum + "/6";
                    }
                }
                 */
                int treeFlagsSize = treeFlags.size();
                if (treeFlagsSize >= 45) {
                    if (treeFlagsSize >= 45 && treeFlagsSize < 90) {
                        pageCount = " " + this.pageNum + "/2";
                    }
                    if (treeFlagsSize >= 90 && treeFlagsSize < 135) {
                        pageCount = " " + this.pageNum + "/3";
                    }
                    if (treeFlagsSize >= 135 && treeFlagsSize < 180) {
                        pageCount = " " + this.pageNum + "/4";
                    }
                    if (treeFlagsSize >= 180 && treeFlagsSize < 225) {
                        pageCount = " " + this.pageNum + "/5";
                    }
                    if (treeFlagsSize >= 225 && treeFlagsSize < 270) {
                        pageCount = " " + this.pageNum + "/6";
                    }
                }

                Inventory GUI = Bukkit.createInventory(this, slotCount, instance.getLocale().localeText("gui_flags_title", "Flags", null) + pageCount);

                ItemStack itemStackNA = guiOptions.get("na").getItemStack();


                int position = 0;
                for (Map.Entry<Flag.Type, Integer> flagEntry : treeFlags.entrySet())
                {
                    //Logging.log("", flagEntry.getKey().name());
                    ItemStack itemFlag = new ItemStack(Material.AIR, 1);
                    if (flagEntry.getValue() == 1) {
                        if (flagMenuItems.containsKey(flagEntry.getKey().name())) {
                            itemFlag = flagMenuItems.get(flagEntry.getKey().name()).getItemStackDenied();
                        } else {
                            itemFlag = flagOff(flagEntry.getKey().name());
                        }
                    }
                    if (flagEntry.getValue() == 2) {
                        if (flagMenuItems.containsKey(flagEntry.getKey().name())) {
                            itemFlag = flagMenuItems.get(flagEntry.getKey().name()).getItemStackNA();
                        } else {
                            itemFlag = flagOff(flagEntry.getKey().name());
                        }
                    }
                    if (flagEntry.getValue() == 3) {
                        if (flagMenuItems.containsKey(flagEntry.getKey().name())) {
                            itemFlag = flagMenuItems.get(flagEntry.getKey().name()).getItemStackAllowed();
                        } else {
                            itemFlag = flagOn(flagEntry.getKey().name());
                        }
                    }

                    switch (this.pageNum)
                    {
                        case 1:
                            if (position < 45) {
                                GUI.setItem(position, itemStackNA); // default to na
                                if (flagEntry.getValue() != 2) {
                                    GUI.setItem(position, itemFlag);
                                    flagPos.put(position,  flagEntry.getKey());
                                }
                            }
                            break;
                        case 2:
                            if (position >= 45 && position < 90) {
                                GUI.setItem(position - 45, itemStackNA); // default to na
                                if (flagEntry.getValue() != 2) {
                                    GUI.setItem(position - 45, itemFlag);
                                    flagPos.put(position,  flagEntry.getKey());
                                }
                            }
                            break;
                        case 3:
                            if (position >= 90 && position < 135) {
                                GUI.setItem(position - 90, itemStackNA); // default to na
                                if (flagEntry.getValue() != 2) {
                                    GUI.setItem(position - 90, itemFlag);
                                    flagPos.put(position,  flagEntry.getKey());
                                }
                            }
                            break;
                        case 4:
                            if (position >= 135 && position < 180) {
                                GUI.setItem(position - 135, itemStackNA); // default to na
                                if (flagEntry.getValue() != 2) {
                                    GUI.setItem(position - 135, itemFlag);
                                    flagPos.put(position,  flagEntry.getKey());
                                }
                            }
                            break;
                        case 5:
                            if (position >= 180 && position < 225) {
                                GUI.setItem(position - 180, itemStackNA); // default to na
                                if (flagEntry.getValue() != 2) {
                                    GUI.setItem(position - 180, itemFlag);
                                    flagPos.put(position,  flagEntry.getKey());
                                }
                            }
                            break;
                        case 6:
                            if (position >= 225 && position < 270) {
                                GUI.setItem(position - 225, itemStackNA); // default to na
                                if (flagEntry.getValue() != 2) {
                                    GUI.setItem(position - 225, itemFlag);
                                    flagPos.put(position,  flagEntry.getKey());
                                }
                            }
                            break;
                        default:
                            break;
                    }
                    if (flagEntry.getValue() != 2) {
                        position++;
                    }
                }

                if (pageNum == 1) {
                    if (position < 45) {
                        for (int i = (position); i < 45; i++) {
                            GUI.setItem(i, itemStackNA);
                        }
                    }
                }
                if (pageNum == 2) {
                    if (position - 45 < 45) {
                        for (int i = (position - 45); i < 45; i++) {
                            GUI.setItem(i, itemStackNA);
                        }
                    }
                }
                if (pageNum == 3) {
                    if (position - 90 < 135) {
                        for (int i = (position - 90); i < 45; i++) {
                            GUI.setItem(i, itemStackNA);
                        }
                    }
                }
                if (pageNum == 4) {
                    if (position - 135 < 180) {
                        for (int i = (position - 135); i < 45; i++) {
                            GUI.setItem(i, itemStackNA);
                        }
                    }
                }
                if (pageNum == 5) {
                    if (position - 180 < 225) {
                        for (int i = (position - 180); i < 45; i++) {
                            GUI.setItem(i, itemStackNA);
                        }
                    }
                }
                if (pageNum == 6) {
                    if (position - 225 < 270) {
                        for (int i = (position - 225); i < 45; i++) {
                            GUI.setItem(i, itemStackNA);
                        }
                    }
                }



                GUI.setItem(45, guiOptions.get("back").getItemStack());

                GUI.setItem(46, itemStackNA);
                GUI.setItem(47, itemStackNA);
                GUI.setItem(48, itemStackNA);
                GUI.setItem(49, itemStackNA);
                GUI.setItem(50, itemStackNA);
                GUI.setItem(52, itemStackNA);


                if (position >= 45 & pageNum > 1) {
                    GUI.setItem(51, guiOptions.get("info_flags_previous").getItemStack());
                } else {
                    GUI.setItem(51, guiOptions.get("info_flags_previous_disabled").getItemStack());
                }

                if ((position > 45 && pageNum == 1) || (position > 90 && pageNum == 2) || (position > 135 && pageNum == 3) || (position > 180 && pageNum == 4) || (position > 225 && pageNum == 5)) {
                    GUI.setItem(53, guiOptions.get("info_flags_next").getItemStack());
                } else {
                    GUI.setItem(53, guiOptions.get("info_flags_next_disabled").getItemStack());
                }


                validGUI = true;
                return GUI;
            } else {
                Inventory GUI = Bukkit.createInventory(this, slotCount, instance.getLocale().localeText("gui_invalid", "Invalid Domain Block", null));
                ItemStack itemStackError = guiOptions.get("error").getItemStack();
                for(int i=0; i<=slotCount; i++){
                    GUI.setItem(i, itemStackError);
                }
                return GUI;
            }
        } else {
            Inventory GUI = Bukkit.createInventory(this, slotCount, instance.getLocale().localeText("gui_invalid", "Invalid Domain Block", null));
            ItemStack itemStackError = guiOptions.get("error").getItemStack();
            for(int i=0; i<=slotCount; i++){
                GUI.setItem(i, itemStackError);
            }

            return GUI;
        }

    }

    @Override
    public void onGUIClick(InventoryClickEvent event) {

        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true); // cancel the event no matter what
            if (this.validGUI) {
                if (event.getClick() == ClickType.LEFT) {
                    DomainBlock domainBlock = domainBlockManager.getDomainBlockByID(this.placedID);
                    if (domainBlock != null) {

                        HashMap<String, String> parameters = new HashMap<String, String>();
                        parameters.put("<%blocktype%>", domainBlock.getConfig().getDisplayName());

                        //boolean isOwner = Check.IsOwner(player, DomainBlock);
                        //boolean hasBypass = PlayerFunctions.hasBypass(player);
                        switch (event.getRawSlot()) {
                            case 45:
                                // Back
                                FlagsGUI flagsGUI = new FlagsGUI(
                                        domainBlock,
                                        player
                                );
                                SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                player.openInventory(flagsGUI.getInventory());
                                break;
                            case 51:
                                if (!event.getCurrentItem().getType().equals(guiOptions.get("info_flags_previous_disabled").getIcon())) {
                                    BlockUnusedFlagsGUI flagBackGUI = new BlockUnusedFlagsGUI(instance);
                                    flagBackGUI.setPlacedID(this.placedID);
                                    flagBackGUI.setPageNum(this.pageNum - 1);
                                    flagBackGUI.setPlayer(player);
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                    player.openInventory(flagBackGUI.getInventory());
                                }
                                break;
                            case 53:
                                if (!event.getCurrentItem().getType().equals(guiOptions.get("info_flags_next_disabled").getIcon())) {
                                    BlockUnusedFlagsGUI flagNextGUI = new BlockUnusedFlagsGUI(instance);
                                    flagNextGUI.setPlacedID(this.placedID);
                                    flagNextGUI.setPageNum(this.pageNum + 1);
                                    flagNextGUI.setPlayer(player);
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                    player.openInventory(flagNextGUI.getInventory());
                                }
                                break;
                            default:
                                if (event.getRawSlot() < 45) {

                                    int position = event.getRawSlot();
                                    if (this.pageNum == 2) {
                                        position = position + 45;
                                    }
                                    if (this.pageNum == 3) {
                                        position = position + 90;
                                    }
                                    if (this.pageNum == 4) {
                                        position = position + 135;
                                    }
                                    if (this.pageNum == 5) {
                                        position = position + 180;
                                    }
                                    if (this.pageNum == 6) {
                                        position = position + 225;
                                    }

                                    if (flagPos.containsKey(position)) {
                                        //instance.getDebug().log("BlockFlagsGUI", "onGUIClick", "Flag click - Position: " + Integer.toString(position));
                                        //instance.getDebug().log("BlockFlagsGUI", "onGUIClick", "Flag: " + flagPos.get(position));


                                        if (treeFlags.containsKey(flagPos.get(position))) {
                                            //instance.getDebug().log("BlockFlagsGUI", "onGUIClick", "Flag: " + flagPos.get(position) + " Flag status: " + Integer.toString(treeFlags.get(flagPos.get(position))));

                                            int flagStatus = treeFlags.get(flagPos.get(position));

                                            // TODO:
                                            switch (flagStatus) {
                                                case 1:
                                                    // flag status = 1 it is an unused flag, so add it
                                                    domainBlock.addFlag(new Flag(domainBlock.getPlacedId(), flagPos.get(position), true));
                                                    domainBlock.setDirty(true);
                                                    domainBlockManager.domainBlockSave(domainBlock);
                                                    flagMenuItems = Domain.getInstance().getData().getFlagMenuItems();
                                                    player.openInventory(this.getInventory());
                                                    break;
                                                case 2:
                                                    // flag status = 2 it is assigned to the block, leave it alone
                                                    break;
                                                case 3:
                                                    // flag status = 3 it is already added, remove it
                                                    domainBlock.removeFlag(new Flag(domainBlock.getPlacedId(), flagPos.get(position), true));
                                                    domainBlock.setDirty(true);
                                                    domainBlockManager.domainBlockSave(domainBlock);
                                                    flagMenuItems = Domain.getInstance().getData().getFlagMenuItems();
                                                    player.openInventory(this.getInventory());
                                                    break;
                                                default:
                                                    // do nothing
                                                    break;
                                            }
                                            break;
                                        } else {
                                            instance.getDebug().log("BlockFlagsGUI", "onGUIClick", "Unable to get flag status");
                                        }
                                    } else {
                                        instance.getDebug().log("BlockFlagsGUI", "onGUIClick", "Unable to get flag position");
                                    }




                                }
                                break;
                        }
                    }
                }
            } else {
                instance.getMessage().send(player, "gui_block_fail", "Unable to process action" , null, true);
            }
        } else {
            instance.getLog().log("BlockEditGUI", "onGUIClick(InventoryClickEvent)", "Inventory click event captured for non-player");
        }
    }
    @Override
    public void setKey(String key) {
        this.key = key;
        instance.getDebug().log("BlockUnusedFlagsGUI", "setKey", "Key = " + this.key);
    }
    @Override
    public void setPlacedID(int placedID) {
        this.placedID = placedID;
    }
    @Override
    public int getPlacedID() {
        return placedID;
    }
}