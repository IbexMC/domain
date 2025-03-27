package com.ibexmc.domain.gui;

import java.util.HashMap;
import java.util.Map;

import com.ibexmc.internal.functions.InventoryFunctions;
import com.ibexmc.internal.functions.SoundFunctions;
import com.ibexmc.internal.functions.StringFunctions;
import com.ibexmc.internal.messaging.Error;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.Domain.IGUI;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.domainblock.open.Merge;
import com.ibexmc.domain.domainblock.OpenDomainBlock;
import com.ibexmc.domain.domainblock.OpenDomainBlockType;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.open.Resize;
import com.ibexmc.domain.util.Permission;
import com.ibexmc.domain.util.functions.PlayerFunctions;

public class BlockEditGUI implements IGUI{
    
    private Domain instance;
    private DomainBlockManager domainBlockManager;
    private HashMap<String, GUIOption> guiOptions = new HashMap<String, GUIOption>();
    private boolean validGUI = false;
    private String key = "";
    private int placedID = -1;
    private int pageNum = 1;

    public BlockEditGUI(Domain instance) {
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
    private int slotCount = 45;

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
        instance.getDebug().log("BlockEditGUI", "getInventory()", "key = " + key);
        if (guiPlayer != null) {
            instance.getDebug().log("BlockEditGUI", "getInventory()", "Player = " + guiPlayer.getDisplayName());
        } else {
            instance.getDebug().log("BlockEditGUI", "getInventory()", "Player = NULL");
        }
        if (placedID >= 0) {
            DomainBlock pb = domainBlockManager.getDomainBlockByID(placedID);
            if (pb != null) {

                Inventory GUI = Bukkit.createInventory(this, slotCount, instance.getLocale().localeText("gui_edit_title", "Edit Field", null));

                // Getting our GUI Options
                guiOptions.put("na", Domain.getInstance().getData().getGUIOption("na", Material.BLACK_STAINED_GLASS_PANE, "&0.", "", true));
                guiOptions.put("disabled", Domain.getInstance().getData().getGUIOption("disabled", Material.BLACK_STAINED_GLASS_PANE, "", "&fThis option is not available", true));
                guiOptions.put("back", Domain.getInstance().getData().getGUIOption("back", Material.NETHER_STAR, "&bBack", "&fReturn to previous page", true));
                guiOptions.put("error", Domain.getInstance().getData().getGUIOption("error", Material.RED_STAINED_GLASS_PANE, "", "", true));
                guiOptions.put("edit_name", Domain.getInstance().getData().getGUIOption("edit_name", Material.NAME_TAG, "&bEdit Name", "&fModifies the name of the field", true));
                guiOptions.put("edit_welcome", Domain.getInstance().getData().getGUIOption("edit_welcome", Material.WRITABLE_BOOK, "&bEdit Welcome Message", "&fModifies the welcome message for the field", true));
                guiOptions.put("edit_farewell", Domain.getInstance().getData().getGUIOption("edit_farewell", Material.WRITABLE_BOOK, "&bEdit Farewell Message", "&fModifies the farewell message for the field", true));
                guiOptions.put("edit_resize", Domain.getInstance().getData().getGUIOption("edit_resize", Material.DAYLIGHT_DETECTOR, "&bResize", "&fGives a Domain Resize wand so you|&fcan resize the field", true));
                guiOptions.put("edit_merge", Domain.getInstance().getData().getGUIOption("edit_merge", Material.ANVIL, "&bMerge", "&fAllows you to merge multiple Domain blocks|&ftogether in order to increase max volume", true));
                guiOptions.put("edit_commands", Domain.getInstance().getData().getGUIOption("edit_commands", Material.COMMAND_BLOCK, "&bAdmin Commands", "&fAdministrator commands", true));
                guiOptions.put("edit_take", Domain.getInstance().getData().getGUIOption("edit_take", Material.DIAMOND_PICKAXE, "&bTake", "&fAllows you to remove the Domain block", true));
                guiOptions.put("edit_status", Domain.getInstance().getData().getGUIOption("edit_status", Material.LEVER, "&bEnable/Disable", "&fAllows you to change the status of|&fthe Domain block.  No flags will|&fbe applied if disabled", true));


                ItemStack itemStackNA = guiOptions.get("na").getItemStack();

                GUI.setItem(1, itemStackNA);
                GUI.setItem(2, itemStackNA);
                GUI.setItem(3, itemStackNA);
                GUI.setItem(4, itemStackNA);
                GUI.setItem(5, itemStackNA);
                GUI.setItem(6, itemStackNA);
                GUI.setItem(7, itemStackNA);
                GUI.setItem(8, itemStackNA);
                GUI.setItem(9, itemStackNA);
                GUI.setItem(11, itemStackNA);
                GUI.setItem(13, itemStackNA);
                GUI.setItem(15, itemStackNA);
                GUI.setItem(17, itemStackNA);
                GUI.setItem(18, itemStackNA);
                GUI.setItem(19, itemStackNA);
                GUI.setItem(20, itemStackNA);
                GUI.setItem(21, itemStackNA);
                GUI.setItem(22, itemStackNA);
                GUI.setItem(23, itemStackNA);
                GUI.setItem(24, itemStackNA);
                GUI.setItem(25, itemStackNA);
                GUI.setItem(26, itemStackNA);
                GUI.setItem(27, itemStackNA);
                GUI.setItem(29, itemStackNA);
                GUI.setItem(31, itemStackNA);
                GUI.setItem(33, itemStackNA);
                GUI.setItem(35, itemStackNA);
                GUI.setItem(36, itemStackNA);
                GUI.setItem(37, itemStackNA);
                GUI.setItem(38, itemStackNA);
                GUI.setItem(39, itemStackNA);
                GUI.setItem(40, itemStackNA);
                GUI.setItem(41, itemStackNA);
                GUI.setItem(42, itemStackNA);
                GUI.setItem(43, itemStackNA);
                GUI.setItem(44, itemStackNA);

                GUI.setItem(0, guiOptions.get("back").getItemStack());
                GUI.setItem(10, guiOptions.get("edit_name").getItemStack());
                GUI.setItem(12, guiOptions.get("edit_welcome").getItemStack());
                GUI.setItem(14, guiOptions.get("edit_farewell").getItemStack());


                if (pb.getConfig() == null) {
                    instance.getError().save(
                            "BlockEditGUI.getInventory.001",
                            "BlockEditGUI",
                            "getInventory()",
                            "Unexpected error opening menu",
                            "Unknown block type",
                            Error.Severity.WARN,
                            null
                    );
                    instance.getMessage().send(guiPlayer, "gui_error", "&4ERROR &f- Unexpected error opening menu", null, true);
                    return null;
                }

                if (pb.getConfig().isPreventResize() && !PlayerFunctions.hasBypass(guiPlayer)) {
                    GUIOption guiResize = guiOptions.get("edit_resize");
                    guiResize.setLore(StringFunctions.stringToList(instance.getLocale().localeText("gui_resize_lore_prevent", "&4This option is disabled for this type of Domain block", null)));
                    GUI.setItem(16, guiResize.getItemStack());
                } else {
                    GUI.setItem(16, guiOptions.get("edit_resize").getItemStack());
                }

                if (pb.getConfig().isPreventResize() && !PlayerFunctions.hasBypass(guiPlayer)) {
                    GUIOption guiMerge = guiOptions.get("edit_merge");
                    guiMerge.setLore(StringFunctions.stringToList(instance.getLocale().localeText("gui_resize_lore_prevent", "&4This option is disabled for this type of Domain block", null)));
                    GUI.setItem(28, guiMerge.getItemStack());
                } else {
                    GUI.setItem(28, guiOptions.get("edit_merge").getItemStack());
                }

                if (Permission.isAdmin(guiPlayer)) {
                    GUI.setItem(30, guiOptions.get("edit_commands").getItemStack());
                } else {
                    GUI.setItem(30, guiOptions.get("disabled").getItemStack());
                }

                if (!pb.getConfig().isOneTimeUse()) {
                    GUI.setItem(32, guiOptions.get("edit_take").getItemStack());
                } else {
                    GUIOption guiTake = guiOptions.get("edit_take");
                    guiTake.setName(instance.getLocale().localeText("gui_take_option_onetimeuse", "&4DESTROY", null));
                    guiTake.setLore(StringFunctions.stringToList(instance.getLocale().localeText("gui_take_lore_onetimeuse", "You can only destroy your Domain block|if there are no child fields within it", null)));
                    GUI.setItem(32, guiTake.getItemStack());
                }

                GUI.setItem(34, guiOptions.get("edit_status").getItemStack());


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
                        Map<String, String> parameters = new HashMap<>();
                        parameters.put("<%blocktype%>", domainBlock.getConfig().getDisplayName());

                        boolean isOwner = domainBlockManager.isOwner(player, domainBlock);
                        boolean hasBypass = PlayerFunctions.hasBypass(player);
                        switch (event.getRawSlot()) {
                            case 0:
                                MainGUI mainGUI = new MainGUI(
                                        instance,
                                        domainBlock,
                                        player
                                );
                                player.openInventory(mainGUI.getInventory());
                                SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                break;
                            case 10:
                                if (guiOptions.get("edit_name").isEnabled()) {
                                    instance.getMessage().send(player, "gui_block_name", "&eEnter the new name for the block in the chat (it will not be visible to other players)",null, true);
                                    Domain.getInstance().getData().openDomainBlockPut(this.placedID, player.getUniqueId(), OpenDomainBlockType.NAME);
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                    player.closeInventory();
                                } else {
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                                }
                                break;
                            case 12:
                                if (guiOptions.get("edit_welcome").isEnabled()) {
                                    instance.getMessage().send(player, "gui_block_welcome", "&eEnter the new welcome message in the chat (it will not be visible to other players)",null, true);
                                    Domain.getInstance().getData().openDomainBlockPut(this.placedID, player.getUniqueId(), OpenDomainBlockType.WELCOME);
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                    player.closeInventory();
                                } else {
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                                }
                                break;
                            case 14:
                                if (guiOptions.get("edit_farewell").isEnabled()) {
                                    instance.getMessage().send(player, "gui_block_farewell", "&eEnter the new farewell message in the chat (it will not be visible to other players)",null, true);
                                    Domain.getInstance().getData().openDomainBlockPut(this.placedID, player.getUniqueId(), OpenDomainBlockType.FAREWELL);
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                    player.closeInventory();
                                } else {
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                                }
                                break;
                            case 16:
                                if (domainBlock.getConfig().isPreventResize() && !PlayerFunctions.hasBypass(player))
                                {
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                                    break;
                                }
                                if (guiOptions.get("edit_resize").isEnabled()) {
                                    if (Domain.getInstance().getData().openDomainBlockExists(player.getUniqueId())) {
                                        OpenDomainBlock openDomainBlock = Domain.getInstance().getData().openDomainBlockGet(player.getUniqueId());
                                        switch (openDomainBlock.getOpenDomainBlockType()) {
                                            case COMMANDS:
                                                instance.getMessage().send(player, "block_closed_to_commands", "This <%blocktype%> field has been closed for commands", parameters, true);
                                                break;
                                            case OWNER:
                                                instance.getMessage().send(player, "block_closed_to_owner_change", "This <%blocktype%> field has been closed for changing owner", parameters, true);
                                                break;
                                            case TAKE:
                                                instance.getMessage().send(player, "block_closed_to_take", "This <%blocktype%> field has been closed for taking", parameters, true);
                                                break;
                                            default:
                                                break;
                                        }
                                        Domain.getInstance().getData().openDomainBlockRemove(player.getUniqueId());
                                        openDomainBlock.closeEffects(player, domainBlock);
                                    } else {
                                        // Resize
                                        ItemStack wand = Resize.wand();

                                        if (InventoryFunctions.addItemsToPlayerInventory(player, wand, false)) {
                                            instance.getMessage().send(player, "block_opened_to_resize", "This <%blocktype%> field has been opened for resize", parameters, true);
                                            instance.getMessage().send(player, "block_opened_to_resize1", "Use the wand to set each corner of the desired size", parameters, true);
                                            instance.getMessage().send(player, "block_opened_to_resize2", "Left click corner 1 and Right click corner 2", parameters, true);
                                            instance.getMessage().send(player, "block_opened_to_resize3", "When you have set both corners, right click the Domain block with the wand to set the field size", parameters, true);

                                            Domain.getInstance().getData().openBlocks.put(
                                                    player.getUniqueId(),
                                                    new Resize(
                                                            domainBlock.getPlacedId()
                                                    )
                                            );
                                            OpenDomainBlock openDomainBlock = new OpenDomainBlock(domainBlock.getPlacedId(), OpenDomainBlockType.NA);
                                            openDomainBlock.openEffects(player, domainBlock);
                                        } else {
                                            // Unable to give the player the resize wand
                                            SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                                        }


                                    }
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                    player.closeInventory();
                                } else {
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                                }
                                break;
                            case 28:
                                if (domainBlock.getConfig().isPreventResize() && !PlayerFunctions.hasBypass(player))
                                {
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                                    break;
                                }
                                if (guiOptions.get("edit_merge").isEnabled()) {
                                    if (isOwner || hasBypass) {
                                        instance.getMessage().send(player, "gui_block_merge_1", "You can now merge another Domain block of the same type with this Domain block" ,null, true);
                                        instance.getMessage().send(player, "gui_block_merge_2", "Right click the Domain block while holding your unused Domain block to merge" ,null, true);
                                        Domain.getInstance().getData().openBlocks.put(
                                                player.getUniqueId(),
                                                new Merge(
                                                        domainBlock.getPlacedId()
                                                )
                                        );
                                        OpenDomainBlock openDomainBlock = new OpenDomainBlock(domainBlock.getPlacedId(), OpenDomainBlockType.NA);
                                        openDomainBlock.openEffectsWithoutVisualization(player, domainBlock);
                                    } else {
                                        instance.getMessage().send(player, "gui_invalid_perm", "&4You do not have permission to access this command",null, true);
                                    }
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                    player.closeInventory();
                                } else {
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                                }
                                break;
                            case 30:
                                if (Permission.isAdmin(guiPlayer)) {
                                    if (guiOptions.get("edit_commands").isEnabled()) {
                                        if (isOwner || hasBypass) {
                                            BlockCommandsGUI commandsGUI = new BlockCommandsGUI(instance);
                                            commandsGUI.setPlacedID(this.placedID);
                                            commandsGUI.setPlayer(player);
                                            SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                            player.openInventory(commandsGUI.getInventory());
                                        } else {
                                            //instance.getMessage().send("gui_invalid_perm", "&4You do not have permission to access this command",null, true);
                                            SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                                        }
                                    } else {
                                        SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                                    }
                                } else {
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                                }
                                break;
                            case 32:
                                if (guiOptions.get("edit_take").isEnabled()) {
                                    DomainBlockTakeConfirmGUI guiConfirmTake = new DomainBlockTakeConfirmGUI(instance);
                                    guiConfirmTake.setPlacedID(domainBlock.getPlacedId());
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                    player.openInventory(guiConfirmTake.getInventory());
                                } else {
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                                }
                                break;
                            case 34:
                                if (guiOptions.get("edit_status").isEnabled()) {
                                    if (domainBlockManager.isOwner(guiPlayer, domainBlock)) {
                                        if (domainBlock.isActive()) {
                                            instance.getMessage().send(player, "gui_block_disabled", "This field is now &4disabled" ,null, true);
                                        } else {
                                            instance.getMessage().send(player, "gui_block_enabled", "This field is now &2enabled" ,null, true);
                                        }
                                        domainBlock.setActive(!domainBlock.isActive());
                                        domainBlock.setDirty(true);
                                        domainBlockManager.domainBlockSave(domainBlock);
                                        SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                    } else {
                                        instance.getMessage().send(player, "gui_block_disable_no_perm", "You do not have permission to disable this field" ,null, true);
                                    }
                                    player.closeInventory();
                                } else {
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                                }
                                break;
                            default:
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