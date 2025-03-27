package com.ibexmc.domain.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ibexmc.domain.flag.Flag;
import com.ibexmc.internal.functions.ColorFunctions;
import com.ibexmc.internal.functions.SoundFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import com.ibexmc.internal.messaging.Error;
import org.bukkit.Bukkit;
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
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.domainblock.OpenDomainBlock;
import com.ibexmc.domain.domainblock.OpenDomainBlockType;
import com.ibexmc.domain.domainblock.open.Magnet;
import com.ibexmc.domain.domainblock.open.Teleport;
import com.ibexmc.domain.util.Permission;

public class BlockCommandsGUI implements IGUI {

    private Domain instance;
    private DomainBlockManager domainBlockManager;
    private HashMap<String, GUIOption> guiOptions = new HashMap<String, GUIOption>();
    private boolean validGUI = false;
    private String key = "";
    private int placedID = -1;
    private int pageNum = 1;

    public BlockCommandsGUI(Domain instance) {
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

    private int slotCount = 27;

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
        return "CommandGUI";
    }

    private Player guiPlayer;

    @Override
    public void setPlayer(Player player) {
        this.guiPlayer = player;
    }

    @Override
    public Inventory getInventory() {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        instance.getDebug().log("BlockCommandsGUI", "getInventory()", "key = " + key);
        if (guiPlayer != null) {
            instance.getDebug().log("BlockCommandsGUI", "getInventory()", "Player = " + guiPlayer.getDisplayName());
        } else {
            instance.getDebug().log("BlockCommandsGUI", "getInventory()", "Player = NULL");
        }
        if (placedID >= 0) {
            DomainBlock pb = domainBlockManager.getDomainBlockByID(placedID);
            if (pb != null) {

                Inventory GUI = Bukkit.createInventory(this, slotCount,
                        instance.getLocale().localeText("gui_commands_title", "Commands", null));

                // Getting our GUI Options
                guiOptions.put("na", Domain.getInstance().getData().getGUIOption("na",
                        Material.BLACK_STAINED_GLASS_PANE, "&0.", "", true));
                guiOptions.put("back", Domain.getInstance().getData().getGUIOption("back", Material.NETHER_STAR,
                        "&bBack", "&fReturn to previous page", true));
                guiOptions.put("error", Domain.getInstance().getData().getGUIOption("error",
                        Material.RED_STAINED_GLASS_PANE, "", "", true));
                guiOptions.put("teleport",
                        Domain.getInstance().getData().getGUIOption("teleport", Material.END_CRYSTAL, "&bTeleport",
                                "&fGives a teleport-to wand to set the|&fteleport location of the field", true));
                guiOptions.put("teleport_disabled",
                        Domain.getInstance().getData().getGUIOption("teleport_disabled", Material.END_CRYSTAL,
                                "&4Teleport disabled - Flag not set",
                                "&fYou must set the Teleport flag in order to|&faccess the teleport to command", true));


                guiOptions.put("magnet",
                        Domain.getInstance().getData().getGUIOption("magnet", Material.IRON_INGOT, "&bMagnet",
                                "&fGives a magnet wand to set the|&fmagnet location of the field", true));
                guiOptions.put("magnet_disabled",
                        Domain.getInstance().getData().getGUIOption("magnet_disabled", Material.END_CRYSTAL,
                                "&4Magnet disabled - Flag not set",
                                "&fYou must set the Magnet flag in order to|&faccess the magnet command", true));

                guiOptions.put("adminblock", Domain.getInstance().getData().getGUIOption("adminblock",
                        Material.END_CRYSTAL, "&bAdmin Block", "&fConvert this to an Admin Block", true));
                guiOptions.put("adminblock_set", Domain.getInstance().getData().getGUIOption("adminblock_set",
                        Material.PLAYER_HEAD, "&4Admin Block", "&4Convert back to a player block", true));
                guiOptions.put("command_on_enter",
                        Domain.getInstance().getData().getGUIOption("command_on_enter",
                                Material.REPEATING_COMMAND_BLOCK, "&bRun Command On Enter",
                                "&fAllows you to set the command to run|&fwhen someone enters the field", true));
                guiOptions.put("command_on_enter_disabled",
                        Domain.getInstance().getData().getGUIOption("command_on_enter_disabled", Material.BARRIER,
                                "&4Run Command On Enter disabled - Flag not set",
                                "&fYou must set the Run Command flag in order to|&faccess Run Command on Enter", true));
                guiOptions.put("command_on_leave",
                        Domain.getInstance().getData().getGUIOption("command_on_leave",
                                Material.CHAIN_COMMAND_BLOCK, "&bRun Command On Leave",
                                "&fAllows you to set the command to run|&fwhen someone leaves the field", true));
                guiOptions.put("command_on_leave_disabled",
                        Domain.getInstance().getData().getGUIOption("command_on_leave_disabled", Material.BARRIER,
                                "&4Run Command On Leave disabled - Flag not set",
                                "&fYou must set the Run Command flag in order to|&faccess Run Command on Leave", true));

                ItemStack itemStackNA = guiOptions.get("na").getItemStack();

                GUI.setItem(1, itemStackNA);
                GUI.setItem(2, itemStackNA);
                GUI.setItem(3, itemStackNA);
                GUI.setItem(4, itemStackNA);
                GUI.setItem(5, itemStackNA);
                GUI.setItem(6, itemStackNA);
                GUI.setItem(7, itemStackNA);
                GUI.setItem(8, itemStackNA);

                GUI.setItem(10, itemStackNA);
                GUI.setItem(12, itemStackNA);
                GUI.setItem(14, itemStackNA);
                GUI.setItem(16, itemStackNA);





                GUI.setItem(18, itemStackNA);
                GUI.setItem(19, itemStackNA);
                GUI.setItem(20, itemStackNA);
                GUI.setItem(21, itemStackNA);
                GUI.setItem(22, itemStackNA);
                GUI.setItem(23, itemStackNA);
                GUI.setItem(24, itemStackNA);
                GUI.setItem(25, itemStackNA);
                GUI.setItem(26, itemStackNA);

                GUI.setItem(0, guiOptions.get("back").getItemStack());

                instance.getDebug().log("BlockCommandGUI", "getInventory", "Checking Teleport Flag");
                if (domainBlockManager.hasActiveFlag(pb, Flag.Type.TELEPORT_TO)) {
                    instance.getDebug().log("BlockCommandGUI", "getInventory", "Has teleport flag");

                    if (guiOptions.containsKey("teleport")) {
                        instance.getDebug().log("BlockCommandsGUI", "getInventory()", "guiOptions for teleport exists");
                        GUIOption goTeleport = guiOptions.get("teleport");
                        if (goTeleport != null) {
                            ItemStack teleportGUIOption = goTeleport.getItemStack();
                            if (teleportGUIOption != null) {
                                teleportGUIOption = new ItemStack(Material.COMPASS, 1);
                                ItemMeta teleportMeta = teleportGUIOption.getItemMeta();
                                if (teleportMeta != null) {
                                    teleportMeta.setDisplayName("Teleport");
                                    if (pb.getTeleportToLocation() != null) {
                                        instance.getDebug().log("BlockCommandGUI", "getInventory", "TP To is not null");
                                        List<String> tpLore = new ArrayList<String>();
                                        if (teleportMeta.getLore() != null) {
                                            instance.getDebug().log("BlockCommandGUI", "getInventory",
                                                    "Existing lore is not null");
                                            tpLore = teleportMeta.getLore();
                                        }
                                        instance.getDebug().log("BlockCommandGUI", "getInventory", "Adding fancy lite string");

                                        tpLore.add(ColorFunctions.colorToString(WorldFunctions.fancyReadable(pb.getTeleportToLocation())));

                                        instance.getDebug().log("BlockCommandGUI", "getInventory", "Setting lore");
                                        teleportMeta.setLore(tpLore);
                                    } else {
                                        instance.getDebug().log("BlockCommandGUI", "getInventory", "TP To is null");
                                    }
                                    instance.getDebug().log("BlockCommandGUI", "getInventory", "Setting meta");
                                    teleportGUIOption.setItemMeta(teleportMeta);
                                }
                            } else {
                                instance.getDebug().log("BlockCommandGUI", "getInventory", "GUI Option for tp is null");
                            }
                            GUI.setItem(9, teleportGUIOption);
                        } else {
                            instance.getDebug().log("BlockCommandsGUI", "getInventory()", "guiOption is null");
                        }
                    } else {
                        instance.getDebug().log("BlockCommandsGUI", "getInventory()", "guiOptions for teleport does not exist");
                    }

                } else {
                    instance.getDebug().log("BlockCommandGUI", "getInventory", "Does not have teleport flag");
                    ItemStack teleportGUIOption = guiOptions.get("teleport_disabled").getItemStack();
                    if (teleportGUIOption == null) {
                        teleportGUIOption = new ItemStack(Material.BARRIER, 1);
                        ItemMeta teleportMeta = teleportGUIOption.getItemMeta();
                        teleportMeta.setDisplayName("Teleport - Disabled");
                        teleportGUIOption.setItemMeta(teleportMeta);
                    }
                    GUI.setItem(9, teleportGUIOption);
                }

                // magnet
                if (domainBlockManager.hasActiveFlag(pb, Flag.Type.MONSTER_MAGNET) || domainBlockManager.hasActiveFlag(pb, Flag.Type.ANIMAL_MAGNET)) {
                    instance.getDebug().log("BlockCommandGUI", "getInventory", "Has magnet flag");

                    if (guiOptions.containsKey("magnet")) {
                        instance.getDebug().log("BlockCommandsGUI", "getInventory()", "guiOptions for magnet exists");
                        GUIOption goMagnet = guiOptions.get("magnet");
                        if (goMagnet != null) {
                            ItemStack magnetGUIOption = goMagnet.getItemStack();
                            if (magnetGUIOption != null) {
                                magnetGUIOption = new ItemStack(Material.COMPASS, 1);
                                ItemMeta magnetMeta = magnetGUIOption.getItemMeta();
                                if (magnetMeta != null) {
                                    magnetMeta.setDisplayName("Magnet");
                                    if (pb.getTeleportToLocation() != null) {
                                        instance.getDebug().log("BlockCommandGUI", "getInventory", "Magnet is not null");
                                        List<String> magnetLore = new ArrayList<String>();
                                        if (magnetMeta.getLore() != null) {
                                            instance.getDebug().log("BlockCommandGUI", "getInventory",
                                                    "Existing lore is not null");
                                            magnetLore = magnetMeta.getLore();
                                        }
                                        instance.getDebug().log("BlockCommandGUI", "getInventory", "Adding fancy lite string");

                                        magnetLore.add(ColorFunctions.colorToString(WorldFunctions.fancyReadable(pb.getMagnetLocation())));

                                        instance.getDebug().log("BlockCommandGUI", "getInventory", "Setting lore");
                                        magnetMeta.setLore(magnetLore);
                                    } else {
                                        instance.getDebug().log("BlockCommandGUI", "getInventory", "Magnet is null");
                                    }
                                    instance.getDebug().log("BlockCommandGUI", "getInventory", "Setting meta");
                                    magnetGUIOption.setItemMeta(magnetMeta);
                                }
                            } else {
                                instance.getDebug().log("BlockCommandGUI", "getInventory", "GUI Option for magnet is null");
                            }
                            GUI.setItem(11, magnetGUIOption);
                        } else {
                            instance.getDebug().log("BlockCommandsGUI", "getInventory()", "guiOption is null");
                        }
                    } else {
                        instance.getDebug().log("BlockCommandsGUI", "getInventory()", "guiOptions for magnet does not exist");
                    }

                } else {
                    instance.getDebug().log("BlockCommandGUI", "getInventory", "Does not have a magnet flag");
                    ItemStack magnetGUIOption = guiOptions.get("magnet_disabled").getItemStack();
                    if (magnetGUIOption == null) {
                        magnetGUIOption = new ItemStack(Material.BARRIER, 1);
                        ItemMeta magnetMeta = magnetGUIOption.getItemMeta();
                        magnetMeta.setDisplayName("Magnet - Disabled");
                        magnetGUIOption.setItemMeta(magnetMeta);
                    }
                    GUI.setItem(11, magnetGUIOption);
                }

                if (pb.isAdminBlock()) {
                    ItemStack adminblockGUIOption = guiOptions.get("adminblock_set").getItemStack();
                    if (adminblockGUIOption == null) {
                        adminblockGUIOption = new ItemStack(Material.BARRIER, 1);
                        ItemMeta teleportMeta = adminblockGUIOption.getItemMeta();
                        teleportMeta.setDisplayName("Admin Block - Already Set");
                        adminblockGUIOption.setItemMeta(teleportMeta);
                    }
                    GUI.setItem(13, adminblockGUIOption);
                } else {
                    ItemStack adminblockGUIOption = guiOptions.get("adminblock").getItemStack();
                    if (adminblockGUIOption == null) {
                        adminblockGUIOption = new ItemStack(Material.END_CRYSTAL, 1);
                        ItemMeta teleportMeta = adminblockGUIOption.getItemMeta();
                        teleportMeta.setDisplayName("Admin Block");
                        adminblockGUIOption.setItemMeta(teleportMeta);
                    }
                    GUI.setItem(13, adminblockGUIOption);
                }

                if (domainBlockManager.hasActiveFlag(pb, Flag.Type.RUN_COMMAND)) {

                    ItemStack onEnterGUIOption = guiOptions.get("command_on_enter").getItemStack();
                    List<String> enterLore = new ArrayList<String>();
                    if (onEnterGUIOption == null) {
                        onEnterGUIOption = new ItemStack(Material.REPEATING_COMMAND_BLOCK, 1);
                        ItemMeta onEnterMeta = onEnterGUIOption.getItemMeta();
                        onEnterMeta.setDisplayName("Run Command On Enter");
                        onEnterGUIOption.setItemMeta(onEnterMeta);
                    }
                    if (pb.getEnterCommands() != null) {
                        ItemMeta onEnterMeta = onEnterGUIOption.getItemMeta();
                        if (onEnterMeta != null) {
                            if (onEnterMeta.getLore() != null) {
                                enterLore = onEnterMeta.getLore();
                            }
                            for (String cmd : pb.getEnterCommands()) {
                                enterLore.add(ColorFunctions.colorToString("&7/" + cmd));
                            }
                            onEnterMeta.setLore(enterLore);
                            onEnterGUIOption.setItemMeta(onEnterMeta);
                        }

                    }
                    GUI.setItem(15, onEnterGUIOption);

                    ItemStack onExitGUIOption = guiOptions.get("command_on_leave").getItemStack();
                    List<String> exitLore = new ArrayList<String>();
                    if (onExitGUIOption == null) {
                        onExitGUIOption = new ItemStack(Material.REPEATING_COMMAND_BLOCK, 1);
                        ItemMeta onExitMeta = onExitGUIOption.getItemMeta();
                        onExitMeta.setDisplayName("Run Command On Leave");
                        onExitGUIOption.setItemMeta(onExitMeta);
                    }
                    if (pb.getLeaveCommands() != null) {
                        ItemMeta onExitMeta = onExitGUIOption.getItemMeta();
                        if (onExitMeta != null) {
                            if (onExitMeta.getLore() != null) {
                                exitLore = onExitMeta.getLore();
                            }
                        }
                        for (String cmd : pb.getLeaveCommands()) {
                            exitLore.add(ColorFunctions.colorToString("&7/" + cmd));
                        }
                    }
                    GUI.setItem(17, onExitGUIOption);

                } else {
                    ItemStack onEnterGUIOption = guiOptions.get("command_on_enter_disabled").getItemStack();
                    if (onEnterGUIOption == null) {
                        onEnterGUIOption = new ItemStack(Material.BARRIER, 1);
                        ItemMeta onEnterMeta = onEnterGUIOption.getItemMeta();
                        onEnterMeta.setDisplayName("Run Command on Enter - Disabled");
                        onEnterGUIOption.setItemMeta(onEnterMeta);
                    }
                    GUI.setItem(15, onEnterGUIOption);

                    ItemStack onExitGUIOption = guiOptions.get("command_on_leave_disabled").getItemStack();
                    if (onExitGUIOption == null) {
                        onExitGUIOption = new ItemStack(Material.BARRIER, 1);
                        ItemMeta onExitMeta = onExitGUIOption.getItemMeta();
                        onExitMeta.setDisplayName("Run Command on Leave - Disabled");
                        onExitGUIOption.setItemMeta(onExitMeta);
                    }
                    GUI.setItem(17, onExitGUIOption);
                }

                if (pb.getConfig() == null) {
                    instance.getError().save(
                            "BlockCommandsGUI.getInventory.001",
                            "BlockCommandsGUI",
                            "getInventory()",
                            "Unable to open menu",
                            "Unknown block type",
                            Error.Severity.URGENT,
                            null
                    );
                    instance.getMessage().send(guiPlayer, "gui_error", "&4ERROR &f- Unexpected error opening menu",null, true);
                    return null;
                }

                validGUI = true;
                return GUI;
            } else {
                Inventory GUI = Bukkit.createInventory(this, slotCount,
                        instance.getLocale().localeText("gui_invalid", "Invalid Domain Block", null));

                ItemStack itemStackError = guiOptions.get("error").getItemStack();
                for (int i = 0; i <= slotCount; i++) {
                    GUI.setItem(i, itemStackError);
                }

                return GUI;
            }
        } else {
            Inventory GUI = Bukkit.createInventory(this, slotCount,
                    instance.getLocale().localeText("gui_invalid", "Invalid Domain Block", null));

            ItemStack itemStackError = guiOptions.get("error").getItemStack();
            for (int i = 0; i <= slotCount; i++) {
                GUI.setItem(i, itemStackError);
            }

            return GUI;
        }

    }

