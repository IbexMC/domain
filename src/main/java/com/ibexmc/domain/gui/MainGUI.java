package com.ibexmc.domain.gui;

import com.ibexmc.internal.functions.SoundFunctions;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.domainblock.Visualize;

import com.ibexmc.domain.util.functions.PlayerFunctions;

import java.util.HashMap;


public class MainGUI implements IInventoryGUI {

    //region Objects
    private final Domain instance;
    private final DomainBlock domainBlock;
    private final Player player;
    private Visualize visualize;
    private DomainBlockManager domainBlockManager;

    //endregion
    //region Constructors
    public MainGUI(Domain instance, DomainBlock domainBlock, Player player) {
        this.instance = instance;
        this.domainBlock = domainBlock;
        this.player = player;
        this.visualize = new Visualize(instance);
        this.domainBlockManager = new DomainBlockManager(instance);
    }
    //endregion
    //region Events
    @Override
    /**
     * Gets the inventory being used in this GUI
     */
    public @NotNull Inventory getInventory() {
        HashMap<String, String> placeHolder = new HashMap<>();
        placeHolder.put("<%blocktype%>", ChatColor.stripColor(domainBlock.getFieldName()));
        String title = instance.getLocale().localeText("gui_main_title", "<%blocktype%> Field", placeHolder);
        if (domainBlock.isAdminBlock()) {
            title = instance.getLocale().localeText("gui_main_title_admin", "ADMIN - <%blocktype%>", placeHolder);
        }
        Inventory inventory = Bukkit.createInventory(
                this,
                27,
                title
        );
        for (int i = 0; i < 27; ++i) {
            switch (i) {
                case 10:
                    inventory.setItem(i, GUIHelper.get("display_field"));
                    break;
                case 12:
                    inventory.setItem(i, GUIHelper.get("edit_field"));
                    break;
                case 14:
                    inventory.setItem(i, GUIHelper.get("info"));
                    break;
                case 16:
                    inventory.setItem(i, GUIHelper.get("permission"));
                    break;
                default:
                    inventory.setItem(i, GUIHelper.get("na"));
                    break;

            }
        }
        return inventory;
    }

    /**
     * Called from InventoryClick for any clicks in an inventory window of this type
     *
     * @param event InventoryClickEvent to process
     */
    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        boolean hasBypass = PlayerFunctions.hasBypass(player);
        switch (event.getRawSlot()) {
            //region Display
            case 10:
                boolean displayEnabled = true;
                GUIOption displayOption = Domain.getInstance().getData().getGUIOption("display_field", Material.BEACON, "&bDisplay Field", "&fVisualize the field", true);
                if (displayOption != null) {
                    displayEnabled = displayOption.isEnabled();
                }
                if (displayEnabled || hasBypass) {
                    visualize.clearVisualization(player); // Clear existing visualizations (including the Domain block mask)
                    visualize.maskBlock(domainBlock.getPlacedId(), player, Material.BEACON);
                    visualize.displayField(
                            player,
                            domainBlock,
                            0,
                            true,
                            false,
                            true
                    );
                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                    player.closeInventory();
                } else {
                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                }
                break;
            //endregion
            //region Edit Field
            case 12:
                boolean editEnabled = true;
                GUIOption editOption = Domain.getInstance().getData().getGUIOption("edit_field", Material.ENCHANTING_TABLE, "&bEdit Field", "&fAccess the Domain Edit Menu", true);
                if (editOption != null) {
                    editEnabled = editOption.isEnabled();
                }
                if (editEnabled || hasBypass) {
                    BlockEditGUI editGUI = new BlockEditGUI(instance);
                    editGUI.setPlacedID(this.domainBlock.getPlacedId());
                    editGUI.setPlayer(player);
                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                    player.openInventory(editGUI.getInventory());
                } else {
                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                }
                break;
            //endregion
            //region Field Information
            case 14:
                boolean infoEnabled = true;
                GUIOption infoOption = Domain.getInstance().getData().getGUIOption("info", Material.KNOWLEDGE_BOOK, "&bField Information", "&fOpens the Domain Information Menu", true);
                if (infoOption != null) {
                    infoEnabled = infoOption.isEnabled();
                }
                if (infoEnabled || hasBypass) {
                    InfoGUI infoGUI = new InfoGUI(instance, domainBlockManager.getDomainBlockById(this.domainBlock.getPlacedId()), player);
                    player.openInventory(infoGUI.getInventory());
                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                } else {
                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                }
                break;
            //endregion
            //region Permissions
            case 16:
                boolean permEnabled = true;
                GUIOption permOption = Domain.getInstance().getData().getGUIOption("permission", Material.PLAYER_HEAD, "&bPermissions", "&fOpens the Domain Permissions menu|&eMay take a few moments to open", true);
                if (permOption != null) {
                    permEnabled = permOption.isEnabled();
                }
                if (permEnabled || hasBypass) {
                    BlockPermsGUI permsGUI = new BlockPermsGUI(instance);
                    permsGUI.setPlacedID(this.domainBlock.getPlacedId());
                    permsGUI.setPlayer(player);
                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                    player.openInventory(permsGUI.getInventory());
                } else {
                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                }
                break;
            //endregion
            default:
                instance.getDebug().log(
                        "InfoGUI",
                        "onClick",
                        "Clicked N/A"
                );
                GUIHelper.soundError(player);
                break;
        }
    }

    /**
     * Used to handle inventory closing
     *
     * @param event InventoryCloseEvent to process
     */
    @Override
    public void onClose(InventoryCloseEvent event) {
        instance.getDebug().log(
                "MainGUI",
                "onClose",
                "Closing Inventory"
        );
    }

    /**
     * Returns the DomainBlock being used in this menu
     *
     * @return DomainBlock
     */
    public DomainBlock domainBlock() {
        return this.domainBlock;
    }


//endregion
}
