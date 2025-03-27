package com.ibexmc.domain.gui;

import java.util.HashMap;
import java.util.Map;

import com.ibexmc.domain.util.DomainParticles;
import com.ibexmc.internal.functions.InventoryFunctions;
import com.ibexmc.internal.functions.SoundFunctions;
import com.ibexmc.internal.functions.StringFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.Domain.IGUI;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.domainblock.vouchers.Voucher;
import com.ibexmc.domain.domainblock.Visualize;

public class DomainBlockTakeConfirmGUI implements IGUI{

    private Domain instance;
    private HashMap<String, GUIOption> guiOptions = new HashMap<String, GUIOption>();
    private boolean validGUI = false;
    private int pageNum = 1;

    public DomainBlockTakeConfirmGUI(Domain instance) {
        this.instance = instance;
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

    private int slotCount = 9;

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
        return "DomainBlockTakeConfirmGUI";
    }
    private Player guiPlayer;
    @Override
    public void setPlayer(Player player) {
        this.guiPlayer = player;
    }

    private void loadOptions() {
        // Getting our GUI Options
        guiOptions.put("na", Domain.getInstance().getData().getGUIOption("na", Material.BLACK_STAINED_GLASS_PANE, "&0.", "", true));
        guiOptions.put("disabled", Domain.getInstance().getData().getGUIOption("disabled", Material.BLACK_STAINED_GLASS_PANE, "", "&fThis option is not available", true));
        guiOptions.put("back", Domain.getInstance().getData().getGUIOption("back", Material.NETHER_STAR, "&bBack", "&fReturn to previous page", true));
        guiOptions.put("error", Domain.getInstance().getData().getGUIOption("error", Material.RED_STAINED_GLASS_PANE, "", "", true));
        guiOptions.put("take_yes", Domain.getInstance().getData().getGUIOption("take_yes", Material.GREEN_STAINED_GLASS_PANE, "&2Yes", "&fTakes the Domain block, removing the field", true));
        guiOptions.put("take_no", Domain.getInstance().getData().getGUIOption("take_no", Material.RED_STAINED_GLASS_PANE, "&4No", "&fCancels taking the Domain block", true));
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
    public Inventory getInventory() {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        instance.getDebug().log("BlockGUI", "getInventory()", "key = " + key);

        loadOptions();

        if (guiPlayer != null) {
            instance.getDebug().log("BlockGUI", "getInventory()", "Player = " + guiPlayer.getDisplayName());
        }
        if (placedID >= 0) {
            DomainBlock pb = domainBlockManager.getDomainBlockByID(placedID);
            if (pb != null) {
                Inventory GUI = Bukkit.createInventory(this, 9, instance.getLocale().localeText("gui_sure_text", "Are you sure?", null));

                GUI.setItem(3, getOption("take_no").getItemStack());
                GUI.setItem(5, getOption("take_yes").getItemStack());


                ItemStack itemStackNA = getOption("na").getItemStack();
                GUI.setItem(0, itemStackNA);
                GUI.setItem(1, itemStackNA);
                GUI.setItem(2, itemStackNA);
                GUI.setItem(4, itemStackNA);
                GUI.setItem(6, itemStackNA);
                GUI.setItem(7, itemStackNA);
                GUI.setItem(8, itemStackNA);

                validGUI = true;
                return GUI;
            } else {
                Inventory GUI = Bukkit.createInventory(this, 9, instance.getLocale().localeText("gui_invalid", "Invalid Domain Block", null));
                ItemStack itemStackError = getOption("error").getItemStack();
                for(int i=0; i<=8; i++){
                    GUI.setItem(i, itemStackError);
                }

                validGUI = true;
                return GUI;
            }
        } else {
            Inventory GUI = Bukkit.createInventory(this, 9, instance.getLocale().localeText("gui_invalid", "Invalid Domain Block", null));
            ItemStack itemStackError = getOption("error").getItemStack();
            for(int i=0; i<=8; i++){
                GUI.setItem(i, itemStackError);
            }

            return GUI;
        }

    }

    @Override
    public void onGUIClick(InventoryClickEvent event) {
        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true); // cancel the event no matter what
            if (this.validGUI) {
                if (event.getClick() == ClickType.LEFT) {
                    DomainBlock domainBlock = domainBlockManager.getDomainBlockByID(this.placedID);
                    Location domainBlockLocation = domainBlock.getBlockLocation().clone();
                    if (domainBlock != null) {
                        switch (event.getRawSlot()) {
                            case 3:
                                instance.getMessage().send(player, "gui_take_cancel", "Domain block Take cancelled", null, true);
                                DomainParticles.denyBlock(domainBlockLocation);
                                SoundFunctions.playSoundWorld(domainBlockLocation, 0.2f, 1.0f, Sound.ENTITY_GENERIC_EXTINGUISH_FIRE);
                                break;
                            case 5:
                                if (domainBlock != null) {
                                    if (domainBlockManager.isOwner(player, domainBlock)) {
                                        Block block = domainBlock.getBlockLocation().getBlock();
                                        for (String voucherKey : domainBlock.getVouchers()) {
                                            Voucher voucher = Domain.getInstance().getData().vouchers.get(voucherKey);
                                            if (voucher != null) {
                                                if (!voucher.singleUse()) {
                                                    ItemStack voucherItem = voucher.voucher();
                                                    InventoryFunctions.addItemsToPlayerInventory(
                                                            player,
                                                            voucherItem,
                                                            true
                                                    );
                                                } else {
                                                    Map<String, String> ph = new HashMap<>();
                                                    ph.put("<%voucher%>", voucher.name());
                                                    instance.getMessage().send(player, "voucher_singleuse",
                                                            "&4You have not received your <%voucher%> back as it was single-use only", ph, true);
                                                }
                                            }
                                        }
                                        if (domainBlock.getConfig().isOneTimeUse()) {
                                            domainBlockManager.removeField(domainBlock.getPlacedId());
                                        } else {
                                            ItemStack itemStack = domainBlock.getConfig().getItem().clone();
                                            itemStack.setAmount(domainBlock.getMergedCount() + 1);
                                            domainBlockManager.removeField(domainBlock.getPlacedId());
                                            InventoryFunctions.addItemsToPlayerInventory(
                                                    player,
                                                    itemStack,
                                                    true
                                            );
                                            BlockState bs = block.getState();
                                            bs.setType(Material.AIR);
                                            bs.update(true);
                                        }
                                        //BlockCommands.removeBlock(player, DomainBlockManager.blockFromDomainBlock(domainBlock));
                                        DomainParticles.takeEffect(domainBlockLocation);
                                        SoundFunctions.playSoundWorld(domainBlockLocation, 0.2f, 1.0f, Sound.BLOCK_CONDUIT_ACTIVATE);
                                        if (!domainBlock.getConfig().isOneTimeUse()) {
                                            instance.getMessage().send(player, "gui_take_success", "You have taken this Domain block", null, true);
                                        }
                                        Visualize visualize = new Visualize(instance);
                                        visualize.clearVisualization(player);
                                        player.closeInventory();
                                    } else {
                                        instance.getMessage().send(player, "gui_take_no_perm", "You do not have permission to take this field", null, true);
                                        DomainParticles.denyBlock(domainBlockLocation);
                                        SoundFunctions.playSoundWorld(domainBlockLocation, 0.2f, 1.0f, Sound.ENTITY_GENERIC_EXTINGUISH_FIRE);
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
                Domain.getInstance().getData().removeOpenConfirmTakeByKey(player.getUniqueId());
                player.closeInventory();
            }
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