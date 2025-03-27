package com.ibexmc.domain.gui;

import java.util.*;
import java.util.Map.Entry;

import com.ibexmc.domain.domainblock.config.IFieldConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.Domain.IGUI;
import com.ibexmc.domain.util.functions.PlayerFunctions;

public class DomainBlockGiveGUI implements IGUI{

    private Domain instance;

    private boolean validGUI = false;
    private int pageNum = 1;

    public DomainBlockGiveGUI(Domain instance) {
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
        return "DomainBlockGiveGUI";
    }
    private Player guiPlayer;
    @Override
    public void setPlayer(Player player) {
        this.guiPlayer = player;
    }
    @Override
    public Inventory getInventory() {
        instance.getDebug().log("BlockGUI", "getInventory()", "key = " + key);
        if (guiPlayer != null) {
            instance.getDebug().log("BlockGUI", "getInventory()", "Player = " + guiPlayer.getDisplayName());
        }
        instance.getDebug().log("BlockGUI", "getInventory()", "placedID = " + Integer.toString(placedID));

        int slots = 27;
        Set<IFieldConfig> lstConfig = instance.getData().getDomainFieldConfigs();
        if (lstConfig != null)
        {
            if (lstConfig.size() > 9  && lstConfig.size() <= 18)
            {
                slots = 27;
            }
            if (lstConfig.size() > 18 && lstConfig.size() <= 27)
            {
                slots = 27;
            }
            if (lstConfig.size() > 27 && lstConfig.size() <= 36)
            {
                slots = 36;
            }
            if (lstConfig.size() > 36 && lstConfig.size() <= 45)
            {
                slots = 45;
            }
            if (lstConfig.size() > 45 && lstConfig.size() <= 54)
            {
                slots = 54;
            }


            slotCount = slots;
            instance.getDebug().log("BlockGUI", "getInventory()", "Slot count: " + Integer.toString(slotCount));
            Inventory GUI = Bukkit.createInventory(this, slotCount, Domain.loggingName);

            if (instance.getData().getAvailableBlocks() != null)
            {
                int i = 0;

                TreeMap<String, ItemStack> sorted = new TreeMap<>();
                sorted.putAll(instance.getData().getAvailableBlocks());

                for (Entry<String, ItemStack> domainBlock : sorted.entrySet()) {
                    GUI.setItem(i, domainBlock.getValue());
                    i++;
                }
            }
            validGUI = true;
            return GUI;
        } else {
            slotCount = 9;
            Inventory GUI = Bukkit.createInventory(this, 9, instance.getLocale().localeText("gui_give_no_block", "No Domain Blocks Found", null));
            ItemStack itemStackNA = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
            ItemMeta itemMetaNA = itemStackNA.getItemMeta();
            if (itemMetaNA != null) {
                itemMetaNA.setDisplayName(" ");
                itemMetaNA.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                itemStackNA.setItemMeta(itemMetaNA);
            }

            for(int i=0; i<=9; i++){
                GUI.setItem(i, itemStackNA);
            }

            return GUI;
        }
    }

    @Override
    public void onGUIClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            Player recipient = this.guiPlayer;
            if (recipient == null) {
                recipient = player;
            }
            event.setCancelled(true); // cancel the event no matter what
            if (this.validGUI) {
                try {
                    if (event.getClick() == ClickType.LEFT) {
                        int firstEmptySlot = PlayerFunctions.firstEmptySlot(recipient.getUniqueId());
                        if (firstEmptySlot >= 0) {
                            if (event.getInventory() != null) {
                                ItemStack itemStack = event.getInventory().getItem(event.getRawSlot());

                                recipient.getInventory().setItem(firstEmptySlot, itemStack);
                                Map<String, String> placeHolder = new HashMap<>();
                                if (itemStack != null) {
                                    ItemMeta imeta = itemStack.getItemMeta();

                                    if (imeta != null) {
                                        placeHolder.put("<%block%>", itemStack.getItemMeta().getDisplayName() + "&r");
                                    } else {
                                        placeHolder.put("<%block%>", "&r");
                                    }

                                } else {
                                    return;
                                }
                                instance.getMessage().send(recipient, "gui_give_success_self", "<%block%> has been placed in your inventory", placeHolder, true);
                                if (!player.getUniqueId().equals(recipient.getUniqueId())) {
                                    placeHolder.put("<%player%>", recipient.getName());
                                    instance.getMessage().send(player, "gui_give_success_recipient", "<%player%> has received 1 <%block%>", placeHolder, true);
                                }
                                if (event.getRawSlot() <= slotCount) {
                                    player.closeInventory();
                                }
                            } else {
                                return;
                            }
                        } else {
                            instance.getMessage().send(player, "gui_give_empty_slot", "You must have an empty inventory slot" , null, true);
                        }
                    }
                } catch (Exception ex) {
                    return;
                }
            }
        }
    }
    @Override
    public void setKey(String key) {
        this.key = key;
    }
    @Override
    public void setPlacedID(int placedID) {
        this.placedID = -1;
    }
    @Override
    public int getPlacedID() {
        return -1;
    }
}