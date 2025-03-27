package com.ibexmc.domain.gui;

import java.util.*;

import com.ibexmc.internal.functions.SoundFunctions;
import com.ibexmc.internal.functions.StringFunctions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.Domain.IGUI;

import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.domainblock.OpenDomainBlockType;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.helper.BlockCommands;
import com.ibexmc.domain.util.functions.PlayerFunctions;

public class BlockPermsGUI implements IGUI{

    private Domain instance;
    private DomainBlockManager domainBlockManager;
    private HashMap<String, GUIOption> guiOptions = new HashMap<String, GUIOption>();
    private boolean validGUI = false;
    private int pageNum = 1;

    public BlockPermsGUI(Domain instance) {
        this.instance = instance;
        domainBlockManager = new DomainBlockManager(instance);
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

    private void loadOptions() {
        // Getting our GUI Options
        guiOptions.put("na", Domain.getInstance().getData().getGUIOption("na", Material.BLACK_STAINED_GLASS_PANE, "&0.", "", true));
        guiOptions.put("disabled", Domain.getInstance().getData().getGUIOption("disabled", Material.BLACK_STAINED_GLASS_PANE, "", "&fThis option is not available", true));
        guiOptions.put("back", Domain.getInstance().getData().getGUIOption("back", Material.NETHER_STAR, "&bBack", "&fReturn to previous page", true));
        guiOptions.put("error", Domain.getInstance().getData().getGUIOption("error", Material.RED_STAINED_GLASS_PANE, "", "", true));
        guiOptions.put("permission_add", Domain.getInstance().getData().getGUIOption("permission_add", Material.TOTEM_OF_UNDYING, "&bAdd Player", "&fAllows you to add a player to the field", true));
        guiOptions.put("permission_make_owner", Domain.getInstance().getData().getGUIOption("permission_make_owner", Material.END_CRYSTAL, "&bMake Owner", "&fChanges ownership of the block to the player", true));
        guiOptions.put("permission_remove", Domain.getInstance().getData().getGUIOption("permission_remove", Material.BARRIER, "&bRemove Player", "&fRemoves the player from the field permissions", true));
        guiOptions.put("permission_edit_player_blank", Domain.getInstance().getData().getGUIOption("permission_edit_player_blank", Material.YELLOW_STAINED_GLASS_PANE, "", "", true));
        guiOptions.put("permission_add_blank_1", Domain.getInstance().getData().getGUIOption("permission_add_blank_1", Material.GREEN_STAINED_GLASS_PANE, "", "", true));
        guiOptions.put("permission_add_blank_2", Domain.getInstance().getData().getGUIOption("permission_add_blank_2", Material.LIME_STAINED_GLASS_PANE, "", "", true));
        guiOptions.put("permission_previous", Domain.getInstance().getData().getGUIOption("permission_previous", Material.LIGHT_BLUE_CONCRETE, "&bPrevious", "&fGoes to the previous page", true));
        guiOptions.put("permission_previous_disabled", Domain.getInstance().getData().getGUIOption("permission_previous_disabled", Material.GRAY_CONCRETE, "&4Previous", "No previous pages found", true));
        guiOptions.put("permission_next", Domain.getInstance().getData().getGUIOption("permission_next", Material.ORANGE_CONCRETE, "&bNext", "&fGoes to the next page", true));
        guiOptions.put("permission_next_disabled", Domain.getInstance().getData().getGUIOption("permission_next_disabled", Material.GRAY_CONCRETE, "&4Next", "No more pages found", true));
    }
    private GUIOption getOption(String key) {
        GUIOption returnOption;
        if (this.guiOptions.containsKey(key)) {
            returnOption = this.guiOptions.get(key);
        } else {
            returnOption = new GUIOption(Material.BARRIER, key, StringFunctions.stringToList("&4This option is missing from the config"), true);
        }
        return returnOption;
    }

    @Override
    public void setPlayer(Player player) {
        this.guiPlayer = player;
    }
    @Override
    public Inventory getInventory() {
        instance.getDebug().log("BlockPermsGUI", "getInventory()", "Key = " + key);


        loadOptions();

        if (placedID >= 0) {
            DomainBlock pb = domainBlockManager.getDomainBlockByID(placedID);
            if (pb != null) {

                TreeMap<UUID, ItemStack> tmPerms = new TreeMap<>();

                if (pb.getAllowedPlayers() != null) {
                    for (UUID uuid : pb.getAllowedPlayers()) {
                        ItemStack head = PlayerFunctions.head(uuid);
                        if (head != null) {
                            ItemMeta headMeta = head.getItemMeta();
                            headMeta.setDisplayName(ChatColor.LIGHT_PURPLE + ChatColor.stripColor(headMeta.getDisplayName()));
                            List<String> lore = new ArrayList<String>();
                            lore.add("" + uuid);
                            if (uuid.equals(pb.getOwner())) {
                                lore.add(ChatColor.GOLD + instance.getLocale().localeText("owner_title", "OWNER", null));
                            }
                            headMeta.setLore(lore);
                            head.setItemMeta(headMeta);

                            tmPerms.put(uuid, head);
                        }
                    }
                }

                String pageCount = "";
                if (tmPerms.size() >= 45) {
                    if (tmPerms.size() >= 45 && tmPerms.size() < 90) {
                        pageCount = " " + Integer.toString(this.pageNum) + "/2";
                    }
                    if (tmPerms.size() >= 90) {
                        pageCount = " " + Integer.toString(this.pageNum) + "/3";
                    }
                }

                Inventory GUI = Bukkit.createInventory(this, slotCount, instance.getLocale().localeText("gui_perms_title", "Permissions", null) + pageCount);

                ItemStack itemStackNA = guiOptions.get("na").getItemStack();

                int position = 0;
                for (Map.Entry<UUID, ItemStack> allowedEntry : tmPerms.entrySet()) {
                    switch (this.pageNum)
                    {
                        case 1:
                            if (position < 45) {
                                GUI.setItem(position, allowedEntry.getValue());
                            }
                            break;
                        case 2:
                            if (position >= 45 && position < 90) {
                                GUI.setItem(position - 45, allowedEntry.getValue());
                            }
                            break;
                        case 3:
                            if (position >= 90 && position < 135) {
                                GUI.setItem(position - 90, allowedEntry.getValue());
                            }
                            break;
                        default:
                            break;
                    }

                    position++;
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


                GUI.setItem(45, getOption("back").getItemStack());

                GUI.setItem(46, getOption("permission_add_blank_1").getItemStack());
                GUI.setItem(47, getOption("permission_add_blank_2").getItemStack());
                GUI.setItem(49, getOption("permission_add_blank_2").getItemStack());
                GUI.setItem(50, getOption("permission_add_blank_1").getItemStack());
                GUI.setItem(52, itemStackNA);
                GUI.setItem(48, getOption("permission_add").getItemStack());

                if (position >= 45 & pageNum > 1) {
                    GUI.setItem(51, getOption("permission_previous").getItemStack());
                } else {
                    GUI.setItem(51, getOption("permission_previous_disabled").getItemStack());
                }

                if ((position > 45 && pageNum == 1) || (position > 90 && pageNum == 2)) {
                    GUI.setItem(53, getOption("permission_next").getItemStack());
                } else {
                    GUI.setItem(53, getOption("permission_next_disabled").getItemStack());
                }


                validGUI = true;
                return GUI;
            } else {
                Inventory GUI = Bukkit.createInventory(this, slotCount, instance.getLocale().localeText("gui_invalid", "Invalid Domain Block", null));
                ItemStack itemStackError = getOption("error").getItemStack();
                for(int i=0; i<=slotCount; i++){
                    GUI.setItem(i, itemStackError);
                }
                return GUI;
            }
        } else {
            Inventory GUI = Bukkit.createInventory(this, slotCount, instance.getLocale().localeText("gui_invalid", "Invalid Domain Block", null));
            ItemStack itemStackError = getOption("error").getItemStack();
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
            if (event.getClickedInventory() != null) {
                if (this.validGUI) {
                    if (event.getClick() == ClickType.LEFT) {
                        DomainBlock domainBlock = domainBlockManager.getDomainBlockByID(this.placedID);
                        //Location DomainBlockLocation = new Location(Bukkit.getWorld(DomainBlock.getWorldName()), DomainBlock.getBlockX(), DomainBlock.getBlockY(), DomainBlock.getBlockZ());
                        if (domainBlock != null) {
                            Map<String, String> parameters = new HashMap<>();
                            parameters.put("<%blocktype%>", domainBlock.getConfig().getDisplayName());

                            //boolean isOwner = Check.IsOwner(player, DomainBlock);
                            //boolean hasBypass = PlayerFunctions.hasBypass(player);
                            switch (event.getRawSlot()) {
                                case 45:
                                    // Back
                                    MainGUI homeGUI = new MainGUI(
                                            instance,
                                            domainBlock,
                                            player
                                    );
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                    player.openInventory(homeGUI.getInventory());
                                    break;
                                case 51:
                                    if (!event.getCurrentItem().getType().equals(getOption("permission_previous_disabled").getIcon())) {
                                        BlockPermsGUI flagBackGUI = new BlockPermsGUI(instance);
                                        flagBackGUI.setPlacedID(this.placedID);
                                        flagBackGUI.setPageNum(this.pageNum - 1);
                                        SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                        player.openInventory(flagBackGUI.getInventory());
                                    }
                                    break;
                                case 53:
                                    if (!event.getCurrentItem().getType().equals(getOption("permission_next_disabled").getIcon())) {
                                        BlockPermsGUI flagNextGUI = new BlockPermsGUI(instance);
                                        flagNextGUI.setPlacedID(this.placedID);
                                        flagNextGUI.setPageNum(this.pageNum + 1);
                                        SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                        player.openInventory(flagNextGUI.getInventory());
                                    }
                                    break;
                                case 47:
                                    if (event.getCurrentItem().getType().equals(getOption("permission_remove").getIcon())) {
                                        // get uuid from player head
                                        if (event.getClickedInventory().getItem(48) != null) {
                                            ItemMeta allowedUUIDMeta = event.getClickedInventory().getItem(48).getItemMeta();
                                            if (allowedUUIDMeta != null) {
                                                List<String> lore = allowedUUIDMeta.getLore();
                                                if (lore != null) {
                                                    String loreText = lore.get(0);
                                                    UUID allowUUID = UUID.fromString(loreText);
                                                    if (allowUUID.toString().equalsIgnoreCase((player.getUniqueId().toString()))) {
                                                        instance.getMessage().send(player,"gui_remove_player_fail_self", "&4You cannot remove yourself from the field", null, true);
                                                        event.setCancelled(true);
                                                        return;
                                                    }
                                                    if (domainBlock.getOwner() != null) {
                                                        if (allowUUID.toString().equalsIgnoreCase((domainBlock.getOwner().toString()))) {
                                                            instance.getMessage().send(player,"gui_remove_player_is_owner", "&4You cannot remove the owner from the field", null, true);
                                                            event.setCancelled(true);
                                                            return;
                                                        }
                                                    }
                                                    if (allowUUID != null) {
                                                        BlockCommands blockCommands = new BlockCommands(instance);
                                                        blockCommands.removeOwnerAllowed(player, allowUUID, this.placedID);
                                                        BlockPermsGUI reloadGUI = new BlockPermsGUI(instance);
                                                        reloadGUI.setPlacedID(this.placedID);
                                                        reloadGUI.setPageNum(this.pageNum);
                                                        SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                                        player.openInventory(reloadGUI.getInventory());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    break;
                                case 48:
                                    if (event.getCurrentItem().getType().equals(getOption("permission_add").getIcon())) {
                                        instance.getMessage().send(player,"gui_allow_player_info", "&eEnter the name of the player to add using the chat (it won't display to anyone else) or right click the player", null, true);
                                        Domain.getInstance().getData().openDomainBlockPut(this.placedID, player.getUniqueId(), OpenDomainBlockType.ALLOW_PLAYER);
                                        SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                        player.closeInventory();
                                        break;
                                    }
                                    if (event.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {


                                        event.getClickedInventory().setItem(46, getOption("permission_add_blank_1").getItemStack());
                                        event.getClickedInventory().setItem(47, getOption("permission_add_blank_2").getItemStack());


                                        event.getClickedInventory().setItem(48, getOption("permission_add").getItemStack());

                                        event.getClickedInventory().setItem(49, getOption("permission_add_blank_1").getItemStack());
                                        event.getClickedInventory().setItem(50, getOption("permission_add_blank_2").getItemStack());
                                    }
                                    break;
                                case 49:
                                    if (event.getCurrentItem().getType().equals(getOption("permission_make_owner").getIcon())) {
                                        instance.getMessage().send(player,"gui_change_owner_warning", "&4CAUTION: Changing Owner cannot be undone", null, true);
                                        HashMap<String, String> placeHolder = new HashMap<String, String>();
                                        if (event.getClickedInventory().getItem(48) != null) {
                                            ItemMeta makeOwnerMeta = event.getClickedInventory().getItem(48).getItemMeta();
                                            if (makeOwnerMeta != null) {

                                                List<String> lore = makeOwnerMeta.getLore();
                                                if (lore != null) {
                                                    String loreText = lore.get(0);
                                                    UUID newOwnerUUID = UUID.fromString(loreText);
                                                    if (newOwnerUUID != null) {
                                                        placeHolder.put("<%playername%>", ChatColor.stripColor(makeOwnerMeta.getDisplayName()));
                                                        if (newOwnerUUID.equals(domainBlock.getOwner())) {
                                                            instance.getMessage().send(player,"gui_change_owner_already_owner", "&4Unable to change owner. <%playername%> is already set as owner", placeHolder, true);
                                                        } else {
                                                            instance.getMessage().send(player,"gui_change_owner_confirm", "&eTo confirm the new owner, type the following (in pink) exactly in the chat: &dconfirm <%playername%>", placeHolder, true);
                                                            Domain.getInstance().getData().openDomainBlockPut(this.placedID, player.getUniqueId(), OpenDomainBlockType.OWNER, newOwnerUUID, ChatColor.stripColor(makeOwnerMeta.getDisplayName()));
                                                            SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                                            player.closeInventory();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    break;
                                default:
                                    if (event.getRawSlot() < 45) {

                                        event.getClickedInventory().setItem(46, getOption("permission_edit_player_blank").getItemStack());
                                        event.getClickedInventory().setItem(50, getOption("permission_edit_player_blank").getItemStack());

                                        event.getClickedInventory().setItem(47, getOption("permission_remove").getItemStack());

                                        ItemStack itemStack = event.getCurrentItem();
                                        event.getClickedInventory().setItem(48, itemStack);

                                        event.getClickedInventory().setItem(49, getOption("permission_make_owner").getItemStack());

                                    }
                                    break;
                            }
                        }
                    }
                } else {
                    instance.getMessage().send(player,"gui_block_fail", "Unable to process action" , null, true);
                }
            }
        } else {
            instance.getLog().log("BlockEditGUI", "onGUIClick(InventoryClickEvent)", "Inventory click event captured for non-player");
        }
    }
    @Override
    public void setKey(String key) {
        this.key = key;
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