    @Override
    public void onGUIClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            // HashMap<String, GUIOption> guiOptions =
            // Domain.instance().config().getGUIOptions();
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true); // cancel the event no matter what
            if (this.validGUI) {
                if (event.getClick() == ClickType.LEFT) {
                    DomainBlock domainBlock = domainBlockManager.getDomainBlockByID(this.placedID);
                    if (domainBlock != null) {
                        HashMap<String, String> parameters = new HashMap<String, String>();
                        parameters.put("<%blocktype%>", domainBlock.getConfig().getDisplayName());
                        switch (event.getRawSlot()) {
                            case 0:
                                BlockEditGUI editGUI = new BlockEditGUI(instance);
                                editGUI.setPlacedID(this.placedID);
                                editGUI.setPlayer(player);
                                player.openInventory(editGUI.getInventory());
                                SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                break;
                            case 9:
                                if (Permission.isAdmin(player)) {
                                    // Give player teleport wand
                                    ItemStack wand = Teleport.wand();

                                    boolean hasItemInHand = false;
                                    if (player.getInventory().getItemInMainHand() != null) {
                                        instance.getDebug().log("BlockCommandsGUI", "get Teleport Wand", "Item in hand: "
                                                + player.getInventory().getItemInMainHand().getType().name());
                                        if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                                            hasItemInHand = true;
                                        }
                                    }
                                    if (!hasItemInHand) {
                                        player.getInventory().setItemInMainHand(wand);
                                        instance.getMessage().send(player,
                                                "teleport_wand_given",
                                                "&aYou have been given a teleport wand.  Left click the block you wish to teleport to with the wand.  When selected, left click the Domain block with the wand to set the location",
                                                null, true);
                                    } else {
                                        instance.getMessage().send(player, "teleport_wand_not_given",
                                                "&cFAILED &f- Your main hand must be empty", null, true);
                                    }

                                    Domain.getInstance().getData().openBlocks.put(
                                            player.getUniqueId(),
                                            new Teleport(domainBlock.getPlacedId())
                                    );
                                    OpenDomainBlock openDomainBlock = new OpenDomainBlock(domainBlock.getPlacedId(), OpenDomainBlockType.NA);
                                    openDomainBlock.openEffectsWithoutVisualization(player, domainBlock);
                                    player.closeInventory();
                                } else {
                                    instance.getMessage().send(player, "gui_invalid_perm",
                                            "&4You do not have permission to access this command", null, true);
                                }
                                break;
                            case 11:
                                if (Permission.isAdmin(player)) {
                                    // Give player magnet wand
                                    ItemStack wand = Magnet.wand();

                                    boolean hasItemInHand = false;
                                    if (player.getInventory().getItemInMainHand() != null) {
                                        instance.getDebug().log("BlockCommandsGUI", "get Magnet Wand", "Item in hand: "
                                                + player.getInventory().getItemInMainHand().getType().name());
                                        if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                                            hasItemInHand = true;
                                        }
                                    }
                                    if (!hasItemInHand) {
                                        player.getInventory().setItemInMainHand(wand);
                                        instance.getMessage().send(player, "magnet_wand_given",
                                                "&aYou have been given a magnet wand.  Left click the block you wish to set as the magnet.  When selected, left click the Domain block with the wand to set the location",
                                                null, true);
                                    } else {
                                        instance.getMessage().send(player, "magnet_wand_not_given",
                                                "&cFAILED &f- Your main hand must be empty", null, true);
                                    }

                                    Domain.getInstance().getData().openBlocks.put(
                                            player.getUniqueId(),
                                            new Magnet(domainBlock.getPlacedId())
                                    );
                                    OpenDomainBlock openDomainBlock = new OpenDomainBlock(domainBlock.getPlacedId(), OpenDomainBlockType.NA);
                                    openDomainBlock.openEffectsWithoutVisualization(player, domainBlock);
                                    player.closeInventory();
                                } else {
                                    instance.getMessage().send(player, "gui_invalid_perm",
                                            "&4You do not have permission to access this command", null, true);
                                }
                                break;
                            case 13:
                                if (Permission.isAdmin(player)) {
                                    if (!domainBlock.isAdminBlock()) {
                                        // not an admin block, make it one
                                        domainBlock.setAdminBlock(true);
                                        domainBlock.setDirty(true);
                                        domainBlockManager.domainBlockSave(domainBlock);
                                        SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                        player.closeInventory();
                                        instance.getMessage().send(
                                                player,
                                                "command_adminblock_success",
                                                "&2SUCCESS - Block has been converted to an Admin block",
                                                null, true
                                        );
                                    } else {
                                        // is already an admin block, convert back
                                        domainBlock.setAdminBlock(false);
                                        domainBlock.setOwner(player.getUniqueId());
                                        domainBlock.setDirty(true);
                                        domainBlockManager.domainBlockSave(domainBlock);
                                        SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                        player.closeInventory();
                                        instance.getMessage().send(
                                                player,
                                                "command_adminblock_back_success",
                                                "&2SUCCESS - Block has been converted from an Admin block",
                                                null, true
                                        );
                                    }
                                } else {
                                    instance.getMessage().send(player, "gui_invalid_perm","&4You do not have permission to access this command", null, true);
                                }
                                break;
                            case 15:
                                // run command - enter
                                if (Permission.isAdmin(player)) {
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                    Domain.getInstance().getData().openDomainBlockPut(domainBlock.getPlacedId(),
                                            player.getUniqueId(), OpenDomainBlockType.COMMAND_ENTER);
                                    OpenDomainBlock openDomainBlock = new OpenDomainBlock(domainBlock.getPlacedId(), OpenDomainBlockType.COMMAND_ENTER);
                                    openDomainBlock.openEffects(player, domainBlock);
                                    instance.getMessage().send(player, "gui_command_onenter_enter",
                                            "&eEnter your command in the chat - The command will not be displayed to any other player but will be saved to the field.  Do not include / in the command.  Example: &5mvtp @p world_nether",
                                            null, true);
                                } else {
                                    instance.getMessage().send(player, "gui_invalid_perm","&4You do not have permission to access this command", null, true);
                                }
                                break;
                            case 17:
                                // run command - leave
                                if (Permission.isAdmin(player)) {
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                    Domain.getInstance().getData().openDomainBlockPut(domainBlock.getPlacedId(),
                                            player.getUniqueId(), OpenDomainBlockType.COMMAND_LEAVE);
                                    OpenDomainBlock openDomainBlock = new OpenDomainBlock(domainBlock.getPlacedId(), OpenDomainBlockType.COMMAND_LEAVE);
                                    openDomainBlock.openEffects(player, domainBlock);
                                    instance.getMessage().send(player,"gui_command_onleave_enter",
                                            "&eEnter your command in the chat - The command will not be displayed to any other player but will be saved to the field.  Do not include / in the command.  Example: &5mvtp @p world_nether",
                                            null, true);
                                } else {
                                    instance.getMessage().send(player, "gui_invalid_perm","&4You do not have permission to access this command", null, true);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            } else {
                instance.getMessage().send(player, "gui_block_fail", "Unable to process action", null, true);
            }
        } else {
            instance.getLog().log("BlockCommandsGUI", "onGUIClick(InventoryClickEvent)",
                    "Inventory click event captured for non-player");
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