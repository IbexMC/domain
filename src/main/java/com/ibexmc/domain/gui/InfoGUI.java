package com.ibexmc.domain.gui;

import com.ibexmc.domain.helper.BlockCommands;
import com.ibexmc.internal.functions.SoundFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

import com.ibexmc.domain.util.functions.PlayerFunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InfoGUI implements IInventoryGUI {

    private Domain instance;

    //region Objects
    private final DomainBlock domainBlock;
    private final Player player;
    //endregion
    //region Constructors
    public InfoGUI(Domain instance, DomainBlock domainBlock, Player player) {
        this.instance = instance;
        this.domainBlock = domainBlock;
        this.player = player;
    }
    //endregion
    //region Events
    @Override
    /**
     * Gets the inventory being used in this GUI
     */
    public @NotNull Inventory getInventory() {
        
        Domain instance = Domain.getInstance();
        
        Inventory inventory = Bukkit.createInventory(
                this,
                27,
                instance.getLocale().localeText("gui_info_title", "Info", null)
        );
        for(int i = 0; i < 27; ++i) {
            switch (i) {
                case 0:
                    inventory.setItem(i, GUIHelper.get("back"));
                    break;
                case 10:
                    inventory.setItem(i, GUIHelper.get("info_info"));
                    break;
                case 12:
                    inventory.setItem(i, GUIHelper.get("info_who"));
                    break;
                case 14:
                    inventory.setItem(i, GUIHelper.get("info_flags"));
                    break;
                case 16:
                    inventory.setItem(i, GUIHelper.get("info_rent"));
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
     * @param event InventoryClickEvent to process
     */
    @Override
    public void onClick(InventoryClickEvent event) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        event.setCancelled(true);
        boolean hasBypass = PlayerFunctions.hasBypass(player);
        switch (event.getRawSlot()) {
            //region Back
            case 0:
                // Back
                MainGUI mainGUI = new MainGUI(
                        instance,
                        domainBlock,
                        player
                );
                player.openInventory(mainGUI.getInventory());
                break;
            //endregion
            //region Info
            case 10:
                boolean infoEnabled = false;
                GUIOption infoOption = Domain.getInstance().getData().getGUIOption("info_info", Material.PAPER, "&bField Information", "&fDisplays information about your field", true);
                if (infoOption != null) {
                    infoEnabled = infoOption.isEnabled();
                }
                if (infoEnabled || hasBypass) {
                    BlockCommands blockCommands = new BlockCommands(instance);
                    blockCommands.getBlockInfo(player, this.domainBlock.getBlockLocation().getBlock());
                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                    player.closeInventory();
                } else {
                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                }
                break;
            //endregion
            //region Who
            case 12:
                boolean whoEnabled = false;
                GUIOption whoOption = Domain.getInstance().getData().getGUIOption("info_who", Material.ENDER_EYE, "&bWho else is in field?", "&fDisplays a list of other players in your field", true);
                if (whoOption != null) {
                    whoEnabled = whoOption.isEnabled();
                }
                if (whoEnabled || hasBypass) {
                    List<DomainBlock.InField> inFields = domainBlock.isInField();
                    List<DomainBlock.InField> visibleInFields = new ArrayList<>();
                    for (DomainBlock.InField inField : inFields) {
                        if (player.getUniqueId().equals(inField.getPlayer().getUniqueId())) {
                            // Do not include the player calling this command
                            continue;
                        }
                        if (inField.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
                            // Do not show players in spectator mode
                            continue;
                        }
                        if (!player.canSee(inField.getPlayer())) {
                            // If the player can't see the player in their field for any reason, do not show them
                            continue;
                        }
                        if (PlayerFunctions.hasBypass(inField.getPlayer())) {
                            // Do not include players in bypass mode
                            continue;
                        }
                        visibleInFields.add(inField);
                    }

                    if (visibleInFields.size() > 0) {

                        TextComponent msg = new TextComponent(instance.getLocale().localeText("who_in_title", "&lPlayers in Field: ", null));

                        for (DomainBlock.InField visibleInField : visibleInFields) {
                            HashMap<String, String> placeHolder = new HashMap<>();
                            placeHolder.put("<%player%>", visibleInField.getPlayer().getDisplayName());
                            placeHolder.put("<%color%>", "&c");
                            if (visibleInField.isAllowed()) {
                                placeHolder.put("<%color%>", "&a");
                            }
                            TextComponent playerMsg = new TextComponent(
                                    instance.getLocale().localeText("who_in", "<%color%><%player%>", placeHolder)
                            );
                            HashMap<String, String> kickMap = new HashMap<>();
                            kickMap.put("<%kickmessage%>", "");
                            kickMap.put("<%distance%>", "" + (int) domainBlock.getBlockLocation().distance(visibleInField.getPlayer().getLocation()));
                            kickMap.put("<%location%>", "" + WorldFunctions.simpleFancyReadable(visibleInField.getPlayer().getLocation()));



                            if (!visibleInField.isAllowed() && domainBlock.getConfig().isAllowWhoInKick()) {

                                kickMap.put("<%kickmessage%>", instance.getLocale().localeText("who_in_kickmessage", "\n&9Click here to kick the player from the field", null));


                                playerMsg.setClickEvent(
                                        new ClickEvent(
                                                ClickEvent.Action.RUN_COMMAND,
                                                "/domain who_in_kick " + domainBlock.getPlacedId() + " " + visibleInField.getPlayer().getUniqueId()
                                        )
                                );


                            }
                            playerMsg.setHoverEvent(
                                    new HoverEvent(
                                            HoverEvent.Action.SHOW_TEXT,
                                            new ComponentBuilder(
                                                    instance.getLocale().localeText(
                                                            "who_in_hover",
                                                            "&f<%location%> &f(<%distance%> blocks away)<%kickmessage%>",
                                                            kickMap
                                                    )
                                            ).create()
                                    )
                            );

                            msg.addExtra(playerMsg);
                            msg.addExtra(" ");
                            player.spigot().sendMessage(msg);
                        }
                    } else {
                        instance.getMessage().send(player, "gui_block_in_field_none", "No other players in this field", null, true);
                    }

                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                    player.closeInventory();
                } else {
                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                }
                break;
            //endregion
            //region Flags
            case 14:
                boolean flagsEnabled = false;
                GUIOption flagsOption = Domain.getInstance().getData().getGUIOption("info_flags", Material.YELLOW_BANNER, "&bFlags", "&fDisplays the flags currently assigned to this field", true);
                if (flagsOption != null) {
                    flagsEnabled = flagsOption.isEnabled();
                }
                if (flagsEnabled || hasBypass) {
                    FlagsGUI flagsGUI = new FlagsGUI(domainBlockManager.getDomainBlockById(this.domainBlock.getPlacedId()), player);
                    player.openInventory(flagsGUI.getInventory());
                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                } else {
                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                }
                break;
                //endregion
            //region Rent
            case 16:
                boolean rentEnabled = false;
                GUIOption rentOption = Domain.getInstance().getData().getGUIOption("info_rent", Material.GOLD_NUGGET, "&bRent Info", "&fDisplays any rent information for this field", true);
                if (this.domainBlock.getConfig().getRentCost() == 0 && this.domainBlock.getConfig().getRentDays() == 0) {
                    rentOption.setEnabled(false);
                }
                if (rentOption != null) {
                    if (rentOption.isEnabled()) {
                        if (domainBlockManager.isOwner(player, domainBlock) || hasBypass) {
                            SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.UI_BUTTON_CLICK);
                            RentGUI rentGUI = new RentGUI(instance, domainBlock, player);
                            player.openInventory(rentGUI.getInventory());
                            return;
                        } else {
                            SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                        }
                    } else {
                        instance.getMessage().send(
                                player,
                                "rent_not_available",
                                "&4This field is not available for rent",
                                null,
                                true
                        );
                        SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                    }
                } else {
                    instance.getMessage().send(
                            player,
                            "rent_not_available",
                            "&4This field is not available for rent",
                            null,
                            true
                    );
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
     * @param event InventoryCloseEvent to process
     */
    @Override
    public void onClose(InventoryCloseEvent event) {

        Domain instance = Domain.getInstance();

        instance.getDebug().log(
                "InfoGUI",
                "onClose",
                "Closing Inventory"
        );
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
