package com.ibexmc.domain.gui;

import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.BlockStore;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.domainblock.config.IFieldConfig;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import com.ibexmc.internal.functions.ColorFunctions;
import com.ibexmc.internal.functions.SoundFunctions;
import com.ibexmc.internal.messaging.Error;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class BlockStoreGUI implements Domain.IGUI {
    private Domain instance;
    private DomainBlockManager domainBlockManager;
    private boolean validGUI = false;
    private int pageNum = 1;

    public BlockStoreGUI(Domain instance) {
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

    private HashMap<Integer, BlockStore> store;

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
        instance.getDebug().log("BlockGUI", "getInventory()", "key = " + key);
        if (guiPlayer != null) {
            instance.getDebug().log("BlockGUI", "getInventory()", "Player = " + guiPlayer.getDisplayName());
        }
        HashMap<String, ItemStack> availableBlocks = Domain.getInstance().getData().getAvailableBlocks();
        if (availableBlocks != null) {

            int position = 0;

            String pageCount = "";
            if (availableBlocks.size() >= 45) {
                if (availableBlocks.size() >= 45 && availableBlocks.size() < 90) {
                    pageCount = " " + Integer.toString(this.pageNum) + "/2";
                }
                if (availableBlocks.size() >= 90) {
                    pageCount = " " + Integer.toString(this.pageNum) + "/3";
                }
            }

            Inventory GUI = Bukkit.createInventory(this, slotCount, instance.getLocale().localeText("gui_store_title", "&1Domain Store", null) + pageCount);

            ItemStack itemStackNA = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
            ItemMeta itemMetaNA = itemStackNA.getItemMeta();
            if (itemMetaNA != null) {
                itemMetaNA.setDisplayName(" ");
                itemMetaNA.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                itemStackNA.setItemMeta(itemMetaNA);
            }

            ItemStack itemStackBack = new ItemStack(Material.NETHER_STAR, 1);
            ItemMeta itemMetaBack = itemStackBack.getItemMeta();
            if (itemMetaBack != null) {
                itemMetaBack.setDisplayName(ChatColor.stripColor(instance.getLocale().localeText("gui_back", "Back", null)));
                itemMetaBack.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                itemStackBack.setItemMeta(itemMetaBack);
            }

            store = new HashMap<Integer, BlockStore>();
            Map<String, IFieldConfig> availableConfigs = Domain.getInstance().getData().getDomainFieldConfigMap();

            if (availableConfigs != null) {
                for (Entry<String, ItemStack> domainBlock : availableBlocks.entrySet()) {
                    ItemStack domainBlockItem = domainBlock.getValue().clone();
                    domainBlockItem.setAmount(1);
                    if (domainBlockItem != null) {
                        if (availableConfigs.containsKey(domainBlock.getKey())) {
                            IFieldConfig blueprintConfig = availableConfigs.get(domainBlock.getKey());
                            if (blueprintConfig != null) {
                                if (blueprintConfig.getCostToBuy() >= 0) {

                                    ItemStack origBlock = domainBlockItem.clone();
                                    ItemStack sellBlock = domainBlockItem.clone();

                                    BlockStore blockStore = new BlockStore(origBlock, blueprintConfig.getDisplayName(), blueprintConfig.getCostToBuy());
                                    store.put(position, blockStore);

                                    ItemMeta domainBlockMeta = sellBlock.getItemMeta();
                                    if (domainBlockMeta != null) {
                                        List<String> lore = blueprintConfig.getItemLore();
                                        if (lore == null) {
                                            lore = new ArrayList<String>();
                                        }
                                        List<String> storeLore = new ArrayList<String>();
                                        for (String loreLine : lore) {
                                            storeLore.add(ColorFunctions.colorToString(loreLine));
                                        }
                                        HashMap<String, String> placeHolderCost = new HashMap<String, String>();
                                        placeHolderCost.put("<%cost%>", Integer.toString(blueprintConfig.getCostToBuy()));
                                        storeLore.add(instance.getLocale().localeText("gui_store_buy_lore", "&2Buy price: $<%cost%>", placeHolderCost));
                                        domainBlockMeta.setLore(storeLore);
                                        sellBlock.setItemMeta(domainBlockMeta);
                                        GUI.setItem(position, sellBlock);



                                        position++;
                                    }
                                }
                            }
                        }
                    }

                }
            }

            validGUI = true;
            return GUI;
        } else {
            Inventory GUI = Bukkit.createInventory(this, slotCount, instance.getLocale().localeText("gui_invalid", "Invalid Domain Block", null));
            ItemStack itemStackNA = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
            ItemMeta itemMetaNA = itemStackNA.getItemMeta();
            if (itemMetaNA != null) {
                itemMetaNA.setDisplayName(" ");
                itemMetaNA.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                itemStackNA.setItemMeta(itemMetaNA);
            }

            for(int i=0; i<=slotCount; i++){
                GUI.setItem(i, itemStackNA);
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
                    switch (event.getRawSlot()) {
                        case 51:
                            if (!event.getCurrentItem().getType().equals(Material.GRAY_CONCRETE)) {
                                BlockStoreGUI flagBackGUI = new BlockStoreGUI(instance);
                                flagBackGUI.setPlacedID(this.placedID);
                                flagBackGUI.setPageNum(this.pageNum - 1);
                                SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                player.openInventory(flagBackGUI.getInventory());
                            }
                            break;
                        case 53:
                            if (!event.getCurrentItem().getType().equals(Material.GRAY_CONCRETE)) {
                                BlockStoreGUI flagNextGUI = new BlockStoreGUI(instance);
                                flagNextGUI.setPlacedID(this.placedID);
                                flagNextGUI.setPageNum(this.pageNum + 1);
                                SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                                player.openInventory(flagNextGUI.getInventory());
                            }
                            break;
                        default:
                            if (event.getRawSlot() < 45) {
                                BlockStore blockStore = store.get(event.getRawSlot());
                                if (blockStore != null) {
                                    int firstEmptySlot = PlayerFunctions.firstEmptySlot(player.getUniqueId());
                                    if (firstEmptySlot >= 0) {
                                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
                                        if (offlinePlayer != null) {
                                            Economy economy = Domain.getInstance().getDependencies().getEconomy();
                                            double playerBalance = economy.getBalance(offlinePlayer);
                                            DecimalFormat formatter = new DecimalFormat("#0.00");
                                            if (playerBalance >= blockStore.getCost()) {
                                                EconomyResponse r = economy.withdrawPlayer(offlinePlayer, blockStore.getCost());
                                                if (r.transactionSuccess()) {

                                                    player.getInventory().setItem(firstEmptySlot, blockStore.getBlock());

                                                    Map<String, String> placeHolder = new HashMap<>();
                                                    placeHolder.put("<%cost%>", Integer.toString(blockStore.getCost()));
                                                    placeHolder.put("<%newbalance%>", formatter.format(economy.getBalance(offlinePlayer)));
                                                    placeHolder.put("<%blocktype%>", blockStore.getBlockType());
                                                    instance.getMessage().send(player, "buy_complete", "&2SUCCESS &f- You have purchased a <%blocktype%> block for &4$<%cost%>", placeHolder, true);
                                                    instance.getMessage().send(player, "buy_complete_newbal", "&2New balance: <%newbalance%>", placeHolder, true);

                                                    player.closeInventory();
                                                } else {
                                                    // error
                                                    instance.getMessage().send(player, "buy_error", "&4ERROR &f- Unexpected error purchasing block", null, true);
                                                    instance.getError().save(
                                                            "001",
                                                            "BlockStoreGUI",
                                                            "onGUIClick",
                                                            "Unexpected Error",
                                                            r.errorMessage,
                                                            Error.Severity.WARN,
                                                            null
                                                    );
                                                }
                                            } else {
                                                // can't afford that
                                                Map<String, String> placeHolder = new HashMap<>();
                                                placeHolder.put("<%cost%>", Integer.toString(blockStore.getCost()));
                                                placeHolder.put("<%blocktype%>", blockStore.getBlockType());
                                                placeHolder.put("<%balance%>", formatter.format(playerBalance));
                                                instance.getMessage().send(player, "buy_no_funds", "&4Insufficient funds for <%blocktype%> block.  Cost: $<%cost%> Balance $<%balance%>", placeHolder, true);
                                            }
                                        }
                                    } else {
                                        instance.getMessage().send(player, "buy_no_slot", "&4You must have a free slot in your inventory to purchase a Domain block", null, true);
                                    }
                                }
                            }
                            break;
                    }
                }
            } else {
                instance.getMessage().send(player, "gui_block_fail", "Unable to process action" , null, true);
            }
        } else {
            instance.getError().save(
                    "002",
                    "BlockStoreGUI",
                    "onGUIClick",
                    "Unexpected Error",
                     "Inventory click event captured for non-player",
                    Error.Severity.WARN,
                    null
            );
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