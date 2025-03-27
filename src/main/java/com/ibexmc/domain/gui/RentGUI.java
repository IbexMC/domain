package com.ibexmc.domain.gui;

import com.ibexmc.domain.domainblock.rent.RentManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.domainblock.open.TypedValue;

import com.ibexmc.domain.util.functions.PlayerFunctions;

public class RentGUI implements IInventoryGUI {

    //region Objects
    private final DomainBlock domainBlock;
    private final Player player;
    private final Domain instance;
    //endregion
    //region Constructors
    public RentGUI(Domain instance, DomainBlock domainBlock, Player player) {
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
                instance.getLocale().localeText("gui_rent_title", "Rent", null)
        );
        for(int i = 0; i < 27; ++i) {
            switch (i) {
                case 0:
                    inventory.setItem(i, GUIHelper.get("back"));
                    break;
                case 11:
                    inventory.setItem(i, GUIHelper.get("rent_info"));
                    break;
                case 13:
                    inventory.setItem(i, GUIHelper.get("rent_cost"));
                    break;
                case 15:
                    inventory.setItem(i, GUIHelper.get("rent_days"));
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
                InfoGUI infoGUI = new InfoGUI(instance, domainBlock, player);
                player.openInventory(infoGUI.getInventory());
                break;
            //endregion
            //region Rent Info
            case 11:
                instance.getDebug().log("RentGUI", "onClick", "Rent Info Clicked");
                if (domainBlockManager.isOwner(player, domainBlock) || hasBypass) {
                    RentManager rentManager = new RentManager(instance);
                    rentManager.rentInfo(player, domainBlock);
                    player.closeInventory();
                }
                break;
            //endregion
            //region Rent Cost
            case 13:
                instance.getDebug().log("RentGUI", "onClick", "Rent Cost Clicked");
                if (domainBlockManager.isOwner(player, domainBlock) || hasBypass) {
                    //rent_set_cost_info: "&eEnter the new rent cost in the chat.  It must be a valid number and will not be visible to other players.  Right click the Domain Block to cancel"
                    if (instance.getData().openBlocks.containsKey(player.getUniqueId())) {
                        instance.getData().openBlocks.remove(player.getUniqueId());
                    }
                    TypedValue rentCostTypedValue = new TypedValue(
                            domainBlock().getPlacedId(),
                            TypedValue.Type.RENT_COST,
                            player
                    );
                    instance.getData().openBlocks.put(player.getUniqueId(), rentCostTypedValue);
                    instance.getMessage().send(
                            player,
                            "rent_set_cost_info",
                            "&eEnter the new rent cost in the chat.  It must be a valid number and will not be visible to other players.  Right click the Domain Block to cancel",
                            null,
                            true
                    );
                    player.closeInventory();
                    return;
                }
                break;
            //endregion
            //region Rent Days
            case 15:
                instance.getDebug().log("RentGUI", "onClick", "Rent Days Clicked");
                if (domainBlockManager.isOwner(player, domainBlock) || hasBypass) {
                    if (instance.getData().openBlocks.containsKey(player.getUniqueId())) {
                        instance.getData().openBlocks.remove(player.getUniqueId());
                    }
                    TypedValue rentDaysTypedValue = new TypedValue(
                            domainBlock().getPlacedId(),
                            TypedValue.Type.RENT_DAYS,
                            player
                    );
                    instance.getData().openBlocks.put(player.getUniqueId(), rentDaysTypedValue);
                    instance.getMessage().send(
                            player,
                            "rent_set_days_info",
                            "&eEnter the new rent days in the chat.  It must be a valid number and will not be visible to other players.  Right click the Domain Block to cancel",
                            null,
                            true
                    );
                    player.closeInventory();
                    return;
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
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        instance.getDebug().log(
                "InfoGUI",
                "onClose",
                "Closing Inventory"
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
